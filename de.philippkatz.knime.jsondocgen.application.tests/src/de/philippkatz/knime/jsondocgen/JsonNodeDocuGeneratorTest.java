package de.philippkatz.knime.jsondocgen;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.knime.core.data.filestore.FileStorePortObject;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.database.DatabasePortObject;
import org.knime.core.node.port.viewproperty.ColorHandlerPortObject;
import org.knime.core.node.port.viewproperty.ViewPropertyPortObject;

public class JsonNodeDocuGeneratorTest {

	private static final class StubNodeModel extends NodeModel {

		protected StubNodeModel() {
			super(new PortType[] { BufferedDataTable.TYPE, DatabasePortObject.TYPE },
					new PortType[] { BufferedDataTable.TYPE });
		}

		@Override
		protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
				throws IOException, CanceledExecutionException {
			// no op.
		}

		@Override
		protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
				throws IOException, CanceledExecutionException {
			// no op.
		}

		@Override
		protected void saveSettingsTo(NodeSettingsWO settings) {
			// no op.
		}

		@Override
		protected void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
			// no op.
		}

		@Override
		protected void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
			// no op.
		}

		@Override
		protected void reset() {
			// no op.
		}

	}

	@Test
	public void testGetPortTypes() throws Exception {
		StubNodeModel stubNodeModel = new StubNodeModel();
		PortType[] inPorts = JsonNodeDocuGenerator.getPorts(stubNodeModel, true);
		assertEquals(2, inPorts.length);
		assertEquals(BufferedDataTable.TYPE, inPorts[0]);
		assertEquals(DatabasePortObject.TYPE, inPorts[1]);

		PortType[] outPorts = JsonNodeDocuGenerator.getPorts(stubNodeModel, false);
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
