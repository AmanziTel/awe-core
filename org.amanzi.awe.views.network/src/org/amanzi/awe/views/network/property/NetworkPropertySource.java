package org.amanzi.awe.views.network.property;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.views.properties.IPropertySource;
import org.neo4j.api.core.Transaction;
import org.neo4j.neoclipse.property.NodePropertySource;
import org.neo4j.neoclipse.property.PropertyTransform;
import org.neo4j.neoclipse.property.PropertyTransform.PropertyHandler;

/**
 * Class that creates a properties of given Node
 * 
 * @author Lagutko_N
 * @since 1.1.0
 */

public class NetworkPropertySource extends NodePropertySource implements IPropertySource {
    public NetworkPropertySource(NeoNode node) {
        super(node.getNode(), null);
    }

    @Override
    public void setPropertyValue(Object id, Object value) {
        Transaction tx = NeoServiceProvider.getProvider().getService().beginTx();
        try {
            if (container.hasProperty((String)id)) {
                // try to keep the same type as the previous value
                Class< ? > c = container.getProperty((String)id).getClass();
                PropertyHandler propertyHandler = PropertyTransform.getHandler(c);
                if (propertyHandler == null) {
                    MessageDialog.openError(null, "Error", "No property handler was found for type " + c.getSimpleName() + ".");
                    return;
                }
                Object o = null;
                try {
                    o = propertyHandler.parse(value);
                } catch (Exception e) {
                    MessageDialog.openError(null, "Error", "Could not parse the input as type " + c.getSimpleName() + ".");
                    return;
                }
                if (o == null) {
                    MessageDialog.openError(null, "Error", "Input parsing resulted in null value.");
                    return;
                }
                try {
                    container.setProperty((String)id, o);
                } catch (Exception e) {
                    MessageDialog.openError(null, "Error", "Error in Neo service: " + e.getMessage());
                }
            } else {
                // simply set the value
                try {
                    container.setProperty((String)id, value);
                } catch (Exception e) {
                    MessageDialog.openError(null, "Error", "Error in Neo service: " + e.getMessage());
                }
        }
            tx.success();
        } finally {
            tx.finish();
            NeoServiceProvider.getProvider().commit();
        }
    }
}
