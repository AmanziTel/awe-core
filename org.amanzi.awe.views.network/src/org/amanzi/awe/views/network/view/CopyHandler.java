package org.amanzi.awe.views.network.view;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;

public class CopyHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) {
        NewNetworkPropertiesView obj = new NewNetworkPropertiesView();
        obj.copyToClipboard();        
        return null;
    }
    
    /*private String personToString(Person person) {
        return person.getFirstName() + "\t" + person.getLastName() + "\t"
                + person.getGender() + "\t" + person.isMarried()
                + System.getProperty("line.separator");
    }*/

}
