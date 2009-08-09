package org.amanzi.splash.neo4j.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.part.FileEditorInput;

public class PieChartEditorInput extends FileEditorInput {
	
	private String chartName;

	public String getPieChartName() {
		return chartName;
	}

	public void setPieChartName(String chartName) {
		this.chartName = chartName;
	}

	public PieChartEditorInput(IFile file) {
		super(file);
		// TODO Auto-generated constructor stub
	}
	
	
	

}
