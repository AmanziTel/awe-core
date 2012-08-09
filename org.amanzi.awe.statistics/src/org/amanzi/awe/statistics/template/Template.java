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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.statistics.engine.IStatisticsHeader;
import org.amanzi.awe.statistics.functions.AggregationFunctions;
import org.amanzi.neo.services.DatasetService.DatasetTypes;

/**
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class Template {
    public enum DataType {
        ROMES, TEMS, GEOPTIMA, MONA, DINGLI, NEMO1, NEMO2, RNC_COUNTERS {

            @Override
            public String getTypeName() {
                return DatasetTypes.COUNTERS.getId();
            }
        },
        PERFORMANCE_COUNTERS {

            @Override
            public String getTypeName() {
                // DatasetTypes.PERFORMANCE_COUNTER.getId();
                return DatasetTypes.COUNTERS.getId();
            }
        },
        GRID {

            @Override
            public String getTypeName() {
                // DatasetTypes.GRID.getId();
                return DatasetTypes.COUNTERS.getId();
            }
        };
        public String getTypeName() {
            return name().toLowerCase();
        }
    }

    private final String templateName;
    private String author;
    private String date;
    private DataType type;
    private Map<String, String> metadata = new HashMap<String, String>();
    private final List<TemplateColumn> columns = new ArrayList<TemplateColumn>();

    public Template(final String name, final DataType type) {
        this.templateName = name;
        this.type = type;
    }

    public Template(final String name) {
        this.templateName = name;
    }

    public void add(final IStatisticsHeader header, final AggregationFunctions function, final String name) {
        columns.add(new TemplateColumn(header, function, name));
    }

    public void add(final IStatisticsHeader header, final String functionName, final String name) {
        columns.add(new TemplateColumn(header, AggregationFunctions.valueOf(functionName), name));
    }

    public void add(final IStatisticsHeader header, final AggregationFunctions function, final Threshold threshold, final String name) {
        columns.add(new TemplateColumn(header, function, threshold, name));
    }

    public void add(final TemplateColumn column) {
        columns.add(column);
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

    public TemplateColumn getColumnByName(final String name) {
        for (TemplateColumn col : columns) {
            if (col.getName().equals(name)) {
                return col;
            }
        }
        return null;
    }

    /**
     * @return Returns the metadata.
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }

    /**
     * @param metadata The metadata to set.
     */
    public void setMetadata(final HashMap<String, String> metadata) {
        this.metadata = metadata;
    }

    /**
     * @return Returns the author.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author The author to set.
     */
    public void setAuthor(final String author) {
        this.author = author;
    }

    /**
     * @return Returns the date.
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date The date to set.
     */
    public void setDate(final String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("Template('%s',%s,%s)", templateName, author, date));
        sb.append("\nmetadata:\n");
        sb.append(metadata).append("\ncolumns:\n");
        sb.append(columns);
        return sb.toString();
    }

}
