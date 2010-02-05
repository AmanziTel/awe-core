package org.amanzi.awe.views.calls;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.amanzi.awe.views.calls.views.CallAnalyserView;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.listener.IUpdateDatabaseListener;
import org.amanzi.neo.core.database.services.UpdateDatabaseEvent;
import org.amanzi.neo.core.database.services.UpdateDatabaseEventType;
import org.amanzi.neo.core.utils.ActionUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CallAnalyserPlugin extends AbstractUIPlugin implements IUpdateDatabaseListener {
    private static final Collection<UpdateDatabaseEventType> handedTypes;
    static {
        Collection<UpdateDatabaseEventType> spr = new HashSet<UpdateDatabaseEventType>();
        spr.add(UpdateDatabaseEventType.GIS);
        handedTypes = Collections.unmodifiableCollection(spr);
    }
	// The plug-in ID
	public static final String PLUGIN_ID = "org.amanzi.awe.views.call";

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
        NeoCorePlugin.getDefault().getUpdateDatabaseManager().addListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
        NeoCorePlugin.getDefault().getUpdateDatabaseManager().removeListener(this);
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
                    ((CallAnalyserView)reuseView).updateView();
                }
            }
        }, true);
    }


    @Override
    public void databaseUpdated(UpdateDatabaseEvent event) {
        updateView();
    }

    @Override
    public Collection<UpdateDatabaseEventType> getType() {
        return handedTypes;
    }
}
