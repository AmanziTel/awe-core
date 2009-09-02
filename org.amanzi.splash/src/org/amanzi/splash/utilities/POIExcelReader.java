package org.amanzi.splash.utilities;

import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.swing.SplashTableModel;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.eteks.openjeks.format.CellFormat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;

/**
 * This java program is used to read the data from a Excel file and display them
 * on the console output.
 *
 * @author dhanago
 */
public class POIExcelReader
{

	/** Creates a new instance of POIExcelReader */
	public POIExcelReader ()
	{}

	/**
	 * This method is used to display the Excel content to command line.
	 *
	 * @param xlsPath
	 */
	@SuppressWarnings ("unchecked")
	public void loadDataFromExcelFile (String xlsPath, SplashTableModel model)
	{
		InputStream inputStream = null;

		try
		{
			inputStream = new FileInputStream (xlsPath);
		}
		catch (FileNotFoundException e)
		{
			System.out.println ("File not found in the specified path.");
			e.printStackTrace ();
		}

		POIFSFileSystem fileSystem = null;

		try
		{
			fileSystem = new POIFSFileSystem (inputStream);

			HSSFWorkbook      workBook = new HSSFWorkbook (fileSystem);
			HSSFSheet         sheet    = workBook.getSheetAt (0);
			Iterator<HSSFRow> rows     = sheet.rowIterator ();

			while (rows.hasNext ())
			{
				HSSFRow row = rows.next ();

//				display row number in the console.
				System.out.println ("Row No.: " + row.getRowNum ());

//				once get a row its time to iterate through cells.
				Iterator<HSSFCell> cells = row.cellIterator ();

				while (cells.hasNext ())
				{
					HSSFCell cell = cells.next ();

					System.out.println ("Cell No.: " + cell.getCellNum ());
					
					int R = row.getRowNum ();
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
						
						Cell c = model.interpret(def, R, C);
						
						//Cell c = new Cell(R,C,richTextString.getString (), richTextString.getString (), new CellFormat());
						model.setValueAt(c, R, C);
						

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
						model.setValueAt(c, R, C);
						break;
					}
					
					case HSSFCell.CELL_TYPE_FORMULA:
						// cell type string.
						String cellFormula = "=" + cell.getCellFormula().toLowerCase();
						
						Cell c = model.interpret(cellFormula, R, C);
						
						//Cell c = new Cell(R,C,richTextString.getString (), richTextString.getString (), new CellFormat());
						model.setValueAt(c, R, C);
						
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
			}
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
	}

//	/**
//	 * The main executable method to test displayFromExcel method.
//	 *
//	 * @param args
//	 */
//	public static void main (String[] args)
//	{
//		POIExcelReader poiExample = new POIExcelReader ();
//		String         xlsPath    = "c:\\test.xls";
//
//		poiExample.loadDataFromExcelFile (xlsPath);
//	}
}                
