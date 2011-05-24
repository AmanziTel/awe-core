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

package org.amanzi.neo.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.services.enums.DatasetRelationshipTypes;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.indexes.PropertyIndex.NeoIndexRelationshipTypes;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.node2node.INode2NodeFilter;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationService;
import org.amanzi.neo.services.utils.Utils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.PruneEvaluator;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.helpers.Predicate;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkService extends DatasetService {

    /** The Constant FREQUENCY. */
    public static final String FREQUENCY = "frequency";
    

    /**
     * The Class NameFilter.
     */
    private class NameFilter implements Evaluator {
        
        /** The name. */
        private String name;
        
        /** The to continue. */
        private boolean toContinue;
        
        /**
         * Instantiates a new name filter.
         *
         * @param name the name
         * @param toContinue the to continue
         */
        public NameFilter(String name, boolean toContinue) {
            this.name = name;
            this.toContinue = toContinue;
        }

        /**
         * Evaluate.
         *
         * @param arg0 the arg0
         * @return the evaluation
         */
        @Override
        public Evaluation evaluate(Path arg0) {
            boolean include = name.equals(getNodeName(arg0.endNode()));
            return Evaluation.of(include, toContinue);
        }
    }
    
    /**
     * The Class MultiNodeTypeFilter.
     */
    private class MultiNodeTypeFilter implements Evaluator {
        
        /** The node types. */
        private INodeType[] nodeTypes;
        
        /** The to continue. */
        private boolean toContinue;
        
        /**
         * Instantiates a new multi node type filter.
         *
         * @param toContinue the to continue
         * @param nodeTypes the node types
         */
        public MultiNodeTypeFilter(boolean toContinue, INodeType ... nodeTypes) {
            this.nodeTypes = nodeTypes;
            this.toContinue = toContinue;
        }

        /**
         * Evaluate.
         *
         * @param arg0 the arg0
         * @return the evaluation
         */
        @Override
        public Evaluation evaluate(Path arg0) {
            boolean include = false;
            INodeType currentNodeType = getNodeType(arg0.endNode());
            
            if (currentNodeType != null) {
                for (INodeType singleType : nodeTypes) {
                    if ((singleType != null) && 
                        (singleType.equals(currentNodeType))) {
                        include = true;
                        break;
                    }
                }
            }
            
            return Evaluation.of(include, toContinue);
        }
        
    }
    
    /**
     * Instantiates a new network service.
     */
    public NetworkService() {
        super();        
    }

    /**
     * Get BSC node with necessary name if bsc node not exist it will be created.
     *
     * @param networkNode - network root node
     * @param bscName -bsc name
     * @param parentNode - parent node
     * @return the bsc node
     */
    public NodeResult getBscNode(Node networkNode, String bscName, Node parentNode) {
        Node result = findBscNode(networkNode, bscName);
        boolean isCreated = result == null;
        if (result == null) {
            Transaction tx = databaseService.beginTx();
            try {
                result = NeoServiceFactory.getInstance().getDatasetService().addSimpleChild(parentNode, NodeTypes.BSC, bscName);
                getIndexService().index(result,
                        Utils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.BSC), bscName);

                tx.success();
            } finally {
                tx.finish();
            }

        }
        return new DatasetService.NodeResultImpl(result, isCreated);
    }

    /**
     * Find BSC node by name.
     *
     * @param networkNode network root node
     * @param bscName bsc name
     * @return bsc node or null if node not found;
     */
    public Node findBscNode(Node networkNode, String bscName) {
        return getIndexService().getSingleNode(
                Utils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.BSC), bscName);
    }

    /**
     * Get site node with necessary name if site node not exist it will be created.
     *
     * @param networkNode - network root node
     * @param siteName -site name
     * @param parentNode - parent node
     * @return the site
     */
    public NodeResult getSite(Node networkNode, String siteName, Node parentNode) {
        Node result = findSiteNode(networkNode, siteName);
        boolean isCreated = result == null;
        if (isCreated) {
            Transaction tx = databaseService.beginTx();
            try {
                result = NeoServiceFactory.getInstance().getDatasetService().addSimpleChild(parentNode, NodeTypes.SITE, siteName);
                getIndexService().index(result,
                        Utils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), siteName);
                tx.success();
            } finally {
                tx.finish();
            }

        }
        return new DatasetService.NodeResultImpl(result, isCreated);
    }

    /**
     * Find Site node by name.
     *
     * @param networkNode network root node
     * @param siteName site name
     * @return site node or null if node not found;
     */
    public Node findSiteNode(Node networkNode, String siteName) {
        return getIndexService().getSingleNode(
                Utils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), siteName);
    }

    /**
     * Find sector node Sector node find by many parameters: if ci==null&&returnFirsElement==true -
     * We return the first matching by name sector node or we use lucene index for finding nodes
     * with necessary ci and fing by others defined parameters.
     * 
     * @param rootNode the root node
     * @param ci the ci property
     * @param lac the lac property
     * @param name the sector name
     * @param returnFirsElement the return firs element
     * @return the sector node or null
     */
    public Node findSector(Node rootNode, Integer ci, Integer lac, String name, boolean returnFirsElement) {
        return super.findSector(rootNode, ci, lac, name, returnFirsElement);
    }

    /**
     * Find sector.
     *
     * @param rootNode the root node
     * @param name the name
     * @param returnFirstElement the return first element
     * @return the node
     */
    public Node findSector(Node rootNode, String name, boolean returnFirstElement) {
        return super.findSector(rootNode, null, null, name, returnFirstElement);
    }

    /**
     * Create sector.
     *
     * @param networkNode - network node
     * @param site - parent node
     * @param sectorName sector name
     * @param ci - ci
     * @param lac - lac
     * @return the node
     */
    public Node createSector(Node networkNode, Node site, String sectorName, Integer ci, Integer lac) {
        Transaction tx = databaseService.beginTx();
        try {
            Node result = NeoServiceFactory.getInstance().getDatasetService().addSimpleChild(site, NodeTypes.SECTOR, sectorName);
            getIndexService().index(result,
                    Utils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR), sectorName);
            if (ci != null) {
                result.setProperty(INeoConstants.PROPERTY_SECTOR_CI, ci);
                getIndexService().index(result,
                        Utils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_SECTOR_CI, NodeTypes.SECTOR), ci);
            }
            if (lac != null) {
                result.setProperty(INeoConstants.PROPERTY_SECTOR_LAC, lac);
                getIndexService().index(result,
                        Utils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_SECTOR_LAC, NodeTypes.SECTOR), lac);
            }
            tx.success();
            return result;
        } finally {
            tx.finish();
        }
    }

    /**
     * Get channel_group node.
     *
     * @param sector - sector node
     * @param channelNum - channel gr Number
     * @return Node
     */
    public Node getChannelGroupNode(Node sector, int channelNum) {
        Node channel = findChannelGroupNode(sector, channelNum);
        if (channel == null) {
            Transaction tx = databaseService.beginTx();
            try {
                Node result = databaseService.createNode();
                result.setProperty(INeoConstants.PROPERTY_NAME_NAME, String.valueOf(channelNum));
                sector.createRelationshipTo(result, Relations.CHANNEL_GROUP);

                tx.success();
                return result;
            } finally {
                tx.finish();
            }
        }
        return channel;
    }

    /**
     * find channel node.
     *
     * @param sector - sector node
     * @param channelNum - channel gr Number
     * @return Node
     */
    public Node findChannelGroupNode(Node sector, int channelNum) {
        final DatasetService ds = NeoServiceFactory.getInstance().getDatasetService();
        final String channelGroupName = String.valueOf(channelNum);

        Iterator<Path> itr = Traversal.description().uniqueness(Uniqueness.NONE).prune(Traversal.pruneAfterDepth(1))
                .filter(new Predicate<Path>() {

                    @Override
                    public boolean accept(Path item) {
                        return item.length() > 0 && ds.getNodeName(item.endNode()).equals(channelGroupName);
                    }
                }).relationships(Relations.CHANNEL_GROUP, Direction.OUTGOING).traverse(sector).iterator();
        return itr.hasNext() ? itr.next().endNode() : null;
    }

    /**
     * The Enum Relations.
     */
    private enum Relations implements RelationshipType {
        
        /** The CHANNE l_ group. */
        CHANNEL_GROUP, 
 /** The S y_ group. */
 SY_GROUP,
/** The CHANNE l_ trx. */
CHANNEL_TRX, 
 /** The FREQUENC y_ root. */
 FREQUENCY_ROOT, 
 /** The F r_ spectrum. */
 FR_SPECTRUM;
    }

    /**
     * Index property.
     * 
     * @param rootNode the root node
     * @param node the node
     * @param propertyName the property name
     * @param value the value
     */
    public void indexProperty(Node rootNode, Node node, String propertyName, Object value) {
        getIndexService().index(
                node,
                Utils.getLuceneIndexKeyByProperty(rootNode, propertyName, NeoServiceFactory.getInstance().getDatasetService()
                        .getNodeType(node)), value);
    }

    /**
     * Gets the tRX node.
     *
     * @param sector the sector
     * @param trxId the trx id
     * @param channelGr the channel gr
     * @return the tRX node
     */
    public NodeResult getTRXNode(Node sector, String trxId, Integer channelGr) {
        Node trxNode = findTrxNode(sector, trxId);
        boolean isCreated = trxNode == null;
        if (isCreated) {
            Transaction tx = databaseService.beginTx();
            try {
                trxNode = NeoServiceFactory.getInstance().getDatasetService().addSimpleChild(sector, NodeTypes.TRX, trxId);
                if (channelGr != null) {
                    trxNode.setProperty("group", channelGr);
                    getChannelGroupNode(sector, channelGr).createRelationshipTo(trxNode, Relations.CHANNEL_TRX);
                }
                tx.success();
            } finally {
                tx.finish();
            }
        }
        return new DatasetService.NodeResultImpl(trxNode, isCreated);
    }


    /**
     * Gets the all trx node.
     * 
     * @param sector the sector
     * @return the all trx node
     */
    public ArrayList<Node> getAllTRXNode(Node sector) {
        Iterable<Node> itr = Traversal.description().uniqueness(Uniqueness.NONE).breadthFirst().evaluator(new Evaluator() {

            @Override
            public Evaluation evaluate(Path path) {
                return Evaluation.of(path.length() == 1, path.length() == 0);
            }
        }).relationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING).traverse(sector).nodes();

        ArrayList<Node> allTrx = new ArrayList<Node>();
        for (Node it : itr) {
            allTrx.add(it);
        }
        return allTrx;
    }

    /**
     * Find trx node.
     *
     * @param sector the sector
     * @param trxId the trx id
     * @return the node
     */
    public Node findTrxNode(Node sector, final String trxId) {
        Iterator<Path> itr = Traversal.description().uniqueness(Uniqueness.NONE).depthFirst()
                .relationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING).evaluator(new Evaluator() {

                    @Override
                    public Evaluation evaluate(Path arg0) {
                        boolean continues = arg0.length() == 0;
                        boolean includes = !continues
                                && arg0.endNode().getProperty(INeoConstants.PROPERTY_NAME_NAME, "").equals(trxId);
                        return Evaluation.of(includes, continues);
                    }
                }).traverse(sector).iterator();
        return itr.hasNext() ? itr.next().endNode() : null;
    }

    /**
     * Adds the freq node.
     *
     * @param trxNode the trx node
     * @param freq the freq
     * @param prevFREQNode the prev freq node
     * @return the node
     */
    public Node addFREQNode(Node trxNode, String freq, Node prevFREQNode) {
        Node freqNode = null;
        if (trxNode != null) {
            Transaction tx = databaseService.beginTx();
            try {
                freqNode = NeoServiceFactory.getInstance().getDatasetService().addSimpleChild(trxNode, NodeTypes.FREQ, freq);
                if (prevFREQNode != null) {
                    prevFREQNode.createRelationshipTo(freqNode, NetworkRelationshipTypes.NEXT);
                }
                tx.success();
            } finally {
                tx.finish();
            }
        }
        return freqNode;
    }

    /**
     * Gets the plan node.
     *
     * @param networkRoot the network root
     * @param trx the trx
     * @param fileName the file name
     * @return the plan node
     */
    public NodeResult getPlanNode(Node networkRoot, Node trx, String fileName) {
        Node planNode = findPlanNode(trx, fileName);
        boolean isCreated = planNode == null;
        if (isCreated) {
            Transaction tx = databaseService.beginTx();
            try{
                planNode = NeoServiceFactory.getInstance().getDatasetService().createNode(NodeTypes.FREQUENCY_PLAN);
                trx.createRelationshipTo(planNode,DatasetRelationshipTypes.PLAN_ENTRY);
                getFrequencyRootNode(networkRoot, fileName).createRelationshipTo(planNode, GeoNeoRelationshipTypes.CHILD);
            tx.success();
            }finally{
                tx.finish();
            }
        }
        return new DatasetService.NodeResultImpl(planNode, isCreated);
    }

    /**
     * Find plan node.
     * 
     * @param trx the trx
     * @param fileName the file name
     * @return the node
     */
    public Node findPlanNode(Node trx, final String fileName) {
        //TODO use lucene index if too slow
        final DatasetService ds = NeoServiceFactory.getInstance().getDatasetService();

        Iterator<Path> itr = Traversal.description().depthFirst().uniqueness(Uniqueness.NONE).relationships(DatasetRelationshipTypes.PLAN_ENTRY, Direction.OUTGOING).evaluator(new Evaluator() {
            
            @Override
            public Evaluation evaluate(Path arg0) {
                boolean includes=arg0.length()==1;
                if (includes){
                    Relationship rel = arg0.endNode().getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING);
                    if (rel==null){
                        includes=false;
                    }else{
                        includes=fileName.equals(getNodeName(rel.getOtherNode(arg0.endNode())));
                    }
                }
                return Evaluation.of(includes, arg0.length()==0);
            }
        }).traverse(trx).iterator();
        return itr.hasNext() ? itr.next().endNode() : null;
    }
    
    /**
     * Find plan node.
     *
     * @param trx the trx
     * @param frModelRoot the fr model root
     * @return the node
     */
    public Node findPlanNode(Node trx, final Node frModelRoot) {
        //TODO use lucene index if too slow
        final DatasetService ds = NeoServiceFactory.getInstance().getDatasetService();

        Iterator<Path> itr = Traversal.description().depthFirst().uniqueness(Uniqueness.NONE).relationships(DatasetRelationshipTypes.PLAN_ENTRY, Direction.OUTGOING).evaluator(new Evaluator() {
            
            @Override
            public Evaluation evaluate(Path arg0) {
                boolean includes=arg0.length()==1;
                if (includes){
                    Relationship rel = arg0.endNode().getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING);
                    if (rel==null){
                        includes=false;
                    }else{
                        includes=frModelRoot.equals(rel.getOtherNode(arg0.endNode()));
                    }
                }
                return Evaluation.of(includes, arg0.length()==0);
            }
        }).traverse(trx).iterator();
        return itr.hasNext() ? itr.next().endNode() : null;
    }

    /**
     * Find indexed node by property.
     * 
     * @param rootNetwork the root network
     * @param type the type
     * @param propertyName the property name
     * @param propertyValue the property value
     * @return the iterable of necessary nodes
     */
    public Iterable<Node> findIndexedNodeByProperty(Node rootNetwork, NodeTypes type, String propertyName, Object propertyValue) {
        return getIndexService().getNodes(Utils.getLuceneIndexKeyByProperty(rootNetwork, propertyName, type), propertyValue);
    }

    /**
     * Find sector by plan.
     * 
     * @param rootNetwork the root network
     * @param bsic the bsic
     * @param arfcn the arfcn
     * @param planName the plan name - if null the search will be execute only with original plan
     * @return the iterable of sectors
     */
    public Iterable<Node> findSectorByPlan(Node rootNetwork, String bsic, int arfcn, final String planName) {
        List<Node> result = new LinkedList<Node>();
        for (Node sector : findIndexedNodeByProperty(rootNetwork, NodeTypes.SECTOR, "BSIC", bsic)) {
            Node trx = findTrxNode(sector, "0");
            if (trx != null) {
                final DatasetService ds = NeoServiceFactory.getInstance().getDatasetService();
                TraversalDescription td;
                if (StringUtils.isNotEmpty(planName)) {
                    td = Traversal.description().uniqueness(Uniqueness.NONE).depthFirst().prune(new PruneEvaluator() {

                        @Override
                        public boolean pruneAfter(Path position) {
                            if (position.length() == 1) {
                                return position.lastRelationship().isType(GeoNeoRelationshipTypes.NEXT);
                            }
                            return false;
                        }
                    }).filter(new Predicate<Path>() {

                        @Override
                        public boolean accept(Path item) {
                            if (item.length() == 1 && item.lastRelationship().isType(GeoNeoRelationshipTypes.NEXT)) {
                                return false;
                            }
                            return planName.equals(ds.getNodeName(item.endNode()));
                        }
                    }).relationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)
                            .relationships(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
                } else {
                    td = Traversal.description().uniqueness(Uniqueness.NONE).depthFirst().prune(Traversal.pruneAfterDepth(1))
                            .filter(Traversal.returnAllButStartNode())
                            .relationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING);
                }
                Iterable<Node> plans = td.traverse(trx).nodes();
                for (Node plan : plans) {
                    int[] arfcnArr = (int[])plan.getProperty("arfcn", null);
                    if (arfcnArr != null) {
                        for (int val : arfcnArr) {
                            if (val == arfcn) {
                                result.add(sector);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Gets the all node2 node filter.
     *
     * @param projecNode the projec node
     * @return the all node2 node filter
     */
    public INode2NodeFilter getAllNode2NodeFilter(final Node projecNode) {
        return new INode2NodeFilter() {

            @Override
            public Iterable<NodeToNodeRelationModel> getModels() {
                List<NodeToNodeRelationModel> result = new ArrayList<NodeToNodeRelationModel>();
                if (projecNode != null) {
                    Traverser networks = getRoots(projecNode, new Evaluator() {

                        @Override
                        public Evaluation evaluate(Path paramPath) {
                            return Evaluation.of(NodeTypes.NETWORK.checkNode(paramPath.endNode()), true);
                        }
                    });
                    NodeToNodeRelationService n2n = NeoServiceFactory.getInstance().getNodeToNodeRelationService();

                    for (Node root : networks.nodes()) {
                        result.addAll(n2n.findAllNode2NodeRoot(root));
                    }

                }
                return result;
            }

            @Override
            public Iterable<Node> getFilteredServNodes(NodeToNodeRelationModel models) {
                // 2 traverser not work!
                // this is get ALL modeks filter - not necessary check exist models in getModels()!
                List<Node> result = new LinkedList<Node>();
                for (Node node : models.getServTraverser(null).nodes()) {
                    result.add(node);
                }
                return result;
            }

            @Override
            public Iterable<Node> getFilteredNeighNodes(NodeToNodeRelationModel models) {
                LinkedHashSet<Node> result=new LinkedHashSet<Node>();
                for (Node proxy : models.getNeighTraverser(null).nodes()) {
                        result.add(proxy);
                }                          
                return result;
            }
        };
    }

    /**
     * Move sector to correct site.
     * 
     * @param site the site
     * @param sector the sector
//     * @return true, if old site was deleted (do not have others sectors)
     */
    public void moveSectorToCorrectSite(Node site, Node sector) {
        Relationship rel = sector.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING);
        Node parent = rel == null ? null : rel.getOtherNode(sector);
        if (parent != null && site.equals(parent)) {
            return;
        }
        Transaction tx = databaseService.beginTx();
        try {
            if (parent != null) {
                updateLocation(parent,site);
                rel.delete();
            }
            site.createRelationshipTo(sector, GeoNeoRelationshipTypes.CHILD);
            tx.success();
            return;
        } finally {
            tx.finish();
        }
    }

    /**
     * Update location.
     *
     * @param parent the parent
     * @param site the site
     * @return true, if successful
     */
    private boolean updateLocation(Node parent, Node site) {
        try {
            //TODO debug
            if (site.hasRelationship(NeoIndexRelationshipTypes.IND_CHILD,Direction.INCOMING)){
                return false;
            }
            if (!site.hasProperty(INeoConstants.PROPERTY_LAT_NAME)&&!site.hasProperty(INeoConstants.PROPERTY_LON_NAME)&&parent.hasProperty(INeoConstants.PROPERTY_LAT_NAME)&&parent.hasProperty(INeoConstants.PROPERTY_LON_NAME)){
                site.setProperty(INeoConstants.PROPERTY_LAT_NAME, parent.getProperty(INeoConstants.PROPERTY_LAT_NAME));
                site.setProperty(INeoConstants.PROPERTY_LON_NAME, parent.getProperty(INeoConstants.PROPERTY_LON_NAME));
                 Iterator<Relationship> rel = parent.getRelationships(NeoIndexRelationshipTypes.IND_CHILD,Direction.INCOMING).iterator();
                if (rel.hasNext()){
                    rel.next().getOtherNode(parent).createRelationshipTo(site, NeoIndexRelationshipTypes.IND_CHILD);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            // TODO Handle Exception
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }
    
    /**
     * Find all frq root.
     *
     * @param networkNode the network node
     * @param additionalEvaluation the additional evaluation
     * @return the traverser
     */
    public Traverser findAllFrqRoot(Node networkNode, Evaluator additionalEvaluation){
        TraversalDescription tr = Traversal.description().depthFirst().uniqueness(Uniqueness.NONE).relationships(Relations.FREQUENCY_ROOT,Direction.OUTGOING).evaluator(new Evaluator() {
            
            @Override
            public Evaluation evaluate(Path arg0) {
                return Evaluation.of(arg0.length()==1, arg0.length()==0);
            }
        });
        if (additionalEvaluation!=null){
            tr = tr.evaluator(additionalEvaluation);
        }
        return tr.traverse(networkNode);
    }
    /**
     * Find site from sector.
     * 
     * @param sector the sector
     * @return the node
     */
    public Node findSiteFromSector(Node sector) {
        Relationship rel = sector.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING);
        return rel == null ? null : rel.getOtherNode(sector);
    }

    /**
     * Find frequency root node.
     *
     * @param networkRoot the network root
     * @param modelName the model name
     * @return the node
     */
    public Node findFrequencyRootNode(Node networkRoot, final String modelName) {
        Iterator<Node> iter = findAllFrqRoot(networkRoot, new Evaluator() {
            
            @Override
            public Evaluation evaluate(Path arg0) {
                return Evaluation.ofIncludes(modelName.equals(getNodeName(arg0.endNode())));
            }
        }).nodes().iterator();
        return iter.hasNext()?iter.next():null;
        
    }

    /**
     * Gets the frequency root node.
     *
     * @param networkRoot the network root
     * @param modelName the model name
     * @param time the time
     * @param domain the domain
     * @return the frequency root node
     */
    public Node getFrequencyRootNode(Node networkRoot, String modelName, String time, String domain) {
        Node result=findFrequencyRootNode(networkRoot, modelName);
        if (result==null){
            result=createFrequencyRootNode(networkRoot,modelName,time,domain);
        }
        return result;
    }
    
    /**
     * Gets the frequency root node.
     *
     * @param networkRoot the network root
     * @param modelName the model name
     * @return the frequency root node
     */
    public Node getFrequencyRootNode(Node networkRoot, String modelName) {
        Node result=findFrequencyRootNode(networkRoot, modelName);
        if (result==null){
            result=createFrequencyRootNode(networkRoot,modelName);
        }
        return result;
    }

    /**
     * Creates the frequency root node.
     *
     * @param networkRoot the network root
     * @param modelName the model name
     * @param time the time
     * @param domain the domain
     * @return the node
     */
    private Node createFrequencyRootNode(Node networkRoot, String modelName, String time, final String domain) {
        Transaction tx = databaseService.beginTx();
        try {
           
            Node result=super.createNode(NodeTypes.FREQUENCY_ROOT, modelName,time,domain);
            networkRoot.createRelationshipTo(result, Relations.FREQUENCY_ROOT);
            // TODO Tsinkel A is it correct place for create link with domain??? Maybe wrong place
            // and strange logic...
            TraversalDescription td = Traversal.description()
            .breadthFirst()
            .relationships(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)
            .evaluator(Evaluators.excludeStartPosition())
            .evaluator(new Evaluator(){
                 public Evaluation evaluate(Path arg0) {
                     
                     boolean includes = false;
                     boolean continues;
                     
                     if (arg0.endNode().getProperty(INeoConstants.PROPERTY_NAME_NAME, "").equals(domain)
                             &&arg0.endNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(NodeTypes.AFP_DOMAIN.getId())){
                         includes = true;
                     }
                     continues =(arg0.length()<2)&&(!includes);
                     return Evaluation.of(includes, continues);
                 }
            });
            int count = 0;
            Node domainNode = null;
            for (Node node : td.traverse(networkRoot).nodes()){
                count++;
                domainNode = node;
            }
            if (count == 1){
                result.createRelationshipTo(domainNode, NetworkRelationshipTypes.DOMAIN);
            }
            tx.success();
            return result;
        } finally {
            tx.finish();
        }
   
    }
    
    /**
     * Creates the frequency root node.
     *
     * @param networkRoot the network root
     * @param modelName the model name
     * @return the node
     */
    private Node createFrequencyRootNode(Node networkRoot, String modelName) {
        Transaction tx = databaseService.beginTx();
        try {
            Node result=super.createNode(NodeTypes.FREQUENCY_ROOT, modelName);
            networkRoot.createRelationshipTo(result, Relations.FREQUENCY_ROOT);
            tx.success();
            return result;
        } finally {
            tx.finish();
        }
   
    }

    /**
     * Gets the plan node.
     *
     * @param rootNode the root node
     * @param trx the trx
     * @return the plan node
     */
    public NodeResult getPlanNode(Node rootNode, Node trx) {
        Node planNode = findPlanNode(trx, rootNode);
        boolean isCreated = planNode == null;
        if (isCreated) {
            Transaction tx = databaseService.beginTx();
            try{
                Node gr=null;
                if (ObjectUtils.equals(getHopType(trx),2)&&!(Boolean)trx.getProperty("bcch",false)){
                    gr = getSYGroup(findSectorOfTRX(trx)).getOriginalNode();
                    planNode=findPlanNode(gr, rootNode);
                }
                if (planNode==null){
                    planNode = NeoServiceFactory.getInstance().getDatasetService().createNode(NodeTypes.FREQUENCY_PLAN);
                    rootNode.createRelationshipTo(planNode, GeoNeoRelationshipTypes.CHILD);
                    if (gr!=null){
                        gr.createRelationshipTo(planNode,DatasetRelationshipTypes.PLAN_ENTRY);
                    }
                }else{
                    isCreated=false;
                }
                trx.createRelationshipTo(planNode,DatasetRelationshipTypes.PLAN_ENTRY);
                tx.success();
            }finally{
                tx.finish();
            }
        }
        return new DatasetService.NodeResultImpl(planNode, isCreated);
    }




    /**
     * Gets the sY group.
     *
     * @param sector the sector
     * @return the sY group
     */
    public WrNode getSYGroup(Node sector) {
        Node group=findSYGroup(sector);
        if (group!=null){
            return new WrNode(group);
        }
        Transaction tx = databaseService.beginTx();
        try{
            group = createNode(NodeTypes.SY_GROUP);
            sector.createRelationshipTo(group,Relations.SY_GROUP);
            tx.success();
            return new WrNode(group,true);
        }finally{
            tx.finish();
        }     
    }


    /**
     * Find sy group.
     *
     * @param sector the sector
     * @return the node
     */
    public Node findSYGroup(Node sector) {
        Relationship rel = sector.getSingleRelationship(Relations.SY_GROUP, Direction.OUTGOING);
        return rel==null?null:rel.getOtherNode(sector);
    }

    /**
     * Find sector of trx.
     *
     * @param trx the trx
     * @return the node
     */
    public Node findSectorOfTRX(Node trx) {
        return trx.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(trx);
    }
    
    /**
     * Find sector of sy group.
     *
     * @param trx the trx
     * @return the node
     */
    public Node findSectorOfSyGroup(Node trx) {
        return trx.getSingleRelationship(Relations.SY_GROUP, Direction.INCOMING).getOtherNode(trx);
    }

    /**
     * Gets the hop type.
     *
     * @param trx the trx
     * @return the hop type
     */
    public Integer getHopType(Node trx) {
        return (Integer)trx.getProperty("hopping_type", 0);
    }

    /**
     * Find all node by type.
     *
     * @param network the network
     * @param type the type
     * @return the iterable
     */
    public Iterable<Node> findAllNodeByType(Node network,final INodeType type) {
        return Traversal.description().depthFirst().relationships(GeoNeoRelationshipTypes.CHILD,Direction.OUTGOING).uniqueness(Uniqueness.NONE).evaluator(new Evaluator() {
            
            @Override
            public Evaluation evaluate(Path arg0) {
                boolean includes=type.equals(getNodeType(arg0.endNode()));
                return Evaluation.of(includes, !includes);
            }
        }).traverse(network).nodes();
    }

    /**
     * Gets the frequency spectrum root node.
     *
     * @param rootNode the root node
     * @return the frequency spectrum root node
     */
    public Node getFrequencySpectrumRootNode(Node rootNode) {
       Relationship rel=rootNode.getSingleRelationship(Relations.FR_SPECTRUM, Direction.OUTGOING);
       if (rel!=null){
           return rel.getOtherNode(rootNode);
       }
       Transaction tx = databaseService.beginTx();
       try{
           Node result= super.createNode(NodeTypes.FR_SPECTRUM, "illegal frequency");
           rootNode.createRelationshipTo(result, Relations.FR_SPECTRUM);
           tx.success();
           return result;
       }finally{
           tx.finish();
       }
    }

    /**
     * Creates the fr spectrim node.
     *
     * @param rootNode the root node
     * @param frequency the frequency
     * @return the node
     */
    public Node createFrSpectrimNode(Node rootNode, int frequency) {
        Transaction tx = databaseService.beginTx();
        try{
            Node result=super.createNode(NodeTypes.FREQ, String.valueOf(frequency));
            result.setProperty(FREQUENCY, frequency);
            rootNode.createRelationshipTo(result, GeoNeoRelationshipTypes.CHILD);
            tx.success();
            return result;
        }finally{
            tx.finish();
        }
    }

    /**
     * Gets the root selection node.
     *
     * @param parentNode the parent node
     * @param selectionListName the selection list name
     * @return the root selection node
     */
    public Node getRootSelectionNode(Node parentNode, String selectionListName) {
        Node result = findRootSelectionNode(parentNode, selectionListName);
        if (result == null) {
            result = createRootSelectionNode(parentNode, selectionListName);
        }
        
        return result;
    }
    
    /**
     * Find root selection node.
     *
     * @param parentNode the parent node
     * @param selectionListName the selection list name
     * @return the node
     */
    public Node findRootSelectionNode(Node parentNode, String selectionListName) {
        Iterator<Node> resultIterator = getNetworkSelectionTraversalDescription(new NameFilter(selectionListName, false)).
                                        traverse(parentNode).nodes().iterator();
        if (resultIterator.hasNext()) {
            return resultIterator.next();
        }
        return null;
    }
    
    /**
     * Gets the all root selection nodes.
     *
     * @param parentNode the parent node
     * @return the all root selection nodes
     */
    public Iterable<Node> getAllRootSelectionNodes(Node parentNode) {
        return getNetworkSelectionTraversalDescription(null).traverse(parentNode).nodes();
    }
    
    /**
     * Creates the root selection node.
     *
     * @param parentNode the parent node
     * @param selectionListName the selection list name
     * @return the node
     */
    private Node createRootSelectionNode(Node parentNode, String selectionListName) {
        Transaction tx = databaseService.beginTx();
        try {
            Node selNode = createNode(NodeTypes.SELECTION_LIST, selectionListName);
            parentNode.createRelationshipTo(selNode, NetworkRelationshipTypes.SELECTION);
            tx.success();
            return selNode;
        } finally {
            tx.finish();
        }
    }
    
    /**
     * Gets the network selection traversal description.
     *
     * @param filter the filter
     * @return the network selection traversal description
     */
    private TraversalDescription getNetworkSelectionTraversalDescription(Evaluator filter) {
        TraversalDescription initialDescrption = Traversal.description().breadthFirst().
                         relationships(NetworkRelationshipTypes.SELECTION, Direction.OUTGOING).
                         evaluator(new Evaluator() {
                    
                             @Override
                             public Evaluation evaluate(Path arg0) {
                                 if (arg0.length() == 1) {
                                     if (NodeTypes.SELECTION_LIST.equals(getNodeType(arg0.endNode()))) {
                                         return Evaluation.INCLUDE_AND_PRUNE;
                                     }
                                     return Evaluation.EXCLUDE_AND_PRUNE;
                                 }
                                 return Evaluation.EXCLUDE_AND_PRUNE;
                             }
                         });
        if (filter != null) {
            return initialDescrption.evaluator(filter);
        }
        
        return initialDescrption;
    }
    
    /**
     * Adds the to selection.
     *
     * @param rootSelectionNode the root selection node
     * @param sectorNode the sector node
     * @param indexKey the index key
     * @return true, if successful
     */
    public boolean addToSelection(Node rootSelectionNode, Node sectorNode, String indexKey) {
        String sectorName = getNodeName(sectorNode);
        Relationship selectionRel = databaseService.index().forRelationships(indexKey).get(INeoConstants.PROPERTY_NAME_NAME, sectorName).getSingle();
        
        if (selectionRel != null) {
            //selection relationship already exists
            return false;
        }
        
        Transaction tx = databaseService.beginTx();
        try {
            selectionRel = rootSelectionNode.createRelationshipTo(sectorNode, NetworkRelationshipTypes.SELECTED);
        
            databaseService.index().forRelationships(indexKey).add(selectionRel, INeoConstants.PROPERTY_NAME_NAME, sectorName);
            tx.success();            
        }
        finally {
            tx.finish();
        }
        
        return true;
    }
    
    /**
     * Gets the all selected nodes.
     *
     * @param rootSelectionNode the root selection node
     * @return the all selected nodes
     */
    public Iterator<Node> getAllSelectedNodes(Node rootSelectionNode) {
        return Traversal.description().breadthFirst().
               relationships(NetworkRelationshipTypes.SELECTED, Direction.OUTGOING).
               traverse(rootSelectionNode).nodes().iterator();
    }
    
    /**
     * Gets the network element traversal.
     *
     * @param filter the filter
     * @param nodeTypes the node types
     * @return the network element traversal
     */
    public TraversalDescription getNetworkElementTraversal(Evaluator filter, INodeType ... nodeTypes) {
        return getChildrenTraversal(new MultiNodeTypeFilter(true, nodeTypes), filter);
    }
    
    /**
     * Gets the selection list element traversal.
     *
     * @param filter the filter
     * @param nodeTypes the node types
     * @return the selection list element traversal
     */
    public TraversalDescription getSelectionListElementTraversal(Evaluator filter, INodeType ... nodeTypes) {
        return getNetworkElementTraversal(filter, nodeTypes).relationships(NetworkRelationshipTypes.SELECTED, Direction.OUTGOING);
    }
    
    /**
     * Gets the network element traversal.
     *
     * @param filters the filters
     * @return the network element traversal
     */
    private TraversalDescription getNetworkElementTraversal(Evaluator ... filters) {
        TraversalDescription description = Traversal.description().depthFirst().relationships(NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
        
        if (filters != null) {
            for (Evaluator singleFilter : filters) {
                if (singleFilter != null) {
                    description = description.evaluator(singleFilter);
                }
            }
            return description;
        }
        
        return description;
    }

    /**
     * Have chanel group.
     * 
     * @param sectorNode the sector node
     * @return true, if successful
     */
    public boolean haveChanelGroup(Node sectorNode) {
        return sectorNode.hasRelationship(Relations.CHANNEL_GROUP, Direction.OUTGOING);
    }

    /**
     * Gets the channel groups.
     * 
     * @param sectorNode the sector node
     * @return the channel groups
     */
    public Iterable<Relationship> getChannelGroups(Node sectorNode) {
        return sectorNode.getRelationships(Relations.CHANNEL_GROUP, Direction.OUTGOING);
    }


    /**
     * Checks if is group have trx.
     * 
     * @param group the group
     * @return true, if is group have trx
     */
    public boolean isGroupHaveTrx(Node group) {
        return group.hasRelationship(Relations.CHANNEL_TRX, Direction.OUTGOING);
    }

    /**
     * Find first id.
     *
     * @param sector the sector
     * @return the int
     */
    public int findFirstID(Node sector) {
        int maxId = -1;
        for (Node trx : getAllTRXNode(sector)) {
            maxId = Math.max(maxId, Integer.parseInt(getNodeName(trx)));
        }
        return 1 + maxId;
    }

    /**
     * Attach single source.
     *
     * @param rootNode the root node
     * @param sourceRootNode the source root node
     */
    public void attachSingleSource(Node rootNode, Node sourceRootNode) {
        Transaction tx = databaseService.beginTx();

        try {
            Iterable<Relationship> rels = rootNode.getRelationships(GeoNeoRelationshipTypes.SOURCE, Direction.OUTGOING);
            for (Relationship rel : rels) {
                rel.delete();
            }
            rootNode.createRelationshipTo(sourceRootNode, GeoNeoRelationshipTypes.SOURCE);
            tx.success();
        } finally {
            tx.finish();
        }
    }


    /**
     * Gets the single source.
     * 
     * @param rootNode the root node
     * @return the single source
     */
    public Node getSingleSource(Node rootNode) {
        Relationship rel = rootNode.getSingleRelationship(GeoNeoRelationshipTypes.SOURCE, Direction.OUTGOING);
        return rel == null ? null : rel.getOtherNode(rootNode);
    }

    /**
     * Find all frequency plan with source.
     * 
     * @param projectNode the project node
     * @return the map ()
     */
    public Map<NetworkModel, Set<FrequencyPlanModel>> findAllFrequencyPlanWithSource(Node projectNode) {
        Map<NetworkModel, Set<FrequencyPlanModel>> result = new HashMap<NetworkModel, Set<FrequencyPlanModel>>();
        for (Path path : getRoots(projectNode, new Evaluator() {

            @Override
            public Evaluation evaluate(Path arg0) {
                return Evaluation.ofIncludes(NodeTypes.NETWORK.checkNode(arg0.endNode()));
            }
        })) {
            NetworkModel model = new NetworkModel(path.endNode());
            Set<FrequencyPlanModel> models = model.findAllFrqModel();
            Iterator<FrequencyPlanModel> it = models.iterator();
            while (it.hasNext()) {
                FrequencyPlanModel type = it.next();
                if (type.getSingleSource() == null) {
                    it.remove();
                }
            }
            if (!models.isEmpty()) {
                result.put(model, models);
            }
        }
        return result;
    }
    
    /**
     * Gets the frequency plan nodes.
     *
     * @param rootFrequencyPlanNode the root frequency plan node
     * @return the frequency plan nodes
     */
    public Iterable<Node> getFrequencyPlanNodes(Node rootFrequencyPlanNode) {
        TraversalDescription description = Traversal.description().breadthFirst().relationships(DatasetRelationshipTypes.CHILD,
                Direction.OUTGOING).evaluator(new Evaluator() {

            @Override
            public Evaluation evaluate(Path arg0) {
                switch (arg0.length()) {
                case 0:
                    return Evaluation.EXCLUDE_AND_CONTINUE;
                case 1:
                    return Evaluation.INCLUDE_AND_PRUNE;
                default:
                    return Evaluation.EXCLUDE_AND_PRUNE;
                }
            }
        });

        return description.traverse(rootFrequencyPlanNode).nodes();
    }


    /**
     * Find sector of plan.
     *
     * @param planNode the plan node
     * @return the node
     */
    public Node findSectorOfPlan(Node planNode) {
        Relationship rel = planNode.getRelationships(DatasetRelationshipTypes.PLAN_ENTRY, Direction.INCOMING).iterator().next();
        Node trxGr=rel.getOtherNode(planNode);
        rel= trxGr.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING);
        if (rel==null){
            rel=trxGr.getSingleRelationship(Relations.SY_GROUP, Direction.INCOMING);
        }
        return rel.getOtherNode(trxGr);
    }

    /**
     * Gets the trx count of sy group.
     *
     * @param gr the gr
     * @return the trx count of sy group
     */
    public int getTrxCountOfSyGroup(Node gr) {
        //TODO store in property of group...
        return getTrxOfSyGroup(gr).size();
    }
    
    /**
     * Gets the trx of sy group.
     *
     * @param gr the gr
     * @return the trx of sy group
     */
    public Set<Node> getTrxOfSyGroup(Node gr) {
        //TODO store in property of group...
        Set<Node> result=new HashSet<Node>();
        Node sector = findSectorOfSyGroup(gr);
        for (Relationship rel:sector.getRelationships(GeoNeoRelationshipTypes.CHILD,Direction.OUTGOING)){
            Node trx=rel.getOtherNode(sector);
            if (getHopType(trx)==2&&!(Boolean)trx.getProperty("bcch",false)){
                result.add(trx);
            }
        }
        return result;
    }
    
    public List<Node> findAllNetworkNodes() {
        return findAllNetworkNodes(databaseService.getReferenceNode());
    }

    public List<Node> findAllNetworkNodes(Node rootNode) {
        ArrayList<Node> result = new ArrayList<Node>();
        
        for (Node node : getAllRootTraverser(Evaluators.toDepth(2), new MultiNodeTypeFilter(true, NodeTypes.NETWORK)).
                         traverse(rootNode).
                         nodes()) {
            result.add(node);
        }
        
        return result;
    }


    /**
     * Gets the total trx of plan.
     *
     * @param plan the plan
     * @return the total trx of plan
     */
    public int getTotalTrxOfPlan(Node plan) {
        int relCount=0;;
        for (Relationship rel:plan.getRelationships(DatasetRelationshipTypes.PLAN_ENTRY, Direction.INCOMING)){
            relCount++;  
        }
        return relCount;
    }

    /**
     * Gets the single trx.
     *
     * @param plan the plan
     * @return the single trx
     */
    public Node getSingleTrx(Node plan) {
        return plan.getSingleRelationship(DatasetRelationshipTypes.PLAN_ENTRY, Direction.INCOMING).getOtherNode(plan);
    }


    /**
     * Checks if is bCCHTRX.
     *
     * @param trx the trx
     * @return true, if is bCCHTRX
     */
    public boolean isBCCHTRX(Node trx) {
        return (Boolean)trx.getProperty("bcch",false);
    }

}
