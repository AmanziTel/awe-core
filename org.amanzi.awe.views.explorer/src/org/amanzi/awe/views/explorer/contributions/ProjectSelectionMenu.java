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

package org.amanzi.awe.views.explorer.contributions;

import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.actions.CompoundContributionItem;

/**
 * <p>
 * class for creation menu for choosing active project
 * </p>
 * 
 * @author Vladislav_Kondratenko
 */
public class ProjectSelectionMenu extends CompoundContributionItem {

    public ProjectSelectionMenu() {
    }

    public ProjectSelectionMenu(String id) {
        super(id);
    }

    @Override
    public void fill(Menu menu, int index) {
        try {
            for (IProjectModel projectModel : ProjectModel.findAllProjectModels()) {
                // create the menu item
                MenuItem menuItem = new MenuItem(menu, SWT.BUTTON1, index);
                menuItem.setText(projectModel.getName());
                menuItem.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        ProjectModel.setActiveProject(((MenuItem)e.getSource()).getText());
                    }
                });
            }
        } catch (AWEException e) {
            // TODO Handle AWEException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    protected IContributionItem[] getContributionItems() {
        IContributionItem[] contributionList = new IContributionItem[0];
        contributionList[0] = this;
        return contributionList;
    }
}