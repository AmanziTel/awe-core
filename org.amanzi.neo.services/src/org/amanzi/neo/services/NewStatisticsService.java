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

import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.neo.services.NewDatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.enums.DatasetRelationshipTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateStatisticsException;
import org.amanzi.neo.services.exceptions.InvalidPropertyStatisticsNodeException;
import org.amanzi.neo.services.exceptions.InvalidStatisticsParameterException;
import org.amanzi.neo.services.exceptions.LoadVaultException;
import org.amanzi.neo.services.statistic.IVault;
import org.amanzi.neo.services.statistic.StatisticsVault;
import org.amanzi.neo.services.statistic.internal.NewPropertyStatistics;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;

/**
 * <p>
 * service to work with statistics
 * </p>
 * 
 * @author Kruglik_A
 * @since 1.0.0
 */
public class NewStatisticsService extends NewAbstractService {
    /**
     * constants for vaultNodes properties keys
     */
    public static final String CLASS = "class";
    public static final String COUNT = "count";
    public static final String NUMBER = "number";
    
    private static final String VALUE_NAME = "v";
    private static final String COUNT_NAME = "c";
    
    private static final String EMPTY_STRING = "";
    private static final String PROP_STAT_NODE = "propertyStatisticsNode";
    private static final String VAULT_NODE = "vaultNode";
    private static final String PROP_STAT = "propStat";
    private static final String ROOT_NODE = "rootNode";
    private static final String VAULT = "vault";

    private static Logger LOGGER = Logger.getLogger(NewStatisticsService.class);
    /**
     * TraversalDescription for child nodes
     */
    private TraversalDescription childNodes = CHILD_ELEMENT_TRAVERSAL_DESCRIPTION.evaluator(Evaluators.atDepth(1));

    
    /**
     * <p>
     * Relationship types for statistics nodes
     * </p>
     * 
     * @author kruglik_a
     * @since 1.0.0
     */
    public enum StatisticsRelationships implements RelationshipType {
        STATISTICS;
    }

    /**
     * <p>
     * Node types for statistics nodes
     * </p>
     * 
     * @author kruglik_a
     * @since 1.0.0
     */
    public enum StatisticsNodeTypes implements INodeType {
        VAULT, PROPERTY_STATISTICS;

        @Override
        public String getId() {
            return name().toLowerCase();
        }
    }

    /**
     * This method recursively loads subVaults
     * 
     * @param vaultNode - node to which the attached statistics vault
     * @return IVault vault for vault node
     * @throws LoadVaultException - this method may generate exception if vault node has wrong
     *         className
     */
    private IVault loadSubVault(Node vaultNode) throws LoadVaultException {
        IVault result;
        String className = (String)vaultNode.getProperty(CLASS, null);
        try {
            @SuppressWarnings("unchecked")
            Class<IVault> klass = (Class<IVault>)Class.forName(className);
            result = klass.newInstance();

            result.setCount((Integer)vaultNode.getProperty(COUNT, null));
            result.setType((String)vaultNode.getProperty(NAME, EMPTY_STRING));
            for (Node propStatNode : getPropertyStatisticsNodes(vaultNode)) {
                NewPropertyStatistics propertyStatistics = loadPropertyStatistics(propStatNode);
                result.addPropertyStatistics(propertyStatistics);
            }
            for (Node subVauldNode : getSubVaultNodes(vaultNode)) {
                result.addSubVault(loadSubVault(subVauldNode));
            }
        } catch (Exception e) {
            throw new LoadVaultException(e);
        }
        return result;

    }

    /**
     * this method get all subVault nodes for parent vaultNode
     * 
     * @param parentVaultNode
     * @return Iterable<Node> subVaultNodes
     */
    public Iterable<Node> getSubVaultNodes(Node parentVaultNode) {
        return childNodes.evaluator(new FilterNodesByType(StatisticsNodeTypes.VAULT)).traverse(parentVaultNode).nodes();
    }

    /**
     * this method get all propertyStatistics nodes for parent vaultNode
     * 
     * @param parentVaultNode
     * @return Iterable<Node> propertyStatisticsNodes
     */
    public Iterable<Node> getPropertyStatisticsNodes(Node parentVaultNode) {
        return childNodes.evaluator(new FilterNodesByType(StatisticsNodeTypes.PROPERTY_STATISTICS)).traverse(parentVaultNode)
                .nodes();
    }
    
    /**
     * This method save vault to database. If it new vault - just save to database,
     * if changed vault - new delete old vault from database and save new vault
     *
     * @param rootNode Node to which attached statistics vault
     * @param vault Vault with statistics
     * @throws DatabaseException - this method may generate exception if exception occurred while
     *         working with a database
     * @throws InvalidStatisticsParameterException - this method may generate exception if some
     *         parameter is null
     * @throws DuplicateStatisticsException - this method may generate exception if rootNode already
     *         has a statistics
     */
    public void saveVault(Node rootNode, IVault vault) throws DatabaseException, InvalidStatisticsParameterException,
            DuplicateStatisticsException {
        LOGGER.debug("start method saveVault(Node rootNode, IVault vault");
        if (rootNode == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter rootNode = null");
            throw new InvalidStatisticsParameterException(ROOT_NODE, rootNode);
        }
        if (vault == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter vault = null");
            throw new InvalidStatisticsParameterException(VAULT, vault);
        }
        // if exist relationship and statistics not changed then
        // throw DuplicateStatisticsException
        if (rootNode.getRelationships(StatisticsRelationships.STATISTICS, Direction.OUTGOING).iterator().hasNext()
                && vault.isStatisticsChanged() == false) {
            LOGGER.error("DuplicateStatisticsException: for this rootNode already exists statistics");
            throw new DuplicateStatisticsException("for this rootNode already exists statistics");
        }
        
        if (vault.isStatisticsChanged()) {
            // need delete rootVault and all subVaults and store changing rootVault
            deleteVault(rootNode);
        }
        
        saveNewVault(rootNode, vault);
        vault.setIsStatisticsChanged(false);
        
        LOGGER.debug("finish method saveVault(Node rootNode, IVault vault");
    }
    
    /**
     * Method delete core vault with statistics
     *
     * @param rootNode Node to which attached statistics vault
     * @throws DatabaseException 
     * @throws InvalidStatisticsParameterException 
     */
    public void deleteVault(Node rootNode) throws DatabaseException, InvalidStatisticsParameterException {
        LOGGER.debug("start method deleteVault(Node rootNode, IVault vault");
        if (rootNode == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter rootNode = null");
            throw new InvalidStatisticsParameterException(ROOT_NODE, rootNode);
        }
        
        Transaction tx = graphDb.beginTx();
        try {
            if (rootNode.getRelationships(StatisticsRelationships.STATISTICS, Direction.OUTGOING).iterator().hasNext()) {
                Relationship statisticsRelationship =
                        rootNode.getSingleRelationship(StatisticsRelationships.STATISTICS, Direction.OUTGOING);
                
                Node rootStaticticsNode = statisticsRelationship.getEndNode();
                Iterable<Relationship> childRelationships =
                        rootStaticticsNode.getRelationships(DatasetRelationshipTypes.CHILD, Direction.OUTGOING);
                
                for (Relationship rel : childRelationships) {
                    Node childNode = rel.getEndNode();
                    if (childNode != null) {
                        if (childNode.getProperty(INeoConstants.PROPERTY_TYPE_NAME).
                                toString().equals(StatisticsNodeTypes.VAULT.getId())) {
                            deleteSubVaults(childNode);
                        }
                        rel.delete();
                        childNode.delete();
                    }
                }
                
                statisticsRelationship.delete();
                rootStaticticsNode.delete();
            }
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not create vault node in database", e);
            tx.failure();
            throw new DatabaseException(e);

        } finally {
            tx.finish();

        }
        LOGGER.debug("finish method deleteVault(Node rootNode, IVault vault");
    }
    
    private void deleteSubVaults(Node vault) {
        for (Relationship childRel : vault.getRelationships(DatasetRelationshipTypes.CHILD, Direction.OUTGOING)) {
            Node subVault = childRel.getEndNode();

            if (subVault != null) {
                if (subVault.getProperty(INeoConstants.PROPERTY_TYPE_NAME).
                        toString().equals(StatisticsNodeTypes.VAULT.getId())) {
                    deleteSubVaults(subVault);
                }
                subVault.delete();
                childRel.delete();
            }
        }
    }

    /**
     * This method save new vault to database
     * 
     * @param rootNode Node to which attached statistics vault
     * @param vault Vault with statistics
     * @throws DatabaseException - this method may generate exception if exception occurred while
     *         working with a database
     */
    private void saveNewVault(Node rootNode, IVault vault) throws DatabaseException{
        LOGGER.debug("start method saveVault(Node rootNode, IVault vault)");
        
        Transaction tx = graphDb.beginTx();
        Node vaultNode = null;
        try {
            vaultNode = createNode(StatisticsNodeTypes.VAULT);

            if (StatisticsNodeTypes.VAULT.getId().equals(getNodeType(rootNode))) {
                rootNode.createRelationshipTo(vaultNode, DatasetRelationTypes.CHILD);
            } else {
                rootNode.createRelationshipTo(vaultNode, StatisticsRelationships.STATISTICS);
            }
            vaultNode.setProperty(NAME, vault.getType());
            vaultNode.setProperty(COUNT, vault.getCount());
            vaultNode.setProperty(CLASS, vault.getClass().getCanonicalName());
            for (NewPropertyStatistics propStat : vault.getPropertyStatisticsMap().values()) {
                savePropertyStatistics(propStat, vaultNode);
            }
            for (IVault subVault : vault.getSubVaults().values()) {
                saveNewVault(vaultNode, subVault);
            }
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not create vault node in database", e);
            tx.failure();
            throw new DatabaseException(e);

        } finally {
            tx.finish();

        }

        LOGGER.debug("finish method saveNewVault(Node rootNode, IVault vault)");
    }

    /**
     * this method load vault from database
     * 
     * @param rootNode - node to which the attached statistics
     * @return
     * @throws InvalidStatisticsParameterException - this method may generate exception if rootNode
     *         parameter is null
     * @throws LoadVaultException - this method may generate exception if vault node has wrong
     *         className
     */
    public IVault loadVault(Node rootNode) throws InvalidStatisticsParameterException, LoadVaultException {
        LOGGER.debug("start method loadVault(Node rootNode)");
        if (rootNode == null) {
            throw new InvalidStatisticsParameterException(ROOT_NODE, rootNode);
        }

        IVault result;
        if (!rootNode.hasRelationship(StatisticsRelationships.STATISTICS, Direction.OUTGOING)) {
            return new StatisticsVault();
        }
        Node vaultNode = rootNode.getSingleRelationship(StatisticsRelationships.STATISTICS, Direction.OUTGOING).getEndNode();
        try {
            result = loadSubVault(vaultNode);
        } catch (LoadVaultException e) {
            LOGGER.error("LoadVaultException: problems to create IVault");
            throw e;
        }
        LOGGER.debug("finish method loadVault(Node rootNode)");
        return result;
    }

    /**
     * this method create propertyStatistics node in database by propertyStatistics object
     * 
     * @param propStat - propertyStatistics object
     * @param vaultNode - parent vault node
     * @throws DatabaseException - this method may generate exception if exception occurred while
     *         working with a database
     * @throws InvalidStatisticsParameterException- this method may generate exception if some
     *         parameter is null
     */
    public void savePropertyStatistics(NewPropertyStatistics propStat, Node vaultNode) throws DatabaseException,
            InvalidStatisticsParameterException {
        LOGGER.debug("start method savePropertyStatistics(NewPropertyStatistics propStat, Node vaultNode)" );
        if (propStat == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter propStat is null");
            throw new InvalidStatisticsParameterException(PROP_STAT, propStat);
        }
        if (vaultNode == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter vaultNode is null");
            throw new InvalidStatisticsParameterException(VAULT_NODE, vaultNode);
        }

        String name = propStat.getName();
        Map<Object, Integer> propMap = propStat.getPropertyMap();
        int number = propMap.size();
        String className = propStat.getKlass().getCanonicalName();
        
        // Kasnitskij_V:
        // Save statistics only if class of statistics is Boolean, String or Number
        Class<?> klass = propStat.getKlass();
        if (klass.equals(Boolean.class) ||
                klass.equals(String.class) ||
                klass.getSuperclass().equals(Number.class)) {
            
            Transaction tx = graphDb.beginTx();
            try {
                Node propStatNode = createNode(StatisticsNodeTypes.PROPERTY_STATISTICS);
                vaultNode.createRelationshipTo(propStatNode, DatasetRelationTypes.CHILD);
                propStatNode.setProperty(NAME, name);
                propStatNode.setProperty(NUMBER, number);
                propStatNode.setProperty(CLASS, className);
        
                int count = 0;
                for (Entry<Object, Integer> entry : propMap.entrySet()) {
                    count++;
                    propStatNode.setProperty(VALUE_NAME + count, entry.getKey());
                    propStatNode.setProperty(COUNT_NAME + count, entry.getValue());
                }
    
                tx.success();
    
            } catch (Exception e) {
                tx.failure();
                LOGGER.error("DatabaseException: " + e.getMessage());
                throw new DatabaseException(e);
            } finally {
                tx.finish();
                LOGGER.debug("finish method savePropertyStatistics(NewPropertyStatistics propStat, Node vaultNode)" );
            }
        }

    }

    /**
     * this method load PropertyStatistics object by propertyStatisticsNode
     * 
     * @param propertyStatisticsNode - this node save propertyStatistics information
     * @return NewPropertyStatistics object
     * @throws InvalidPropertyStatisticsNodeException
     * @throws LoadVaultException - this method may generate exception if propertyStatistics node
     *         has wrong className
     * @throws InvalidStatisticsParameterException - this method may generate exception if
     *         propertyStatisticsNode parameter is null
     */
    public NewPropertyStatistics loadPropertyStatistics(Node propertyStatisticsNode) throws InvalidPropertyStatisticsNodeException,
            LoadVaultException, InvalidStatisticsParameterException {
        LOGGER.debug("start method loadPropertyStatistics(Node propertyStatisticsNode)");
        if (propertyStatisticsNode == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter propertyStatisticsNode is null");
            throw new InvalidStatisticsParameterException(PROP_STAT_NODE, propertyStatisticsNode);
        }
        if (((String)propertyStatisticsNode.getProperty(NAME, EMPTY_STRING)).isEmpty()) {
            LOGGER.error("InvalidPropertyStatisticsNodeException: propertyStatisticsNode has not name property");
            throw new InvalidPropertyStatisticsNodeException(NAME);
        }
        if (!propertyStatisticsNode.hasProperty(CLASS)) {
            LOGGER.error("InvalidPropertyStatisticsNodeException: propertyStatisticsNode has not class property");
            throw new InvalidPropertyStatisticsNodeException(CLASS);
        }
        NewPropertyStatistics result = null;
        try {
            String name = (String)propertyStatisticsNode.getProperty(NAME);
            Class< ? > klass = Class.forName((String)propertyStatisticsNode.getProperty(CLASS));
            result = new NewPropertyStatistics(name, klass);
            Integer number = (Integer)propertyStatisticsNode.getProperty(NUMBER, 0);

            for (int i = 1; i <= number; i++) {
                Object value = propertyStatisticsNode.getProperty(VALUE_NAME + i);
                Integer count = (Integer)propertyStatisticsNode.getProperty(COUNT_NAME + i);
                result.updatePropertyMap(value, count);
            }

        } catch (Exception e) {
            LOGGER.error("LoadVaultException: " + e.getMessage());
            throw new LoadVaultException(e);
        }
        LOGGER.debug("finish method loadPropertyStatistics(Node propertyStatisticsNode)");
        return result;
    }

}
