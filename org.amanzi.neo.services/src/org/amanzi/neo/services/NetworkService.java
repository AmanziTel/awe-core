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
import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.PruneEvaluator;
import org.neo4j.graphdb.traversal.TraversalDescription;
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
public class NetworkService extends AbstractService {
    
    private DatasetService datasetService;
    
    public NetworkService() {
        super();
        datasetService = NeoServiceFactory.getInstance().getDatasetService();
    }

    /**
     * Get BSC node with necessary name if bsc node not exist it will be created
     * 
     * @param networkNode - network root node
     * @param bscName -bsc name
     * @param parentNode - parent node
     * @return
     */
    public Node getBscNode(Node networkNode, String bscName, Node parentNode) {
        Node result = findBscNode(networkNode, bscName);
        if (result == null) {
            Transaction tx = databaseService.beginTx();
            try {
                result = NeoServiceFactory.getInstance().getDatasetService().addSimpleChild(parentNode, NodeTypes.BSC, bscName);
                getIndexService().index(result, Utils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.BSC), bscName);

                tx.success();
            } finally {
                tx.finish();
            }

        }
        return result;
    }

    /**
     * Find BSC node by name
     * 
     * @param networkNode network root node
     * @param bscName bsc name
     * @return bsc node or null if node not found;
     */
    public Node findBscNode(Node networkNode, String bscName) {
        return getIndexService().getSingleNode(Utils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.BSC), bscName);
    }

    /**
     * Get site node with necessary name if site node not exist it will be created
     * 
     * @param networkNode - network root node
     * @param siteName -site name
     * @param parentNode - parent node
     * @return
     */
    public Node getSite(Node networkNode, String siteName, Node parentNode) {
        Node result = findSiteNode(networkNode, siteName);
        if (result == null) {
            Transaction tx = databaseService.beginTx();
            try {
                result = NeoServiceFactory.getInstance().getDatasetService().addSimpleChild(parentNode, NodeTypes.SITE, siteName);
                getIndexService().index(result, Utils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), siteName);
                tx.success();
            } finally {
                tx.finish();
            }

        }
        return result;
    }

    /**
     * Find Site node by name
     * 
     * @param networkNode network root node
     * @param siteName site name
     * @return site node or null if node not found;
     */
    public Node findSiteNode(Node networkNode, String siteName) {
        return getIndexService().getSingleNode(Utils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), siteName);
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
        return datasetService.findSector(rootNode, ci, lac, name, returnFirsElement);
    }
    
    public Node findSector(Node rootNode, String name, boolean returnFirstElement) {
        return datasetService.findSector(rootNode, null, null, name, returnFirstElement); 
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
            getIndexService().index(result, Utils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR), sectorName);
            if (ci != null) {
                result.setProperty(INeoConstants.PROPERTY_SECTOR_CI, ci);
                getIndexService().index(result, Utils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_SECTOR_CI, NodeTypes.SECTOR), ci);
            }
            if (lac != null) {
                result.setProperty(INeoConstants.PROPERTY_SECTOR_LAC, lac);
                getIndexService().index(result, Utils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_SECTOR_LAC, NodeTypes.SECTOR), lac);
            }
            tx.success();
            return result;
        } finally {
            tx.finish();
        }
    }

    /**
     * Get channel node
     * 
     * @param sector - sector node
     * @param channelNum - channel gr Number
     * @return Node
     */
    public Node getChannelNode(Node sector, int channelNum) {
        Node channel = findChannelNode(sector, channelNum);
        if (channel==null){
            Transaction tx = databaseService.beginTx();
            try {
                Node result = databaseService.createNode();
                result.setProperty(INeoConstants.PROPERTY_NAME_NAME, String.valueOf(channelNum));
                sector.createRelationshipTo(result, Relations.CHANNEL);

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
    public Node findChannelNode(Node sector, int channelNum) {
        final DatasetService ds = NeoServiceFactory.getInstance().getDatasetService();
        final String channelName = String.valueOf(channelNum);
        
        Iterator<Path> itr = Traversal.description().uniqueness(Uniqueness.NONE).prune(Traversal.pruneAfterDepth(1)).filter(new Predicate<Path>() {
            
            @Override
            public boolean accept(Path item) {
                return item.length()>0&&ds.getNodeName(item.endNode()).equals(channelName);
            }
        }).relationships(Relations.CHANNEL, Direction.OUTGOING).traverse(sector).iterator();
        return itr.hasNext()?itr.next().endNode():null;
    }
    private enum Relations implements RelationshipType{
        CHANNEL;
    }

    /**
     * Index property.
     *
     * @param rootNode the root node
     * @param node the node
     * @param propertyName the property name
     * @param value the value
     */
    public void indexProperty(Node rootNode, Node node, String propertyName, String value) {
        getIndexService().index(node, Utils.getLuceneIndexKeyByProperty(rootNode, propertyName, NeoServiceFactory.getInstance().getDatasetService().getNodeType(node)), value);
    }

    /**
     *
     * @param sector
     * @param trxId
     * @param channelGr
     * @return
     */
    public Node getTRXNode(Node sector, String trxId, Integer channelGr) {
        Node trxNode = findTrxNode(sector, trxId,channelGr);
        if (trxNode==null){
            Transaction tx = databaseService.beginTx();
            try {
                trxNode=NeoServiceFactory.getInstance().getDatasetService().addSimpleChild(sector, NodeTypes.TRX, trxId);
                if (channelGr != null) {
                    trxNode.setProperty("group", channelGr);
                }
                tx.success();
            } finally {
                tx.finish();
            }           
        }
        return trxNode;
    }
    
    /**
     * 
     *
     * @param sector
     * @return
     */
    public ArrayList<Node> getAllTRXNode(Node sector) {
        Iterable<Node> itr = Traversal.description().uniqueness(Uniqueness.NONE).breadthFirst().prune(Traversal.pruneAfterDepth(1)).
                relationships(GeoNeoRelationshipTypes.CHILD,Direction.OUTGOING).traverse(sector).nodes();
        
        ArrayList<Node> allTrx = new ArrayList<Node>();
        for (Node it : itr) {
            allTrx.add(it);
        }
        return allTrx;
    }

    /**
     *
     * @param sector
     * @param trxId
     * @param channelGr
     * @return
     */
    public Node findTrxNode(Node sector, final String trxId,final  Integer channelGr) {
        Iterator<Path> itr = Traversal.description().uniqueness(Uniqueness.NONE).depthFirst().prune(Traversal.pruneAfterDepth(1)).relationships(GeoNeoRelationshipTypes.CHILD,Direction.OUTGOING).filter(new Predicate<Path>() {
            
            @Override
            public boolean accept(Path item) {
                boolean isLengthNotZero = item.length() > 0;
                boolean isTrxId = item.endNode().getProperty(INeoConstants.PROPERTY_NAME_NAME,"").equals(trxId);
                boolean isChannelGroup = item.endNode().getProperty("group",-1).equals(channelGr);
                
                if (channelGr != null) {
                    return isLengthNotZero && isTrxId && isChannelGroup;
                }
                else {
                    return isLengthNotZero && isTrxId;
                }
            }
        }).traverse(sector).iterator();
        return itr.hasNext()?itr.next().endNode():null;
    }    

    /**
    *
    * @param sector
    * @param trxId
    * @param channelGr
    * @return
    */
   public Node addFREQNode(Node trxNode, String freq, Node prevFREQNode) {
       Node freqNode = null;
       if (trxNode!=null){
           Transaction tx = databaseService.beginTx();
           try {
        	   freqNode=NeoServiceFactory.getInstance().getDatasetService().addSimpleChild(trxNode, NodeTypes.FREQ, freq);
        	   if(prevFREQNode != null) {
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
     * @param fileName the file name
     * @return the plan node
     */
    public Node getPlanNode(Node trx, String fileName) {
        Node planNode = findPlanNode(trx, fileName);
        if (planNode==null){
                planNode=NeoServiceFactory.getInstance().getDatasetService().addSimpleChild(trx, NodeTypes.FREQUENCY_PLAN, fileName);
        }
        return planNode;
    }

    /**
     * Find plan node.
     *
     * @param trx the trx
     * @param fileName the file name
     * @return the node
     */
    public Node findPlanNode(Node trx, final String fileName) {
        final DatasetService ds = NeoServiceFactory.getInstance().getDatasetService();

        Iterator<Path> itr = Traversal.description().uniqueness(Uniqueness.NONE).prune(Traversal.pruneAfterDepth(1))
                .filter(new Predicate<Path>() {

                    @Override
                    public boolean accept(Path item) {
                        return item.length() > 0 && fileName.equals(ds.getNodeName(item.endNode()));
                    }
                }).relationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING).traverse(trx).iterator();
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
    public Iterable<Node>findIndexedNodeByProperty(Node rootNetwork, NodeTypes type,String propertyName,Object propertyValue){
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
    public Iterable<Node>findSectorByPlan(Node rootNetwork,String bsic,int arfcn,final String planName){
       List<Node>result=new LinkedList<Node>();
       for (Node sector:findIndexedNodeByProperty(rootNetwork,NodeTypes.SECTOR,"BSIC",bsic)){
           Node trx=findTrxNode(sector, "0",0);
           if (trx!=null){
               final DatasetService ds = NeoServiceFactory.getInstance().getDatasetService();
               TraversalDescription td;
               if (StringUtils.isNotEmpty(planName)){
                   td=Traversal.description().uniqueness(Uniqueness.NONE).depthFirst().prune(new PruneEvaluator() {
                    
                    @Override
                    public boolean pruneAfter(Path position) {
                       if (position.length()==1){
                           return position.lastRelationship().isType(GeoNeoRelationshipTypes.NEXT);
                       }
                       return false;
                    }
                }).filter(new Predicate<Path>() {
                    
                    @Override
                    public boolean accept(Path item) {
                        if (item.length()==1&&item.lastRelationship().isType(GeoNeoRelationshipTypes.NEXT)){
                            return false;
                        }
                        return planName.equals(ds.getNodeName(item.endNode()));
                    }
                }).relationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING).relationships(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
               }else{
                   td=Traversal.description().uniqueness(Uniqueness.NONE).depthFirst().prune(Traversal.pruneAfterDepth(1)).filter(Traversal.returnAllButStartNode()).relationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING);
               }
               Iterable<Node> plans=td.traverse(trx).nodes();
               for (Node plan:plans){
                  int[] arfcnArr=(int[])plan.getProperty("arfcn",null);
                  if (arfcnArr!=null){
                      for (int val:arfcnArr) {
                        if (val==arfcn){
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
}
