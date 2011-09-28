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

package org.amanzi.awe.cassidian.loader.parser;

import java.io.File;

import org.amanzi.awe.cassidian.constants.ChildTypes;
import org.amanzi.awe.cassidian.structure.Attachment;
import org.amanzi.awe.cassidian.structure.CellReselection;
import org.amanzi.awe.cassidian.structure.CommonTestData;
import org.amanzi.awe.cassidian.structure.CompleteGpsData;
import org.amanzi.awe.cassidian.structure.CompleteGpsDataList;
import org.amanzi.awe.cassidian.structure.EventsElement;
import org.amanzi.awe.cassidian.structure.GPSData;
import org.amanzi.awe.cassidian.structure.GroupAttach;
import org.amanzi.awe.cassidian.structure.Handover;
import org.amanzi.awe.cassidian.structure.InconclusiveElement;
import org.amanzi.awe.cassidian.structure.ItsiAttach;
import org.amanzi.awe.cassidian.structure.MPTSync;
import org.amanzi.awe.cassidian.structure.NeighborData;
import org.amanzi.awe.cassidian.structure.NeighborDetails;
import org.amanzi.awe.cassidian.structure.Ntpq;
import org.amanzi.awe.cassidian.structure.PESQResultElement;
import org.amanzi.awe.cassidian.structure.ProbeIDNumberMap;
import org.amanzi.awe.cassidian.structure.RecieveMsg;
import org.amanzi.awe.cassidian.structure.SendMsg;
import org.amanzi.awe.cassidian.structure.SendReport;
import org.amanzi.awe.cassidian.structure.ServingData;
import org.amanzi.awe.cassidian.structure.TNSElement;
import org.amanzi.awe.cassidian.structure.TOCElement;
import org.amanzi.awe.cassidian.structure.TTCElement;

/**
 * <p>
 * contain parsed tagsname
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class Handler extends AbstractHandler {
    Handler() {
        super();
        innerElements.put(ChildTypes.PESQ_RESULT.getId(), PESQResultElement.class);
        innerElements.put(ChildTypes.TOC.getId(), TOCElement.class);
        innerElements.put(ChildTypes.TTC.getId(), TTCElement.class);
        innerElements.put(ChildTypes.COMPLEATE_GPS_DATA_LIST.getId(), CompleteGpsDataList.class);
        innerElements.put(ChildTypes.COMPLEATE_GPS_DATA.getId(), CompleteGpsData.class);
        innerElements.put(ChildTypes.PROBE_ID_NUMBER_MAP.getId(), ProbeIDNumberMap.class);
        innerElements.put(ChildTypes.SERVING_DATA.getId(), ServingData.class);
        innerElements.put(ChildTypes.NEIGHBOR_DATA.getId(), NeighborData.class);
        innerElements.put(ChildTypes.NEIGHBOR_DETAILS.getId(), NeighborDetails.class);
        innerElements.put(ChildTypes.NTPQ.getId(), Ntpq.class);
        innerElements.put(ChildTypes.MPT_SYNC.getId(), MPTSync.class);
        innerElements.put(ChildTypes.GROUP_ATTACH.getId(), GroupAttach.class);
        innerElements.put(ChildTypes.ATTACHMENT.getId(), Attachment.class);
        innerElements.put(ChildTypes.INCONCLUSIVE.getId(), InconclusiveElement.class);
        innerElements.put(ChildTypes.SEND_MSG.getId(), SendMsg.class);
        innerElements.put(ChildTypes.RECIEVE_MSG.getId(), RecieveMsg.class);
        innerElements.put(ChildTypes.SEND_REPORT.getId(), SendReport.class);
        innerElements.put(ChildTypes.ITSI_ATTACH.getId(), ItsiAttach.class);
        innerElements.put(ChildTypes.CELL_RESEL.getId(), CellReselection.class);
        innerElements.put(ChildTypes.HANDOVER.getId(), Handover.class);

        mainElements.put(ChildTypes.EVENTS.getId(), EventsElement.class);
        mainElements.put(ChildTypes.GPSDATA.getId(), GPSData.class);
        mainElements.put(ChildTypes.COMMON_TEST_DATA.getId(), CommonTestData.class);
    }

    public TNSElement parseElement(File file) {
        tns= new TNSElement();
        parse(file);
        return tns;
    }

}
