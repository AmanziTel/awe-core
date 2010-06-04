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

package org.amanzi.awe.wizards.geoptima;

import org.amanzi.neo.preferences.SelectDataPreferencePage;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * <p>
 * Select Data Source Handler
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class SelectDataSourceHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        SelectDataPreferencePage page = new SelectDataPreferencePage();
        page.setTitle("Select Data Source");
        page.init(PlatformUI.getWorkbench());
        PreferenceManager mgr = new PreferenceManager();
        IPreferenceNode node = new PreferenceNode("1", page); //$NON-NLS-1$
        mgr.addToRoot(node);
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        PreferenceDialog pdialog = new PreferenceDialog(shell, mgr);;
        if (pdialog.open() == PreferenceDialog.OK) {
            page.performOk();
        }
        return null;

    }

}
