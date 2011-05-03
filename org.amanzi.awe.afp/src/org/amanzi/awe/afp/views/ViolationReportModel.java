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

package org.amanzi.awe.afp.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amanzi.awe.ui.custom_table.ChangeModelType;
import org.amanzi.awe.ui.custom_table.IModelChangeEvent;
import org.amanzi.awe.ui.custom_table.TableModel;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.utils.ConvertIterator;
import org.amanzi.neo.services.utils.CountedIterable;
import org.amanzi.neo.services.utils.FilteredIterator;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 * ViolationReportModel
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class ViolationReportModel extends TableModel {
    private CountedIterable<ViolationWrapper> iterable = null;
    private ILazyContentProvider provider;
    private FrequencyPlanModel model;
    private List<String> columns;
    private String[] names = new String[0];
    private NodeToNodeRelationModel impact;
    private NetworkService ns = NeoServiceFactory.getInstance().getNetworkService();

    public ViolationReportModel() {
        provider = new ContentProvider();
        columns = new ArrayList<String>();
        columns.add("Site");
        columns.add("Sector");
        columns.add("TRX");
        columns.add("Violation");
        columns.add("Site");
        columns.add("Sector");
        columns.add("TRX");
        columns.add("Impact");
        columns.add("Co-Site");
        columns.add("Co-Sector");

    }

    @Override
    public ILazyContentProvider getContentProvider() {
        return provider;
    }

    @Override
    public int getRowsCount() {
        return iterable == null ? 0 : iterable.getElementCount(true);
    }

    public String getDataTxt(int column, int row) {
        ViolationWrapper el = iterable.getElement(row);
        return el.getStrValue(column);
    }

    @Override
    public TableViewerColumn createColumn(TableViewer viewer, int columnId) {
        TableViewerColumn columnVuewer = super.createColumn(viewer, columnId);
        columnVuewer.setLabelProvider(new ViolationLabelProvider(columnId));
        return columnVuewer;
    }

    public void setFrequencyPlanModel(final FrequencyPlanModel model) {
        this.model = model;
        impact = new NodeToNodeRelationModel(model.getSingleSource());
        names = (String[])impact.getRootNode().getProperty("names");

        final Iterable<Relationship> relationIter2 = impact.getNeighTraverser(null).relationships();
        final Iterable<Relationship> relationIter = new Iterable<Relationship>() {

            @Override
            public Iterator<Relationship> iterator() {
                return new FilteredIterator<Relationship>(relationIter2.iterator()) {

                    @Override
                    public boolean canBeNext(Relationship impactRel) {
                        Node trx1 = impact.findNodeFromProxy(impactRel.getStartNode());
                        Node trx2 = impact.findNodeFromProxy(impactRel.getEndNode());
                        Node planNode1 = model.findPlanNode(trx1);
                        Node planNode2 = model.findPlanNode(trx2);
                        if (planNode1 == null || planNode2 == null) {
                            return false;
                        }
                        Integer arfcn1 = (Integer)planNode1.getProperty("arfcn", null);
                        Integer arfcn2 = (Integer)planNode2.getProperty("arfcn", null);
                        if (arfcn1 == null || arfcn2 == null) {
                            return false;
                        }
                        boolean isCo= arfcn1.equals(arfcn2);
                        //co handled during cretion impact
                        boolean isAdj= Math.abs(arfcn1 - arfcn2) == 1;
                        if (isAdj){
                            float ad=(Float)impactRel.getProperty("adj",0.0f);
                            isAdj=ad>0;
                        }
                        return isCo||isAdj;
                    }
                };
            }
        };
        Iterable<ViolationWrapper> baseIterable = new Iterable<ViolationWrapper>() {

            @Override
            public Iterator<ViolationWrapper> iterator() {
                return new ConvertIterator<ViolationWrapper, Relationship>(relationIter.iterator()) {

                    @Override
                    protected ViolationWrapper convert(Relationship next) {
                        return new ViolationWrapper(next);
                    }
                };
            }
        };
        iterable = new CountedIterable<ViolationWrapper>(baseIterable);
        fireEvent(new IModelChangeEvent() {

            @Override
            public ChangeModelType getType() {
                return ChangeModelType.CONTENT;
            }

            @Override
            public Object getData() {
                return iterable;
            }
        });

    }

    @Override
    public boolean canSort() {
        return false;
    }

    @Override
    public void sortData(int columnId, int direction) {
    }

    @Override
    public void updateColumn(Table table, TableColumn column, int columnId) {
        column.setText(getColumnTxt(columnId));

    }

    public String getColumnTxt(int columnId) {
        if (columnId < columns.size()) {
            return columns.get(columnId);
        } else {
            return names[columnId - columns.size()];
        }
    }

    @Override
    public int getColumnsCount() {
        return columns.size() + names.length;
    }

    public static class ContentProvider implements ILazyContentProvider {
        CountedIterable<ViolationWrapper> iterable;
        private TableViewer viewer;

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            this.viewer = (TableViewer)viewer;
            iterable = (CountedIterable<ViolationWrapper>)newInput;
        }

        @Override
        public void updateElement(int index) {
            if (iterable == null) {
                return;
            }
            ViolationWrapper wrapper = iterable.getElement(index);
            viewer.replace(wrapper, index);
        }

    }

    public class ViolationLabelProvider extends ColumnLabelProvider {
        private final int columnId;

        public ViolationLabelProvider(int columnId) {
            this.columnId = columnId;
        }

        @Override
        public String getText(Object element) {
            ViolationWrapper wrapper = (ViolationWrapper)element;
            return wrapper == null ? "" : wrapper.getStrValue(columnId);
        }
    }

    public class ViolationWrapper {
        private final Relationship impactrelation;
        private final String[] fields;

        public ViolationWrapper(Relationship impactrelation) {
            this.impactrelation = impactrelation;
            fields = new String[getColumnsCount()];
            fill();
        }

        /**
         * @param columnId
         * @return
         */
        public String getStrValue(int columnId) {
            return fields[columnId];
        }

        public void fill() {
            Node trx1 = impact.findNodeFromProxy(impactrelation.getStartNode());
            Node trx2 = impact.findNodeFromProxy(impactrelation.getEndNode());
            Node sector1 = trx1.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(trx1);
            Node sector2 = trx2.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(trx2);
            Node site1 = sector1.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(sector1);
            Node site2 = sector2.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(sector2);

            Node planNode1 = model.findPlanNode(trx1);
            Node planNode2 = model.findPlanNode(trx2);

            Integer arfcn1 = (Integer)planNode1.getProperty("arfcn");
            Integer arfcn2 = (Integer)planNode2.getProperty("arfcn");
            boolean isCo = arfcn1.equals(arfcn2);
            int i = 0;
            fields[i++] = ns.getNodeName(site1);
            fields[i++] = ns.getNodeName(sector1);
            fields[i++] = ns.getNodeName(trx1);
            fields[i++] = isCo ? "Co-channel" : "Adj-channel";
            fields[i++] = ns.getNodeName(site2);
            fields[i++] = ns.getNodeName(sector2);
            fields[i++] = ns.getNodeName(trx2);
            fields[i++] = String.valueOf(impactrelation.getProperty(isCo ? "co" : "adj"));
            float[] arr = (float[])impactrelation.getProperty(isCo ? "contributions_co" : "contributions_adj");
            fields[i++] = String.valueOf(arr[arr.length - 2]);
            fields[i++] = String.valueOf(arr[arr.length - 1]);
            for (int j = 0; j < arr.length - 2; j++) {
                fields[i++] = String.valueOf(arr[j]);
            }
        }

    }

    /**
     *
     */

}
