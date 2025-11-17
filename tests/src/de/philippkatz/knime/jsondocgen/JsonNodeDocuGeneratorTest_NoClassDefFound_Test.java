package de.philippkatz.knime.jsondocgen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeView;
import org.knime.core.node.NodeFactory.NodeType;
import org.knime.workbench.repository.RepositoryManager;
import org.knime.workbench.repository.model.DefaultNodeTemplate;
import org.knime.workbench.repository.model.IRepositoryObject;

import de.philippkatz.knime.jsondocgen.docs.CategoryDoc.CategoryDocBuilder;

public class JsonNodeDocuGeneratorTest_NoClassDefFound_Test {

	public static class MyNodeFactory extends NodeFactory<NodeModel> {

		@Override
		public NodeModel createNodeModel() {
			throw new NoClassDefFoundError("org/RDKit/ROMol");
		}

		@Override
		protected int getNrNodeViews() {
			return 0;
		}

		@Override
		public NodeView<NodeModel> createNodeView(int viewIndex, NodeModel nodeModel) {
			return null;
		}

		@Override
		protected boolean hasDialog() {
			return false;
		}

		@Override
		protected NodeDialogPane createNodeDialogPane() {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void test() throws TransformerException, Exception {
		var parentCategory = new CategoryDocBuilder();
		var parent = RepositoryManager.INSTANCE.getRoot();
		NodeFactory<NodeModel> factory = new MyNodeFactory();
		var current = new DefaultNodeTemplate( //
				(Class<NodeFactory<? extends NodeModel>>) factory.getClass(), //
				"Bad Node", //
				"de.philippkatz.knime.nodes.plugin", //
				"/", //
				NodeType.Other);

		var result = new JsonNodeDocuGenerator().generate(current, parent, parentCategory, Collections.emptySet());

		assertTrue(result);

		var nodes = parentCategory.build().getNodes();
		assertEquals(1, nodes.size());
		assertEquals("Bad Node", nodes.get(0).name);
	}

}
