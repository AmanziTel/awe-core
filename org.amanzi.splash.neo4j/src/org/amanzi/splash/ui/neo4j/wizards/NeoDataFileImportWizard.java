/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.amanzi.splash.ui.neo4j.wizards;

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
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class NeoDataFileImportWizard extends Wizard implements IImportWizard {

	NeoDataImportWizardPage mainPage;

	public NeoDataFileImportWizard() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		final IFile file = mainPage.createNewFile();

		if (file == null)
			return false;


		String path = file.getLocation().toString();
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
			int i=0;
			int j=0;
			while (line != null  && line.lastIndexOf(";") > 0){
				CSVParser parser = new CSVParser(';');
				List list = parser.parse(line);
				Iterator it = list.iterator();
				i = 0;
				while (it.hasNext()) {
					model.setValueAt(new Cell("",it.next().toString()), i++, j);
					System.out.println(it.next());
				}

				line = lnr.readLine();
				j++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}



	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("File Import Wizard"); //NON-NLS-1
		setNeedsProgressMonitor(true);
		mainPage = new NeoDataImportWizardPage("Import Neo Data File",selection); //NON-NLS-1
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages(); 
		addPage(mainPage);        
	}

}
