/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.amanzi.neo.wizards;

import java.io.File;

import org.amanzi.neo.loader.LoadNetwork;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * Load Network page wizard
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class NetworkImportWizardPage extends WizardPage {
    private FileFieldEditor editor;
    private String fileName = null;

    /**
     * Constructor
     * 
     * @param pageName page name
     * @param description description
     */
    public NetworkImportWizardPage(String pageName, String description) {
        super(pageName);
        setTitle(pageName);
        setDescription(description);
        setPageComplete(fileName != null);
    }

    @Override
    public void createControl(Composite parent) {
        Composite fileSelectionArea = new Composite(parent, SWT.NONE);
        GridData fileSelectionData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
        fileSelectionArea.setLayoutData(fileSelectionData);

        GridLayout fileSelectionLayout = new GridLayout();
        fileSelectionLayout.numColumns = 3;
        fileSelectionLayout.makeColumnsEqualWidth = false;
        fileSelectionLayout.marginWidth = 0;
        fileSelectionLayout.marginHeight = 0;
        fileSelectionArea.setLayout(fileSelectionLayout);

        editor = new FileFieldEditor("fileSelect", "Select File: ", fileSelectionArea); // NON-NLS-1
        // //NON-NLS-2
        editor.getTextControl(fileSelectionArea).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setFileName(NetworkImportWizardPage.this.editor.getStringValue());
            }
        });
        editor.setFileExtensions(LoadNetwork.NETWORK_FILE_EXTENSIONS);

        setControl(fileSelectionArea);
    }

    /**
     * sets file name
     * 
     * @param fileName - file name
     */
    protected void setFileName(String fileName) {
        this.fileName = fileName;
        setPageComplete(validate());
    }

    /**
     * validate page
     * 
     * @return true - if page valid
     */
    private boolean validate() {
        return new File(getFileName()).isFile();
    }

    /**
     * gets file name
     * 
     * @return Returns the fileName.
     */
    public String getFileName() {
        return fileName;
    }

}
