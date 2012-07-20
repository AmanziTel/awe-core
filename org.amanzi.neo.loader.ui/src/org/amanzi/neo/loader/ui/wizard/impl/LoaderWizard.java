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

package org.amanzi.neo.loader.ui.wizard.impl;

import org.amanzi.neo.loader.ui.page.ILoaderPage;
import org.amanzi.neo.loader.ui.wizard.ILoaderWizard;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class LoaderWizard extends Wizard implements ILoaderWizard {

    @Override
    public void init(final IWorkbench workbench, final IStructuredSelection selection) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean performFinish() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void addLoaderPage(final ILoaderPage loaderPage) {
        // TODO Auto-generated method stub

    }

}
