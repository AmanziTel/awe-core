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

import java.util.LinkedHashSet;
import java.util.Set;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.NodeResult;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.node2node.INodeToNodeType;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationService;
import org.amanzi.neo.services.node2node.NodeToNodeTypes;
import org.amanzi.neo.services.utils.Utils;
import org.apache.commons.lang.ObjectUtils;
import org.geotools.geometry.jts.JTS;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class NetworkModel {
    private final Node rootNode;
    private final DatasetService ds;

    private final NetworkService networkService;
    private NodeToNodeRelationService n2nserrvice;

    public NetworkModel(Node rootNode) {
        this.rootNode = rootNode;
        ds = NeoServiceFactory.getInstance().getDatasetService();
        networkService = NeoServiceFactory.getInstance().getNetworkService();
        n2nserrvice = NeoServiceFactory.getInstance().getNodeToNodeRelationService();
    }

    protected NodeToNodeRelationModel getNodeToNodeRelationModel(INodeToNodeType type, String name) {
        // TODO define - should we always create new instance?
        return new NodeToNodeRelationModel(rootNode, type, name);
    }

    public NodeToNodeRelationModel getInterferenceMatrix(String name) {
        return getNodeToNodeRelationModel(NodeToNodeTypes.INTERFERENCE_MATRIX, name);
    }

    public NodeToNodeRelationModel getShadowing(String name) {
        return getNodeToNodeRelationModel(NodeToNodeTypes.SHADOWING, name);
    }

    public NodeToNodeRelationModel getNeighbours(String name) {
        return getNodeToNodeRelationModel(NodeToNodeTypes.NEIGHBOURS, name);
    }

    public NodeToNodeRelationModel getTriangulation(String name) {
        return getNodeToNodeRelationModel(NodeToNodeTypes.TRIANGULATION, name);
    }

    public NodeToNodeRelationModel getTransmission(String name) {
        return getNodeToNodeRelationModel(NodeToNodeTypes.TRANSMISSION, name);
    }

    public Node findSector(String name) {
        return networkService.findSector(rootNode, name, true);
    }

    public NodeResult getPlan(Node carrierNode, String fileName) {
        return networkService.getPlanNode(carrierNode, fileName);
    }

    public NodeResult getCarrier(Node sector, String trxId, Integer channelGr) {
        return networkService.getTRXNode(sector, trxId, channelGr);
    }

    public Set<NodeToNodeRelationModel> findAllNode2NodeRoot() {
        return n2nserrvice.findAllNode2NodeRoot(rootNode);
    }

    public Node getClosestSector(Node servSector, Integer bsic, Integer bcch) {
        Coordinate c = getCoordinateOfSector(servSector);
        CoordinateReferenceSystem crs = getCrs();
        if (c == null || crs == null) {
            return null;
        }
        Double dist = null;
        Node candidateNode = null;
        Set<Node> nodes = findSectorsByBsicBcch(bsic, bcch);
        for (Node candidate : nodes) {
            if (servSector.equals(candidate)){
                continue;
            }
            Coordinate c1 = getCoordinateOfSector(candidate);
            if (c1 == null) {
                continue;
            }
                double distance;
                try {
                    distance = JTS.orthodromicDistance(c, c1, crs);
                } catch (Exception e) {
                    e.printStackTrace();
                    distance= Math.sqrt(Math.pow(c.x - c1.x, 2) + Math.pow(c.y - c1.y, 2));

                }
                if (distance > 30000) {
                    continue;
                }
                if (candidateNode == null || distance < dist) {
                    dist = distance;
                    candidateNode = candidate;
                }
        }
        return candidateNode;
    }

    public Set<Node> findSectorsByBsicBcch(Integer bsic, Integer bcch) {
        Set<Node> result = new LinkedHashSet<Node>();
        String indexName = Utils.getLuceneIndexKeyByProperty(rootNode, "BSIC", NodeTypes.SECTOR);

        for (Node node : networkService.getIndexService().getNodes(indexName, bsic)) {
            Integer bcchno = (Integer)node.getProperty("bcch", null);
            if (ObjectUtils.equals(bcch, bcchno)) {
                result.add(node);
            }
        }
        return result;
    }

    public CoordinateReferenceSystem getCrs() {
        Node gis = ds.findGisNode(rootNode);
        if (gis != null) {
            return Utils.getCRS(gis, null);
        }
        return null;
    }

    public Coordinate getCoordinateOfSector(Node servSector) {
        if (servSector == null) {
            return null;
        }
        return getCoordinateOfSite(getSiteOfSector(servSector));
    }

    public Coordinate getCoordinateOfSite(Node site) {
        Double lat = (Double)site.getProperty(INeoConstants.PROPERTY_LAT_NAME, null);
        Double lon = (Double)site.getProperty(INeoConstants.PROPERTY_LON_NAME, null);
        if (lat == null || lon == null) {
            return null;
        }
        return new Coordinate(lat, lon);
    }

    public Node getSiteOfSector(Node sector) {
        Relationship rel = sector.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING);
        return rel != null ? rel.getOtherNode(sector) : null;
    }

}
