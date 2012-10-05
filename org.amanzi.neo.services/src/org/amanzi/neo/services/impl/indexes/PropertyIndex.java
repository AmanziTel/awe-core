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
package org.amanzi.neo.services.impl.indexes;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

/**
 * <p>
 * The PropertyIndex provides a mechanism for indexing data according to a single numerical
 * property. The index is based on taking the numerical property and breaking it into a set of
 * ranges defined by the 'cluster' parameter. Then the clusters are grouped again based on the
 * 'step' parameter. These are again group, repeatedly until at the highest level there is only one
 * group. The resulting tree structure is an index in that you can find any specific node based on
 * the value of the parameter indexed with very few relationships traversed.
 * </p>
 * <p>
 * <dl>
 * <dt>Type</dt>
 * <dd>The index is possible on any numeric types, like Integer, Long, Float and Double. For
 * example, when creating a new index, use the syntax 'PropertyIndex&gt;Float&gt; myIndex = new
 * PropertyIndex&ltFloat&gt(...)'</dd>
 * <dt>Cluster</dt>
 * <dd>The first level of the index is created by converting the desired property into blocks of a
 * specified size, the cluster size. This is of the same type as the property being indexed. The
 * first cluster starts at the value of the first node indexed, and all subsequent nodes within that
 * range will be seen as in the same cluster. The choice of this parameter is specific to the data
 * being indexed. Typically you will choose a number that either groups a desired set into one
 * cluster, or a number that leads to the most common result you wish to achieve when querying the
 * data. For example, if the property is a timestamp, you might want the cluster to represent one
 * second of time, if it is common to look at the data in one second intervals.</dd>
 * <dt>Step</dt>
 * <dd>All clusters are themselves grouped into higher level clusters, and this time the grouping is
 * based on the 'step' parameter, which must be an Integer > 1. The choice of this parameter is
 * usually specific to the density and diversity of the data. The goal is to achieve a high
 * performance index, by making sure that the number of child nodes in the level below any parent
 * node is not too different to the total number of levels in the index. For example, we found that
 * if the property is a timestamp, the cluster size is set to one second, and it is possible to have
 * months of data, a step size of 10 leads to about ten nodes per cluster on each level, and about
 * ten levels in total.</dd>
 * <dt>Level</dt>
 * <dd>The zero-level is the set of clustered nodes grouped according to the cluster parameter
 * above. Each node in this level is the parent of one or more nodes in the original data. All
 * higher levels are clustered according to the step size defined above, and each node in a
 * particular level is the parent of one or more nodes in the level below. The index tree is always
 * created to sufficient levels that there is a single parent node at the highest level containing
 * all nodes below it. This root node is the starting point for a search if nothing but the search
 * property is known. However, is is quite common that searches might start at nodes in the vacinity
 * of the result node, in which case the search can be started at such an appropriate data node, or
 * cluster/index node.</dd>
 * <dt>Step(n)</dt>
 * <dd>This is the size of the range represented by a single node at a specific level. It is of the
 * same type as the indexed property.</dd>
 * <dt>Index(n)</dt>
 * <dd>This is a Integer index specific to a particular level, starting at 0 and going up. Each
 * index node represents a range of data underneath that node.</dd>
 * <dt>Range(n)</dt>
 * <dd>The range of data represented by a particular node at a particular level includes all data
 * with property values >= Min(n) and < Max(n). The range is of the same type as the indexed
 * property.</dd>
 * <dt>Min(n)</dt>
 * <dd>The minimum value at a particular level is calculated according to the equation: min(n) =
 * origin + index(n) * step(n). Min(n), origin and step(n) are of the same type as the indexed
 * property, but index(n) is an integer.</dd>
 * <dt>Max(n)</dt>
 * <dd>The maximum value at a particular level is calculated according to the equation: max(n) =
 * min(N) + step(n). Max(n) and step(n) are of the same type as the indexed property.</dd>
 * </dl>
 * </p>
 * <p>
 * When creating an index, it is necessary to specify the property name to index, its type, the
 * cluster size and the step size. Then simply pass in each node in turn to the PropertyIndex and
 * they will be indexed. The total tree created will depend on the complete range of data, and the
 * settings provided at the start. Note that the tree is created as the nodes are passed it, not in
 * advanced, and so will grow as data is added. This also allows an index to be expanded later.
 * </p>
 * <p>
 * Also, since some internal data is represented in Integers, the user needs to specify how to
 * convert the specific data type to an integer and back. We have combined the requirements for
 * cluster, step and type conversion into two methods specified by the IndexConverter interface:
 * <dl>
 * <dt>indexOf(value,origin)</dt>
 * <dd>Converts a specified value into an Integer index based on the specified origin</dd>
 * <dt>valueOf(index,origin)</dt>
 * <dd>Converts a specified Integer index into an original data value based on the specified origin</dd>
 * </dl>
 * These methods perform the clustering operations describe above for the first level. All further
 * levels are based on these methods, as modified by the two Integers, step and level.
 * </p>
 * <p>
 * For example, to create a temporal index using the property 'timestamp' of type 'Long', cluster
 * size 1s and step size 10:
 * 
 * <pre>
 * </pre>
 * 
 * @author craig
 * @since 1.0.0
 * @param <E> the type of the property to index, any standard numerical type
 */
public class PropertyIndex<E extends Comparable<E>> {

    private final String property;
    private final GraphDatabaseService neo;
    private E origin;
    private final ValueConverter<E> converter;
    private Node root;
    private final int step;
    private final List<IndexLevel> levels = new ArrayList<IndexLevel>();

    /**
     * Generic interface for calculating an index value from a data value. This is used for stepping
     * up the resolution scale during indexing. The reason this interface requires so many methods
     * is because in Java we do not have operator overloading and so require specifying all
     * necessary arithmetic formulae as methods.
     */
    public interface ValueConverter<E> {
        /**
         * Convert an original data value into an index at level 0.
         */
        int indexOf(E value, E origin, E stepSize);

        /**
         * Convert an index at level 0 to an original data value.
         */
        E valueOf(Integer index, E origin, E stepSize);

        /**
         * Return the size of the step for the specified level. The results of this are passed into
         * the functions above. We have separated out this component to allow for a small numerical
         * optimization.
         */
        E stepSize(int level, int step);

        /**
         * Compare two values
         * 
         * @param a
         * @param b
         * @return a.compareTo(b)
         */
        int compare(E a, E b);
    }

    /**
     * This specific converter works on values of type Long. The cluster size is passed to the
     * constructor.
     */
    public static class LongConverter implements ValueConverter<Long> {
        private final long cluster;

        public LongConverter(final long cluster) {
            this.cluster = cluster;
        }

        @Override
        public int indexOf(final Long value, final Long origin, final Long stepSize) {
            return (int)((value - (origin - (stepSize / 2))) / stepSize);
        }

        @Override
        public Long valueOf(final Integer index, final Long origin, final Long stepSize) {
            return (origin - (stepSize / 2)) + ((long)index * stepSize);
        }

        @Override
        public Long stepSize(final int level, final int step) {
            return (long)(cluster * Math.pow(step, level));
        }

        @Override
        public int compare(final Long a, final Long b) {
            return a.compareTo(b);
        }
    }

    /**
     * This specific converter works on values of type Integer. The cluster size is passed to the
     * constructor.
     */
    public static class IntegerConverter implements ValueConverter<Integer> {
        private final int cluster;

        public IntegerConverter(final int cluster) {
            this.cluster = cluster;
        }

        @Override
        public int indexOf(final Integer value, final Integer origin, final Integer stepSize) {
            return (value - (origin - (stepSize / 2))) / stepSize;
        }

        @Override
        public Integer valueOf(final Integer index, final Integer origin, final Integer stepSize) {
            return (origin - (stepSize / 2)) + (index * stepSize);
        }

        @Override
        public Integer stepSize(final int level, final int step) {
            return (int)(cluster * Math.pow(step, level));
        }

        @Override
        public int compare(final Integer a, final Integer b) {
            return a.compareTo(b);
        }
    }

    /**
     * This specific converter works on values of type Float. The cluster size is passed to the
     * constructor.
     */
    public static class FloatConverter implements ValueConverter<Float> {
        private final float cluster;

        public FloatConverter(final float cluster) {
            this.cluster = cluster;
        }

        @Override
        public int indexOf(final Float value, final Float origin, final Float stepSize) {
            // We found Math.floor() performed as well as 'if+cast+floor', so use it only
            return (int)(Math.floor((value - (origin - (stepSize / 2))) / stepSize));
        }

        @Override
        public Float valueOf(final Integer index, final Float origin, final Float stepSize) {
            return (origin - (stepSize / 2)) + ((float)index * stepSize);
        }

        @Override
        public Float stepSize(final int level, final int step) {
            return (float)(cluster * Math.pow(step, level));
        }

        @Override
        public int compare(final Float a, final Float b) {
            return a.compareTo(b);
        }
    }

    /**
     * This specific converter works on values of type Double. The cluster size is passed to the
     * constructor.
     */
    public static class DoubleConverter implements ValueConverter<Double> {
        private final double cluster;

        public DoubleConverter(final double cluster) {
            this.cluster = cluster;
        }

        @Override
        public int indexOf(final Double value, final Double origin, final Double stepSize) {
            // We found Math.floor() performed as well as 'if+cast+floor', so use it only
            return (int)(Math.floor((value - (origin - (stepSize / 2))) / stepSize));
        }

        @Override
        public Double valueOf(final Integer index, final Double origin, final Double stepSize) {
            return (origin - (stepSize / 2)) + ((double)index * stepSize);
        }

        @Override
        public Double stepSize(final int level, final int step) {
            return cluster * Math.pow(step, level);
        }

        @Override
        public int compare(final Double a, final Double b) {
            return a.compareTo(b);
        }
    }

    /**
     * This specific converter can take time values as longs and convert them to the first index
     * level, by dividing by 1000L to get from milliseconds to seconds.
     */
    public static class TimeIndexConverter extends LongConverter {
        private static final long DEFAULT_TIMELINE_CLUSTER = 1000L;

        public TimeIndexConverter() {
            super(DEFAULT_TIMELINE_CLUSTER);
        }
    }

    public enum NeoIndexRelationshipTypes implements RelationshipType {
        IND_CHILD, IND_NEXT, INDEX;
    }

    /**
     * The IndexLevel class holds information about a specific level or depth of the index. At each
     * level, a data value can be converted into an index, and vice versa. Internally this class
     * wraps the specified converter for doing most of the work, but is slightly optimized in that
     * the stepsize need only be calculated only() per level. It also makes use of the PropertyIndex
     * internal values for origin, step and converter.
     * 
     * @author craig
     * @since 1.0.0
     */
    private final class IndexLevel {
        private static final String INEX_PROPERTY = "index";
        private int level = 0;
        private E stepSize = null;
        private E currentValue = null;
        private int index = -1;
        private E min;
        private E max;
        private Node indexNode = null;

        /**
         * This constructor is used to build index levels dynamically as the data is being loaded
         * into the database.
         * 
         * @param level
         * @param value
         * @param lowerNode
         */
        private IndexLevel(final int level, final E value, final Node lowerNode) {
            this.level = level;
            this.stepSize = converter.stepSize(level, step);
            setValue(value);
            makeIndexNode();
            linkTo(lowerNode);
        }

        /**
         * This constructor is used to build the index levels based on existing index nodes in the
         * database.
         * 
         * @param level
         * @param indexNode
         */
        @SuppressWarnings("unchecked")
        private IndexLevel(final int level, final Node indexNode) {
            this.level = level;
            this.stepSize = converter.stepSize(level, step);
            this.index = (Integer)indexNode.getProperty(INEX_PROPERTY);
            this.min = (E)indexNode.getProperty("min");
            this.max = (E)indexNode.getProperty("max");
            this.currentValue = this.min;
            this.indexNode = indexNode;
            Integer indexLevel = (Integer)indexNode.getProperty("level", null);
            if (this.level != indexLevel) {
                throw new IllegalArgumentException("Invalid index node passed for level: " + this.level + " != " + indexLevel);
            }
        }

        private IndexLevel setValue(final E value) {
            if (!value.equals(currentValue)) {
                this.currentValue = value;
                int newIndex = converter.indexOf(value, origin, stepSize);
                if (newIndex != this.index) {
                    this.index = newIndex;
                    this.min = converter.valueOf(index, origin, stepSize);
                    this.max = converter.valueOf(index + 1, origin, stepSize);
                    this.indexNode = null;
                }
            }
            return this;
        }

        private boolean includes(final E value) {
            int vindex = converter.indexOf(value, origin, stepSize);
            return vindex == index;
        }

        private Node makeIndexNode() {
            if (indexNode == null) {
                indexNode = neo.createNode();
                indexNode.setProperty(INEX_PROPERTY, index);
                indexNode.setProperty("level", level);
                indexNode.setProperty("min", min);
                indexNode.setProperty("max", max);
            }
            return indexNode;
        }

        private void linkTo(final Node lowerNode) {
            if ((indexNode != null) && (lowerNode != null)) {
                indexNode.createRelationshipTo(lowerNode, NeoIndexRelationshipTypes.IND_CHILD);
            }
        }

        private void searchChildrenOf(final Node parentIndex) {
            for (Relationship rel : parentIndex.getRelationships(NeoIndexRelationshipTypes.IND_CHILD, Direction.OUTGOING)) {
                Node child = rel.getEndNode();
                int testIndex = (Integer)child.getProperty(INEX_PROPERTY);
                if (testIndex == index) {
                    indexNode = child;
                    break;
                }
            }
        }

    }

    public PropertyIndex(final GraphDatabaseService neo, Node reference, final String name, final String property,
            final ValueConverter<E> converter, final int step) {
        if (neo == null) {
            throw new IllegalArgumentException("Index NeoService must exist");
        }
        if ((property == null) || (property.length() < 1)) {
            throw new IllegalArgumentException("Index property must be a non-empty string");
        }
        if (reference == null) {
            reference = neo.getReferenceNode();
        }
        this.neo = neo;
        this.property = property;
        this.step = step;
        this.converter = converter;
        for (Relationship relation : reference.getRelationships(NeoIndexRelationshipTypes.INDEX, Direction.OUTGOING)) {
            Node node = relation.getEndNode();
            if (node.getProperty("type", "").toString().equals("property_index")
                    && node.getProperty("name", "").toString().equals(name)
                    && node.getProperty("property", "").toString().equals(property)) {
                this.root = node;
            }
        }
        if (this.root == null) {
            this.root = neo.createNode();
            root.setProperty("name", name);
            root.setProperty("property", property);
            root.setProperty("type", "property_index");
            reference.createRelationshipTo(root, NeoIndexRelationshipTypes.INDEX);
        } else {
            ArrayList<Node> existingLevelNodes = new ArrayList<Node>();
            Node indexNode = getIndexChildOf(this.root);
            while (indexNode != null) {
                existingLevelNodes.add(0, indexNode);
                indexNode = getIndexChildOf(indexNode);
            }
            for (int level = 0; level < existingLevelNodes.size(); level++) {
                levels.add(new IndexLevel(level, existingLevelNodes.get(level)));
            }
            if (levels.size() > 0) {
                // TODO check correct set
                this.origin = levels.get(0).min;
            }
        }
    }

    private Node getIndexChildOf(final Node parent) {
        for (Relationship rel : parent.getRelationships(NeoIndexRelationshipTypes.IND_CHILD, Direction.OUTGOING)) {
            Node child = rel.getEndNode();
            Integer index = (Integer)child.getProperty("index", null);
            Integer level = (Integer)child.getProperty("level", null);
            if ((index != null) && (level != null) && (index == 0)) {
                return child;

            }
        }
        return null;
    }

    public void finishUp() {
        if (root != null) {
            Node highestIndex = null;
            for (IndexLevel level : levels) {
                if (level.indexNode != null) {
                    highestIndex = level.indexNode;
                }
            }
            if (highestIndex != null) {
                // Deleting any previous starting relationships
                for (Relationship rel : root.getRelationships(NeoIndexRelationshipTypes.IND_CHILD, Direction.OUTGOING)) {
                    rel.delete();
                }
                // Make a new one to the top node (might be same as before or higher level node
                root.createRelationshipTo(highestIndex, NeoIndexRelationshipTypes.IND_CHILD);
            }
        }
    }

    /**
     * This is the main API entry point to the index. Each node added has the value of the index
     * property extracted and compared to the current index tree. The index node that is found or
     * created is returned. The tree is index nodes is automatically updated as necessary to contain
     * nodes of sufficient depth to cover all data passed to this method.
     * 
     * @param node to index
     * @return index node at level 0
     */
    public Node add(final Node node) {
        E value = getProperty(node);
        if (value != null) {
            if (origin == null) {
                origin = value;
            }
            Node indexNode = getIndexNode(value);
            indexNode.createRelationshipTo(node, NeoIndexRelationshipTypes.IND_CHILD);
            return indexNode;
        } else {
            return null;
        }
    }

    /**
     * This is the main indexing method of the class. Here we first search up the cached index stack
     * until we find an index that covers the required value. Then we step back down creating
     * sub-index nodes as we go until we hit the lowest level. With data where nodes coming in are
     * usually near the previous nodes, the cache stack will usually contain the right nodes and it
     * will not often be required to search high of the stack. This should give very good
     * performance for that kind of data.
     * 
     * @param value of the specific type being indexed
     * @return the level 0 index node for this value (created and linked into the index if required)
     */
    private Node getIndexNode(final E value) {
        // search as high as necessary to find a node that covers this value
        IndexLevel indexLevel = getLevelIncluding(value);
        // now step down building index all the way to the bottom
        while (indexLevel.level > 0) {
            IndexLevel lowerLevel = levels.get(indexLevel.level - 1);
            // Set the value in the lower level to the desired value to index, this removes internal
            // node cash, so we much recreate that by finding or creating a new index node
            lowerLevel.setValue(value);
            // First search the node tree for existing child index nodes that match
            if (lowerLevel.indexNode == null) {
                lowerLevel.searchChildrenOf(indexLevel.indexNode);
            }
            // If no child node was found, create one and link it into the tree
            if (lowerLevel.indexNode == null) {
                lowerLevel.makeIndexNode();
                indexLevel.linkTo(lowerLevel.indexNode);
            }
            // Finally step down one level and repeat until we're at the bottom
            indexLevel = lowerLevel;
        }
        return indexLevel.indexNode;
    }

    /**
     * Search up the cached index stack for an index that includes the specified value. The higher
     * we go, the wider the range covered, so at some point the specified value will match. If the
     * value is similar to the previous value, the search is likely to exit at level 0 or 1. The
     * more different, the higher it needs to go. This mean our dynamic index is fastest for data
     * that comes in a stream of related values. Note that each level is created on demand, based on
     * the contents of the lower level index, not the passed in value. This in effect means the
     * index is forced to be related to the previous data, ensuring no disconnected graphs in the
     * index tree.
     * 
     * @param value to index
     * @return lowest cached index value matching the data.
     */
    private IndexLevel getLevelIncluding(final E value) {
        int level = 0;
        IndexLevel indexLevel = null;
        do {
            indexLevel = getLevel(level++);
        } while (!indexLevel.includes(value));
        return indexLevel;
    }

    /**
     * Return or create the specified index level in the index cache. Each level created is based on
     * the values in the level below, so that we maintain a connected graph. When a new level is
     * created, its index Node is also created in the graph and connected to the index node of the
     * level below it.
     */
    private IndexLevel getLevel(final int level) {
        while (levels.size() <= level) {
            int iLev = levels.size();
            if (iLev == 0) {
                // When creating the very first level, use the origin point, and no child index node
                levels.add(new IndexLevel(iLev, origin, null));
            } else {
                // All higher levels are build on top of the lower levels (using the same current
                // value as the level below, and linking the index nodes together into the index
                // tree)
                IndexLevel lowerLevel = levels.get(iLev - 1);
                levels.add(new IndexLevel(iLev, lowerLevel.currentValue, lowerLevel.indexNode));
            }
        }
        return levels.get(level);
    }

    @SuppressWarnings("unchecked")
    private E getProperty(final Node node) {
        return (E)(node == null ? null : node.getProperty(property, null));
    }
}
