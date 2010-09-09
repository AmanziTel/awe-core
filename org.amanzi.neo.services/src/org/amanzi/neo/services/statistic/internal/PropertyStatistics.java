package org.amanzi.neo.services.statistic.internal;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.services.statistic.ChangeClassRule;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.kernel.Traversal;

/**
 * The Class PropertyStatistics.
 */
public class PropertyStatistics {
    private static final TraversalDescription PROPERTYS=Traversal.description().depthFirst().relationships(StatisticRelationshipTypes.PROPERTY, Direction.OUTGOING).uniqueness(Uniqueness.NONE).filter(Traversal.returnAllButStartNode()).prune(Traversal.pruneAfterDepth( 1));

    /** The property name. */
    private final String propertyName;
    
    /** The klass. */
    private Class< ? > klass=null;
    
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
    private boolean isChanged;

    /**
     * Instantiates a new property statistics.
     * 
     * @param propertyName the property name
     * @param rule the rule
     */
    public PropertyStatistics(String propertyName){
        super();
        this.propertyName = propertyName;
        this.rule =ChangeClassRule.REMOVE_OLD_CLASS;
        isChanged=false;
    }
    public void load(Node vaultNode){
        clearStatistic();
        
        isChanged=false;
        
    }
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
       this(propertyName,rule);
        setClass(klass);
    }

    /**
     * Adds the new value.
     *
     * @param value the value
     * @return true, if successful
     */
    public boolean addNewValue(Object value) {
        if (value == null) {
            return false;
        }
        Class< ? extends Object> classValue = value.getClass();
        if (klass == null) {
            setClass(classValue);
        }
        if (klass == classValue) {
            addValueToStatistic(value);
        } else {
            switch (rule) {
            case REMOVE_OLD_CLASS:
                clearStatistic();
                setClass(classValue);
                addValueToStatistic(value);
                break;
            default/* ignore variant */:
                return false;
            }
        }
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
    //only by one field - propertyName
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
        klass=classValue;
        isComparable=Comparable.class.isAssignableFrom(classValue);
    }


    /**
     * Clear statistic.
     */
    private void clearStatistic() {
        count = 0;
        values.clear();
        minValue=null;
        maxValue=null;

    }


    /**
     * Adds the value to statistic.
     *
     * @param value the value
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void addValueToStatistic(Object value) {
        count++;
        if (isComparable){
            if (minValue==null){
                minValue=value;
            }else if(((Comparable)minValue).compareTo((Comparable)value)>0){
                minValue=value;
            }
            if (maxValue==null){
                maxValue=value;
            }else if(((Comparable)maxValue).compareTo((Comparable)value)<0){
                maxValue=value;
            }
         //because from possible neo4j storing data only array is not comparable we use  isComparable for define: should we compute statistics by value
            if (values.size()<StatisticHandler.MAX_VALUES_SIZE){
                Long countStat=values.get(value);
                if (countStat==null){
                    countStat=0l;
                }
                values.put(value,1l+countStat);
            }else{
                //compute statistic only for first MAX_VALUES_SIZE values
                Long countStat=values.get(value);
                if (countStat!=null){
                    values.put(value,1l+countStat);
                }               
            }
        }
    }

    public boolean register(Class klass, ChangeClassRule rule) {
        if (this.klass!=null){
            setClass(klass);
            this.rule=rule;
            return true;
        }
        return false;
    }
    /**
     *
     * @param vaultNode
     * @return
     */
    public static Map<String,PropertyStatistics> loadProperties(Node vaultNode) {
        return null;
    }
    
}