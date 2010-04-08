/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.awe.views.reuse.mess_table.view;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.neoclipse.property.NodePropertySource;
import org.neo4j.neoclipse.property.PropertyDescriptor;
import org.neo4j.neoclipse.property.PropertyTransform;
import org.neo4j.neoclipse.property.PropertyTransform.PropertyHandler;

/**
 * <p>
 * Property page for Message and Event table. 
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class EventPropertySheetPage extends PropertySheetPage implements ISelectionListener {

    
    /**
     * Constructor. Sets SourceProvider for this Page 
     */
    public EventPropertySheetPage() {
        super();        
        setPropertySourceProvider(new EventPropertySourceProvider());
    }
    
    /**
     * Creates a Control of this Page and adds a Listener for NetworkTreeView
     */
    public void createControl(Composite parent) {
        super.createControl(parent);
        
        getSite().getPage().addSelectionListener(MessageAndEventTableView.VIEW_ID, this);
    }
    
    private class EventPropertySourceProvider implements IPropertySourceProvider {

        @Override
        public IPropertySource getPropertySource(Object arg0) {
            if(arg0 instanceof Node){
                return new EventPropertySource((Node)arg0);
            }
            return null;
        }
        
    }
    
    @SuppressWarnings("restriction")
    private class EventPropertySource extends NodePropertySource implements IPropertySource {

        /**
         * @param node
         * @param propertySheet
         */
        public EventPropertySource(Node node) {
            super(node, null);
        }
        
        @Override
        public IPropertyDescriptor[] getPropertyDescriptors() {
            List<IPropertyDescriptor> descs = new ArrayList<IPropertyDescriptor>();
            descs.addAll(getHeadPropertyDescriptors());
            Iterable<String> keys = container.getPropertyKeys();
            for (String key : keys) {
                Object value = container.getProperty((String)key);
                Class< ? > c = value.getClass();
                NodeTypes nt = NodeTypes.getNodeType(container,null);
                if(nt == null || nt.isPropertyEditable(key))
                    descs.add(new PropertyDescriptor(key, key, PROPERTIES_CATEGORY, c));
                else
                    descs.add(new PropertyDescriptor(key, key, NODE_CATEGORY));
            }
            return descs.toArray(new IPropertyDescriptor[descs.size()]);
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
    
}
