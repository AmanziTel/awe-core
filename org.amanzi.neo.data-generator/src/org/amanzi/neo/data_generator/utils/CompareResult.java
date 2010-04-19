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
    private Set<String>idProperties;
    private Set<Node> missedNodes = new HashSet<Node>();
    private Set<Node> moredNodes = new HashSet<Node>();
    private Set<CompareNodes> difNodes = new HashSet<CompareNodes>();
    GraphDatabaseService service;

    /**
     * 
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
     * @param etalonSet
     * @param netSet
     * @param relationshipTypesAndDirections
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
     * @param netNode
     * @param etalonNode
     */
    private void compareNodes(Node netNode, Node etalonNode) {
        CompareNodes compare = new CompareNodes(netNode, etalonNode);
        if (!compare.isEquals()) {
            difNodes.add(compare);
        }

    }

    /**
     * @param netSet
     * @param etalonNode
     * @return
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

    public static class CompareNodes {
        // TODO add relation comparing!
        private Node netNode;
        private Node etalonNode;
        private final boolean equals;
        private Set<CompareProperties> difProp;

        /**
         * @param netNode
         * @param etalonNode
         */
        public CompareNodes(Node netNode, Node etalonNode) {
            super();
            difProp = new HashSet<CompareProperties>();
            this.netNode = netNode;
            this.etalonNode = etalonNode;
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

    public static class CompareProperties {
        private String key;
        private Node netNode;
        private Node etalonNode;
        private final boolean equals;
        private Object netValue;
        private Object etalonValue;

        /**
         * @param key
         * @param netNode
         * @param etalonNode
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
