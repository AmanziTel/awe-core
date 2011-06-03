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

import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.statistic.StatisticManager;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Kondratenko_V
 * @since 1.0.0
 */
public abstract class DefaultDistribution  implements IDistributionModel {

    protected String nType;
    protected String pName;
    protected IDistributionalModel distributionalModel;
    protected static IStatistic stat ;
    protected List<IRange> rangeList;
    


    /**
     * @param nodeType type of node
     * @param propertyName name of property
     */
   public DefaultDistribution(IDistributionalModel model,String nodeType,String propertyName){
        nType=nodeType;
        pName=propertyName;
        distributionalModel=model;
        stat= StatisticManager.getStatistic(model.getRootNode());
        rangeList=new ArrayList<IRange>();
        init();
    }
   /**
    * Initialize statistic property value
    */
  abstract protected  void init();
  /**
   * @return Returns the rangeList.
   */
  abstract public List<IRange> getRangeList(); 
  

}
