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
import org.amanzi.awe.cassidian.structure.CommonTestData;
import org.amanzi.awe.cassidian.structure.CompleteGpsData;
import org.amanzi.awe.cassidian.structure.CompleteGpsDataList;
import org.amanzi.awe.cassidian.structure.EventsElement;
import org.amanzi.awe.cassidian.structure.GPSData;
import org.amanzi.awe.cassidian.structure.PESQResultElement;
import org.amanzi.awe.cassidian.structure.ProbeIDNumberMap;
import org.amanzi.awe.cassidian.structure.ServingData;
import org.amanzi.awe.cassidian.structure.TNSElement;
import org.amanzi.awe.cassidian.structure.TOCElement;
import org.amanzi.awe.cassidian.structure.TTCElement;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class Handler extends AbstractHandler {
    Handler() {
        super();
        innerElement.put(ChildTypes.PESQ_RESULT.getId(), PESQResultElement.class);
        innerElement.put(ChildTypes.TOC.getId(), TOCElement.class);
        innerElement.put(ChildTypes.TTC.getId(), TTCElement.class);
        innerElement.put(ChildTypes.COMPLEATE_GPS_DATA_LIST.getId(), CompleteGpsDataList.class);
        innerElement.put(ChildTypes.COMPLEATE_GPS_DATA.getId(), CompleteGpsData.class);
        innerElement.put(ChildTypes.PROBE_ID_NUMBER_MAP.getId(), ProbeIDNumberMap.class);
        innerElement.put(ChildTypes.SERVING_DATA.getId(), ServingData.class);

        mainElement.put(ChildTypes.EVENTS.getId(), EventsElement.class);
        mainElement.put(ChildTypes.GPSDATA.getId(), GPSData.class);
        mainElement.put(ChildTypes.COMMON_TEST_DATA.getId(), CommonTestData.class);
    }

    public TNSElement parseElement(File file) {
        parse(file);
        return tns;
    }

}
