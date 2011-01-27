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

package org.amanzi.awe.afp.ericsson.saver;

import org.amanzi.awe.afp.ericsson.BARRecords;
import org.amanzi.awe.afp.ericsson.IRecords;
import org.amanzi.awe.afp.ericsson.parser.RecordTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Bar saver
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class BarSaver extends AbstractHeaderSaver<RecordTransferData> {

    @Override
    public void save(RecordTransferData element) {
        IRecords type = element.getRecord().getEvent().getType();
        if (type instanceof BARRecords) {
            switch ((BARRecords)type) {
            case ADMINISTRATIVE:
                storeAdminValues(element);
                return;
                case ACTIVE_BALIST_RECORDING_CELL_DATA:
                    handleCellData(element);
                    return;
                case ACTIVE_BALIST_RECORDING_NEIGHBOURING_CELL_DATA:
                    handleNeighbour(element);
                    return;
            default:
                return;
            }
        }
    }

    /**
     *
     * @param element
     */
    private void handleNeighbour(RecordTransferData element) {
    }

    /**
     *
     * @param element
     */
    private void handleCellData(RecordTransferData element) {
    }

    /**
     * @param element
     */
    private void storeAdminValues(RecordTransferData element) {
    }

    @Override
    public Iterable<MetaData> getMetaData() {
        return null;
    }

    @Override
    protected void fillRootNode(Node rootNode, RecordTransferData element) {
    }

    @Override
    protected String getRootNodeType() {
        return NodeTypes.NETWORK.getId();
    }

    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return NodeTypes.SECTOR.getId();
    }
    /**
     * @param object
     * @return
     */
    private String getString(byte[] data) {
        if (data == null) {
            return null;
        }
        int len=0;
        for (int i = 0; i < data.length; i++) {
            if (data[i]!=0){
                data[len]=data[i];
                len++;
            }
        }
        return new String(data,0,len);
    }
}