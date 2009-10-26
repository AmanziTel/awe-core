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
package org.amanzi.neo.wizards;

import java.io.File;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

/**
 * <p>
 * Extension of FileFieldEditor with possibility setting default directory
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class FileFieldEditorExt extends FileFieldEditor {
    private String defaulDirrectory;
    private String[] ext;

    /**
     * Creates a file field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public FileFieldEditorExt(String string, String string2, Composite main) {
        super(string, string2, main);
    }

    /**
     * @return Returns the defaulDirrectory.
     */
    public String getDefaulDirectory() {
        return defaulDirrectory;
    }

    /**
     * @param defaulDirrectory The defaulDirrectory to set.
     */
    public void setDefaulDirectory(String defaulDirrectory) {
        this.defaulDirrectory = defaulDirrectory;
    }

    @Override
    protected String changePressed() {
        File f = new File(getTextControl().getText());
        if (!f.exists()) {
            f = null;
        }
        File d = getFile(f);
        if (d == null) {
            return null;
        }

        return d.getAbsolutePath();
    }

    /**
     * Helper to open the file chooser dialog.
     * 
     * @param startingDirectory the directory to open the dialog on.
     * @return File The File the user selected or <code>null</code> if they do not.
     */
    protected File getFile(File startingDirectory) {

        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
        if (startingDirectory != null) {
            dialog.setFileName(startingDirectory.getPath());
        } else {
            dialog.setFilterPath(getDefaulDirectory());
        }
        if (ext != null) {
            dialog.setFilterExtensions(ext);
        }
        String file = dialog.open();
        if (file != null) {
            setDefaulDirectory(dialog.getFilterPath());
            file = file.trim();
            if (file.length() > 0) {
                return new File(file);
            }
        }

        return null;
    }

    @Override
    public void setFileExtensions(String[] extensions) {
        this.ext = extensions;
        super.setFileExtensions(extensions);
    }
}