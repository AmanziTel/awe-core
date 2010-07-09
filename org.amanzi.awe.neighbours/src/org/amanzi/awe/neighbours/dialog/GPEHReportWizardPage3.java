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

package org.amanzi.awe.neighbours.dialog;

import java.io.File;

import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.amanzi.neo.wizards.DirectoryEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * <p>
 * GPEHReportWizardPage3
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class GPEHReportWizardPage3 extends WizardPage {

    private Combo cFileType;
    private DirectoryEditor editorDir;

    protected GPEHReportWizardPage3(String pageName, String pageDescription) {
        super(pageName);
        setTitle(pageName);
        setDescription(pageDescription);
    }

    public FileType getFileType() {
        return FileType.findByString(cFileType.getText());
    }

    /**
     * Creates the control.
     * 
     * @param parent the parent
     */
    @Override
    public void createControl(Composite parent) {
        final Group main = new Group(parent, SWT.FILL);
        main.setLayout(new GridLayout(3, false));
        main.setText("Select output location");

        Label label = new Label(main, SWT.NONE);
        label.setText("File type");
        cFileType = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData layoutData = new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 1);
        layoutData.minimumWidth = 200;
        cFileType.setLayoutData(layoutData);
        
        editorDir = new DirectoryEditor("editor", NeoLoaderPluginMessages.AMSImport_directory, main);
        editorDir.setChangeButtonText("...");
        editorDir.getTextControl(main).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (!editorDir.getTextControl(main).isEnabled()) {
                    return;
                }
                validateFinish();
            }
        });
        

        setControl(main);

        init();
    }
    
    /**
     * Validate finish.
     */
    private void validateFinish() {
        setPageComplete(isValidPage());
    }

    /**
     * Checks if is valid page.
     * 
     * @return true, if is valid page
     */
    protected boolean isValidPage() {
        try {
            String dir = editorDir.getStringValue();
            File file = new File(dir);
            return file.isAbsolute() && file.exists() && !file.isFile();
        } catch (Exception e) {
            // TODO: debug
            e.printStackTrace();
            return false;
        }
    }

    private void init() {
        formFileTypes();
        
        validateFinish();
    }

    private void formFileTypes() {
        for (FileType fileType : FileType.values())
            cFileType.add(fileType.toString());
        cFileType.select(0);
        cFileType.setEnabled(false);
    }

    public enum FileType {
        CSV, PDF, XLS;
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }

        public static FileType findByString(String string) {
            if (string == null) {
                return null;
            }
            for (FileType type : FileType.values()) {
                if (type.toString().equals(string.toLowerCase())) {
                    return type;
                }
            }
            return null;
        }

    }
    /**
     * Gets the target dir.
     * 
     * @return the target dir
     */
    public File getTargetDir() {
        return new File(editorDir.getStringValue());
    }

}
