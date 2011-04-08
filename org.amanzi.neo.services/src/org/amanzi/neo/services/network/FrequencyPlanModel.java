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
package org.amanzi.neo.services.network;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.NodeResult;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.neo4j.graphdb.Node;
/**
 * 
 * <p>
 *Frequency Plan Model
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class FrequencyPlanModel {
    private final Node rootNode;
    private DatasetService ds;
    private NetworkService networkService;
    FrequencyPlanModel(Node rootNode) {
        this.rootNode = rootNode;
        ds = NeoServiceFactory.getInstance().getDatasetService();
        networkService = NeoServiceFactory.getInstance().getNetworkService();
    }
    public static FrequencyPlanModel getModel(Node networkRoot,String modelName,String time, String domain){
        NetworkService ns = NeoServiceFactory.getInstance().getNetworkService();
        Node rootNode= ns.getFrequencyRootNode(networkRoot,modelName,time, domain);
        return new FrequencyPlanModel(rootNode);       
    }
    public static FrequencyPlanModel getModel(Node networkRoot,String modelName){
        NetworkService ns = NeoServiceFactory.getInstance().getNetworkService();
        Node rootNode= ns.getFrequencyRootNode(networkRoot,modelName);
        return new FrequencyPlanModel(rootNode);       
    }
    public static FrequencyPlanModel findModel(Node networkRoot,String modelName){
        NetworkService ns = NeoServiceFactory.getInstance().getNetworkService();
        Node rootNode= ns.findFrequencyRootNode(networkRoot,modelName);
        return rootNode==null?null:new FrequencyPlanModel(rootNode);
    }
    public NodeResult getPlanNode(Node trx) {
        return networkService.getPlanNode(rootNode, trx);
    }
    public Node findPlanNode(Node trx) {
        return networkService.findPlanNode(trx,rootNode);
    }
    public String getName() {
        return ds.getNodeName(rootNode);
    }

    public void attachSingleSource(Node sourceRootNode) {
        networkService.attachSingleSource(rootNode, sourceRootNode);
    }
    /**
     * @return Returns the rootNode.
     */
    public Node getRootNode() {
        return rootNode;
    }

    public Node getSingleSource() {
        return networkService.getSingleSource(rootNode);
    }

}
