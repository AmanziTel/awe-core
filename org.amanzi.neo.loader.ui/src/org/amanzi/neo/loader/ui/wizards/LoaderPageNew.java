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

import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.db.manager.DatabaseManager.DatabaseAccessType;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.ILoaderNew;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.core.newsaver.IData;
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
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Vladislav_Kondratenko
 */
public abstract class LoaderPageNew<T extends IConfiguration> extends WizardPage implements ILoaderPageNew<T> {

    private ArrayList<ILoaderNew<IData, T>> newloaders = new ArrayList<ILoaderNew<IData, T>>();
    private CoordinateReferenceSystem selectedCRS;

    /**
     * Instantiates a new loader page.
     * 
     * @param pageName the page name
     */
    protected LoaderPageNew(String pageName) {
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
     * Sets the access type.
     * 
     * @param batchMode the new access type
     */
    protected void setAccessType(final boolean batchMode) {
        ((AbstractLoaderWizard< ? >)getWizard()).setAccessType(batchMode ? DatabaseManager.DatabaseAccessType.BATCH
                : DatabaseAccessType.EMBEDDED);
    }

    protected ILoaderNew< ? extends IData, T> autodefineNew(IConfiguration data) {
        ILoaderNew< ? extends IData, T> loader = getNewSelectedLoader();
        ILoaderNew< ? extends IData, T> candidate = null;
        if (loader != null) {
            Result validateResult = loader.getValidator().isAppropriate(data.getFilesToLoad());
            if (validateResult == Result.SUCCESS) {
                return loader;
            } else if (validateResult == Result.UNKNOWN) {
                candidate = loader;
            }
        }
        if (newloaders.isEmpty()) {
            AbstractLoaderWizardNew<T> wizard = (AbstractLoaderWizardNew<T>)getWizard();
            newloaders.addAll(wizard.getNewLoaders());
        }
        for (ILoaderNew<IData, T> loadr : newloaders) {
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
        updateNew();
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
        } catch (NoSuchAuthorityCodeException e) {
            // TODO Handle NoSuchAuthorityCodeException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    protected void updateNew() {
        setPageComplete(validateConfigData(getNewConfigurationData()));
    }

    /**
     * Gets the loaders descriptions.
     * 
     * @return the loaders descriptions
     */
    protected String[] getNewLoadersDescriptions() {
        if (newloaders.isEmpty()) {
            AbstractLoaderWizardNew<T> wizard = (AbstractLoaderWizardNew<T>)getWizard();
            newloaders.addAll(wizard.getNewLoaders());
        }
        String[] result = new String[newloaders.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = newloaders.get(i).getLoaderInfo().getType();
        }
        return result;
    }

    @Override
    public void setWizard(IWizard newWizard) {
        if (newWizard instanceof AbstractLoaderWizardNew) {
            super.setWizard(newWizard);
        } else {
            throw new IllegalArgumentException();
        }
    }

    protected void selectNewLoader(int selectionIndex) {
        ILoaderNew<IData, T> loader;
        if (selectionIndex < 0 || selectionIndex >= newloaders.size()) {
            loader = null;
        } else {
            loader = newloaders.get(selectionIndex);
        }
        setSelectedLoaderNew(loader);
    }

    protected int setSelectedLoaderNew(ILoaderNew< ? extends IData, T> loader) {
        AbstractLoaderWizardNew<T> wizard = (AbstractLoaderWizardNew<T>)getWizard();
        wizard.setSelectedLoaderNew(loader);
        return loader == null ? -1 : newloaders.indexOf(loader);
    }

    /**
     * Gets new selected loader.
     * 
     * @return the selected loader
     */
    protected ILoaderNew< ? extends IData, T> getNewSelectedLoader() {
        AbstractLoaderWizardNew<T> wizard = (AbstractLoaderWizardNew<T>)getWizard();
        return wizard.getNewSelectedLoader();
    }

    public T getNewConfigurationData() {
        IWizard wizard = getWizard();
        return ((AbstractLoaderWizardNew<T>)wizard).getNewConfigurationData();
    }

    /**
     * validate new configuration data
     * 
     * @param configurationData
     * @return true if validation result <code>SUCCESS</code>
     */
    protected abstract boolean validateConfigData(T configurationData);

    /**
     *
     */
    protected abstract void update();

}
