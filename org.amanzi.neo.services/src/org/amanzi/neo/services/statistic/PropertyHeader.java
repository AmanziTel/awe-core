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

package org.amanzi.neo.services.statistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.db.manager.NeoServiceProvider;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.statistic.internal.PropertyHeaderImpl;
import org.amanzi.neo.services.statistic.internal.StatisticRelationshipTypes;
import org.amanzi.neo.services.utils.Utils;
import org.hsqldb.lib.StringUtil;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.helpers.Pair;
import org.neo4j.helpers.Predicate;
import org.neo4j.kernel.Traversal;

/**
 * <p>
 * Contains information of property
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class PropertyHeader implements IPropertyHeader {

    /** String RELATION_PROPERTY field */
    private static final String RELATION_PROPERTY = "property";
    private final Node node;
    private final boolean isGis;
    private final boolean isDataset;
    private final boolean havePropertyNode;

    public static IPropertyHeader getPropertyStatistic(Node node) {
        DatasetService service = NeoServiceFactory.getInstance().getDatasetService();
        String key = service.getNodeName(node);
        Node root = service.findRootByChild(node);
        if (StringUtil.isEmpty(key)) {
            key = service.getNodeName(root);
        }
        if (root != null && root.hasRelationship(StatisticRelationshipTypes.STATISTIC_PROP, Direction.OUTGOING)) {
            return new PropertyHeaderImpl(node, key);
        }
        return new PropertyHeader(node);
    }

    /**
     * Constructor
     * 
     * @param node - gis Node
     */
    private PropertyHeader(Node node) {
        isGis = Utils.isGisNode(node);
        isDataset = !isGis && Utils.isDatasetNode(node);
        this.node = node;
        havePropertyNode = node.hasRelationship(GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING);
    }

    /**
     * get Numeric Fields of Neighbour
     * 
     * @param neighbourName name of neighbour
     * @return array or null
     */
    @Override
    public String[] getNeighbourNumericFields(String neighbourName) {
        Node neighbour = Utils.findNeighbour(node, neighbourName);
        if (neighbour == null) {
            return null;
        }
        String[] result = Utils.getNumericFields(neighbour);
        return result;
    }

    /**
     * get All Fields of Neighbour
     * 
     * @param neighbourName name of neighbour
     * @return array or null
     */
    @Override
    public String[] getNeighbourAllFields(String neighbourName) {
        if (isGis) {
            return getDataVault().getNeighbourAllFields(neighbourName);
        }
        Node neighbour = Utils.findNeighbour(node, neighbourName);
        if (neighbour == null) {
            return null;
        }
        String[] result = Utils.getAllFields(neighbour);
        return result;
    }

    /**
     * get All Fields of Neighbour
     * 
     * @param neighbourName name of neighbour
     * @return array or null
     */
    @Override
    public String[] getTransmissionAllFields(String neighbourName) {
        if (isGis) {
            return getDataVault().getTransmissionAllFields(neighbourName);
        }
        Node neighbour = Utils.findTransmission(node, neighbourName, NeoServiceProvider.getProvider().getService());
        if (neighbour == null) {
            return null;
        }
        String[] result = Utils.getAllFields(neighbour);
        return result;
    }

    /**
     * get Numeric Fields of current node
     * 
     * @return array or null
     */
    @Override
    public String[] getNumericFields(String nodeTypeId) {

        return havePropertyNode ? getDefinedNumericFields() : isGis ? getDataVault().getNumericFields(nodeTypeId) : Utils.getNumericFields(node);
    }

    /**
     * get data vault
     * 
     * @return data vault
     */
    protected PropertyHeader getDataVault() {
        return isGis || isDataset ? new PropertyHeader(node.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).getOtherNode(node)) : this;
    }

    @Override
    public String[] getAllFields(String nodeTypeId) {
        return havePropertyNode ? getDefinedAllFields() : isGis ? getDataVault().getAllFields(nodeTypeId) : getNumericFields("-main-type-");// Utils.getAllFields(node);
    }

    public String[] getIdentityFields() {
        List<String> result = new ArrayList<String>();
        Relationship rel = node.getSingleRelationship(GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING);
        if (rel != null) {
            Node propertyNode = rel.getEndNode();
            for (Node node : propertyNode.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.CHILD,
                    Direction.OUTGOING)) {
                result.addAll(Arrays.asList((String[])node.getProperty("identity_properties")));
            }
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * Get all defined fields from drive gis node
     * 
     * @return
     */
    private String[] getDefinedAllFields() {
        List<String> result = new ArrayList<String>();
        Relationship propRel = node.getSingleRelationship(GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING);
        if (propRel != null) {
            Node propNode = propRel.getEndNode();
            for (Node node : propNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.CHILD,
                    Direction.OUTGOING)) {
                String propType = (String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME, null);
                String propertyName = propType.equals("string") ? INeoConstants.PROPERTY_STATS : INeoConstants.PROPERTY_DATA;
                String[] properties = (String[])node.getProperty(propertyName, null);
                if (propType != null && properties != null) {
                    result.addAll(Arrays.asList(properties));
                }
            }

        }
        return result.toArray(new String[0]);
    }

    private String[] getDefinedNumericFields() {
        List<String> ints = new ArrayList<String>();
        List<String> floats = new ArrayList<String>();
        List<String> longs = new ArrayList<String>();
        List<String> result = new ArrayList<String>();
        Relationship propRel = node.getSingleRelationship(GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING);
        if (propRel != null) {
            Node propNode = propRel.getEndNode();
            for (Node node : propNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.CHILD,
                    Direction.OUTGOING)) {
                String propType = (String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME, null);
                String[] properties = (String[])node.getProperty(INeoConstants.PROPERTY_DATA, null);
                if (propType != null && properties != null) {
                    if (propType.equals("integer")) {
                        ints.addAll(Arrays.asList(properties));
                    } else if (propType.equals("float")) {
                        floats.addAll(Arrays.asList(properties));
                    } else if (propType.equals("long")) {
                        longs.addAll(Arrays.asList(properties));
                    } else if (propType.equals("double")) {
                        longs.addAll(Arrays.asList(properties));
                    }
                }
            }
        }
        result.addAll(ints);
        result.addAll(floats);
        result.addAll(longs);
        return result.toArray(new String[0]);
    }

    private String[] getDefinedStringFields() {
        List<String> result = new ArrayList<String>();
        Relationship propRel = node.getSingleRelationship(GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING);
        if (propRel != null) {
            Node propNode = propRel.getEndNode();
            for (Node node : propNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.CHILD,
                    Direction.OUTGOING)) {
                String propType = (String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME, null);
                String[] properties = (String[])node.getProperty(INeoConstants.PROPERTY_DATA, null);
                if (propType != null && properties != null) {
                    if (propType.equals("string")) {
                        result.addAll(Arrays.asList(properties));
                    }
                }
            }
        }
        return result.toArray(new String[0]);
    }

    /**
     * get list of properties
     * 
     * @return AllChannels properties
     */
    @Override
    public String[] getAllChannels() {
        return isGis ? getDataVault().getAllChannels() : (String[])node.getProperty(INeoConstants.PROPERTY_ALL_CHANNELS_NAME, null);
    }

    /**
     * gets list of Neighbour properties in network
     * 
     * @return Collection
     */
    @Override
    public Collection<String> getNeighbourList() {
        List<String> result = new ArrayList<String>();
        if (isGis) {
            return getDataVault().getNeighbourList();
        }
        Iterable<Relationship> neighb = node.getRelationships(NetworkRelationshipTypes.NEIGHBOUR_DATA, Direction.OUTGOING);
        for (Relationship relationship : neighb) {
            result.add(Utils.getNeighbourPropertyName(Utils.getSimpleNodeName(relationship.getOtherNode(node), "")));
        }
        return result;
    }

    /**
     * @param neighbourName
     * @return
     */
    @Override
    public String[] getNeighbourIntegerFields(String neighbourName) {
        if (isGis) {
            return getDataVault().getNeighbourIntegerFields(neighbourName);
        }
        Node neighbour = Utils.findNeighbour(node, neighbourName);
        if (neighbour == null) {
            return null;
        }
        String[] result = (String[])neighbour.getProperty(INeoConstants.LIST_INTEGER_PROPERTIES, null);
        return result;
    }

    /**
     * @param neighbourName
     * @return
     */
    @Override
    public String[] getTransmissionIntegerFields(String neighbourName) {
        if (isGis) {
            return getDataVault().getTransmissionIntegerFields(neighbourName);
        }
        Node neighbour = Utils.findTransmission(node, neighbourName, NeoServiceProvider.getProvider().getService());
        if (neighbour == null) {
            return null;
        }
        String[] result = (String[])neighbour.getProperty(INeoConstants.LIST_INTEGER_PROPERTIES, null);
        return result;
    }

    /**
     * @param neighbourName
     * @return
     */
    @Override
    public String[] getNeighbourDoubleFields(String neighbourName) {
        if (isGis) {
            return getDataVault().getNeighbourDoubleFields(neighbourName);
        }
        Node neighbour = Utils.findNeighbour(node, neighbourName);
        if (neighbour == null) {
            return null;
        }
        String[] result = (String[])neighbour.getProperty(INeoConstants.LIST_DOUBLE_PROPERTIES, null);
        return result;
    }

    /**
     * @param neighbourName
     * @return
     */
    @Override
    public String[] getTransmissionDoubleFields(String neighbourName) {
        if (isGis) {
            return getDataVault().getTransmissionDoubleFields(neighbourName);
        }
        Node neighbour = Utils.findTransmission(node, neighbourName, NeoServiceProvider.getProvider().getService());
        if (neighbour == null) {
            return null;
        }
        String[] result = (String[])neighbour.getProperty(INeoConstants.LIST_DOUBLE_PROPERTIES, null);
        return result;
    }

    /**
     * @return list of possible event
     */
    @Override
    public Collection<String> getEvents() {
        if (isGis) {
            return getDataVault().getEvents();
        }
        Set<String> result = new HashSet<String>();
        Relationship propRel = node.getSingleRelationship(GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING);
        if (propRel != null) {
            Node propNode = propRel.getEndNode();
            Iterator<Node> iterator = propNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    if (currentPos.isStartNode()) {
                        return false;
                    }
                    Object property = currentPos.lastRelationshipTraversed().getProperty(RELATION_PROPERTY, "");
                    // Pechko_E event property name for TEMS and ROMES is "event_type"
                    // Pechko_E for Nemo is "event_id"
                    return "event_type".equals(property) || "event_id".equals(property);
                }
            }, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING, GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING).iterator();
            if (iterator.hasNext()) {
                Iterator<String> propertyKeys = iterator.next().getPropertyKeys().iterator();
                while (propertyKeys.hasNext()) {
                    result.add(propertyKeys.next());
                }
            }
        }
        return result;
    }

    /**
     * <p>
     * Contains information about property statistics
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    public static class PropertyStatistics implements ISinglePropertyStat {
        private final Relationship statisticsRelation;
        private final Node typeNode;
        private final Node valueNode;
        private String name;

        /**
         * Constructor
         * 
         * @param statisticsRelation
         * @param typeNode
         * @param valueNode
         */

        public PropertyStatistics(Relationship statisticsRelation, Node typeNode, Node valueNode) {
            super();
            this.statisticsRelation = statisticsRelation;
            this.typeNode = typeNode;
            this.valueNode = valueNode;
            name = Utils.getSimpleNodeName(typeNode, "");
        }

        /**
         * Gets the count of property
         * 
         * @return the count
         */
        public long getCount() {
            return statisticsRelation != null ? ((Number)statisticsRelation.getProperty("count", 0)).longValue() : 0l;
        }

        public Pair<Double, Double> getMinMax() {
            return new Pair<Double, Double>((Double)statisticsRelation.getProperty(INeoConstants.MIN_VALUE, null), (Double)statisticsRelation.getProperty(
                    INeoConstants.MAX_VALUE, null));
        }

        @Override
        public Class getType() {
            if (name.equals("long")) {
                return Long.class;
            }
            if (name.equals("float")) {
                return Float.class;
            }
            if (name.equals("integer")) {
                return Integer.class;
            }
            if (name.equals("double")) {
                return Double.class;
            }
            return String.class;
        }

        @Override
        public Comparable getMin() {
            return (Comparable)statisticsRelation.getProperty(INeoConstants.MIN_VALUE, null);
        }

        @Override
        public Comparable getMax() {
            return (Comparable)statisticsRelation.getProperty(INeoConstants.MAX_VALUE, null);
        }

        @Override
        public Object parseValue(String string) {
            if (Number.class.isAssignableFrom(getType())) {
                try {
                    return Utils.getNumberValue(getType(), string);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return string;
        }

        @Override
        public Map<Object, Long> getValueMap() {
            Map<Object, Long> result = new HashMap<Object, Long>();
            if (valueNode != null) {
                for (String propString : valueNode.getPropertyKeys()) {
                    result.put(propString, ((Number)valueNode.getProperty(propString)).longValue());
                }
            }
            return result;
        }

    }

    /**
     * @param propertyName
     * @return
     */
    @Override
    public PropertyHeader.PropertyStatistics getPropertyStatistic(String nodeTypeId, final String propertyName) {
        if (isGis) {
            return getDataVault().getPropertyStatistic(nodeTypeId, propertyName);
        }
        if (havePropertyNode) {
            Node property = node.getSingleRelationship(GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING).getOtherNode(node);
            Iterator<Node> iterator = property.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Relationship relation = currentPos.lastRelationshipTraversed();
                    return relation != null && relation.isType(GeoNeoRelationshipTypes.PROPERTIES) && relation.getProperty("property", "").equals(propertyName);
                }
            }, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING, GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING).iterator();
            if (iterator.hasNext()) {
                Node node = iterator.next();
                Relationship relation = node.getSingleRelationship(GeoNeoRelationshipTypes.PROPERTIES, Direction.INCOMING);
                PropertyStatistics result = new PropertyStatistics(relation, relation.getOtherNode(node), node);
                return result;
            }
        }
        return null;
    }

    @Override
    public boolean isHavePropertyNode() {
        return havePropertyNode;
    }

    /**
     * Returns the COMMONLY USED VALUE value of property type from statistic
     * 
     * @param <T> type of property value
     * @param nodeType type of node
     * @param propertyName property name
     * @param defValue default value
     * @return average value (or default if average is not found)
     */
    protected <T> T getAverageValue(String nodeType, final String propertyName, T defValue) {
        if (!NodeTypes.SECTOR.getId().equals(nodeType))
            return defValue;
        Node root = Utils.getParentNode(node, NodeTypes.NETWORK.getId());
        final TraversalDescription td = Traversal.description().depthFirst().relationships(GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING).relationships(
                GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)
        // .uniqueness(Uniqueness.RELATIONSHIP_GLOBAL)
                .uniqueness(Uniqueness.NODE_GLOBAL).filter(new Predicate<Path>() {

                    @Override
                    public boolean accept(Path item) {
                        return item.lastRelationship() != null && item.lastRelationship().isType(GeoNeoRelationshipTypes.PROPERTIES)
                                && propertyName.equals(item.lastRelationship().getProperty("property", ""));
                    }
                });
        Traverser traverser = td.traverse(root);
        Iterator<Path> iterator = traverser.iterator();
        if (iterator.hasNext()) {
            Path next = iterator.next();
            int count = 0;
            String result = null;
            for (String key : next.endNode().getPropertyKeys()) {
                Integer curCount = (Integer)next.endNode().getProperty(key, 0);
                if (curCount > count) {
                    count = curCount;
                    result = key;
                }
            }
            if (result != null) {
                Class< ? extends Object> clazz = defValue.getClass();
                Object parsRes = result;
                try {
                    if (clazz == Integer.class) {
                        parsRes = Integer.parseInt(result);
                    } else if (clazz == Long.class) {
                        parsRes = Long.parseLong(result);
                    } else if (clazz == Float.class) {
                        parsRes = Float.parseFloat(result);
                    } else if (clazz == Double.class) {
                        parsRes = Double.parseDouble(result);
                    }
                    return (T)parsRes;
                } catch (NumberFormatException e) {
                    return defValue;
                }
            }
            return defValue;
        }
        return defValue;
    }

    /**
     * Returns the list of fields from statistic by value types
     * 
     * @return Map<String, String[]>
     */
    private Map<String, String[]> getDefinedFields() {
        Map<String, String[]> result = new HashMap<String, String[]>();
        Relationship propRel = node.getSingleRelationship(GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING);
        if (propRel != null) {
            Node propNode = propRel.getEndNode();
            for (Node node : propNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.CHILD,
                    Direction.OUTGOING)) {
                String propType = (String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME, null);
                String propertyName = propType.equals("string") ? INeoConstants.PROPERTY_STATS : INeoConstants.PROPERTY_DATA;
                String[] properties = (String[])node.getProperty(propertyName, null);
                if (propType != null && properties != null) {
                    result.put(propType, properties);
                }
            }

        }
        return result;
    }

    /**
     * Returns the map of property keys and values from statistic for target nodeType
     * 
     * @param type type of node
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> getStatisticParams(INodeType type) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (type != NodeTypes.SECTOR)
            return result;

        Map<String, String[]> defFields = getDefinedFields();
        for (Map.Entry<String, String[]> entry : defFields.entrySet()) {
            Object defValue = "";
            if (entry.getKey().equalsIgnoreCase("String")) {
                defValue = "";
            } else if (entry.getKey().equalsIgnoreCase("Integer")) {
                defValue = 0;
            } else if (entry.getKey().equalsIgnoreCase("Long")) {
                defValue = 0L;
            } else if (entry.getKey().equalsIgnoreCase("Double")) {
                defValue = 0D;
            } else if (entry.getKey().equalsIgnoreCase("Float")) {
                defValue = 0F;
            }
            for (String paramNane : entry.getValue()) {
                result.put(paramNane, getAverageValue(type.getId(), paramNane, defValue));
            }

        }
        return result;
    }

    @Override
    public <T> boolean updateStatistic(String nodeTypeId, String propertyName, T newValue, T oldValue) {
        return false;
    }

    @Override
    public boolean updateStatisticCount(String nodeTypeId, long count) {
        return false;
    }

}
