package org.amanzi.neo.index;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import org.neo4j.api.core.Direction;
import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.Transaction;

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
    private String[] properties;
    private NeoService neo;
    private E[] origin;
    private MultiValueConverter<E> converter;
    private Node root;
    private int step;
    private ArrayList<IndexLevel> levels = new ArrayList<IndexLevel>();
    private int[] originIndices = null;

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
        @SuppressWarnings("unchecked")
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
        @SuppressWarnings("unchecked")
        private IndexLevel(int level, Node indexNode) throws IOException {
            this.level = level;
            this.stepSize = converter.stepSize(level, step);
            this.indices = (int[])indexNode.getProperty("index");
            this.min = converter.deserialize((byte[])indexNode.getProperty("min"));
            this.max = converter.deserialize((byte[])indexNode.getProperty("max"));
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
                indexNode.setProperty("level", level);
                indexNode.setProperty("min", converter.serialize(min));
                indexNode.setProperty("max", converter.serialize(max));
            }
            return indexNode;
        }

        private void linkTo(Node lowerNode) {
            if (indexNode != null && lowerNode != null) {
                indexNode.createRelationshipTo(lowerNode, PropertyIndex.NeoIndexRelationshipTypes.CHILD);
            }
        }

        private void searchChildrenOf(Node parentIndex) {
            for (Relationship rel : parentIndex.getRelationships(PropertyIndex.NeoIndexRelationshipTypes.CHILD, Direction.OUTGOING)) {
                Node child = rel.getEndNode();
                int[] testIndex = (int[])child.getProperty("index");
                if (Arrays.equals(testIndex, indices)) {
                    indexNode = child;
                    break;
                }
            }
        }

    }

    public interface MultiValueConverter<E> extends PropertyIndex.ValueConverter<E>{
        public Object serialize(Object[] data) throws IOException;

        public E[] deserialize(Object buffer) throws IOException;
    }

    public static class MultiFloatConverter extends PropertyIndex.FloatConverter implements MultiValueConverter<Float> {
        public MultiFloatConverter(float cluster) {
            super(cluster);
        }

        public Float[] deserialize(Object buffer) throws IOException {
            float[] data = (float[]) buffer;
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
    }

    public static class MultiDoubleConverter extends PropertyIndex.DoubleConverter implements MultiValueConverter<Double> {
        public MultiDoubleConverter(double cluster) {
            super(cluster);
        }

        public Double[] deserialize(Object buffer) throws IOException {
            double[] data = (double[]) buffer;
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
    }

    public MultiPropertyIndex(NeoService neo, Node reference, String name, String[] properties,
            MultiValueConverter<E> converter, int step) throws IOException {
        if (neo == null)
            throw new IllegalArgumentException("Index NeoService must exist");
        if (properties == null || properties.length < 1 || properties[0].length() < 1)
            throw new IllegalArgumentException("Index properties must be a non-empty array of non-empty strings");
        if (reference == null)
            reference = neo.getReferenceNode();
        this.neo = neo;
        this.properties = properties;
        this.step = step;
        this.converter = converter;
        for (Relationship relation : reference.getRelationships(PropertyIndex.NeoIndexRelationshipTypes.INDEX, Direction.OUTGOING)) {
            Node node = relation.getEndNode();
            if (node.getProperty("type", "").toString().equals("property_index")
                    && node.getProperty("name", "").toString().equals(name)
                    && Arrays.equals((String[])node.getProperty("properties", null),properties)) {
                this.root = node;
            }
        }
        if (this.root == null) {
            this.root = neo.createNode();
            root.setProperty("name", name);
            root.setProperty("properties", properties);
            root.setProperty("type", "property_index");
            reference.createRelationshipTo(root, PropertyIndex.NeoIndexRelationshipTypes.INDEX);
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
                this.origin = levels.get(0).min;
            }
        }
    }

    private Node getIndexChildOf(Node parent) {
        for (Relationship rel : parent.getRelationships(PropertyIndex.NeoIndexRelationshipTypes.CHILD, Direction.OUTGOING)) {
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
        if (root != null) {
            Node highestIndex = null;
            for (IndexLevel level : levels) {
                if (level.indexNode != null) {
                    highestIndex = level.indexNode;
                }
            }
            if (highestIndex != null) {
                // Deleting any previous starting relationships
                for (Relationship rel : root.getRelationships(PropertyIndex.NeoIndexRelationshipTypes.CHILD, Direction.OUTGOING)) {
                    rel.delete();
                }
                // Make a new one to the top node (might be same as before or higher level node
                root.createRelationshipTo(highestIndex, PropertyIndex.NeoIndexRelationshipTypes.CHILD);
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
     * @throws IOException 
     */
    public Node add(Node node) throws IOException {
        E[] values = getProperties(node);
        if (values != null) {
            if (origin == null) {
                origin = values;
            }
            Node indexNode = getIndexNode(values);
            indexNode.createRelationshipTo(node, PropertyIndex.NeoIndexRelationshipTypes.CHILD);
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
     * @throws IOException 
     */
    private IndexLevel getLevel(int level) throws IOException {
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
                levels.add(new IndexLevel(iLev, lowerLevel.currentValues, lowerLevel.indexNode));
            }
        }
        return levels.get(level);
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

    private static void expandToInclude(double[] bbox, double x, double y) {
        if (x < bbox[0]) {
            bbox[0] = x;
        }
        if (y < bbox[1]) {
            bbox[1] = y;
        }
        if (x > bbox[2]) {
            bbox[2] = x;
        }
        if (y > bbox[3]) {
            bbox[3] = y;
        }
    }

    /**
     * A main method for useful quick-turn-around testing and debugging of data parsing on various
     * sample files.
     * 
     * @param args
     */
    public static void main(String[] args) {
        Random random = new Random(0);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        EmbeddedNeo neo = new EmbeddedNeo("../../testing/neo");
        PropertyIndex.MultiTimer timer = new PropertyIndex.MultiTimer();
        Transaction tx = neo.beginTx();
        try {
            Node prevNode = null;
            long dataStartTime = format.parse("12:00:00").getTime();
            double speed = 0.00001;
            double direction = Math.PI / 4;
            double[] bbox = new double[] {Double.MAX_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};
            double latitude = 55.39528407F;
            double longitude = 14.14990186F;
            PropertyIndex<Long> timeIndex = new PropertyIndex<Long>(neo, neo.getReferenceNode(), "TimePropertyIndexTest",
                    "timestamp", new PropertyIndex.TimeIndexConverter(), 10);
            MultiPropertyIndex<Double> locationIndex = new MultiPropertyIndex<Double>(neo, neo.getReferenceNode(),
                    "LocationPropertyIndexTest", new String[] {"latitude", "longitude"}, new MultiDoubleConverter(0.001),
                    10);
            for (int i = 0; i < 10000; i++) {
                long timestamp = dataStartTime + i * (111 + random.nextInt(100));
                Date date = new Date(timestamp);
                Node node = neo.createNode();
                node.setProperty("time", format.format(date));
                node.setProperty("timestamp", timestamp);
                node.setProperty("value", (double)(random.nextInt(10)));
                node.setProperty("dbm", (double)(10 - random.nextInt(60)));
                speed += 0.000005 - 0.00001 * random.nextDouble(); // vary around the original speed
                direction += 0.1 * Math.PI * random.nextDouble(); // make a slow semi-circular
                // turn
                latitude += speed * Math.sin(direction);
                longitude += speed * Math.cos(direction);
                expandToInclude(bbox, longitude, latitude);
                node.setProperty("latitude", latitude);
                node.setProperty("longitude", longitude);
                if (prevNode != null) {
                    prevNode.createRelationshipTo(node, PropertyIndex.NeoIndexRelationshipTypes.NEXT);
                }
                prevNode = node;
                timeIndex.add(node);
                locationIndex.add(node);
                if (i % 1000 == 0) {
                    tx.success();
                    tx.finish();
                    timer.mark("commit:" + i);
                    tx = neo.beginTx();
                } else {
                    timer.mark(i);
                }
            }
            timeIndex.finishUp();
            locationIndex.finishUp();
            tx.success();
        } catch (Exception e) {
            System.err.println("Error running property index test: " + e);
            e.printStackTrace(System.err);
        } finally {
            tx.finish();
            neo.shutdown();
            timer.mark("shutdown");
            System.out.println("Ran test in " + timer.total() / 1000.0 + "s (averaged " + timer.average()
                    + "ms per data point - longest " + timer.max() + "ms)");
            System.out.println("\taverage: " + timer.average() + "ms");
            System.out.println("\tlongest: " + timer.max() + "ms");
            System.out.println("\tlast: " + timer.last() + "ms");
            timer.printHist(System.out);
        }
    }
}
