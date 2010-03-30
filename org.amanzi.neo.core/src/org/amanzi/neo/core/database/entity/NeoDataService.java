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
import java.util.Map;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.statistic_manager.DataStatisticManager;
import org.eclipse.core.runtime.AssertionFailedException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

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
    }

    /**
     * Constructor
     */
    public NeoDataService() {
        service = NeoServiceProvider.getProvider().getService();
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
                throw new AssertionFailedException("Node shuld fave correct type: " + node);
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
    public Probe findOrCreateProbe(ProbeNetwork network, String probeName) {
        return null;
//         Transaction tx = beginTx();
//         try {
//         Collection<Probe> results = service.get(Probe.class, "name", probeName);
//         if (results.isEmpty()) {
//         Probe result=new Probe();
//         result.setName(probeName);
//         result.setParent(network);
//         graph.persist(result);
//         return result;
//         } else {
//         return results.iterator().next();
//         }
//         } finally {
//         tx.finish();
//         }
    }

    /**
     * @param probe
     * @param basename
     * @return
     */
    public <T extends Network> T findOrCreateNetworkNode(Class<T> klas, String networkName) {
        return null;
//         Transaction tx = beginTx();
//         try {
//          DataRoot dataRoot=findDataRootByName(networkName);    
//         if (dataRoot==null) {
//         T result = createNewEntity(klas);
//         Gis gis=new Gis();
//         gis.setName(networkName);
//         gis.setGisType(GisTypes.NETWORK);
//         gis.setDataroot(result);
//         graph.persist(gis);
//         service.getReferenceNode().createRelationshipTo(graph.get(gis),GeoNeoRelationshipTypes.CHILD);
//         service.getReferenceNode().createRelationshipTo(graph.get(result),GeoNeoRelationshipTypes.CHILD);
//         return result;
//         } else {
//         return results.iterator().next();
//         }
//         } finally {
//         tx.finish();
//         }
    }

    /**
     *
     * @param networkName
     * @return
     */
    public   DataRoot findDataRootByName(String networkName) {
        Transaction tx = beginTx();
        try{
            
            return null;
        }finally{
            
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
     * @param klas necessary class. should have constructor with  parameters:  Node , NeoDataService 
     * @return entity object
     */
    private <T> T createNewEntity(Class<T> klas, Node node) {
        try {
            return klas.getConstructor(Node.class,NeoDataService.class).newInstance(node,this);
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

    public DataStatisticManager loadStatistics(Class< ? extends Base> klass, Base root) {
        return null;
    }

    /**
     * @return
     */
    public Node getReferenceNode() {
        return service.getReferenceNode();
    }

    /**
     *
     * @return
     */
    Node createNode() {
        return service.createNode();
    }
}
