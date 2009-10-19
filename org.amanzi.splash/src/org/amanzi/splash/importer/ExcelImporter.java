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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.splash.swing.Cell;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.neo4j.api.core.Transaction;

import com.eteks.openjeks.format.CellFormat;

/**
 * Importer of Excel data into Splash
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class ExcelImporter extends AbstractImporter {

    /**
     * Constructor 
     * 
     * @param containerPath path to Project that will contain new Spreadsheet
     * @param fileName name of File to import
     * @param stream content of File to import
     * @param fileSize size of File to import
     */
    public ExcelImporter(IPath containerPath, String fileName, InputStream stream, long fileSize) {
        super(containerPath, fileName, stream, fileSize);
    }

    @Override
    @SuppressWarnings(value = {"deprecation", "unchecked"})
    public void run(IProgressMonitor monitor) throws InvocationTargetException {
        POIFSFileSystem fileSystem = null;
        
        monitor.beginTask("Importing data from Excel", 100);

        Transaction tx = NeoUtils.beginTransaction();
        try
        {
            fileSystem = new POIFSFileSystem (fileContent);

            HSSFWorkbook      workBook = new HSSFWorkbook (fileSystem);
            HSSFSheet         sheet    = workBook.getSheetAt (0);
            Iterator<HSSFRow> rows     = sheet.rowIterator ();
            
            int rowCount = sheet.getLastRowNum();

            while (rows.hasNext ())
            {
                HSSFRow row = rows.next ();

                //display row number in the console.
                System.out.println ("Row No.: " + row.getRowNum ());

                //once get a row its time to iterate through cells.
                Iterator<HSSFCell> cells = row.cellIterator ();
                
                int R = row.getRowNum ();

                while (cells.hasNext ())
                {
                    HSSFCell cell = cells.next ();

                    System.out.println ("Cell No.: " + cell.getCellNum ());
                    
                    int C = cell.getCellNum();
                    
                    /*
                     * Now we will get the cell type and display the values
                     * accordingly.
                     */
                    switch (cell.getCellType ())
                    {
                    case HSSFCell.CELL_TYPE_NUMERIC :
                    {

                        // cell type numeric.
                        System.out.println ("====================================================");
                        System.out.println ("Numeric value: " + cell.getNumericCellValue ());
                        System.out.println ("====================================================");
                        String def = Double.toString(cell.getNumericCellValue());
                        
                        Cell c = new Cell(R, C, def, def, new CellFormat());
                        //TODO: interpet!!!!!!
                        //Cell c = model.interpret(def, R, C);
                        
                        saveCell(c);
                        break;
                    }

                    case HSSFCell.CELL_TYPE_STRING :
                    {
                        // cell type string.
                        HSSFRichTextString richTextString = cell.getRichStringCellValue ();
                        System.out.println ("====================================================");
                        System.out.println ("String value: " + richTextString.getString ());
                        System.out.println ("====================================================");
                        Cell c = new Cell(R,C,richTextString.getString (), richTextString.getString (), new CellFormat());
                        saveCell(c);
                        break;
                    }
                    
                    case HSSFCell.CELL_TYPE_FORMULA:
                        // cell type string.
                        String cellFormula = "=" + cell.getCellFormula().toLowerCase();
                        
                        Cell c = new Cell(R, C, cellFormula, cellFormula, new CellFormat());
                        //TODO: interpet!!!!!!
                        //Cell c = model.interpret(def, R, C);
                        
                        saveCell(c);
                        
                        System.out.println ("====================================================");
                        System.out.println ("Formula value: " + cellFormula);
                        System.out.println ("====================================================");

                        break;

                    default :
                    {

                        // types other than String and Numeric.
                        System.out.println ("Type not supported.");

                        break;
                    }
                    }
                }
                
                int percentage = (int)(100 * ((float)R/(float)rowCount));
                monitor.worked(percentage);
            }
        }
        catch (IOException e)
        {
            throw new InvocationTargetException(e);
        }
        finally {
            tx.success();
            tx.finish();
        }
    }

}
