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

import java.util.ArrayList;

import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.parser.IConfigurationData;
import org.amanzi.neo.loader.core.parser.IDataElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;

/**
 * <p>
 * Abstract page. Implementation of this class should define default constructor!
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public abstract class LoaderPage<T extends IConfigurationData> extends WizardPage implements ILoaderPage<T> {

    private ArrayList<ILoader< ? extends IDataElement, T>> loaders=new ArrayList<ILoader<? extends IDataElement,T>>();

    /**
     * Instantiates a new loader page.
     * 
     * @param pageName the page name
     */
    protected LoaderPage(String pageName) {
        super(pageName);
    }

    @Override
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
        if ("title".equals(propertyName)) {
            setTitle(String.valueOf(data));
        }
        if ("description".equals(propertyName)) {
            setDescription(String.valueOf(data));
        }
    }

    /**
     * update
     */
    protected void update() {
        setPageComplete(validateConfigData(getConfigurationData()));
    }

    /**
     * Gets the loaders descriptions.
     *
     * @return the loaders descriptions
     */
    protected String[] getLoadersDescriptions() {
        if (loaders.isEmpty()){
            AbstractLoaderWizard<T> wizard = (AbstractLoaderWizard<T>)getWizard();
            loaders.addAll(wizard.getLoaders());
        }
        String[] result=new String[loaders.size()];
        for (int i = 0; i < result.length; i++) {
            result[i]=loaders.get(i).getDescription();
        }
        return result;      
    }

    @Override
    public void setWizard(IWizard newWizard) {
        if (newWizard instanceof AbstractLoaderWizard) {
            super.setWizard(newWizard);
        }else{
            throw new IllegalArgumentException();
        }
    }

    protected void selectLoader(int selectionIndex) {
        ILoader< ? extends IDataElement, T> loader;
        if (selectionIndex<0||selectionIndex>=loaders.size()){
            loader=null;
        }else{
            loader = loaders.get(selectionIndex);
        }
        AbstractLoaderWizard<T> wizard = (AbstractLoaderWizard<T>)getWizard();
        wizard.setSelectedLoader(loader);
   }
    
    /**
     * Gets the selected loader.
     *
     * @return the selected loader
     */
    protected ILoader< ? extends IDataElement, T> getSelectedLoader(){
        AbstractLoaderWizard<T> wizard = (AbstractLoaderWizard<T>)getWizard();
        return wizard.getSelectedLoader();
    }
    /**
     * Gets the configuration data.
     * 
     * @return the configuration data
     */
    public T getConfigurationData() {
        IWizard wizard = getWizard();
        return ((AbstractLoaderWizard<T>)wizard).getConfigurationData();
    }
    
    /**
     * Validate config data.
     * 
     * @param configurationData the configuration data
     * @return true, if successful
     */
    protected abstract boolean validateConfigData(T configurationData);

}
