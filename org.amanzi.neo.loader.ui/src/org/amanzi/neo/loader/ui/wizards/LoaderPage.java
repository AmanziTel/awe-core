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

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.core.saver.IData;
import org.amanzi.neo.loader.ui.preferences.CommonCRSPreferencePage;
import org.amanzi.neo.services.ui.utils.ActionUtil;
import org.amanzi.neo.services.utils.RunnableWithResult;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * common action for wizards pages
 * 
 * @author Vladislav_Kondratenko
 */
public abstract class LoaderPage<T extends IConfiguration> extends WizardPage implements ILoaderPage<T> {

    protected ArrayList<ILoader<IData, T>> loaders = new ArrayList<ILoader<IData, T>>();
    private CoordinateReferenceSystem selectedCRS;

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

    @SuppressWarnings("unchecked")
    /**
     * try to autodefine loader by seted information
     *
     * @param data
     * @return
     */
    protected ILoader< ? extends IData, T> autodefineNew(T data) {
        ILoader< ? extends IData, T> loader = getSelectedLoader();
        ILoader< ? extends IData, T> candidate = null;
        if (loader != null) {
            Result validateResult = loader.getValidator().isAppropriate(data.getFilesToLoad());
            if (validateResult == Result.SUCCESS) {
                return loader;
            } else if (validateResult == Result.UNKNOWN) {
                candidate = loader;
            }
        }
        if (loaders.isEmpty()) {
            AbstractLoaderWizard<T> wizard = (AbstractLoaderWizard<T>)getWizard();
            loaders.addAll(wizard.getLoaders());
        }
        for (ILoader<IData, T> loadr : loaders) {
            Result validateResult = loadr.getValidator().isAppropriate(data.getFilesToLoad());
            if (validateResult == Result.SUCCESS) {
                return loadr;
            } else if (candidate == null && validateResult == Result.UNKNOWN) {
                candidate = loadr;
            }
        }
        return candidate;
    }

    /**
    *
    */
    protected void selectCRS() {
        CoordinateReferenceSystem result = ActionUtil.getInstance().runTaskWithResult(
                new RunnableWithResult<CoordinateReferenceSystem>() {

                    private CoordinateReferenceSystem result;

                    @Override
                    public CoordinateReferenceSystem getValue() {
                        return result;
                    }

                    @Override
                    public void run() {
                        result = null;
                        CommonCRSPreferencePage page = new CommonCRSPreferencePage();
                        page.setSelectedCRS(getSelectedCRS());
                        page.setTitle("Select Coordinate Reference System");
                        page.setSubTitle("Select the coordinate reference system from the list of commonly used CRS's, or add a new one with the Add button");
                        page.init(PlatformUI.getWorkbench());
                        PreferenceManager mgr = new PreferenceManager();
                        IPreferenceNode node = new PreferenceNode("1", page); //$NON-NLS-1$
                        mgr.addToRoot(node);
                        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                        PreferenceDialog pdialog = new PreferenceDialog(shell, mgr);;
                        if (pdialog.open() == PreferenceDialog.OK) {
                            page.performOk();
                            result = page.getCRS();
                        }

                    }

                });

        setSelectedCRS(result);
    }

    protected void setSelectedCRS(CoordinateReferenceSystem result) {
        if (result == null) {
            return;
        }
        selectedCRS = result;
        commonUpdate();
    }

    /**
     * Gets the selected crs.
     * 
     * @return the selected crs
     */
    public CoordinateReferenceSystem getSelectedCRS() {
        return selectedCRS == null ? getDefaultCRS() : selectedCRS;
    }

    /**
     * Gets the default crs.
     * 
     * @return the default crs
     */
    private CoordinateReferenceSystem getDefaultCRS() {
        try {
            return CRS.decode("EPSG:4326");
        } catch (FactoryException e) {
            // TODO Handle NoSuchAuthorityCodeException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * update configurated page
     */
    protected void commonUpdate() {
        setPageComplete(validateConfigData(getConfigurationData()));
    }

    /**
     * Gets the loaders descriptions.
     * 
     * @return the loaders descriptions
     */
    @SuppressWarnings("unchecked")
    protected String[] getLoadersDescription() {
        if (loaders.isEmpty()) {
            AbstractLoaderWizard<T> wizard = (AbstractLoaderWizard<T>)getWizard();
            loaders.addAll(wizard.getLoaders());
        }
        String[] result = new String[loaders.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = loaders.get(i).getLoaderInfo().getName();
        }
        return result;
    }

    @Override
    public void setWizard(IWizard newWizard) {
        if (newWizard instanceof AbstractLoaderWizard) {
            super.setWizard(newWizard);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * select loader with appropriate with it number in combobox
     * 
     * @param selectionIndex
     */
    protected void selectLoader(int selectionIndex) {
        ILoader<IData, T> loader;
        if (selectionIndex < 0 || selectionIndex >= loaders.size()) {
            loader = null;
        } else {
            loader = loaders.get(selectionIndex);
        }
        setSelectedLoader(loader);
    }

    /**
     * set the loader with which user will work;
     * 
     * @param loader
     * @return
     */
    @SuppressWarnings("unchecked")
    protected int setSelectedLoader(ILoader< ? extends IData, T> loader) {
        AbstractLoaderWizard<T> wizard = (AbstractLoaderWizard<T>)getWizard();
        wizard.setSelectedLoader(loader);
        return loader == null ? -1 : loaders.indexOf(loader);
    }

    /**
     * Gets new selected loader.
     * 
     * @return the selected loader
     */
    @SuppressWarnings("unchecked")
    protected ILoader< ? extends IData, T> getSelectedLoader() {
        AbstractLoaderWizard<T> wizard = (AbstractLoaderWizard<T>)getWizard();
        return wizard.getSelectedLoader();
    }

    /**
     * @return configuration data instance; if it is <code>NULL</code> return new one;
     */
    @SuppressWarnings("unchecked")
    public T getConfigurationData() {
        IWizard wizard = getWizard();
        return ((AbstractLoaderWizard<T>)wizard).getConfigurationData();
    }

    /**
     * validate new configuration data
     * 
     * @param configurationData
     * @return true if validation result <code>SUCCESS</code>
     */
    protected abstract boolean validateConfigData(T configurationData);

    /**
     * method for updating page
     */
    protected abstract void update();

}
