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

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.db.manager.DatabaseManager.DatabaseAccessType;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.ILoaderProgressListener;
import org.amanzi.neo.loader.core.IProgressEvent;
import org.amanzi.neo.loader.core.parser.IConfigurationData;
import org.amanzi.neo.loader.core.parser.IDataElement;
import org.amanzi.neo.loader.ui.NeoLoaderPlugin;
import org.amanzi.neo.services.ui.utils.ActionUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
public abstract class AbstractLoaderWizard<T extends IConfigurationData> extends Wizard implements IGraphicInterfaceForLoaders<T>, IImportWizard {

    /** The pages. */
    protected List<IWizardPage> pages = new ArrayList<IWizardPage>();
    /** The loaders. */
    protected LinkedHashMap<ILoader< ? extends IDataElement, T>, LoaderInfo<T>> loaders = new LinkedHashMap<ILoader< ? extends IDataElement, T>, LoaderInfo<T>>();

    /** The max main page id. */
    protected int maxMainPageId;
    /** The batch mode. */
    
    private ILoader< ? extends IDataElement, T> selectedLoader;

    /**
     * Gets the access type.
     * 
     * @return the access type
     */
    public DatabaseAccessType getAccessType() {
//        return accessType;
        //NOW BATCH DATABASE ACCESS DO NOT SUPPORT 
        return DatabaseAccessType.EMBEDDED;
    }

    /**
     * Gets the loaders.
     * 
     * @return the loaders
     */
    public Set<ILoader< ? extends IDataElement, T>> getLoaders() {
        return loaders.keySet();
    }

    /**
     * Sets the access type.
     * 
     * @param accessType the new access type
     */
    public void setAccessType(DatabaseAccessType accessType) {
        
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        //init pref store in not initialized before
        NeoLoaderPlugin.getDefault().getPreferenceStore().getString("init");
        
        setNeedsProgressMonitor(true);
        maxMainPageId = -1;
        List<IWizardPage> mainPages = getMainPagesList();
        for (IWizardPage iWizardPage : mainPages) {
            addPage(iWizardPage);
            maxMainPageId++;
        }
        for (Map.Entry<ILoader< ? extends IDataElement, T>, LoaderInfo<T>> loaderEntry : loaders.entrySet()) {
            LoaderInfo<T> info = loaderEntry.getValue();
            int idPage = 0;
            for (IConfigurationElement pageClass : info.getPages()) {
                ILoaderPage<T> page = createAdditionalPage(pageClass);
                // for this comparing for pages should be implement correct equals and hashCode
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

    @Override
    public boolean canFinish() {
        for (int i = 0; i <= maxMainPageId; i++) {
            if (!pages.get(i).isPageComplete()) {
                return false;
            }
        }
        ILoader< ? extends IDataElement, T> loader = getSelectedLoader();
        if (loader == null) {
            return false;
        }
        LoaderInfo<T> info = loaders.get(loader);
        if (info.pageId.length == 0) {
            return true;
        }
        for (int i = 0; i < info.pageId.length; i++) {
            if (!pages.get(info.pageId[i]).isPageComplete()) {
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
        ILoader< ? extends IDataElement, T> loader = getSelectedLoader();
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
        ILoader< ? extends IDataElement, T> loader = getSelectedLoader();
        if (loader == null) {
            return null;
        }
        LoaderInfo<T> info = loaders.get(loader);
        if (info.pageId.length == 0) {
            return null;
        }
        if (index == maxMainPageId) {
            return info.pageId.length == 0 ? null : pages.get(info.pageId[0]);
        }
        Integer nextWizardId = info.getNextWizardId(index);
        return nextWizardId == null ? null : pages.get(nextWizardId);
    }

    @Override
    public boolean performFinish() {
        final DatabaseAccessType accessType = getAccessType();
        final T data = getConfigurationData();
        final ILoader< ? extends IDataElement, T> loader = getSelectedLoader();

        if (data == null || loader == null) {
            return false;
        }
        if (accessType != DatabaseAccessType.EMBEDDED) {
            IRunnableWithProgress importer = new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    ActionUtil.getInstance().runTask(new Runnable() {

                        @Override
                        public void run() {
                            DatabaseManager.getInstance().setDatabaseAccessType(accessType);
                        }
                    }, false);
                    try {
                        load(accessType, data, loader, monitor);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        ActionUtil.getInstance().runTask(new Runnable() {

                            @Override
                            public void run() {
                                DatabaseManager.getInstance().setDatabaseAccessType(DatabaseAccessType.EMBEDDED);
                            }
                        }, false);
                    }
                }
            };
            try {
                getContainer().run(true, true, importer);
            } catch (InvocationTargetException e) {
                // TODO Handle InvocationTargetException
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (InterruptedException e) {
                // TODO Handle InterruptedException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        } else {
            Job job = new Job("Load data") {

                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    load(accessType, data, loader, monitor);
                    return Status.OK_STATUS;
                }
            };
            job.schedule();
        }
        return true;
    }

    /**
     * Load.
     * 
     * @param accessType the batch mode
     * @param data the data
     * @param loader the loader
     * @param monitor the monitor
     */
    protected void load(final DatabaseAccessType accessType, final T data, final ILoader< ? extends IDataElement, T> loader, IProgressMonitor monitor) {
        assignMonitorToProgressLoader(monitor, loader);
        loader.setup(accessType, data);
        if (accessType==DatabaseAccessType.EMBEDDED){
            loader.setPrintStream(new PrintStream(AweConsolePlugin.getDefault().getPrintStream()));
        }
        loader.load();
    }

    /**
     * Assign monitor to progress loader.
     * 
     * @param monitor the monitor
     * @param loader the loader
     */
    protected void assignMonitorToProgressLoader(final IProgressMonitor monitor, ILoader< ? extends IDataElement, T> loader) {
        monitor.beginTask(loader.getDescription(), 1000);
        loader.addProgressListener(new ILoaderProgressListener() {
            int jobCount = 0;

            @Override
            public void updateProgress(IProgressEvent event) {
                monitor.subTask(event.getProcessName());
                int jobDone = (int)(event.getPercentage() * 1000);
                monitor.worked(jobDone - jobCount);
                jobCount = jobDone;
                if (monitor.isCanceled()){
                    event.cancelProcess();
                }
            }
        });
    }

    /**
     * Adds the loader.
     * 
     * @param loader the loader
     * @param additionalPageClasses the additional page classes
     */
    @Override
    public void addLoader(ILoader< ? extends IDataElement, T> loader, IConfigurationElement[] pageConfigElements) {
        LoaderInfo<T> info = new LoaderInfo<T>();
        info.setAdditionalPages(pageConfigElements);
        loaders.put(loader, info);
    }

    /**
     * Gets the configuration data.
     * 
     * @return the configuration data
     */
    public abstract T getConfigurationData();

    /**
     * Gets the selected loader.
     * 
     * @return the selected loader
     */
    public ILoader< ? extends IDataElement, T> getSelectedLoader() {
        return selectedLoader;
    }

    /**
     * Sets the selected loader.
     * 
     * @param selectedLoader the selected loader
     */
    public void setSelectedLoader(ILoader< ? extends IDataElement, T> selectedLoader) {
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

    @Override
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
        // do nothing
    }
}
