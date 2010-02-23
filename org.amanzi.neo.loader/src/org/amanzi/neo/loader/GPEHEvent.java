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

package org.amanzi.neo.loader;

import java.util.ArrayList;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class GPEHEvent implements IGPEHBlock{
    protected ArrayList<Event> events;
    /**
     * 
     */
    public GPEHEvent() {
        events=new ArrayList<Event>();
    }
    public void addEvent(Event event) {
        events.add(event);
    }
public static class Event{
    public int hour;
    public Integer minute;
    protected Integer second;
    protected Integer millisecond;
    protected Integer id;
    protected String notParsed;
    public Integer ueContextId;
    public Integer rncModuleId;
    public Integer cellID1;
    public Integer rncID1;
    public Integer cellID2;
public Integer rncID2;
public Integer cellID3;
public Integer rncID3;
public Integer cellID4;
public Integer rncID4;
}
@Override
public void setEndRecord(GPEHEnd end) {
}

}
