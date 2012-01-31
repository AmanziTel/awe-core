package org.amanzi.awe.views.network.view;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;

public class CopyHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) {
        NetworkPropertiesView obj = new NetworkPropertiesView();
        obj.copyToClipboard();        
        return null;
    }    

}
