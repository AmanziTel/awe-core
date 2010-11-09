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

package org.amanzi.neo.core.database.entity;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.amanzi.neo.core.utils.statistic_manager.DataStatisticManager;
import org.amanzi.neo.core.utils.statistic_manager.Header;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.GisTypes;
import org.amanzi.neo.services.enums.NetworkTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.core.runtime.AssertionFailedException;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;

/**
 * <p>
 * Service class for working with Neo Data
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class NeoDataService {
    private final GraphDatabaseService service;
    private static Map<String, Class< ? extends Base>> typeMap = new HashMap<String, Class< ? extends Base>>();
    static {
        typeMap.put(NodeTypes.GIS.getId(), Gis.class);
        typeMap.put(NodeTypes.PROBE.getId(), Probe.class);
        typeMap.put(NodeTypes.NETWORK.getId(), Network.class);
    }

    /**
     * Constructor
     */
    public NeoDataService() {
        service = NeoServiceProviderUi.getProvider().getService();
    }

    /**
     * get wrapper for node
     * 
     * @param node - node
     * @return node wrapper
     */
    public Base getInstance(Node node) {
        Transaction tx = beginTx();
        try {
            if (getReferenceNode().equals(node)) {
                return new Root(this);
            }
            String type = (String)node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, null);
            Class< ? extends Base> klass = typeMap.get(type);
            if (klass == null) {
                throw new AssertionFailedException("Node should fave correct type: " + node);
            }
            if (klass == Network.class) {
                NetworkTypes netType = NetworkTypes.getNodeType(node);
                switch (netType) {
                case PROBE:
                    klass = RadioNetwork.class;
                    break;
                case RADIO:
                    klass = ProbeNetwork.class;
                default:
                    throw new AssertionFailedException("Node should fave correct type: " + node);
                }
            }
            return createNewEntity(klass, node);
        } finally {
            tx.finish();
        }
    }

    /**
     * @param network
     * @param probeName
     * @return
     */
    public Probe findOrCreateProbe(ProbeNetwork network, final String probeName) {
        Transaction tx = beginTx();
        try {
            Iterator<Node> iterator = asNode(network).traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    if (currentPos.isStartNode()) {
                        return false;
                    }
                    Base wrapper = getInstance(currentPos.currentNode());
                    return ((wrapper instanceof Probe) && (probeName.equals(wrapper.getName())));
                }
            }, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
            if (iterator.hasNext()) {
                return (Probe)getInstance(iterator.next());
            } else {
                Probe result = new Probe();
                result.setRoot(network);
                result.setName(probeName);
                result.createAndSave(this);
                tx.success();
                return result;
            }
        } finally {
            tx.finish();
        }
    }

    public <T extends DataRoot> T findOrCreateDataNode(Class<T> klas, String networkName) {
        Transaction tx = beginTx();
        try {
            DataRoot dataRoot = findDataRootByName(networkName);
            if (dataRoot == null) {
                Gis gis = new Gis();
                gis.setName(networkName);

                T result = createNewEntity(klas);
                result.setGis(gis);
                // todo remove storing data type in gis node?
                gis.setPropertyValue(INeoConstants.PROPERTY_GIS_TYPE_NAME, Network.class.isAssignableFrom(klas) ? GisTypes.NETWORK.getHeader() : GisTypes.DRIVE
                        .getHeader());
                result.setName(networkName);
                gis.createAndSave(this);
                result.createAndSave(this);
                tx.success();
                return result;
            } else {
                return (T)dataRoot;
            }
        } finally {
            tx.finish();
        }
    }

    public DataRoot findDataRootByName(final String dataRootName) {
        Transaction tx = beginTx();
        try {
            Iterator<Node> iterator = getReferenceNode().traverse(Order.DEPTH_FIRST, NeoUtils.getStopEvaluator(2), new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    if (currentPos.depth() != 2) {
                        return false;
                    }
                    Base wrapper = getInstance(currentPos.currentNode());
                    return (wrapper instanceof DataRoot && (dataRootName.equals(wrapper.getName())));
                }
            }, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).iterator();
            return iterator.hasNext() ? (DataRoot)getInstance(iterator.next()) : null;
        } finally {
            tx.finish();
        }
    }

    /**
     * Create new entity
     * 
     * @param <T>
     * @param klas necessary class. should have constructor without parameters
     * @return entity object
     */
    private <T> T createNewEntity(Class<T> klas) {
        try {
            return klas.getConstructor().newInstance();
        } catch (IllegalArgumentException e) {
            // TODO Handle IllegalArgumentException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (SecurityException e) {
            // TODO Handle SecurityException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (InstantiationException e) {
            // TODO Handle InstantiationException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (IllegalAccessException e) {
            // TODO Handle IllegalAccessException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (InvocationTargetException e) {
            // TODO Handle InvocationTargetException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (NoSuchMethodException e) {
            // TODO Handle NoSuchMethodException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Create new entity
     * 
     * @param <T>
     * @param klas necessary class. should have constructor with parameters: Node , NeoDataService
     * @return entity object
     */
    private <T> T createNewEntity(Class<T> klas, Node node) {
        try {
            return klas.getConstructor(Node.class, NeoDataService.class).newInstance(node, this);
        } catch (IllegalArgumentException e) {
            // TODO Handle IllegalArgumentException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (SecurityException e) {
            // TODO Handle SecurityException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (InstantiationException e) {
            // TODO Handle InstantiationException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (IllegalAccessException e) {
            // TODO Handle IllegalAccessException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (InvocationTargetException e) {
            // TODO Handle InvocationTargetException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (NoSuchMethodException e) {
            // TODO Handle NoSuchMethodException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * @param entity
     * @return
     */
    public Node asNode(Base entity) {
        return entity.node;
    }

    /**
     * @return
     */
    public Transaction beginTx() {
        return service.beginTx();
    }

    public DataStatisticManager loadStatistics(Base root) {
        try {
            DataStatisticManager result = new DataStatisticManager();
            Relationship propertyRelation = asNode(root).getSingleRelationship(GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING);
            if (propertyRelation != null) {
                Node property = propertyRelation.getOtherNode(asNode(root));
                for (Relationship typeRel : property.getRelationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)) {
                    Node typeNode = typeRel.getOtherNode(property);
                    Class< ? extends Object> klass;
                    String typeName = NeoUtils.getNodeName(typeNode);
                    klass = Class.forName(typeName);
                    for (Relationship propertyRel:typeNode.getRelationships(GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING)){
                        String propertyName=(String)propertyRel.getProperty("property");
                        int countALL=(Integer)propertyRel.getProperty("count");
                        Header header=new Header(propertyName,propertyName,1,klass);
                        header.setCountALL(countALL);
                        if (Number.class.isAssignableFrom(klass)){
                            Double min=(Double)propertyRel.getProperty("min_value");
                            Double max=(Double)propertyRel.getProperty("max_value");
                            header.setMin(min);
                            header.setMax(max);
                        }
                        HashMap<Object, Integer> valuesMap=new HashMap<Object, Integer>();
                        Node valueNode=propertyRel.getOtherNode(typeNode);
                        header.setValues(valuesMap);
                    }
                }
            }
            return result;
        } catch (ClassNotFoundException e) {
            // TODO Handle ClassNotFoundException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * @return
     */
    public Node getReferenceNode() {
        return service.getReferenceNode();
    }

    /**
     * @return
     */
    Node createNode() {
        return service.createNode();
    }
}
