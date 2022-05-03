package de.philippkatz.knime.jsondocgen;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ConfigurableNodeFactory;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeView;
import org.knime.core.node.context.NodeCreationConfiguration;

import de.philippkatz.knime.jsondocgen.JsonNodeDocuGenerator.PortDirection;
import de.philippkatz.knime.jsondocgen.docs.NodeDoc.DynamicPortGroup;

public class JsonNodeDocuGeneratorTest_DynamicPorts {

	// https://github.com/knime/knime-core/blob/master/org.knime.core/src/eclipse/org/knime/core/node/NodeDescription41Proxy.java
	// https://github.com/knime/knime-excel/blob/master/org.knime.ext.poi2/src/org/knime/ext/poi2/node/write3/XLSWriter2NodeFactory.java
	
	public class DynamicPortsStubNodeFactory extends ConfigurableNodeFactory<NodeModel> {

		@Override
		protected Optional<PortsConfigurationBuilder> createPortsConfigBuilder() {
			final PortsConfigurationBuilder builder = new PortsConfigurationBuilder();
			builder.addFixedInputPortGroup("Data table", BufferedDataTable.TYPE);
			builder.addOptionalInputPortGroup("Optional data table", BufferedDataTable.TYPE);
			return Optional.of(builder);
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
	public void test_getDynamicPorts() {
		List<DynamicPortGroup> dynamicInPorts = JsonNodeDocuGenerator.getDynamicPorts(new DynamicPortsStubNodeFactory(),
				PortDirection.In);
		assertEquals(1, dynamicInPorts.size());
		assertEquals("Optional data table", dynamicInPorts.get(0).groupIdentifier);
		assertEquals("org.knime.core.node.BufferedDataTable", dynamicInPorts.get(0).portObjectClasses.get(0));
	}

}
