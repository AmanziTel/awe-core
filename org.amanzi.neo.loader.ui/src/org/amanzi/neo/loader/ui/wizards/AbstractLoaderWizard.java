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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.ILoaderProgressListener;
import org.amanzi.neo.loader.core.IProgressEvent;
import org.amanzi.neo.loader.core.saver.IData;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.neo.services.ui.enums.EventsType;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.IEventsListener;
import org.amanzi.neo.services.ui.events.ShowOnMapEvent;
import org.amanzi.neo.services.ui.utils.ActionUtil;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * <p>
 * Abstract class for wizard for loaders
 * </p>
 * .
 * 
 * @param <T> the generic type
 * @author tsinkel_a
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public abstract class AbstractLoaderWizard<T extends IConfiguration> extends Wizard
        implements
            IGraphicInterfaceForLoaders<T>,
            IImportWizard {
    private static final Logger LOGGER = Logger.getLogger(AbstractLoaderWizard.class);
    public static final String IS_MAIN_ATTRUBUTE = "isMain";
    public static final String CLASS_ATTRUBUTE = "class";
    private Set<ILoader<IData, T>> pageLoaders = new HashSet<ILoader<IData, T>>();
    protected T configData;
    static {
        EventManager.getInstance().addListener(EventsType.SHOW_ON_MAP, new ShowOnMapHandling());
    }

    /**
     * abstract loader id, required for listeners;
     */

    public AbstractLoaderWizard() {
        super();

    }

    /** The pages. */
    protected List<IWizardPage> pages = new ArrayList<IWizardPage>();

    /**
     * new loaders
     */
    protected LinkedHashMap<ILoader<IData, T>, List<IConfigurationElement>> wizardLoaders = new LinkedHashMap<ILoader<IData, T>, List<IConfigurationElement>>();
    protected Map<ILoader< ? extends IData, T>, T> requiredLoaders = new LinkedHashMap<ILoader< ? extends IData, T>, T>();
    /** The max main page id. */
    protected int maxMainPageId = 1;
    private ILoader< ? extends IData, T> selectedLoader;

    /**
     * Gets new loaders.
     * 
     * @return the loaders
     */
    public Set<ILoader<IData, T>> getWizardLoaders() {
        return wizardLoaders.keySet();
    }

    /**
     * get loaders for current active page;
     */
    public Set<ILoader<IData, T>> getWizardLoadersForPage(String pageName) {
        pageLoaders.clear();
        for (ILoader<IData, T> loader : wizardLoaders.keySet()) {
            for (IConfigurationElement page : wizardLoaders.get(loader)) {
                if (page.getAttribute(CLASS_ATTRUBUTE).equals(pageName)) {
                    pageLoaders.add(loader);
                }
            }
        }
        return pageLoaders;
    }

    /**
     * check if current loader used in few pages
     * 
     * @return
     */
    public boolean isFewPagesForLoader() {
        ILoader< ? extends IData, T> checkedLoader = getSelectedLoader();
        if (checkedLoader == null) {
            return false;
        }
        ILoader< ? extends IData, T> loader = checkForLoader(checkedLoader);
        if (loader == null) {
            return false;
        }
        if (pageLoaders.contains(loader) && wizardLoaders.get(loader).size() > 1) {
            return true;
        }
        return false;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setNeedsProgressMonitor(true);

        // maxMainPageId = -1;
        IWizardPage mainPages = getMainPage();
        if (mainPages != null) {
            addPage(mainPages);
        } else {
            return;
        }
        for (List<IConfigurationElement> pages : wizardLoaders.values()) {
            for (IConfigurationElement page : pages) {
                ILoaderPage<T> createdPage = createAdditionalPage(page);
                int id = checkIndexInPages(createdPage);
                if (id == -1) {
                    addPage(createdPage);
                }
            }
        }
        selectedLoader = null;
    }

    /**
     * try to find page with the same name in page list..
     * 
     * @param page
     * @return index of found page, else return -1
     */
    private int checkIndexInPages(ILoaderPage<T> page) {
        int id = -1;
        for (IWizardPage p : pages) {
            if (p.getName().equals(page.getName())) {
                id++;
                break;
            }
        }
        return id;
    }

    /**
     * <p>
     * describe handling of show on map event;
     * </p>
     * 
     * @author Vladislav_Kondratenko
     * @since 1.0.0
     */
    private static class ShowOnMapHandling implements IEventsListener<ShowOnMapEvent> {

        @Override
        public void handleEvent(ShowOnMapEvent data) {
            List<IDataModel> modelsList = data.getModelsList();
            LoaderUiUtils.addGisDataToMap(modelsList.get(0).getName(), modelsList);
        }

        @Override
        public Object getSource() {
            return null;
        }
    }

    @Override
    public boolean canFinish() {
        for (int i = 0; i < maxMainPageId; i++) {
            if (!pages.get(i).isPageComplete()) {
                return false;
            }
        }
        ILoader< ? extends IData, T> loadernew = getSelectedLoader();
        if (loadernew == null) {
            return false;
        }
        for (int i = 0; i < pages.size(); i++) {
            if (!pages.get(i).isPageComplete()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates the additional page.
     * 
     * @param pageElement the page element
     * @return the i loader page
     */
    protected ILoaderPage<T> createAdditionalPage(IConfigurationElement pageElement) {
        try {
            return (ILoaderPage<T>)pageElement.createExecutableExtension(CLASS_ATTRUBUTE);
        } catch (CoreException e1) {
            // TODO Handle CoreException
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }
    }

    /**
     * return start page Start page this is page with isMain attribute
     * 
     * @return
     */
    protected IWizardPage getMainPage() {
        for (List<IConfigurationElement> pages : wizardLoaders.values()) {
            for (IConfigurationElement page : pages) {
                if (page.getAttribute(IS_MAIN_ATTRUBUTE) != null && Boolean.valueOf(page.getAttribute(IS_MAIN_ATTRUBUTE)))
                    return createAdditionalPage(page);
            }
        }
        return null;
    }

    /**
     * Adds the page.
     * 
     * @param page the page
     */
    @Override
    public void addPage(IWizardPage page) {
        pages.add(page);
        super.addPage(page);
    }

    /**
     * Gets the previous page.
     * 
     * @param page the page
     * @return the previous page
     */
    @Override
    public IWizardPage getPreviousPage(IWizardPage page) {
        int index = pages.indexOf(page);
        if (index == 0 || index == -1) {
            // first page or page not found
            return null;
        }
        if (index <= maxMainPageId) {
            return pages.get(index - 1);
        }
        return pages.get(index - 1);
    }

    /**
     * Gets the next page.
     * 
     * @param page the page
     * @return the next page
     */
    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        int index = pages.indexOf(page);
        if (index == -1) {
            // page not found
            return null;
        }
        if (index < maxMainPageId) {
            LoaderPage<T> nextPage = (LoaderPage<T>)pages.get(index + 1);
            nextPage.setPredifinedValues();
            return nextPage;
        }
        ILoader< ? extends IData, T> selectedLoader = getSelectedLoader();
        if (selectedLoader == null) {
            return null;
        }
        LoaderPage<T> nextPage;
        if ((pages.size() - 1) == index) {
            return null;
        }
        nextPage = (LoaderPage<T>)pages.get(index + 1);
        nextPage.setPredifinedValues();
        return nextPage;
    }

    @Override
    public boolean performFinish() {
        final Map<ILoader< ? extends IData, T>, T> newloader = getRequiredLoaders();
        Job job = new Job("Load data") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {

                load(newloader, monitor);

                return Status.OK_STATUS;
            }

        };
        job.schedule();
        return true;
    }

    /**
     * Load.
     * 
     * @param accessType the batch mode
     * @param data the data
     * @param loader the loader
     * @param monitor the monitor
     * @throws Exception
     */
    protected void load(final Map<ILoader< ? extends IData, T>, T> newloader, final IProgressMonitor monitor) {

        for (ILoader< ? extends IData, T> loader : newloader.keySet()) {
            if (newloader.get(loader) != null && newloader.get(loader).getDatasetNames().size() > 1) {
                assignMonitorToProgressLoader(monitor, loader);
                try {
                    loader.init(newloader.get(loader));
                } catch (Exception e) {
                    LOGGER.info("error while initialize data ", e);
                    showError("Error.", "Cann't initialize loader:" + loader.getLoaderInfo().getName());
                    return;
                }
                try {
                    loader.run();
                } catch (AWEException e) {
                    LOGGER.info("error while saving data ", e);
                    showError("Error.", " exception was thrown  while saving data");
                    continue;
                }
            }
        }
    }

    /**
     * show error message in ui thread;
     * 
     * @param title
     * @param message
     */
    private void showError(final String title, final String message) {
        ActionUtil.getInstance().runTask(new Runnable() {
            @Override
            public void run() {
                MessageDialog.openError(getShell(), title, message);
            }
        }, false);
    }

    /**
     * Assign monitor to progress loader.
     * 
     * @param monitor the monitor
     * @param loader the loadero
     */
    protected void assignMonitorToProgressLoader(final IProgressMonitor monitor, ILoader< ? extends IData, T> loader) {
        monitor.beginTask(loader.getLoaderInfo().getName(), 1000);
        loader.addProgressListener(new ILoaderProgressListener() {
            int jobCount = 0;

            @Override
            public void updateProgress(IProgressEvent event) {
                monitor.subTask(event.getProcessName());
                int jobDone = (int)(event.getPercentage() * 1000);
                monitor.worked(jobDone - jobCount);
                jobCount = jobDone;
                if (monitor.isCanceled()) {
                    event.cancelProcess();
                }
            }
        });
    }

    @Override
    public void addLoaderToWizard(ILoader<IData, T> loader, IConfigurationElement pageConfigElements) {
        ILoader< ? extends IData, T> foundLoader = checkForLoader(loader);
        if (foundLoader != null) {
            List<IConfigurationElement> wizardPage = wizardLoaders.get(foundLoader);
            if (wizardPage != null) {
                wizardPage.add(pageConfigElements);
            } else {
                wizardPage = new LinkedList<IConfigurationElement>();
                wizardPage.add(pageConfigElements);
            }
        } else {
            List<IConfigurationElement> pages = new LinkedList<IConfigurationElement>();
            pages.add(pageConfigElements);
            wizardLoaders.put(loader, pages);
        }

        requiredLoaders.put(loader, null);
    }

    /**
     * return existed loader or null of not exist
     * 
     * @param loader
     * @return
     */
    private ILoader< ? extends IData, T> checkForLoader(ILoader< ? extends IData, T> loader) {

        for (ILoader< ? extends IData, T> containedLoader : wizardLoaders.keySet()) {
            if (containedLoader.getLoaderInfo().getName().equals(loader.getLoaderInfo().getName())) {
                return containedLoader;
            }
        }
        return null;
    }

    /**
     * get the configuration data or create new one ;
     * 
     * @return
     */
    public T getConfigurationData() {
        ILoader< ? extends IData, T> currentLoader = getSelectedLoader();
        if (currentLoader != null) {
            if (requiredLoaders.get(currentLoader) == null) {
                configData = getConfigInstance();
                requiredLoaders.put(currentLoader, configData);
            } else {
                configData = requiredLoaders.get(currentLoader);
            }
        } else {
            configData = getConfigInstance();
            try {
                configData.getDatasetNames().put(ConfigurationDataImpl.PROJECT_PROPERTY_NAME,
                        ProjectModel.getCurrentProjectModel().getName());
            } catch (AWEException e) {
                // TODO Handle AWEException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
        return configData;
    }

    /**
     * create instance of configData
     * 
     * @return
     */
    protected abstract T getConfigInstance();

    /**
     * Gets the selected loader.
     * 
     * @return the selected loader
     */
    public ILoader< ? extends IData, T> getSelectedLoader() {
        return selectedLoader;
    }

    /**
     * Sets new selected loader.
     * 
     * @param selectedLoader the selected loader
     */
    public void setSelectedLoader(ILoader< ? extends IData, T> selectedLoader) {
        this.selectedLoader = selectedLoader;

    }

    /**
     * @return Returns the requiredLoaders.
     */
    public Map<ILoader< ? extends IData, T>, T> getRequiredLoaders() {
        return requiredLoaders;
    }

    @Override
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
        // TODO Auto-generated method stub

    }

}
