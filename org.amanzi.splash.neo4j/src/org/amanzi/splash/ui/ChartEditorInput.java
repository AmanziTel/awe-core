package org.amanzi.splash.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.part.FileEditorInput;

public class ChartEditorInput extends FileEditorInput {
	
	private String chartName;

	public String getChartName() {
		return chartName;
	}

	public void setChartName(String chartName) {
		this.chartName = chartName;
	}

	public ChartEditorInput(IFile file) {
		super(file);
		// TODO Auto-generated constructor stub
	}
	
	
	

}
