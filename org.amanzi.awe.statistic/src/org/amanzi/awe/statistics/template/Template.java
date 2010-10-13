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

package org.amanzi.awe.statistics.template;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.statistics.engine.IAggregationFunction;
import org.amanzi.awe.statistics.engine.IStatisticsHeader;
import org.amanzi.awe.statistics.functions.AggregationFunctions;
import org.amanzi.neo.core.enums.OssType;
import org.amanzi.neo.core.utils.Pair;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class Template {
    public enum DataType {
        ROMES, TEMS, NEMO1, NEMO2, RNC_COUNTERS {

            @Override
            public String getTypeName() {
                return OssType.COUNTER.getId();
            }
        },
        PERFORMANCE_COUNTERS {

            @Override
            public String getTypeName() {
                return OssType.PERFORMANCE_COUNTER.getId();
            }
        },
        GRID {
            
            @Override
            public String getTypeName() {
                return OssType.GRID.getId();
            }
        },
        GPEH {

            @Override
            public String getTypeName() {
                return OssType.GPEH.getId();
            }
        };
        public String getTypeName() {
            return name().toLowerCase();
        }
    }

    private String templateName;
    private DataType type;
    private List<TemplateColumn> columns = new ArrayList<TemplateColumn>();

    public Template(String name, DataType type) {
        this.templateName = name;
        this.type = type;
    }

    public Template(String name, Template baseTemplate) {
        // TODO
    }

    public Template(Node templateNode) {
        // TODO
    }

    public void add(IStatisticsHeader header, AggregationFunctions function, String name) {
        columns.add(new TemplateColumn(header, function, name));
    }

    public void add(IStatisticsHeader header, String functionName, String name) {
        columns.add(new TemplateColumn(header, AggregationFunctions.valueOf(functionName), name));
    }

    public void add(IStatisticsHeader header, AggregationFunctions function, Threshold threshold, String name) {
        columns.add(new TemplateColumn(header, function, threshold, name));
    }

    public void remove(IStatisticsHeader header, IAggregationFunction function) {
        // TODO
    }

    public void clearAll() {
        columns.clear();
    }

    /**
     * @return Returns the columns.
     */
    public List<TemplateColumn> getColumns() {
        return columns;
    }

    /**
     * @return Returns the templateName.
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * @return Returns the type.
     */
    public DataType getType() {
        return type;
    }

    public TemplateColumn getColumnByName(String name) {
        for (TemplateColumn col : columns) {
            if (col.getName().equals(name)) {
                return col;
            }
        }
        return null;
    }
    public void calculateAll(Node node){
        
    }

}
