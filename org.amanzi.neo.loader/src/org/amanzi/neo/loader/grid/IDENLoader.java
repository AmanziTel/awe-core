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

package org.amanzi.neo.loader.grid;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.DriveLoader;
import org.amanzi.neo.loader.UncompressInputStream;
import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.ui.utils.LoaderUtils;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.osgi.framework.Bundle;

import au.com.bytecode.opencsv.CSVReader;

/**
 * <p>
 * Loader for IDEN counters data
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class IDENLoader extends DriveLoader {
    private static final String STAT = "stat";
    private static final String NTID = "ntid";

    private static final String FILE_FILTER_REGEX = "^.+_%s.+\\.Z$";
    private static final String STAT_FILE_FILTER_REGEX = "ecl_%s.+\\.Z$";
    private static final String HEADER_FILE_LINE_PATTERN = "(\\d+)\\s(\\w+)\\s([\\w|_|\\d]+)\\s?([\\w|_]+)?";
    private static final String HEADER_ID_FILE_LINE_PATTERN = "(\\d+)\\s([\\w|_]+)";
    private static Map<String, List<String>> headers_map;
    private static Map<String, List<String>> headers_id_map;
    private static Map<String, Map<String, String>> headers_lookup_map = new HashMap<String, Map<String, String>>();
    private static Map<String, Map<String, String>> lookup_values_map = new HashMap<String, Map<String, String>>();
    private static List<String> config_files = new ArrayList<String>();
    private static List<String> config_id_files = new ArrayList<String>();
    static {
        config_files.add("ecl_stat.txt");
        config_files.add("cel_stat.txt");
        config_id_files.add("cel_ntid.txt");
        config_id_files.add("sit_ntid.txt");
        headers_map = new HashMap<String, List<String>>();
        headers_id_map = new HashMap<String, List<String>>();
        Bundle bundle = NeoLoaderPlugin.getDefault().getBundle();
        Pattern pat = Pattern.compile(HEADER_ID_FILE_LINE_PATTERN);
        // read id files headers
        for (String config_id_file : config_id_files) {
            URL entry = bundle.getEntry("config/" + config_id_file);
            try {
                Scanner scanner = new Scanner(new File(FileLocator.toFileURL(entry).getPath()));
                System.out.println(FileLocator.toFileURL(entry).getPath());
                ArrayList<String> stat_headers = new ArrayList<String>();
                while (scanner.hasNextLine()) {
                    Matcher matcher = pat.matcher(scanner.nextLine());
                    if (matcher.matches()) {
                        stat_headers.add(matcher.group(2).toLowerCase());
                    }
                }
                scanner.close();
                headers_id_map.put(config_id_file.split("\\.")[0], stat_headers);
            } catch (Exception e) {
                e.printStackTrace();
                // TODO Handle IOException
            }
        }
        // read stat files headers
        pat = Pattern.compile(HEADER_FILE_LINE_PATTERN);
        for (String config_file : config_files) {
            URL entry = bundle.getEntry("config/" + config_file);
            try {
                Scanner scanner = new Scanner(new File(FileLocator.toFileURL(entry).getPath()));
                System.out.println(FileLocator.toFileURL(entry).getPath());
                ArrayList<String> stat_headers = new ArrayList<String>();
                HashMap<String, String> lookup_for_header = new HashMap<String, String>();
                while (scanner.hasNextLine()) {
                    Matcher matcher = pat.matcher(scanner.nextLine());
                    if (matcher.matches()) {
                        String header = matcher.group(3);
                        stat_headers.add(header.toLowerCase());
                        String group;
                        if ((group = matcher.group(4)) != null) {
                            lookup_for_header.put(header.toLowerCase(), group);
                        }
                    }
                }
                scanner.close();
                String file_key = config_file.split("\\.")[0];
                headers_lookup_map.put(file_key, lookup_for_header);
                headers_map.put(file_key, stat_headers);
            } catch (Exception e) {
                e.printStackTrace();
                // TODO Handle IOException
            }
        }
    }

    private DatasetService service;
    private Node root;
    private Node parent;
    private Node lastMNode;
    private String directory;
    private GraphDatabaseService neoService;

    /**
     * Instantiates a new grid loader.
     * 
     * @param directory the directory
     * @param datasetName the dataset name
     * @param display the display
     * @param neo TODO
     */
    public IDENLoader(String directory, String datasetName, Display display, GraphDatabaseService neo) {
        initialize("Grid Counter", neo, directory, display);
        this.directory = directory;
        this.display = display;
        this.neoService = neo;
        basename = datasetName;
        service = NeoServiceFactory.getInstance().getDatasetService();
        addKnownHeader(1, "site", "site", true);
        addKnownHeader(1, "cell", "cell", true);

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
        // load network structure
        createNetworkStructure();

        // load statistics
        loadStatistics(monitor);
    }

    /**
     * @param monitor
     */
    private void loadStatistics(IProgressMonitor monitor) {
        mainTx = neoService.beginTx();
        NeoUtils.addTransactionLog(mainTx, Thread.currentThread(), "IDENLoader");
        try {
            InputStream is;
            List<File> idFileList = getSortedList(directory, NTID);
            String characterSet = NeoLoaderPlugin.getDefault().getCharacterSet();
            for (File gridFile : idFileList) {
                if (monitor.isCanceled()) {
                    break;
                }
                is = getInputStream(gridFile);
                String name = gridFile.getName();
                CSVReader reader = new CSVReader(new InputStreamReader(is, characterSet), '|');
                try {
                    String[] nextLine;
                    String key = name.split("\\.")[0];
                    Map<String, String> lookup = new HashMap<String, String>();
                    while ((nextLine = reader.readNext()) != null) {
                        // lookup.put(id, name);
                        lookup.put(nextLine[1], nextLine[2]);
                    }
                    lookup_values_map.put(key, lookup);
                } catch (Exception e) {
                    error(e.getLocalizedMessage());
                    e.printStackTrace();
                } finally {
                    reader.close();
                }
            }
            List<File> statFileList = getSortedList(directory, STAT);

            monitor.beginTask("Loading data", statFileList.size());

            for (File gridFile : statFileList) {
                if (monitor.isCanceled()) {
                    break;
                }
                String name = gridFile.getName();
                String path = gridFile.getPath();
                basename = name;
                // initialize("Grid Counter", neoService, basename, display);
                indexesInitialized = false;
                addDriveIndexes();
                initializeIndexes();
              
                root = service.getRootNode(LoaderUtils.getAweProjectName(), basename, NodeTypes.OSS);
                monitor.subTask("Load " + name);
                lastMNode = null;
                parent = service.findFileNode(root, path);
                if (parent == null) {
                    parent = service.getFileNode(root, path);
                    is = getInputStream(gridFile);
                    CSVReader reader = new CSVReader(new InputStreamReader(is, characterSet), '|');
                    try {
                        long line = 0;
                        long comm = 0;
                        String[] nextLine;
                        String propertyFormat = getPropertyPrefix(gridFile);
                        boolean containceData = name.contains("_stat.");
                        int startind = containceData ? 2 : 0;
                        String key = name.split("\\.")[0];
                        boolean useHeaders = false;
                        boolean useLookups = false;
                        List<String> headers = null;
                        Map<String, String> lookups = null;
                        if (useHeaders = headers_map.containsKey(key)) {
                            headers = headers_map.get(key);
                        }
                        if (useLookups = headers_lookup_map.containsKey(key)) {
                            lookups = headers_lookup_map.get(key);
                        }
                        while ((nextLine = reader.readNext()) != null) {
                            if (monitor.isCanceled()) {
                                break;
                            }
                            try {
                                line++;
                                comm++;
                                BaseTransferData data = new BaseTransferData();
                                data.setLine(line);
                                data.setFileName(name);

                                for (int i = startind; i < nextLine.length; i++) {
                                    if (StringUtils.isNotEmpty(nextLine[i])) {
                                        if (useHeaders) {
                                            if (useLookups) {
                                                String lookupIdFile = lookups.get(headers.get(i));
                                                if (lookup_values_map.containsKey(lookupIdFile)) {
                                                    String value = lookup_values_map.get(lookupIdFile).get(nextLine[i]);
                                                    data.put(headers_id_map.get(lookupIdFile).get(2), value);
                                                } else {
                                                    data.put(headers.get(i), nextLine[i]);
                                                }
                                            } else {
                                                data.put(headers.get(i), nextLine[i]);
                                            }
                                        } else {
                                            data.put(String.format(propertyFormat, i), nextLine[i]);
                                        }
                                    }
                                }
                                if (containceData && nextLine.length >= 2) {
                                    String dateTime = new StringBuilder(nextLine[0]).append(' ').append(nextLine[1]).toString();
                                    Calendar cl = getTime(dateTime);
                                    data.put(INeoConstants.PROPERTY_TIME_NAME, dateTime);
                                    data.setWorkDate(cl);
                                }
                                save(data);
                            } finally {
                                if (comm > commitSize) {
                                    commit(true);
                                    comm = 0;
                                }
                            }
                        }
                    } catch (Exception e) {
                        error(e.getLocalizedMessage());
                        e.printStackTrace();
                    } finally {
                        reader.close();
                    }
                    commit(true);
                    saveProperties();
                    finishUpIndexes();
                    finishUp();
                    monitor.worked(1);
                } else {
                    System.out.println("File '"+path + "' already loaded");
                }
            }
            commit(true);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        } finally {
            commit(false);
        }
    }

    /**
     *
     */
    private void createNetworkStructure() {
        List<File> fileList = getSortedList(directory, NTID);
        for (File file : fileList) {
            System.out.println("ID file: " + file.getPath());
        }
    }

    /**
     * Gets the time.
     * 
     * @param datetime the datetime
     * @return the time
     */
    private Calendar getTime(String datetime) {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Date result;
        try {
            result = df.parse(datetime);
        } catch (ParseException e) {
            error(String.format("Can't parce time %s", datetime));
            return null;
        }
        Calendar cl = Calendar.getInstance();
        cl.setTime(result);
        return cl;
    }

    /**
     * Pet property prfix.
     * 
     * @param gridFile the grid file
     * @return the string
     */
    private String getPropertyPrefix(File gridFile) {
        int len = gridFile.getName().indexOf('.');
        String propertyFormat = len > -1 ? gridFile.getName().substring(0, len) : "property";
        // if (gridFile.getName().contains("_ntid")) {
        // propertyFormat = "ntid%s";
        // } else if (gridFile.getName().contains("_stat")){
        // propertyFormat = "stat%s";
        // }else{
        // propertyFormat = "prop%s";
        // }
        return propertyFormat + "%s";
    }

    /**
     * Save data
     * 
     * @param data the data
     */
    private void save(BaseTransferData data) {
        lastMNode = service.createMNode(parent, lastMNode);
        for (Map.Entry<String, String> entry : data.entrySet()) {
            setIndexPropertyNotParcedValue(getHeaderMap(1).headers, lastMNode, entry.getKey(), entry.getValue());
        }
        if (data.getWorkDate() != null) {
            long timeInMillis = data.getWorkDate().getTimeInMillis();
            updateTimestampMinMax(1, timeInMillis);
            lastMNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timeInMillis);
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
        } else if (Pattern.matches("^.+\\.Z$", gridFile.getName())) {
            in = new UncompressInputStream(in);
        }
        return in;
    }

    /**
     * Gets the sorted list.
     * 
     * @param filename the filename
     * @param filetype the file type (<i>stat</i> or <i>id</i>)
     * @return the sorted list
     */
    private List<File> getSortedList(String filename, String filetype) {
        List<File> result = getAllFiles(new File(filename), filetype);
        Collections.sort(result);
        return result;
    }

    /**
     * Gets the all files.
     * 
     * @param root the root
     * @param fileType TODO
     * @return the all files
     */
    protected List<File> getAllFiles(File root, final String fileType) {
        FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                if (pathname.isFile()) {
                    if (fileType.equalsIgnoreCase("stat")){
                        //load only ecl_stat file
                        return pathname.getName().equalsIgnoreCase("ecl_stat.unl.Z");
                    }
                    return Pattern.matches(String.format(FILE_FILTER_REGEX, fileType), pathname.getName());
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

    @Override
    public Node getDatasetNode() {
        return root;
    }

}
