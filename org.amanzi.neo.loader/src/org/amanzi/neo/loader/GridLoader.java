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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.core.parser.HeaderTransferData;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.Node;

import au.com.bytecode.opencsv.CSVReader;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class GridLoader extends DriveLoader {

    private DatasetService service;
    private Node root;
    private Node parent;
    private Node lastMNode;

    /**
     * @param directory
     * @param datasetName
     * @param display
     */
    public GridLoader(String directory, String datasetName, Display display) {
        initialize("Grid Counter", null, directory, display);
        basename = datasetName;
        service = NeoServiceFactory.getInstance().getDatasetService();
    }

    @Override
    public void run(IProgressMonitor monitor) throws IOException {
        List<File> fileList = getSortedList(filename);
        String characterSet = NeoLoaderPlugin.getDefault().getCharacterSet();
        mainTx = neo.beginTx();
        NeoUtils.addTransactionLog(mainTx, Thread.currentThread(), "AbstractLoader");
        try {
            initializeIndexes();
            InputStream is;
            root = service.getRootNode(LoaderUtils.getAweProjectName(), basename, NodeTypes.OSS);
            monitor.beginTask("Load grid data", fileList.size());
            for (File gridFile : fileList) {
                if (monitor.isCanceled()){
                    break;
                }
                monitor.subTask("Load "+gridFile.getName());
                lastMNode=null;
                parent = service.getFileNode(root, gridFile.getName());
                is = getInputStream(gridFile);
                CSVReader reader = new CSVReader(new InputStreamReader(is, characterSet), '|');
                try {
                    long line = 0;
                    long comm = 0;
                    String[] nextLine;
                    while ((nextLine = reader.readNext()) != null) {
                        if (monitor.isCanceled()){
                            break;
                        }
                        try {
                            line++;
                            comm++;
                            HeaderTransferData data = new HeaderTransferData();
                            data.setLine(line);
                            data.setFileName(gridFile.getName());
                            String propertyFormat=petPropertyPrfix(gridFile);

                            for (int i = 0; i < nextLine.length; i++) {
                                if (StringUtils.isNotEmpty(nextLine[i])){
                                    data.put(String.format(propertyFormat, i), nextLine[i]);
                                }
                            }
                            save(data);
                        } finally {
                            if(comm>commitSize){
                                commit(true);
                                comm=0;
                            }
                        }
                    }
                } catch (Exception e) {
                    error(e.getLocalizedMessage());
                    e.printStackTrace();
                } finally {
                    monitor.worked(1);
                    reader.close();
                }
                commit(true);
            }
            commit(true);
            saveProperties();
            finishUpIndexes();
            finishUp();
        }catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        } 
        finally {
            commit(false);
        }
    }


    /**
     *
     * @param gridFile
     * @return
     */
    private String petPropertyPrfix(File gridFile) {
        int len=gridFile.getName().indexOf('.');
        String propertyFormat=len>-1?gridFile.getName().substring(0,len):"property";
//        if (gridFile.getName().contains("_ntid")) {
//            propertyFormat = "ntid%s";
//        } else  if (gridFile.getName().contains("_stat")){
//            propertyFormat = "stat%s";
//        }else{
//            propertyFormat = "prop%s";
//        }
        return propertyFormat+"%s";
    }

    private void save(HeaderTransferData data) {
        lastMNode=service.createMNode(parent, lastMNode);  
        for (Map.Entry<String,String>entry:data.entrySet()){
            setIndexPropertyNotParcedValue(getHeaderMap(1).headers, lastMNode, entry.getKey(), entry.getValue());
        }
    }

    /**
     * @param gridFile
     * @return
     * @throws IOException
     */
    private InputStream getInputStream(File gridFile) throws IOException {
        InputStream in = new FileInputStream(gridFile);
        if (Pattern.matches("^.+\\.gz$", gridFile.getName())) {
            in = new GZIPInputStream(in);
        } else if (Pattern.matches("^.+\\.Z$", gridFile.getName())){
            in = new UncompressInputStream(in);
        }
        return in;
    }

    /**
     * @param filename
     * @return
     */
    private List<File> getSortedList(String filename) {
        List<File> result = getAllFiles(new File(filename));
        Collections.sort(result);
        return result;
    }

    protected List<File> getAllFiles(File root) {
        FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                if (pathname.isFile()){
                    return Pattern.matches("^.+\\.Z$", pathname.getName());
                }
                return true;
            }
        };
        if (root.isDirectory()) {
            return getAllFiles(root, filter);
        } else {
            List<File> result = new ArrayList<File>();
            if (filter.accept(root)) {
                result.add(root);
            }
            return result;
        }
    }

    public static List<File> getAllFiles(File directory, FileFilter filter) {
        LinkedList<File> result = new LinkedList<File>();
        for (File childFile : directory.listFiles(filter)) {
            if (childFile.isDirectory()) {
                result.addAll(getAllFiles(childFile, filter));
            } else {
                result.add(childFile);
            }
        }
        return result;
    }

    @Override
    protected void parseLine(String line) {
    }

    @Override
    protected String getPrymaryType(Integer key) {
        return NodeTypes.M.getId();
    }

    @Override
    protected Node getStoringNode(Integer key) {
        return root;
    }

}
