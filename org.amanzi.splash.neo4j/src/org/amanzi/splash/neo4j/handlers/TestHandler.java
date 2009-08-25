package org.amanzi.splash.neo4j.handlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.List;

import org.amanzi.splash.neo4j.swing.Cell;
import org.amanzi.splash.neo4j.swing.SplashTable;
import org.amanzi.splash.neo4j.swing.SplashTableModel;
import org.amanzi.splash.neo4j.ui.AbstractSplashEditor;
import org.amanzi.splash.neo4j.utilities.CSVParser;
import org.amanzi.splash.neo4j.utilities.NeoSplashUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;

import com.eteks.openjeks.format.CellFormat;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class TestHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public TestHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
//		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
//		MessageDialog.openInformation(
//				window.getShell(),
//				"Amanzi: Neo4j-based  JRuby Spreadsheet Splash IDE Plug-in",
//				"Hello, Eclipse world");
		
		String path = "c:\\sample.txt";
		NeoSplashUtil.logn("path: " + path);
		InputStream is;
		try {
			is = new FileInputStream(path);
			LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));
			String line;
			line = lnr.readLine();
			AbstractSplashEditor editor = (AbstractSplashEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			SplashTable table = editor.getTable();
			SplashTableModel model = (SplashTableModel) table.getModel();
			//model.setValueAt(new Cell("Hello","Hello"), 1, 1);
			int i=1;
			int j=1;
			while (line != null  && line.lastIndexOf(";") > 0){
				CSVParser parser = new CSVParser(';');
				List list = parser.parse(line);
				Iterator it = list.iterator();
				j = 0;
				while (it.hasNext()) {
					model.setValueAt(new Cell(i, j++, "",(String) it.next(), new CellFormat()), i, j++);
					
					//System.out.println(it.next());
				}

				line = lnr.readLine();
				i++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
