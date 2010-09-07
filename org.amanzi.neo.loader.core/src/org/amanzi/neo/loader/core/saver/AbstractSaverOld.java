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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.amanzi.awe.parser.core.IDataElementOldVersion;
import org.amanzi.awe.parser.core.IParserOldVersion;
import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.DatasetType;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.indexes.MultiPropertyIndex;
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
public abstract class AbstractSaverOld<T extends IDataElementOldVersion>  {
    public static final int PROJECT_EXISTS = 0x01;
    
    public static final int DATASET_EXISTS = 0x10; 
    protected Node datasetNode;
    
    protected DatasetService datasetService;
    
    protected Class<? extends IParserOldVersion<T>> parserClass;
    
    private HashMap<String, ArrayList<MultiPropertyIndex<?>>> indexes = new HashMap<String, ArrayList<MultiPropertyIndex<?>>>();
    
    public AbstractSaverOld() {
        this.datasetService = NeoServiceFactory.getInstance().getDatasetService();
    }
    
    public void init(String projectName, String datasetName, DatasetType datasetType, int existsMask) {
        Node projectNode = datasetService.getProjectNode(projectName, (existsMask & PROJECT_EXISTS) == PROJECT_EXISTS);
        datasetNode = datasetService.getDatasetNode(projectNode, datasetName, datasetType, (existsMask & DATASET_EXISTS) == DATASET_EXISTS);        
    }
    
    protected void addIndex(String nodeType, MultiPropertyIndex<?> index) {
        ArrayList<MultiPropertyIndex<?>> indexList = indexes.get(nodeType);
        if (indexList == null) {
            indexList = new ArrayList<MultiPropertyIndex<?>>();
            indexes.put(nodeType, indexList);
        }
        if (!indexList.contains(index)) {
            indexList.add(index);
        }
    }
    
    protected void initializeIndexes() {
        for (ArrayList<MultiPropertyIndex<?>> indexList : indexes.values()) {
            for (MultiPropertyIndex<?> singleIndex : indexList) {
                try {
                    singleIndex.initialize(DatabaseManager.getInstance().getCurrentDatabaseService(), null);
                }
                catch (IOException e) {
                    //TODO: handle exception
                    e.printStackTrace();
                }
            }
        }
    }
    
    protected void index(Node node) {
        String type = (String)node.getProperty("type");
        ArrayList<MultiPropertyIndex<?>> indexList = indexes.get(type);
        for (MultiPropertyIndex<?> singleIndex : indexList) {
            try {
                singleIndex.add(node);
            }
            catch (IOException e) { 
                //TODO: handle exception
                e.printStackTrace();
            }
        }
    }
    
    protected void finishUpIndexes() {
        for (ArrayList<MultiPropertyIndex<?>> indexList : indexes.values()) {
            for (MultiPropertyIndex<?> singleIndex : indexList) {
                singleIndex.finishUp();
            }
        }
    }   
    
}
