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

package org.amanzi.neo.data_generator.utils;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Compare 2 networks and contains result of comparing
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class CompareResult {
    private boolean equals;
    private Set<String> idProperties;
    private final Set<Node> missedNodes = new HashSet<Node>();
    private final Set<Node> moredNodes = new HashSet<Node>();
    private final Set<CompareNodes> difNodes = new HashSet<CompareNodes>();
    GraphDatabaseService service;

    /**
     * Instantiates a new compare result.
     * 
     * @param service the service
     */
    public CompareResult(GraphDatabaseService service) {
        this.service = service;
        equals = false;
    }

    /**
     * @return Returns the equals.
     */
    public boolean isEquals() {
        return equals;
    }

    /**
     * @return Returns the idProperties.
     */
    public Set<String> getIdProperties() {
        return idProperties;
    }

    /**
     * @param idProperties The idProperties to set.
     */
    public void setIdProperties(Set<String> idProperties) {
        this.idProperties = idProperties;
    }

    /**
     * Compare 2 set of nodes
     * 
     * @param etalonSet the etalon set
     * @param networkSet the network set
     * @param relationshipTypesAndDirections the relationship types and directions
     */
    public void compareNodeSets(HashSet<Node> etalonSet, HashSet<Node> networkSet, Object[] relationshipTypesAndDirections) {
        HashSet<Node> netSet = new HashSet<Node>(networkSet);
        missedNodes.clear();
        moredNodes.clear();
        difNodes.clear();
        for (Node etalonNode : etalonSet) {
            Node netNode = findNode(netSet, etalonNode);
            if (netNode != null) {
                compareNodes(netNode, etalonNode);
                netSet.remove(netNode);
            } else {
                missedNodes.add(etalonNode);
            }
        }
        moredNodes.addAll(netSet);
        equals = moredNodes.isEmpty() && difNodes.isEmpty() && missedNodes.isEmpty();
    }

    /**
     * Compare nodes.
     * 
     * @param netNode the net node
     * @param etalonNode the etalon node
     */
    private void compareNodes(Node netNode, Node etalonNode) {
        CompareNodes compare = new CompareNodes(netNode, etalonNode);
        if (!compare.isEquals()) {
            difNodes.add(compare);
        }

    }

    /**
     * Find node.
     * 
     * @param netSet the network set
     * @param etalonNode the etalon node
     * @return the node
     */
    private Node findNode(HashSet<Node> netSet, Node etalonNode) {
        if (idProperties == null) {
            return null;
        }
        NEXT_NODE: for (Node netNode : netSet) {
            for (String key : idProperties) {
                Object id = etalonNode.getProperty(key, null);
                if (id != null) {
                    Object idNet = netNode.getProperty(key, null);
                    if (!id.equals(idNet)) {
                        continue NEXT_NODE;
                    }
                }
            }
            return netNode;
        }
        return null;
    }

    /**
     * <p>
     * Compare nodes and contains result of comparing
     * </p>
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public static class CompareNodes {
        // TODO add relation comparing!
        private final boolean equals;
        private final Set<CompareProperties> difProp;

        /**
         * Instantiates a new compare nodes.
         * 
         * @param netNode the net node
         * @param etalonNode the etalon node
         */
        public CompareNodes(Node netNode, Node etalonNode) {
            super();
            difProp = new HashSet<CompareProperties>();
            Set<String> keys = new HashSet<String>();
            for (String key : etalonNode.getPropertyKeys()) {
                keys.add(key);
                CompareProperties compare = new CompareProperties(key, netNode, etalonNode);
                if (!compare.isEquals()) {
                    difProp.add(compare);
                }
            }
            for (String key : netNode.getPropertyKeys()) {
                if (keys.contains(key)) {
                    continue;
                }
                CompareProperties compare = new CompareProperties(key, netNode, etalonNode);
                if (!compare.isEquals()) {
                    difProp.add(compare);
                }
            }
            equals = difProp.isEmpty();
        }

        /**
         * @return Returns the equals.
         */
        public boolean isEquals() {
            return equals;
        }

        /**
         * @return Returns the difProp.
         */
        public Set<CompareProperties> getDifProp() {
            return difProp;
        }

    }

    /**
     * <p>
     * Compare Properties and contains result of comparing
     * </p>
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public static class CompareProperties {
        private final String key;
        private final Node netNode;
        private final Node etalonNode;
        private final boolean equals;
        private final Object netValue;
        private final Object etalonValue;

        /**
         * Instantiates a new compare properties.
         * 
         * @param key the key
         * @param netNode the net node
         * @param etalonNode the etalon node
         */
        public CompareProperties(String key, Node netNode, Node etalonNode) {
            super();
            this.key = key;
            this.netNode = netNode;
            this.etalonNode = etalonNode;
            netValue = netNode.getProperty(key, null);
            etalonValue = etalonNode.getProperty(key, null);
            equals = netValue == null ? etalonValue == null : netValue.equals(etalonValue);
        }

        /**
         * @return Returns the equals.
         */
        public boolean isEquals() {
            return equals;
        }

        /**
         * @return Returns the key.
         */
        public String getKey() {
            return key;
        }

        /**
         * @return Returns the netNode.
         */
        public Node getNetNode() {
            return netNode;
        }

        /**
         * @return Returns the etalonNode.
         */
        public Node getEtalonNode() {
            return etalonNode;
        }

        /**
         * @return Returns the netValue.
         */
        public Object getNetValue() {
            return netValue;
        }

        /**
         * @return Returns the etalonValue.
         */
        public Object getEtalonValue() {
            return etalonValue;
        }

    }

    /**
     * @param equals The equals to set.
     */
    public void setEquals(boolean equals) {
        this.equals = equals;
    }

    /**
     * @return Returns the missedNodes.
     */
    public Set<Node> getMissedNodes() {
        return missedNodes;
    }

    /**
     * @return Returns the moredNodes.
     */
    public Set<Node> getMoredNodes() {
        return moredNodes;
    }

    /**
     * @return Returns the difNodes.
     */
    public Set<CompareNodes> getDifNodes() {
        return difNodes;
    }

}
