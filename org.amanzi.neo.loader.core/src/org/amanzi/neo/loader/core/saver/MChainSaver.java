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

package org.amanzi.neo.loader.core.saver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.amanzi.awe.parser.core.IDataElementOldVersion;
import org.amanzi.neo.loader.core.DatasetInfo;
import org.amanzi.neo.services.indexes.MultiPropertyIndex;
import org.amanzi.neo.services.indexes.MultiPropertyIndex.MultiTimeIndexConverter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
@Deprecated
public abstract class MChainSaver<T extends IDataElementOldVersion> extends FileDatasetSaver<T> {
    
    protected DatasetInfo datasetInfo;
    
    protected Node previousMNode;
    
    public MChainSaver() {
        super();
    }
    
    @Override
    public void init(String projectName, String datasetName, Object datasetType, int existsMask) {
        super.init(projectName, datasetName, datasetType, existsMask);
        if ((existsMask & DATASET_EXISTS) == DATASET_EXISTS) {
            datasetInfo = new DatasetInfo(datasetNode);
        }
        else {
            datasetInfo = new DatasetInfo();
        }
        try {
            addIndex("m", new MultiPropertyIndex<Long>("Index-timestamp-" + datasetName, new String[] {"timestamp"}, new MultiTimeIndexConverter(), 10));
        }
        catch (IOException e) {
            //TODO: handle exception
            e.printStackTrace();
        }
    }
    
    protected void initialize(File singleFile) {
        super.initialize(singleFile);
        previousMNode = datasetService.getLastNodeInFile(currentFileNode);
    }
    
    protected void processElement(T dataElement, HashMap<String, Object> indexInfo, IProgressMonitor monitor) {
        Node mNode = null;//datasetService.createMNode(currentFileNode, previousMNode, indexInfo);
        datasetInfo.increaseNodes();
        
        mNode.setProperty("name", dataElement.getName());
        mNode.setProperty("timestamp", dataElement.getTimestamp());
        index(mNode);
        
        for (Entry<String, Object> entry : dataElement.entrySet()) {
            mNode.setProperty(entry.getKey(), entry.getValue());
        }
        
        datasetInfo.updateTimestamp(dataElement.getTimestamp());
        previousMNode = mNode;
    }
    
    protected void saveLocation(Node mNode, T dataElement) {
        Long lat = dataElement.getLatitude();
        Long lon = dataElement.getLongitude();
        long timestamp = dataElement.getTimestamp();
        
        if ((lat == null) ||
            (lon == null)) {
            return;
        }
        
        Node mpNode = null;//datasetService.createMPNode(mNode);
        mpNode.setProperty("latitude", lat);
        mpNode.setProperty("longitude", lon);
        mpNode.setProperty("timestamp", timestamp);
    }
    
    @Override
    protected void finishUpFile() {
        if (previousMNode != null) {
            currentFileNode.setProperty("lastNodeId", previousMNode.getId());
        }
        
    }

    @Override
    protected void finishUp() {        
        datasetNode.setProperty("min_timestamp", datasetInfo.getMinTimestamp());
        datasetNode.setProperty("max_timestamp", datasetInfo.getMaxTimestamp());
        datasetNode.setProperty("count", datasetInfo.getNodeCount());
    }
}
