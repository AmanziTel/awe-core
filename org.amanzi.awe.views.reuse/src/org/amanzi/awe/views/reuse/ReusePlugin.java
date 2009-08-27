package org.amanzi.awe.views.reuse;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.amanzi.awe.views.reuse.views.ReuseAnalyserView;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.listener.IUpdateBDListener;
import org.amanzi.neo.core.database.services.UpdateDatabaseEvent;
import org.amanzi.neo.core.database.services.UpdateDatabaseEventType;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.ActionUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.neo4j.api.core.Transaction;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ReusePlugin extends AbstractUIPlugin implements IUpdateBDListener {
    private static final Collection<UpdateDatabaseEventType> handedTypes;
    static {
        Collection<UpdateDatabaseEventType> spr = new HashSet<UpdateDatabaseEventType>();
        spr.add(UpdateDatabaseEventType.GIS);
        handedTypes = Collections.unmodifiableCollection(spr);
    }
    /** String VIEW_ID field */
    private static final String VIEW_ID = "org.amanzi.awe.views.reuse.views.ReuseAnalyserView";

    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.awe.views.reuse";

	// The shared instance
	private static ReusePlugin plugin;

    private Transaction currentTransaction;
	
	/**
	 * The constructor
	 */
	public ReusePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
        currentTransaction = NeoServiceProvider.getProvider().getService().beginTx();
        NeoCorePlugin.getDefault().getUpdateDatabaseManager().addListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
        currentTransaction.finish();
        NeoCorePlugin.getDefault().getUpdateDatabaseManager().removeListener(this);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ReusePlugin getDefault() {
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
     *
     */
    private void updateView() {
        ActionUtil.getInstance().runTask(new Runnable() {

            @Override
            public void run() {
                IViewPart reuseView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(VIEW_ID);
                if (reuseView != null) {
                    ((ReuseAnalyserView)reuseView).updateGisNode();
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
