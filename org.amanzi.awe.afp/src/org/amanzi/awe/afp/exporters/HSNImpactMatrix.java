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

package org.amanzi.awe.afp.exporters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.statistic.IStatistic;
import org.geotools.geometry.jts.JTS;
import org.jfree.util.Log;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * <p>
 * Hsn Impact matrix
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class HSNImpactMatrix {
    private final Map<Node, NodeInfo> plans = new HashMap<Node, NodeInfo>();
    private final Map<Node, Integer> definedPlans = new HashMap<Node, Integer>();
    private final Map<Node, Integer> storedPlans = new HashMap<Node, Integer>();
    private final FrequencyPlanModel freq;
    private final NetworkService ns = NeoServiceFactory.getInstance().getNetworkService();
    private final NetworkModel model;
    private final Random rnd = new Random();

    public HSNImpactMatrix(NetworkModel model, FrequencyPlanModel freq) {
        this.model = model;
        this.freq = freq;
        plans.clear();
        Iterator<Node> it = freq.getFrequencyPlansIterator();
        while (it.hasNext()) {
            Node plan = it.next();
            if (plan.hasProperty(INeoConstants.PROPERTY_MAL)) {
                Node sector = ns.findSectorOfPlan(plan);
                Coordinate coord = model.getCoordinateOfSector(sector);
                plans.put(plan, new NodeInfo(coord, (int[])plan.getProperty(INeoConstants.PROPERTY_MAL)));
            }
        }
    }

    public void clearDefine() {
        definedPlans.clear();
    }

    public boolean isDefined(Node planToSet) {
        return definedPlans.keySet().contains(planToSet);
    }

    public void definePlan(Node planToSet, int hsn) {
        definedPlans.put(planToSet, hsn);
    }

    public void storeDefine() {
        storedPlans.clear();
        storedPlans.putAll(definedPlans);
    }

    public void saveStored(FrequencyPlanModel model, IStatistic statistic) {
        Transaction tx = DatabaseManager.getInstance().getCurrentDatabaseService().beginTx();
        try {
            for (Entry<Node, Integer> entry : storedPlans.entrySet()) {
                Node plan = entry.getKey();
                plan.setProperty("hsn", entry.getValue());
                statistic.indexValue(model.getName(), NodeTypes.FREQUENCY_PLAN.getId(), "hsn", entry.getValue());

            }
            statistic.save();
            tx.success();
        } finally {
            tx.finish();
        }
    }

    /**
     * @return
     */
    public Iterable<Node> getPlanNodeIterator() {
        return Collections.unmodifiableCollection(plans.keySet());
    }

    /**
     * @param crs
     * @return
     */
    public double calculateViolation(CoordinateReferenceSystem crs) {
        boolean[] hsnb = new boolean[64];
        double violation = 0;
        List<Node> nodes = new ArrayList<Node>(definedPlans.keySet());
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            Integer hsn = definedPlans.get(node);
            if (hsnb[hsn]) {
                continue;
            }
            hsnb[hsn] = true;
            for (int j = i + 1; j < nodes.size(); j++) {
                Node node2 = nodes.get(j);
                Integer hsn2 = definedPlans.get(node2);
                if (!hsn2.equals(hsn)) {
                    continue;
                }
                violation += getViolation(crs, node, node2);
            }
        }
        return violation;
    }

    /**
     * @param crs
     * @param node
     * @param node2
     * @return
     */
    private double getViolation(CoordinateReferenceSystem crs, Node node, Node node2) {
        NodeInfo info1 = plans.get(node);
        NodeInfo info2 = plans.get(node2);
        int[] mal1 = info1.getMal();
        int[] mal2 = info2.getMal();
        // mal should be sorted! and do not contain equals elements
        int aindex = 0;
        int bindex = 0;
        int overlap = 0;
        while (aindex < mal1.length && bindex < mal2.length) {
            if (mal1[aindex] == mal2[bindex]) {
                overlap++;
                aindex++;
                bindex++;
            } else if (mal1[aindex] < mal2[bindex]) {
                aindex++;
            } else {
                bindex++;
            }
        }
        double violation = 0;
        if (overlap > 0) {
            try {
                violation = overlap / JTS.orthodromicDistance(info1.getCrd(), info2.getCrd(), crs);
            } catch (TransformException e) {
                e.printStackTrace();
                Log.error("Incorrect CRS", e);
                violation = overlap / info1.getCrd().distance(info2.getCrd());
            }
        }
        return violation;
    }

    private static class NodeInfo {
        final Coordinate crd;
        final int[] mal;

        /**
         * @param crd
         * @param mal
         */
        public NodeInfo(Coordinate crd, int[] mal) {
            super();
            this.crd = crd;
            this.mal = mal;
            Arrays.sort(this.mal);
        }

        public Coordinate getCrd() {
            return crd;
        }

        public int[] getMal() {
            return mal;
        }

    }

    /**
     * @return
     */
    public int getSize() {
        return plans.size();
    }

    /**
     * @param hsn
     */
    public void definePlanToRandomNode(int hsn) {
        if (definedPlans.size() == plans.size()) {
            throw new IndexOutOfBoundsException();
        }
        int ind = rnd.nextInt(definedPlans.size() - plans.size());
        int i = 0;
        for (Node node : plans.keySet()) {
            if (isDefined(node)) {
                continue;
            }
            if (i == ind) {
                definePlan(node, hsn);
                return;
            }
            i++;
        }
    }
}
