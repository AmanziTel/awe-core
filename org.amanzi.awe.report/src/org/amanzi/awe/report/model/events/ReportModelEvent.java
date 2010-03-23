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

package org.amanzi.awe.report.model.events;

import org.amanzi.awe.report.model.IReportPart;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author user
 * @since 1.0.0
 */
public class ReportModelEvent {
    private ReportEventType type;
    private IReportPart source;
    private Object data;

    /**
     * @param type
     * @param source
     * @param data
     */
    public ReportModelEvent(ReportEventType type, IReportPart source, Object data) {
        this.type = type;
        this.source = source;
        this.data = data;
    }

    /**
     * @return Returns the type.
     */
    public ReportEventType getType() {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(ReportEventType type) {
        this.type = type;
    }

    /**
     * @return Returns the source.
     */
    public IReportPart getSource() {
        return source;
    }

    /**
     * @param source The source to set.
     */
    public void setSource(IReportPart source) {
        this.source = source;
    }

    /**
     * @return Returns the data.
     */
    public Object getData() {
        return data;
    }

    /**
     * @param data The data to set.
     */
    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ReportModelEvent [data=" + data + ", source=" + source + ", type=" + type + "]";
    }

}
