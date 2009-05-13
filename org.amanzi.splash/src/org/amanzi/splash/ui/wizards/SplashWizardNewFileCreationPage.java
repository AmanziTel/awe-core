package org.amanzi.splash.ui.wizards;

/*
 * "The Java Developer's Guide to Eclipse"
 *   by D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003, 2004. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.MessageFormat;

import org.amanzi.splash.utilities.Util;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import com.eteks.openjeks.format.CellFormat;


/**
 * Add dimensions [rows, columns] to the standard <code>WizardNewFileCreationPage</code>.
 * 
 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage
 */
public class SplashWizardNewFileCreationPage
	extends WizardNewFileCreationPage {
	private IWorkbench workbench;
	private int rowCount = 30, columnCount = 30;
	private Text rowText, columnText;
	private boolean initialized = false;
	private static int nameCounter = 1;

	public SplashWizardNewFileCreationPage(
		IWorkbench workbench,
		IStructuredSelection selection) {
		super("SplashWizardNewFileCreationPage", selection);

		this.setTitle("Folder, Name and Dimensions");
		//Lagutko 8.05.2009, extract Spreadsheet file extensition to constant
		this.setFileName("sample" + nameCounter + Util.DEFAULT_SPREADSHEET_EXTENSION);
		this.workbench = workbench;
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		Group group = new Group((Composite) getControl(), SWT.NONE);
		group.setLayoutData(
			new GridData(
				GridData.GRAB_HORIZONTAL
					| GridData.HORIZONTAL_ALIGN_FILL
					| GridData.GRAB_VERTICAL
					| GridData.VERTICAL_ALIGN_FILL));
		group.setText("Dimensions");

		group.setLayout(new FormLayout());

		Label rowLabel = new Label(group, SWT.LEFT);
		rowLabel.setText("Rows");

		rowText = new Text(group, SWT.SINGLE | SWT.BORDER);
		rowText.setText(Integer.toString(rowCount));

		rowText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				SplashWizardNewFileCreationPage.this.handleEvent(null);
			}
		});

		Label columnLabel = new Label(group, SWT.LEFT);
		columnLabel.setText("Columns");

		columnText = new Text(group, SWT.SINGLE | SWT.BORDER);
		columnText.setText(Integer.toString(columnCount));

		FormData data = new FormData();
		data.left = new FormAttachment(0, 10);
		data.top = new FormAttachment(0, 10);
		rowLabel.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(30, 0);
		data.top = new FormAttachment(0, 10);
		data.width = 100;
		rowText.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0, 10);
		data.top = new FormAttachment(rowLabel, 10, SWT.DEFAULT);
		columnLabel.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(rowText, 0, SWT.LEFT);
		data.top = new FormAttachment(rowLabel, 10, SWT.DEFAULT);
		data.width = 100;
		columnText.setLayoutData(data);

		columnText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				SplashWizardNewFileCreationPage.this.handleEvent(null);
			}
		});

		Label separator = new Label(group, SWT.LEFT);
		data = new FormData();
		data.left = new FormAttachment(0, 10);
		data.top = new FormAttachment(columnText, 10, SWT.DEFAULT);
		data.height = 10;
		separator.setLayoutData(data);

		initialized = true;
	}

	public boolean finish() {
		IFile newFile = createNewFile();
		if (newFile == null)
			return false;

		//Lagutko: use utility function to open editor
		if (Util.openSpreadsheet(workbench, newFile) == null) {
			return false;
		}

		nameCounter++;

		return true;
	}

	public void handleEvent(Event event) {
		super.handleEvent(event);

		if (initialized) {
			if (isPageComplete()) {
				validateFileName();
			}

			if (isPageComplete()) {
				validateDimensions();
			}
		}
	}

	private void validateFileName() {
		String filename = getFileName();

		IPath path = new Path(filename);
		if (path.getFileExtension() == null
			|| !path.getFileExtension().toLowerCase().equals("jrss")) {
			setPageComplete(false);
			setErrorMessage("Extension must be 'jrss'");
		} else {
			setPageComplete(true);
		}
	}

	private void validateDimensions() {
		try {
			rowCount = Integer.parseInt(rowText.getText());
			columnCount = Integer.parseInt(columnText.getText());
			setPageComplete(true);
		} catch (NumberFormatException e) {
			setPageComplete(false);
			setErrorMessage("Invalid number.");
			return;
		}
		
		if (columnCount > Short.MAX_VALUE) {
			setPageComplete(false);
			setErrorMessage(
				MessageFormat.format("Maximum number of columns is {0}.", 
				new Object[] {Integer.toString(Short.MAX_VALUE)}));
			return;
		}
		
		if (rowCount > Short.MAX_VALUE) {
			setPageComplete(false);
			setErrorMessage(
				MessageFormat.format("Maximum number of rows is {0}.", 
				new Object[] {Integer.toString(Short.MAX_VALUE)}));
			return;
		}		

		if (columnCount < 0 || rowCount < 1) {
			setPageComplete(false);
			setErrorMessage("Invalid number of rows or columns.");
			return;
		}
		
		if (columnCount < 1) {
			setPageComplete(false);
			setErrorMessage("Must have a minimum of one column.");
			return;
		}
	}

	protected InputStream getInitialContents() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				sb.append(";" + "" + ";" + Util.getFormatString(new CellFormat()) + ";" + "false;");
			}
			sb.append("\n");
		}

		return new ByteArrayInputStream(sb.toString().getBytes());
	}
}