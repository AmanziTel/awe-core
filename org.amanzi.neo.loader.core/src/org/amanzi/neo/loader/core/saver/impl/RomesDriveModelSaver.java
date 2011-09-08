package org.amanzi.neo.loader.core.saver.impl;

import java.io.PrintStream;
import java.util.Arrays;

import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.model.impl.DriveModel;

public class RomesDriveModelSaver<T extends BaseTransferData> implements
		ISaver<T> {

	private DriveModel dm;
	private MetaData metadata = new MetaData("dataset", MetaData.SUB_TYPE,
			"romes");

	@Override
	public void init(T element) {
		// TODO Auto-generated method stub

	}

	@Override
	public void save(T element) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finishUp(T element) {
		// TODO Auto-generated method stub

	}

	@Override
	public PrintStream getPrintStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPrintStream(PrintStream outputStream) {
		// TODO Auto-generated method stub

	}

	/*
	 * the method never really used
	 * 
	 * @see org.amanzi.neo.loader.core.saver.ISaver#getMetaData()
	 */
	@Override
	public Iterable<MetaData> getMetaData() {
		return Arrays.asList(new MetaData[] { metadata });
	}

	public DriveModel getDriveModel() {
		return dm;
	}

}
