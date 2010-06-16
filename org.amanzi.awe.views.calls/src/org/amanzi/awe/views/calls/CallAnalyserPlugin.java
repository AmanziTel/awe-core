package org.amanzi.awe.views.calls;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.amanzi.awe.views.calls.upload.StatisticsDataLoader;
import org.amanzi.awe.views.calls.views.CallAnalyserView;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.listener.IUpdateViewListener;
import org.amanzi.neo.core.database.services.events.ImportCsvStatisticsEvent;
import org.amanzi.neo.core.database.services.events.UpdateViewEvent;
import org.amanzi.neo.core.database.services.events.UpdateViewEventType;
import org.amanzi.neo.core.utils.ActionUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CallAnalyserPlugin extends AbstractUIPlugin implements IUpdateViewListener {
    private static final Collection<UpdateViewEventType> handedTypes;
    static {
        Collection<UpdateViewEventType> spr = new HashSet<UpdateViewEventType>();
        spr.add(UpdateViewEventType.GIS);
        spr.add(UpdateViewEventType.IMPORT_STATISTICS);
        handedTypes = Collections.unmodifiableCollection(spr);
    }
	// The plug-in ID
	public static final String PLUGIN_ID = "org.amanzi.awe.views.calls";

	// The shared instance
	private static CallAnalyserPlugin plugin;
	
	/**
	 * The constructor
	 */
	public CallAnalyserPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
        NeoCorePlugin.getDefault().getUpdateViewManager().addListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
        NeoCorePlugin.getDefault().getUpdateViewManager().removeListener(this);
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static CallAnalyserPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

    /**
     *updates ReuseAnalyserView
     */
    private void updateView() {
        ActionUtil.getInstance().runTask(new Runnable() {

            @Override
            public void run() {
                IViewPart reuseView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(CallAnalyserView.ID);
                if (reuseView != null) {
                    ((CallAnalyserView)reuseView).updateView(false);
                }
            }
        }, true);
    }
    
    private void runImportStatistics(ImportCsvStatisticsEvent event){
        final String fileName = event.getDirectory();
        final String dataset = event.getDataset();
        final String network = event.getNetwork();
        Job job = new Job("Import AMS statistics '" + (new File(fileName)).getName() + "'") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {                
                StatisticsDataLoader loader = new StatisticsDataLoader(fileName, dataset, network, null);                
                try {
                    loader.run(monitor);
                } catch (IOException e) {
                    return new Status(Status.ERROR, "org.amanzi.awe.views.calls", e.getMessage());
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }


    @Override
    public void updateView(UpdateViewEvent event) {
        switch (event.getType()) {
        case IMPORT_STATISTICS:
            runImportStatistics((ImportCsvStatisticsEvent)event);
            break;
        default:
            updateView();
        }        
    }

    @Override
    public Collection<UpdateViewEventType> getType() {
        return handedTypes;
    }
}
