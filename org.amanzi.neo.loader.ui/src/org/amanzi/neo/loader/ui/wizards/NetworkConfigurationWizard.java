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

package org.amanzi.neo.loader.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;


/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author lagutko_n
 * @since 1.0.0
 */
public class NetworkConfigurationWizard extends AbstractLoaderWizard {
    
    private String datasetName;
    
    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        if (datasetName == null && page instanceof AbstractLoaderPage) {
            datasetName = ((AbstractLoaderPage<?>)page).getConfiguration().getDatasetName();
        }
        
        IWizardPage result = super.getNextPage(page);
        
        if (datasetName != null && result instanceof AbstractLoaderPage) {
            ((AbstractLoaderPage<?>)result).getConfiguration().setDatasetName(datasetName);
            ((AbstractLoaderPage<?>)result).updateValues();
        }
        
        return result;
    }

}
