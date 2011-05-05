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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.utils.Pair;
import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTS;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * <p>
 * Bsic matrix
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class BSICMatrix {
    private final Map<Node, Coordinate> plans = new HashMap<Node, Coordinate>();
    private final Map<Node, Pair<String, String>> definedPlans = new HashMap<Node, Pair<String, String>>();
    private final Map<Node, Pair<String, String>> storedPlans = new HashMap<Node, Pair<String, String>>();
    private final Integer arfcn;
    private final CoordinateReferenceSystem crs;

    public BSICMatrix(Integer arfcn, CoordinateReferenceSystem crs) {
        this.arfcn = arfcn;
        this.crs = crs;
        plans.clear();
    }

    public void addPlan(Node plan, Coordinate crd) {
        plans.put(plan, crd);
    }

    public int getSize() {
        return plans.size();
    }

    public Iterator<Node> getPlanIterator() {
        return plans.keySet().iterator();
    }

    public void clearDefine() {
        definedPlans.clear();
    }

    public List<Node> getAscList(final Node firstPlanNode) {
        final Coordinate coord = plans.get(firstPlanNode);
        ArrayList<Node> result = new ArrayList<Node>(plans.keySet());
        Collections.sort(result, new Comparator<Node>() {

            @Override
            public int compare(Node o1, Node o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1.equals(firstPlanNode)) {
                    return -1;
                }
                if (o2.equals(firstPlanNode)) {
                    return 1;
                }
                return Double.compare(coord.distance(plans.get(o1)), coord.distance(plans.get(o2)));
            }

        });
        return result;
    }

    public Pair<Double, Collection<Node>> getPlanWithFarPlans(final Node planToSet, List<Node> sortedAscByDistance, int id, int count) {
        // TODO optimize
        Double violat = 0d;
        final Coordinate dist = plans.get(planToSet);
        TreeSet<Node> result = new TreeSet<Node>(new Comparator<Node>() {

            @Override
            public int compare(Node o1, Node o2) {
                if (o1.equals(o2)) {
                    return 0;
                }
                if (o1.equals(planToSet)) {
                    return -1;
                }
                if (o2.equals(planToSet)) {
                    return 1;
                }
                double dist1 = dist.distance(plans.get(o1));
                double dist2 = dist.distance(plans.get(o2));
                // desc order
                return Double.compare(dist2, dist1);
            }

        });
        result.add(planToSet);
        if (count > 1) {
            for (int i = id; i < sortedAscByDistance.size(); i++) {
                Node plan = sortedAscByDistance.get(i);
                if (isDefined(plan)) {
                    continue;
                }
                result.add(plan);
                if (result.size() > count) {
                    result.remove(result.last());
                }
            }
            Iterator<Node> it = result.iterator();
            it.next();
            while (it.hasNext()) {
                Node plan = it.next();
                try {
                    violat += 1d / JTS.orthodromicDistance(dist, plans.get(plan), crs);
                } catch (TransformException e) {
                    Logger.getLogger(getClass()).error(e.getLocalizedMessage(), e);
                    violat += 1d / dist.distance(plans.get(plan));
                }
            }
        }
        return new Pair<Double, Collection<Node>>(violat, result);
    }

    public boolean isDefined(Node planToSet) {
        return definedPlans.keySet().contains(planToSet);
    }

    public void definePlans(Collection<Node> plans, Pair<String, String> bsic) {
        for (Node plan : plans) {
            definePlan(plan, bsic);
        }
    }

    public void definePlan(Node planToSet, Pair<String, String> bsic) {
        definedPlans.put(planToSet, bsic);
    }

    public Set<Node> getNodesWithDifCoord() {
        Map<Node, Coordinate> result = new HashMap<Node, Coordinate>();
        for (Entry<Node, Coordinate> entry : plans.entrySet()) {
            if (!result.values().contains(entry.getValue())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result.keySet();
    }

    public void storeDefine() {
        storedPlans.clear();
        storedPlans.putAll(definedPlans);
    }

    public Integer getArfcn() {
        return arfcn;
    }

    public void saveStored(FrequencyPlanModel model,IStatistic statistic) {
        Transaction tx = DatabaseManager.getInstance().getCurrentDatabaseService().beginTx();
        try{
            for (Entry<Node, Pair<String, String>> entry:storedPlans.entrySet()){
                Node plan = entry.getKey();
                String nccStr = entry.getValue().left();
                String bccStr = entry.getValue().right();
                plan.setProperty("ncc", nccStr);
                plan.setProperty("bcc", bccStr);
                statistic.indexValue(model.getName(), NodeTypes.FREQUENCY_PLAN.getId(), "ncc", nccStr);
                statistic.indexValue(model.getName(), NodeTypes.FREQUENCY_PLAN.getId(), "bcc", bccStr);

            }
            statistic.save();
            tx.success();
        }finally{
            tx.finish();
        }
        
        
        
    }

}
