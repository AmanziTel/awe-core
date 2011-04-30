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

import java.util.Arrays;
import java.util.Iterator;

import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.statistic.StatisticManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 * Maio optimizer
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class MaioOptimizer {
    private final NetworkModel network;
    private final FrequencyPlanModel freq;
    private NetworkService ns;
    private IStatistic statistic;

    public MaioOptimizer(NetworkModel network, FrequencyPlanModel freq) {
        this.network = network;
        this.freq = freq;
        ns = NeoServiceFactory.getInstance().getNetworkService();
        statistic = StatisticManager.getStatistic(network.getRootNode());
    }

    /**
     * @param monitor
     */
    public void run(IProgressMonitor monitor) {
        int count = 0;
        Iterator<Node> planIter = freq.getFrequencyPlansIterator();
        Transaction tx = DatabaseManager.getInstance().getCurrentDatabaseService().beginTx();
        try {
            while (planIter.hasNext()) {
                final Node plan = planIter.next();
                int[] mal = (int[])plan.getProperty(INeoConstants.PROPERTY_MAL, null);
                if (mal == null) {
                    continue;
                }
                int numOfTrx = ns.getTotalTrxOfPlan(plan) - 1;
                int arrLength = Math.min(mal.length, numOfTrx);
                if (arrLength < 1) {
                    continue;
                }
                int step = haveAdj(mal) ? 2 : 1;
                int[] maio = new int[arrLength];
                int j = 0;
                for (int i = 0; i < arrLength; i++) {
                    maio[i] = j;
                    j += step;
                    if (j >= mal.length) {
                        j = 1;
                    }
                }
                plan.setProperty(INeoConstants.PROPERTY_MAIO, maio);
                if (count++ > 5000) {
                    count = 0;
                    tx.success();
                    tx.finish();
                    tx = DatabaseManager.getInstance().getCurrentDatabaseService().beginTx();
                }

            }
            // statistic.save();
            tx.success();
        } finally {
            tx.finish();
        }
    }

    /**
     * @param mal
     * @return
     */
    private boolean haveAdj(int[] mal) {
        Arrays.sort(mal);
        for (int i=1;i<mal.length;i++){
            if (Math.abs(mal[i-1]-mal[i])<=1){
                return true;
            }
        }
        return false;
    }

}
