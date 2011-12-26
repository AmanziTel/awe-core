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

package org.amanzi.neo.loader.core.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * common row container contains temporary <b>HEADERS</b> and <b>ROW</b> information
 * 
 * @author Kondratenko_Vladislav
 */
public class CSVContainer implements IData {
    /**
     * contain rows values;
     */
    private List<String> row = new LinkedList<String>();
    /**
     * name of parsed file
     */
    private File file;

    /**
     * contain header values;
     */
    private List<String> headers;
    /**
     * contains first line of file;
     */
    private String firstLine = StringUtils.EMPTY;

    /**
     * @return Returns the firstLine.
     */
    public String getFirstLine() {
        return firstLine;
    }

    /**
     * @param firstLine The firstLine to set.
     */
    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }

    public CSVContainer(int minimalLength) {
        super();
    }

    /**
     * @return Returns the row.
     */
    public List<String> getValues() {
        return row;
    }

    /**
     * @param row The row to set.
     */
    public void setValues(List<String> row) {

        this.row = row;
    }

    /**
     * @return Returns the header.
     */
    public List<String> getHeaders() {
        return headers;
    }

    /**
     * @param header The header to set.
     */
    public void setHeaders(List<String> header) {
        this.headers = header;
    }

    /**
     * @return Returns the fileName.
     */
    public File getFile() {
        return file;
    }

    /**
     * @param fileName The fileName to set.
     */
    public void setFile(File fileName) {
        this.file = fileName;
    }
    
    public void put(String header, String value) {
        row.add(value);
        headers.add(header);
    }

}
