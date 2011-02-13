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

package org.amanzi.awe.views.neighbours.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.amanzi.awe.catalog.neo.NeoCatalogPlugin;
import org.amanzi.awe.catalog.neo.upd_layers.events.ChangeModelEvent;
import org.amanzi.awe.catalog.neo.upd_layers.events.ChangeSelectionEvent;
import org.amanzi.awe.catalog.neo.upd_layers.events.UpdateLayerEventTypes;
import org.amanzi.awe.ui.AweUiPlugin;
import org.amanzi.awe.ui.IGraphModel;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.TransactionWrapper;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.node2node.INode2NodeFilter;
import org.amanzi.neo.services.node2node.INodeToNodeType;
import org.amanzi.neo.services.node2node.Node2NodeSelectionInformation;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationService;
import org.amanzi.neo.services.statistic.ISelectionInformation;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.statistic.StatisticManager;
import org.amanzi.neo.services.ui.IconManager;
import org.amanzi.neo.services.utils.RunnableWithResult;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 * View for node2node network structures
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class Node2NodeViews extends ViewPart {
    private static final String SHOW_NEIGHBOUR = "show relation '%s' > '%s' on map";
    private static final String SHOW_SERVE = "show all '%s' relations on map";
    
    public static final String ID="org.amanzi.awe.views.neighbours.views.Node2NodeViews";
    private static final int PAGE_SIZE = 64;
    private IStatistic statistic;
    private Table table;
    private NodeToNodeRelationModel n2nModel;
    private TransactionWrapper tx;
    private ArrayList<TableColumn> columns = new ArrayList<TableColumn>();
    private int colColut = 0;
    private Combo n2nSelection;
    private Button commit;
    private Button rollback;
    
    private Button search;
    private Button returnFullList;
    private Text textToSearch;
    private String searchingSector = "";
    /** String SEARCH field */
    private static final String SEARCH = "Search";
    
    protected DatasetService ds;
    protected NodeToNodeRelationService n2ns;
    private NetworkService networks;
    private boolean isDisposed;
    private Map<String, NodeToNodeRelationModel> modelMap = new HashMap<String, NodeToNodeRelationModel>();
    private ISelectionInformation information;
    private ArrayList<String> propertys;
    private INode2NodeFilter filter;
    private CountedIteratorWr createdIter;
    protected boolean drawLines;
    private IGraphModel model=null;
    private String selectedServ=null;
    private Font fontNormal;
    private Font fontSelected;
    private TableViewer view;
    protected int column=-1;
    private Wrapper data;
    @Override
    public void createPartControl(Composite parent) {
        Composite main = new Composite(parent, SWT.FILL);
        Layout mainLayout = new GridLayout(5, false);
        main.setLayout(mainLayout);
        Label label = new Label(main, SWT.LEFT);
        label.setText(getListTxt());

        n2nSelection = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        n2nSelection.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                n2nSelectionChange();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        GridData layoutData = new GridData();
        layoutData.widthHint = 150;
        n2nSelection.setLayoutData(layoutData);
        Button drawArrow = new Button(main,SWT.CHECK);
        drawArrow.setText("Draw lines");
        drawArrow.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                drawLines=((Button)e.getSource()).getSelection();
                updateCurrentModel();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        drawLines=true;
        drawArrow.setSelection(drawLines);
        commit = new Button(main, SWT.BORDER | SWT.PUSH);
        commit.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                commit();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        commit.setToolTipText("Commit");
        commit.setImage(IconManager.getIconManager().getCommitImage());
        commit.setEnabled(false);
        rollback = new Button(main, SWT.BORDER | SWT.PUSH);
        rollback.setImage(IconManager.getIconManager().getRollbackImage());
        rollback.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                rollback();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        rollback.setToolTipText("Rollback");
        rollback.setEnabled(false);
        
        //Kasnitskij_V:

        Label label2 = new Label(main, SWT.FLAT);
        label2.setText("Write here what do you want to search:");
        textToSearch = new Text(main, SWT.SINGLE | SWT.BORDER);
//        textToSearch.setSize(200, 20);
        //textToSearch.setLayoutData(layoutData);
        
        search = new Button(main, SWT.PUSH);
        search.setText(SEARCH);
        search.addMouseListener(new MouseListener() {
            
            @Override
            public void mouseUp(MouseEvent e) {
            }
            
            @Override
            public void mouseDown(MouseEvent e) {
                try {
                    searchingSector = textToSearch.getText();
                }
                catch (NullPointerException ex) {
                    
                }
                formCollumns();
            }
            
            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });
        //search.setLayoutData(layoutData);
        
        returnFullList = new Button(main, SWT.PUSH);
//        returnFullList.setSize(200, 20);
        layoutData=new GridData();
        layoutData.horizontalSpan=2;
        returnFullList.setLayoutData(layoutData);
        returnFullList.setText("Return full list");
        returnFullList.addMouseListener(new MouseListener() {
            
            @Override
            public void mouseUp(MouseEvent e) {
            }
            
            @Override
            public void mouseDown(MouseEvent e) {
                searchingSector = "";
                formCollumns();
            }
            
            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });
        returnFullList.setLayoutData(layoutData);
        
        table = new Table(main, SWT.VIRTUAL | SWT.BORDER);
        view=new TableViewer(table);
        view.setContentProvider(new VirtualContentProvider());
        view.setLabelProvider(new VirtualLabelProvider());
//        table.addListener(SWT.SetData, new Listener() {
//
//            @Override
//            public void handleEvent(Event event) {
//                setData(event);
//            }
//        });
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.addListener(SWT.EraseItem, new Listener() {
            public void handleEvent(Event event) {
                if((event.detail & SWT.SELECTED) != 0 ){
                    event.detail &= ~SWT.SELECTED;
                }
            }
        });
        view.setItemCount(0);
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1);
        view.getControl().setLayoutData(layoutData);
        setFilter(networks.getAllNode2NodeFilter(AweUiPlugin.getDefault().getUiService().getActiveProjectNode()));
        final TableEditor editor = new TableEditor(table);
        // The editor must have the same size as the cell and must
        // not be any smaller than 50 pixels.
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        editor.minimumWidth = 50;
        // editing the second column
        table.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(Event event) {
                Rectangle clientArea = table.getClientArea();
                Point pt = new Point(event.x, event.y);
                int index = table.getTopIndex();
                while (index < table.getItemCount()) {
                    boolean visible = false;
                    final TableItem item = table.getItem(index);
                    for (int i = 0; i < table.getColumnCount(); i++) {
                        Rectangle rect = item.getBounds(i);
                        if (rect.contains(pt)) {
                            column = i;
                            data=(Wrapper)item.getData();
                            if (i<2){
                                return;
                            }
                            final Text text = new Text(table, SWT.NONE);
                            Listener textListener = new Listener() {
                                public void handleEvent(final Event e) {
                                    switch (e.type) {
                                    case SWT.FocusOut:
                                        setData(item, column, text);
                                        text.dispose();
                                        break;
                                    case SWT.Traverse:
                                        switch (e.detail) {
                                        case SWT.TRAVERSE_RETURN:
                                            setData(item, column, text);
                                            // FALL THROUGH
                                        case SWT.TRAVERSE_ESCAPE:
                                            text.dispose();
                                            e.doit = false;
                                        }
                                        break;
                                    }
                                }
                            };
                            text.addListener(SWT.FocusOut, textListener);
                            text.addListener(SWT.Traverse, textListener);
                            editor.setEditor(text, item, i);
                            text.setText(item.getText(i));
                            text.selectAll();
                            text.setFocus();
                            return;
                        }
                        if (!visible && rect.intersects(clientArea)) {
                            visible = true;
                        }
                    }
                    if (!visible)
                        return;
                    index++;
                }
            }
        });

        table.addListener(SWT.MouseDoubleClick, new Listener() {


            public void handleEvent(Event event) {
                Rectangle clientArea = table.getClientArea();
                Point pt = new Point(event.x, event.y);
                int index = table.getTopIndex();
                while (index < table.getItemCount()) {
                    boolean visible = false;
                    final TableItem item = table.getItem(index);
                    for (int i = 0; i < 2; i++) {
                        Rectangle rect = item.getBounds(i);
                        if (rect.contains(pt)) {
                             column = i;

                             data = (Wrapper)item.getData();
                            createAndFireModel((Relationship)data.cont,i);
                            if (column<2){
                                selectedServ=data.getText(column);
                                view.refresh();
                            }
                            return;
                        }
                        if (!visible && rect.intersects(clientArea)) {
                            visible = true;
                        }
                    }
                    if (!visible)
                        return;
                    index++;
                }
            }
        });
        fontNormal = table.getFont();
        FontData[] fd = fontNormal.getFontData();
        fd[0].setStyle(SWT.BOLD);
        // TODO dispose font resources in plugin stop()?
        fontSelected = new Font(fontNormal.getDevice(), fd);
        hookContextMenu();
    }
    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(table);
        table.setMenu(menu);
    }
    /**
     *
     * @param manager
     */
    protected void fillContextMenu(IMenuManager manager) {
        if (data==null||n2nModel==null){
            return;
        }
        if (column==0){
            fillServMenu(manager,data);
        }else if (column==1){
            fillNeighMenu(manager,data);
        }
    }
    /**
     *
     * @param manager
     * @param data2
     */
    private void fillNeighMenu(IMenuManager manager, final Wrapper data) {
        manager.add(new Action(String.format(SHOW_NEIGHBOUR, data.getText(0), data.getText(1))) {
            @Override
            public void run() {
                model=new N2NGraphModel((Relationship)data.cont, false, drawLines);
                fireModel(model);
            }
        });
        manager.add(new Action(String.format("Zoom to %s (x8)", data.getText(1))) {
            @Override
            public void run() {
                if (n2nModel!=null){
                    Node networkRoot=n2nModel.getNetworkNode();
                    Node gisNode = ds.findGisNode(networkRoot);
                    Collection<Node> sites=new ArrayList<Node>();
                    Node sector=n2ns.findNodeFromProxy(((Relationship)data.cont).getEndNode());
                    Node site=sector.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(sector);
                    sites.add(site);
                    ChangeSelectionEvent event = new ChangeSelectionEvent(UpdateLayerEventTypes.ZOOM,gisNode,sites);
                    NeoCatalogPlugin.getDefault().getLayerManager().sendUpdateMessage(event);            
                }    
            }
        });
    }
    /**
     *
     * @param manager
     * @param data2
     */
    private void fillServMenu(IMenuManager manager,final  Wrapper data) {
        manager.add(new Action(String.format(SHOW_SERVE, data.getText(0))) {
            @Override
            public void run() {
                model=new N2NGraphModel((Relationship)data.cont, false, drawLines);
                fireModel(model);
            }
        });
        manager.add(new Action(String.format("Zoom to %s (x8)", data.getText(0))) {
            @Override
            public void run() {
                if (n2nModel!=null){
                    Node networkRoot=n2nModel.getNetworkNode();
                    Node gisNode = ds.findGisNode(networkRoot);
                    Collection<Node> sites=new ArrayList<Node>();
                    Node sector=n2ns.findNodeFromProxy(((Relationship)data.cont).getStartNode());
                    Node site=sector.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(sector);
                    sites.add(site);
                    ChangeSelectionEvent event = new ChangeSelectionEvent(UpdateLayerEventTypes.ZOOM,gisNode,sites);
                    NeoCatalogPlugin.getDefault().getLayerManager().sendUpdateMessage(event);            
                }    
            }
        });
    }
    /**
     *
     */
    protected void updateCurrentModel() {
        if (model instanceof N2NGraphModel){
            ((N2NGraphModel)model).setDrawLines(drawLines);
            fireModel(model);
        }
    }

    /**
     *
     * @param data
     * @param i
     */
    protected void createAndFireModel(Relationship data, int i) {
        IGraphModel model;
        if (data==null||i<0||i>2||n2nModel==null){
            model=null;
        }else{
            INodeToNodeType type = n2nModel.getType();
            model=new N2NGraphModel(data, i==0, drawLines);
        }
        fireModel(model);
    }

    /**
     * @param event
     */
    protected void setData(Event event) {
        TableItem item = (TableItem)event.item;
        int index = event.index;
        int start = index / PAGE_SIZE * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, table.getItemCount());
        
        int k = 0;
        for (int i = start; i < end; i++) {
            PropertyContainer cont = getElement(i);
            String servingNodeName = ds.getNodeName(((Relationship)cont).getStartNode());

            item = table.getItem(i);
            // Kasnitskij_V:
            // search need sector
            if (!searchingSector.equals("")) {
                if (servingNodeName.equals(searchingSector)) {
                    item = table.getItem(k++);
                }
            }
            
            item.setData(cont);

            item.setText(0, servingNodeName);
            if (servingNodeName.equals(selectedServ)){
                item.setFont(0,fontSelected);
            }else{
                item.setFont(0,fontNormal);  
            }
            item.setText(1, ds.getNodeName(((Relationship)cont).getEndNode()));
            for (int j = 2; j < colColut; j++) {
                item.setText(j, String.valueOf(cont.getProperty(propertys.get(j - 2), "")));
            }
        }
        if (k != 0) {
            view.setItemCount(k);
        }
        else {
            if (!searchingSector.equals("")) {
                textToSearch.setText("not found");
            }
            else {
                textToSearch.setText("");
            }
        }
    }

    private PropertyContainer getElement(final int i) {
        Callable<PropertyContainer> cl = new Callable<PropertyContainer>() {

            @Override
            public PropertyContainer call() {
                if (createdIter.getIndex() - 1 > i) {
                    createdIter = new CountedIteratorWr(n2ns.getRelationTraverserByServNode(filter.getFilteredServNodes(n2nModel)).iterator());
                }
                PropertyContainer res = null;
                while (createdIter.hasNext()) {
                    res = createdIter.next();
                    if (createdIter.getIndex() - 1 == i) {
                        return res;
                    }
                }
                return null;
            }

        };
        try {
            return tx.submit(cl).get();
        } catch (InterruptedException e) {
            // TODO Handle InterruptedException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (ExecutionException e) {
            // TODO Handle ExecutionException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * @param allNode2NodeFilter
     */
    public void setFilter(final INode2NodeFilter allNode2NodeFilter) {
        if (!isDisposed) {
            if (tx.isChanged()) {
                int askUserToCommit = askUserToCommit();
                if (askUserToCommit == SWT.YES) {
                    tx.commit();
                } else if (askUserToCommit == SWT.NO) {
                    tx.rollback();
                } else {
                    // not changed anything
                    return;
                }
            }
            this.filter = allNode2NodeFilter;
            setSelectionModel(allNode2NodeFilter);
            view.setItemCount(0);
            table.clearAll();
            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {
                    updateNewFilters();
                }
            });
        }
    }

    /**
     * @return
     */
    private int askUserToCommit() {
        return org.amanzi.neo.services.ui.utils.ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult<Integer>() {

            private Integer value;

            @Override
            public void run() {
                MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_QUESTION);
                messageBox.setMessage("Some data do not commited. Commit data?");
                value = messageBox.open();
            }

            @Override
            public Integer getValue() {
                return value;
            }
        });
    }

    /**
 *
 */
    protected void updateNewFilters() {
        List<String> items = new ArrayList<String>();
        items.addAll(modelMap.keySet());
        Collections.sort(items);
        n2nSelection.setItems(items.toArray(new String[0]));
        table.setVisible(false);
    }

    /**
     * /**
     * 
     * @param allNode2NodeFilter
     */
    private void setSelectionModel(INode2NodeFilter allNode2NodeFilter) {
        modelMap.clear();
        for (NodeToNodeRelationModel model : allNode2NodeFilter.getModels()) {
            modelMap.put(model.getDescription(), model);
        }
    }

    @Override
    public void dispose() {
        fireModel(null);
        isDisposed = true;
        tx.stop(false);
        super.dispose();
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        ds = NeoServiceFactory.getInstance().getDatasetService();
        n2ns = NeoServiceFactory.getInstance().getNodeToNodeRelationService();
        networks = NeoServiceFactory.getInstance().getNetworkService();
        n2nModel = null;
        tx = new TransactionWrapper();
    }

    /**
     *
     */
    protected void rollback() {
        tx.rollback();
        table.clearAll();
        transactionChange(false);
    }

    /**
     *
     */
    protected void commit() {
        tx.commit();
        transactionChange(false);
    }

    /**
     *
     */
    protected void n2nSelectionChange() {
        NodeToNodeRelationModel model = modelMap.get(n2nSelection.getText());
        if (ObjectUtils.equals(model, n2nModel)) {
            return;
        }
        fireModel(null);
        n2nModel = model;
        formCollumns();
    }
    protected void fireModel(IGraphModel model){
        this.model=model;
        if (n2nModel!=null){
            Node networkRoot=n2nModel.getNetworkNode();
            Node gisNode = ds.findGisNode(networkRoot);
            ChangeModelEvent event = new ChangeModelEvent(gisNode,model);
            NeoCatalogPlugin.getDefault().getLayerManager().sendUpdateMessage(event);            
        }
    }
    private void formCollumns() {
        int countRelation = 0;
        table.clearAll();
        if (n2nModel == null) {
            colColut = 0;
            statistic = null;
        } else {
            Node networkNode = n2nModel.getNetworkNode();
            statistic = StatisticManager.getStatistic(networkNode);
            String key = n2nModel.getName();
            String nodeTypeId = NodeTypes.NODE_NODE_RELATIONS.getId();
            information = new Node2NodeSelectionInformation(networkNode, statistic, n2nModel, nodeTypeId, n2nModel.getDescription());
            Set<String> propertyNames = information.getPropertySet();
            propertys = new ArrayList<String>();
            propertys.addAll(propertyNames);
            colColut = propertyNames.size() + 2;
            while (columns.size() < colColut) {
                TableColumn col = new TableColumn(table, SWT.NONE);
                columns.add(col);
            }
            columns.get(0).setText("Serv node name");
            columns.get(1).setText("Neigh node name");
            for (int i = 2; i < colColut; i++) {
                String propertyName = propertys.get(i - 2);
                TableColumn tableColumn = columns.get(i);
                tableColumn.setText(propertyName);
                tableColumn.setToolTipText("Type " + information.getPropertyInformation(propertyName).getStatistic().getType().getName());

            }
//            for (Node n:filter.getFilteredServNodes(n2nModel)){
//                System.out.println(ds.getNodeName(n));
//            }
//            System.out.println("____");
            
            for (Relationship rel : n2ns.getRelationTraverserByServNode(filter.getFilteredServNodes(n2nModel))) {
                countRelation++;
            }
            System.out.println(countRelation);
            createdIter = createCountedIter();

        }
        resizecolumns();
        view.setItemCount(countRelation);
        table.setVisible(countRelation > 0);
    }

    /**
     * @return
     */
    protected CountedIteratorWr createCountedIter() {

        try {
            return tx.submit(new Callable<CountedIteratorWr>() {

                @Override
                public CountedIteratorWr call() {
                    return new CountedIteratorWr(n2ns.getRelationTraverserByServNode(filter.getFilteredServNodes(n2nModel)).iterator());
                }

            }).get();
        } catch (InterruptedException e) {
            // TODO Handle InterruptedException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (ExecutionException e) {
            // TODO Handle ExecutionException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     *
     */
    private void resizecolumns() {
        int ind = -1;
        for (TableColumn col : columns) {
            ind++;
            if (ind < colColut) {
                if (col.getWidth() == 0) {
                    col.setWidth(150);
                }
            } else {
                col.setWidth(0);
                col.setToolTipText(null);
            }
        }
    }

    protected String getListTxt() {
        return "Lists:";
    }

    @Override
    public void setFocus() {
        n2nSelection.setFocus();
    }

    /**
     * @param item
     * @param column
     * @param text
     */
    protected void setData(final TableItem item, final int column, final Text text) {
        if(StringUtils.equals(item.getText(column),text.getText())){
            return;
        }
        final String propertyName = propertys.get(column - 2);
        final PropertyContainer cont = (PropertyContainer)((Wrapper)item.getData()).cont;
        String key = n2nModel.getName();
        String nodeTypeId = NodeTypes.NODE_NODE_RELATIONS.getId();
        final Object newValue = statistic.parseValue(key, nodeTypeId, propertyName, text.getText());
        if (statistic.updateValue(key, nodeTypeId, propertyName, newValue, cont.getProperty(propertyName, null))) {
            Runnable task=new Runnable() {
                @Override
                public void run() {
                    if (newValue == null) {
                        cont.removeProperty(propertyName);
                    } else {
                        cont.setProperty(propertyName, newValue);
                    }
                    statistic.save();
                }
            };
            tx.submit(task);
            transactionChange(true);
            item.setText(column, text.getText());
        }
    }


    private void transactionChange(boolean isChange) {
        tx.setChanged(isChange);
        commit.setEnabled(isChange);
        rollback.setEnabled(isChange);
    }

    private static class CountedIteratorWr implements Iterator<PropertyContainer> {
        private final Iterator< ? extends PropertyContainer> baseIterator;
        private int index;

        CountedIteratorWr(Iterator< ? extends PropertyContainer> baseIterator) {
            this.baseIterator = baseIterator;
            index = 0;

        }

        @Override
        public boolean hasNext() {
            return baseIterator.hasNext();
        }

        @Override
        public PropertyContainer next() {
            index++;
            return baseIterator.next();
        }

        @Override
        public void remove() {
            baseIterator.remove();
        }

        public int getIndex() {
            return index;
        }

    }
    public class VirtualContentProvider implements ILazyContentProvider{

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        @Override
        public void updateElement(int index) {
                int start = index / PAGE_SIZE * PAGE_SIZE;
                int end = Math.min(start + PAGE_SIZE, table.getItemCount());
                
                int k = 0;
                int ind;
                for (int i = start; i < end; i++) {
                    PropertyContainer cont = getElement(i);
                    String servingNodeName = ds.getNodeName(((Relationship)cont).getStartNode());

                    ind=i;
                    // Kasnitskij_V:
                    // search need sector
                    if (!searchingSector.equals("")) {
                        if (servingNodeName.equals(searchingSector)) {
                            ind=k++;
                        }
                    }
                    Wrapper wr = new Wrapper(cont,ind);
                    wr.addProperty(servingNodeName);
                    wr.addProperty( ds.getNodeName(((Relationship)cont).getEndNode()));
                    for (int j = 2; j < colColut; j++) {
                        wr.addProperty( String.valueOf(cont.getProperty(propertys.get(j - 2), "")));
                    }
                    view.replace(wr, ind);
                }
                if (k != 0) {
                    view.setItemCount(k);
                }
                else {
                    if (!searchingSector.equals("")) {
                        textToSearch.setText("not found");
                    }
                    else {
                        textToSearch.setText("");
                    }
                }
        }
        
    }
    public class VirtualLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider{

        @Override
        public Font getFont(Object element, int columnIndex) {
            Wrapper wr = (Wrapper)element;
            return columnIndex<2?wr.getText(columnIndex).equals(selectedServ)?fontSelected:fontNormal:fontNormal;
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            Wrapper wr = (Wrapper)element;
            return wr.getText(columnIndex);
        }


        
    }
    private static class Wrapper{

        private final PropertyContainer cont;
        private final int index;
        private final ArrayList<String>values=new ArrayList<String>();

        /**
         * @param cont
         * @param index
         */
        public Wrapper(PropertyContainer cont, int index) {
            this.cont = cont;
            this.index = index;
        }
        /**
         *
         * @param columnIndex
         * @return
         */
        public String getText(int columnIndex) {
            return values.get(columnIndex);
        }
        public void addProperty(String val){
            values.add( val);
        }
    }
}
