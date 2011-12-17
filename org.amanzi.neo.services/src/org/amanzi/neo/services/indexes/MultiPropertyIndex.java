package org.amanzi.neo.services.indexes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.amanzi.neo.services.indexes.PropertyIndex.NeoIndexRelationshipTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

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
    
    private final String[] properties;
    private GraphDatabaseService neo;
    private E[] origin;
    private final MultiValueConverter<E> converter;
    private Node root;
    private int step;
    private final ArrayList<IndexLevel> levels = new ArrayList<IndexLevel>();
    private int[] originIndices = null;
    private final ArrayList<Node> nodesToIndex = new ArrayList<Node>();
    private final String name;

    public abstract class Linearizer<T extends Comparable<T>> {
        public T toLinear(T original) {
            return original;
        }

        public T toOriginal(T linear) {
            return linear;
        }

        public abstract T toAverage(T linear, int count);
    }

    public class IntegerLinearizer extends Linearizer<Integer> {

        @Override
        public Integer toAverage(Integer linear, int count) {
            return count == 0 ? 0 : linear / count;
        }
    }

    public class LongLinearizer extends Linearizer<Long> {

        @Override
        public Long toAverage(Long linear, int count) {
            return count == 0 ? 0 : linear / count;
        }
    }

    public class FloatLinearizer extends Linearizer<Float> {

        @Override
        public Float toAverage(Float linear, int count) {
            return count == 0 ? 0 : linear / count;
        }
    }

    public class DoubleLinearizer extends Linearizer<Double> {

        @Override
        public Double toAverage(Double linear, int count) {
            return count == 0 ? 0 : linear / count;
        }
    }

    public class DoubleLogLinearizer extends DoubleLinearizer {
        @Override
        public Double toLinear(Double original) {
            return original;
        }

        @Override
        public Double toOriginal(Double linear) {
            return linear;
        }

    }

    /**
     * The IndexLevel class holds information about a specific level or depth of the index. At each
     * level, a data value can be converted into an index, and vice versa. Internally this class
     * wraps the specified converter for doing most of the work, but is slightly optimized in that
     * the stepsize need only be calculated once per level. It also makes use of the PropertyIndex
     * internal values for origin, step and converter.
     * 
     * @author craig
     * @since 1.0.0
     */
    class IndexLevel {
        int level = 0;
        private E stepSize = null;
        private E[] currentValues = null;
        int[] indices = null;
        E[] min;
        E[] max;
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
        private IndexLevel(int level, E[] values, Node lowerNode) throws IOException {
            this.level = level;
            this.stepSize = converter.stepSize(level, step);
            this.min = Arrays.copyOf(values, values.length);
            this.max = Arrays.copyOf(values, values.length);
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
        private IndexLevel(int level, Node indexNode) throws IOException {
            this.level = level;
            this.stepSize = converter.stepSize(level, step);
            this.indices = (int[])indexNode.getProperty("index");
            this.min = converter.deserialize(indexNode.getProperty("min"));
            this.max = converter.deserialize(indexNode.getProperty("max"));
            this.currentValues = this.min;
            this.indexNode = indexNode;
            Integer indexLevel = (Integer)indexNode.getProperty("level", null);
            if (this.level != indexLevel) {
                throw new IllegalArgumentException("Invalid index node passed for level: " + this.level + " != " + indexLevel);
            }
        }

        private IndexLevel setValues(E[] values) {
            if (!Arrays.equals(values, currentValues)) {
                this.currentValues = values;
                int[] newIndices = new int[values.length];
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

        private boolean includes(E[] values) {
            for (int i = 0; i < values.length; i++) {
                int vindex = converter.indexOf(values[i], origin[i], stepSize);
                if (vindex != indices[i]) {
                    return false;
                }
            }
            return true;
        }

        private Node makeIndexNode() throws IOException {
            if (indexNode == null) {
                indexNode = neo.createNode();
                indexNode.setProperty("index", indices);
                indexNode.setProperty("type", "multi_index");
                indexNode.setProperty("level", level);
                indexNode.setProperty("min", converter.serialize(min));
                indexNode.setProperty("max", converter.serialize(max));
            }
            return indexNode;
        }

        private void linkTo(Node lowerNode) {
            if (indexNode != null && lowerNode != null) {
                indexNode.createRelationshipTo(lowerNode, PropertyIndex.NeoIndexRelationshipTypes.IND_CHILD);
            }
        }

        private void searchChildrenOf(Node parentIndex) {
            for (Relationship rel : parentIndex.getRelationships(PropertyIndex.NeoIndexRelationshipTypes.IND_CHILD, Direction.OUTGOING)) {
                Node child = rel.getEndNode();
                int[] testIndex = (int[])child.getProperty("index");
                if (Arrays.equals(testIndex, indices)) {
                    indexNode = child;
                    break;
                }
            }
        }

    }

    public interface MultiValueConverter<E> extends PropertyIndex.ValueConverter<E> {
        public Object serialize(Object[] data) throws IOException;

        public E[] deserialize(Object buffer) throws IOException;

        /**
         *
         * @param e
         * @param stepSize
         * @return
         */
        public E correctOrig(E e, E stepSize);

    }

    public static class MultiFloatConverter extends PropertyIndex.FloatConverter implements MultiValueConverter<Float> {
        public MultiFloatConverter(float cluster) {
            super(cluster);
        }

        public Float[] deserialize(Object buffer) throws IOException {
            float[] data = (float[])buffer;
            Float[] result = new Float[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = data[i];
            }
            return result;
        }

        @Override
        public Object serialize(Object[] data) throws IOException {
            float[] result = new float[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = (Float)data[i];
            }
            return result;
        }

        @Override
        public Float correctOrig(Float e, Float stepSize) {
            return e+stepSize/2;
        }
    }

    public static class MultiDoubleConverter extends PropertyIndex.DoubleConverter implements MultiValueConverter<Double> {
        public MultiDoubleConverter(double cluster) {
            super(cluster);
        }

        public Double[] deserialize(Object buffer) throws IOException {
            double[] data = (double[])buffer;
            Double[] result = new Double[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = data[i];
            }
            return result;
        }

        @Override
        public Object serialize(Object[] data) throws IOException {
            double[] result = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = (Double)data[i];
            }
            return result;
        }

        @Override
        public Double correctOrig(Double e, Double stepSize) {
            return e+stepSize/2;
        }
    }

    public static class MultiIntegerConverter extends PropertyIndex.IntegerConverter implements MultiValueConverter<Integer> {
        public MultiIntegerConverter(int cluster) {
            super(cluster);
        }

        public Integer[] deserialize(Object buffer) throws IOException {
            int[] data = (int[])buffer;
            Integer[] result = new Integer[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = data[i];
            }
            return result;
        }

        @Override
        public Object serialize(Object[] data) throws IOException {
            int[] result = new int[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = (Integer)data[i];
            }
            return result;
        }

        @Override
        public Integer correctOrig(Integer e, Integer stepSize) {
            return e+stepSize/2;
        }
    }

    public static class MultiLongConverter extends PropertyIndex.LongConverter implements MultiValueConverter<Long> {
        public MultiLongConverter(long cluster) {
            super(cluster);
        }

        public Long[] deserialize(Object buffer) throws IOException {
            long[] data = (long[])buffer;
            Long[] result = new Long[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = data[i];
            }
            return result;
        }

        @Override
        public Object serialize(Object[] data) throws IOException {
            long[] result = new long[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = (Long)data[i];
            }
            return result;
        }

        @Override
        public Long correctOrig(Long e, Long stepSize) {
            return e+stepSize/2;
        }
    }

    /**
     * This specific converter can take time values as longs and convert them to the first index
     * level, by dividing by 1000L to get from milliseconds to seconds.
     */
    public static class MultiTimeIndexConverter extends MultiLongConverter {
        public MultiTimeIndexConverter() {
            super(1000L);
        }
    }

    public MultiPropertyIndex(GraphDatabaseService neo, String name, String[] properties, MultiValueConverter<E> converter,int step) throws IOException {
        this(name,properties,converter,step);
        initialize(neo,null);
    }

    public MultiPropertyIndex(String name, String[] properties, MultiValueConverter<E> converter, int step) throws IOException {
        if (properties == null || properties.length < 1 || properties[0].length() < 1)
            throw new IllegalArgumentException("Index properties must be a non-empty array of non-empty strings");
        this.properties = properties;
        this.step = step%2==1?step:step+1;
        this.converter = converter;
        this.name = name;
    }

    public void initialize(GraphDatabaseService neo, Node reference) throws IOException {
        if (neo == null)
            throw new IllegalArgumentException("Index NeoService must exist");
        this.neo = neo;
        if (reference == null)
            reference = neo.getReferenceNode();
        for (Relationship relation : reference.getRelationships(PropertyIndex.NeoIndexRelationshipTypes.INDEX, Direction.OUTGOING)) {
            Node node = relation.getEndNode();
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
            root.setProperty("step", step);
            reference.createRelationshipTo(root, PropertyIndex.NeoIndexRelationshipTypes.INDEX);
        } else {
            this.step = (Integer)root.getProperty("step", 10);
            String[] savedProperties = (String[])root.getProperty("properties", null);
            if(!Arrays.equals(this.properties, savedProperties)){
                throw new IllegalArgumentException("Specified properties do not match saved properties for index "+name);
            }
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
                this.origin = Arrays.copyOf(levels.get(0).min, levels.get(0).min.length);
                E stepSize = converter.stepSize(0, step);
                for (int i = 0; i < origin.length; i++) {
                    origin[i]=converter.correctOrig(origin[i],stepSize);
                }
                
            }
        }
    }

    public void setAggregations(String[] propertiesToAggregate) {
        // this.propertiesToAggregate = propertiesToAggregate;
    }

    /**
     * Deletes the index data. This does not delete the underlying data, so if that is intended,
     * that should be done by application code.
     */
    public void clear() {
        ArrayList<Node> toDelete = new ArrayList<Node>();
        for (Relationship indexRel : this.root.getRelationships(NeoIndexRelationshipTypes.INDEX, Direction.OUTGOING)) {
            Node indexNode = indexRel.getEndNode();
            toDelete.add(indexNode);
            while (toDelete.size() > 0) {
                Node node = toDelete.remove(0);
                for (Relationship rel : node.getRelationships(NeoIndexRelationshipTypes.IND_CHILD, Direction.OUTGOING)) {
                    toDelete.add(rel.getEndNode());  
                    rel.delete();
                }
                node.delete();
            }
        }
        levels.clear();
        this.origin = null;
    }

    private Node getIndexChildOf(Node parent) {
        for (Relationship rel : parent.getRelationships(PropertyIndex.NeoIndexRelationshipTypes.IND_CHILD, Direction.OUTGOING)) {
            Node child = rel.getEndNode();
            int[] index = (int[])child.getProperty("index", null);
            Integer level = (Integer)child.getProperty("level", null);
            if (index != null && level != null) {
                if (originIndices == null) {
                    originIndices = new int[index.length];
                    // TODO: remove this code once we are sure initialization is correct
                    for (int i = 0; i < originIndices.length; i++) {
                        assert originIndices[i] == 0;
                    }
                }
                if (Arrays.equals(index, originIndices)) {
                    return child;
                }
            }
        }
        return null;
    }

    public void finishUp() {
        Transaction tx = neo.beginTx();
        try {
            flush();
        } catch (IOException e) {
        }
        try {
            if (root != null) {
                Node highestIndex = null;
                for (IndexLevel level : levels) {
                    if (level.indexNode != null) {
                        highestIndex = level.indexNode;
                    }
                }
                if (highestIndex != null) {
                    // Deleting any previous starting relationships
                    for (Relationship rel : root.getRelationships(PropertyIndex.NeoIndexRelationshipTypes.IND_CHILD, Direction.OUTGOING)) {
                        rel.delete();
                    }
                    // Make a new one to the top node (might be same as before or higher level node
                    root.createRelationshipTo(highestIndex, PropertyIndex.NeoIndexRelationshipTypes.IND_CHILD);
                }
            }
        } catch (Exception e) {
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
     * @throws IOException
     */
    public void add(Node node) throws IOException {
        nodesToIndex.add(node);
    }

    /**
     * This method processes the current set of nodes, adding them to the index, and then clearing
     * the list. It returns the last index node created.
     * 
     * @throws IOException
     */
    public Node flush() throws IOException {
        for (Node node : nodesToIndex) {
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
    private Node index(Node node) throws IOException {
        E[] values = getProperties(node);
        if (values != null) {
            boolean valid = true;
            for (E value : values) {
                if (value == null || value.equals("")) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                if (origin == null) {
                    origin = Arrays.copyOf(values, values.length);
                }
                Node indexNode = getIndexNode(values);
                indexNode.createRelationshipTo(node, PropertyIndex.NeoIndexRelationshipTypes.IND_CHILD);
                return indexNode;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private class SearchEvaluator implements StopEvaluator, ReturnableEvaluator {
        private final ArrayList<int[]> levelMin = new ArrayList<int[]>();
        private final ArrayList<int[]> levelMax = new ArrayList<int[]>();
        private Node currentNode = null;
        private boolean indexOnEdge = false;
        private boolean nodeInRange = false;
        private boolean nodeIsIndex = false;
        private final E[] min;
        private final E[] max;
        private int countNodes = 0;
        private int countResults = 0;

        private SearchEvaluator(final E[] min, final E[] max, Integer resolution) throws IOException {
            if (origin == null) {
                throw new IllegalArgumentException("No indexed data");
            }
            if (min.length < origin.length || max.length < origin.length) {
                throw new IllegalArgumentException("Can only search for data with bounds of same dimension as original data");
            }
            this.min = min;
            this.max = max;
            // First we convert the search range into index ranges for each level of the index
            for (int iLev = 0; iLev < levels.size(); iLev++) {
                IndexLevel level = MultiPropertyIndex.this.getLevel(iLev);
                int[] newIndices = new int[origin.length];
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

        private void setTestNode(Node node) {
            if (node != currentNode) {
                currentNode = node;
                countNodes++;
                int[] index = (int[])node.getProperty("index", null);
                Integer level = (Integer)node.getProperty("level", null);
                // Do not traverse further if we are outside the index tree
                if (index == null || level == null || index.length < origin.length) {
                    nodeIsIndex = false;
                    nodeInRange = true;
                    if (indexOnEdge) {
                        // Index was on edge, and can contain objects on both sides of the range
                        // Switch to exhaustive testing
                        E[] values = MultiPropertyIndex.this.getProperties(node);
                        for (int i = 0; i < origin.length; i++) {
                            if (values[i] == null) {
                                nodeInRange = false;
                                break;
                            }
                            if (MultiPropertyIndex.this.converter.compare(values[i], min[i]) < 0) {
                                nodeInRange = false;
                                break;
                            }
                            if (MultiPropertyIndex.this.converter.compare(values[i], max[i]) >= 0) {
                                nodeInRange = false;
                                break;
                            }
                        }
                    }
                    if (nodeInRange) {
                        countResults++;
                    }
                } else {
                    // Index nodes get tested to see if they are in the range
                    nodeIsIndex = true;
                    nodeInRange = true;
                    indexOnEdge = false;
                    int[] minIndex = levelMin.get(level);
                    int[] maxIndex = levelMax.get(level);
                    for (int i = 0; i < origin.length; i++) {
                        if (index[i] < minIndex[i]) {
                            nodeInRange = false;
                            break;
                        }
                        if (index[i] > maxIndex[i]) {
                            nodeInRange = false;
                            break;
                        }
                        if (index[i] == minIndex[i] || index[i] == maxIndex[i]) {
                            // Index is on the edge of the range, and so can contain data on both
                            // sides
                            indexOnEdge = true;
                        }
                    }
                }

            }
        }

        @Override
        public boolean isStopNode(TraversalPosition currentPos) {
            if (currentPos.isStartNode()) {
                return false;
            } else {
                setTestNode(currentPos.currentNode());
                return !nodeIsIndex || !nodeInRange;
            }
        }

        @Override
        public boolean isReturnableNode(TraversalPosition currentPos) {
            if (currentPos.currentNode().hasProperty("state")) {
                if (currentPos.currentNode().getProperty("state").equals("disabled")) {
                    return false;
                }
            }
            
            if (currentPos.isStartNode()) {
                return false;
            } else {
                setTestNode(currentPos.currentNode());
                return nodeInRange && !nodeIsIndex;
            }
        }

    }

    public Traverser searchTraverser(final E[] min, final E[] max, int resolution) {
        try {
            // Create a Stop/Returnable evaluator that understands the range in terms of the index
            SearchEvaluator searchEvaluator = new SearchEvaluator(min, max, resolution);
            // Then we return a traverser using this evaluator
            return this.root.traverse(Order.DEPTH_FIRST, searchEvaluator, searchEvaluator, NeoIndexRelationshipTypes.IND_CHILD,
                    Direction.OUTGOING, NeoIndexRelationshipTypes.IND_NEXT, Direction.OUTGOING);
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    public Traverser searchTraverser(final E[] min, final E[] max) {
        try {
            // Create a Stop/Returnable evaluator that understands the range in terms of the index
            SearchEvaluator searchEvaluator = new SearchEvaluator(min, max, null);
            // Then we return a traverser using this evaluator
            return this.root.traverse(Order.DEPTH_FIRST, searchEvaluator, searchEvaluator, NeoIndexRelationshipTypes.IND_CHILD,
                    Direction.OUTGOING, NeoIndexRelationshipTypes.IND_NEXT, Direction.OUTGOING);
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    public Collection<Node> find(E[] min, E[] max) {
        ArrayList<Node> results = new ArrayList<Node>();
        //TODO why not use  -   return searchTraverser(min, max).getAllNodes();???
        for (Node node : searchTraverser(min, max)) {
            results.add(node);
        }
        return results;
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
    private Node getIndexNode(E[] values) throws IOException {
        // search as high as necessary to find a node that covers this value
        IndexLevel indexLevel = getLevelIncluding(values);
        // now step down building index all the way to the bottom
        while (indexLevel.level > 0) {
            IndexLevel lowerLevel = levels.get(indexLevel.level - 1);
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
    private IndexLevel getLevelIncluding(E[] values) throws IOException {
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
    private IndexLevel getLevel(int level) throws IOException {
        while (levels.size() <= level) {
            int iLev = levels.size();
            if (iLev == 0) {
                // When creating the very first level, use the origin point, and no child index node
                levels.add(new IndexLevel(iLev, Arrays.copyOf(origin, origin.length), null));
            } else {
                // All higher levels are build on top of the lower levels (using the same current
                // value as the level below, and linking the index nodes together into the index
                // tree)
                IndexLevel lowerLevel = levels.get(iLev - 1);
                levels.add(new IndexLevel(iLev, lowerLevel.currentValues, lowerLevel.indexNode));
            }
        }
        return levels.get(level < 0 ? levels.size() + level : level);
    }

    @SuppressWarnings("unchecked")
    private E[] getProperties(Node node) {
        Object[] values = new Object[properties.length];
        for (int i = 0; i < properties.length; i++) {
            values[i] = getProperty(node, properties[i]);
        }
        return (E[])values;
    }

    @SuppressWarnings("unchecked")
    private E getProperty(Node node, String property) {
        return (E)(node == null ? null : node.getProperty(property, null));
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof MultiPropertyIndex<?>)){
            return false;
        }
        return (this.name.equalsIgnoreCase(((MultiPropertyIndex<?>)obj).name));
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
