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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.amanzi.neo.core.INeoConstants;
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
 * <p>
 * Loader for greed counters data
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
     * Instantiates a new grid loader.
     *
     * @param directory the directory
     * @param datasetName the dataset name
     * @param display the display
     */
    public GridLoader(String directory, String datasetName, Display display) {
        initialize("Grid Counter", null, directory, display);
        basename = datasetName;
        service = NeoServiceFactory.getInstance().getDatasetService();
    }
    private void addDriveIndexes() {
        try {
            addIndex(NodeTypes.M.getId(), NeoUtils.getTimeIndexProperty(basename));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }
    @Override
    public void run(IProgressMonitor monitor) throws IOException {
        List<File> fileList = getSortedList(filename);
        String characterSet = NeoLoaderPlugin.getDefault().getCharacterSet();
        mainTx = neo.beginTx();
        addDriveIndexes();
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
                            boolean containceData = gridFile.getName().contains("_stat.");
                            int startind=containceData?2:0;
                            for (int i = startind; i < nextLine.length; i++) {
                                if (StringUtils.isNotEmpty(nextLine[i])){
                                    data.put(String.format(propertyFormat, i), nextLine[i]);
                                }
                            }
                            if (containceData&&nextLine.length>=2){
                                String dateTime=new StringBuilder(nextLine[0]).append(' ').append(nextLine[1]).toString();
                                Calendar cl= getTime(dateTime);
                                data.put(INeoConstants.PROPERTY_TIME_NAME,dateTime);
                                data.setWorkDate(cl);
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
     * Gets the time.
     *
     * @param datetime the datetime
     * @return the time
     */
    private Calendar getTime(String datetime) {
        SimpleDateFormat df=new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Date result;
        try {
            result = df.parse(datetime);
        } catch (ParseException e) {
            error(String.format("Can't parce time %s", datetime));
            return null;
        }
        Calendar cl=Calendar.getInstance();
        cl.setTime(result);
        return cl;
    }
    /**
     * Pet property prfix.
     *
     * @param gridFile the grid file
     * @return the string
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

    /**
     * Save data
     *
     * @param data the data
     */
    private void save(HeaderTransferData data) {
        lastMNode=service.createMNode(parent, lastMNode);  
        for (Map.Entry<String,String>entry:data.entrySet()){
            setIndexPropertyNotParcedValue(getHeaderMap(1).headers, lastMNode, entry.getKey(), entry.getValue());
        }
        if (data.getWorkDate()!=null){
            lastMNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME,data.getWorkDate().getTimeInMillis());
            index(lastMNode);
        }
    }


    /**
     * Gets the input stream for file
     *
     * @param gridFile the grid file
     * @return the input stream
     * @throws IOException Signals that an I/O exception has occurred.
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
     * Gets the sorted list.
     *
     * @param filename the filename
     * @return the sorted list
     */
    private List<File> getSortedList(String filename) {
        List<File> result = getAllFiles(new File(filename));
        Collections.sort(result);
        return result;
    }

    /**
     * Gets the all files.
     *
     * @param root the root
     * @return the all files
     */
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

    /**
     * Gets the all files.
     *
     * @param directory the directory
     * @param filter the filter
     * @return the all files
     */
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
