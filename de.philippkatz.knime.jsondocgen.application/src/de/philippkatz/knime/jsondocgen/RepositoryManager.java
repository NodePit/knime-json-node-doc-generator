/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 * -------------------------------------------------------------------
 *
 * History
 *   16.03.2005 (georg): created
 */
package de.philippkatz.knime.jsondocgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.knime.core.node.NodeLogger;
import org.knime.workbench.repository.RepositoryFactory;
import org.knime.workbench.repository.model.AbstractContainerObject;
import org.knime.workbench.repository.model.Category;
import org.knime.workbench.repository.model.DynamicNodeTemplate;
import org.knime.workbench.repository.model.IContainerObject;
import org.knime.workbench.repository.model.IRepositoryObject;
import org.knime.workbench.repository.model.MetaNodeTemplate;
import org.knime.workbench.repository.model.NodeTemplate;
import org.knime.workbench.repository.model.Root;
import org.osgi.framework.Bundle;

/**
 * Manages the (global) KNIME Repository. This class collects all the
 * contributed extensions from the extension points and creates an arbitrary
 * model. The repository is created on-demand as soon as one of the three public
 * methods is called. Thus the first call can take some time to return.
 * Subsequent calls will return immediately with the full repository tree.
 *
 * This code is copied from org.knime.workbench.repository; in
 * {@link #readNodes()} and {@link #readNodes()}, the filtering of "deprecated"
 * nodes was removed, b/c they should be added to the documentation as well.
 * Check, if a node is deprecated through {@link #isDeprecated(String)}.
 *
 * https://bitbucket.org/KNIME/knime-core/raw/642ae0a903ba9e0078221716ef8460d9a7a5bd47/org.knime.workbench.repository/src/eclipse/org/knime/workbench/repository/RepositoryManager.java
 *
 * @author Florian Georg, University of Konstanz
 * @author Thorsten Meinl, University of Konstanz
 * @author Philipp Katz, seleniumnodes.com
 */
public final class RepositoryManager {

	private static final NodeLogger LOGGER = NodeLogger.getLogger(RepositoryManager.class);

	/** The singleton instance. */
	public static final RepositoryManager INSTANCE = new RepositoryManager();

	// ID of "node" extension point
	private static final String ID_NODE = "org.knime.workbench.repository.nodes";

	// ID of "category" extension point
	private static final String ID_CATEGORY = "org.knime.workbench.repository.categories";

	private static final String ID_META_NODE = "org.knime.workbench.repository.metanode";

	private static final String ID_NODE_SET = "org.knime.workbench.repository.nodesets";

	private final Root m_root = new Root();

	private final Set<String> deprecatedNodes = new HashSet<>();

	/**
	 * Creates the repository model. This instantiates all contributed category/node
	 * extensions found in the global Eclipse PluginRegistry, and attaches them to
	 * the repository tree.
	 */
	private RepositoryManager() {
	}

	private void readRepository() {
		assert !m_root.hasChildren();
		readCategories();
		readNodes();
		readNodeSets();
		readMetanodes();
		removeEmptyCategories(m_root);
	}

	private void readMetanodes() {
		// iterate over the meta node config elements and create meta node templates
		IExtension[] metanodeExtensions = getExtensions(ID_META_NODE);
		for (IExtension mnExt : metanodeExtensions) {
			IConfigurationElement[] mnConfigElems = mnExt.getConfigurationElements();
			for (IConfigurationElement mnConfig : mnConfigElems) {

				try {
					MetaNodeTemplate metaNode = RepositoryFactory.createMetaNode(mnConfig);
					LOGGER.debug("Found meta node definition '" + metaNode.getID() + "': " + metaNode.getName());

					IContainerObject parentContainer = m_root.findContainer(metaNode.getCategoryPath());
					// If parent category is illegal, log an error and append the node to the
					// repository root.
					if (parentContainer == null) {
						LOGGER.warn("Invalid category-path for node contribution: '" + metaNode.getCategoryPath()
								+ "' - adding to root instead");
						m_root.addChild(metaNode);
					} else {
						// everything is fine, add the node to its parent category
						parentContainer.addChild(metaNode);
					}
				} catch (Throwable t) {
					String message = "MetaNode " + mnConfig.getAttribute("id") + "' from plugin '"
							+ mnConfig.getNamespaceIdentifier() + "' could not be created: " + t.getMessage();
					Bundle bundle = Platform.getBundle(mnConfig.getNamespaceIdentifier());

					if (bundle == null || bundle.getState() != Bundle.ACTIVE) {
						// if the plugin is null, the plugin could not be activated maybe due to a not
						// activateable plugin (plugin class cannot be found)
						message = message + " The corresponding plugin bundle could not be activated!";
					}

					LOGGER.error(message, t);
				}
			}
		}
	}

	private void readCategories() {
		IExtension[] categoryExtensions = getExtensions(ID_CATEGORY);
		ArrayList<IConfigurationElement> allElements = new ArrayList<IConfigurationElement>();

		for (IExtension ext : categoryExtensions) {
			// iterate through the config elements and create 'Category' objects
			IConfigurationElement[] elements = ext.getConfigurationElements();
			allElements.addAll(Arrays.asList(elements));
		}

		removeDuplicatesFromCategories(allElements);

		// sort first by path-depth, so that everything is there in the
		// right order
		Collections.sort(allElements, (o1, o2) -> {
			String element1 = o1.getAttribute("path");
			String element2 = o2.getAttribute("path");
			if (element1 == element2) {
				return 0;
			} else if (element1 == null) {
				return -1;
			} else if (element2 == null) {
				return 1;
			} else if (element1.equals(element2)) {
				return 0;
			} else if ("/".equals(element1)) {
				return -1;
			} else if ("/".equals(element2)) {
				return 1;
			} else {
				int countSlashes1 = 0;
				for (int i1 = 0; i1 < element1.length(); i1++) {
					if (element1.charAt(i1) == '/') {
						countSlashes1++;
					}
				}

				int countSlashes2 = 0;
				for (int i2 = 0; i2 < element2.length(); i2++) {
					if (element2.charAt(i2) == '/') {
						countSlashes2++;
					}
				}
				return countSlashes1 - countSlashes2;
			}
		});

		for (IConfigurationElement e : allElements) {
			try {
				Category category = RepositoryFactory.createCategory(m_root, e);
				LOGGER.debug(
						"Found category extension '" + category.getID() + "' on path '" + category.getPath() + "'");
			} catch (Exception ex) {
				String message = "Category '" + e.getAttribute("level-id") + "' from plugin '"
						+ e.getDeclaringExtension().getNamespaceIdentifier() + "' could not be created in parent path '"
						+ e.getAttribute("path") + "'.";
				LOGGER.error(message, ex);
			}
		}
	}

	private void readNodes() {
		IContainerObject uncategorized = Optional.ofNullable(m_root.findContainer("/uncategorized"))
				// this should never happen, but who knows...
				.orElse(m_root);

		Stream<IConfigurationElement> elementStream = Stream.of(RepositoryManager.getExtensions(ID_NODE))
				.flatMap(ext -> Stream.of(ext.getConfigurationElements()));

		elementStream.forEach(elem -> {
			try {
				NodeTemplate node = RepositoryFactory.createNode(elem);

				LOGGER.debug("Found node extension '" + node.getID() + "': " + node.getName());

				String nodeName = node.getID();
				nodeName = nodeName.substring(nodeName.lastIndexOf('.') + 1);

				// Ask the root to lookup the category-container located at
				// the given path
				IContainerObject parentContainer = m_root.findContainer(node.getCategoryPath());

				// If parent category is illegal, log an error and append
				// the node to the repository root.
				if (parentContainer == null) {
					LOGGER.coding("Unknown category for node " + node.getID() + " (plugin: "
							+ node.getContributingPlugin() + "): " + node.getCategoryPath()
							+ ". Node will be added to 'Uncategorized' instead");
					uncategorized.addChild(node);
				} else {
					String nodePluginId = elem.getNamespaceIdentifier();
					String categoryPluginId = parentContainer.getContributingPlugin();
					if (categoryPluginId == null) {
						categoryPluginId = "";
					}
					int secondDotIndex = nodePluginId.indexOf('.', nodePluginId.indexOf('.') + 1);
					if (secondDotIndex == -1) {
						secondDotIndex = 0;
					}

					if (!parentContainer.isLocked() || nodePluginId.equals(categoryPluginId)
							|| nodePluginId.startsWith("org.knime.") || nodePluginId.startsWith("com.knime.")
							|| nodePluginId.regionMatches(0, categoryPluginId, 0, secondDotIndex)) {
						// container not locked, or node and category from same plug-in
						// or the vendor is the same (comparing the first two parts of the plug-in ids)
						parentContainer.addChild(node);
					} else {
						LOGGER.coding("Locked category for node " + node.getID() + ": " + node.getCategoryPath()
								+ ". Node will be added to 'Uncategorized' instead");
						uncategorized.addChild(node);
					}
				}

				if (isDeprecated(elem)) {
					deprecatedNodes.add(node.getID());
				}

			} catch (Throwable t) {
				String message = "Node " + elem.getAttribute("factory-class") + "' from plugin '"
						+ elem.getNamespaceIdentifier() + "' could not be created: " + t.getMessage();
				Bundle bundle = Platform.getBundle(elem.getNamespaceIdentifier());

				if (bundle == null || bundle.getState() != Bundle.ACTIVE) {
					// if the plugin is null, the plugin could not be activated maybe due to a not
					// activateable plugin (plugin class cannot be found)
					message += " The corresponding plugin bundle could not be activated!";
				}
				LOGGER.error(message, t);
			}
		}); // for configuration elements
	}

	private static boolean isDeprecated(IConfigurationElement elem) {
		return "true".equalsIgnoreCase(elem.getAttribute("deprecated"));
	}

	private void readNodeSets() {
		Stream<IConfigurationElement> elementStream = Stream.of(RepositoryManager.getExtensions(ID_NODE_SET))
				.flatMap(ext -> Stream.of(ext.getConfigurationElements()));

		elementStream.forEach(elem -> {

			try {
				Collection<DynamicNodeTemplate> dynamicNodeTemplates = RepositoryFactory.createNodeSet(m_root, elem);

				for (DynamicNodeTemplate node : dynamicNodeTemplates) {

					String nodeName = node.getID();
					nodeName = nodeName.substring(nodeName.lastIndexOf('.') + 1);

					// Ask the root to lookup the category-container located at the given path
					IContainerObject parentContainer = m_root.findContainer(node.getCategoryPath());

					// If parent category is illegal, log an error and append the node to the
					// repository root.
					if (parentContainer == null) {
						LOGGER.warn("Invalid category-path for node contribution: '" + node.getCategoryPath()
								+ "' - adding to root instead");
						m_root.addChild(node);
					} else {
						// everything is fine, add the node to its parent category
						parentContainer.addChild(node);
					}

					if (isDeprecated(elem)) {
						deprecatedNodes.add(node.getID());
					}

				}

			} catch (Throwable t) {
				String message = "Node " + elem.getAttribute("factory-class") + "' from plugin '"
						+ elem.getNamespaceIdentifier() + "' could not be created.";
				Bundle bundle = Platform.getBundle(elem.getNamespaceIdentifier());

				if (bundle == null || bundle.getState() != Bundle.ACTIVE) {
					// if the plugin is null, the plugin could not
					// be activated maybe due to a not
					// activateable plugin (plugin class cannot be found)
					message += " The corresponding plugin bundle could not be activated!";
				}
				LOGGER.error(message, t);
			}
		});
	}

	/**
	 * Returns the extensions for a given extension point.
	 *
	 * @param pointID
	 *            The extension point ID
	 *
	 * @return The extensions
	 */
	private static IExtension[] getExtensions(final String pointID) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(pointID);
		if (point == null) {
			throw new IllegalStateException("Invalid extension point : " + pointID);

		}
		return point.getExtensions();
	}

	private static void removeDuplicatesFromCategories(final ArrayList<IConfigurationElement> allElements) {

		// brute force search
		for (int i = 0; i < allElements.size(); i++) {
			for (int j = allElements.size() - 1; j > i; j--) {

				String pathOuter = allElements.get(i).getAttribute("path");
				String levelIdOuter = allElements.get(i).getAttribute("level-id");
				String pathInner = allElements.get(j).getAttribute("path");
				String levelIdInner = allElements.get(j).getAttribute("level-id");

				if (pathOuter.equals(pathInner) && levelIdOuter.equals(levelIdInner)) {

					String nameI = allElements.get(i).getAttribute("name");
					String nameJ = allElements.get(j).getAttribute("name");

					// the removal is only reported in case the names
					// are not equal (if they are equal,the user will not
					// notice any difference (except possibly the picture))
					if (!nameI.equals(nameJ)) {
						String pluginI = allElements.get(i).getDeclaringExtension().getNamespaceIdentifier();
						String pluginJ = allElements.get(j).getDeclaringExtension().getNamespaceIdentifier();

						String message = "Category '" + pathOuter + "/" + levelIdOuter
								+ "' was found twice. Names are '" + nameI + "'(Plugin: " + pluginI + ") and '" + nameJ
								+ "'(Plugin: " + pluginJ + "). The category with name '" + nameJ + "' is ignored.";

						LOGGER.warn(message);
					}

					// remove from the end of the list
					allElements.remove(j);

				}
			}
		}
	}

	private static void removeEmptyCategories(final AbstractContainerObject treeNode) {
		for (IRepositoryObject object : treeNode.getChildren()) {
			if (object instanceof AbstractContainerObject) {
				AbstractContainerObject cat = (AbstractContainerObject) object;
				removeEmptyCategories(cat);
				if (!cat.hasChildren() && cat.getParent() != null) {
					cat.getParent().removeChild((AbstractContainerObject) object);
				}
			}
		}
	}

	/**
	 * Returns the repository root. If the repository has not yet read, it will be
	 * created during the call. Thus the first call to this method can take some
	 * time.
	 *
	 * @return the root object
	 */
	public synchronized Root getRoot() {
		if (!m_root.hasChildren()) {
			readRepository();
		}
		return m_root;
	}

	/**
	 * Get whether the node with the given ID is marked deprecated through the
	 * extension point.
	 *
	 * @param id
	 *            The node id.
	 * @return <code>true</code> in case the node was marked deprecated.
	 */
	public synchronized boolean isDeprecated(String id) {
		if (!m_root.hasChildren()) {
			readRepository();
		}
		return deprecatedNodes.contains(id);
	}

}
