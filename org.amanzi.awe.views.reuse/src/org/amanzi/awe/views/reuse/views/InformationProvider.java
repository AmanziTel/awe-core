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

package org.amanzi.awe.views.reuse.views;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.statistic.StatisticManager;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class InformationProvider {


    private IStatistic statistic;
    private DatasetService ds;
    private final Node root;
    private String name;

    /**
     * @param node
     */
    public InformationProvider(Node root) {
       this.root = root;
    statistic=StatisticManager.getStatistic(root);
       ds=NeoServiceFactory.getInstance().getDatasetService();
       name=ds.getNodeName(root);
       
    }

    /**
     *
     * @return
     */
    public Map<String, ISelectionInformation> getStatisticMap() {
        HashMap<String, ISelectionInformation> result = new HashMap<String, ISelectionInformation>();
        Set<String> rootKey = statistic.getRootKey();

        if (rootKey.isEmpty()){
            return result;
        }
        
        if (NodeTypes.NETWORK.checkNode(root)){
            Set<String> nodeTypeKey=statistic.getNodeTypeKey(name);
            if (nodeTypeKey.isEmpty()){
                return result;
            }        
            for (String nodeType:nodeTypeKey){
                ISelectionInformation inf=new BaseNetworkSelectionInformation(root,statistic,name,nodeType);
                if (!inf.getPropertySet().isEmpty()){
                    result.put(inf.getDescription(), inf);
                }
            }
        }else{
          //TODO implement
        }
        return result;
    }

}
