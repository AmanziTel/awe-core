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
import java.util.Map;

import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.statistic.StatisticManager;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * <p>
 *BSIC optimizer
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class BsicOptimizer {
    private NetworkModel network;
    private FrequencyPlanModel freq;
    private NetworkService ns;
    private IStatistic statistic;
    private Map<Integer,BSICMatrix>groups=new HashMap<Integer, BSICMatrix>();
    private final int[] bcc;
    private final int[] ncc;
    private int totalCount;
    public BsicOptimizer(NetworkModel network, FrequencyPlanModel freq,int[]bcc,int[]ncc)  {
        this.network = network;
        this.freq = freq;
        this.bcc = bcc;
        this.ncc = ncc;
        totalCount=bcc.length*ncc.length;
        Assert.isTrue(totalCount>0);
        ns = NeoServiceFactory.getInstance().getNetworkService();
        statistic = StatisticManager.getStatistic(network.getRootNode());   
    }
    public void run(IProgressMonitor monitor) {
        defineGroups(monitor);
    }

    private void defineGroups(IProgressMonitor monitor) {
        groups.clear();
    }
}
