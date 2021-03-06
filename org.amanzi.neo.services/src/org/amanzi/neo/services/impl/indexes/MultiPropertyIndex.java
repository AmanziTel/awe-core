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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.amanzi.neo.services.impl.indexes.PropertyIndex.NeoIndexRelationshipTypes;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;

/**
 * <p>
 * The MultiPropertyIndex provides a mechanism for indexing data according to a multiple numerical
 * properties of the same type. See the base class PrpertyIndex for more details on how the indexing
 * is done. For multiple properties we simply duplicate the index, min and max values for each
 * dimension.
 * 
 * @author craig
 * @since 1.0.0
 * @param <E> the type of the property to index, any standard numerical type
 */
public class MultiPropertyIndex<E extends Object> {

    private static final String MAX_PROPERTY = "max";
    private static final String MIN_PROPERTY = "min";
    private static final String INDEX_PROPERTY = "index";
    private static final String LEVEL_PROPERTY = "level";

    private static final int DEFAULT_STEP = 10;

    private static final String STEP_PROPERTY = "step";

    private static final Logger LOGGER = Logger.getLogger(MultiPropertyIndex.class);

    private final String[] properties;
    private GraphDatabaseService neo;
    private E[] origin;
    private final MultiValueConverter<E> converter;
    private Node root;
    private int step;
    private final List<IndexLevel> levels = new ArrayList<IndexLevel>();
    private int[] originIndices = null;
    private final List<Node> nodesToIndex = new ArrayList<Node>();
    private final String name;

    public abstract class Linearizer<T extends Comparable<T>> {
        public T toLinear(final T original) {
            return original;
        }

        public T toOriginal(final T linear) {
            return linear;
        }

        public abstract T toAverage(T linear, int count);
    }

    public class IntegerLinearizer extends Linearizer<Integer> {

        @Override
        public Integer toAverage(final Integer linear, final int count) {
            return count == 0 ? 0 : linear / count;
        }
    }

    public class LongLinearizer extends Linearizer<Long> {

        @Override
        public Long toAverage(final Long linear, final int count) {
            return count == 0 ? 0 : linear / count;
        }
    }

    public class FloatLinearizer extends Linearizer<Float> {

        @Override
        public Float toAverage(final Float linear, final int count) {
            return count == 0 ? 0 : linear / count;
        }
    }

    public class DoubleLinearizer extends Linearizer<Double> {

        @Override
        public Double toAverage(final Double linear, final int count) {
            return count == 0 ? 0 : linear / count;
        }
    }

    public class DoubleLogLinearizer extends DoubleLinearizer {
        @Override
        public Double toLinear(final Double original) {
            return original;
        }

        @Override
        public Double toOriginal(final Double linear) {
            return linear;
        }

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
        private int level = 0;
        private E stepSize = null;
        private E[] currentValues = null;
        private int[] indices = null;
        private final E[] min;
        private E[] max;
        private Node indexNode = null;

        /**
         * This constructor is used to build index levels dynamically as the data is being loaded
         * into the database.
         * 
         * @param level
         * @param values
         * @param lowerNode
         * @throws IOException
         */
        private IndexLevel(final int level, final E[] values, final Node lowerNode) throws IOException {
            this.level = level;
            this.stepSize = converter.stepSize(level, step);
            this.min = this.max = ArrayUtils.clone(values);
            setValues(values);
            makeIndexNode();
            linkTo(lowerNode);
        }

        /**
         * This constructor is used to build the index levels based on existing index nodes in the
         * database.
         * 
         * @param level
         * @param indexNode
         * @throws IOException
         */
        private IndexLevel(final int level, final Node indexNode) throws IOException {
            this.level = level;
            this.stepSize = converter.stepSize(level, step);
            this.indices = (int[])indexNode.getProperty(INDEX_PROPERTY);
            this.min = converter.deserialize(indexNode.getProperty(MIN_PROPERTY));
            this.max = converter.deserialize(indexNode.getProperty(MAX_PROPERTY));
            this.currentValues = this.min;
            this.indexNode = indexNode;
            final Integer indexLevel = (Integer)indexNode.getProperty(LEVEL_PROPERTY, null);
            if (this.level != indexLevel) {
                throw new IllegalArgumentException("Invalid index node passed for level: " + this.level + " != " + indexLevel);
            }
        }

        private IndexLevel setValues(final E[] values) {
            if (!Arrays.equals(values, currentValues)) {
                this.currentValues = ArrayUtils.clone(values);
                final int[] newIndices = new int[values.length];
                for (int i = 0; i < values.length; i++) {
                    newIndices[i] = converter.indexOf(values[i], origin[i], stepSize);
                }
                if (!Arrays.equals(newIndices, this.indices)) {
                    this.indices = newIndices;
                    for (int i = 0; i < indices.length; i++) {
                        this.min[i] = converter.valueOf(indices[i], origin[i], stepSize);
                        this.max[i] = converter.valueOf(indices[i] + 1, origin[i], stepSize);
                    }
                    this.indexNode = null;
                }
            }
            return this;
        }

        private boolean includes(final E[] values) {
            for (int i = 0; i < values.length; i++) {
                final int vindex = converter.indexOf(values[i], origin[i], stepSize);
                if (vindex != indices[i]) {
                    return false;
                }
            }
            return true;
        }

        private Node makeIndexNode() throws IOException {
            if (indexNode == null) {
                indexNode = neo.createNode();
                indexNode.setProperty(INDEX_PROPERTY, indices);
                indexNode.setProperty("type", "multi_index");
                indexNode.setProperty(LEVEL_PROPERTY, level);
                indexNode.setProperty(MIN_PROPERTY, converter.serialize(min));
                indexNode.setProperty(MAX_PROPERTY, converter.serialize(max));
            }
            return indexNode;
        }

        private void linkTo(final Node lowerNode) {
            if ((indexNode != null) && (lowerNode != null)) {
                indexNode.createRelationshipTo(lowerNode, PropertyIndex.NeoIndexRelationshipTypes.IND_CHILD);
            }
        }

        private void searchChildrenOf(final Node parentIndex) {
            for (final Relationship rel : parentIndex.getRelationships(PropertyIndex.NeoIndexRelationshipTypes.IND_CHILD,
                    Direction.OUTGOING)) {
                final Node child = rel.getEndNode();
                final int[] testIndex = (int[])child.getProperty(INDEX_PROPERTY);
                if (Arrays.equals(testIndex, indices)) {
                    indexNode = child;
                    break;
                }
            }
        }

    }

    public interface MultiValueConverter<E> extends PropertyIndex.ValueConverter<E> {
        Object serialize(Object[] data) throws IOException;

        E[] deserialize(Object buffer) throws IOException;

        /**
         * @param e
         * @param stepSize
         * @return
         */
        E correctOrig(E e, E stepSize);

    }

    public static class MultiFloatConverter extends PropertyIndex.FloatConverter implements MultiValueConverter<Float> {
        public MultiFloatConverter(final float cluster) {
            super(cluster);
        }

        @Override
        public Float[] deserialize(final Object buffer) throws IOException {
            final float[] data = (float[])buffer;
            return ArrayUtils.toObject(data);
        }

        @Override
        public Object serialize(final Object[] data) throws IOException {
            final float[] result = new float[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = (Float)data[i];
            }
            return result;
        }

        @Override
        public Float correctOrig(final Float e, final Float stepSize) {
            return e - (stepSize / 2);
        }
    }

    public static class MultiDoubleConverter extends PropertyIndex.DoubleConverter implements MultiValueConverter<Double> {
        public MultiDoubleConverter(final double cluster) {
            super(cluster);
        }

        @Override
        public Double[] deserialize(final Object buffer) throws IOException {
            final double[] data = (double[])buffer;
            return ArrayUtils.toObject(data);
        }

        @Override
        public Object serialize(final Object[] data) throws IOException {
            final double[] result = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = (Double)data[i];
            }
            return result;
        }

        @Override
        public Double correctOrig(final Double e, final Double stepSize) {
            return e - (stepSize / 2);
        }
    }

    public static class MultiIntegerConverter extends PropertyIndex.IntegerConverter implements MultiValueConverter<Integer> {
        public MultiIntegerConverter(final int cluster) {
            super(cluster);
        }

        @Override
        public Integer[] deserialize(final Object buffer) throws IOException {
            final int[] data = (int[])buffer;
            return ArrayUtils.toObject(data);
        }

        @Override
        public Object serialize(final Object[] data) throws IOException {
            final int[] result = new int[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = (Integer)data[i];
            }
            return result;
        }

        @Override
        public Integer correctOrig(final Integer e, final Integer stepSize) {
            return e - (stepSize / 2);
        }
    }

    public static class MultiLongConverter extends PropertyIndex.LongConverter implements MultiValueConverter<Long> {
        public MultiLongConverter(final long cluster) {
            super(cluster);
        }

        @Override
        public Long[] deserialize(final Object buffer) throws IOException {
            final long[] data = (long[])buffer;
            return ArrayUtils.toObject(data);
        }

        @Override
        public Object serialize(final Object[] data) throws IOException {
            final long[] result = new long[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = (Long)data[i];
            }
            return result;
        }

        @Override
        public Long correctOrig(final Long e, final Long stepSize) {
            return e - (stepSize / 2);
        }
    }

    /**
     * This specific converter can take time values as longs and convert them to the first index
     * level, by dividing by 1000L to get from milliseconds to seconds.
     */
    public static class MultiTimeIndexConverter extends MultiLongConverter {
        private static final long DEFAULT_TIMELINE_CLUSTER = 1000L;

        public MultiTimeIndexConverter() {
            super(DEFAULT_TIMELINE_CLUSTER);
        }
    }

    public MultiPropertyIndex(final String name, final String[] properties, final MultiValueConverter<E> converter, final int step)
            throws IOException {
        if ((properties == null) || (properties.length < 1) || (properties[0].length() < 1)) {
            throw new IllegalArgumentException("Index properties must be a non-empty array of non-empty strings");
        }
        this.properties = ArrayUtils.clone(properties);
        this.step = (step & 1) == 1 ? step : step + 1;
        this.converter = converter;
        this.name = name;
    }

    public void initialize(final GraphDatabaseService neo, final Node reference) throws IOException {
        assert reference != null;

        if (neo == null) {
            throw new IllegalArgumentException("Index NeoService must exist");
        }
        this.neo = neo;
        for (final Relationship relation : reference.getRelationships(PropertyIndex.NeoIndexRelationshipTypes.INDEX,
                Direction.OUTGOING)) {
            final Node node = relation.getEndNode();
            if (node.getProperty("type", "").toString().equals("property_index")
                    && node.getProperty("name", "").toString().equals(name)
                    && Arrays.equals((String[])node.getProperty("properties", null), properties)) {
                this.root = node;
            }
        }
        if (this.root == null) {
            this.root = neo.createNode();
            root.setProperty("name", name);
            root.setProperty("properties", properties);
            root.setProperty("type", "property_index");
            root.setProperty(STEP_PROPERTY, step);
            reference.createRelationshipTo(root, PropertyIndex.NeoIndexRelationshipTypes.INDEX);
        } else {
            this.step = (Integer)root.getProperty(STEP_PROPERTY, DEFAULT_STEP);
            final String[] savedProperties = (String[])root.getProperty("properties", null);
            if (!Arrays.equals(this.properties, savedProperties)) {
                throw new IllegalArgumentException("Specified properties do not match saved properties for index " + name);
            }
            final ArrayList<Node> existingLevelNodes = new ArrayList<Node>();
            Node indexNode = getIndexChildOf(this.root);
            while (indexNode != null) {
                existingLevelNodes.add(0, indexNode);
                indexNode = getIndexChildOf(indexNode);
            }
            for (int level = 0; level < existingLevelNodes.size(); level++) {
                levels.add(new IndexLevel(level, existingLevelNodes.get(level)));
            }
            if (levels.size() > 0) {
                this.origin = Arrays.copyOf(levels.get(0).min, levels.get(0).min.length);
                final E stepSize = converter.stepSize(0, step);
                for (int i = 0; i < origin.length; i++) {
                    origin[i] = converter.correctOrig(origin[i], stepSize);
                }

            }
        }
    }

    public void setAggregations(final String[] propertiesToAggregate) {
        // this.propertiesToAggregate = propertiesToAggregate;
    }

    /**
     * Deletes the index data. This does not delete the underlying data, so if that is intended,
     * that should be done by application code.
     */
    public void clear() {
        final ArrayList<Node> toDelete = new ArrayList<Node>();
        for (final Relationship indexRel : this.root.getRelationships(NeoIndexRelationshipTypes.INDEX, Direction.OUTGOING)) {
            final Node indexNode = indexRel.getEndNode();
            toDelete.add(indexNode);
            while (toDelete.size() > 0) {
                final Node node = toDelete.remove(0);
                for (final Relationship rel : node.getRelationships(NeoIndexRelationshipTypes.IND_CHILD, Direction.OUTGOING)) {
                    toDelete.add(rel.getEndNode());
                    rel.delete();
                }
                node.delete();
            }
        }
        levels.clear();
        this.origin = null;
    }

    private Node getIndexChildOf(final Node parent) {
        for (final Relationship rel : parent
                .getRelationships(PropertyIndex.NeoIndexRelationshipTypes.IND_CHILD, Direction.OUTGOING)) {
            final Node child = rel.getEndNode();
            final int[] index = (int[])child.getProperty("index", null);
            final Integer level = (Integer)child.getProperty("level", null);
            if ((index != null) && (level != null)) {
                if (originIndices == null) {
                    originIndices = new int[index.length];
                    // TODO: remove this code only() we are sure initialization is correct
                    for (final int originIndice : originIndices) {
                        assert originIndice == 0;
                    }
                }
                if (Arrays.equals(index, originIndices)) {
                    return child;
                }
            }
        }
        return null;
    }

    public synchronized void finishUp() {
        final Transaction tx = neo.beginTx();
        try {
            flush();
        } catch (final IOException e) {
            LOGGER.error("Error on flushing MultiPropertyIndexes", e);
        }
        try {
            if (root != null) {
                Node highestIndex = null;
                for (final IndexLevel level : levels) {
                    if (level.indexNode != null) {
                        highestIndex = level.indexNode;
                    }
                }
                if (highestIndex != null) {
                    // Deleting any previous starting relationships
                    for (final Relationship rel : root.getRelationships(PropertyIndex.NeoIndexRelationshipTypes.IND_CHILD,
                            Direction.OUTGOING)) {
                        rel.delete();
                    }
                    // Make a new one to the top node (might be same as before or higher level node
                    root.createRelationshipTo(highestIndex, PropertyIndex.NeoIndexRelationshipTypes.IND_CHILD);
                }
            }
            tx.success();
        } catch (final Exception e) {
            LOGGER.error("Error on writing MultiPropertyIndexes to DB", e);
            tx.failure();
        } finally {
            tx.finish();
        }
    }

    /**
     * This is the main API entry point to the index. Each node added is stored in a list until the
     * flush() or finishUp() methods are called, at which time the list is processed and an index
     * built. This is achieved by taking each node and extracting the value of the index property
     * and comparing it to the current index tree. The tree is index nodes is automatically updated
     * as necessary to contain nodes of sufficient depth to cover all data passed to this method. It
     * is possible to call flush() after every add() or occasionally, or never, and it is always
     * called by the final finishUp() method.
     * 
     * @param node to index
     */
    public void add(final Node node) {
        nodesToIndex.add(node);
    }

    /**
     * This method processes the current set of nodes, adding them to the index, and then clearing
     * the list. It returns the last index node created.
     * 
     * @throws IOException
     */
    public synchronized Node flush() throws IOException {
        for (final Node node : nodesToIndex) {
            index(node);
        }
        nodesToIndex.clear();
        return levels.size() > 0 ? levels.get(0).indexNode : null;
    }

    /**
     * This method does the work of indexing a particular node. It returns the index node for the
     * node passed.
     * 
     * @throws IOException
     */
    private Node index(final Node node) throws IOException {
        final E[] values = getProperties(node);

        boolean valid = true;
        for (final E value : values) {
            if ((value == null) || value.equals("")) {
                valid = false;
                break;
            }
        }
        if (valid) {
            if (origin == null) {
                origin = Arrays.copyOf(values, values.length);
            }
            final Node indexNode = getIndexNode(values);
            indexNode.createRelationshipTo(node, PropertyIndex.NeoIndexRelationshipTypes.IND_CHILD);
            return indexNode;
        } else {
            return null;
        }
    }

    private final class SearchEvaluator implements Evaluator {
        private final List<int[]> levelMin = new ArrayList<int[]>();
        private final List<int[]> levelMax = new ArrayList<int[]>();
        private Node currentNode = null;
        private boolean indexOnEdge = false;
        private boolean nodeInRange = false;
        private boolean nodeIsIndex = false;
        private final E[] min;
        private final E[] max;

        private SearchEvaluator(final E[] min, final E[] max, final Integer resolution) throws IOException {
            if (origin == null) {
                throw new IllegalArgumentException("No indexed data");
            }
            if ((min.length < origin.length) || (max.length < origin.length)) {
                throw new IllegalArgumentException("Can only search for data with bounds of same dimension as original data");
            }
            this.min = ArrayUtils.clone(min);
            this.max = ArrayUtils.clone(max);
            // First we convert the search range into index ranges for each level of the index
            for (int iLev = 0; iLev < levels.size(); iLev++) {
                final IndexLevel level = MultiPropertyIndex.this.getLevel(iLev);
                final int[] newIndices = new int[origin.length];
                for (int i = 0; i < origin.length; i++) {
                    newIndices[i] = converter.indexOf(min[i], origin[i], level.stepSize);
                }
                levelMin.add(Arrays.copyOf(newIndices, origin.length));
                for (int i = 0; i < origin.length; i++) {
                    newIndices[i] = converter.indexOf(max[i], origin[i], level.stepSize);
                }
                levelMax.add(Arrays.copyOf(newIndices, origin.length));
            }
        }

        private void setTestNode(final Node node) {
            if (!node.equals(currentNode)) {
                currentNode = node;
                final int[] index = (int[])node.getProperty(INDEX_PROPERTY, null);
                final Integer level = (Integer)node.getProperty(LEVEL_PROPERTY, null);
                // Do not traverse further if we are outside the index tree
                if ((index == null) || (level == null) || (index.length < origin.length)) {
                    nodeIsIndex = false;
                    nodeInRange = true;
                    if (indexOnEdge) {
                        // Index was on edge, and can contain objects on both sides of the range
                        // Switch to exhaustive testing
                        final E[] values = MultiPropertyIndex.this.getProperties(node);
                        for (int i = 0; i < origin.length; i++) {
                            if (values[i] == null) {
                                nodeInRange = false;
                                break;
                            }
                            if (MultiPropertyIndex.this.converter.compare(values[i], min[i]) <= 0) {
                                nodeInRange = false;
                                break;
                            }
                            if (MultiPropertyIndex.this.converter.compare(values[i], max[i]) >= 0) {
                                nodeInRange = false;
                                break;
                            }
                        }
                    }
                } else {
                    // Index nodes get tested to see if they are in the range
                    nodeIsIndex = true;
                    nodeInRange = true;
                    indexOnEdge = false;
                    final int[] minIndex = levelMin.get(level);
                    final int[] maxIndex = levelMax.get(level);
                    for (int i = 0; i < origin.length; i++) {
                        if (index[i] < minIndex[i]) {
                            nodeInRange = false;
                            break;
                        }
                        if (index[i] > maxIndex[i]) {
                            nodeInRange = false;
                            break;
                        }
                        if ((index[i] == minIndex[i]) || (index[i] == maxIndex[i])) {
                            // Index is on the edge of the range, and so can contain data on both
                            // sides
                            indexOnEdge = true;
                        }
                    }
                }

            }
        }

        public boolean isStopNode(final Node currentPos, final boolean isStartNode) {
            if (isStartNode) {
                return false;
            } else {
                setTestNode(currentPos);
                return !nodeIsIndex || !nodeInRange;
            }
        }

        public boolean isReturnableNode(final Node currentPos, final boolean isStartNode) {
            if (currentPos.hasProperty("state") && currentPos.getProperty("state").equals("disabled")) {
                return false;
            }

            if (isStartNode) {
                return false;
            } else {
                setTestNode(currentPos);
                return nodeInRange && !nodeIsIndex;
            }
        }

        @Override
        public Evaluation evaluate(final Path arg0) {
            final Node lastNode = arg0.endNode();
            final boolean isStartNode = arg0.endNode().equals(arg0.startNode());

            return Evaluation.of(isReturnableNode(lastNode, isStartNode), !isStopNode(lastNode, isStartNode));
        }
    }

    public Traverser searchTraverser(final E[] min, final E[] max, final int resolution) {
        try {
            // Create a Stop/Returnable evaluator that understands the range in terms of the index
            final SearchEvaluator searchEvaluator = new SearchEvaluator(min, max, resolution);
            // Then we return a traverser using this evaluator

            return Traversal.description().depthFirst().relationships(NeoIndexRelationshipTypes.IND_CHILD, Direction.OUTGOING)
                    .relationships(NeoIndexRelationshipTypes.IND_NEXT, Direction.OUTGOING).evaluator(searchEvaluator)
                    .traverse(root);
        } catch (final IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    public TraversalDescription searchTraverser(final E[] min, final E[] max) {
        try {
            // Create a Stop/Returnable evaluator that understands the range in terms of the index
            final SearchEvaluator searchEvaluator = new SearchEvaluator(min, max, null);
            // Then we return a traverser using this evaluator
            return Traversal.description().depthFirst().relationships(NeoIndexRelationshipTypes.IND_CHILD, Direction.OUTGOING)
                    .relationships(NeoIndexRelationshipTypes.IND_NEXT, Direction.OUTGOING).evaluator(searchEvaluator);
        } catch (final IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    public Iterable<Node> find(final E[] min, final E[] max) {
        return searchTraverser(min, max).traverse(root).nodes();
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
     * @throws IOException
     */
    private Node getIndexNode(final E[] values) throws IOException {
        // search as high as necessary to find a node that covers this value
        IndexLevel indexLevel = getLevelIncluding(values);
        // now step down building index all the way to the bottom
        while (indexLevel.level > 0) {
            final IndexLevel lowerLevel = levels.get(indexLevel.level - 1);
            // Set the value in the lower level to the desired value to index, this removes internal
            // node cash, so we much recreate that by finding or creating a new index node
            lowerLevel.setValues(values);
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
     * @throws IOException
     */
    private IndexLevel getLevelIncluding(final E[] values) throws IOException {
        int level = 0;
        IndexLevel indexLevel = null;
        do {
            indexLevel = getLevel(level++);
        } while (!indexLevel.includes(values));
        return indexLevel;
    }

    /**
     * Return or create the specified index level in the index cache. Each level created is based on
     * the values in the level below, so that we maintain a connected graph. When a new level is
     * created, its index Node is also created in the graph and connected to the index node of the
     * level below it.
     * 
     * @param int level to get, or -1 to get the last level (-2 for one below that, etc.)
     * @throws IOException
     */
    private IndexLevel getLevel(final int level) throws IOException {
        while (levels.size() <= level) {
            final int iLev = levels.size();
            if (iLev == 0) {
                // When creating the very first level, use the origin point, and no child index node
                levels.add(new IndexLevel(iLev, Arrays.copyOf(origin, origin.length), null));
            } else {
                // All higher levels are build on top of the lower levels (using the same current
                // value as the level below, and linking the index nodes together into the index
                // tree)
                final IndexLevel lowerLevel = levels.get(iLev - 1);
                levels.add(new IndexLevel(iLev, lowerLevel.currentValues, lowerLevel.indexNode));
            }
        }
        return levels.get(level < 0 ? levels.size() + level : level);
    }

    @SuppressWarnings("unchecked")
    private E[] getProperties(final Node node) {
        final Object[] values = new Object[properties.length];
        for (int i = 0; i < properties.length; i++) {
            values[i] = getProperty(node, properties[i]);
        }
        return (E[])values;
    }

    @SuppressWarnings("unchecked")
    private E getProperty(final Node node, final String property) {
        return (E)(node == null ? null : node.getProperty(property, null));
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof MultiPropertyIndex< ? >)) {
            return false;
        }
        return (this.name.equalsIgnoreCase(((MultiPropertyIndex< ? >)obj).name));
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
