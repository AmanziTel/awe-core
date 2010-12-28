package org.amanzi.awe.afp.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class AfpTRXFilter extends ViewerFilter {
	
	private String equalityString;
	
	public void setEqualityText(String text){
		this.equalityString = text;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (equalityString.equals((String)element))
			return true;
		return false;
	}

}
