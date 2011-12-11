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
import java.util.Arrays;
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
import org.amanzi.neo.loader.core.parser.IConfigurationData;
import org.amanzi.neo.loader.core.saver.IData;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.neo.services.ui.enums.EventsType;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.IEventsListener;
import org.amanzi.neo.services.ui.events.ShowOnMapEvent;
import org.amanzi.neo.services.ui.utils.ActionUtil;
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

    public static final String IS_MAIN_ATTRUBUTE = "isMain";
    public static final String CLASS_ATTRUBUTE = "class";
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
    protected LinkedHashMap<ILoader<IData, T>, LoaderInfo<T>> loaders = new LinkedHashMap<ILoader<IData, T>, LoaderInfo<T>>();
    protected Map<ILoader< ? extends IData, T>, T> requiredLoaders = new LinkedHashMap<ILoader< ? extends IData, T>, T>();
    /** The max main page id. */
    protected int maxMainPageId;
    private ILoader< ? extends IData, T> selectedLoader;

    /**
     * Gets new loaders.
     * 
     * @return the loaders
     */
    public Set<ILoader<IData, T>> getLoaders() {
        return loaders.keySet();
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setNeedsProgressMonitor(true);

        // maxMainPageId = -1;
        List<IWizardPage> mainPages = getMainPagesList();
        for (IWizardPage iWizardPage : mainPages) {
            addPage(iWizardPage);
            maxMainPageId++;
        }
        for (Map.Entry<ILoader<IData, T>, LoaderInfo<T>> loaderEntry : loaders.entrySet()) {
            LoaderInfo<T> info = loaderEntry.getValue();
            int idPage = 0;
            for (IConfigurationElement pageClass : info.getPages()) {
                ILoaderPage<T> page = createAdditionalPage(pageClass);
                int id = checkIndexInPages(page);
                if (id == -1) {
                    addPage(page);
                    id = pages.size() - 1;
                }
                info.setPage(idPage, id);
                idPage++;
            }
        }
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
        if (loadernew != null) {
            LoaderInfo<T> info = loaders.get(loadernew);
            if (info.pageId.length == 0) {
                return true;
            }
            for (int i = 0; i < info.pageId.length; i++) {
                if (!pages.get(info.pageId[i]).isPageComplete()) {
                    return false;
                }
            }
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

    protected List<IWizardPage> getMainPagesList() {
        List<IWizardPage> mainPages = new LinkedList<IWizardPage>();
        for (Map.Entry<ILoader<IData, T>, LoaderInfo<T>> loaderEntry : loaders.entrySet()) {
            LoaderInfo<T> info = loaderEntry.getValue();
            for (IConfigurationElement pageClass : info.getPages()) {
                if (Boolean.parseBoolean(pageClass.getAttribute(IS_MAIN_ATTRUBUTE))) {
                    mainPages.add(createAdditionalPage(pageClass));
                    return mainPages;
                }
            }
        }
        return mainPages;
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
        ILoader< ? extends IData, T> loader = getSelectedLoader();
        LoaderInfo<T> info = loaders.get(loader);
        int previousWizardId = info.getPreviousWizardId(index);
        return previousWizardId == -1 ? pages.get(maxMainPageId) : pages.get(previousWizardId);
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
            return pages.get(index + 1);
        }
        ILoader< ? extends IData, T> selectedLoader = getSelectedLoader();
        if (selectedLoader == null) {
            return null;
        }
        LoaderInfo<T> infonew = loaders.get(selectedLoader);
        if (infonew == null) {
            return null;
        }
        if (index == maxMainPageId && infonew != null) {
            return null;
        }
        Integer nextWizardIdNew = null;
        if (infonew != null) {
            nextWizardIdNew = infonew.getNextWizardId(index);
        }
        return nextWizardIdNew == null ? null : pages.get(nextWizardIdNew);
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
            if (newloader.get(loader) != null && !newloader.get(loader).getFilesToLoad().isEmpty()) {
                assignMonitorToProgressLoader(monitor, loader);
                try {
                    loader.init(newloader.get(loader));
                } catch (Exception e) {
                    showError("Error.", "Cann't initialize loader:" + loader.getLoaderInfo().getName());
                    return;
                }
                try {
                    loader.run();
                } catch (DatabaseException e) {
                    showError("Error.", "Database exception was thrown  while saving data");
                    continue;
                } catch (AWEException e) {
                    showError("Error.", "error while finishup main transaction");
                    break;
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
    public void addLoaderToPage(ILoader<IData, T> loader, IConfigurationElement pageConfigElements) {
        LoaderInfo<T> info = new LoaderInfo<T>();
        info.setAdditionalPages(pageConfigElements);
        loaders.put(loader, info);
        requiredLoaders.put(loader, null);
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
     * <p>
     * Additional information about loaders
     * </p>
     * .
     * 
     * @param <T> the generic type
     * @author tsinkel_a
     * @since 1.0.0
     */
    public static class LoaderInfo<T extends IConfigurationData> {

        /** The page classes. */
        private IConfigurationElement[] pageClasses;

        /** The page id. */
        private int[] pageId = new int[0];

        /**
         * Sets the additional pages.
         * 
         * @param pageConfigElements the new additional pages
         */
        public void setAdditionalPages(IConfigurationElement pageConfigElements) {
            ArrayList<IConfigurationElement> list = new ArrayList<IConfigurationElement>();
            list.add(pageConfigElements);
            if (pageClasses != null) {
                list.addAll(Arrays.asList(pageClasses));
            }
            this.pageClasses = list.toArray(new IConfigurationElement[list.size()]);

            pageId = new int[pageClasses.length];
        }

        /**
         * Gets the previous wizard id.
         * 
         * @param index the index
         * @return the previous wizard id
         */
        public int getPreviousWizardId(int index) {
            for (int i = 0; i < pageId.length; i++) {
                if (pageId[i] == index) {
                    return i == 0 ? -1 : pageId[i - 1];
                }
            }
            throw new IllegalArgumentException("Wrong index=" + index);
        }

        /**
         * Gets the previous wizard id.
         * 
         * @param index the index
         * @return the previous wizard id
         */
        public Integer getNextWizardId(int index) {
            if (pageId.length == 0) {
                return null;
            }
            for (int i = 0; i < pageId.length; i++) {
                if (pageId[i] == index) {
                    return i == pageId.length - 1 ? null : pageId[i + 1];
                }
            }
            throw new IllegalArgumentException("Wrong index=" + index);
        }

        /**
         * Sets the page.
         * 
         * @param realId the real id
         * @param wizardId the wizard id
         */
        public void setPage(int realId, int wizardId) {
            pageId[realId] = wizardId;
        }

        /**
         * Gets the pages.
         * 
         * @return the pages
         */
        public IConfigurationElement[] getPages() {
            return pageClasses;
        }
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
