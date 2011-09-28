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

import org.amanzi.neo.loader.dialogs.DriveDialog;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 *DirectoryEditor
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class DirectoryEditor extends DirectoryFieldEditor {
    
    /**
     * Creates a directory field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public DirectoryEditor(String name, String labelText, Composite parent) {
        super(name, labelText, parent);
    }
    
    /* (non-Javadoc)
     * Method declared on StringButtonFieldEditor.
     * Opens the directory chooser dialog and returns the selected directory.
     */
    @Override
    protected String changePressed() {
        getTextControl().setText(DriveDialog.getDefaultDirectory());
        
        return super.changePressed();
    }
    
}

