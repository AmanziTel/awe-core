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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.amanzi.awe.ui.AweUiPlugin;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.TransactionWrapper;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.node2node.INode2NodeFilter;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
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

    @Override
    public void createPartControl(Composite parent) {
        Composite main = new Composite(parent, SWT.FILL);
        Layout mainLayout = new GridLayout(4, false);
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
        textToSearch.setSize(200, 20);
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
        returnFullList.setSize(200, 20);
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
        table.addListener(SWT.SetData, new Listener() {

            @Override
            public void handleEvent(Event event) {
                setData(event);
            }
        });
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setItemCount(0);
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
        table.setLayoutData(layoutData);
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
                    for (int i = 2; i < table.getColumnCount(); i++) {
                        Rectangle rect = item.getBounds(i);
                        if (rect.contains(pt)) {
                            final int column = i;
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
            item.setText(1, ds.getNodeName(((Relationship)cont).getEndNode()));
            for (int j = 2; j < colColut; j++) {
                item.setText(j, String.valueOf(cont.getProperty(propertys.get(j - 2), "")));
            }
        }
        if (k != 0) {
            table.setItemCount(k);
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
            table.setItemCount(0);
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
        n2nModel = model;
        formCollumns();
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
        table.setItemCount(countRelation);
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
        final PropertyContainer cont = (PropertyContainer)item.getData();
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
}
