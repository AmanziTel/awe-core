package org.amanzi.awe.views.network.view;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class FilterHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		PropertiesView obj = new PropertiesView();
		obj.filter(event);
		PropertiesView propertiesView = null;
		try {
			propertiesView = (PropertiesView) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.showView(PropertiesView.PROPERTIES_VIEW_ID);
			propertiesView.updateTableView();
		} catch (PartInitException e) {
		}

		return null;
	}

}
