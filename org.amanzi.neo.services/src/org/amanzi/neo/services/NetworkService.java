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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.services.enums.DatasetRelationshipTypes;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.indexes.PropertyIndex.NeoIndexRelationshipTypes;
import org.amanzi.neo.services.node2node.INode2NodeFilter;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationService;
import org.amanzi.neo.services.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.PruneEvaluator;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.helpers.Predicate;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

/**
 * <p>
 * Network service
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkService extends DatasetService {

    public static final String FREQUENCY = "frequency";
    
    private class NameFilter implements Evaluator {
        
        private String name;
        
        private boolean toContinue;
        
        public NameFilter(String name, boolean toContinue) {
            this.name = name;
            this.toContinue = toContinue;
        }

        @Override
        public Evaluation evaluate(Path arg0) {
            boolean include = name.equals(getNodeName(arg0.endNode()));
            return Evaluation.of(include, toContinue);
        }
    }
    
    private class MultiNodeTypeFilter implements Evaluator {
        
        private INodeType[] nodeTypes;
        
        private boolean toContinue;
        
        public MultiNodeTypeFilter(boolean toContinue, INodeType ... nodeTypes) {
            this.nodeTypes = nodeTypes;
            this.toContinue = toContinue;
        }

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
    
    public NetworkService() {
        super();        
    }

    /**
     * Get BSC node with necessary name if bsc node not exist it will be created
     * 
     * @param networkNode - network root node
     * @param bscName -bsc name
     * @param parentNode - parent node
     * @return
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
     * Find BSC node by name
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
     * Get site node with necessary name if site node not exist it will be created
     * 
     * @param networkNode - network root node
     * @param siteName -site name
     * @param parentNode - parent node
     * @return
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
     * Find Site node by name
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

    public Node findSector(Node rootNode, String name, boolean returnFirstElement) {
        return super.findSector(rootNode, null, null, name, returnFirstElement);
    }

    /**
     * Create sector
     * 
     * @param networkNode - network node
     * @param site - parent node
     * @param sectorName sector name
     * @param ci - ci
     * @param lac - lac
     * @return
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
     * Get channel_group node
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
     * find channel node
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

    private enum Relations implements RelationshipType {
        CHANNEL_GROUP, CHANNEL_TRX, FREQUENCY_ROOT, FR_SPECTRUM;
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
     * @param sector
     * @param trxId
     * @param channelGr
     * @return
     */
    public NodeResult getTRXNode(Node sector, String trxId, Integer channelGr) {
        Node trxNode = findTrxNode(sector, trxId, channelGr);
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
     * @param sector
     * @return
     */
    public ArrayList<Node> getAllTRXNode(Node sector) {
        Iterable<Node> itr = Traversal.description().uniqueness(Uniqueness.NONE).breadthFirst().prune(Traversal.pruneAfterDepth(1))
                .relationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING).traverse(sector).nodes();

        ArrayList<Node> allTrx = new ArrayList<Node>();
        for (Node it : itr) {
            allTrx.add(it);
        }
        return allTrx;
    }

    /**
     * @param sector
     * @param trxId
     * @param channelGr
     * @return
     */
    public Node findTrxNode(Node sector, final String trxId, final Integer channelGr) {
        Iterator<Path> itr = Traversal.description().uniqueness(Uniqueness.NONE).depthFirst()
                .relationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING).evaluator(new Evaluator() {

                    @Override
                    public Evaluation evaluate(Path arg0) {
                        boolean continues = arg0.length() == 0;
                        boolean includes = !continues
                                && arg0.endNode().getProperty(INeoConstants.PROPERTY_NAME_NAME, "").equals(trxId)
                                && (channelGr == null || arg0.endNode().getProperty("group", -1).equals(channelGr));
                        return Evaluation.of(includes, continues);
                    }
                }).traverse(sector).iterator();
        return itr.hasNext() ? itr.next().endNode() : null;
    }

    /**
     * @param sector
     * @param trxId
     * @param channelGr
     * @return
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
     * @param trx the trx
     * @param trx 
     * @param fileName the file name
     * @return the plan node
     */
    public NodeResult getPlanNode(Node networkRoot, Node trx, String fileName) {
        Node planNode = findPlanNode(trx, fileName);
        boolean isCreated = planNode == null;
        if (isCreated) {
            Transaction tx = databaseService.beginTx();
            try{
                planNode = NeoServiceFactory.getInstance().getDatasetService().createNode(NodeTypes.FREQUENCY_PLAN, "");
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
     * @param fileName the file name
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
            Node trx = findTrxNode(sector, "0", 0);
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
    public Traverser findAllFrqRoot(Node networkNode, Evaluator additionalEvaluation){
        TraversalDescription tr = Traversal.description().depthFirst().uniqueness(Uniqueness.NONE).relationships(Relations.FREQUENCY_ROOT,Direction.OUTGOING).evaluator(new Evaluator() {
            
            @Override
            public Evaluation evaluate(Path arg0) {
                return Evaluation.of(arg0.length()==1, arg0.length()==0);
            }
        });
        if (additionalEvaluation!=null){
            tr.evaluator(additionalEvaluation);
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

    public Node findFrequencyRootNode(Node networkRoot, final String modelName) {
        Iterator<Node> iter = findAllFrqRoot(networkRoot, new Evaluator() {
            
            @Override
            public Evaluation evaluate(Path arg0) {
                return Evaluation.ofIncludes(modelName.equals(getNodeName(arg0.endNode())));
            }
        }).nodes().iterator();
        return iter.hasNext()?iter.next():null;
        
    }

    public Node getFrequencyRootNode(Node networkRoot, String modelName) {
        Node result=findFrequencyRootNode(networkRoot, modelName);
        if (result==null){
            result=createFrequencyRootNode(networkRoot,modelName);
        }
        return result;
    }

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

    public NodeResult getPlanNode(Node rootNode, Node trx) {
        Node planNode = findPlanNode(trx, rootNode);
        boolean isCreated = planNode == null;
        if (isCreated) {
            Transaction tx = databaseService.beginTx();
            try{
                planNode = NeoServiceFactory.getInstance().getDatasetService().createNode(NodeTypes.FREQUENCY_PLAN, "");
                trx.createRelationshipTo(planNode,DatasetRelationshipTypes.PLAN_ENTRY);
                rootNode.createRelationshipTo(planNode, GeoNeoRelationshipTypes.CHILD);
            tx.success();
            }finally{
                tx.finish();
            }
        }
        return new DatasetService.NodeResultImpl(planNode, isCreated);
    }

    public Iterable<Node> findAllNodeByType(Node network,final INodeType type) {
        return Traversal.description().depthFirst().relationships(GeoNeoRelationshipTypes.CHILD,Direction.OUTGOING).uniqueness(Uniqueness.NONE).evaluator(new Evaluator() {
            
            @Override
            public Evaluation evaluate(Path arg0) {
                boolean includes=type.equals(getNodeType(arg0.endNode()));
                return Evaluation.of(includes, !includes);
            }
        }).traverse(network).nodes();
    }

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

    public Node getRootSelectionNode(Node parentNode, String selectionListName) {
        Node result = findRootSelectionNode(parentNode, selectionListName);
        if (result == null) {
            result = createRootSelectionNode(parentNode, selectionListName);
        }
        
        return result;
    }
    
    public Node findRootSelectionNode(Node parentNode, String selectionListName) {
        Iterator<Node> resultIterator = getNetworkSelectionTraversalDescription(new NameFilter(selectionListName, false)).
                                        traverse(parentNode).nodes().iterator();
        if (resultIterator.hasNext()) {
            return resultIterator.next();
        }
        return null;
    }
    
    public Iterable<Node> getAllRootSelectionNodes(Node parentNode) {
        return getNetworkSelectionTraversalDescription(null).traverse(parentNode).nodes();
    }
    
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
    
    public Iterator<Node> getAllSelectedNodes(Node rootSelectionNode) {
        return Traversal.description().breadthFirst().
               relationships(NetworkRelationshipTypes.SELECTED, Direction.OUTGOING).
               traverse(rootSelectionNode).nodes().iterator();
    }
    
    public TraversalDescription getNetworkElementTraversal(Evaluator filter, INodeType ... nodeTypes) {
        return getNetworkElementTraversal(new MultiNodeTypeFilter(true, nodeTypes), filter);
    }
    
    public TraversalDescription getSelectionListElementTraversal(Evaluator filter, INodeType ... nodeTypes) {
        return getNetworkElementTraversal(filter, nodeTypes).relationships(NetworkRelationshipTypes.SELECTED, Direction.OUTGOING);
    }
    
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
}
