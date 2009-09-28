package org.amanzi.neo.loader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.services.UpdateDatabaseEvent;
import org.amanzi.neo.core.database.services.UpdateDatabaseEventType;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.MeasurementRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.service.listener.NeoServiceProviderEventAdapter;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.NetworkLoader.CRS;
import org.amanzi.neo.loader.dialogs.DriveDialog;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;

public abstract class DriveLoader extends NeoServiceProviderEventAdapter {
    private String typeName = "Drive";
    protected NeoService neo;
    protected String filename = null;
    protected String basename = null;
    protected String dataset=null;
    private long started = System.currentTimeMillis();
    private String[] headers = null;
    private HashMap<String,Integer> headerIndex = null;
    protected int line_number = 0;
    private int limit = 0;
    private CRS crs = null;
    private Node gis = null;
    private Node file = null;
    private double[] bbox;
    protected long savedData = 0;
    private Node datasetNode=null;
    private static int[] times = new int[2];
    private HashMap<Integer,int[]> stats = new HashMap<Integer,int[]>();
    private int countValidMessage = 0;
    private int countValidLocation = 0;
    private int countValidChanged = 0;
    private NeoServiceProvider neoProvider;
    private Display display;
    private String fieldSepRegex;
    private String[] possibleFieldSepRegexes = new String[]{"\\t","\\,","\\;"};
    /** How many units of work for the progress monitor for each file */
    public static final int WORKED_PER_FILE = 10;

    /**
     * Initialize Loader with a specified set of parameters 
     * 
     * @param type defaults to 'Drive' if empty
     * @param neoService defaults to looking up from Neoclipse if null
     * @param fileName name of file to load
     * @param display Display to use for scheduling plugin lookups and message boxes, or null
     * @param datasetName to add data to, or null to add to same as filename
     */
    protected final void initialize(String typeString, NeoService neoService, String filenameString, Display display, String datasetName) {
        if (typeString != null && !typeString.isEmpty()) {
            this.typeName = typeString;
        }
        if(neoService == null) {
            //if Display is given than start Neo using syncExec
            if (display != null) {
                display.syncExec(new Runnable() {
                    public void run() {
                        initializeNeo();
                    }
                });
            }
            //if Display is not given than initialize Neo as usual
            else {
                initializeNeo();
            }
        } else {
            this.neo = neoService;
        }
        if (datasetName==null||datasetName.trim().isEmpty()){
            this.dataset=null;
        }else{
            this.dataset = datasetName.trim();
        }
        this.display = display;
        this.filename = filenameString;
        this.basename = (new File(filename)).getName();
    }
    
    private void initializeNeo() {
        if(this.neoProvider == null) this.neoProvider = NeoServiceProvider.getProvider();
        if(this.neo == null) this.neo = this.neoProvider.getService();
        this.neoProvider.addServiceProviderListener(this);
    }

    /**
     * Converts to lower case and replaces all illegal characters with '_' and removes training '_'
     * @param original header String
     * @return edited String
     */
    protected final static String cleanHeader(String header) {
        return header.replaceAll("[\\s\\-\\[\\]\\(\\)\\/\\.]+", "_").replaceAll("\\_$", "").toLowerCase();
    }

    protected boolean haveHeader() {
        return headers != null;
    }
    private void determineFieldSepRegex(String line){
        if(fieldSepRegex == null){
            int maxMatch = 0;
            for(String regex:possibleFieldSepRegexes){
                String[] fields = line.split(regex);
                if(fields.length > maxMatch){
                    maxMatch = fields.length;
                    fieldSepRegex = regex;
                }
            }
        }
    }
    protected String[] splitLine(String line){
        return line.split(fieldSepRegex);
    }
    protected final void parseHeader(String line){
        debug(line);
        determineFieldSepRegex(line);
        String fields[] = splitLine(line);
        if(fields.length<2) return;
        headers = fields;
        headerIndex = new HashMap<String,Integer>();
        int index=0;
        for(String header:headers){
            header = cleanHeader(header);
            debug("Added header["+index+"] = "+header);
            headerIndex.put(header,index++);
        }
    }
    protected final int i_of(String header){
        return headerIndex.get(header);
    }
    
    protected final String status(){
        if(started<=0) started = System.currentTimeMillis();
        return (line_number>0 ? "line:"+line_number : ""+((System.currentTimeMillis()-started)/1000.0)+"s");
    }

    private final void runInDisplay(Runnable runnable) {
        if(display!=null){
            PlatformUI.getWorkbench().getDisplay().asyncExec(runnable);
        } else {
            runnable.run();
        }
    }
    
    protected final void debug(final String line){
        runInDisplay(new Runnable() {
            public void run() {
                NeoLoaderPlugin.debug(typeName + ":"+basename+":"+status()+": "+line);
            }
        });
    }
    
    protected final void info(final String line){
        runInDisplay(new Runnable() {
            public void run() {
                NeoLoaderPlugin.notify(typeName + ":"+basename+":"+status()+": "+line);
            }
        }); 
    }
    
    protected final void notify(final String line){
        runInDisplay(new Runnable() {
            public void run() {
                NeoLoaderPlugin.notify(typeName + ":"+basename+":"+status()+": "+line);
            }
        });     
    }
    
    protected final void error(final String line){
        runInDisplay(new Runnable() {
            public void run() {
                NeoLoaderPlugin.notify(typeName + ":"+basename+":"+status()+": "+line);
            }
        }); 
    }

    public void setLimit(int value){
        this.limit = value;
    }

    protected final void addToMap() throws MalformedURLException {
        if (gis != null && neoProvider != null) {
            Transaction transaction = neo.beginTx();
            try {
                if (bbox != null) {
                    gis.setProperty(INeoConstants.PROPERTY_BBOX_NAME, bbox);
                    gis.setProperty("count", savedData + (Long)gis.getProperty("count", 0L));
                }
                HashSet<Node> nodeToDelete = new HashSet<Node>();
                for (Relationship relation : gis.getRelationships(NetworkRelationshipTypes.AGGREGATION, Direction.OUTGOING)) {
                    nodeToDelete.add(relation.getEndNode());
                }
                for (Node node : nodeToDelete) {
                    NeoCorePlugin.getDefault().getProjectService().deleteNode(node);
                }
                transaction.success();
                Node mainNode = datasetNode == null ? file : datasetNode;
                NeoCorePlugin.getDefault().getProjectService().addDriveToProject(LoaderUtils.getAweProjectName(), mainNode);
            } finally {
                transaction.finish();
//                if(neoProvider!=null){
//                    neoProvider.commit();
//                }
            }
        }
        if(neoProvider!=null){
            String databaseLocation = neoProvider.getDefaultDatabaseLocation();
            NeoCorePlugin.getDefault().getUpdateDatabaseManager()
                    .fireUpdateDatabase(new UpdateDatabaseEvent(UpdateDatabaseEventType.GIS));
            ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
            URL url = new URL("file://" + databaseLocation);
            List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(url);
            for (IService service : services) {
                if (catalog.getById(IService.class, service.getIdentifier(), new NullProgressMonitor()) != null) {
                    catalog.replace(service.getIdentifier(), service);
                } else {
                    catalog.add(service);
                }
            }
            if (dataset == null) {
                DriveDialog.addGisToMap(gis);
            }
        }
    }
    
    public Node getGisNode() {
        return gis;
    }

    protected final void addStats(int pn_code, int ec_io) {
        if(!stats.containsKey(pn_code)) this.stats.put(pn_code,new int[2]);
        stats.get(pn_code)[0]+=1;
        stats.get(pn_code)[1]+=ec_io;
    }

    protected final void checkCRS(String[] latlon) {
        if (crs == null) {
            crs = CRS.fromLocation(Float.parseFloat(latlon[0]), Float.parseFloat(latlon[1]), null);
            file.setProperty(INeoConstants.PROPERTY_CRS_TYPE_NAME, crs.getType());
            file.setProperty(INeoConstants.PROPERTY_CRS_NAME, crs.toString());
            gis.setProperty(INeoConstants.PROPERTY_CRS_TYPE_NAME, crs.getType());
            gis.setProperty(INeoConstants.PROPERTY_CRS_NAME, crs.toString());
        }
    }

    protected final void updateBBox(double lat, double lon) {
        if (bbox == null) {
            bbox = new double[] {lon, lon, lat, lat};
        } else {
            if (bbox[0] > lon)
                bbox[0] = lon;
            if (bbox[1] < lon)
                bbox[1] = lon;
            if (bbox[2] > lat)
                bbox[2] = lat;
            if (bbox[3] < lat)
                bbox[3] = lat;
        }
    }

    protected final void findOrCreateFileNode(Node mp) {
        if (file == null) {
            Node reference = neo.getReferenceNode();
            datasetNode = findOrCreateDatasetNode(neo.getReferenceNode(), dataset);
            file = findOrCreateFileNode(reference, datasetNode);

            Node mainFileNode = datasetNode == null ? file : datasetNode;
            file.createRelationshipTo(mp, GeoNeoRelationshipTypes.NEXT);
            gis = getGISNode(neo, mainFileNode);
            bbox = (double[])gis.getProperty(INeoConstants.PROPERTY_BBOX_NAME, bbox);

            debug("Added '" + mp.getProperty(INeoConstants.PROPERTY_TIME_NAME) + "' as first measurement of '"
                    + file.getProperty(INeoConstants.PROPERTY_FILENAME_NAME));
        }
    }

    /**
     * gets GIS node for
     * 
     * @param neo neo serice
     * @param mainNode main drive node
     * @return gis node for mainNode
     */
    protected final static Node getGISNode(NeoService neo, Node mainNode) {
        Node gis = null;
        Transaction transaction = neo.beginTx();
        try {
            Node reference = neo.getReferenceNode();
            for (Relationship relationship : mainNode.getRelationships(GeoNeoRelationshipTypes.NEXT, Direction.INCOMING)) {
                Node node = relationship.getStartNode();
                if (node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME)
                        && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(INeoConstants.GIS_TYPE_NAME)
                        && node.hasProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME)
                        && node.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME).toString().equals(GisTypes.DRIVE.getHeader())){
                    if(!node.getRelationships(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).iterator().hasNext()) {
                        node.createRelationshipTo(mainNode, GeoNeoRelationshipTypes.NEXT);
                    }
                    return node;
                }
            }
            gis = neo.createNode();
            gis.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.GIS_TYPE_NAME);
            gis.setProperty(INeoConstants.PROPERTY_NAME_NAME, NeoUtils.getNodeName(mainNode));
            gis.setProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, GisTypes.DRIVE.getHeader());
            reference.createRelationshipTo(gis, NetworkRelationshipTypes.CHILD);
            gis.createRelationshipTo(mainNode, GeoNeoRelationshipTypes.NEXT);
            transaction.success();
        } finally {
            transaction.finish();
        }
        return gis;
    }

    /**
     * Finds or create if not exist necessary file node
     * 
     * @param root root node
     * @param datasetNode dataset node
     * @return
     */
    protected final Node findOrCreateFileNode(Node root, Node datasetNode) {
        ReturnableEvaluator fileReturnableEvaluator=new ReturnableEvaluator() {
            
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                final Node currentNode = currentPos.currentNode();
                return NeoUtils.isFileNode(currentNode) && basename.equals(NeoUtils.getNodeName(currentNode));
            }
        };
        Traverser fileNodeTraverser;
        if (datasetNode!=null){
            fileNodeTraverser=datasetNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, fileReturnableEvaluator, GeoNeoRelationshipTypes.NEXT,Direction.OUTGOING);
        }else{
            fileNodeTraverser = root.traverse(Order.DEPTH_FIRST, NeoUtils.getStopEvaluator(2), fileReturnableEvaluator,
                    GeoNeoRelationshipTypes.NEXT,
                    Direction.OUTGOING, NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
        }
        final Iterator<Node> iterator = fileNodeTraverser.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        Node result = neo.createNode();
        result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.FILE_TYPE_NAME);
        result.setProperty(INeoConstants.PROPERTY_NAME_NAME, basename);
        result.setProperty(INeoConstants.PROPERTY_FILENAME_NAME, filename);
        if (datasetNode != null) {
            datasetNode.createRelationshipTo(result, GeoNeoRelationshipTypes.NEXT);
        }
        return result;
    }

    /**
     * Finds or create if not exist necessary dataset node
     * 
     * @param root root node
     * @param datasetName name of dataset node
     * @return dataset node
     */
    protected final Node findOrCreateDatasetNode(Node root, final String datasetName) {
        Transaction tx = null;
        Node result;
        try {
            tx = neo.beginTx();
            if (datasetName == null || datasetName.isEmpty()) {
                return null;
            }
            Traverser traverse = NeoCorePlugin.getDefault().getProjectService().getAllDatasetTraverser(root);
            for (Node node : traverse) {
                if (node.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").equals(datasetName)) {
                    return node;
                }
            }
            result = neo.createNode();
            result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.DATASET_TYPE_NAME);
            result.setProperty(INeoConstants.PROPERTY_NAME_NAME, datasetName);
            tx.success();
            return result;
        } finally {
            tx.finish();
        }
    }

    protected final static String propertiesString(Node node){
        StringBuffer properties = new StringBuffer();
        for(String property:node.getPropertyKeys()) {
            if(properties.length()>0) properties.append(", ");
            properties.append(property).append(" => ").append(node.getProperty(property));
        }
        return properties.toString();
    }

    public void printStats(boolean verbose) {
        long taken = System.currentTimeMillis() - started;
        addTimes(taken);
        notify("Finished loading " + basename + " data in " + (taken / 1000.0) + " seconds");
        notify("Read " + (line_number - 1) + " data lines and then filtered down to:");
        notify("\t" + countValidMessage + " with valid messages");
        notify("\t" + countValidLocation + " with known locations");
        notify("\t" + countValidChanged + " with changed data");
        notify("Read " + stats.keySet().size() + " unique PN codes:");
        for (int pn_code : stats.keySet()) {
            int[] pn_counts = stats.get(pn_code);
            notify("\t" + pn_code + " measured " + pn_counts[0] + " times (average Ec/Io = " + pn_counts[1] / pn_counts[0] + ")");
        }
        if (file != null && verbose) {
            printMeasurements(file);
        } else {
            error("No measurement file node found");
        }
    }

    private void printMeasurement(Node measurement){
        info("Found measurement: "+propertiesString(measurement)+" --- "+childrenString(measurement));
    }

    private String childrenString(Node node){
        StringBuffer sb = new StringBuffer();
        for(Relationship relationship:node.getRelationships(MeasurementRelationshipTypes.CHILD,Direction.OUTGOING)){
            if(sb.length()>0) sb.append(", ");
            Node ms = relationship.getEndNode();
            sb.append(ms.getProperty(INeoConstants.PRPOPERTY_CHANNEL_NAME)).append(":");
            sb.append(ms.getProperty(INeoConstants.PROPERTY_CODE_NAME)).append("=");
            sb.append((ms.getProperty(INeoConstants.PROPERTY_DBM_NAME).toString()+"000000").substring(0,6));
        }
        return sb.toString();       
    }
    private void printMeasurements(Node file){
        if (file == null)
            return;
        Transaction transaction = neo.beginTx();
        try {
            for (Relationship relationship : file.getRelationships(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING)) {
                Node measurement = relationship.getEndNode();
                printMeasurement(measurement);
                Iterator<Relationship> relationships = measurement.getRelationships(GeoNeoRelationshipTypes.NEXT,
                        Direction.OUTGOING).iterator();
                while (relationships.hasNext()) {
                    relationship = relationships.next();
                    measurement = relationship.getEndNode();
                    printMeasurement(measurement);
                    relationships = measurement.getRelationships(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING)
                            .iterator();
                }
            }
            transaction.success();
        } finally {
            transaction.finish();
        }
    }

    /**
     * Initialize the accumulating times stats. Do this at the beginning of a block of loading many
     * files and then at the end call the method printTimesStats to see the results.
     */
    public void initiTimes() {
        times = new int[2];
    }

    private static int addTimes(long taken) {
        times[0] += 1;
        times[1] += taken;
        return times[0];
    }

    public static void printTimesStats() {
        System.err.println("Finished " + times[0] + " loads in " + times[1] / 60000.0 + " minutes (average "
                + (times[1] / times[0]) / 1000.0 + " seconds per load)");
    }

    protected int incValidMessage() {
        return ++countValidMessage;
    }

    protected int incValidLocation() {
        return ++countValidLocation;
    }

    protected int incValidChanged() {
        return ++countValidChanged;
    }

    protected boolean isOverLimit() {
        return limit > 0 && countValidChanged > limit;
    }

    /**
     * This is the main method of the class and needs to be implemented by subclasses. It should
     * open the file, iterate over its contents and build the appropriate data structures in the
     * database.
     * 
     * @param monitor
     * @throws IOException 
     */
    public abstract void run(IProgressMonitor monitor) throws IOException;

    @Override
    public void onNeoStop(Object source) {
        unregisterNeoManager();        
    }
    
    //Lagutko 21.07.2009, using of neo.core plugin
    private void unregisterNeoManager(){        
//        neoProvider.commit();
        neoProvider.removeServiceProviderListener(this);        
    }

}
