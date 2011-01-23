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

import org.amanzi.awe.ui.AweUiPlugin;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.TransactionWrapper;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
    private static final int PAGE_SIZE =64;
    private IStatistic statistic;
    private Table table;
    private NodeToNodeRelationModel n2nModel;
    private TransactionWrapper tx;
    private ArrayList<TableColumn> columns = new ArrayList<TableColumn>();
    private int colColut = 0;
    private Combo n2nSelection;
    private Button commit;
    private Button rollback;
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
        table = new Table(main, SWT.VIRTUAL | SWT.BORDER);
        table.addListener(SWT.SetData, new Listener() {

            @Override
            public void handleEvent(Event event) {
                setData(event);
            }
        });
        table.setItemCount(0);
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
        table.setLayoutData(layoutData);
        setFilter(networks.getAllNode2NodeFilter(AweUiPlugin.getDefault().getUiService().getActiveProjectNode()));
    }

    /**
     * @param event
     */
    protected void setData(Event event) {
        TableItem item = (TableItem)event.item;
        int index = event.index;
        int start = index / PAGE_SIZE * PAGE_SIZE;
        int end = Math.min (start + PAGE_SIZE, table.getItemCount ());
        for (int i = start; i < end; i++) {
            item = table.getItem (i);
            item.setText ("Item " + i);
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
    }

    /**
     *
     */
    protected void commit() {
        tx.commit();
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
        int countRelation=0;
        table.clearAll();
        if (n2nModel == null) {
            colColut = 0;
            statistic = null;
        } else {
            Node networkNode = n2nModel.getNetworkNode();
            statistic = StatisticManager.getStatistic(networkNode);
            String key = n2nModel.getName();
            Set<String> nodeTypeKey = statistic.getNodeTypeKey(key);
            // expected nodeTypeKey.size()==1;
            if (nodeTypeKey.isEmpty()) {
                colColut = 0;
            } else {
                String nodeTypeId = nodeTypeKey.iterator().next();
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
                for (Relationship rel:n2ns.getRelationTraverserByServNode(filter.getFilteredServNodes(n2nModel))){
                    countRelation++; 
                }
                createdIter = createCountedIter();
            }
        }
        resizecolumns();
        table.setItemCount(countRelation);
    }

    /**
     *
     * @return
     */
    protected CountedIteratorWr createCountedIter() {
        return new CountedIteratorWr(n2ns.getRelationTraverserByServNode(filter.getFilteredServNodes(n2nModel)).iterator());
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
    private static class CountedIteratorWr implements Iterator<PropertyContainer>{
        private final Iterator<? extends PropertyContainer> baseIterator;
        private int index;

        CountedIteratorWr(Iterator<? extends PropertyContainer> baseIterator){
            this.baseIterator = baseIterator;
            index=0;
            
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
