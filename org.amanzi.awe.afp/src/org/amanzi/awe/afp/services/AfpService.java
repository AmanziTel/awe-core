package org.amanzi.awe.afp.services;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.afp.models.parameters.ChannelType;
import org.amanzi.awe.afp.models.parameters.FrequencyBand;
import org.amanzi.awe.afp.models.parameters.OptimizationType;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.enums.DatasetRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluators;

public class AfpService extends AbstractService {
    
    private static final Logger LOGGER = Logger.getLogger(AfpService.class);
    
    private static final String OPTIMIZATION_TYPES = "optimization_types";
    
    private static final String OPTIMIZATION_BANDS = "optimization_domains";
    
    private static final String CHANNEL_TYPES = "channel_types";

    private static AfpService instance;
    
    private DatasetService datasetService;
    
    private NetworkService networkService;

    public static AfpService getService() {
        if (instance == null) {
            instance = new AfpService();
        }

        return instance;
    }

    /**
     * constructor
     */
    private AfpService() {
        super();
        datasetService = NeoServiceFactory.getInstance().getDatasetService();
        networkService = NeoServiceFactory.getInstance().getNetworkService();
    }

    public Node createAfpNode(String scenarioName, Node networkNode) {
        LOGGER.info("Creating AFP Node for Scenario <" +scenarioName + ">");
        
        Transaction tx = databaseService.beginTx();
        
        Node result = null;
        try {
            result = datasetService.createNode(NodeTypes.AFP);
            result.setProperty(INeoConstants.PROPERTY_NAME_NAME, scenarioName);
            
            networkNode.createRelationshipTo(result, DatasetRelationshipTypes.CHILD);
         
            tx.success();
        }
        catch (Exception e){ 
            LOGGER.error("An error on creating AFP Node", e);
        }
        finally {
            tx.finish();
        }
        
        return result;
    }
    
    public List<Node> getAllAfpNodes(Node networkNode) {
        LOGGER.info("Search for all AFP Model Nodes");
        
        ArrayList<Node> result = new ArrayList<Node>();
        
        for (Node afpNode : networkService.getNetworkElementTraversal(Evaluators.atDepth(1), NodeTypes.AFP).
                                           traverse(networkNode).nodes()) {
            result.add(afpNode);
        }
        
        return result;
    }
    
    public List<OptimizationType> loadOptimizationTypes(Node modelNode) {
        ArrayList<OptimizationType> result = new ArrayList<OptimizationType>();
        
        String[] enabledTypes = (String[])modelNode.getProperty(OPTIMIZATION_TYPES, ArrayUtils.EMPTY_STRING_ARRAY);
        
        for (String type : enabledTypes) {
            result.add(OptimizationType.valueOf(type));
        }
        
        return result;
    }
    
    public List<FrequencyBand> loadOptimizationBands(Node modelNode) {
        ArrayList<FrequencyBand> result = new ArrayList<FrequencyBand>();
        
        String[] enabledTypes = (String[])modelNode.getProperty(OPTIMIZATION_BANDS, ArrayUtils.EMPTY_STRING_ARRAY);
        
        for (String type : enabledTypes) {
            result.add(FrequencyBand.valueOf(type));
        }
        
        return result;
    }
    
    public List<ChannelType> loadChannelTypes(Node modelNode) {
        ArrayList<ChannelType> result = new ArrayList<ChannelType>();
        
        String[] enabledTypes = (String[])modelNode.getProperty(CHANNEL_TYPES, ArrayUtils.EMPTY_STRING_ARRAY);
        
        for (String type : enabledTypes) {
            result.add(ChannelType.valueOf(type));
        }
        
        return result;
    }
    
    public void saveOptimizationTypes(Node modelNode, List<OptimizationType> enabledTypes) {
        LOGGER.info("Saving Optimization Types");
        String[] parameterValue = new String[enabledTypes.size()];
        int i = 0;
        for (OptimizationType type : enabledTypes) {
            parameterValue[i++] = type.name();
        }
        
        Transaction tx = databaseService.beginTx();
        try {
            modelNode.setProperty(OPTIMIZATION_TYPES, parameterValue);
            tx.success();
        }
        catch (Exception e) {
            LOGGER.error("Error on saving Optimization Types", e);
        }
        finally {
            tx.finish();
        }
    }
    
    public void saveOptimizationBands(Node modelNode, List<FrequencyBand> optimizationBands) {
        LOGGER.info("Saving Optimization Bands");
        
        String[] parameterValue = new String[optimizationBands.size()];
        int i = 0;
        for (FrequencyBand band : optimizationBands) {
            parameterValue[i++] = band.name();
        }
        
        Transaction tx = databaseService.beginTx();
        try { 
            modelNode.setProperty(OPTIMIZATION_BANDS, parameterValue);
            tx.success();
        }
        catch (Exception e) {
            LOGGER.error("Error on saving Optimization Bands", e);
        }
        finally {
            tx.finish();
        }
    }
    
    public void saveChannelTypes(Node modelNode, List<ChannelType> channelTypes) {
        LOGGER.info("Saving Channel Types");
        String[] parameterValue = new String[channelTypes.size()];
        int i = 0;
        for (ChannelType type : channelTypes) {
            parameterValue[i++] = type.name();
        }
        
        Transaction tx = databaseService.beginTx();
        try {
            modelNode.setProperty(CHANNEL_TYPES, parameterValue);
            tx.success();
        }
        catch (Exception e) {
            LOGGER.error("Error on saving Channel Types", e);
        }
        finally {
            tx.finish();
        }
    }
}
