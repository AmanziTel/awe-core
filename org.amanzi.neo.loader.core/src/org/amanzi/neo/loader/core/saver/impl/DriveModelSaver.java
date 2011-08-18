package org.amanzi.neo.loader.core.saver.impl;

import java.io.PrintStream;

import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.model.DriveModel;

public class DriveModelSaver<T extends BaseTransferData> implements ISaver<T> {
	
	private DriveModel dm;

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

	@Override
	public Iterable<MetaData> getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	public DriveModel getDriveModel() {
		return dm;
	}

	public void setDm(DriveModel dm) {
		this.dm = dm;
	}

}
