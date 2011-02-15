package org.amanzi.neo.services.network;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.NodeResult;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.neo4j.graphdb.Node;

public class FrequencyPlanModel {
    private final Node rootNode;
    private DatasetService ds;
    private NetworkService networkService;
    FrequencyPlanModel(Node rootNode) {
        this.rootNode = rootNode;
        ds = NeoServiceFactory.getInstance().getDatasetService();
        networkService = NeoServiceFactory.getInstance().getNetworkService();
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
    public NodeResult getPlanNode(NodeResult trx) {
        return networkService.getPlanNode(rootNode, trx);
    }
    public String getName() {
        return ds.getNodeName(rootNode);
    }
}
