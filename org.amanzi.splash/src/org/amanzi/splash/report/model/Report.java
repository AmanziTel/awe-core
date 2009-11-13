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
import java.util.Date;
import java.util.List;

import org.amanzi.splash.report.IReportPart;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author user
 * @since 1.0.0
 */
public class Report {
    private String name;
    private String date;
    private String author;
    private List<IReportPart> parts = new ArrayList<IReportPart>(0);

    /**
     * @param name
     * @param date
     * @param author
     */
    public Report(String name, String date, String author) {
        super();
        this.name = name;
        this.date = date;
        this.author = author;
    }

    /**
     * @param name
     */
    public Report(String name) {
        super();
        this.name = name;
    }

    public Report() {

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
     * @return Returns the date.
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date The date to set.
     */
    public void setDate(String date) {
        System.out.println("java setDate: " + date);
        this.date = date;
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
    public void setAuthor(String author) {
        System.out.println("java setAuthor: " + author);
        this.author = author;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(super.toString());
        sb.append(" name: ").append(name).append("; author: ").append(author).append("; date: ").append(date).append(
                partsToString());
        return sb.toString();
    }

    private String partsToString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\nParts:");
        for (IReportPart part : parts) {
            sb.append("\n").append(part.getClass().getName()).append(" ");
            if (part instanceof ReportText) {
                sb.append(((ReportText)part).getText());
            } else if (part instanceof Chart) {
                sb.append(((Chart)part).getName());

            }
        }
        return sb.toString();
    }

    public void addText(ReportText text) {
        parts.add(text);
    }

    /**
     * @return Returns the parts.
     */
    public List<IReportPart> getParts() {
        return parts;
    }

    public void addPart(IReportPart part) {
        System.out.println("java addPart: " + part);
        parts.add(part);
    }

    public String getScript() {
        StringBuffer sb=  new StringBuffer("report '").append(name).append("' do\n").append("author '").append(author).append("'\n").append("date '")
                .append(date).append("'");
        for (IReportPart part:parts){
            sb.append("\n").append(part.getScript());
        }
        return sb.append("\nend").toString();

    }

    public void addImage(ReportImage imagePart) {
    }

}
