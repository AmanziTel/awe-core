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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.ILoaderProgressListener;
import org.amanzi.neo.loader.core.IProgressEvent;
import org.amanzi.neo.loader.core.parser.IConfigurationData;
import org.amanzi.neo.loader.core.saver.IData;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.ui.events.IEventsListener;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.UpdateDataEvent;
import org.amanzi.neo.services.ui.neoclipse.manager.NeoclipseViewerManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.amanzi.neo.services.ui.enums.*;

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
public abstract class AbstractLoaderWizard<T extends IConfiguration> extends Wizard
        implements
            IGraphicInterfaceForLoaders<T>,
            IImportWizard {

    @SuppressWarnings("unchecked")
    public AbstractLoaderWizard() {
        super();
        EventManager.getInstance().addListener(EventsType.UPDATE_DATA, new RefreshNeoclipseView());

    }

    /** The pages. */
    protected List<IWizardPage> pages = new ArrayList<IWizardPage>();

    /**
     * new loaders
     */
    protected LinkedHashMap<ILoader<IData, T>, LoaderInfo<T>> newloaders = new LinkedHashMap<ILoader<IData, T>, LoaderInfo<T>>();
    protected Map<ILoader< ? extends IData, T>, T> requiredLoaders = new LinkedHashMap<ILoader< ? extends IData, T>, T>();
    /** The max main page id. */
    protected int maxMainPageId;
    private ILoader< ? extends IData, T> newSelectedLoader;

    /**
     * Gets new loaders.
     * 
     * @return the loaders
     */
    public Set<ILoader<IData, T>> getNewLoaders() {
        return newloaders.keySet();
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setNeedsProgressMonitor(true);
        maxMainPageId = -1;
        List<IWizardPage> mainPages = getMainPagesList();
        for (IWizardPage iWizardPage : mainPages) {
            addPage(iWizardPage);
            maxMainPageId++;
        }
        for (Map.Entry<ILoader<IData, T>, LoaderInfo<T>> loaderEntry : newloaders.entrySet()) {
            LoaderInfo<T> info = loaderEntry.getValue();
            int idPage = 0;
            for (IConfigurationElement pageClass : info.getPages()) {
                ILoaderPage<T> page = createAdditionalPage(pageClass);
                // for this comparing for pages should be implement correct
                // equals and hashCode
                // methods (not necessary)
                int id = pages.indexOf(pageClass);
                if (id == -1) {
                    addPage(page);
                    id = pages.size() - 1;
                }
                info.setPage(idPage, id);
                idPage++;
            }
        }
    }

    private class RefreshNeoclipseView implements IEventsListener<UpdateDataEvent> {
        @Override
        public void handleEvent(UpdateDataEvent data) {
            NeoclipseViewerManager.getInstance().refreshNeoclipseView();
        }
    }

    @Override
    public boolean canFinish() {
        for (int i = 0; i <= maxMainPageId; i++) {
            if (!pages.get(i).isPageComplete()) {
                return false;
            }
        }
        ILoader< ? extends IData, T> loadernew = getNewSelectedLoader();
        if (loadernew == null) {
            return false;
        }
        if (loadernew != null) {
            LoaderInfo<T> info = newloaders.get(loadernew);
            if (info.pageId.length == 0) {
                return true;
            }
            for (int i = 0; i < info.pageId.length; i++) {
                if (!pages.get(info.pageId[i]).isPageComplete()) {
                    return false;
                }
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
    @SuppressWarnings("unchecked")
    protected ILoaderPage<T> createAdditionalPage(IConfigurationElement pageElement) {
        try {
            return (ILoaderPage<T>)pageElement.createExecutableExtension("class");
        } catch (CoreException e1) {
            // TODO Handle CoreException
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }
    }

    /**
     * Gets the main pages list.
     * 
     * @return the main pages list
     */
    protected abstract List<IWizardPage> getMainPagesList();

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
        ILoader< ? extends IData, T> loader = getNewSelectedLoader();
        LoaderInfo<T> info = newloaders.get(loader);
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
        ILoader< ? extends IData, T> loaderNew = getNewSelectedLoader();
        if (loaderNew == null) {
            return null;
        }
        LoaderInfo<T> infonew = newloaders.get(loaderNew);
        if (infonew == null) {
            return null;
        }
        if (index == maxMainPageId && infonew != null) {
            return infonew.pageId.length == 0 ? null : pages.get(infonew.pageId[0]);
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
                newload(newloader, monitor);
                try {
                    addDataToCatalog();
                    EventManager.getInstance().fireEvent(new UpdateDataEvent());
                } catch (MalformedURLException e) {
                    MessageDialog.openError(getShell(), "Error while add data to catalog", "Cann't add data to catalog");
                    e.printStackTrace();
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        return true;
    }

    public static void addDataToCatalog() throws MalformedURLException {
        String databaseLocation = DatabaseManagerFactory.getDatabaseManager().getLocation();

        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        URL url = new URL("file://" + databaseLocation);
        List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(url);
        for (IService service : services) {
            if (catalog.getById(IService.class, service.getID(), new NullProgressMonitor()) != null) {
                catalog.replace(service.getID(), service);
            } else {
                catalog.add(service);
            }
        }
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
    protected void newload(final Map<ILoader< ? extends IData, T>, T> newloader, IProgressMonitor monitor) {

        for (ILoader< ? extends IData, T> loader : newloader.keySet()) {
            if (newloader.get(loader) != null) {
                assignMonitorToProgressLoader(monitor, loader);
                try {
                    loader.init(newloader.get(loader));
                } catch (Exception e) {
                    MessageDialog.openError(getShell(), getWindowTitle(), "Cann't initialize loader:"
                            + loader.getLoaderInfo().getType());
                    return;
                }
                try {
                    loader.run();
                } catch (DatabaseException e) {
                    MessageDialog.openError(getShell(), getWindowTitle(), "Cann't load data, because: " + e.getMessage());
                    continue;
                } catch (AWEException e) {
                    MessageDialog
                            .openError(getShell(), getWindowTitle(), "Cann't finsihup transaction, because: " + e.getMessage());
                    break;
                }
            }
        }
    }

    /**
     * Assign monitor to progress loader.
     * 
     * @param monitor the monitor
     * @param loader the loader
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
    public void addNewLoader(ILoader<IData, T> loader, IConfigurationElement[] pageConfigElements) {
        LoaderInfo<T> info = new LoaderInfo<T>();
        info.setAdditionalPages(pageConfigElements);
        newloaders.put(loader, info);
    }

    /**
     * get the new configuration data ;
     * 
     * @return
     */
    public abstract T getNewConfigurationData();

    /**
     * Gets the selected loader.
     * 
     * @return the selected loader
     */
    public ILoader< ? extends IData, T> getNewSelectedLoader() {
        return newSelectedLoader;
    }

    /**
     * Sets new selected loader.
     * 
     * @param selectedLoader the selected loader
     */
    public void setSelectedLoaderNew(ILoader< ? extends IData, T> selectedLoader) {
        this.newSelectedLoader = selectedLoader;

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
        public void setAdditionalPages(IConfigurationElement[] pageConfigElements) {
            this.pageClasses = pageConfigElements;

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
