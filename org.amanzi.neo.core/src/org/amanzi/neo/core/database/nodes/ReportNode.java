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

package org.amanzi.neo.core.database.nodes;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.neo4j.api.core.Node;

/**
 * Node for a report
 * 
 * @author Pechko E.
 * @since 1.0.0
 */
public class ReportNode extends AbstractNode {
    public static final String REPORT_NAME = "Report name";
    public static final String REPORT_DATE = "Report date";
    public static final String REPORT_AUTHOR = "Report author";

    public ReportNode(Node node) {
        super(node);
        setParameter(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.REPORT_TYPE.getId());
    }

    public static ReportNode fromNode(Node node) {
        return new ReportNode(node);
    }

    public void setReportName(String reportName) {
        setParameter(REPORT_NAME, reportName);
        setParameter(INeoConstants.PROPERTY_NAME_NAME, reportName);
    }

    public void setReportDate(String reportDate) {
        setParameter(REPORT_DATE, reportDate);
    }

    public void setReportAuthor(String reportAuthor) {
        setParameter(REPORT_AUTHOR, reportAuthor);
    }

    public String getReportName() {
        return (String)getParameter(REPORT_NAME);
    }

    public String getReportDate() {
        return (String)getParameter(REPORT_DATE);
    }

    public String getReportAuthor() {
        return (String)getParameter(REPORT_AUTHOR);
    }

    /**
     * Adds a text to a report
     * 
     * @param text a text to be added
     */
    public void addReportText(TextNode text) {
        addRelationship(SplashRelationshipTypes.REPORT_TEXT, text.getUnderlyingNode());
    }
}
