package de.philippkatz.knime.jsondocgen;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeView;
import org.knime.core.webui.node.dialog.NodeDialog;
import org.knime.core.webui.node.dialog.NodeDialogFactory;
import org.knime.core.webui.node.dialog.kai.KaiNodeInterface;
import org.knime.core.webui.node.dialog.kai.KaiNodeInterfaceFactory;

public class JsonNodeDocuGeneratorTest_NewFlags_Test {

	@SuppressWarnings("restriction")
	private static class MyNodeFactory extends NodeFactory<NodeModel>
			implements KaiNodeInterfaceFactory, NodeDialogFactory {

		@Override
		public NodeModel createNodeModel() {
			return null;
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

		@Override
		public KaiNodeInterface createKaiNodeInterface() {
			return null;
		}

		@Override
		public NodeDialog createNodeDialog() {
			return null;
		}

	}

	@Test
	public void test() {
		assertTrue(JsonNodeDocuGenerator.hasModernDialog(new MyNodeFactory()));
	}

}
