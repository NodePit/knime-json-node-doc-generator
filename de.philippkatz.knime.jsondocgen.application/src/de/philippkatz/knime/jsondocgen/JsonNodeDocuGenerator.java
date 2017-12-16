/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   Oct 10, 2013 (hornm): created
 */
package de.philippkatz.knime.jsondocgen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.streamable.PartitionInfo;
import org.knime.workbench.repository.model.Category;
import org.knime.workbench.repository.model.IContainerObject;
import org.knime.workbench.repository.model.IRepositoryObject;
import org.knime.workbench.repository.model.NodeTemplate;
import org.knime.workbench.repository.model.Root;
import org.w3c.dom.Element;

import de.philippkatz.knime.jsondocgen.docs.CategoryDoc;
import de.philippkatz.knime.jsondocgen.docs.CategoryDoc.CategoryDocBuilder;
import de.philippkatz.knime.jsondocgen.docs.NodeDoc.NodeDocBuilder;

/**
 * Creates a summary of the node descriptions of a all available KNIME nodes in
 * JSON format.
 *
 * This class is based on org.knime.workbench.repository.util.NodeDocuGenerator
 *
 * @author Martin Horn, University of Konstanz
 * @author Philipp Katz, seleniumnodes.com
 */
public class JsonNodeDocuGenerator implements IApplication {

	private static final String DESTINATION_ARG = "-destination";

	private static final String CATEGORY_ARG = "-category";

	private static final String PLUGIN_ARG = "-plugin";

	private static final String INCLUDE_DEPRECATED_ARG = "-includeDeprecated";

	private static void printUsage() {
		System.err.println("Usage: NodeDocuGenerator options");
		System.err.println("Allowed options are:");
		System.err.println("\t" + DESTINATION_ARG
				+ " dir : directory where the result should be written to (directory must exist)");
		System.err.println("\t" + PLUGIN_ARG
				+ " plugin-id : Only nodes of the specified plugin will be considered (specify multiple plugins by repeating this option). If not all available plugins will be processed.");
		System.err.println("\t" + CATEGORY_ARG
				+ " category-path (e.g. /community) : Only nodes within the specified category path will be considered. If not specified '/' is used.");
		System.err.println(
				"\t" + INCLUDE_DEPRECATED_ARG + " : Include nodes marked as 'deprecated' in the extension point.");

	}

	/* target directory */
	private File m_directory;

	private List<String> m_pluginId = new ArrayList<>();

	private String m_catPath = "/";

	private boolean m_includeDeprecated = false;

	private CategoryDocBuilder rootCategoryDoc;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object start(final IApplicationContext context) throws Exception {
		Object o = context.getArguments().get("application.args");
		Display.getDefault();
		if ((o != null) && (o instanceof String[])) {
			String[] args = (String[]) o;
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals(DESTINATION_ARG)) {
					m_directory = new File(args[i + 1]);
				} else if (args[i].equals(CATEGORY_ARG)) {
					m_catPath = args[i + 1];
				} else if (args[i].equals(PLUGIN_ARG)) {
					m_pluginId.add(args[i + 1]);
				} else if (args[i].equals(INCLUDE_DEPRECATED_ARG)) {
					m_includeDeprecated = true;
				} else if (args[i].equals("-help")) {
					printUsage();
					return EXIT_OK;
				}
			}
		}

		if (m_directory == null) {
			System.err.println("No output directory specified");
			printUsage();
			return 1;
		} else if (!m_directory.exists() && !m_directory.mkdirs()) {
			System.err.println("Could not create output directory '" + m_directory.getAbsolutePath() + "'.");
			return 1;
		}

		generate();

		return EXIT_OK;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {

	}

	/**
	 * Starts generating the node reference documents.
	 *
	 * @throws Exception
	 */
	private void generate() throws Exception {

		System.out.println("Reading node repository");

		IRepositoryObject root = RepositoryManager.INSTANCE.getRoot();
		rootCategoryDoc = new CategoryDocBuilder();
		rootCategoryDoc.setId(root.getID());
		rootCategoryDoc.setName(root.getName());
		rootCategoryDoc.setContributingPlugin(root.getContributingPlugin());

		// determine the root category according to the user specified category
		// path (only if the specified category should appear as new root)
		// String[] cats = m_catPath.split("/");
		// for (int i = 1; i < cats.length; i++) {
		// IRepositoryObject[] children = null;
		// if (root instanceof Root) {
		// children = ((Root)root).getChildren();
		// } else if (root instanceof Category) {
		// children = ((Category)root).getChildren();
		// } else {
		// break;
		// }
		// if (children != null) {
		// for (int j = 0; j < children.length; j++) {
		// if (children[j].getID().equals(cats[i])) {
		// root = children[j];
		// break;
		// }
		// }
		// }
		//
		// }

		// replace '/' with points and remove leading '/'
		if (m_catPath.startsWith("/")) {
			m_catPath = m_catPath.substring(1);
		}
		m_catPath = m_catPath.replaceAll("/", ".");

		// recursively generate the node reference and the node description
		// pages
		generate(m_directory, root, null, rootCategoryDoc);

		CategoryDoc rootCategory = rootCategoryDoc.build();
		String resultJson = rootCategory.toJson();
		File resultFile = new File(m_directory, "nodeDocumentation.json");
		System.out.println("Writing result to " + resultFile);
		IOUtils.write(resultJson, new FileOutputStream(resultFile), StandardCharsets.UTF_8);

	}

	/**
	 * Recursively generates the nodes description documents and the menu entries.
	 *
	 * @param directory
	 * @param current
	 * @param parent
	 *            parent repository object as some nodes pointing to "frequently
	 *            used"-repository object as a parent
	 * @param parentCategory
	 *            The parent category where to insert the JSON entry.
	 * @throws Exception
	 * @throws TransformerException
	 *
	 * @return true, if the element was added to the documentation, false if it has
	 *         been skipped
	 */
	private boolean generate(final File directory, final IRepositoryObject current, final IRepositoryObject parent,
			CategoryDocBuilder parentCategory) throws TransformerException, Exception {

		if (current instanceof NodeTemplate) {

			// skip node if not part of the specified plugin
			if (!m_pluginId.isEmpty() && !m_pluginId.contains(current.getContributingPlugin())) {
				return false;
			}

			// skip if not in a sub-category of the category specified
			// as argument
			if (m_catPath.length() > 0) {
				String catIdentifier = getCategoryIdentifier(parent);
				if (!catIdentifier.startsWith(m_catPath)) {
					return false;
				}
			}

			// create the JSON entry from the node XML description
			NodeTemplate nodeTemplate = (NodeTemplate) current;
			Element xmlDescription = nodeTemplate.createFactoryInstance().getXMLDescription();
			NodeDocBuilder builder = NodeDocJsonParser.parse(xmlDescription, new NodeDocBuilder());
			builder.setId(current.getID());
			builder.setContributingPlugin(current.getContributingPlugin());
			builder.setIconBase64(getImageBase64(nodeTemplate.getIcon()));
			builder.setStreamable(isStreamable(nodeTemplate));
			builder.setAfterId(stringOrNull(nodeTemplate.getAfterID()));
			boolean deprecated = RepositoryManager.INSTANCE.isDeprecated(current.getID());
			builder.setDeprecated(deprecated);
			if (!deprecated || m_includeDeprecated) {
				parentCategory.addNode(builder.build());
			}

			return true;
		} else if (current instanceof Category || current instanceof Root) {
			System.out.println("Processing category " + getPath(current));
			IRepositoryObject[] repoObjs = ((IContainerObject) current).getChildren();

			CategoryDocBuilder newCategory = parentCategory;

			if (current instanceof Category) {
				Category category = (Category) current;
				CategoryDocBuilder builder = new CategoryDocBuilder();
				builder.setId(category.getID());
				builder.setName(category.getName());
				builder.setDescription(category.getDescription());
				builder.setContributingPlugin(category.getContributingPlugin());
				builder.setIconBase64(getImageBase64(category.getIcon()));
				builder.setAfterId(stringOrNull(category.getAfterID()));
				newCategory = builder;
			}

			boolean hasChildren = false;
			for (IRepositoryObject repoObj : repoObjs) {
				hasChildren = hasChildren | generate(directory, repoObj, current, newCategory);
			}

			if (hasChildren && current instanceof Category) {
				parentCategory.addChild(newCategory.build());
			}

			return hasChildren;

		} else {
			// if the repository object is neither a node nor a category
			// (hence,
			// most likely a metanode), we just ignore them for now
			return false;
		}

	}

	private String stringOrNull(String string) {
		return (string == null || string.isEmpty()) ? null : string;
	}

	private static String getImageBase64(Image image) {
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] { image.getImageData() };
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		imageLoader.save(stream, SWT.IMAGE_PNG);
		return new String(Base64.getEncoder().encode(stream.toByteArray()));
	}

	/**
	 * This code is taken from
	 * org.knime.workbench.repository.view.AbstractRepositoryView.enrichWithAdditionalInfo(IRepositoryObject,
	 * IProgressMonitor, boolean)
	 */
	private static boolean isStreamable(NodeTemplate nodeTemplate) {
		try {
			NodeFactory<? extends NodeModel> nf = nodeTemplate.createFactoryInstance();
			NodeModel nm = nf.createNodeModel();
			// check whether the current node model overrides the
			// #createStreamableOperator-method
			Method m = nm.getClass().getMethod("createStreamableOperator", PartitionInfo.class, PortObjectSpec[].class);
			if (m.getDeclaringClass() != NodeModel.class) {
				// method has been overriden -> node is probably streamable or distributable
				return true;
			}
		} catch (Throwable t) {
			System.out.println(
					"Unable to instantiate the node " + nodeTemplate.getFactory().getName() + ": " + t.getMessage());
		}
		return false;
	}

	/*
	 * Helper to compose the category names/identifier of the super-categories and
	 * the current one
	 */
	private static String getCategoryIdentifier(final IRepositoryObject cat) {
		IContainerObject parent = cat.getParent();
		String identifier = cat.getID();
		while (parent != null && !(parent instanceof Root)) {
			identifier = parent.getID() + "." + identifier;
			parent = parent.getParent();
		}
		return identifier;
	}

	private static String getPath(final IRepositoryObject object) {
		if (object.getParent() != null) {
			return getPath(object.getParent()) + "/" + object.getName();
		} else {
			return "";
		}
	}
}
