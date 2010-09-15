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

package org.amanzi.neo.loader.core;

import java.io.File;
import java.util.List;

import org.amanzi.awe.parser.core.IDataElementOldVersion;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.loader.core.saver.MChainSaver;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * <p>
 * Loader for files with table data
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class CsvLoader implements IProgressableLoader<IDataElementOldVersion> {

    private final String filename;
    private final String datasetName;
    private final String projectName;
    private final IProgressMonitor monitor;

    public CsvLoader(String filename, String datasetName,String projectName, IProgressMonitor monitor) {
        this.filename = filename;
        this.datasetName = datasetName;
        this.projectName = projectName;
        this.monitor = monitor;

    }

    private class CsvSaver extends MChainSaver<IDataElementOldVersion> {

        @Override
        public List<File> getFiles() {
            return null;
        }

    }

    public void load() {
        CsvSaver saver = new CsvSaver();
        saver.init(projectName, datasetName, NodeTypes.DATASET, 1);
        
        long before = System.currentTimeMillis();
        // saver.process(org.amanzi.awe.parser.csv.CsvFileParser.class, monitor);
       
        System.out.println(System.currentTimeMillis() - before);
        
        //TODO: fake - workaround for bug in Neo4j
        for (int i = 0; i < 10000; i++) {
            DatabaseManager.getInstance().getCurrentDatabaseService().createNode();
        }        
    }

    @Override
    public void addProgressListener(IProgressListener listener) {
    }

    @Override
    public void removeProgressListener(IProgressListener listener) {
    }

    @Override
    public void fireProgressEvent(ProgressEventOld event) {
    }

}
