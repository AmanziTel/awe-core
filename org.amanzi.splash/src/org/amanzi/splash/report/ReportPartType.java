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

package org.amanzi.splash.report;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Pechko_E
 * @since 1.0.0
 */
public enum ReportPartType {
    TEXT("text"), CHART("chart"), IMAGE("image"), TABLE("table");
    private String text;

    /**
     * @param text
     */
    private ReportPartType(String text) {
        this.text = text;
    }

    /**
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }

    public static String getTypesAsRegex() {
        StringBuffer sb=new StringBuffer();
        ReportPartType[] vals = values();
        for (int i=0;i<vals.length;i++){
            sb.append(vals[i].getText()).append(i<vals.length-1?"|":"");
        }
        return sb.toString();

    }

}
