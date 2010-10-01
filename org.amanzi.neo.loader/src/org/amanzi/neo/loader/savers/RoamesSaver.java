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

package org.amanzi.neo.loader.savers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.GisProperties;
import org.amanzi.neo.loader.core.parser.HeaderTransferData;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class RoamesSaver extends DriveSaver<HeaderTransferData> {
    protected Double currentLatitude;
    protected Double currentLongitude;
    private Node lastMLocation;
    
    @Override
    public void finishSaveNewElement(HeaderTransferData element) {
    }

    @Override
    protected Calendar getWorkDate(HeaderTransferData element) {
        CharSequence filename = element.getFileName();
        Pattern p = Pattern.compile(".*_(\\d{6})_.*");
        Matcher m = p.matcher(filename);
        Date date=new Date();
        boolean correctTime=false;
        if (m.matches()) {
            String dateText = m.group(1);
            try {
                date = (new SimpleDateFormat("yyMMdd")).parse(dateText);
                correctTime=true;
            } catch (ParseException e) {
                error("Wrong filename format: " + filename);
            }
        } 
        if (correctTime) {
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }
        Calendar result = askTime(element);
        if (result == null) {
            applyToAll = true;
        }
        return result;

    }

    @Override
    protected void definePropertyMap(HeaderTransferData element) {
        Set<String> headers = element.keySet();
        defineHeader(headers, "time", new String[] {"time.*"});
        defineHeader(headers, "latitude", new String[] {".*latitude.*"});
        defineHeader(headers, "longitude", new String[] {".*longitude.*"});
        defineHeader(headers, "events", new String[] {"Event Type","event_type"});
        defineHeader(headers, "time", new String[] {"time", "Timestamp", "timestamp"});
        defineHeader(headers, INeoConstants.SECTOR_ID_PROPERTIES, new String[] {  ".*Server.*Report.*CI.*"});

    }

    @Override
    protected void addDriveIndexes() {
        try {
            addIndex(NodeTypes.M.getId(), service.getTimeIndexProperty(rootname));
            addIndex(NodeTypes.MP.getId(), service.getLocationIndexProperty(rootname));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    protected void fillRootNode(Node rootNode, HeaderTransferData element) {
    }

    @Override
    protected String getRootNodeType() {
        return NodeTypes.DATASET.getId();
    }

    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return NodeTypes.MP.getId();
    }
 @Override
public void save(HeaderTransferData element) {
    super.save(element);
    String time = getStringValue("time", element);
    Long timestamp = defineTimestamp(workDate, time);
 }

/**
 *
 * @param workDate
 * @param time
 * @return
 */
private Long defineTimestamp(Calendar workDate, String time) {
    return null;
}

}
