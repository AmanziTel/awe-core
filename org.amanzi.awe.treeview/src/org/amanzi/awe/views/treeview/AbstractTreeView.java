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

import org.amanzi.awe.ui.label.CommonViewLabelProvider;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.model.IDataElement;
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
public abstract class AbstractTreeView extends ViewPart {

    private final String SEARCH_PATTERN = ".*%s.*";
    /**
     * event manager
     */
    protected final AWEEventManager eventManager;

    protected TreeViewer treeViewer;
    protected Text tSearch;

    /**
     * The constructor.
     */
    protected AbstractTreeView() {
        this.eventManager = AWEEventManager.getManager();
        addEventListeners();
    }

    /**
     * add search listener to searc field
     */
    protected void addSearchListener() {
        tSearch.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String searchText = tSearch.getText();
                searchInTreeView(searchText);

            }
        });
    }

    /**
     * @param searchText
     */
    protected void searchInTreeView(String searchText) {
        Object[] elements = treeViewer.getExpandedElements();
        for (Object element : elements) {
            IDataElement treeItem = ((IDataElement)element);
            String elementName = (String)treeItem.get(AbstractService.NAME);
            if (elementName.matches(String.format(SEARCH_PATTERN, searchText))) {
                selectDataElement(treeItem);
                return;
            }
        }
    }

    /**
     * Select node
     * 
     * @param dataElement - dataElement to select
     */
    protected void selectDataElement(IDataElement dataElement) {
        this.treeViewer.refresh();
        this.treeViewer.reveal(dataElement);
        this.treeViewer.setSelection(new StructuredSelection(new Object[] {dataElement}));
    }

    /**
     * set providers to tree view
     */
    protected void setProviders() {
        this.treeViewer.setContentProvider(getContentProvider());
        this.treeViewer.setLabelProvider(new CommonViewLabelProvider(this.treeViewer));
    }

    /**
     * @return content provider
     */
    protected abstract IContentProvider getContentProvider();

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

}
