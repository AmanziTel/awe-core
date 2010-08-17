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
 * Gpeh Report type
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public enum GpehReportType {
    IDCM_INTRA("INTRA-FREQUENCY ICDM"),
    IDCM_INTER("INTER-FREQUENCY ICDM"),
    CELL_RF_CORRELATION("CELL RF CORRELATION"),
    CELL_RSCP_ANALYSIS("CELL RSCP ANALYSIS"),
    CELL_ECNO_ANALYSIS("CELL ECNO ANALYSIS"),
    UE_TX_POWER_ANALYSIS("UE TX POWER ANALYSIS"),
    NBAP_UL_INTERFERENCE("UL INTERFERENCE"),
    NBAP_DL_TX_CARRIER_POWER("DL TX CARRIER POWER"),
    NBAP_NON_HS_POWER("NON HS POWER"),
    NBAP_HSDS_REQUIRED_POWER("HSDS REQUIRED POWER");

    private final String id;

    private GpehReportType(String id) {
        this.id = id;
    }

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns NetworkTypes by its ID
     * 
     * @param enumId id of Node Type
     * @return NodeTypes or null
     */
    public static GpehReportType getEnumById(String enumId) {
        if (enumId == null) {
            return null;
        }
        for (GpehReportType call : GpehReportType.values()) {
            if (call.getId().equals(enumId)) {
                return call;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return id;
    }

}
