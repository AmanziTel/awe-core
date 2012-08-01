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

package org.amanzi.awe.views.treeview;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.ui.events.EventStatus;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.awe.views.treeview.provider.impl.CommontTreeViewLabelProvider;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/**
 * <p>
 * common tree view functionality
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractTreeView extends ViewPart implements IAWEEventListenter {

    protected static final IGeneralNodeProperties GENERAL_NODE_PROPERTIES = AWEUIPlugin.getDefault().getGeneralNodeProperties();
    // TODO: LN: 01.08.2012, remove commented line
    // private final String SEARCH_PATTERN = ".*%s.*";
    /**
     * event manager
     */
    // TODO: LN: 01.08.2012, make fields private with protected getter
    protected final AWEEventManager eventManager;

    protected TreeViewer treeViewer;
    protected Text tSearch;

    /**
     * The constructor.
     */
    protected AbstractTreeView() {
        this.eventManager = AWEEventManager.getManager();
        eventManager.addListener(this, EventStatus.DATA_UPDATED);
        addEventListeners();
    }

    /**
     * add search listener to search field
     */
    protected void addSearchListener() {
        // TODO: LN: 01.08.2012, to additional class, another way - make AbstractTreeView implements
        // ModifyListener
        tSearch.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String searchText = tSearch.getText();
                searchInTreeView(searchText);

            }
        });
    }

    /**
     * search tree element by name
     * 
     * @param searchText
     */
    // TODO: LN: 01.08.2012, should be abstract, since this class didn't know how to search for text
    protected void searchInTreeView(final String searchText) {
    }

    public void selectDataElement(IDataElement dataElement) {
        this.treeViewer.reveal(dataElement);
        this.treeViewer.setSelection(new StructuredSelection(new Object[] {dataElement}));
    }

    /**
     * set providers to tree view
     */
    protected void setProviders() {
        this.treeViewer.setContentProvider(getContentProvider());
        // TODO: LN: 01.08.2012, CommonTreeViewLabelProvider can be declared as constant
        this.treeViewer.setLabelProvider(new CommontTreeViewLabelProvider());
    }

    /**
     * @return content provider
     */
    protected abstract IContentProvider getContentProvider();

    /**
     * added required listeners to event manager
     */
    // TODO: LN: 01.08.2012: initialization of listeners should be run on init() method
    // TODO: LN: 01.08.2012: also please check that you remove this Listeners from Manager on
    // dispose() method
    protected abstract void addEventListeners();

    /**
     * layout components
     * 
     * @param parent
     */
    protected void setLayout(Composite parent) {
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        parent.setLayout(layout);
        FormData formData = new FormData();
        if (tSearch != null) {
            formData.top = new FormAttachment(0, 5);
            formData.left = new FormAttachment(0, 5);
            formData.right = new FormAttachment(100, -5);
            tSearch.setLayoutData(formData);
            formData = new FormData();
            formData.top = new FormAttachment(tSearch, 5);
        } else {
            formData = new FormData();
            formData.top = new FormAttachment(0, 5);
        }

        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        formData.bottom = new FormAttachment(100, -5);
        treeViewer.getTree().setLayoutData(formData);
    }

    @Override
    public void onEvent(IEvent event) {
        switch (event.getStatus()) {
        case PROJECT_CHANGED:
        case DATA_UPDATED:
            updateView();
            break;
        default:
            break;
        }
    }

    protected void updateView() {
        treeViewer.refresh();
    }
}
