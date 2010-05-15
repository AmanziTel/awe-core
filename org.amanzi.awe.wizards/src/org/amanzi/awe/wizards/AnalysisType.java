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

package org.amanzi.awe.wizards;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public enum AnalysisType {
    ANALYZE_COUNTERS("counters"), ANALYZE_KPIS("counters"), ANALYZE_PROPERTIES("counters"), ANALYZE_EVENTS("counters");
    private String type;

    /**
     * @param type
     */
    private AnalysisType(String type) {
        this.type = type;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

}
