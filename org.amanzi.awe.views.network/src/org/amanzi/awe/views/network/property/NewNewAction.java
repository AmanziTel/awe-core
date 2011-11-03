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
package org.amanzi.awe.views.network.property;

import java.io.IOException;

import org.amanzi.awe.catalog.neo.NeoCatalogPlugin;
import org.amanzi.awe.catalog.neo.upd_layers.events.UpdateLayerEvent;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.statistic.IPropertyHeader;
import org.amanzi.neo.services.statistic.PropertyHeader;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.neoclipse.property.PropertyTransform.PropertyHandler;

/**
 * Action to add a new property to a PropertyContainer.
 */
public class NewNewAction extends Action
{
    private final PropertyHandler propertyHandler;
    
    private NewNetworkPropertySheetPage page;

    public NewNewAction( final Composite parent, NewNetworkPropertySheetPage page, final PropertyHandler propertyHandler) {
        super(propertyHandler.name());
        this.propertyHandler = propertyHandler;
        this.page = page;
    }

    @Override
    public void run()
    {
        PropertyContainer propertyContainer = ((DataElement)page.getCurrentNode()).getNode();
        if ( propertyContainer == null )
        {
            return;
        }
        InputDialog keyInput = new InputDialog( null, "Key entry",
            "Please enter the key of the new property", null, null );
        if ( keyInput.open() != Dialog.OK || keyInput.getReturnCode() != Dialog.OK )
        {
            return;
        }
        String key = keyInput.getValue();
        addProperty( propertyContainer, key, propertyHandler);
    }
    
    /**
     * Add a property to Node/Relationship. The user will be asked for
     * confirmation if the key already exists.
     * 
     * @param container
     * @param key
     * @param propertyHandler
     * @param propertySheet
     */
    public void addProperty( final PropertyContainer container,
            final String key, final PropertyHandler propertyHandler)
    {
        if ( container.hasProperty( key ) )
        {
            if ( !MessageDialog.openQuestion(
                    null,
                    "Key exists",
                    "The key \""
                            + key
                            + "\" already exists, do you want to overwrite the old value?" ) )
            {
                return;
            }
        }
        InputDialog valueInput = new InputDialog( null, "Value entry",
                "Please enter the value of the new property",
                propertyHandler.render( propertyHandler.value() ),
                propertyHandler.getValidator() );
        if ( valueInput.open() != Dialog.OK && valueInput.getReturnCode() != Dialog.OK )
        {
            return;
        }
        Object val = null;
        try
        {
            val = propertyHandler.parse( valueInput.getValue() );
        }
        catch ( IOException e )
        {
            MessageDialog.openError( null, "Error message",
                    "Error parsing the input value, no changes will be performed." );
            return;
        }
        setProperty( container, key, val);
    }
    
    /**
     * Set a property value, no questions asked.
     * 
     * @param container
     * @param key
     * @param value
     * @param propertySheet
     */
    public void setProperty( final PropertyContainer container,
            final String key, final Object value)
    {
        try
        {
            container.setProperty( key, value );
        }
        catch ( Exception e )
        {
            MessageDialog.openError( null, "Error", "Error in Neo service: "
                                                    + e.getMessage() );
            e.printStackTrace();
        }
        // Kasnitskij_V:
        updateStatistics(container, key, value);
        page.refresh();
        NeoServiceProviderUi.getProvider().commit();
        updateLayer(container);
    }
    
    /**
     *updates layer
     */
    private void updateLayer(PropertyContainer container) {
        Node gisNode = NeoUtils.findGisNodeByChild((Node)container);
        NeoCatalogPlugin.getDefault().getLayerManager().sendUpdateMessage(new UpdateLayerEvent(gisNode));
    }
    
    /**
     * Update statistics.
     *
     * @param container the container
     * @param container 
     * @param id the id
     * @param oldValue the old value
     */
    private void updateStatistics(PropertyContainer container, String id, Object oldValue) {
        if (container instanceof Node){
            DatasetService service = NeoServiceFactory.getInstance().getDatasetService();
            Node root = service.findRootByChild((Node)container);
            if (root!=null){
                IPropertyHeader stat = PropertyHeader.getPropertyStatistic(root);
                stat.updateStatistic(service.getNodeType((Node)container).getId(), id, container.getProperty(id, null), null);
            }
        }
    }
}
