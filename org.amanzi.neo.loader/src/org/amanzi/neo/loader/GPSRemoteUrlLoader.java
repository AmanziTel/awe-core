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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * <p>
 * Loader for GPS data from url
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class GPSRemoteUrlLoader extends GPSLoader {

    /** The url. */
    private final URL url;

    /**
     * Instantiates a new GPS remote url loader.
     * 
     * @param url the url
     * @param datasetName the dataset name
     * @param display the display
     */
    public GPSRemoteUrlLoader(URL url, String datasetName, Display display) {
        super(datasetName, datasetName, display);
        this.url = url;
        dataset = datasetName;
    }

    /**
     * Initialize Loader with a specified set of parameters.
     * 
     * @param typeString the type string
     * @param neoService defaults to looking up from Neoclipse if null
     * @param filenameString the filename string
     * @param display Display to use for scheduling plugin lookups and message boxes, or null
     */
    protected void initialize(String typeString, GraphDatabaseService neoService, String filenameString, Display display) {
        if (typeString != null && !typeString.isEmpty()) {
            this.typeName = typeString;
        }
        initializeNeo(neoService, display);
        this.display = display;
        this.filename = filenameString;
        this.basename = dataset;
    }

    /**
     * Run.
     * 
     * @param monitor the monitor
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void run(IProgressMonitor monitor) throws IOException {
        if (monitor != null) {
            monitor.setTaskName(basename);
        }
        String characterSet = NeoLoaderPlugin.getDefault().getCharacterSet();
        BufferedReader reader = null;

        mainTx = neo.beginTx();
        NeoUtils.addTransactionLog(mainTx, Thread.currentThread(), "AbstractLoader");
        try {
            InputStream inputStream = url.openStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, characterSet);
            reader = new BufferedReader(inputStreamReader);
            initializeIndexes();
            int prevLineNumber = 0;
            String line;
            headerWasParced = !needParceHeaders();
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                System.out.println(line);
                if (!headerWasParced) {
                    parseHeader(line);
                } else {
                    parseLine(line);
                }

                if (lineNumber > prevLineNumber + commitSize) {
                    commit(true);
                    prevLineNumber = lineNumber;
                }
                if (isOverLimit())
                    break;

            }
            commit(true);
            saveProperties();
            finishUpIndexes();
            finishUp();
        } finally {
            commit(false);
            if (reader != null) {
                reader.close();
            }
        }
    }

}
