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

import org.amanzi.neo.core.database.nodes.CellID;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.splash.report.IReportPart;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author user
 * @since 1.0.0
 */
public class Chart implements IReportPart {
    private String name;
    private Pair<CellID, CellID> categories;
    private Pair<CellID, CellID> values;
    private String sheet;
    private int order;

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
    public Chart(String name) {
        super();
        this.name = name;
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
     * @return Returns the sheet.
     */
    public String getSheet() {
        return sheet;
    }

    /**
     * @param sheet The sheet to set.
     */
    public void setSheet(String sheet) {
        this.sheet = sheet;
    }

    /**
     * @param order The order to set.
     */
    public void setOrder(int order) {
        this.order = order;
    }

   

    @Override
    public String getScript() {
        return new StringBuffer("chart '").append(name).append("' do\n").append("self.sheet='").append(sheet).append(
                "'\nself.categories=").append(categories.l()).append("..").append(categories.r()).append("\nself.values=").append(
                values.l()).append("..").append(values.r()).append("\nend").toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((categories == null) ? 0 : categories.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + order;
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
        if (order != other.order)
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
