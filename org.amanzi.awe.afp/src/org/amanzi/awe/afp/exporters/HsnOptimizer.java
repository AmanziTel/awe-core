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

import java.util.Random;

import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.statistic.StatisticManager;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class HsnOptimizer {
    static final Logger LOG=Logger.getLogger(HsnOptimizer.class);
    private NetworkModel network;
    private FrequencyPlanModel freq;
    private NetworkService ns;
    private IStatistic statistic;
    private CoordinateReferenceSystem crs;
    private Random random=new Random();

    public HsnOptimizer(NetworkModel network, FrequencyPlanModel freq) {
        this.network = network;
        crs=network.getCrs();
        this.freq = freq;
        ns = NeoServiceFactory.getInstance().getNetworkService();
        statistic = StatisticManager.getStatistic(network.getRootNode());
    }
    public void run(IProgressMonitor monitor) {
        HSNImpactMatrix matrix = new HSNImpactMatrix(network,freq);
        optimize(matrix,monitor);
        
        
    }
    /**
     *
     * @param matrix
     * @param monitor
     */
    private void optimize(HSNImpactMatrix matrix, IProgressMonitor monitor) {
        double violation=Double.POSITIVE_INFINITY;
        //TODO only random, check for more correctly mechanism
        int k=0;
        int maxK=100;
        do{
            k++;
            matrix.clearDefine();
            double viol=defineMatrix(matrix);
            if (viol<violation){
                k=0;
                maxK=maxK/2;
                violation=viol;
                matrix.storeDefine();
            }
        }while (violation>0||k<maxK);
        matrix.saveStored(freq,statistic);
        LOG.info(String.format("Violation by hsn is %s ",violation));
    }
    /**
     *
     * @param matrix
     * @return
     */
    private double defineMatrix(HSNImpactMatrix matrix) {
        int hsn=1;
        for (int i=0;i<matrix.getSize();i++){
            matrix.definePlanToRandomNode(hsn);
            hsn++;
            if (hsn>63){
                hsn=1;
            }
        }
        return matrix.calculateViolation(crs);
    }
    /**
     *
     * @return
     */
//    private int getHsn() {
//        //1..63
//        return random.nextInt(63)+1;
//    }
}
