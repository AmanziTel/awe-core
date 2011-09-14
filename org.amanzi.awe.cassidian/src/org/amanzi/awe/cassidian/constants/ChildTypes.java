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
/**
 * 
 * <p>
 * contains names of main tag.
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public enum ChildTypes {

    COMPLEATE_GPS_DATA("compleateGpsData"),
    COMPLEATE_GPS_DATA_LIST("completeGpsDataList"),
    GPSDATA("gpsData"),
    TOC("toc"), 
    TTC("ttc"),
    EVENTS("events"),
    PESQ_RESULT("pesqResult"),
    TNS_ROOT("tns:interfaceData"),
    PROBE_ID_NUMBER_MAP("probeIDNumberMap"),
    COMMON_TEST_DATA("commonTestData"),
    SERVING_DATA("servingData"),
    NEIGHBOR_DATA("neighborData"),
    NEIGHBOR_DETAILS("neighborDetails"),
    NTPQ("ntpq"),
    MPT_SYNC("mptSync"),
    GROUP_ATTACH("groupAttach"),
    ATTACHMENT("attach"), 
    INCONCLUSIVE("isInconclusive"),
    SEND_MSG("sendMsg"),
    SEND_REPORT("sendReport"),
    ITSI_ATTACH("itsiAttach"),
    HANDOVER("handover"),
    CELL_RESEL("cellResel"),
    RECIEVE_MSG("receiveMsg");
    
      
    private String header;

    ChildTypes(String s) {
        header = s;
    }

    public String getId() {
        return header;
    }

}
