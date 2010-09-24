package org.amanzi.neo.services.statistic.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.db.manager.INeoDbService;
import org.amanzi.neo.services.statistic.ChangeClassRule;
import org.amanzi.neo.services.statistic.ISinglePropertyStat;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.helpers.Predicate;
import org.neo4j.kernel.Traversal;

/**
 * The Class PropertyStatistics.
 */
public class PropertyStatistics implements ISinglePropertyStat{

    /** The Constant PROPERTYS. */
    private static final TraversalDescription PROPERTYS = Traversal.description().depthFirst().relationships(StatisticRelationshipTypes.PROPERTY, Direction.OUTGOING)
            .uniqueness(Uniqueness.NONE).filter(Traversal.returnAllButStartNode()).prune(Traversal.pruneAfterDepth(1));

    /** The property name. */
    private final String propertyName;

    /** The klass. */
    @SuppressWarnings("rawtypes")
    private Class klass = null;



    /** The rule. */
    private ChangeClassRule rule;

    /** The min value. */
    private Object minValue;

    /** The max value. */
    private Object maxValue;

    /** The count. */
    private long count;

    /** The values. */
    private Map<Object, Long> values = new HashMap<Object, Long>();

    /** The is comparable. */
    private boolean isComparable;

    /** The is changed. */
    private boolean isChanged;

    /** The parent. */
    private Node parent;

    /** The prop node. */
    private Node propertyNode;

    /**
     * Instantiates a new property statistics.
     * 
     * @param propertyName the property name
     */
    public PropertyStatistics(String propertyName) {
        super();
        this.propertyName = propertyName;
        this.rule = ChangeClassRule.REMOVE_OLD_CLASS;
        isChanged = false;
    }

    /**
     * Load.
     * 
     * @param vaultNode the vault node
     * @param propNode the prop node
     */
    public void load(Node vaultNode, Node propNode) {
        clearStatistic();
        this.parent = vaultNode;
        this.propertyNode = propNode;
        String klassName = (String)propNode.getProperty(StatisticProperties.CLASS, null);
        if (klassName != null) {
            Class< ? > classValue;
            try {
                classValue = Class.forName(klassName);
                setClass(classValue);
            } catch (ClassNotFoundException e) {
                // TODO Handle ClassNotFoundException
                e.printStackTrace();
            }
        }
        count = (Long)propNode.getProperty(StatisticProperties.COUNT, 0l);
        int statCount = (Integer)propNode.getProperty(StatisticProperties.STAT_SIZE, 0);
        for (int i = 0; i < statCount; i++) {
            long coun = (Long)propNode.getProperty(StatisticProperties.VALUE_COUNT + i, 0l);
            Object key = propNode.getProperty(StatisticProperties.VALUE_KEY + i);
            values.put(key, coun);
        }
        isChanged = false;

    }

    /**
     * Instantiates a new property statistics.
     * 
     * @param propertyName the property name
     * @param rule the rule
     */
    public PropertyStatistics(String propertyName, ChangeClassRule rule) {
        this(propertyName);
        this.rule = rule;
    }

    /**
     * Instantiates a new property statistics.
     * 
     * @param propertyName the property name
     * @param klass the klass
     * @param rule the rule
     */
    public PropertyStatistics(String propertyName, Class< ? > klass, ChangeClassRule rule) {
        this(propertyName, rule);
        setClass(klass);
    }

    /**
     * Adds the new value.
     * 
     * @param value the value
     * @param count 
     * @return true, if successful
     */
    public boolean addNewValue(Object value, int count) {
        if (value == null) {
            return false;
        }
        Class< ? extends Object> classValue = value.getClass();
        if (klass == null) {
            setClass(classValue);
        }
        if (klass == classValue) {
            addValueToStatistic(value,count);
        } else {
            switch (rule) {
            case REMOVE_OLD_CLASS:
                clearStatistic();
                setClass(classValue);
                addValueToStatistic(value,count);
                break;
            default/* ignore variant */:
                return false;
            }
        }
        isChanged=true;
        return true;

    }

    /**
     * Hash code.
     * 
     * @return the int
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((propertyName == null) ? 0 : propertyName.hashCode());
        return result;
    }

    /**
     * Equals.
     * 
     * @param obj the obj
     * @return true, if successful
     */
    @Override
    // only by one field - propertyName
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PropertyStatistics other = (PropertyStatistics)obj;
        if (propertyName == null) {
            if (other.propertyName != null)
                return false;
        } else if (!propertyName.equals(other.propertyName))
            return false;
        return true;
    }

    /**
     * Sets the class.
     * 
     * @param classValue the new class
     */
    private void setClass(Class< ? extends Object> classValue) {
        klass = classValue;
        isComparable = Comparable.class.isAssignableFrom(classValue);
    }

    /**
     * Clear statistic.
     */
    private void clearStatistic() {
        count = 0;
        values.clear();
        minValue = null;
        maxValue = null;

    }

    /**
     * Adds the value to statistic.
     * 
     * @param value the value
     * @param countVal 
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void addValueToStatistic(Object value, int countVal) {
        isChanged = true;
        count+=countVal;
        if (isComparable&&countVal>0) {
            if (minValue == null) {
                minValue = value;
            } else if (((Comparable)minValue).compareTo((Comparable)value) > 0) {
                minValue = value;
            }
            if (maxValue == null) {
                maxValue = value;
            } else if (((Comparable)maxValue).compareTo((Comparable)value) < 0) {
                maxValue = value;
            }
            // because from possible neo4j storing data only array is not comparable we use
            // isComparable for define: should we compute statistics by value
            if (values.size() < StatisticHandler.MAX_VALUES_SIZE) {
                Long countStat = values.get(value);
                if (countStat == null) {
                    countStat = 0l;
                }
                values.put(value, 1l + countStat);
            } else {
                // compute statistic only for first MAX_VALUES_SIZE values
                Long countStat = values.get(value);
                if (countStat != null) {
                    values.put(value, 1l + countStat);
                }
            }
        }
    }

    /**
     * Register.
     * 
     * @param klass the klass
     * @param rule the rule
     * @return true, if successful
     */
    public boolean register(Class<?> klass, ChangeClassRule rule) {
        if (this.klass == null) {
            setClass(klass);
            this.rule = rule;
            return true;
        }
        return false;
    }

    /**
     * Load properties.
     * 
     * @param vaultNode the vault node
     * @return the map
     */
    public static Map<String, PropertyStatistics> loadProperties(Node vaultNode) {
        Map<String, PropertyStatistics> result = new HashMap<String, PropertyStatistics>();
        for (Path path : PROPERTYS.traverse(vaultNode)) {
            String key = (String)path.endNode().getProperty(StatisticProperties.KEY);
            PropertyStatistics properties = new PropertyStatistics(key);
            result.put(key, properties);
            properties.load(vaultNode, path.endNode());
        }
        return result;
    }

    /**
     * Save vault.
     * 
     * @param service the service
     * @param parentNode the parent node
     * @param endNode the end node
     */
    public void save(INeoDbService service, Node parentNode, Node propNode) {
        if (isChanged(parentNode)) {
            rule=ChangeClassRule.IGNORE_NEW_CLASS;
            parent = parentNode;
            Transaction tx = service.beginTx();
            try {
                if (propNode == null) {
                    Iterator<Node> iterator = PROPERTYS.filter(new Predicate<Path>() {

                        @Override
                        public boolean accept(Path paramT) {
                            return propertyName.equals(paramT.endNode().getProperty(StatisticProperties.KEY, ""));
                        }
                    }).traverse(parent).nodes().iterator();
                    if (iterator.hasNext()) {
                        propertyNode = iterator.next();
                    } else {
                        propertyNode = service.createNode();
                        propertyNode.setProperty(StatisticProperties.KEY, propertyName);
                        parent.createRelationshipTo(propertyNode, StatisticRelationshipTypes.PROPERTY);
                    }
                } else {
                    propertyNode = propNode;
                }
                propertyNode.setProperty(StatisticProperties.COUNT, count);
                if (klass!=null){
                    propertyNode.setProperty(StatisticProperties.CLASS, klass.getCanonicalName());
                }
                if (isComparable){
                    if (minValue!=null){
                        propertyNode.setProperty(StatisticProperties.MIN_VALUE, minValue);
                    }
                    if (maxValue!=null){
                        propertyNode.setProperty(StatisticProperties.MAX_VALUE, maxValue);
                    }
                }
                propertyNode.setProperty(StatisticProperties.STAT_SIZE, values.size());
                int i = 0;
                for (Map.Entry<Object, Long> entry : values.entrySet()) {
                    propertyNode.setProperty(StatisticProperties.VALUE_COUNT + i, entry.getValue());
                    propertyNode.setProperty(StatisticProperties.VALUE_KEY + i, entry.getKey());
                    i++;
                }
                // TODO clear properties if necessary (and if BatchInserter support delete of
                // properties)
                tx.success();
            } finally {
                tx.finish();
            }
        }
        isChanged = false;
    }

    /**
     * Checks if is changed.
     * 
     * @param parentNode the parent node
     * @return true, if is changed
     */
    public boolean isChanged(Node parentNode) {
        return isChanged || parent == null || !parentNode.equals(this.parent);
    }
    public Class< ? > getKlass() {
        return klass;
    }

    @Override
    public long getCount() {
        return count;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getType() {
        return klass;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Comparable getMin() {
        return (Comparable)minValue;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Comparable getMax() {
        return (Comparable)maxValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object parseValue(String value) {
        if (klass==null){
            return autoParse(value);
        }
        if (klass==String.class){
            return value;
        }
        try {
            return NeoUtils.getNumberValue(klass,  value);
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return autoParse(value);
    }
    
    public static Object autoParse(String value) {
        try{
        if (value.contains(".")){
            return Float.parseFloat(value);
        }else{
            return Integer.parseInt(value);
        }
        }catch (Exception e) {
            return value;
        }
    }
    @Override
    public Map<Object, Long> getValueMap() {
        return Collections.unmodifiableMap(values);
    }
}