package de.philippkatz.knime.jsondocgen;

import java.io.File;
import java.io.IOException;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.database.DatabasePortObject;

@SuppressWarnings("deprecation")
final class StubNodeModel extends NodeModel {

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