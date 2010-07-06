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

package org.amanzi.awe.neighbours.gpeh;

/**
 * <p>
 * GpehReport enum.
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public enum GpehReport {
    RF_PERFORMANCE_ANALYSIS("RF Performance Analysis", GpehReportType.CELL_RSCP_ANALYSIS, GpehReportType.CELL_ECNO_ANALYSIS, GpehReportType.UE_TX_POWER_ANALYSIS,
            GpehReportType.CELL_RF_CORRELATION, GpehReportType.NBAP_UL_INTERFERENCE),
    INTERFERENCE_ANALYSIS("Interference Analysis", GpehReportType.IDCM_INTER, GpehReportType.IDCM_INTRA);

    private final String string;
    private final GpehReportType[] reportTypes;

    private GpehReport(String string, GpehReportType... gpehReportTypes) {
        this.string = string;
        this.reportTypes = gpehReportTypes;
    }

    public GpehReportType[] getReportypes() {
        return reportTypes;
    }

    public static GpehReport getEnumById(String enumId) {
        if (enumId == null) {
            return null;
        }
        for (GpehReport report : GpehReport.values()) {
            if (report.toString().equals(enumId)) {
                return report;
            }
        }
        return null;
    }

}
