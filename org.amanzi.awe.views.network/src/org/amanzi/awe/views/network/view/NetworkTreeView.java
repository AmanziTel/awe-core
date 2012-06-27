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
import org.amanzi.neo.model.distribution.IDistributionBar;
import org.amanzi.neo.model.distribution.IDistributionModel;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.ISelectionModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.neo.services.ui.enums.EventsType;
import org.amanzi.neo.services.ui.events.AnalyseEvent;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.IEventsListener;
import org.amanzi.neo.services.ui.events.ShowOnMapEvent;
import org.amanzi.neo.services.ui.events.UpdateDataEvent;
import org.amanzi.neo.services.ui.providers.CommonViewLabelProvider;
import org.apache.commons.lang3.StringUtils;
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
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
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
        private final ISelectionModel selectionModel;
        private final List<String> nameExistingSector = new ArrayList<String>();

        /**
         * Constructor
         * 
         * @param selection - selection
         * @throws AWEException
         */
        public AddToSelectionListAction(IStructuredSelection selection, String nameSelectionList, Set<IDataElement> selectedNodes,
                INetworkModel networkModel) throws AWEException {
            this.text = nameSelectionList;
            this.selectedNodes = selectedNodes;
            this.selectionModel = networkModel.findSelectionModel(this.text);
            for (IDataElement element : selectedNodes) {
                if (this.selectionModel.isExistSelectionLink(element)) {
                    this.nameExistingSector.add(element.toString());
                    this.enabled = false;
                }
            }
        }

        @Override
        public String getText() {
            return this.text;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void run() {
            try {
                if (this.enabled == false) {
                    Collections.sort(this.nameExistingSector);
                    String msg = StringUtils.EMPTY;
                    String sectors = NetworkMessages.SECTOR;
                    if (this.nameExistingSector.size() > 1) {
                        sectors = sectors + "s";
                    }
                    sectors = sectors + " ";
                    int i;
                    for (i = 0; i < (this.nameExistingSector.size() - 1); i++) {
                        sectors = sectors + "" + this.nameExistingSector.get(i) + ", ";
                    }
                    sectors = sectors + "" + this.nameExistingSector.get(i) + " already exist in list - ";
                    msg = sectors + " " + this.text + "!";
                    MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            NetworkMessages.ALREADY_EXIST, msg);
                } else {
                    Iterator<IDataElement> it = this.selectedNodes.iterator();
                    while (it.hasNext()) {
                        IDataElement element = it.next();
                        this.selectionModel.linkToSector(element);
                    }
                }
            } catch (AWEException e) {
                MessageDialog.openError(null, NetworkMessages.ERROR_TITLE, NetworkMessages.ADD_TO_SELECTION_LIST_ERROR);
            }
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
        public Object getSource() {
            return NETWORK_TREE_VIEW_ID;
        }

        @Override
        public void handleEvent(AnalyseEvent data) {
            NetworkTreeView.this.viewer.expandToLevel(data.getSelectedModel(), 2);
            List<IDataElement> elements = data.getSelectedElements();
            if (!elements.isEmpty()) {
                NetworkTreeView.this.notInterruptEvent = Boolean.FALSE;
                NetworkTreeView.this.viewer.setSelection(new StructuredSelection(elements.toArray()));
            }
        }
    }

    /**
     * TODO Purpose of NetworkTreeView
     * <p>
     * Action for copy element
     * </p>
     * 
     * @author ladornaya_a
     * @since 1.0.0
     */
    private class CopyOfElementAction extends Action {

        private final boolean enabled;
        private IDataElement element;

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        public CopyOfElementAction(IStructuredSelection selection) {
            this.enabled = (selection.size() == 1) && !(selection.getFirstElement() instanceof INetworkModel);
            if (this.enabled) {
                this.element = (IDataElement)selection.getFirstElement();
            }
        }

        @Override
        public String getText() {
            return NetworkMessages.COPY_OF_ELEMENT;
        }

        @Override
        public boolean isEnabled() {
            return this.enabled;
        }

        @Override
        public void run() {
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            CopyOfElementDialog cdialog = new CopyOfElementDialog(shell, this.element, NetworkMessages.COPY_OF_ELEMENT, SWT.OK);
            if (cdialog.open() == SWT.OK) {

            } else {

            }
        }
    }

    /**
     * TODO Purpose of NetworkTreeView
     * <p>
     * Action for creating of new element
     * </p>
     * 
     * @author ladornaya_a
     * @since 1.0.0
     */
    private class CreateNewElementAction extends Action {

        // submenu name
        private final String text;

        // selected element
        private final IDataElement element;

        // network model
        private final INetworkModel networkModel;

        // type of new element
        private final String type;

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        public CreateNewElementAction(IStructuredSelection selection, String type, IDataElement element, INetworkModel networkModel) {
            this.text = type;
            this.element = element;
            this.type = type;
            this.networkModel = networkModel;
        }

        @Override
        public String getText() {
            return this.text;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void run() {
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            CreateNewElementDialog cdialog = new CreateNewElementDialog(shell, this.element, this.type, this.networkModel,
                    NetworkMessages.CREATE_NEW_ELEMENT, SWT.OK);
            if (cdialog.open() == SWT.OK) {

            } else {

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

        private final boolean enabled;
        private INetworkModel network;

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        public CreateSelectionList(IStructuredSelection selection) {
            this.enabled = (selection.size() == 1) && (selection.getFirstElement() instanceof INetworkModel);
            if (this.enabled) {
                this.network = (INetworkModel)selection.getFirstElement();
            }
        }

        @Override
        public String getText() {
            return NetworkMessages.CREATE_SELECTION_LIST;
        }

        @Override
        public boolean isEnabled() {
            return this.enabled;
        }

        @Override
        public void run() {
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            SelectionListDialog pdialog = new SelectionListDialog(shell, this.network, NetworkMessages.NEW_SELECTION_LIST, SWT.OK);
            if (pdialog.open() == SWT.OK) {

            } else {

            }
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

        @SuppressWarnings("rawtypes")
        private DeleteAction(IStructuredSelection selection) {
            this.interactive = true;
            this.dataElementsToDelete = new ArrayList<IDataElement>();
            Iterator iterator = selection.iterator();
            HashSet<String> nodeTypes = new HashSet<String>();
            while (iterator.hasNext()) {
                Object element = iterator.next();
                if ((element != null) && (element instanceof IDataElement) && !(element instanceof INetworkModel)) {
                    this.dataElementsToDelete.add((IDataElement)element);
                }
            }
            String type = nodeTypes.size() == 1 ? nodeTypes.iterator().next() : "node";
            switch (this.dataElementsToDelete.size()) {
            case 0:
                this.text = NetworkMessages.SELECT_DATA_ELEMENTS_TO_DELETE;
                break;
            case 1:
                this.text = "Delete " + type + " '" + this.dataElementsToDelete.get(0).toString() + "'";
                break;
            case 2:
            case 3:
            case 4:
                for (IDataElement dataElement : this.dataElementsToDelete) {
                    if (this.text == null) {
                        this.text = "Delete " + type + "s " + dataElement;
                    } else {
                        this.text += ", " + dataElement;
                    }
                }
                break;
            default:
                this.text = "Delete " + this.dataElementsToDelete.size() + " " + type + "s";
                break;
            }
            // TODO: Find a more general solution
            this.text = this.text.replaceAll("citys", "cities");
        }

        private DeleteAction(List<IDataElement> nodesToDelete, String text) {
            this.dataElementsToDelete = nodesToDelete;
            this.text = text;
        }

        @Override
        public String getText() {
            return this.text;
        }

        @Override
        public boolean isEnabled() {
            return this.dataElementsToDelete.size() > 0;
        }

        @Override
        public void run() {

            if (this.interactive) {
                MessageBox msg = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.YES | SWT.NO);
                msg.setText(NetworkMessages.DELETE_DATA_ELEMENT);
                msg.setMessage(getText() + NetworkMessages.DELETE_DATA_ELEMENT_MSG);
                int result = msg.open();
                if (result != SWT.YES) {
                    return;
                }
            }

            // Kasnitskij_V:
            // It's need when user want to delete nodes using bad-way.
            // For example, if we have a structure city->site->sector with
            // values
            // Dortmund->{AMZ000210, AMZ000234->{A0234, A0236, A0289}}
            // and user choose to delete nodes Dortmund, AMZ000234, A0236.
            // We should delete in start A0236, then AMZ000234 and
            // all it remained nodes, and in the end - Dortmund and all it
            // remained nodes
            int countOfNodesToDelete = this.dataElementsToDelete.size();
            IDataElement[] dataElementsToDeleteArray = new IDataElement[countOfNodesToDelete];
            this.dataElementsToDelete.toArray(dataElementsToDeleteArray);

            for (int i = countOfNodesToDelete - 1; i >= 0; i--) {
                IDataElement dataElement = dataElementsToDeleteArray[i];
                INetworkModel networkModel = (INetworkModel)dataElement.get(INeoConstants.NETWORK_MODEL_NAME);
                try {
                    networkModel.deleteElement(dataElement);
                } catch (AWEException e) {
                    MessageDialog.openError(null, NetworkMessages.ERROR_TITLE, NetworkMessages.ERROR_MSG);
                }
            }
            NetworkTreeView.this.eventManager.fireEvent(new UpdateDataEvent());
            NetworkTreeView.this.viewer.refresh();
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
        private final IDataElement sector;
        private final ISelectionModel model;

        /**
         * Constructor
         * 
         * @param selection - selection
         * @throws AWEException
         */
        public DeleteFromSelectionListAction(ISelectionModel model, IDataElement sector) throws AWEException {
            this.text = model.getName();
            this.sector = sector;
            this.model = model;
        }

        @Override
        public String getText() {
            return this.text;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void run() {
            this.model.deleteSelectionLink(this.sector);
        }
    }

    /**
     * TODO Purpose of NetworkTreeView
     * <p>
     * Action which export selected network to file
     * </p>
     * 
     * @author ladornaya_a
     * @since 1.0.0
     */
    private class ExportToFileAction extends Action {

        private final boolean enabled;
        private INetworkModel network;

        /**
         * Constructor
         * 
         * @param selection selected elements
         */
        public ExportToFileAction(IStructuredSelection selection) {
            this.enabled = (selection.size() == 1) && (selection.getFirstElement() instanceof INetworkModel);
            if (this.enabled) {
                this.network = (INetworkModel)selection.getFirstElement();
            }
        }

        @Override
        public String getText() {
            return NetworkMessages.EXPORT_WINDOW_TITLE;
        }

        @Override
        public boolean isEnabled() {
            return this.enabled;
        }

        @Override
        public void run() {
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            ExportToFileSettingsWizard wizard = new ExportToFileSettingsWizard(this.network);
            WizardDialog dialog = new WizardDialog(shell, wizard);
            dialog.open();
        }

    }

    private class RenameAction extends Action {

        private boolean enabled;
        private IDataElement dataElement;

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        public RenameAction(IStructuredSelection selection) {
            this.enabled = (selection.size() == 1) && (selection.getFirstElement() instanceof IDataElement)
                    && !(selection.getFirstElement() instanceof INetworkModel);
            if (this.enabled) {
                this.dataElement = (IDataElement)selection.getFirstElement();
                this.enabled = (this.dataElement.get(INeoConstants.PROPERTY_NAME_NAME) == null) ? false : true;
            }
        }

        /**
         * Opens a dialog asking the user for a new name.
         * 
         * @return The new name of the element.
         */
        private String getNewName(String oldName) {
            InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), NetworkMessages.RENAME_MSG,
                    StringUtils.EMPTY, oldName, null); //$NON-NLS-1$
            int result = dialog.open();
            if (result == Dialog.CANCEL) {
                return oldName;
            }
            return dialog.getValue();
        }

        @Override
        public String getText() {
            return NetworkMessages.RENAME;
        }

        @Override
        public boolean isEnabled() {
            return this.enabled;
        }

        @Override
        public void run() {
            String value = getNewName(this.dataElement.get(INeoConstants.PROPERTY_NAME_NAME).toString());
            INetworkModel networkModel = (INetworkModel)this.dataElement.get(INeoConstants.NETWORK_MODEL_NAME);
            try {
                networkModel.renameElement(this.dataElement, value);
            } catch (AWEException e) {
                MessageDialog.openError(null, NetworkMessages.ERROR_TITLE, e.toString());
            }
            NetworkTreeView.this.eventManager.fireEvent(new UpdateDataEvent());
            NetworkTreeView.this.viewer.refresh();
        }
    }

    /**
     * Class uses when user click on data element
     * 
     * @author Kasnitskij_V
     */
    private class SelectAction extends Action {
        private final boolean enabled;
        private final boolean isEditable;
        private final String text;

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        public SelectAction(IStructuredSelection selection, String text, boolean isEditable) {

            this.enabled = true;
            this.text = text;
            this.isEditable = isEditable;
            // currentDataElement = selectedDataElements.iterator().next();
        }

        @Override
        public String getText() {
            return this.text;
        }

        @Override
        public boolean isEnabled() {
            return this.enabled;
        }

        @Override
        public void run() {
            updateNetworkPropertiesView(this.isEditable);
        }
    }

    /**
     * TODO Purpose of NetworkTreeView
     * <p>
     * </p>
     * 
     * @author ladornaya_a
     * @since 1.0.0
     */
    private class ShowOnMapAction extends Action {

        // zoom
        private static final double ZOOM = 0d;

        // selected element
        private final IStructuredSelection selection;

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        public ShowOnMapAction(IStructuredSelection selection) {
            this.selection = selection;
        }

        @Override
        public String getText() {
            return NetworkMessages.SHOW_ON_MAP;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public void run() {
            Iterator it = this.selection.iterator();
            Object elementObject = it.next();

            NetworkModel networkModel;

            // if selected element - network
            if (elementObject instanceof INetworkModel) {
                networkModel = (NetworkModel)elementObject;
                networkModel.clearSelectedElements();
                NetworkTreeView.this.eventManager.fireEvent(new ShowOnMapEvent(networkModel, ZOOM));
            } else {
                IDataElement element = (IDataElement)elementObject;

                // network
                networkModel = (NetworkModel)element.get(INeoConstants.NETWORK_MODEL_NAME);

                // if selected element - site or sector
                if (element.get(AbstractService.TYPE).equals(NetworkElementNodeType.SITE.getId())
                        || element.get(AbstractService.TYPE).equals(NetworkElementNodeType.SECTOR.getId())) {
                    networkModel.clearSelectedElements();
                    networkModel.setSelectedDataElementToList(element);
                    NetworkTreeView.this.eventManager.fireEvent(new ShowOnMapEvent(networkModel, true));
                }
            }
        }
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
        public Object getSource() {
            return null;
        }

        @Override
        public void handleEvent(UpdateDataEvent data) {
            NetworkTreeView.this.viewer.refresh();
        }

    }

    public static final String NETWORK_TREE_VIEW_ID = "org.amanzi.awe.views.network.views.NewNetworkTreeView";

    public static final String PROPERTIES_VIEW_ID = "org.amanzi.awe.views.network.views.PropertiesView";

    private boolean currentMode = false;

    private boolean notInterruptEvent = Boolean.TRUE;

    /*
     * TreeViewer for database Nodes
     */
    protected TreeViewer viewer;

    private Text tSearch;

    private final Set<IDataElement> selectedDataElements = new HashSet<IDataElement>();

    /**
     * event manager
     */
    private final EventManager eventManager;

    /**
     * The constructor.
     */
    public NetworkTreeView() {
        this.eventManager = EventManager.getInstance();
        addListeners();
    }

    /**
     * add required Listener
     */
    @SuppressWarnings("unchecked")
    private void addListeners() {
        this.eventManager.addListener(EventsType.UPDATE_DATA, new UpdateDataHandling());
        this.eventManager.addListener(EventsType.ANALYSE, new AnalyseHandling());
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {
        this.tSearch = new Text(parent, SWT.BORDER);
        this.viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        this.viewer.setComparer(new IElementComparer() {

            @Override
            public boolean equals(Object a, Object b) {
                if ((a instanceof IDistributionalModel) && (b instanceof IDistributionalModel)) {
                    return ((IDistributionalModel)a).getName().equals(((IDistributionalModel)b).getName());
                } else if ((a instanceof IDistributionModel) && (b instanceof IDistributionModel)) {
                    IDistributionModel aa = (IDistributionModel)a;
                    IDistributionModel bb = (IDistributionModel)b;
                    return aa.getName().equals(bb.getName())
                            && aa.getAnalyzedModel().getName().equals(bb.getAnalyzedModel().getName());
                } else if ((a instanceof IDistributionBar) && (b instanceof IDistributionBar)) {
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

            @Override
            public int hashCode(Object element) {
                return 0;
            }
        });

        setProviders();
        this.viewer.setInput(getSite());

        this.viewer.getTree().addMouseTrackListener(new MouseTrackListener() {

            @Override
            public void mouseEnter(MouseEvent e) {
                NetworkTreeView.this.notInterruptEvent = Boolean.TRUE;
            }

            @Override
            public void mouseExit(MouseEvent e) {
            }

            @Override
            public void mouseHover(MouseEvent e) {
            }
        });

        this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (NetworkTreeView.this.notInterruptEvent) {
                    NetworkTreeView.this.selectedDataElements.clear();
                    IStructuredSelection selection = ((IStructuredSelection)event.getSelection());
                    Iterator< ? > it = selection.iterator();
                    INetworkModel model = null;
                    while (it.hasNext()) {
                        Object elementObject = it.next();
                        if (elementObject instanceof INetworkModel) {
                            model = (INetworkModel)elementObject;
                            continue;
                        } else {
                            IDataElement element = (IDataElement)elementObject;
                            model = (INetworkModel)element.get(INeoConstants.NETWORK_MODEL_NAME);
                            NetworkTreeView.this.selectedDataElements.add(element);
                        }
                    }
                    updateNetworkPropertiesView(NetworkTreeView.this.currentMode);

                    if (model != null) {
                        model.clearSelectedElements();
                        model.setSelectedDataElements(new ArrayList<IDataElement>(NetworkTreeView.this.selectedDataElements));
                        NetworkTreeView.this.eventManager.fireEvent(new ShowOnMapEvent(model, false));
                    }

                }
            }
        });
        hookContextMenu();
        getSite().setSelectionProvider(this.viewer);
        setLayout(parent);
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
        String nameNetwork = StringUtils.EMPTY;
        INetworkModel network = null;

        // Sub menu
        Set<IDataElement> selectedNodes = new HashSet<IDataElement>();
        MenuManager subMenu = new MenuManager(NetworkMessages.ADD_TO_SELECTION_LIST);

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
                            (IStructuredSelection)this.viewer.getSelection(), nameSelectionList, selectedNodes, networkModel);
                    subMenu.add(addToSelectionListAction);
                }
                manager.add(subMenu);
            } catch (AWEException e) {
                MessageDialog.openError(null, NetworkMessages.ERROR_TITLE, NetworkMessages.ADD_TO_SELECTION_LIST_ERROR);
            }

        }
    }

    /**
     * Create new element and copy properties from selected element to new element
     * 
     * @param selection selected elements
     * @param manager menu manager
     */
    @SuppressWarnings("rawtypes")
    private void createSubmenuCopyOfElement(IStructuredSelection selection, IMenuManager manager) {
        if (selection.size() == 1) {
            Iterator it = selection.iterator();
            Object elementObject = it.next();
            if (!(elementObject instanceof INetworkModel)) {
                CopyOfElementAction copyOfElementAction = new CopyOfElementAction((IStructuredSelection)this.viewer.getSelection());
                manager.add(copyOfElementAction);
            }
        }
    }

    /**
     * Added menu for creating of new element
     * 
     * @param selection selected elements
     * @param manager menu manager
     */
    @SuppressWarnings("rawtypes")
    private void createSubmenuCreateNewElement(IStructuredSelection selection, IMenuManager manager) {

        // boolean values for node types
        boolean isNetwork = false;
        boolean isCity = false;
        boolean isMsc = false;
        boolean isBsc = false;
        boolean isSite = false;

        // parent element for new element
        IDataElement element = null;

        INetworkModel networkModel;

        if (selection.size() == 1) {

            // selected element
            Iterator it = selection.iterator();
            Object elementObject = it.next();

            // Sub menu
            MenuManager subMenu = new MenuManager(NetworkMessages.CREATE_NEW_ELEMENT);

            // if selected element - network
            if (elementObject instanceof INetworkModel) {
                isNetwork = true;
                networkModel = (INetworkModel)elementObject;
            } else {
                element = (IDataElement)elementObject;
                INodeType typeNode = NodeTypeManager.getType(element);
                String type = typeNode.getId();
                networkModel = (INetworkModel)element.get(INeoConstants.NETWORK_MODEL_NAME);

                // if selected element - city
                if (type.equals(NetworkElementNodeType.CITY.getId())) {
                    isCity = true;
                }

                // if selected element - msc
                else if (type.equals(NetworkElementNodeType.MSC.getId())) {
                    isMsc = true;
                }

                // if selected element - bsc
                else if (type.equals(NetworkElementNodeType.BSC.getId())) {
                    isBsc = true;
                }

                // if selected element - site
                else if (type.equals(NetworkElementNodeType.SITE.getId())) {
                    isSite = true;
                }
            }

            if (isNetwork || isMsc || isCity || isBsc) {
                // site
                CreateNewElementAction createNewElementActionSite = new CreateNewElementAction(
                        (IStructuredSelection)this.viewer.getSelection(), NetworkElementNodeType.SITE.getId(), element,
                        networkModel);
                subMenu.add(createNewElementActionSite);

                if (isNetwork || isMsc || isCity) {
                    // bsc
                    CreateNewElementAction createNewElementActionBsc = new CreateNewElementAction(
                            (IStructuredSelection)this.viewer.getSelection(), NetworkElementNodeType.BSC.getId(), element,
                            networkModel);
                    subMenu.add(createNewElementActionBsc);

                    if (isNetwork || isCity) {
                        // msc
                        CreateNewElementAction createNewElementActionMsc = new CreateNewElementAction(
                                (IStructuredSelection)this.viewer.getSelection(), NetworkElementNodeType.MSC.getId(), element,
                                networkModel);
                        subMenu.add(createNewElementActionMsc);

                        if (isNetwork) {
                            // city
                            CreateNewElementAction createNewElementActionCity = new CreateNewElementAction(
                                    (IStructuredSelection)this.viewer.getSelection(), NetworkElementNodeType.CITY.getId(), element,
                                    networkModel);
                            subMenu.add(createNewElementActionCity);
                        }
                    }
                }
            } else if (isSite) {
                // sector
                CreateNewElementAction createNewElementActionSector = new CreateNewElementAction(
                        (IStructuredSelection)this.viewer.getSelection(), NetworkElementNodeType.SECTOR.getId(), element,
                        networkModel);
                subMenu.add(createNewElementActionSector);
            }

            // add submenu to menu
            manager.add(subMenu);
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
                CreateSelectionList createSelectionList = new CreateSelectionList((IStructuredSelection)this.viewer.getSelection());
                manager.add(createSelectionList);
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
        INetworkModel network;

        // Sub menu
        IDataElement element = null;
        MenuManager subMenu = new MenuManager(NetworkMessages.DELETE_FROM_SELECTION_LIST);

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
                    MessageDialog.openError(null, NetworkMessages.ERROR_TITLE, NetworkMessages.DELETE_FROM_SELECTION_LIST_ERROR);
                }
                manager.add(subMenu);
            }
        }
    }

    /**
     * create menu - Export to file
     * 
     * @param selection selected elements
     * @param manager menu manager
     */
    @SuppressWarnings("rawtypes")
    private void createSubmenuExportToFile(IStructuredSelection selection, IMenuManager manager) {

        // selection size = 1
        if (selection.size() == 1) {
            Iterator it = selection.iterator();
            Object elementObject = it.next();

            // if network
            if (elementObject instanceof INetworkModel) {
                ExportToFileAction exportToFileAction = new ExportToFileAction((IStructuredSelection)this.viewer.getSelection());
                manager.add(exportToFileAction);
            }
        }
    }

    /**
     * create menu - Show on map
     * 
     * @param selection selected elements
     * @param manager menu manager
     */
    private void createSubmenuShowOnMap(IStructuredSelection selection, IMenuManager manager) {
        if (selection.size() == 1) {
            ShowOnMapAction showOnMap = new ShowOnMapAction((IStructuredSelection)this.viewer.getSelection());
            manager.add(showOnMap);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    private void fillContextMenu(IMenuManager manager) {
        SelectAction select = new SelectAction((IStructuredSelection)this.viewer.getSelection(), NetworkMessages.SHOW_PROPERTIES,
                false);
        if (select.isEnabled()) {
            manager.add(select);
        }
        SelectAction edit = new SelectAction((IStructuredSelection)this.viewer.getSelection(), NetworkMessages.EDIT_PROPERTIES,
                true);
        if (select.isEnabled()) {
            manager.add(edit);
        }

        RenameAction renameAction = new RenameAction((IStructuredSelection)this.viewer.getSelection());
        manager.add(renameAction);

        DeleteAction deleteAction = new DeleteAction((IStructuredSelection)this.viewer.getSelection());
        manager.add(deleteAction);

        createSubmenuAddToSelectionList((IStructuredSelection)this.viewer.getSelection(), manager);

        createSubmenuDeleteFromSelectionList((IStructuredSelection)this.viewer.getSelection(), manager);

        createSubmenuCreateSelectionList((IStructuredSelection)this.viewer.getSelection(), manager);

        createSubmenuCopyOfElement((IStructuredSelection)this.viewer.getSelection(), manager);

        createSubmenuCreateNewElement((IStructuredSelection)this.viewer.getSelection(), manager);

        createSubmenuShowOnMap((IStructuredSelection)this.viewer.getSelection(), manager);

        createSubmenuExportToFile((IStructuredSelection)this.viewer.getSelection(), manager);
    }

    /**
     * Creates a popup menu
     */
    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                NetworkTreeView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
        this.viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, this.viewer);
    }

    /**
     * Select node
     * 
     * @param dataElement - dataElement to select
     */
    public void selectDataElement(IDataElement dataElement) {
        this.viewer.refresh();
        this.viewer.reveal(dataElement);
        this.viewer.setSelection(new StructuredSelection(new Object[] {dataElement}));
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        this.viewer.getControl().setFocus();
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
        this.tSearch.setLayoutData(formData);

        formData = new FormData();
        formData.top = new FormAttachment(this.tSearch, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        formData.bottom = new FormAttachment(100, -5);
        this.viewer.getTree().setLayoutData(formData);
    }

    /**
     * Set Label and Content providers for TreeView
     * 
     * @param neoServiceProvider
     */

    protected void setProviders() {
        this.viewer.setContentProvider(new NetworkTreeContentProvider());
        this.viewer.setLabelProvider(new CommonViewLabelProvider(this.viewer));
    }

    /**
     * Load selected data elements to network properties view
     * 
     * @param isEditable
     */
    private void updateNetworkPropertiesView(boolean isEditable) {
        try {
            this.currentMode = isEditable;
            PropertiesView propertiesView = (PropertiesView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .showView(PROPERTIES_VIEW_ID);
            propertiesView.updateTableView(this.selectedDataElements, isEditable);
        } catch (PartInitException e) {
            MessageDialog.openError(null, NetworkMessages.ERROR_TITLE, NetworkMessages.NETWORK_PROPERTIES_OPEN_ERROR + e);
        }
    }
}