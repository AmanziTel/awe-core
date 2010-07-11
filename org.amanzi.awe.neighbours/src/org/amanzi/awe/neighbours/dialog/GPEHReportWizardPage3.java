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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * <p>
 * GPEHReportWizardPage3
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class GPEHReportWizardPage3 extends WizardPage {

    private DirectoryEditor editorDir;

    protected GPEHReportWizardPage3(String pageName, String pageDescription) {
        super(pageName);
        setTitle(pageName);
        setDescription(pageDescription);
    }

    /**
     * Creates the control.
     * 
     * @param parent the parent
     */
    @Override
    public void createControl(Composite parent) {
        final Group main = new Group(parent, SWT.FILL);
        main.setLayout(new GridLayout(1, false));
        main.setText("Select output location");
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        main.setLayoutData(data);

        // Group margin = new Group(main, SWT.FILL);
        // main.setLayout(new GridLayout(1, false));
        // main.setText("Select output location");
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
        validateFinish();
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
