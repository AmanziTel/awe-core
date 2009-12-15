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

package org.amanzi.splash.report.model;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.core.database.nodes.CellID;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.splash.chart.ChartType;
import org.amanzi.splash.report.IReportPart;
import org.amanzi.splash.report.ReportPartType;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.AbstractDataset;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class Chart implements IReportPart {
    private String name;
    private ChartType chartType;
    private Pair<CellID, CellID> categories;
    private Pair<CellID, CellID> values;
    private String sheet;
    private String categoriesProperty;
    private String[] valuesProperties;
    private Long[] nodeIds;
    private List<String> errors = new ArrayList<String>();
    private AbstractDataset dataset;
    //JFReeChart settings
    private String title="";
    private PlotOrientation orientation=PlotOrientation.VERTICAL;
    private String domainAxisLabel="";
    private String rangeAxisLabel="Value";
//
    private int index;
    /**
     * @param name
     * @param categories
     * @param values
     * @param sheet
     */
    public Chart(String name, Pair<CellID, CellID> categories, Pair<CellID, CellID> values, String sheet) {
        super();
        this.name = name;
        this.categories = categories;
        this.values = values;
        this.sheet = sheet;
    }

    /**
     * @param name
     * @param sheet
     */
    public Chart(String name, String sheet) {
        super();
        this.name = name;
        this.sheet = sheet;
    }

    /**
     * @param name
     */
    public Chart(String title) {
        super();
        this.title = title;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the chartType.
     */
    public ChartType getChartType() {
        return chartType;
    }

    /**
     * @param chartType The chartType to set.
     */
    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    /**
     * @return Returns the categories.
     */
    public Pair<CellID, CellID> getCategories() {
        return categories;
    }

    /**
     * @param categories The categories to set.
     */
    public void setCategories(CellID start, CellID end) {
        this.categories = new Pair<CellID, CellID>(start, end);
    }

    /**
     * @param categories The categories to set.
     */

    /**
     * @return Returns the values.
     */
    public Pair<CellID, CellID> getValues() {
        return values;
    }

    /**
     * @param values The values to set.
     */
    public void setValues(CellID start, CellID end) {
        this.values = new Pair<CellID, CellID>(start, end);
    }

    /**
     * @return Returns the categoriesProperty.
     */
    public String getCategoriesProperty() {
        return categoriesProperty;
    }

    /**
     * @param categoriesProperty The categoriesProperty to set.
     */
    public void setCategoriesProperty(String categoriesProperty) {
        System.out.println("java setCategoriesProperty(" + categoriesProperty + ")");
        this.categoriesProperty = categoriesProperty;
    }

    /**
     * @return Returns the valuesProperties.
     */
    public String[] getValuesProperties() {
        return valuesProperties;
    }

    /**
     * @param valuesProperties The valuesProperties to set.
     */
    public void setValuesProperties(String[] valuesProperties) {
        System.out.println("java setValuesProperties(" + valuesProperties + ")");
        this.valuesProperties = valuesProperties;
    }

    /**
     * @return Returns the nodeIds.
     */
    public Long[] getNodeIds() {
        return nodeIds;
    }

    /**
     * @param nodeIds The nodeIds to set.
     */
    public void setNodeIds(Long[] nodeIds) {
        this.nodeIds = nodeIds;
    }

    /**
     * @return Returns the sheet.
     */
    public String getSheet() {
        return sheet;
    }

    public boolean isSheetBased() {
        return sheet != null && categories != null && values != null;
    }

    public boolean isNodeRangeBased() {
        return nodeIds != null && categoriesProperty != null && valuesProperties != null;
    }

    /**
     * @param sheet The sheet to set.
     */
    public void setSheet(String sheet) {
        this.sheet = sheet;
    }

    public void addError(String err) {
        errors.add(err);
    }

    public boolean hasErrors() {
        return errors.size() != 0;
    }

    public void clearErrors() {
        errors.clear();
    }

    /**
     * @return Returns the dataset.
     */
    public AbstractDataset getDataset() {
        return dataset;
    }

    /**
     * @param dataset The dataset to set.
     */
    public void setDataset(AbstractDataset dataset) {
        this.dataset = dataset;
    }

    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return Returns the orientation.
     */
    public PlotOrientation getOrientation() {
        return orientation;
    }

    /**
     * @param orientation The orientation to set.
     */
    public void setOrientation(PlotOrientation orientation) {
        this.orientation = orientation;
    }

    /**
     * @return Returns the domainAxisLabel.
     */
    public String getDomainAxisLabel() {
        return domainAxisLabel;
    }

    /**
     * @param domainAxisLabel The domainAxisLabel to set.
     */
    public void setDomainAxisLabel(String domainAxisLabel) {
        this.domainAxisLabel = domainAxisLabel;
    }

    /**
     * @return Returns the rangeAxisLabel.
     */
    public String getRangeAxisLabel() {
        return rangeAxisLabel;
    }

    /**
     * @param rangeAxisLabel The rangeAxisLabel to set.
     */
    public void setRangeAxisLabel(String rangeAxisLabel) {
        this.rangeAxisLabel = rangeAxisLabel;
    }

    @Override
    public String getScript() {
        return new StringBuffer("chart '").append(name).append("' do\n").append("self.sheet='").append(sheet).append(
                "'\nself.categories=").append(categories.l()).append("..").append(categories.r()).append("\nself.values=").append(
                values.l()).append("..").append(values.r()).append("\nend").toString();
    }

    /**
     * @return Returns the type.
     */
    public ReportPartType getType() {
        return ReportPartType.CHART;
    }

    
    /**
     * @return Returns the index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index The index to set.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((categories == null) ? 0 : categories.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((sheet == null) ? 0 : sheet.hashCode());
        result = prime * result + ((values == null) ? 0 : values.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Chart other = (Chart)obj;
        if (categories == null) {
            if (other.categories != null)
                return false;
        } else if (!categories.equals(other.categories))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (sheet == null) {
            if (other.sheet != null)
                return false;
        } else if (!sheet.equals(other.sheet))
            return false;
        if (values == null) {
            if (other.values != null)
                return false;
        } else if (!values.equals(other.values))
            return false;
        return true;
    }


}
