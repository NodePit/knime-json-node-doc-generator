package de.philippkatz.knime.jsondocgen;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;
import org.knime.core.node.ConfigurableNodeFactory;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeView;
import org.knime.core.node.context.NodeCreationConfiguration;

public class JsonNodeDocuGeneratorTest_ConfigurableNodeFactory_Test {

	private static final class ConfigurableStubNodeFactory extends ConfigurableNodeFactory<NodeModel> {

		@Override
		protected Optional<PortsConfigurationBuilder> createPortsConfigBuilder() {
			return Optional.empty();
		}

		@Override
		protected NodeModel createNodeModel(NodeCreationConfiguration creationConfig) {
			return new StubNodeModel();
		}

		@Override
		protected NodeDialogPane createNodeDialogPane(NodeCreationConfiguration creationConfig) {
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

	}

	@Test
	public void testInstantiateModel() throws Exception {
		NodeModel model = JsonNodeDocuGenerator.createNodeModel(new ConfigurableStubNodeFactory());
		assertTrue(model instanceof StubNodeModel);
	}

}
