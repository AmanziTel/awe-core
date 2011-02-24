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
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.core.parser.IConfigurationData;
import org.amanzi.neo.loader.core.parser.IDataElement;
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
 * <p>
 * Abstract page. Implementation of this class should define default constructor!
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public abstract class LoaderPage<T extends IConfigurationData> extends WizardPage implements ILoaderPage<T> {

    private ArrayList<ILoader< ? extends IDataElement, T>> loaders = new ArrayList<ILoader< ? extends IDataElement, T>>();
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
    /**
     * Sets the access type.
     *
     * @param batchMode the new access type
     */
    protected void setAccessType(final boolean batchMode) {
        ((AbstractLoaderWizard<?>)getWizard()).setAccessType(batchMode?DatabaseManager.DatabaseAccessType.BATCH:DatabaseAccessType.EMBEDDED);
    }
    
    protected ILoader< ? extends IDataElement, T> autodefine(T data) {
        ILoader< ? extends IDataElement, T> loader = getSelectedLoader();
        ILoader< ? extends IDataElement, T> candidate=null;
        if (loader != null) {
            IValidateResult validateResult = loader.getValidator().accept(data);
            if (validateResult.getResult() == Result.SUCCESS ) {
                return loader;
            }else if (validateResult.getResult()==Result.UNKNOWN){
                candidate=loader;
            }
        }
        if (loaders.isEmpty()) {
            AbstractLoaderWizard<T> wizard = (AbstractLoaderWizard<T>)getWizard();
            loaders.addAll(wizard.getLoaders());
        }
        for (ILoader< ? extends IDataElement, T> loadr:loaders){
            Result result = loadr.getValidator().accept(data).getResult();
            if (result==Result.SUCCESS){
                return loadr;
            }else if (candidate==null&&result==Result.UNKNOWN){
                candidate=loadr;
            }
        }
        return candidate;
    }
    /**
    *
    */
   protected void selectCRS() {
       CoordinateReferenceSystem result = ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult<CoordinateReferenceSystem>() {

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
       update();
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
        if (loaders.isEmpty()) {
            AbstractLoaderWizard<T> wizard = (AbstractLoaderWizard<T>)getWizard();
            loaders.addAll(wizard.getLoaders());
        }
        String[] result = new String[loaders.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = loaders.get(i).getDescription();
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

    protected void selectLoader(int selectionIndex) {
        ILoader< ? extends IDataElement, T> loader;
        if (selectionIndex < 0 || selectionIndex >= loaders.size()) {
            loader = null;
        } else {
            loader = loaders.get(selectionIndex);
        }
        setSelectedLoader(loader);
    }
    protected int setSelectedLoader(ILoader< ? extends IDataElement, T> loader) {
        AbstractLoaderWizard<T> wizard = (AbstractLoaderWizard<T>)getWizard();
        wizard.setSelectedLoader(loader);
        return loader==null?-1:loaders.indexOf(loader);
    }
    

    /**
     * Gets the selected loader.
     * 
     * @return the selected loader
     */
    protected ILoader< ? extends IDataElement, T> getSelectedLoader() {
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
