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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.NodeResult;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.networkModel.IDistributionModel;
import org.amanzi.neo.services.networkModel.IDistributionalModel;
import org.amanzi.neo.services.networkModel.PropertyEvaluator;
import org.amanzi.neo.services.networkModel.StringDistributionModel;
import org.amanzi.neo.services.networkselection.SelectionModel;
import org.amanzi.neo.services.node2node.INodeToNodeType;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationService;
import org.amanzi.neo.services.node2node.NodeToNodeTypes;
import org.amanzi.neo.services.statistic.ISinglePropertyStat;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.statistic.StatisticManager;
import org.amanzi.neo.services.utils.Utils;
import org.apache.commons.lang.ObjectUtils;
import org.geotools.geometry.jts.JTS;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Evaluator;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * <p>
 * Network Model
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class NetworkModel implements IDistributionalModel, INetworkTraversableModel {

    private final Node rootNode;
    private final DatasetService ds;

    private final NetworkService networkService;
    private NodeToNodeRelationService n2nserrvice;

    private IStatistic statistics;
    private DatasetStructureHandler datasetHandler;

    public NetworkModel(Node rootNode) {
        this.rootNode = rootNode;
        ds = NeoServiceFactory.getInstance().getDatasetService();
        networkService = NeoServiceFactory.getInstance().getNetworkService();
        datasetHandler=new DatasetStructureHandler(rootNode, networkService);
        n2nserrvice = NeoServiceFactory.getInstance().getNodeToNodeRelationService();
    }

    protected NodeToNodeRelationModel getNodeToNodeRelationModel(INodeToNodeType type, String name) {
        // TODO define - should we always create new instance?
        return new NodeToNodeRelationModel(rootNode, type, name);
    }

    public NodeToNodeRelationModel getIllegalFrequency() {
        return getNodeToNodeRelationModel(NodeToNodeTypes.ILLEGAL_FREQUENCY, "Illegal Frequencies");
    }

    public NodeToNodeRelationModel getInterferenceMatrix(String name) {
        return getNodeToNodeRelationModel(NodeToNodeTypes.INTERFERENCE_MATRIX, name);
    }

    public NodeToNodeRelationModel getImpactMatrix(String name) {
        return getNodeToNodeRelationModel(NodeToNodeTypes.IMPACT, name);
    }

    public NodeToNodeRelationModel getShadowing(String name) {
        return getNodeToNodeRelationModel(NodeToNodeTypes.SHADOWING, name);
    }

    public NodeToNodeRelationModel getNeighbours(String name) {
        return getNodeToNodeRelationModel(NodeToNodeTypes.NEIGHBOURS, name);
    }

    public NodeToNodeRelationModel getException(String name) {
        return getNodeToNodeRelationModel(NodeToNodeTypes.EXCEPTION, name);
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
        return networkService.getPlanNode(rootNode, carrierNode, fileName);
    }

    public NodeResult getCarrier(Node sector, String trxId, Integer channelGr) {
        return networkService.getTRXNode(sector, trxId, channelGr);
    }

    public Set<NodeToNodeRelationModel> findAllNode2NodeRoot() {
        return n2nserrvice.findAllNode2NodeRoot(rootNode);
    }

    public Node getClosestSector(Node servSector, Integer bsic, Integer bcch) {
        Set<Node> nodes = findSectorsByBsicBcch(bsic, bcch);
        return getClosestNode(servSector, nodes, 30000);
    }

    public FrequencyPlanModel getFrequencyModel(String modelName, String time, String domain) {
        return FrequencyPlanModel.getModel(rootNode, modelName, time, domain);
    }

    public FrequencyPlanModel getFrequencyModel(String modelName) {
        return FrequencyPlanModel.getModel(rootNode, modelName);
    }

    /**
     * @param servSector
     * @param candidates
     * @return
     */
    public Node getClosestNode(Node servSector, Collection<Node> candidates, double maxDistance) {
        Coordinate c = getCoordinateOfSector(servSector);
        CoordinateReferenceSystem crs = getCrs();
        if (c == null || crs == null) {
            return null;
        }
        Double dist = null;
        Node candidateNode = null;
        for (Node candidate : candidates) {
            if (servSector.equals(candidate)) {
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
                distance = Math.sqrt(Math.pow(c.x - c1.x, 2) + Math.pow(c.y - c1.y, 2));

            }
            if (distance > maxDistance) {
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

    public IllegalFrequencySpectrumModel getFrequencySpectrum() {
        return new IllegalFrequencySpectrumModel(networkService.getFrequencySpectrumRootNode(rootNode));
    }

    public Coordinate getCoordinateOfSite(Node site) {
        if (site == null) {
            return null;
        }
        Double lat = (Double)site.getProperty(INeoConstants.PROPERTY_LAT_NAME, null);
        Double lon = (Double)site.getProperty(INeoConstants.PROPERTY_LON_NAME, null);
        if (lat == null || lon == null) {
            return null;
        }
        return new Coordinate(lon, lat);
    }

    public Node getSiteOfSector(Node sector) {
        Relationship rel = sector.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING);
        return rel != null ? rel.getOtherNode(sector) : null;
    }

    public List<Node> getSectorsOfSite(Node site) {
        ArrayList<Node> result = new ArrayList<Node>();

        for (Relationship childRelationship : site.getRelationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)) {
            result.add(childRelationship.getEndNode());
        }

        return result;
    }

    public Set<NodeToNodeRelationModel> findAllN2nModels(NodeToNodeTypes type) {
        return n2nserrvice.findAllN2nModels(rootNode, type);

    }

    public Set<FrequencyPlanModel> findAllFrqModel() {
        Set<FrequencyPlanModel> result = new HashSet<FrequencyPlanModel>();
        for (Node root : networkService.findAllFrqRoot(rootNode, null).nodes()) {
            result.add(new FrequencyPlanModel(root));
        }
        return result;
    }

    public Iterable<Node> findAllNodeByType(INodeType type) {
        return networkService.findAllNodeByType(rootNode, type);
    }

    public SelectionModel getSelectionModel(String name) {
        Node selectionModelNode = networkService.getRootSelectionNode(rootNode, name);

        return new SelectionModel(rootNode, selectionModelNode);
    }

    public Map<String, SelectionModel> getAllSelectionModels() {
        HashMap<String, SelectionModel> result = new HashMap<String, SelectionModel>();

        for (Node singleNode : networkService.getAllRootSelectionNodes(rootNode)) {
            SelectionModel model = new SelectionModel(rootNode, singleNode);
            result.put(model.getName(), model);
        }

        return result;
    }

    @Override
    public Iterable<Node> getAllElementsByType(Evaluator filter, INodeType... nodeTypes) {
        return networkService.getNetworkElementTraversal(filter, nodeTypes).traverse(rootNode).nodes();
   
    }

    public boolean listNameExists(String name) {
        for (NodeToNodeRelationModel m : findAllNode2NodeRoot()) {
            if (m.getName().equals(name))
                return true;
        }
        return false;
    }

    public String makeUniqueListName(String name) {
        while (listNameExists(name)) {
            String base = name;
            int count = 1;
            Pattern p = Pattern.compile("\\w+(\\d+)");
            Matcher m = p.matcher(name);
            if (m.matches()) {
                String number = m.group(1);
                count = Integer.parseInt(number);
                base = base.replace(number, "");
            }
            count++;
            name = base + count;
        }
        return name;
    }

    public String getName() {
        return networkService.getNodeName(rootNode);
    }

    public List<Node> getAllTrxNodesOfSector(Node sector) {
        return networkService.getAllTRXNode(sector);
    }

    public FrequencyPlanModel findFrequencyModel(String modelName) {
        return FrequencyPlanModel.findModel(rootNode, modelName);
    }

    public static List<NetworkModel> getAllNetworkModels() {
        List<NetworkModel> result = new ArrayList<NetworkModel>();

        NetworkService networkService = NeoServiceFactory.getInstance().getNetworkService();

        for (Node networkNode : networkService.findAllNetworkNodes()) {
            result.add(new NetworkModel(networkNode));
        }

        return result;
    }

    public Node getRootNode() {
        return rootNode;
    }

    
   
/**
 * @property Node property name
 * @type Node type
 * 
 */
        
    @Override
    public IDistributionModel getModel(String property, INodeType type) {
        IStatistic stat = StatisticManager.getStatistic(rootNode);

        ISinglePropertyStat propertyType = stat.findPropertyStatistic(rootNode.getProperty(INeoConstants.PROPERTY_NAME_NAME)
                .toString(), type.getId(), property);

        IDistributionModel model = null;
        if (propertyType.getType() == String.class) {
            model = new StringDistributionModel(property, NodeTypes.STATISTICS_ROOT, this);

        }
        return model;
    }

   

    public DatasetStructureHandler getDatasetStructureHandler() {
        return datasetHandler;
    }

    @Override
    public void init() {
    }



}
