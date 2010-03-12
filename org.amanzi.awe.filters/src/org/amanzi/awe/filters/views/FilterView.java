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
package org.amanzi.awe.filters.views;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.dialogs.ColorEditor;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.awe.filters.AbstractFilter;
import org.amanzi.awe.filters.ChainRule;
import org.amanzi.awe.filters.FilterUtil;
import org.amanzi.awe.filters.tree.TreeNeoNode;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.icons.IconManager;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.PropertyHeader;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;

/**
 * <p>
 * view for handle filters
 * </p>
 * 
 * @author Tsinkel_A
 * @since 1.0.0
 */
public class FilterView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.amanzi.awe.filters.views.FilterView"; //$NON-NLS-1$

    private static final RGB DEFAULT_COLOR = new RGB(0, 0, 0);

    private TreeViewer viewer;
    private DrillDownAdapter drillDownAdapter;
    private AddGroupAction addGroupAction;
    private AddFilterAction addFilterAction;
    private final NeoService service;

    private Node rootFlterNode;

    private Composite groupFrame;

    private Group filterFrame;

    private Text tGroupName;

    private Combo cGroupProperty;

    private Button bGroupOk;

    private Button bGroupCancel;

    private Combo cFilterProperty;

    private Button bFilterOk;

    private Button bFilterCancel;

    private Combo cFirst;

    private Text cFirstText;

    private Combo cSecRel;

    private Combo cSecond;

    private Text cSecondText;

    private TreeNeoNode rootTree;

    private DeleteAction deleteAction;

    private Combo cGis;

    private Text tGisFilter;

    private final LinkedHashMap<String, Node> dataMap;

    private ClearFilter clearFiltr;

    private AssignFilter assignFiltr;

    private AddChainFilterAction addChainFilterAction;

    private Composite filterChainFrame;

    private Text tFilterChainName;

    private Combo cRule;

    private Button bFilterChainOk;

    private Button bFilterChainCancel;

    private ColorEditor filterColorEditor;

    private Label filterColorEditorLabel;

    private Label chainColorEditorLabel;

    private ColorEditor chainColorEditor;

    /**
     * <p>
     * filter tree content provider
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {
        private Object invisibleRoot;
        private TreeNeoNode rootNode;

        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
            Transaction tx = NeoUtils.beginTx(service);
            try {
                if (newInput instanceof TreeNeoNode) {
                    invisibleRoot = newInput;
                    rootNode = (TreeNeoNode)newInput;
                } else if (newInput instanceof Node) {
                    rootNode = new TreeNeoNode(getFileRootNode());
                    invisibleRoot = service.getReferenceNode();
                } else {
                    invisibleRoot = null;
                    rootNode = null;
                }
            } finally {
                tx.finish();
            }
        }

        public void dispose() {
        }

        public Object[] getElements(Object parent) {
            if (invisibleRoot == null) {
                return null;
            } else if (invisibleRoot instanceof Node) {
                return new Object[] {rootNode};
            } else {
                return getChildren(parent);
            }
        }

        public Object getParent(Object child) {
            if (child instanceof TreeNeoNode) {
                return ((TreeNeoNode)child).getParent(service);
            }
            return null;
        }

        public Object[] getChildren(Object parent) {
            if (parent instanceof TreeNeoNode) {
                return ((TreeNeoNode)parent).getChildren(service);
            }
            return new Object[0];
        }

        public boolean hasChildren(Object parent) {
            if (parent instanceof TreeNeoNode)
                return ((TreeNeoNode)parent).hasChildren(service);
            return false;
        }

    }

    /**
     * <p>
     * filter tree label provider
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    class ViewLabelProvider extends LabelProvider {

        @Override
        public String getText(Object obj) {
            return obj.toString();
        }

        @Override
        public Image getImage(Object obj) {
            String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
            if (obj instanceof TreeNeoNode && ((TreeNeoNode)obj).getType() != NodeTypes.FILTER) {
                imageKey = ISharedImages.IMG_OBJ_FOLDER;
            }
            if (obj instanceof TreeNeoNode) {
                TreeNeoNode element = (TreeNeoNode)obj;
                element.formColorImage(service);
                Image image = element.getImage();
                if  (image!=null){
                    return image;
                }
            }
            return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
        }
    }

    /**
     * The constructor.
     */
    public FilterView() {
        service = NeoServiceProvider.getProvider().getService();
        dataMap = new LinkedHashMap<String, Node>();
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        parent.setLayout(layout);
        Composite row = new Composite(parent, SWT.FILL);
        FormData layoutData = new FormData();
        layoutData.left = new FormAttachment(0, 2);
        layoutData.right = new FormAttachment(50, -2);
        layoutData.top = new FormAttachment(0, 2);
        row.setLayoutData(layoutData);
        row.setLayout(new GridLayout(5, false));
        // first row
        Label label = new Label(row, SWT.NONE);
        label.setText(Messages.FilterView_1);
        cGis = new Combo(row, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        cGis.setLayoutData(new GridData(100, SWT.DEFAULT));
        updateGisItems();
        label = new Label(row, SWT.NONE);
        label.setText(Messages.FilterView_2);
        tGisFilter = new Text(row, SWT.READ_ONLY);
        tGisFilter.setLayoutData(new GridData(100, SWT.DEFAULT));
        viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        layoutData = new FormData();
        layoutData.left = new FormAttachment(0, 2);
        layoutData.right = new FormAttachment(50, -2);
        layoutData.top = new FormAttachment(row, 2);
        layoutData.bottom = new FormAttachment(100, -2);
        viewer.getControl().setLayoutData(layoutData);
        groupFrame = new Composite(parent, SWT.BORDER);
        layoutData = new FormData();
        layoutData.left = new FormAttachment(50, 2);
        layoutData.right = new FormAttachment(100, -2);
        layoutData.top = new FormAttachment(0, 2);
        layoutData.bottom = new FormAttachment(100, -2);
        groupFrame.setLayoutData(layoutData);
        groupFrame.setLayout(new GridLayout(2, false));

        filterChainFrame = new Composite(parent, SWT.BORDER);
        layoutData = new FormData();
        layoutData.left = new FormAttachment(50, 2);
        layoutData.right = new FormAttachment(100, -2);
        layoutData.top = new FormAttachment(0, 2);
        layoutData.bottom = new FormAttachment(100, -2);
        filterChainFrame.setLayoutData(layoutData);
        filterChainFrame.setLayout(new GridLayout(2, false));
        filterFrame = new Group(parent, SWT.NONE);
        filterFrame.setLayout(new GridLayout(3, false));
        layoutData = new FormData();
        layoutData.left = new FormAttachment(50, 2);
        layoutData.right = new FormAttachment(100, -2);
        layoutData.top = new FormAttachment(0, 2);
        layoutData.bottom = new FormAttachment(100, -2);
        filterFrame.setLayoutData(layoutData);
        drillDownAdapter = new DrillDownAdapter(viewer);
        viewer.setContentProvider(new ViewContentProvider());
        viewer.setLabelProvider(new ViewLabelProvider());
        viewer.setInput(service.getReferenceNode());

        // group frame
        label = new Label(groupFrame, SWT.NONE);
        label.setText(Messages.FilterView_3);
        tGroupName = new Text(groupFrame, SWT.BORDER);
        tGroupName.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        label = new Label(groupFrame, SWT.NONE);
        label.setText(Messages.FilterView_4);
        cGroupProperty = new Combo(groupFrame, SWT.BORDER | SWT.DROP_DOWN);
        cGroupProperty.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        Composite rowFrame = new Composite(groupFrame, SWT.FILL);
        rowFrame.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
        rowFrame.setLayout(new GridLayout(2, true));
        bGroupOk = new Button(rowFrame, SWT.PUSH | SWT.CENTER);
        bGroupOk.setText(Messages.FilterView_5);
        bGroupOk.setLayoutData(new GridData(100, SWT.DEFAULT));
        bGroupCancel = new Button(rowFrame, SWT.PUSH | SWT.CENTER);
        bGroupCancel.setText(Messages.FilterView_6);
        bGroupCancel.setLayoutData(new GridData(100, SWT.DEFAULT));

        // filter chain frame
        label = new Label(filterChainFrame, SWT.NONE);
        label.setText(Messages.FilterView_17);
        tFilterChainName = new Text(filterChainFrame, SWT.BORDER);
        tFilterChainName.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        label = new Label(filterChainFrame, SWT.NONE);
        label.setText(Messages.FilterView_18);
        cRule = new Combo(filterChainFrame, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        cRule.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        fillRuleCombo();
        chainColorEditorLabel = new Label(filterChainFrame, SWT.NONE);
        chainColorEditorLabel.setText(Messages.FilterView_19);
        chainColorEditor = new ColorEditor(filterChainFrame);
        chainColorEditor.getButton().setVisible(false);
        chainColorEditorLabel.setVisible(false);
        rowFrame = new Composite(filterChainFrame, SWT.FILL);
        rowFrame.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
        rowFrame.setLayout(new GridLayout(2, true));
        bFilterChainOk = new Button(rowFrame, SWT.PUSH | SWT.CENTER);
        bFilterChainOk.setText(Messages.FilterView_5);
        bFilterChainOk.setLayoutData(new GridData(100, SWT.DEFAULT));
        bFilterChainCancel = new Button(rowFrame, SWT.PUSH | SWT.CENTER);
        bFilterChainCancel.setText(Messages.FilterView_6);
        bFilterChainCancel.setLayoutData(new GridData(100, SWT.DEFAULT));

        // filter frame
        label = new Label(filterFrame, SWT.NONE);
        label.setText(Messages.FilterView_7);
        cFilterProperty = new Combo(filterFrame, SWT.BORDER | SWT.DROP_DOWN);
        cFilterProperty.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));
        label = new Label(filterFrame, SWT.NONE);
        label.setText(Messages.FilterView_8);
        cFirst = new Combo(filterFrame, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        cFirst.setItems(getFilters());
        cFirstText = new Text(filterFrame, SWT.BORDER);
        cFirstText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        cSecRel = new Combo(filterFrame, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        cSecRel.setItems(FilterUtil.getFilterRel());
        cSecond = new Combo(filterFrame, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        cSecond.setItems(getFilters());
        cSecondText = new Text(filterFrame, SWT.BORDER);
        cSecondText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        filterColorEditorLabel = new Label(filterFrame, SWT.NONE);
        filterColorEditorLabel.setText(Messages.FilterView_19);
        filterColorEditor = new ColorEditor(filterFrame);
        filterColorEditor.getButton().setVisible(false);
        filterColorEditorLabel.setVisible(false);
        rowFrame = new Composite(filterFrame, SWT.FILL);
        rowFrame.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 3, 1));
        rowFrame.setLayout(new GridLayout(2, true));
        bFilterOk = new Button(rowFrame, SWT.PUSH | SWT.CENTER);
        bFilterOk.setText(Messages.FilterView_9);
        bFilterOk.setLayoutData(new GridData(100, SWT.DEFAULT));
        bFilterCancel = new Button(rowFrame, SWT.PUSH | SWT.CENTER);
        bFilterCancel.setText(Messages.FilterView_10);
        bFilterCancel.setLayoutData(new GridData(100, SWT.DEFAULT));
        filterChainFrame.setVisible(false);
        groupFrame.setVisible(false);
        filterFrame.setVisible(false);
        addListeners();
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
    }

    /**
     *fills rules
     */
    private void fillRuleCombo() {
        List<String> result = new LinkedList<String>();
        for (ChainRule rule : ChainRule.values()) {
            result.add(rule.getId());
        }
        cRule.setItems(result.toArray(new String[0]));
        cRule.select(0);
    }

    /**
     *update gis item
     */
    public void updateGisItems() {
        dataMap.clear();
        Transaction tx = NeoUtils.beginTx(service);
        try {
            Traverser traverse = service.getReferenceNode().traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    return NeoUtils.isGisNode(currentPos.currentNode());
                }
            }, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING);
            for (Node node : traverse) {
                Object type = node.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, ""); //$NON-NLS-1$
                if (type.equals(GisTypes.NETWORK.getHeader())) {
                    dataMap.put(Messages.FilterView_12 + NeoUtils.getNodeName(node), node);
                    final Node networkNode = node.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).getOtherNode(node);
                    dataMap.put(Messages.FilterView_13 + NeoUtils.getNodeName(networkNode), networkNode);
                } else {
                    dataMap.put(NeoUtils.getNodeName(node), node);
                }
            }
            cGis.setItems(dataMap.keySet().toArray(new String[0]));
        } finally {
            tx.finish();
        }
    }

    /**
     * @return
     */
    private String[] getFilters() {
        return FilterUtil.getFilterDes();
    }

    /**
     *
     */
    private void addListeners() {
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                updateActions();
                groupFrame.setVisible(false);
                filterFrame.setVisible(false);
                filterChainFrame.setVisible(false);
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                if (selection.size() != 1) {
                    return;
                }
                TreeNeoNode element = (TreeNeoNode)selection.getFirstElement();
                switch (element.getType()) {
                case FILTER_GROUP:
                    showGroupFilter(element);
                    break;
                case FILTER:
                    showFilter(element);
                    break;
                case FILTER_CHAIN:
                    showFilterChain(element);
                    break;
                default:
                    break;
                }
            }
        });
        tGroupName.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
            }

            @Override
            public void focusGained(FocusEvent e) {
                viewer.getControl().setEnabled(false);
            }
        });
        cGroupProperty.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
            }

            @Override
            public void focusGained(FocusEvent e) {
                viewer.getControl().setEnabled(false);
            }
        });
        tGroupName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
            }
        });
        cGroupProperty.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
            }
        });
        bGroupOk.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                saveGroup();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        bFilterOk.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                saveFilter();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        bFilterCancel.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                showFilter(rootTree);
                viewer.getControl().setEnabled(true);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        bFilterChainOk.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                saveFilterChain();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        bFilterChainCancel.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                showFilterChain(rootTree);
                viewer.getControl().setEnabled(true);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        bGroupCancel.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                showGroupFilter(rootTree);
                viewer.getControl().setEnabled(true);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        final FocusListener listener = new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
            }

            @Override
            public void focusGained(FocusEvent e) {
                viewer.getControl().setEnabled(false);
            }
        };
        filterColorEditor.getButton().addFocusListener(listener);
        chainColorEditor.getButton().addFocusListener(listener);
        cFilterProperty.addFocusListener(listener);
        cFirst.addFocusListener(listener);
        cFirstText.addFocusListener(listener);
        cSecond.addFocusListener(listener);
        cSecondText.addFocusListener(listener);
        cSecRel.addFocusListener(listener);
        cRule.addFocusListener(listener);
        tFilterChainName.addFocusListener(listener);
        cGis.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateGisFilter();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }

    /**
     * fire changing filter event
     * 
     * @param filterNode - changed filters
     */
    protected void fireFilterChangeEvent(Node filterNode) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            Traverser tr = FilterUtil.getDataNodesOfFilter(filterNode);
            for (Node node : tr) {
                Node gis = NeoUtils.findGisNodeByChild(node);
                if (gis != null) {
                    refreshLayer(gis);
                }
            }
        } finally {
            tx.finish();
        }
    }

    /**
     * fire event: remove filter
     * 
     * @param dataNode - data Node
     * @param filterNode - changed filters
     */
    protected void fireChangeFilterForData(Node dataNode) {
        Node gis = NeoUtils.findGisNodeByChild(dataNode);
        if (gis != null) {
            refreshLayer(gis);
        }
    }

    /**
     *refresh layer if necessary
     * 
     * @param gis - gis node
     */
    private void refreshLayer(Node gis) {
        IMap activeMap = ApplicationGIS.getActiveMap();
        if (activeMap != ApplicationGIS.NO_MAP) {
            try {
                for (ILayer layer : activeMap.getMapLayers()) {
                    IGeoResource resourse = layer.findGeoResource(GeoNeo.class);
                    if (resourse != null) {
                        GeoNeo geo = resourse.resolve(GeoNeo.class, null);
                        if (gis != null && geo.getMainGisNode().equals(gis)) {
                            layer.refresh(null);
                        }
                    }
                }
            } catch (IOException e) {
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
    }

    /**
     *show filter chain part of view
     * 
     * @param element - tree node
     */
    protected void showFilterChain(TreeNeoNode element) {
        rootTree = element;
        Transaction tx = NeoUtils.beginTx(service);
        try {
            tFilterChainName.setText(NeoUtils.getSimpleNodeName(rootTree.getNode(), "")); //$NON-NLS-1$)
            cRule.setText((String)element.getNode().getProperty(FilterUtil.PROPERTY_ORDER, "")); //$NON-NLS-1$
            final TreeNeoNode parent = element.getParent(service);
            if (parent != null && parent.getType() == NodeTypes.FILTER_GROUP) {
                chainColorEditor.setColorValue(getColor(FilterUtil.PROPERTY_FILTER_COLOR, element.getNode(), DEFAULT_COLOR));
                chainColorEditor.getButton().setVisible(true);
                chainColorEditorLabel.setVisible(true);
            } else {
                chainColorEditor.getButton().setVisible(false);
                chainColorEditorLabel.setVisible(false);
            }
        } finally {
            NeoUtils.finishTx(tx);
        }
        filterChainFrame.setVisible(true);
    }

    /**
     *save filter chain
     */
    protected void saveFilterChain() {
        final String name = tFilterChainName.getText();
        final String rule = cRule.getText();
        final RGB color;
        if (rootTree.getParent(service).getType() == NodeTypes.FILTER_GROUP) {
            color = chainColorEditor.getColorValue();
        } else {
            color = null;
        }
        Job job = new Job(Messages.FilterView_15) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                Transaction tx = NeoUtils.beginTx(service);
                final Node node;
                try {

                    node = rootTree.getNode();
                    NeoUtils.setNodeName(node, name, service);
                    node.setProperty(FilterUtil.PROPERTY_ORDER, rule);
                    saveColor(node, FilterUtil.PROPERTY_FILTER_COLOR, color);
                    NeoUtils.successTx(tx);
                } finally {
                    NeoUtils.finishTx(tx);
                }
                fireFilterChangeEvent(node);
                rootTree.refresh(service);
                ActionUtil.getInstance().runTask(new Runnable() {

                    @Override
                    public void run() {

                        viewer.refresh(rootTree);
                        updateGisFilter();
                    }
                }, true);
                return Status.OK_STATUS;
            }

        };
        job.schedule();
        viewer.getControl().setEnabled(true);
    }

    /**
     *
     */
    protected void saveFilter() {
        final String property = cFilterProperty.getText();
        final String first = cFirst.getText();
        final String firsttxt = cFirstText.getText();
        final String secondrel = cSecRel.getText();
        final String second = cSecond.getText();
        final String secontxt = cSecondText.getText();
        final RGB color;
        if (rootTree.getParent(service).getType() == NodeTypes.FILTER_GROUP) {
            color = filterColorEditor.getColorValue();
        } else {
            color = null;
        }
        Job job = new Job(Messages.FilterView_14) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                Transaction tx = NeoUtils.beginTx(service);
                final Node node;
                try {

                    node = rootTree.getNode();
                    NeoUtils.setNodeName(node, property, service);
                    FilterUtil.setGroupProperty(node, property, service);
                    node.setProperty(FilterUtil.PROPERTY_FIRST, first);
                    node.setProperty(FilterUtil.PROPERTY_FIRST_TXT, firsttxt);
                    node.setProperty(FilterUtil.PROPERTY_SECOND_REL, secondrel);
                    node.setProperty(FilterUtil.PROPERTY_SECOND, second);
                    node.setProperty(FilterUtil.PROPERTY_SECOND_TXT, secontxt);
                    saveColor(node, FilterUtil.PROPERTY_FILTER_COLOR, color);
                    NeoUtils.successTx(tx);
                } finally {
                    NeoUtils.finishTx(tx);
                }
                fireFilterChangeEvent(node);
                rootTree.refresh(service);
                ActionUtil.getInstance().runTask(new Runnable() {

                    @Override
                    public void run() {
                        viewer.refresh(rootTree);
                        updateGisFilter();
                    }
                }, true);
                return Status.OK_STATUS;
            }

        };
        job.schedule();
        viewer.getControl().setEnabled(true);

    }

    /**
     *
     */
    protected void saveGroup() {
        final String name = tGroupName.getText();
        final String property = cGroupProperty.getText();
        Job job = new Job(Messages.FilterView_15) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                Transaction tx = NeoUtils.beginTx(service);
                final Node node;
                try {

                     node = rootTree.getNode();
                    NeoUtils.setNodeName(node, name, service);
                    FilterUtil.setGroupProperty(node, property, service);
                    NeoUtils.successTx(tx);
                } finally {
                    NeoUtils.finishTx(tx);
                }
                fireFilterChangeEvent(node);
                rootTree.refresh(service);
                ActionUtil.getInstance().runTask(new Runnable() {

                    @Override
                    public void run() {
                        viewer.refresh(rootTree);
                        updateGisFilter();
                    }
                }, true);
                return Status.OK_STATUS;
            }

        };
        job.schedule();
        viewer.getControl().setEnabled(true);
    }

    /**
     * @param element
     */
    protected void showFilter(TreeNeoNode element) {
        rootTree = element;
        Transaction tx = NeoUtils.beginTx(service);
        try {
            cFilterProperty.setText(FilterUtil.getGroupProperty(element.getNode(), "", service)); //$NON-NLS-1$
            cFirst.setText((String)element.getNode().getProperty(FilterUtil.PROPERTY_FIRST, "")); //$NON-NLS-1$
            cFirstText.setText((String)element.getNode().getProperty(FilterUtil.PROPERTY_FIRST_TXT, "")); //$NON-NLS-1$
            cSecRel.setText((String)element.getNode().getProperty(FilterUtil.PROPERTY_SECOND_REL, "")); //$NON-NLS-1$
            cSecond.setText((String)element.getNode().getProperty(FilterUtil.PROPERTY_SECOND, "")); //$NON-NLS-1$
            cSecondText.setText((String)element.getNode().getProperty(FilterUtil.PROPERTY_SECOND_TXT, "")); //$NON-NLS-1$
            final TreeNeoNode parent = element.getParent(service);
            if (parent != null && parent.getType() == NodeTypes.FILTER_GROUP) {
                filterColorEditor.setColorValue(getColor(FilterUtil.PROPERTY_FILTER_COLOR, element.getNode(), DEFAULT_COLOR));
                filterColorEditor.getButton().setVisible(true);
                filterColorEditorLabel.setVisible(true);
            } else {
                filterColorEditor.getButton().setVisible(false);
                filterColorEditorLabel.setVisible(false);
            }
        } finally {
            NeoUtils.finishTx(tx);
        }
        filterFrame.setVisible(true);
    }

    /**
     * @param element
     */
    protected void showGroupFilter(TreeNeoNode element) {
        rootTree = element;
        Transaction tx = NeoUtils.beginTx(service);
        try {
            tGroupName.setText(NeoUtils.getSimpleNodeName(rootTree.getNode(), "")); //$NON-NLS-1$
            cGroupProperty.setText(FilterUtil.getGroupProperty(rootTree.getNode(), "", service)); //$NON-NLS-1$
        } finally {
            NeoUtils.finishTx(tx);
        }
        groupFrame.setVisible(true);
    }

    /**
     * Save color in database
     * 
     * @param node node
     * @param property property name
     * @param rgb color
     */
    private void saveColor(Node node, String property, RGB rgb) {
        if (node == null || property == null) {
            return;
        }
        if (rgb == null) {
            node.removeProperty(property);
        } else {
            int[] array = new int[3];
            array[0] = rgb.red;
            array[1] = rgb.green;
            array[2] = rgb.blue;
            node.setProperty(property, array);
        }
    }

    /**
     * Gets color of right bar
     * 
     * @param aggrNode aggregation node
     * @param defaultColor
     * @return RGB
     */
    private RGB getColor(String property, Node aggrNode, RGB defaultColor) {
        if (aggrNode != null) {
            int[] colors = (int[])aggrNode.getProperty(property, null);
            if (colors != null) {
                return new RGB(colors[0], colors[1], colors[2]);
            }
        }
        return defaultColor;
    }

    /**
     * @return
     */
    private Node getFileRootNode() {
        Job job = new Job(Messages.FilterView_24) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                rootFlterNode = NeoUtils.findOrCreateFilterRootNode(service);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        try {
            job.join();
        } catch (InterruptedException e) {
            // TODO Handle InterruptedException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        return rootFlterNode;
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                FilterView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(addGroupAction);
        manager.add(new Separator());
        manager.add(addFilterAction);
        manager.add(new Separator());
        manager.add(deleteAction);
    }

    private void fillContextMenu(IMenuManager manager) {
        updateActions();
        if (addGroupAction.isEnabled()) {
            manager.add(addGroupAction);
        }
        if (addChainFilterAction.isEnabled()) {
            manager.add(addChainFilterAction);
        }
        if (addFilterAction.isEnabled()) {
            manager.add(addFilterAction);
        }
        manager.add(new Separator());
        if (assignFiltr.isEnabled()) {
            manager.add(assignFiltr);
        }
        manager.add(new Separator());
        if (deleteAction.isEnabled()) {
            manager.add(deleteAction);
        }
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    /**
     *
     */
    private void updateActions() {
        addGroupAction.setEnabled(addGroupAction.isAcionEnable());
        addChainFilterAction.setEnabled(addChainFilterAction.isAcionEnable());
        addFilterAction.setEnabled(addFilterAction.isAcionEnable());
        deleteAction.setEnabled(deleteAction.isAcionEnable());
        clearFiltr.setEnabled(clearFiltr.isAcionEnable());
        assignFiltr.setEnabled(assignFiltr.isAcionEnable());
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(addFilterAction);
        manager.add(addChainFilterAction);
        manager.add(new Separator());
        manager.add(addGroupAction);
        manager.add(new Separator());
        manager.add(assignFiltr);
        manager.add(clearFiltr);
        manager.add(new Separator());
        manager.add(deleteAction);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
    }

    private void makeActions() {
        addGroupAction = new AddGroupAction();
        addFilterAction = new AddFilterAction();
        deleteAction = new DeleteAction();
        clearFiltr = new ClearFilter();
        assignFiltr = new AssignFilter();
        addChainFilterAction = new AddChainFilterAction();
    }

    /**
     * @param node
     * @return
     */
    protected void createAndSelectNewGroup(final TreeNeoNode nodeTree) {
        Job job = new Job(Messages.FilterView_26) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                Transaction tx = NeoUtils.beginTx(service);
                final Node node;
                try {
                    node = service.createNode();
                    NodeTypes.FILTER_GROUP.setNodeType(node, service);
                    NeoUtils.setNodeName(node, Messages.FilterView_27, service);
                    NeoUtils.addChild(nodeTree.getNode(), node, null, service);
                    NeoUtils.successTx(tx);
                } finally {
                    NeoUtils.finishTx(tx);
                }
                ActionUtil.getInstance().runTask(new Runnable() {

                    @Override
                    public void run() {
                        viewer.refresh(nodeTree);
                        viewer.setSelection(new StructuredSelection(new Object[] {new TreeNeoNode(node)}), true);
                    }
                }, true);
                return Status.OK_STATUS;
            }

        };
        job.schedule();
    }

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
            }
        });
    }

    private void showMessage(String message) {
        MessageDialog.openInformation(viewer.getControl().getShell(), Messages.FilterView_28, message);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    /**
     * <p>
     * Add filter action
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    public class AddFilterAction extends Action {
        private TreeNeoNode node;

        public AddFilterAction() {
            setText(Messages.FilterView_29);
            setToolTipText(Messages.FilterView_30);
            setImageDescriptor(ImageDescriptor.createFromImage(IconManager.getIconManager().getImage("add_filter")));
        }

        @Override
        public void run() {
            if (isAcionEnable()) {
                createAndSelectNewFilter(node);
            } else {
                this.setEnabled(false);
            }
        }

        public boolean isAcionEnable() {
            IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
            if (selection.size() != 1) {
                return false;
            }
            Object element = (selection).getFirstElement();
            if (!(element instanceof TreeNeoNode)) {
                return false;
            }
            node = (TreeNeoNode)element;
            return node.getType() != NodeTypes.FILTER;
        }

    }

    /**
     * <p>
     * Delete filter action
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    public class DeleteAction extends Action {
        /**
         * 
         */
        public DeleteAction() {
            setText(Messages.FilterView_31);
            setToolTipText(Messages.FilterView_32);
            setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));

        }

        private TreeNeoNode node;

        @Override
        public void run() {
            if (isAcionEnable()) {
                deleteNode(node);
            } else {
                this.setEnabled(false);
            }
        }

        public boolean isAcionEnable() {
            IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
            if (selection.size() != 1) {
                return false;
            }
            Object element = (selection).getFirstElement();
            if (!(element instanceof TreeNeoNode)) {
                return false;
            }
            node = (TreeNeoNode)element;
            return node.getType() != NodeTypes.FILTER_ROOT;
        }

    }

    /**
     * <p>
     * Assign filter action
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    public class AssignFilter extends Action {

        public AssignFilter() {
            setText(Messages.FilterView_33);
            setToolTipText(Messages.FilterView_34);
            setImageDescriptor(ImageDescriptor.createFromImage(IconManager.getIconManager().getImage("assign_filter")));

        }

        private Node dataNode;
        private Node filterNode;

        @Override
        public void run() {
            if (isAcionEnable()) {
                setFilter(dataNode, filterNode);
            } else {
                this.setEnabled(false);
            }
        }

        public boolean isAcionEnable() {
            dataNode = dataMap.get(cGis.getText());
            if (dataNode == null) {
                return false;
            }
            IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
            if (selection.size() != 1) {
                return false;
            }
            Object element = (selection).getFirstElement();
            if (!(element instanceof TreeNeoNode)) {
                return false;
            }
            TreeNeoNode node = (TreeNeoNode)element;
            filterNode = node.getNode();
            return node.getType() != NodeTypes.FILTER_ROOT;
        }

    }

    /**
     * <p>
     * Clear filter action
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    public class ClearFilter extends Action {

        public ClearFilter() {
            setText(Messages.FilterView_35);
            setToolTipText(Messages.FilterView_36);
            setImageDescriptor(ImageDescriptor.createFromImage(IconManager.getIconManager().getImage("clear_filter")));

        }

        private Node dataNode;

        @Override
        public void run() {
            if (isAcionEnable()) {
                clearFilter(dataNode);
            } else {
                this.setEnabled(false);
            }
        }

        public boolean isAcionEnable() {
            dataNode = dataMap.get(cGis.getText());
            if (dataNode == null) {
                return false;
            }
            Transaction tx = NeoUtils.beginTx(service);
            try {
                return dataNode.hasRelationship(GeoNeoRelationshipTypes.USE_FILTER, Direction.OUTGOING);
            } finally {
                tx.finish();
            }
        }

    }

    /**
     * <p>
     * Add group action
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    public class AddGroupAction extends Action {
        /**
         * 
         */
        public AddGroupAction() {
            setText(Messages.FilterView_37);
            setToolTipText(Messages.FilterView_38);
            setImageDescriptor(ImageDescriptor.createFromImage(IconManager.getIconManager().getImage("add_filter_group")));

        }

        private TreeNeoNode node;

        @Override
        public void run() {
            if (isAcionEnable()) {
                createAndSelectNewGroup(node);
            } else {
                this.setEnabled(false);
            }
        }

        public boolean isAcionEnable() {
            IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
            if (selection.size() != 1) {
                return false;
            }
            Object element = (selection).getFirstElement();
            if (!(element instanceof TreeNeoNode)) {
                return false;
            }
            node = (TreeNeoNode)element;
            return node.getType() == NodeTypes.FILTER_ROOT;
        }

    }

    /**
     * <p>
     * Add chain filter action
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    public class AddChainFilterAction extends Action {

        public AddChainFilterAction() {
            setText(Messages.FilterView_0);
            setToolTipText(Messages.FilterView_11);
            setImageDescriptor(ImageDescriptor.createFromImage(IconManager.getIconManager().getImage("add_filter_chain")));
        }

        private TreeNeoNode node;

        @Override
        public void run() {
            if (isAcionEnable()) {
                createAndSelectNewFilterChain(node);

            } else {
                this.setEnabled(false);
            }
        }

        public boolean isAcionEnable() {
            IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
            if (selection.size() != 1) {
                return false;
            }
            Object element = (selection).getFirstElement();
            if (!(element instanceof TreeNeoNode)) {
                return false;
            }
            node = (TreeNeoNode)element;
            return node.getType() != NodeTypes.FILTER;

        }

    }

    /**
     * @param node
     */
    public void createAndSelectNewFilter(final TreeNeoNode nodeTree) {
        Job job = new Job(Messages.FilterView_39) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                Transaction tx = NeoUtils.beginTx(service);
                final Node node;
                try {
                    node = service.createNode();
                    NodeTypes.FILTER.setNodeType(node, service);
                    NeoUtils.setNodeName(node, FilterUtil.getGroupProperty(nodeTree.getNode(), "", service), service); //$NON-NLS-1$
                    node.setProperty(FilterUtil.PROPERTY_FILTERED_NAME, FilterUtil.getGroupProperty(nodeTree.getNode(), "", service)); //$NON-NLS-1$
                    NeoUtils.addChild(nodeTree.getNode(), node, null, service);
                    NeoUtils.successTx(tx);
                } finally {
                    NeoUtils.finishTx(tx);
                }
                ActionUtil.getInstance().runTask(new Runnable() {

                    @Override
                    public void run() {
                        viewer.refresh(nodeTree);
                        viewer.setSelection(new StructuredSelection(new Object[] {new TreeNeoNode(node)}), true);
                    }
                }, true);
                return Status.OK_STATUS;
            }

        };
        job.schedule();
    }

    /**
     * Create new FilterChain
     * 
     * @param node - parent node
     */
    public void createAndSelectNewFilterChain(final TreeNeoNode nodeTree) {
        Job job = new Job(Messages.FilterView_26) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                Transaction tx = NeoUtils.beginTx(service);
                final Node node;
                try {
                    node = service.createNode();
                    NodeTypes.FILTER_CHAIN.setNodeType(node, service);
                    NeoUtils.setNodeName(node, Messages.FilterView_16, service);
                    NeoUtils.addChild(nodeTree.getNode(), node, null, service);
                    NeoUtils.successTx(tx);
                } finally {
                    NeoUtils.finishTx(tx);
                }
                ActionUtil.getInstance().runTask(new Runnable() {

                    @Override
                    public void run() {
                        viewer.refresh(nodeTree);
                        viewer.setSelection(new StructuredSelection(new Object[] {new TreeNeoNode(node)}), true);
                    }
                }, true);
                return Status.OK_STATUS;
            }

        };
        job.schedule();
    }

    /**
     * assign filter to data
     * 
     * @param dataNode - data node
     * @param filterNode -filter
     */
    public void setFilter(final Node dataNode, final Node filterNode) {
        Job job = new Job(Messages.FilterView_42) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                Transaction tx = NeoUtils.beginTx(service);
                try {
                    for (Relationship relation : dataNode.getRelationships(GeoNeoRelationshipTypes.USE_FILTER, Direction.OUTGOING)) {
                        relation.delete();
                    }
                    dataNode.createRelationshipTo(filterNode, GeoNeoRelationshipTypes.USE_FILTER);
                    tx.success();
                } finally {
                    tx.finish();
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        try {
            job.join();
            fireChangeFilterForData(dataNode);
        } catch (InterruptedException e) {
            // TODO Handle InterruptedException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        updateGisFilter();
    }

    /**
     * remove assign data to filter
     * 
     * @param dataNode - data node
     */
    public void clearFilter(final Node dataNode) {
        Job job = new Job(Messages.FilterView_42) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                Transaction tx = NeoUtils.beginTx(service);
                
                try {
                    for (Relationship relation : dataNode.getRelationships(GeoNeoRelationshipTypes.USE_FILTER, Direction.OUTGOING)) {
                        relation.delete();
                    }
                    tx.success();
                } finally {
                    tx.finish();
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        try {
            job.join();

            
        } catch (InterruptedException e) {
            // TODO Handle InterruptedException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        updateGisFilter();
        fireChangeFilterForData(dataNode);
    }

    /**
     *update data
     */
    private void updateGisFilter() {
        Node data = dataMap.get(cGis.getText());
        String result;
        if (data == null) {
            result = ""; //$NON-NLS-1$
        } else {
            Transaction tx = NeoUtils.beginTx(service);
            try {
                Relationship relation = data.getSingleRelationship(GeoNeoRelationshipTypes.USE_FILTER, Direction.OUTGOING);
                if (relation == null) {
                    result = ""; //$NON-NLS-1$
                } else {
                    result = AbstractFilter.getInstance(relation.getOtherNode(data), service).toString();
                }
            } finally {
                tx.finish();
            }
        }
        updatePropertyHelp(data);
        updateActions();
        tGisFilter.setText(result);
    }

    /**
     * Updates Property list
     * 
     * @param data
     */
    private void updatePropertyHelp(Node data) {

        String[] fields;
        if (data != null) {
            fields = new PropertyHeader(data).getAllFields();
        } else {
            fields = new String[0];
        }
        if (fields == null) {
            fields = new String[0];
        }
        String txt = cGroupProperty.getText();
        cGroupProperty.setItems(fields);
        cGroupProperty.setText(txt);
        txt = cFilterProperty.getText();
        cFilterProperty.setItems(fields);
        cFilterProperty.setText(txt);
    }

    /**
     * delete filter
     * 
     * @param node - filter to delete
     */
    public void deleteNode(final TreeNeoNode node) {
        viewer.remove(node);
        viewer.getControl().setEnabled(false);
        final TreeNeoNode parent = node.getParent(service);
        Job job = new Job(Messages.FilterView_46) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    Transaction tx = NeoUtils.beginTx(service);
                    try {
                        NeoCorePlugin.getDefault().getProjectService().dirtyRemoveNodeFromStructure(node.getNode());
                        // TODO use manager
                        // NodeDeletingManager deleteManager = new NodeDeletingManager(service);
                        // deleteManager.deleteNode(node.getNode());
                        // } catch (IllegalAccessException e) {
                        // throw (RuntimeException)new RuntimeException().initCause(e);
                        NeoUtils.successTx(tx);
                    } finally {
                        NeoUtils.finishTx(tx);
                    }
                } finally {
                    ActionUtil.getInstance().runTask(new Runnable() {

                        @Override
                        public void run() {
                            // viewer.setInput(service.getReferenceNode());
                            viewer.refresh(true);
                            if (parent != null) {
                                viewer.setSelection(new StructuredSelection(new Object[] {parent}), true);
                            }
                            viewer.getControl().setEnabled(true);
                        }
                    }, true);
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();

    }
}
