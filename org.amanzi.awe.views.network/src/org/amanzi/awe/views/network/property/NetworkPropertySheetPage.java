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

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.awe.views.network.view.NetworkTreeView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.views.properties.PropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.neo4j.neoclipse.property.PropertyTransform;

/**
 * Property Sheet Page that shows Properties of Node that was selected on NetworkTree
 *
 * @author Lagutko_N
 * @since 1.0.0
 */

public class NetworkPropertySheetPage extends PropertySheetPage implements ISelectionListener {
    
    private NetworkPropertySourceProvider provider;
    
    private ISelection selection;
    
    private Composite parent;
    
    private Action deleteAction;
    
    /**
     * Constructor. Sets SourceProvider for this Page 
     * 
     * @param viewer
     */
    
    public NetworkPropertySheetPage() {
        super();        
        provider = new NetworkPropertySourceProvider();
        setPropertySourceProvider(provider);
    }
    
    /**
     * Creates a Control of this Page and adds a Listener for NetworkTreeView
     */
    
    public void createControl(Composite parent) {
        super.createControl(parent);
        
        this.parent = parent;
        
        createMenu(parent);
        getSite().getPage().addSelectionListener(NetworkTreeView.NETWORK_TREE_VIEW_ID, this);
    }
    
    /**
     * Create the context menu for this property sheet.
     * @param parent
     */
    private void createMenu( final Composite parent )
    {
        MenuManager menuManager = createNewSubmenu( parent );
        
        deleteAction = new DeleteAction(parent, this);
        menuManager.add(new Separator());
        menuManager.add(deleteAction);
        
        Menu menu = menuManager.createContextMenu( getControl() );
        getControl().setMenu( menu );
    }
    
    /**
     * Create submenu for adding new properties.
     */
    private MenuManager createNewSubmenu( final Composite parent )
    {
        //MenuManager addMenuMgr = new MenuManager( "New", Icons.NEW_ENABLED
        //    .descriptor(), "propertiesAddSubmenu" );
        MenuManager addMenuMgr = new MenuManager( "New", "propertiesAddSubmenu" );
        
        addMenuMgr.add( new NewAction( parent, this, PropertyTransform
            .getHandler( String.class ) ) );
        addMenuMgr.add( new NewAction( parent, this, PropertyTransform
            .getHandler( Character.class ) ) );
        addMenuMgr.add( new NewAction( parent, this, PropertyTransform
            .getHandler( Long.class ) ));
        addMenuMgr.add( new NewAction( parent, this, PropertyTransform
            .getHandler( Integer.class ) ) );
        addMenuMgr.add( new NewAction( parent, this, PropertyTransform
            .getHandler( Short.class ) ) );
        addMenuMgr.add( new NewAction( parent, this, PropertyTransform
            .getHandler( Byte.class ) ) );
        addMenuMgr.add( new NewAction( parent, this, PropertyTransform
            .getHandler( Double.class ) ) );
        addMenuMgr.add( new NewAction( parent, this, PropertyTransform
            .getHandler( Float.class ) ) );
        addMenuMgr.add( new NewAction( parent, this, PropertyTransform
            .getHandler( Boolean.class ) ) );
        return addMenuMgr;
    }
    
    public NeoNode getCurrentNode() {
        return provider.getLastRawObject();
    }
    
    @Override
    public void handleEntrySelection( final ISelection selection )
    {
        super.handleEntrySelection( selection );
        this.selection = selection;
        if ( selection instanceof IStructuredSelection )
        {
            IStructuredSelection ss = (IStructuredSelection) selection;
            if ( ss.size() > 1 )
            {
                setRestrictedEnabled( false );
                return;
            }
            Object firstElement = ss.getFirstElement();
            if ( firstElement == null )
            {
                setRestrictedEnabled( false );
                return;
            }
            if ( firstElement instanceof PropertySheetEntry )
            {
                PropertySheetEntry entry = (PropertySheetEntry) firstElement;
                if ( entry.getEditor( parent ) == null )
                {
                    setRestrictedEnabled( false );
                    return;
                }
            }
        }
        setRestrictedEnabled( true );
    }
    
    /**
     * Enable/disable actions that operate on an existing property.
     * @param enabled
     */
    private void setRestrictedEnabled( final boolean enabled )
    {
        if ( deleteAction != null )
        {
            deleteAction.setEnabled( enabled );
        }
    }

    public ISelection getSelection() { 
        return selection;
    }
}
