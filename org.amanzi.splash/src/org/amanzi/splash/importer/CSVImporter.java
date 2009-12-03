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

package org.amanzi.splash.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.amanzi.neo.core.utils.CSVParser;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.neo4j.api.core.Transaction;

import com.eteks.openjeks.format.CellFormat;


/**
 * Importer of CVS data
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class CSVImporter extends AbstractImporter {
    
    /**
     * An exception that will be thrown when initial part of spreadsheet already imported
     * 
     * @author Lagutko_N
     * @since 1.0.0
     */
    public class CSVImportException extends Exception {

        /** long serialVersionUID field */
        private static final long serialVersionUID = 6314922325253836414L;
        
    }
    
    /*
     * Number of rows for initial import
     */
    private static int INITIAL_ROW_NUMBER = 100;
    
    /*
     * A reader for file content
     */
    private BufferedReader reader;
    
    /*
     * Number of row to process
     */
    private int row = 0;    
    
    /*
     * Number of read bytes
     */
    private long bytesRead = 0;
    
    /**
     * Constructor
     * 
     * @param containerPath path to Project that will contain imported Spreadsheet
     * @param fileName name of imported File
     * @param stream content of imported File
     */
    public CSVImporter(IPath containerPath, String fileName, InputStream stream, long fileSize) {
        super(containerPath, fileName, stream, fileSize);
        reader = new BufferedReader(new InputStreamReader(fileContent));
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException {
        monitor.beginTask("Importing CSV data into Splash", 100);
        
        //create a Spreadsheet        
        createSpreadsheet(null);
        
        Transaction tx = NeoUtils.beginTransaction();
        try {
            String line;
            
            line = reader.readLine();
            
            // detecting type of separator;
            char sep=';';
            if (line.contains(";") == true){
                sep = ';';
            }else if (line.contains("\t")){
                sep = '\t';
            }else if (line.contains(",")){
                sep = ',';
            }
            
            CSVParser parser = new CSVParser(sep);
            int perc = 0;
            long totalBytes = fileSize;            
            int prevPerc = 0;
            CellFormat defaultFormat = new CellFormat();
            while (line != null  && line.lastIndexOf(sep) > 0){
                NeoSplashUtil.logn("loading line #" + row);

                List<String> list = parser.parse(line);
                Iterator<String> it = list.iterator();
                int j = 0;
                while (it.hasNext()) {
                    String value = (String)it.next();
                    Cell cell = new Cell(row, j, value, value, defaultFormat);
                    //save a cell
                    saveCell(cell);
                    j++;
                }

                monitor.setTaskName("Loading record #" + row);
                bytesRead += line.length();
                perc = (int)(100.0 * (float)bytesRead / (float)totalBytes);
                if (perc > prevPerc) {                    
                    monitor.worked(perc - prevPerc);
                    prevPerc = perc;
                }

                line = reader.readLine();
                
                row++;
                
                //update transaction each 1000 rows
                if ((row % 20) == 0) {
                    tx = updateTransaction(tx);                 
                }
                if (row == INITIAL_ROW_NUMBER) {
                    throw new InvocationTargetException(new CSVImportException());
                }
            }
            tx.success();
        } catch (IOException e) {
            tx.failure();
            throw new InvocationTargetException(e);
        }
        finally {
            tx.finish();
        }
    }
    
}
