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
import java.util.HashMap;
import java.util.List;

import org.amanzi.awe.parser.core.IDataElementOldVersion;
import org.amanzi.awe.parser.core.IParserOldVersion;
import org.eclipse.core.runtime.IProgressMonitor;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
@Deprecated
public abstract class FileDatasetSaver<T extends IDataElementOldVersion> extends AbstractSaverOld<T> {

    protected IParserOldVersion<T> parser;

    protected Node currentFileNode;

    protected int fileCount = 0;

    protected long currentFileSize;

    protected long currentFileDone;

    protected String currentFileName;

    public abstract List<File> getFiles();

    protected abstract void processElement(T dataElement, HashMap<String, Object> indexInfo, IProgressMonitor monitor);

    public void process(Class< ? extends IParserOldVersion<T>> parserClass, IProgressMonitor monitor) {
        this.parserClass = parserClass;
        initializeIndexes();

        List<File> files = getFiles();
        fileCount = files.size();
        System.out.println("Loading " + fileCount + " files");
        int i = 0;

        double fullJob = Integer.MAX_VALUE;
        double fileRange = fullJob / fileCount;
        // currentFileDone
        monitor.beginTask("GPEH data loading", ((Double)fullJob).intValue());
        filesIterator: {
            
            for (File singleFile : files) {
                initialize(singleFile);

                try {
                    long lastFilePercent = 0;
                    // monitor.beginTask("GPEH data loading: " + singleFile.getName(),
                    // ((Long)currentFileSize).intValue());
                    while (parser.hasNext()) {
                        if (monitor.isCanceled())
                            break filesIterator;
                        processElement(parser.next(), null, monitor);

                        double workDone = parser.getProccessedSize() - currentFileDone;

                        double workDonePercent = workDone * 100 / currentFileSize;

                        double monitorDone = (fileRange * workDonePercent) / 100;
                        int mon = ((Double)monitorDone).intValue();
                        if (mon != 0) {
                            monitor.worked(mon);
                            currentFileDone = parser.getProccessedSize();
                        }
                        long filePercent = (100*currentFileDone)/currentFileSize;
                        if(lastFilePercent != filePercent){
                            monitor.setTaskName("Loaded " + filePercent + "% of file " + singleFile.getName());
                            lastFilePercent = filePercent;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error in file " + singleFile.getName());
                    e.printStackTrace();
                }
                monitor.setTaskName("Loaded 100% of file " + singleFile.getName());
                

                finishUpIndexes();
                finishUpFile();

                // monitor.done();

                float status = (float)++i / (float)fileCount;
                int iStatus = (int)(status * 1000);
                status = (float)iStatus / 10;
                System.out.println("Status: " + status + "% of work is done");
            }
        }

        finishUp();
    }

    protected abstract void finishUpFile();

    protected abstract void finishUp();

    protected void initialize(File singleFile) {
        try {
            parser = parserClass.newInstance();
            parser.init(singleFile);

            currentFileNode = datasetService.getFileNode(datasetNode, singleFile.getName());
            currentFileNode.setProperty("lastChildId", -1L);

            currentFileSize = parser.getFileSize();

            currentFileName = singleFile.getName();
        } catch (IllegalAccessException e) {

        } catch (InstantiationException e) {

        }
    }

}
