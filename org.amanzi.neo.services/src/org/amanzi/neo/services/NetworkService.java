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

import java.util.Iterator;

import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.utils.Utils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.helpers.Predicate;
import org.neo4j.kernel.Traversal;

/**
 * <p>
 * Network service
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkService extends AbstractService {

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
    private Node findBscNode(Node networkNode, String bscName) {
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
    private Node findSiteNode(Node networkNode, String siteName) {
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
        return NeoServiceFactory.getInstance().getDatasetService().findSector(rootNode, ci, lac, name, returnFirsElement);
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


}
