package org.amanzi.neo.loader.core.saver.impl;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.db.manager.NeoServiceProvider;
import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class RomesDriveModelSaver<T extends BaseTransferData> implements ISaver<T> {

    private DriveModel dm;
    private MetaData metadata = new MetaData("dataset", MetaData.SUB_TYPE, "romes");
    private PrintStream out;
    private Transaction tx;
    private int count = 0;
    private GraphDatabaseService graphDb = NeoServiceProvider.getProvider().getService();
    private String filename;

    protected Map<String, String> propertyMap = new HashMap<String, String>();

    @Override
    public void init(T element) {
        String projectName = element.getProjectName();
        String rootName = element.getRootName();
        filename = element.getFileName();

        tx = graphDb.beginTx();
        try {
            Node project = NeoServiceFactory.getInstance().getNewProjectService().getProject(projectName);
            dm = new DriveModel(project, null, rootName, DriveTypes.ROMES);
            dm.getFile(filename);
            tx.success();
        } catch (AWEException e) {
            // TODO: log
            throw new RuntimeException(e);
        } finally {
            tx.finish();
            tx = null;
        }

    }

    @Override
    public void save(T element) {
        transaction();

        definePropertyMap(element);

        Map<String, Object> params = new HashMap<String, Object>();
        // for each field
        for (String key : element.keySet()) {
            // get header
            String header = getHeader(key);
            // get actual type
            Object value = element.get(key);
            // put property
            params.put(header, value);
        }
        // add measurement
        try {
            dm.addMeasurement(filename, params);
            tx.success();
        } catch (AWEException e) {
            // TODO log
            throw new RuntimeException(e);
        }

    }

    // private Object getActualValue(String key, String string) {
    // Object result = string;
    // String type = values.get(key);
    // if (type != null) {
    // if (type.equalsIgnoreCase("long")) {
    // try {
    // result = Long.parseLong(string);
    // } catch (NumberFormatException e) {
    // result = string;
    // }
    // }
    // if (type.equalsIgnoreCase("date")) {
    // try {
    // DateFormat format = new SimpleDateFormat("dd.MM.yy");
    // Date date = format.parse(string);
    // result = date.getTime();
    // } catch (ParseException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    // }
    // return result;
    // }

    private String getHeader(String key) {
        String result = key;
        for (String hKey : propertyMap.keySet()) {
            if (hKey.equalsIgnoreCase(key)) {
                result = propertyMap.get(hKey);
            }
        }
        return result;
    }

    private void transaction() {
        if (tx == null) {
            tx = graphDb.beginTx();
        } else {
            if (count == 100) {
                tx.finish();
                tx = graphDb.beginTx();
                count = 0;
            } else {
                count++;
            }
        }
    }

    @Override
    public void finishUp(T element) {
        if (tx != null) {
            tx.finish();
        }
    }

    @Override
    public PrintStream getPrintStream() {
        return out;
    }

    @Override
    public void setPrintStream(PrintStream outputStream) {
        this.out = outputStream;

    }

    /*
     * the method never really used
     * @see org.amanzi.neo.loader.core.saver.ISaver#getMetaData()
     */
    @Override
    public Iterable<MetaData> getMetaData() {
        return Arrays.asList(new MetaData[] {metadata});
    }

    public DriveModel getDriveModel() {
        return dm;
    }

    protected void definePropertyMap(BaseTransferData element) {
        Set<String> headers = element.keySet();
        defineHeader(headers, "time", new String[] {"time.*"});
        defineHeader(headers, "latitude", new String[] {".*latitude.*"});
        defineHeader(headers, "longitude", new String[] {".*longitude.*"});
        defineHeader(headers, "events", new String[] {"Event Type", "event_type"});
        defineHeader(headers, "time", new String[] {"time", "Timestamp", "timestamp"});
        defineHeader(headers, INeoConstants.SECTOR_ID_PROPERTIES, new String[] {".*Server.*Report.*CI.*"});

    }

    protected void defineHeader(Set<String> headers, String newName, String[] possibleHeaders) {
        if (possibleHeaders == null) {
            return;
        }
        for (String header : headers) {
            if (propertyMap.values().contains(header)) {
                continue;
            }
            for (String headerRegExp : possibleHeaders) {
                Pattern pat = Pattern.compile(headerRegExp, Pattern.CASE_INSENSITIVE);
                Matcher match = pat.matcher(header);
                if (match.matches()) {
                    propertyMap.put(newName, header);
                    return;
                }
            }
        }
    }

}
