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

package org.amanzi.awe.statistics.entities.impl;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class AbstractFlaggedEntity extends AbstractEntity {

    private static final String BBOX_NAME = "bbox";

    /**
     * @param parent
     * @param current
     * @param type
     */
    protected AbstractFlaggedEntity(Node parent, Node current, INodeType type) {
        super(parent, current, type);
    }

    /**
     * @param nodeType
     */
    protected AbstractFlaggedEntity(INodeType nodeType) {
        super(nodeType);
    }

    /**
     * set or remove flagged. if true- set flaggedProperty to group else remove it from group node
     * 
     * @param flagged
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public void setFlagged(boolean flagged) throws IllegalNodeDataException, DatabaseException {
        if (flagged) {
            statisticService.setAnyProperty(rootNode, PROPERTY_FLAGGED_NAME, flagged);
        } else {
            statisticService.removeNodeProperty(rootNode, PROPERTY_FLAGGED_NAME);

        }
    }

    /**
     * return flagged value of group
     * 
     * @return
     */
    public boolean isFlagged() {
        Boolean isFlagged = (Boolean)statisticService.getNodeProperty(rootNode, PROPERTY_FLAGGED_NAME);
        if (isFlagged == null) {
            return Boolean.FALSE;
        }
        return isFlagged();
    }

    /**
     * @return
     */
    public double[] getBbox() {
        return (double[])statisticService.getNodeProperty(rootNode, BBOX_NAME);
    }

    /**
     * @param ure
     * @param cellNode
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public void updateBbox(ReferencedEnvelope ure) throws IllegalNodeDataException, DatabaseException {
        double[] existedBbox = getBbox();
        if (existedBbox != null) {
            double[] bbox = existedBbox;
            ReferencedEnvelope re = new ReferencedEnvelope(bbox[0], bbox[1], bbox[2], bbox[3], null);
            re.expandToInclude(ure);
            statisticService.setAnyProperty(rootNode, BBOX_NAME,
                    new double[] {re.getMinX(), re.getMaxX(), re.getMinY(), re.getMaxY()});
        } else {
            statisticService.setAnyProperty(rootNode, BBOX_NAME,
                    new double[] {ure.getMinX(), ure.getMaxX(), ure.getMinY(), ure.getMaxY()});
        }
    }
}
