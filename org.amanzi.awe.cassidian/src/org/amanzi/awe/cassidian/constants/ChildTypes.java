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

package org.amanzi.awe.cassidian.constants;

public enum ChildTypes {

    COMPLEATE_GPS_DATA("compleateGpsData"),
    COMPLEATE_GPS_DATA_LIST("completeGpsDataList"),
    GPSDATA("gpsData"), TOC("toc"), 
    TTC("ttc"),
    EVENTS("events"),
    PESQ_RESULT("pesqResult"),
    TNS_ROOT("tns:interfaceData"),
    PROBE_ID_NUMBER_MAP("probeIDNumberMap"),
    COMMON_TEST_DATA("commonTestData"),
    SERVING_DATA("servingData");
    private String header;

    ChildTypes(String s) {
        header = s;
    }

    public String getId() {
        return header;
    }

}
