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

    // TODO: LN: 01.08.2012, initialize this in Constructor
    // TODO: LN: 01.08.2012, do not use protected modifer
    protected static final IGeneralNodeProperties GENERAL_NODE_PROPERTIES = AWEUIPlugin.getDefault().getGeneralNodeProperties();
    /**
     * event manager
     */
    private final AWEEventManager eventManager;

    private TreeViewer treeViewer;
    private Text tSearch;

    // TODO: LN: 01.08.2012, move constants to the top
    private static final CommonTreeViewLabelProvider LABEL_PROVIDER = new CommonTreeViewLabelProvider();

    /**
     * The constructor.
     */
    protected AbstractTreeView() {
        super();
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
    // TODO: LN: 01.08.2012, this method do nothing - make it abstract
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
            // TODO: LN: 01.08.2012, also log exception e
            LOGGER.error("can't init");
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
        // TODO: LN: 01.08.2012, why it called not in addEventsListeners?
        eventManager.addListener(this, EventStatus.DATA_UPDATED);
        // TODO: LN: 01.08.2012, why called two times?
        addEventListeners();
        addEventListeners();
        // TODO: LN: 01.08.2012, super.init should be called first
        super.init(site);
    }

    /**
     * added required listeners to event manager
     */
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
     * @return Returns the tSearch.
     */
    protected Text gettSearch() {
        return tSearch;
    }

    /**
     * @param tSearch The tSearch to set.
     */
    protected void settSearch(Text tSearch) {
        this.tSearch = tSearch;
    }

    /**
     * @return Returns the generalNodeProperties.
     */
    protected static IGeneralNodeProperties getGeneralNodeProperties() {
        return GENERAL_NODE_PROPERTIES;
    }

    /**
     * @return Returns the eventManager.
     */
    protected AWEEventManager getEventManager() {
        return eventManager;
    }
}
