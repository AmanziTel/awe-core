package org.amanzi.awe.statistics.view;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.amanzi.neo.services.events.UpdateViewEvent;
import org.amanzi.neo.services.events.UpdateViewEventType;
import org.amanzi.neo.services.ui.IUpdateViewListener;
import org.amanzi.neo.services.ui.NeoServicesUiPlugin;
import org.amanzi.neo.services.ui.utils.ActionUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class StatisticsViewPlugin extends AbstractUIPlugin implements IUpdateViewListener {
    private static final Collection<UpdateViewEventType> handedTypes;
    static {
        Collection<UpdateViewEventType> spr = new HashSet<UpdateViewEventType>();
        spr.add(UpdateViewEventType.GIS);
        handedTypes = Collections.unmodifiableCollection(spr);
    }
    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.awe.statistics.view";

    // The shared instance
    private static StatisticsViewPlugin plugin;

    /**
     * The constructor
     */
    public StatisticsViewPlugin() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        NeoServicesUiPlugin.getDefault().getUpdateViewManager().addListener(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        NeoServicesUiPlugin.getDefault().getUpdateViewManager().removeListener(this);
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static StatisticsViewPlugin getDefault() {
        return plugin;
    }

    public static ImageDescriptor getImageDescriptor(String imageFilePath) {
        return imageDescriptorFromPlugin(PLUGIN_ID, imageFilePath);
    }

    @Override
    public Collection<UpdateViewEventType> getType() {
        return handedTypes;
    }

    @Override
    public void updateView(UpdateViewEvent event) {
        ActionUtil.getInstance().runTask(new Runnable() {

            @Override
            public void run() {
                final IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
                        StatisticsView.ID);
                if (view != null) {
                    ((StatisticsView)view).fireDatasetLoaded();
                }
            }
        }, true);
    }

}
