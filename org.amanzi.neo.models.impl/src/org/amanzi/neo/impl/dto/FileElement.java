package org.amanzi.neo.impl.dto;

import org.amanzi.neo.models.measurement.IMeasurementModel.IFileElement;
import org.neo4j.graphdb.Node;

public class FileElement extends DataElement implements IFileElement {

	private String path;

	public FileElement(final Node node) {
		super(node);
	}

	public void setPath(final String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}

}
