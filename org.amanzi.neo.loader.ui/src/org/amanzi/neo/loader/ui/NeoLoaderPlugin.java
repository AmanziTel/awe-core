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

package org.amanzi.neo.loader.ui;

import org.amanzi.neo.loader.core.preferences.PreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * <p>
 * NeoLoaderPlugin
 * </p>
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public class NeoLoaderPlugin extends AbstractUIPlugin {

    /*
     * Plugin variable
     */

    static private NeoLoaderPlugin plugin;
    private IPropertyChangeListener propertyListener;

    /**
     * Constructor for SplashPlugin.
     */
    public NeoLoaderPlugin() {
        super();
        plugin = this;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        propertyListener = new IPropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                PreferenceStore.getPreferenceStore().changeProperty(event.getProperty(), event.getNewValue(), event.getOldValue());
            }
        };
        getPreferenceStore().addPropertyChangeListener(propertyListener);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        getPreferenceStore().removePropertyChangeListener(propertyListener);
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     */
    public static NeoLoaderPlugin getDefault() {
        return plugin;
    }
}
