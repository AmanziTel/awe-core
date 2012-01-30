package org.amanzi.awe.views.network.view;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


public class FilterHandler extends AbstractHandler{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        NetworkPropertiesView obj = new NetworkPropertiesView();
        obj.filter(event);
        NetworkPropertiesView propertiesView = null;
        try {
            propertiesView = (NetworkPropertiesView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .showView("org.amanzi.awe.views.network.views.NewNetworkPropertiesView");
            propertiesView.updateTableView();
        } catch (PartInitException e) {
        }
        
        return null;
    }

   

}
