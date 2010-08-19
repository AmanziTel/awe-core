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

package org.amanzi.awe.catalog.neo.upd_layers;

import java.util.HashMap;

import net.refractions.udig.project.ILayer;

import org.amanzi.awe.catalog.neo.upd_layers.events.UpdateLayerEvent;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class LayerUpdateManager {
    
    private HashMap<ILayer, UpdateLayerListener> listeners = new HashMap<ILayer, UpdateLayerListener>();
    
    /**
     * Register listener.
     *
     * @param aListener LayerUpdateListener
     */
    public void registerListener(UpdateLayerListener aListener, ILayer layer){
        listeners.put(layer, aListener);
    }
    
    /**
     * Remove listener.
     *
     * @param aListener LayerUpdateListener
     */
    public void removeListener(ILayer layer) {
        listeners.remove(layer);
    }

    public void sendUpdateMessage(final UpdateLayerEvent event) {
        SafeRunner.run(new ISafeRunnable() {
            @Override
            public void run() throws Exception {
                for(UpdateLayerListener curr : listeners.values()){
                    curr.updateLayerOnEvent(event);
                }
            }

            @Override
            public void handleException(Throwable exception) {
            }
        });
        
    }

}
