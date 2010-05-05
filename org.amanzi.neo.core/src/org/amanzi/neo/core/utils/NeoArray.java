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

package org.amanzi.neo.core.utils;

import java.util.Arrays;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class NeoArray {
    private final Node rootNode;
    private final String arrayName;
    private final String propertyValueName;
    private final int level;
    private int maxCoord;
    private final GraphDatabaseService service;
    private final String propertyArrayName;
    private final String propertyArrayLevelName;

    public NeoArray(Node rootNode, String arrayName, GraphDatabaseService service) {
        this(rootNode, arrayName, -1, service);
    }

    public NeoArray(Node rootNode, String arrayName, int level, GraphDatabaseService service) {
        this.rootNode = rootNode;
        this.arrayName = arrayName;
        propertyValueName = new StringBuilder(arrayName).append("_value").toString();
        propertyArrayName = getArrayProperty(arrayName);
        propertyArrayLevelName = new StringBuilder(arrayName).append("_level").toString();
        Transaction tx = service.beginTx();
        try {
            this.level = level > 0 ? level : (Integer)rootNode.getProperty(propertyArrayLevelName);
            this.service = service;
            if (!rootNode.hasProperty(propertyArrayName)) {
                assert !Thread.currentThread().getName().equals("main");
                rootNode.setProperty(propertyArrayName, 0);
                rootNode.setProperty(propertyArrayLevelName, this.level);
                tx.success();
            }
            maxCoord = (Integer)rootNode.getProperty(propertyArrayName);

        } finally {
            tx.finish();
        }
    }

    private static String getArrayProperty(String arrayName) {
        return new StringBuilder(arrayName).append("_size").toString();
    }

    public static boolean hasArray(String arrayName,Node node,GraphDatabaseService service){
        Transaction tx = service.beginTx();
        try{
            return node.hasProperty(getArrayProperty(arrayName));
        }finally{
            tx.finish();
        }
    }
    public Node getNode(int... coordinate) {
        Transaction tx = service.beginTx();
        try {
            Relationship rel = rootNode.getSingleRelationship(getArrayRelationshipType(coordinate[0]), Direction.OUTGOING);
            if (rel == null) {
                return null;
            } else {
                final int len = coordinate.length;
                if (len == 1) {
                    return rel.getOtherNode(rootNode);
                } else {
                    return new NeoArray(rel.getOtherNode(rootNode), arrayName, level - 1, service).getNode(Arrays.copyOfRange(
                            coordinate, 1, len));
                }
            }
        } finally {
            tx.finish();
        }
    }

    public Object getValue(int... coordinate) {
        Node node = getNode(coordinate);
        if (node == null) {
            return null;
        }

        final int len = coordinate.length;
        if (len == level) {
            Transaction tx = service.beginTx();
            try {
                return node.getProperty(propertyValueName, null);
            } finally {
                tx.finish();
            }

        } else {
            return new NeoArray(node, arrayName, level - len, service);
        }
    }

    public Object getValueFromNode(Node node) {
        if (node == null) {
            return null;
        }
        Transaction tx = service.beginTx();
        try {
            return node.getProperty(propertyValueName, null);
        } finally {
            tx.finish();
        }
    }

    public void setValueToNode(Node node, Object value) {
        assert !"main".equals(Thread.currentThread().getName());
        Transaction tx = service.beginTx();
        try {
            node.setProperty(propertyValueName, value);
            tx.success();
        } finally {
            tx.finish();
        }
    }
    public Node findOrCreateNode(int... coordinate) {
        int coord = coordinate[0];
        Transaction tx = service.beginTx();
        Node node;
        final int len = coordinate.length;
        try {
            final RelationshipType relName = getArrayRelationshipType(coord);
            Relationship rel = rootNode.getSingleRelationship(relName, Direction.OUTGOING);
            if (rel == null) {
                if (maxCoord < coord) {
                    maxCoord = coord;
                    rootNode.setProperty(propertyArrayName, 0);
                }
                node = service.createNode();
                rootNode.createRelationshipTo(node, relName);
            } else {
                node = rel.getOtherNode(rootNode);
            }
            tx.success();
        } finally {
            tx.finish();
        }
        if (len == 1) {
            return node;
        } else {
            return new NeoArray(node, arrayName, level - 1, service).findOrCreateNode(Arrays.copyOfRange(coordinate, 1, len));
        }

    }

    public void setValue(Object value, int... coordinate) {
        assert coordinate.length == level;
        Transaction tx = service.beginTx();
        Node node;
        try {
            node = findOrCreateNode(coordinate);
            if (value == null) {
                node.setProperty(propertyValueName, value);
            } else {
                node.removeProperty(propertyValueName);
            }
            tx.success();
        } finally {
            tx.finish();
        }
    }

    public RelationshipType getArrayRelationshipType(int coord) {
        final String name = new StringBuilder(arrayName).append("_ARRAY_IND_").append(coord).toString();
        return new RelationshipType() {

            @Override
            public String name() {
                return name;
            }
        };
    }
}
