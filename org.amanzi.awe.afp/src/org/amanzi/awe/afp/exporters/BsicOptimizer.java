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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.statistic.StatisticManager;
import org.amanzi.neo.services.utils.Pair;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * <p>
 * BSIC optimizer
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class BsicOptimizer {
    static final Logger LOG=Logger.getLogger(BsicOptimizer.class);
    private NetworkModel network;
    private FrequencyPlanModel freq;
    private NetworkService ns;
    private IStatistic statistic;
    private Map<Integer, BSICMatrix> groups = new HashMap<Integer, BSICMatrix>();
    private final int[] bcc;
    private final int[] ncc;
    private int totalCount;
    private CoordinateReferenceSystem crs;

    public BsicOptimizer(NetworkModel network, FrequencyPlanModel freq, int[] bcc, int[] ncc) {
        this.network = network;
        crs=network.getCrs();
        
        this.freq = freq;
        this.bcc = bcc;
        this.ncc = ncc;
        totalCount = bcc.length * ncc.length;
        Assert.isTrue(totalCount > 0);
        ns = NeoServiceFactory.getInstance().getNetworkService();
        statistic = StatisticManager.getStatistic(network.getRootNode());
    }

    public void run(IProgressMonitor monitor) {
        defineGroups(monitor);
        for (BSICMatrix matrix : groups.values()) {
            optimizeMatrix(matrix);
        }
    }

    /**
     * @param matrix
     */
    private void optimizeMatrix(BSICMatrix matrix) {
        if (matrix.getSize() <= totalCount) {
            setBSIC(matrix);
        } else {
            optimizeBSIC(matrix);
        }
    }

    /**
     * @param matrix
     */
    private void optimizeBSIC(BSICMatrix matrix) {
        Set<Node>nodes=matrix.getNodesWithDifCoord();
        double violation=Double.POSITIVE_INFINITY;
        for (Node firstNode: nodes){
            double viol=optimizeByFirstNode(matrix,firstNode);
            if (viol<violation){
                matrix.storeDefine();
                violation=viol;
            }
        }
        LOG.info(String.format("Violation by arfcn=%s is %s "+matrix.getArfcn(),violation));
    }
    public double optimizeByFirstNode(BSICMatrix matrix,Node firstPlanNode){
        int averageCount=(int)Math.ceil((double)matrix.getSize()/totalCount);
        int totalDefined=0;
        int totalDefinedBsic=0;
        int totalSize=matrix.getSize();
        double violation=0;
        matrix.clearDefine();
        Iterator<Pair<String,String>>bsciIterator=formBSICIterator();
        List<Node> sortedAscByDistance= matrix.getAscList(firstPlanNode);
        int i=0;
        boolean simpleAssign=false;
        while(totalDefined<totalSize){
            Node planToSet=sortedAscByDistance.get(i++);
            if (matrix.isDefined(planToSet)){
                continue;
            }
            if (simpleAssign){
                matrix.definePlan(planToSet,bsciIterator.next()); 
                continue;
            }
            Pair<Double,Collection<Node>> plans=matrix.getPlanWithFarPlans(planToSet,sortedAscByDistance,i,averageCount);
            violation+=plans.left();
            totalDefined+=plans.right().size();
            matrix.definePlans(plans.right(),bsciIterator.next());
            totalDefinedBsic++;
            simpleAssign=totalCount-totalDefinedBsic>=totalSize-totalDefined;
        }  
        return violation;
    }

    /**
     *
     * @return
     */
    private Iterator<Pair<String, String>> formBSICIterator() {
        return new Iterator<Pair<String, String>>(){
            private int it=-1;
            

            @Override
            public boolean hasNext() {
                return it+1<totalCount;
            }

            @Override
            public Pair<String, String> next() {
                it++;
                int nccId=it/bcc.length;
                int bccId=it%bcc.length;
                return new Pair<String, String>(String.valueOf(ncc[nccId]), String.valueOf(bcc[bccId]));
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * @param matrix
     */
    private void setBSIC(BSICMatrix matrix) {
        Transaction tx = DatabaseManager.getInstance().getCurrentDatabaseService().beginTx();
        try {
            Iterator<Node> planIterator = matrix.getPlanIterator();
            l1: for (int nc : ncc) {
                for (int bc : bcc) {
                    if (planIterator.hasNext()) {
                        Node plan = planIterator.next();
                        String nccStr = String.valueOf(nc);
                        String bccStr = String.valueOf(bc);
                        plan.setProperty("ncc", nccStr);
                        plan.setProperty("bcc", bccStr);
                        statistic.indexValue(freq.getName(), NodeTypes.FREQUENCY_PLAN.getId(), "ncc", nccStr);
                        statistic.indexValue(freq.getName(), NodeTypes.FREQUENCY_PLAN.getId(), "bcc", bccStr);

                    } else {
                        break l1;
                    }
                }
            }
            statistic.save();
            tx.success();
        } finally {
            tx.finish();
        }
    }

    private void defineGroups(IProgressMonitor monitor) {
        groups.clear();
        for (Node plan : freq.getFrequencyPlans()) {
            Integer arfcn = (Integer)plan.getProperty(INeoConstants.PROPERTY_SECTOR_ARFCN, null);
            if (arfcn == null) {
                continue;
            }
            Node trx = freq.getSingleTrx(plan);
            if (!ns.isBCCHTRX(trx)) {
                continue;
            }
            Node sector=ns.findSectorOfPlan(plan);
            Coordinate coordinate = network.getCoordinateOfSector(sector);
            if (coordinate==null){
                LOG.error(String.format("Skip plan from optimize - Sector '%s' do not have coordinates",sector));
            }
            BSICMatrix matrix = groups.get(arfcn);
            if (matrix == null) {
                matrix = new BSICMatrix(arfcn,crs);
                groups.put(arfcn, matrix);
            }
            matrix.addPlan(plan,coordinate);
        }
    }
}
