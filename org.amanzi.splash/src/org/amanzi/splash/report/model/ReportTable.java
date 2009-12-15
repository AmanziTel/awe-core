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

import org.amanzi.splash.report.IReportPart;
import org.amanzi.splash.report.ReportPartType;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Pechko_E
 * @since 1.0.0
 */
public class ReportTable implements IReportPart {
    private String title;
    private String[] headers;
    private List<String[]> tableItems=new ArrayList<String[]>();
    private int index;
    /**
     * @param title
     */
    public ReportTable(String title) {
        this.title = title;
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
     * @return Returns the headers.
     */
    public String[] getHeaders() {
        return headers;
    }

    /**
     * @param headers The headers to set.
     */
    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    /**
     * @return Returns the tableItems.
     */
    public List<String[]> getTableItems() {
        return tableItems;
    }

    public void addRow(String[] row){
        tableItems.add(row);
    }

    @Override
    public String getScript() {
        return null;
    }

    /**
     * @return Returns the type.
     */
    public ReportPartType getType() {
        return ReportPartType.TABLE;
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

}
