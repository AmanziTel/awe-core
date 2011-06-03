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

package org.amanzi.neo.services.networkModel;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.statistic.StatisticManager;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public abstract class DefaultDistribution implements IDistributionModel {

    protected INodeType nodeType;
    protected String propertyName;
    protected IDistributionalModel distributionalModel;
    protected IStatistic stat;
    protected List<IRange> rangeList;
    protected final NetworkService networkService;
   
    /**
     * @param nodeType type of node
     * @param propertyName name of property
     */
    protected DefaultDistribution(IDistributionalModel distributionalModel, INodeType nodeType, String propertyName) {
        networkService = NeoServiceFactory.getInstance().getNetworkService();
        this.nodeType = nodeType;
        this.propertyName = propertyName;
        this.distributionalModel = distributionalModel;
        stat = StatisticManager.getStatistic(distributionalModel.getRootNode());
        rangeList = new ArrayList<IRange>();
        init();
    }

    /**
     * Initialize statistic property value
     */
    abstract protected void init();

    /**
     * @return Returns the rangeList.
     */
    abstract public List<IRange> getRangeList();

}
