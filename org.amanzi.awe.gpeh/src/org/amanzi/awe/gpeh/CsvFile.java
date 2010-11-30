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

package org.amanzi.awe.gpeh;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Class to represent csv-files
 * <p>
 *
 * </p>
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class CsvFile {

    private static final int BUFFER_SIZE = 8*1024;
    private String newline = System.getProperty("line.separator");
    private BufferedWriter writer  = null;
    private int eventId = 0;
    private File file = null;
    ArrayList<String> headers = null;
    
    public void setHeaders(ArrayList<String> headers) {
        this.headers = headers;
    }
    
    public ArrayList<String> getHeaders() {
        return this.headers;
    }
    
    /**
     * @param _eventId The id of event
     */
    public void setEventId(int _eventId) {
        this.eventId = _eventId;
    }

    /**
     * @return Returns the id of event
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * @param _file The _file to set.
     */
    public void setFile(File _file) {
        this.file = _file;
    }

    /**
     * @return Returns the _file.
     */
    public File getFile() {
        return file;
    }

    /**
     * Constructor.
     * Creates a csv file for writing data to it
     * @param file The file to write data to
     * @return 
     * @throws IOException 
     */
    public CsvFile(File file) throws IOException {
        writer = new BufferedWriter(new FileWriter(file), BUFFER_SIZE);
    }

    /**
     * Closes the csv-file.
     */
    public void close() throws IOException {
        this.writer.flush();
        this.writer.close();
    }
    
    /**
     * Writes a data-record to the file. Note that data must have
     * same number of elements as the header had.
     *
     * @param data Data to write to csv-file
     */
     public void writeData(ArrayList<String> values) throws IOException {

        int n = 0;
        for (String value : values) {
            if (n != 0) {
                this.writer.write("\t");
            }
            
            if (value != null) {
                this.writer.write(value);
                n = 1;
            }
        }

        this.writer.write(newline);
    }
}
