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

package org.amanzi.awe.ui.view.widgets;

import java.io.File;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AdvancedFileFieldEditor extends FileFieldEditor {

    private static final int NUMBER_OF_COLUMNS = 4;

    /**
     * @param name
     * @param labelText
     * @param parent
     */
    public AdvancedFileFieldEditor(final String name, final String labelText, final Composite parent) {
        super(name, labelText, parent);
        setFilterPath(new File(AWEUIPlugin.getDefault().getDefaultLoadPath()));
    }

    @Override
    protected boolean checkState() {
        boolean state = super.checkState();

        if (state) {
            File file = new File(getStringValue());

            if (file.getParentFile() != null) {
                AWEUIPlugin.getDefault().setDefaultLoadPath(file.getParentFile().getAbsolutePath());
            }
        }

        return state;
    }

    @Override
    public int getNumberOfControls() {
        return NUMBER_OF_COLUMNS;
    }
}
