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

import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;

/**
 * Action to delete a property from a PropertyContainer.
 */
public class DeleteAction extends Action    
{
    private NetworkPropertySheetPage propertySheet;
    
    private Composite parent;
    
    public DeleteAction( final Composite parent,
        final NetworkPropertySheetPage propertySheet )
    {
        super("Remove");
        this.propertySheet = propertySheet;
        this.parent = parent;
    }

    protected void performOperation( final PropertyContainer container,
        final IPropertySheetEntry entry )
    {
        String key = entry.getDisplayName();
        
        removeProperty( container, key, propertySheet );
    }
    
    @Override
    public void run()
    {
        IPropertySheetEntry entry = getPropertySheetEntry();
        if ( entry == null )
        {
            return;
        }
        Node propertyContainer = propertySheet.getCurrentNode().getNode();
        if ( propertyContainer == null )
        {
            return;
        }
        performOperation( propertyContainer, entry );
    }
    
    /**
     * Get the current selected editable property sheet entry, if available.
     * Returns <code>null</code> on failure, after showing appropriate error
     * messages.
     * @return selected property sheet entry
     */
    protected IPropertySheetEntry getPropertySheetEntry()
    {
        ISelection selection = propertySheet.getSelection();
        if ( selection.isEmpty() )
        {
            MessageDialog.openError( null, "Error", "Nothing is selected." );
            return null;
        }
        if ( !(selection instanceof IStructuredSelection) )
        {
            MessageDialog.openError( null, "Error", "Error in selection type." );
            return null;
        }
        IStructuredSelection ss = (IStructuredSelection) selection;
        Object firstElement = ss.getFirstElement();
        if ( !(firstElement instanceof IPropertySheetEntry) )
        {
            MessageDialog.openError( null, "Error",
                "The selection is not in a property sheet." );
            return null;
        }
        IPropertySheetEntry entry = (IPropertySheetEntry) firstElement;
        if ( entry.getEditor( parent ) == null )
        {
            MessageDialog.openError( null, "Error",
                "This item can not be changed." );
            return null;
        }
        return entry;
    }
    
    /**
     * Remove a property from Node/Relationship.
     * 
     * @param container
     * @param key
     * @param propertySheet
     */
    public void removeProperty( final PropertyContainer container,
            final String key, final NetworkPropertySheetPage propertySheet )
    {
        boolean confirmation = MessageDialog.openConfirm( null,
                "Confirm removal",
                "Do you really want to remove the selected property?" );
        if ( !confirmation )
        {
            return;
        }
        try
        {
            container.removeProperty( key );
        }
        catch ( Exception e )
        {
            MessageDialog.openError( null, "Error", "Error in Neo service: "
                                                    + e.getMessage() );
        }
        
        NeoServiceProviderUi.getProvider().commit();
        //TODO: update statistics
        
        
        propertySheet.refresh();
    }
}
