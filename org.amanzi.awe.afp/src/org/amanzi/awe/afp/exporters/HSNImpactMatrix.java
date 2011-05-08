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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.statistic.IStatistic;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * <p>
 *Hsn Impact matrix
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class HSNImpactMatrix {
    private final Map<Node, Coordinate> plans = new HashMap<Node, Coordinate>();
    private final Map<Node,Integer> definedPlans = new HashMap<Node, Integer>();
    private final Map<Node,Integer> storedPlans = new HashMap<Node, Integer>();
    private final FrequencyPlanModel freq;
    private final NetworkService ns=NeoServiceFactory.getInstance().getNetworkService();
    private final NetworkModel model;

    public HSNImpactMatrix(NetworkModel model,FrequencyPlanModel freq) {
        this.model = model;
        this.freq = freq;
        plans.clear();
        Iterator<Node> it = freq.getFrequencyPlansIterator();
        while (it.hasNext()) {
            Node plan = it.next();
            if (plan.hasProperty(INeoConstants.PROPERTY_MAL)) {
                Node sector = ns.findSectorOfPlan(plan);
                Coordinate coord=model.getCoordinateOfSector(sector);
                plans.put(plan,coord);
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
    public void saveStored(FrequencyPlanModel model,IStatistic statistic) {
        Transaction tx = DatabaseManager.getInstance().getCurrentDatabaseService().beginTx();
        try{
            for (Entry<Node, Integer> entry:storedPlans.entrySet()){
                Node plan = entry.getKey();
                plan.setProperty("hsn", entry.getValue());
                statistic.indexValue(model.getName(), NodeTypes.FREQUENCY_PLAN.getId(), "hsn", entry.getValue());

            }
            statistic.save();
            tx.success();
        }finally{
            tx.finish();
        }
    }

}
