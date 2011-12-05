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
package org.amanzi.awe.views.network.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.amanzi.awe.awe.views.view.provider.NetworkTreeContentProvider;
import org.amanzi.awe.awe.views.view.provider.NetworkTreeLabelProvider;
import org.amanzi.neo.model.distribution.IDistributionBar;
import org.amanzi.neo.model.distribution.IDistributionModel;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.ISelectionModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.neo.services.ui.enums.EventsType;
import org.amanzi.neo.services.ui.events.AnalyseEvent;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.IEventsListener;
import org.amanzi.neo.services.ui.events.UpdateDataEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * This View contains a tree of objects found in the database. The tree is built based on the
 * existence of the NetworkRelationshipTypes.CHILD relation, and the set of INetworkModel nodes
 * defined by the INetworkModel.java class.
 * 
 * @author Kasnitskij_V
 * @since 1.0.0
 */

public class NetworkTreeView extends ViewPart {

    private static final String RENAME_MSG = "Enter new Name";

    /*
     * ID of this View
     */
    public static final String NETWORK_TREE_VIEW_ID = "org.amanzi.awe.views.network.views.NetworkTreeView";

    private static final String SHOW_PROPERTIES = "Show properties";
    private static final String EDIT_PROPERTIES = "Edit properties";
    private static final String ERROR_TITLE = "Error";
    private boolean currentMode = false;

    /*
     * TreeViewer for database Nodes
     */
    protected TreeViewer viewer;

    private Text tSearch;

    private Set<IDataElement> selectedDataElements = new HashSet<IDataElement>();

    /**
     * event manager
     */
    private EventManager eventManager;

    /**
     * The constructor.
     */
    public NetworkTreeView() {
        eventManager = EventManager.getInstance();
        addListeners();
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {

        tSearch = new Text(parent, SWT.BORDER);
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setComparer(new IElementComparer() {

            @Override
            public int hashCode(Object element) {
                return 0;
            }

            @Override
            public boolean equals(Object a, Object b) {
                if (a instanceof IDistributionalModel && b instanceof IDistributionalModel) {
                    return ((IDistributionalModel)a).getName().equals(((IDistributionalModel)b).getName());
                } else if (a instanceof IDistributionModel && b instanceof IDistributionModel) {
                    IDistributionModel aa = (IDistributionModel)a;
                    IDistributionModel bb = (IDistributionModel)b;
                    return aa.getName().equals(bb.getName())
                            && aa.getAnalyzedModel().getName().equals(bb.getAnalyzedModel().getName());
                } else if (a instanceof IDistributionBar && b instanceof IDistributionBar) {
                    IDistributionBar aa = (IDistributionBar)a;
                    IDistributionBar bb = (IDistributionBar)b;
                    return aa.getName().equals(bb.getName())
                            && aa.getDistribution().getName().equals(bb.getDistribution().getName())
                            && aa.getDistribution().getAnalyzedModel().getName()
                                    .equals(bb.getDistribution().getAnalyzedModel().getName());

                } else {
                    return a == null ? b == null : a.equals(b);
                }
            }
        });

        setProviders();
        viewer.setInput(getSite());
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {

                selectedDataElements.clear();
                IStructuredSelection selection = ((IStructuredSelection)event.getSelection());
                Iterator< ? > it = selection.iterator();
                while (it.hasNext()) {
                    Object elementObject = it.next();
                    if (elementObject instanceof INetworkModel) {
                        continue;
                    } else {
                        IDataElement element = (IDataElement)elementObject;
                        selectedDataElements.add(element);
                    }
                }
                NetworkPropertiesView propertiesView = null;
                try {
                    propertiesView = (NetworkPropertiesView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                            .showView("org.amanzi.awe.views.network.views.NetworkPropertiesView");
                } catch (PartInitException e) {
                }
                if (selectedDataElements.size() == 1) {
                    propertiesView.updateTableView((IDataElement)selection.getFirstElement(), currentMode);
                } else {
                    propertiesView.updateTableView(null, currentMode);
                }
            }
        });
        hookContextMenu();
        getSite().setSelectionProvider(viewer);
        setLayout(parent);
    }

    /**
     * Creates a popup menu
     */
    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                NetworkTreeView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void fillContextMenu(IMenuManager manager) {
        SelectAction select = new SelectAction((IStructuredSelection)viewer.getSelection(), SHOW_PROPERTIES, false);
        if (select.isEnabled()) {
            manager.add(select);
        }
        SelectAction edit = new SelectAction((IStructuredSelection)viewer.getSelection(), EDIT_PROPERTIES, true);
        if (select.isEnabled()) {
            manager.add(edit);
        }

        RenameAction renameAction = new RenameAction((IStructuredSelection)viewer.getSelection());
        manager.add(renameAction);

        DeleteAction deleteAction = new DeleteAction((IStructuredSelection)viewer.getSelection());
        manager.add(deleteAction);

        createSubmenuAddToSelectionList((IStructuredSelection)viewer.getSelection(), manager);

        createSubmenuDeleteFromSelectionList((IStructuredSelection)viewer.getSelection(), manager);

        createSubmenuCreateSelectionList((IStructuredSelection)viewer.getSelection(), manager);
    }

    /**
     * add required Listener
     */
    @SuppressWarnings("unchecked")
    private void addListeners() {
        eventManager.addListener(EventsType.UPDATE_DATA, new UpdateDataHandling());
        eventManager.addListener(EventsType.ANALYSE, new AnalyseHandling());
    }

    /**
     * <p>
     * describe listener to refresh Network Tree View
     * </p>
     * 
     * @author Kondratenko_Vladislav
     * @since 1.0.0
     */
    private class UpdateDataHandling implements IEventsListener<UpdateDataEvent> {
        @Override
        public void handleEvent(UpdateDataEvent data) {
            viewer.refresh();
        }

        @Override
        public Object getSource() {
            return null;
        }

    }

    /**
     * <p>
     * describe handling of ANALYSE event
     * </p>
     * 
     * @author Vladislav_Kondratenko
     * @since 1.0.0
     */
    private class AnalyseHandling implements IEventsListener<AnalyseEvent> {

        @Override
        public void handleEvent(AnalyseEvent data) {
            viewer.refresh();
            viewer.expandToLevel(data.getSelectedModel(), 2);
        }

        @Override
        public Object getSource() {
            return NETWORK_TREE_VIEW_ID;
        }

    }

    /**
     * Class uses when user click on data element
     * 
     * @author Kasnitskij_V
     */
    private class SelectAction extends Action {
        private boolean enabled;
        private boolean isEditable;
        private String text;
        private IDataElement currentDataElement;
        private final static String ERROR_MSG = "Some error with select of DataElement";

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        public SelectAction(IStructuredSelection selection, String text, boolean isEditable) {

            enabled = selectedDataElements.size() == 1;
            this.text = text;
            this.isEditable = isEditable;
            currentDataElement = selectedDataElements.iterator().next();
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void run() {
            try {
                currentMode = isEditable;
                NetworkPropertiesView propertiesView = (NetworkPropertiesView)PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage().showView("org.amanzi.awe.views.network.views.NetworkPropertiesView");
                propertiesView.updateTableView(currentDataElement, isEditable);
            } catch (PartInitException e) {
                MessageDialog.openError(null, ERROR_TITLE, ERROR_MSG);
            }
        }
    }

    private class RenameAction extends Action {

        private boolean enabled;
        private final String text;
        private IDataElement dataElement;

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        public RenameAction(IStructuredSelection selection) {
            text = "Rename";
            enabled = selection.size() == 1 && selection.getFirstElement() instanceof IDataElement
                    && !(selection.getFirstElement() instanceof INetworkModel);
            if (enabled) {
                dataElement = (IDataElement)selection.getFirstElement();
                enabled = (dataElement.get(INeoConstants.PROPERTY_NAME_NAME) == null) ? false : true;
            }
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void run() {
            String value = getNewName(dataElement.get(INeoConstants.PROPERTY_NAME_NAME).toString());
            INetworkModel networkModel = (INetworkModel)dataElement.get(INeoConstants.NETWORK_MODEL_NAME);
            try {
                networkModel.renameElement(dataElement, value);
            } catch (AWEException e) {
                MessageDialog.openError(null, "Could not rename!", e.toString());
            }
            viewer.refresh();
        }

        /**
         * Opens a dialog asking the user for a new name.
         * 
         * @return The new name of the element.
         */
        private String getNewName(String oldName) {
            InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), RENAME_MSG, "", oldName, null); //$NON-NLS-1$
            int result = dialog.open();
            if (result == Dialog.CANCEL)
                return oldName;
            return dialog.getValue();
        }
    }

    /**
     * Action to delete all selected nodes and their child nodes in the graph, but not nodes related
     * by other geographic relationships. The result is designed to remove sub-tree's from the tree
     * view, leaving remaining tree nodes in place.
     * 
     * @author Kasnitskij_V
     * @since 1.0.0
     */
    private class DeleteAction extends Action {
        private final List<IDataElement> dataElementsToDelete;
        private String text = null;
        private boolean interactive = false;
        private final static String ERROR_MSG = "Error with deleting of DataElement";
        private final static String SELECT_DATA_ELEMENTS_TO_DELETE = "Select data elements to delete";
        private final static String DELETE_DATA_ELEMENT = "Delete data element";
        private final static String DELETE_DATA_ELEMENT_MSG = "?\n\nAll contained data will also be deleted!";

        private DeleteAction(List<IDataElement> nodesToDelete, String text) {
            this.dataElementsToDelete = nodesToDelete;
            this.text = text;
        }

        @SuppressWarnings("rawtypes")
        private DeleteAction(IStructuredSelection selection) {
            interactive = true;
            dataElementsToDelete = new ArrayList<IDataElement>();
            Iterator iterator = selection.iterator();
            HashSet<String> nodeTypes = new HashSet<String>();
            while (iterator.hasNext()) {
                Object element = iterator.next();
                if (element != null && element instanceof IDataElement && !(element instanceof INetworkModel)) {
                    dataElementsToDelete.add((IDataElement)element);
                }
            }
            String type = nodeTypes.size() == 1 ? nodeTypes.iterator().next() : "node";
            switch (dataElementsToDelete.size()) {
            case 0:
                text = SELECT_DATA_ELEMENTS_TO_DELETE;
                break;
            case 1:
                text = "Delete " + type + " '" + dataElementsToDelete.get(0).toString() + "'";
                break;
            case 2:
            case 3:
            case 4:
                for (IDataElement dataElement : dataElementsToDelete) {
                    if (text == null) {
                        text = "Delete " + type + "s " + dataElement;
                    } else {
                        text += ", " + dataElement;
                    }
                }
                break;
            default:
                text = "Delete " + dataElementsToDelete.size() + " " + type + "s";
                break;
            }
            // TODO: Find a more general solution
            text = text.replaceAll("citys", "cities");
        }

        @Override
        public void run() {

            if (interactive) {
                MessageBox msg = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.YES | SWT.NO);
                msg.setText(DELETE_DATA_ELEMENT);
                msg.setMessage(getText() + DELETE_DATA_ELEMENT_MSG);
                int result = msg.open();
                if (result != SWT.YES) {
                    return;
                }
            }

            // Kasnitskij_V:
            // It's need when user want to delete nodes using bad-way.
            // For example, if we have a structure city->site->sector with values
            // Dortmund->{AMZ000210, AMZ000234->{A0234, A0236, A0289}}
            // and user choose to delete nodes Dortmund, AMZ000234, A0236.
            // We should delete in start A0236, then AMZ000234 and
            // all it remained nodes, and in the end - Dortmund and all it remained nodes
            int countOfNodesToDelete = dataElementsToDelete.size();
            IDataElement[] dataElementsToDeleteArray = new IDataElement[countOfNodesToDelete];
            dataElementsToDelete.toArray(dataElementsToDeleteArray);

            for (int i = countOfNodesToDelete - 1; i >= 0; i--) {
                IDataElement dataElement = dataElementsToDeleteArray[i];
                INetworkModel networkModel = (INetworkModel)dataElement.get(INeoConstants.NETWORK_MODEL_NAME);
                try {
                    networkModel.deleteElement(dataElement);
                } catch (AWEException e) {
                    MessageDialog.openError(null, ERROR_TITLE, ERROR_MSG);
                }
            }

            viewer.refresh();
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public boolean isEnabled() {
            return dataElementsToDelete.size() > 0;
        }
    }

    /**
     * Creates submenu - Create selection List
     * 
     * @param selection
     * @param manager
     */
    @SuppressWarnings("rawtypes")
    private void createSubmenuCreateSelectionList(IStructuredSelection selection, IMenuManager manager) {
        if (selection.size() == 1) {
            Iterator it = selection.iterator();
            Object elementObject = it.next();
            if (elementObject instanceof INetworkModel) {
                CreateSelectionList createSelectionList = new CreateSelectionList((IStructuredSelection)viewer.getSelection());
                manager.add(createSelectionList);
            }
        }
    }

    /**
     * Action for creating of selection list
     * 
     * @author Ladornaya_A
     * @since 1.0.0
     */
    private class CreateSelectionList extends Action {

        private boolean enabled;
        private final String text;
        private INetworkModel network;

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        public CreateSelectionList(IStructuredSelection selection) {
            text = "Create selection list";
            enabled = selection.size() == 1 && selection.getFirstElement() instanceof INetworkModel;
            if (enabled) {
                network = (INetworkModel)selection.getFirstElement();
            }
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void run() {
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            SelectionListDialog pdialog = new SelectionListDialog(shell, network, "New selection list", SWT.OK);
            if (pdialog.open() == SWT.OK) {

            } else {

            }
        }

    }

    /**
     * Method create submenu - Add to selection list
     * 
     * @param selection
     * @param manager
     */
    @SuppressWarnings("rawtypes")
    private void createSubmenuAddToSelectionList(IStructuredSelection selection, IMenuManager manager) {

        boolean isNetwork = false;
        boolean isSector = true;
        boolean firstNode = true;
        boolean isOneNetwork = true;
        String text = "Add to selection list";
        String nameNetwork = "";
        INetworkModel network = null;
        final String ERROR_MSG = "Error when you try to add DataElement to selection list";

        // Sub menu
        Set<IDataElement> selectedNodes = new HashSet<IDataElement>();
        MenuManager subMenu = new MenuManager(text);

        Iterator it = selection.iterator();
        while (it.hasNext()) {
            Object elementObject = it.next();
            if (elementObject instanceof INetworkModel) {
                isNetwork = true;
                continue;
            } else {
                IDataElement element = (IDataElement)elementObject;
                selectedNodes.add(element);
                if (!NodeTypeManager.getType(element).getId().equals(NetworkElementNodeType.SECTOR.getId())) {
                    isSector = false;
                }
                network = (INetworkModel)((DataElement)element).get(INeoConstants.NETWORK_MODEL_NAME);
                if (firstNode) {
                    nameNetwork = network.getName();
                    firstNode = false;
                } else {
                    if (!network.getName().equals(nameNetwork)) {
                        isOneNetwork = false;
                    }
                }

            }
        }
        if (isSector && isOneNetwork && !isNetwork) {
            try {
                INetworkModel networkModel = ProjectModel.getCurrentProjectModel().findNetwork(nameNetwork);
                Iterable<ISelectionModel> selectionModel = networkModel.getAllSelectionModels();
                Iterator<ISelectionModel> iterator = selectionModel.iterator();
                while (iterator.hasNext()) {
                    String nameSelectionList = iterator.next().getName();
                    AddToSelectionListAction addToSelectionListAction = new AddToSelectionListAction(
                            (IStructuredSelection)viewer.getSelection(), nameSelectionList, selectedNodes, networkModel);
                    subMenu.add(addToSelectionListAction);
                }
                manager.add(subMenu);
            } catch (AWEException e) {
                MessageDialog.openError(null, ERROR_TITLE, ERROR_MSG);
            }

        }
    }

    /**
     * Method create submenu - Delete from selection list
     * 
     * @param selection
     * @param manager
     */
    @SuppressWarnings("rawtypes")
    private void createSubmenuDeleteFromSelectionList(IStructuredSelection selection, IMenuManager manager) {

        boolean isNetwork = true;
        boolean isSector = true;
        String text = "Delete from selection list";
        INetworkModel network;
        final String ERROR_MSG = "Error when you try to delete DataElement from selection list";

        // Sub menu
        IDataElement element = null;
        MenuManager subMenu = new MenuManager(text);

        if (selection.size() == 1) {
            Iterator it = selection.iterator();
            Object elementObject = it.next();
            if (!(elementObject instanceof INetworkModel)) {
                element = (IDataElement)elementObject;
                if (!NodeTypeManager.getType(element).getId().equals(NetworkElementNodeType.SECTOR.getId())) {
                    isSector = false;
                }
                isNetwork = false;
            }
            if (!isNetwork && isSector) {
                network = (INetworkModel)((DataElement)element).get(INeoConstants.NETWORK_MODEL_NAME);
                Iterable<ISelectionModel> modelsOfSector;
                try {
                    modelsOfSector = network.getAllSelectionModelsOfSector(element);
                    Iterator<ISelectionModel> iterator = modelsOfSector.iterator();
                    while (iterator.hasNext()) {
                        ISelectionModel model = iterator.next();
                        DeleteFromSelectionListAction deleteFromSelectionListAction = new DeleteFromSelectionListAction(model,
                                element);
                        subMenu.add(deleteFromSelectionListAction);
                    }
                } catch (AWEException e) {
                    MessageDialog.openError(null, ERROR_TITLE, ERROR_MSG);
                }
                manager.add(subMenu);
            }
        }
    }

    /**
     * Action for adding of sectors to selection list
     * 
     * @author Ladornaya_A
     * @since 1.0.0
     */
    private class AddToSelectionListAction extends Action {
        private boolean enabled = true;
        private final String text;
        private Set<IDataElement> selectedNodes = new HashSet<IDataElement>();
        private ISelectionModel selectionModel;
        private List<String> nameExistingSector = new ArrayList<String>();
        private final static String ERROR_MSG = "Error when you try to add DataElement to selection list";

        /**
         * Constructor
         * 
         * @param selection - selection
         * @throws AWEException
         */
        public AddToSelectionListAction(IStructuredSelection selection, String nameSelectionList, Set<IDataElement> selectedNodes,
                INetworkModel networkModel) throws AWEException {
            text = nameSelectionList;
            this.selectedNodes = selectedNodes;
            this.selectionModel = networkModel.findSelectionModel(text);
            for (IDataElement element : selectedNodes) {
                if (selectionModel.isExistSelectionLink(element)) {
                    nameExistingSector.add(element.toString());
                    enabled = false;
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void run() {
            try {
                if (enabled == false) {
                    Collections.sort(nameExistingSector);
                    String msg = "";
                    String sectors = "Sector";
                    if (nameExistingSector.size() > 1) {
                        sectors = sectors + "s";
                    }
                    sectors = sectors + " ";
                    int i;
                    for (i = 0; i < nameExistingSector.size() - 1; i++) {
                        sectors = sectors + "" + nameExistingSector.get(i) + ", ";
                    }
                    sectors = sectors + "" + nameExistingSector.get(i) + " already exist in list - ";
                    msg = sectors + " " + text + "!";
                    MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            "Already exist!", msg);
                } else {
                    Iterator<IDataElement> it = selectedNodes.iterator();
                    while (it.hasNext()) {
                        IDataElement element = it.next();
                        selectionModel.linkToSector(element);
                    }
                }
            } catch (AWEException e) {
                MessageDialog.openError(null, ERROR_TITLE, ERROR_MSG);
            }
        }
    }

    /**
     * Action for delete sector from selection list
     * 
     * @author Ladornaya_A
     * @since 1.0.0
     */
    private class DeleteFromSelectionListAction extends Action {
        private final String text;
        private IDataElement sector;
        private ISelectionModel model;

        /**
         * Constructor
         * 
         * @param selection - selection
         * @throws AWEException
         */
        public DeleteFromSelectionListAction(ISelectionModel model, IDataElement sector) throws AWEException {
            text = model.getName();
            this.sector = sector;
            this.model = model;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void run() {
            model.deleteSelectionLink(sector);
        }
    }

    /**
     * @param parent
     */
    private void setLayout(Composite parent) {
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        parent.setLayout(layout);
        FormData formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        tSearch.setLayoutData(formData);

        formData = new FormData();
        formData.top = new FormAttachment(tSearch, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        formData.bottom = new FormAttachment(100, -5);
        viewer.getTree().setLayoutData(formData);
    }

    /**
     * Set Label and Content providers for TreeView
     * 
     * @param neoServiceProvider
     */

    protected void setProviders() {
        viewer.setContentProvider(new NetworkTreeContentProvider());
        viewer.setLabelProvider(new NetworkTreeLabelProvider());
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    /**
     * Select node
     * 
     * @param dataElement - dataElement to select
     */
    public void selectDataElement(IDataElement dataElement) {
        viewer.refresh();
        viewer.reveal(dataElement);
        viewer.setSelection(new StructuredSelection(new Object[] {dataElement}));
    }

}