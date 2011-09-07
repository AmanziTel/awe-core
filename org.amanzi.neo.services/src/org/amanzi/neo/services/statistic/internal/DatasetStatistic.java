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

package org.amanzi.neo.services.statistic.internal;

import java.util.Collection;
import java.util.Set;

import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.services.statistic.ChangeClassRule;
import org.amanzi.neo.services.statistic.ISinglePropertyStat;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.utils.Utils;
import org.hsqldb.lib.StringUtil;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Dataset Statistic
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class DatasetStatistic implements IStatistic {

    private Node root;
    private StatisticHandler handler;

    public DatasetStatistic(Node root) {
        handler = new StatisticHandler(root);
        this.root = root;

    }

    /**
     * 
     */
    public DatasetStatistic() {
        handler = new StatisticHandler();
    }

    @Override
    public void save() {
       
        if (root != null) {
            handler.saveStatistic();
        }
    }

    /**
     *
     */
    public void init() {
       
        if (root != null) {
            handler.loadStatistic();
        }
    }

    @Override
    public boolean indexValue(String rootKey, String nodeType, String propertyName, Object propertyValue) {
        return handler.indexValue(rootKey, nodeType, propertyName, propertyValue);
    }

    @Override
    public Object parseValue(String rootname, String nodeType, String key, String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }
        PropertyStatistics prop = handler.findProperty(rootname, nodeType, key);
        if (prop == null) {
            return PropertyStatistics.autoParse(value);
        }
        return prop.parseValue(value);
    }

    @Override
    public void updateTypeCount(String rootKey, String nodeType, long count) {
        handler.increaseTypeCount(rootKey, nodeType, count);
    }
    @Override
    public void setTypeCount(String rootKey, String nodeType, long count) {
        handler.setTypeCount(rootKey, nodeType, count);
    }
    @Override
    public long getTotalCount(String rootKey, String nodeType) {
        return handler.getTotalCount(rootKey, nodeType);
    }

    @Override
    public void registerProperty(String rootKey, String nodeType, String name, Class klass, String defValue) {
        Object value = null;
        if (StringUtil.isEmpty(defValue)) {
            value = defValue;
        } else {
            if (Number.class.isAssignableFrom(klass)) {
                try {
                    value = Utils.getNumberValue(klass, defValue);
                } catch (Exception e) {
                    // TODO handle exception
                    e.printStackTrace();
                    value = null;
                }
            }else if (String.class==klass){
                value=defValue;
            }
        }
        if (handler.registerProperty(rootKey, nodeType, name, klass, ChangeClassRule.IGNORE_NEW_CLASS)) {
            handler.indexValue(rootKey, nodeType, name, value, 0);
        }

    }

    @Override
    public Collection<String> getPropertyNameCollection(String key, String nodeTypeId, Comparable<Class<?>> comparable) {
        return handler.getPropertyNameCollection(key, nodeTypeId, comparable);
    }

    @Override
    public ISinglePropertyStat findPropertyStatistic(String key, String nodeTypeId, String propertyName) {
        PropertyStatistics stat = handler.findProperty(key, nodeTypeId, propertyName);
        return stat;
    }

    @Override
    public <T> boolean updateValue(String rootKey, String nodeType, String propertyName, T newValue, T oldValue) {
        if (oldValue == null) {
            return indexValue(rootKey, nodeType, propertyName, newValue);
        } else if (newValue == null) {
            return deleteValue(rootKey, nodeType, propertyName, oldValue);
        } else {
            return updateNotNullValues(rootKey, nodeType, propertyName, newValue, oldValue);
        }
    }

    public boolean deleteValue(String rootKey, String nodeTypeId, String propertyName, Object valueToDelete) {
        PropertyStatistics stat = handler.findProperty(rootKey, nodeTypeId, propertyName);
        if (stat == null) {
            return false;
        }
        return stat.deleteValue(valueToDelete);
    }

    protected  <T extends Object> boolean updateNotNullValues(String rootKey, String nodeTypeId,String propertyName,T newValue,T oldValue){
        PropertyStatistics stat=handler.findProperty(rootKey,nodeTypeId,propertyName);
        if (stat==null){
            return false;
        }
        return stat.updateNotNullValues(newValue,oldValue);
    }

    @Override
    public Set<String> getRootKey() {
        return handler.getRootKey();
    }

    @Override
    public Set<String> getNodeTypeKey(String rootKey) {
        return  handler.getNodeTypeKey(rootKey);
    }
}
