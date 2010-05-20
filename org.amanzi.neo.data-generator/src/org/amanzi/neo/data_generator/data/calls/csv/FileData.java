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

package org.amanzi.neo.data_generator.data.calls.csv;

import java.util.HashMap;

/**
 * <p>
 * Data in one file.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class FileData {

    private String name;
    private Long time;
    private HashMap<Integer,HashMap<CsvHeaders, Object>> data = new HashMap<Integer,HashMap<CsvHeaders, Object>>();
    
    public FileData(String fileName, Long timestamp) {
        name = fileName;
        time = timestamp; 
    }
    
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return Returns the time.
     */
    public Long getTime() {
        return time;
    }
    
    /**
     * Gets cell value by header.
     *
     * @param lineNum int (row number)
     * @param header {@link CsvHeaders}
     * @return Object.
     */
    public Object getCellValue(int line,CsvHeaders header){
        return data.get(line).get(header);
    }
    
    /**
     * Add value to data.
     *
     * @param lineNum int (row number)
     * @param header {@link CsvHeaders}
     * @param value Object
     */
    public void addCellValue(int lineNum,CsvHeaders header, Object value){
        HashMap<CsvHeaders, Object> line = data.get(lineNum);
        if(line==null){
            line = new HashMap<CsvHeaders, Object>();
            data.put(lineNum, line);
        }
        line.put(header, value);
    }
    
    /**
     * @return lines count.
     */
    public int getLineCount(){
        return data.size();
    }
}
