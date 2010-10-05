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

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.GisProperties;
import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * contains base functionality for Dataset saver
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public abstract class DatasetSaver<T extends BaseTransferData> extends AbstractHeaderSaver<T> {

    /**
     * Creates the mp location.
     * 
     * @param lastMLocation the last m location
     * @param element the element
     * @param time the time
     * @param timestamp the timestamp
     * @param latitude the latitude
     * @param longitude the longitude
     * @return the node
     */
    protected Node createMpLocation(Node lastMLocation, T element, String time, Long timestamp, Number latitude, Number longitude) {
        if (latitude == null || latitude.longValue() == 0 || longitude == null || longitude.longValue() == 0) {
            return lastMLocation;
        }
        if (lastMLocation != null) {
            lastMLocation.setProperty(INeoConstants.PROPERTY_LAST_LINE_NAME, element.getLine() - 1);
        }
        lastMLocation = service.createNode(NodeTypes.MP, time);
        String mpId = NodeTypes.MP.getId();
        statistic.increaseTypeCount(rootname, mpId, 1);
        updateTx(1, 0);
        setProperty(rootname, mpId, lastMLocation, INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
        setProperty(rootname, mpId, lastMLocation, INeoConstants.PROPERTY_LAT_NAME, latitude.doubleValue());
        setProperty(rootname, mpId, lastMLocation, INeoConstants.PROPERTY_LON_NAME, longitude.doubleValue());
        lastMLocation.setProperty(INeoConstants.PROPERTY_FIRST_LINE_NAME, element.getLine());
        index(lastMLocation);
        GisProperties gisProperties = getGisProperties(rootNode);
        gisProperties.updateBBox(latitude.doubleValue(), longitude.doubleValue());
        gisProperties.checkCRS(latitude.doubleValue(), longitude.doubleValue(), null);
        return lastMLocation;
    }


}
