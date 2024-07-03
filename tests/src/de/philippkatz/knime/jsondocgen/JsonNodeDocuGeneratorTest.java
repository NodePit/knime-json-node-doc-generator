package de.philippkatz.knime.jsondocgen;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.knime.core.data.filestore.FileStorePortObject;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.database.DatabasePortObject;
import org.knime.core.node.port.viewproperty.ColorHandlerPortObject;
import org.knime.core.node.port.viewproperty.ViewPropertyPortObject;

import de.philippkatz.knime.jsondocgen.JsonNodeDocuGenerator.PortDirection;

@SuppressWarnings("deprecation")
public class JsonNodeDocuGeneratorTest {

	@Test
	public void testGetPortTypes() throws Exception {

		NodeFactory<StubNodeModel> stubNodeFactory = new NodeFactory<StubNodeModel>() {
			@Override
			public StubNodeModel createNodeModel() {
				return new StubNodeModel();
			}

			@Override
			protected int getNrNodeViews() {
				return 0;
			}

			@Override
			public NodeView<StubNodeModel> createNodeView(int viewIndex, StubNodeModel nodeModel) {
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
		};

		PortType[] inPorts = JsonNodeDocuGenerator.getPorts(stubNodeFactory, PortDirection.In);
		assertEquals(2, inPorts.length);
		assertEquals(BufferedDataTable.TYPE, inPorts[0]);
		assertEquals(DatabasePortObject.TYPE, inPorts[1]);

		PortType[] outPorts = JsonNodeDocuGenerator.getPorts(stubNodeFactory, PortDirection.Out);
		assertEquals(1, outPorts.length);
		assertEquals(BufferedDataTable.TYPE, outPorts[0]);
	}

	@Test
	public void testGetParentPortObjectClasses() {
		List<Class<? extends PortObject>> parentPortObjectClasses = JsonNodeDocuGenerator
				.getParentPortObjectClasses(ColorHandlerPortObject.class);
		assertEquals(1, parentPortObjectClasses.size());
		assertEquals(ViewPropertyPortObject.class, parentPortObjectClasses.get(0));

		parentPortObjectClasses = JsonNodeDocuGenerator.getParentPortObjectClasses(FileStorePortObject.class);
		assertEquals(1, parentPortObjectClasses.size());
		assertEquals(PortObject.class, parentPortObjectClasses.get(0));
	}

	@Test
	public void testMakeHexColor() {
		assertEquals("008000", JsonNodeDocuGenerator.makeHexColor(32768));
		assertEquals("000000", JsonNodeDocuGenerator.makeHexColor(0));
		assertEquals("9b9b9b", JsonNodeDocuGenerator.makeHexColor(-6579301));
		assertEquals("800000", JsonNodeDocuGenerator.makeHexColor(8388608));
	}

}
