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

import org.amanzi.awe.ui.events.EventStatus;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.awe.views.treeview.provider.impl.CommonTreeViewLabelProvider;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.apache.log4j.Logger;
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
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * <p>
 * common tree view functionality
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractTreeView extends ViewPart implements IAWEEventListenter, ModifyListener {
    private static final Logger LOGGER = Logger.getLogger(AbstractTreeView.class);
    private static final CommonTreeViewLabelProvider LABEL_PROVIDER = new CommonTreeViewLabelProvider();
    /**
     * event manager
     */
    private final AWEEventManager eventManager;

    private final IGeneralNodeProperties generalNodeProperties;

    private TreeViewer treeViewer;
    private Text tSearch;

    protected AbstractTreeView(IGeneralNodeProperties properties) {
        super();
        generalNodeProperties = properties;
        this.eventManager = AWEEventManager.getManager();
    }

    @Override
    public void modifyText(ModifyEvent e) {
        String searchText = tSearch.getText();
        searchInTreeView(searchText);
    }

    /**
     * search tree element by name
     * 
     * @param searchText
     */
    // TODO: KV: not implemented yet
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
        this.treeViewer.setLabelProvider(LABEL_PROVIDER);
    }

    @Override
    public void createPartControl(Composite parent) {
        createControls(parent);
        try {
            init(this.getViewSite());
        } catch (PartInitException e) {
            LOGGER.error("can't init", e);
        }
    }

    /**
     * @param parent
     */
    protected abstract void createControls(Composite parent);

    /**
     * @return content provider
     */
    protected abstract IContentProvider getContentProvider();

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        addEventListeners();

    }

    /**
     * added required listeners to event manager
     */
    protected void addEventListeners() {
        eventManager.addListener(this, EventStatus.DATA_UPDATED, EventStatus.PROJECT_CHANGED);
    }

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
        // TODO: LN: 03.08.2012, why handled two types of events if this class listens only one
        // type?
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

    /**
     * @return Returns the treeViewer.
     */
    protected TreeViewer getTreeViewer() {
        return treeViewer;
    }

    /**
     * @param treeViewer The treeViewer to set.
     */
    protected void setTreeViewer(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

    /**
     * @param tSearch The tSearch to set.
     */
    protected void setSearchField(Text tSearch) {
        this.tSearch = tSearch;
    }

    /**
     * @return Returns the generalNodeProperties.
     */
    protected IGeneralNodeProperties getGeneralNodeProperties() {
        return generalNodeProperties;
    }

    /**
     * @return Returns the eventManager.
     */
    protected AWEEventManager getEventManager() {
        return eventManager;
    }

    @Override
    public void setFocus() {
    }
}
