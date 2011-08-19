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

package org.amanzi.awe.cassidian.structure;

import java.util.LinkedList;
import java.util.List;

import org.amanzi.awe.cassidian.constants.ChildTypes;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class TNSElement implements IXmlTag {
    private List<EventsElement> events = new LinkedList<EventsElement>();
    private List<GPSData> gps = new LinkedList<GPSData>();
    private List<CommonTestData> ctd = new LinkedList<CommonTestData>();

    /**
     * @return Returns the ctd.
     */
    public List<CommonTestData> getCtd() {
        return ctd;
    }

    /**
     * @param ctd The ctd to set.
     */
    public void setCtd(List<CommonTestData> ctd) {
        this.ctd = ctd;
    }

    public void addMembertoCommonTestList(CommonTestData member){
        ctd.add(member);
    }
    /**
     * @return added Gps to memberList;
     */
    public void addMembertoGPSList(GPSData member) {
        gps.add(member);
    }

    public List<GPSData> getGps() {
        return gps;
    }

    /**
     * @param gps The gps to set.
     */
    public void setGps(List<GPSData> gps) {
        this.gps = gps;
    }

    /**
     * @return Returns the events.
     */
    public List<EventsElement> getEvents() {
        return events;
    }

    /**
     * @param events The events to set.
     */
    public void setEvents(List<EventsElement> events) {
        this.events = events;
    }

    public void addMembertoEventsList(EventsElement class1) {
        events.add(class1);
    }

    /**
     * @return
     */
    public String getType() {
        return ChildTypes.TNS_ROOT.getId();
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (tagName.equalsIgnoreCase(ChildTypes.EVENTS.getId())) {
            addMembertoEventsList((EventsElement)value);
        } else if (tagName.equalsIgnoreCase(ChildTypes.GPSDATA.getId())) {
            addMembertoGPSList((GPSData)value);
        }else if (tagName.equalsIgnoreCase(ChildTypes.COMMON_TEST_DATA.getId())) {
            addMembertoCommonTestList((CommonTestData)value);
        }
    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (tagName.equalsIgnoreCase(ChildTypes.EVENTS.getId())) {
            return events;
        }
        return null;
    }

}
