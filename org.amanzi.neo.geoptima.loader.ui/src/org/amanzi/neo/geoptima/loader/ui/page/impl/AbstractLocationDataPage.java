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

package org.amanzi.neo.geoptima.loader.ui.page.impl;

import org.amanzi.neo.geoptima.loader.ui.internal.Messages;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractLocationDataPage extends AbstractConfigurationPage {
    /**
     * @param pageName
     */
    protected AbstractLocationDataPage(final String pageName) {
        super(pageName);
    }

    @Override
    public IWizardPage getPreviousPage() {
        return getWizard().getPages()[0];
    }

    @Override
    public IWizardPage getNextPage() {
        IWizardPage page = getWizard().getPage(Messages.selectDataUploadingFilters_PageName);
        if (page == null) {
            ((Wizard)getWizard()).addPage(new SelectDatasetCredentialsPage());
        } else {
            return page;
        }
        return getWizard().getPage(Messages.selectDataUploadingFilters_PageName);
    }
}
