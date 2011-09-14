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

package org.amanzi.neo.loader.ui.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.amanzi.neo.loader.core.utils.ITableExporter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import au.com.bytecode.opencsv.CSVWriter;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Export utils
 * </p>.
 *
 * @author TsAr
 * @since 1.0.0
 */
public class ExportUtils {
    
    /** The separator. */
    private char separator;
    
    /** The quotechar. */
    private char quotechar;
    
    /** The escapechar. */
    private char escapechar;
    
    /** The line end. */
    private String lineEnd;
    
    /** The char set. */
    private Charset charSet;

    /**
     * Instantiates a new export utils.
     */
    public ExportUtils() {
        separator=CSVWriter.DEFAULT_SEPARATOR;
        quotechar=CSVWriter.DEFAULT_QUOTE_CHARACTER;
        escapechar=CSVWriter.DEFAULT_ESCAPE_CHARACTER;
        lineEnd=CSVWriter.DEFAULT_LINE_END;
        charSet=Charset.defaultCharset();
    }
    
    /**
     * Export table.
     *
     * @param tableModel the table model
     * @param monitor the monitor
     * @param jobName the job name
     * @param outputFile the output file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void exportTable(ITableExporter tableModel,IProgressMonitor monitor,String jobName,File outputFile) throws IOException{
        if (monitor==null){
            monitor=new NullProgressMonitor();
        }
        if (jobName==null){
            jobName="Export table to file "+outputFile.getName();
        }
        monitor.beginTask(jobName, tableModel.getRowCount()+1);
        CSVWriter writer=getCSVWriter(outputFile);
        try{
            String[] line=new String[tableModel.getColumnCount()];
            for (int column=0;column<tableModel.getColumnCount();column++){
                line[column]=tableModel.getHeader(column);
            }
            writer.writeNext(line); 
            monitor.worked(1);
            for (int row=0;row<tableModel.getRowCount();row++){
                for (int column=0;column<tableModel.getColumnCount();column++){
                    line[column]=tableModel.getItem(row, column);
                }                
                writer.writeNext(line);  
                monitor.worked(1);
            }
        }finally{
            closeWriter(writer);
        }
        
    }

    /**
     * Close writer.
     *
     * @param writer the writer
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void closeWriter(CSVWriter writer) throws IOException {
        if (writer!=null){
            writer.close();
        }
    }


    /**
     * Gets the CSV writer.
     *
     * @param outputFile the output file
     * @return the cSV writer
     * @throws FileNotFoundException the file not found exception
     */
    private CSVWriter getCSVWriter(File outputFile) throws FileNotFoundException {
        return new CSVWriter(new OutputStreamWriter(new FileOutputStream(outputFile), charSet),separator,quotechar,escapechar,lineEnd);
    }
    
    /**
     * Gets the char set.
     *
     * @return the char set
     */
    public Charset getCharSet() {
        return charSet;
    }

    /**
     * Sets the char set.
     *
     * @param charSet the new char set
     */
    public void setCharSet(Charset charSet) {
        this.charSet = charSet==null?Charset.defaultCharset():charSet;
    }

    /**
     * Gets the separator.
     *
     * @return the separator
     */
    public char getSeparator() {
        return separator;
    }
    
    /**
     * Sets the separator.
     *
     * @param separator the new separator
     */
    public void setSeparator(char separator) {
        this.separator = separator;
    }
    
    /**
     * Gets the quotechar.
     *
     * @return the quotechar
     */
    public char getQuotechar() {
        return quotechar;
    }
    
    /**
     * Sets the quotechar.
     *
     * @param quotechar the new quotechar
     */
    public void setQuotechar(char quotechar) {
        this.quotechar = quotechar;
    }
    
    /**
     * Gets the escapechar.
     *
     * @return the escapechar
     */
    public char getEscapechar() {
        return escapechar;
    }
    
    /**
     * Sets the escapechar.
     *
     * @param escapechar the new escapechar
     */
    public void setEscapechar(char escapechar) {
        this.escapechar = escapechar;
    }
    
    /**
     * Gets the line end.
     *
     * @return the line end
     */
    public String getLineEnd() {
        return lineEnd;
    }
    
    /**
     * Sets the line end.
     *
     * @param lineEnd the new line end
     */
    public void setLineEnd(String lineEnd) {
        this.lineEnd = lineEnd;
    }
    
}
