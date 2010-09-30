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

import java.util.Calendar;

import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.GisProperties;
import org.amanzi.neo.loader.core.parser.HeaderTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.IStructuredSaver;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class RoamesSaver extends AbstractHeaderSaver<HeaderTransferData> implements IStructuredSaver<HeaderTransferData> {
    protected boolean newElem;
    protected Calendar workDate;
    protected boolean applyToAll;
    protected Double currentLatitude;
    protected Double currentLongitude;
    private Node parent;
    private Node virtualParent;
    private long count;
    private Node lastMNode;
    private Node lastMsNode;
    private Node lastMLocation;
    private String previous_ms = null;
    private String previous_time = null;
    private int previous_pn_code = -1;
    private String virtualDatasetName;
    private Integer hours;
    @Override
    public void save(HeaderTransferData element) {
    }

    @Override
    public boolean beforeSaveNewElement(HeaderTransferData element) {+
        newElem=true;
    hours=null;
    //TODO define new latitude
    currentLatitude=null;
    currentLatitude=null; 
    virtualParent=null;
    workDate=getWorkDate(element);
    boolean result = workDate==null;
    parent=null;
    if (!result){
        parent=service.getFileNode(rootNode, element.getFileName());
        lastMNode=null;
    }
    return result;
    }

    @Override
    public void finishSaveNewElement(HeaderTransferData element) {
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
        return null;
    }

}
