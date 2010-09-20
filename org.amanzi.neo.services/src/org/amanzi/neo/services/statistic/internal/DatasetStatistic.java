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

import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.services.statistic.IStatistic;
import org.hsqldb.lib.StringUtil;
import org.neo4j.graphdb.Node;

/**
 * TODO implement
 * <p>
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class DatasetStatistic implements IStatistic {

    private Node root;
    private StatisticHandler handler;

    public DatasetStatistic(Node root) {
        this();
        this.root = root;

    }

    /**
     * 
     */
    public DatasetStatistic() {
         handler=new StatisticHandler();
    }

    @Override
    public void save() {
        if (root!=null){
            handler.saveStatistic(DatabaseManager.getInstance().getCurrentDatabaseService(),root);
        }
    }

    /**
     *
     */
    public void init() {
        if (root!=null){
            handler.loadStatistic(root);
        }
    }

    @Override
    public boolean indexValue(String rootKey, String nodeType, String propertyName, Object propertyValue) {
        return handler.indexValue(rootKey, nodeType, propertyName, propertyValue);
    }

    @Override
    public Object parseValue(String rootname, String nodeType, String key, String value) {
        if (StringUtil.isEmpty(value)){
            return null;
        }
        PropertyStatistics prop=handler.findProperty(rootname,nodeType,key);
        if (prop==null){
            return autoParse(value);
        }
        return parseV(prop.getKlass(),value);
    }


    /**
     * Parses the v.
     *
     * @param klass the klass
     * @param value the value
     * @return the object
     */
    private Object parseV(Class klass, String value) {
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


    /**
     * Auto parse.
     *
     * @param value the value
     * @return the object
     */
    private Object autoParse(String value) {
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
    public void increaseTypeCount(String rootKey, String nodeType, long count) {
        handler.increaseTypeCount(rootKey,  nodeType,  count);
    }

    @Override
    public long getTotalCount(String rootKey, String nodeType) {
        return handler.getTotalCount(rootKey,nodeType);
    }

}
