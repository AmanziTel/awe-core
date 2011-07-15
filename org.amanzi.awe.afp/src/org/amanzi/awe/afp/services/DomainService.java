package org.amanzi.awe.afp.services;

import org.amanzi.awe.afp.models.AfpFrequencyDomainModel;
import org.amanzi.awe.afp.models.IAfpConstants;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

public class DomainService extends AbstractService{
    /**
     * constructor
     */
    public DomainService(){
        super();
    }
    
    public Node findFrequencyDomainsNode(Node afpNode){
        TraversalDescription td = Traversal.description()
        .relationships(DomainRelations.DOMAINS, Direction.OUTGOING)
        .evaluator(Evaluators.toDepth(1))
        .evaluator(Evaluators.excludeStartPosition());
        
        Node frequencyDomainsNode = null;
        for (Node node : td.traverse(afpNode).nodes()){
            if (node.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").equals("Frequency Domains")){
                frequencyDomainsNode = node;
            }           
        }
        return frequencyDomainsNode;
        
    }
    
    public Node createFrequencyDomainsNode(Node afpNode){
        Node frequencyDomainsNode = databaseService.createNode();
        afpNode.createRelationshipTo(frequencyDomainsNode, DomainRelations.DOMAINS);
        frequencyDomainsNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, "Frequency Domains");
        return frequencyDomainsNode;
    }
    
    public Node getFrequencyDomainsNode(Node afpNode){
        Node freqDomainsNode = findFrequencyDomainsNode(afpNode);
        if (freqDomainsNode == null){
            freqDomainsNode = createFrequencyDomainsNode(afpNode);
        }
        return freqDomainsNode;
    }
    
    public Node getLastFreqDomainNode(Node frequencyDomainsNode,final DomainRelations relation){
        TraversalDescription td = Traversal.description()
        .relationships(relation, Direction.OUTGOING)
        .evaluator(Evaluators.excludeStartPosition())
        .evaluator(new Evaluator() {
            
            @Override
            public Evaluation evaluate(Path arg0) {
                // TODO Auto-generated method stub
                boolean includes = false;
                if (!arg0.endNode().hasRelationship(relation, Direction.OUTGOING))
                    includes = true;
                return Evaluation.ofIncludes(includes);
            }
        });
        Node lastNode = frequencyDomainsNode;
        for (Node currentNode : td.traverse(frequencyDomainsNode).nodes()){
            lastNode = currentNode;
        }
        
        return lastNode;
    }
    
    public Node findFrequencyNode(Node frequencyDomainsNode,String name){
        TraversalDescription td = Traversal.description()
        .relationships(DomainRelations.NEXT, Direction.OUTGOING)
        .evaluator(Evaluators.excludeStartPosition());
        Node frequencyNode = null;
        for (Node currentDomain : td.traverse(frequencyDomainsNode).nodes()){
            if (currentDomain.getProperty(INeoConstants.PROPERTY_NAME_NAME).equals(name))
                frequencyNode = currentDomain;
        }
        return frequencyNode;
        
    }
    
    public void createFrequencyDomainNode(Node frequencyDomainsNode, AfpFrequencyDomainModel domainModel, Integer order) {
        Node frequencyNode = findFrequencyNode(frequencyDomainsNode, domainModel.getName());
        if (frequencyNode==null){
            frequencyNode = databaseService.createNode();
            NodeTypes.DOMAIN.setNodeType(frequencyNode, databaseService);
            NeoUtils.setNodeName(frequencyNode, domainModel.getName(), databaseService);
            frequencyNode.setProperty(IAfpConstants.AFP_PROPERTY_DOMAIN_NAME, IAfpConstants.AFP_DOMAIN_NAME_FREQUENCY);
            frequencyNode.setProperty("order", order);
            if (domainModel.getFilters() != null) {
                frequencyNode.setProperty(IAfpConstants.AFP_PROPERTY_FILTERS_NAME, domainModel.getFilters());
            }
            frequencyNode.setProperty(IAfpConstants.AFP_PROPERTY_FREQUENCY_BAND_NAME, domainModel.getBand());
            frequencyNode.setProperty(IAfpConstants.AFP_PROPERTY_FREQUENCIES_NAME, domainModel.getFrequencies());
            frequencyNode.setProperty(IAfpConstants.AFP_PROPERTY_TRX_COUNT_NAME, domainModel.getNumTRX());
         
            getLastFreqDomainNode(frequencyDomainsNode, DomainRelations.NEXT)
            .createRelationshipTo(frequencyNode, DomainRelations.NEXT);
        }
        else {
            if (domainModel.getFilters() != null) {
                frequencyNode.setProperty(IAfpConstants.AFP_PROPERTY_FILTERS_NAME, domainModel.getFilters());
            }
            frequencyNode.setProperty(IAfpConstants.AFP_PROPERTY_FREQUENCY_BAND_NAME, domainModel.getBand());
            frequencyNode.setProperty(IAfpConstants.AFP_PROPERTY_FREQUENCIES_NAME, domainModel.getFrequencies());
            frequencyNode.setProperty(IAfpConstants.AFP_PROPERTY_TRX_COUNT_NAME, domainModel.getNumTRX());

        }

    }

    public void addAssignRelation(Node frequencyDomainsNode, AfpFrequencyDomainModel domainModel){
        Node freqDomNode = findFrequencyNode(frequencyDomainsNode, domainModel.getName());
        if (!freqDomNode.hasRelationship(DomainRelations.ASSIGNED_NEXT, Direction.INCOMING)){
            Node lastNode = getLastFreqDomainNode(frequencyDomainsNode, DomainRelations.ASSIGNED_NEXT);
            lastNode.createRelationshipTo(freqDomNode, DomainRelations.ASSIGNED_NEXT);
        }
        
    }
    
}
