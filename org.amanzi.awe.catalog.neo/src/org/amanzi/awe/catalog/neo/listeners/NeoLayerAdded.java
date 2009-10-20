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
package org.amanzi.awe.catalog.neo.listeners;

import net.refractions.udig.project.interceptor.LayerInterceptor;
import net.refractions.udig.project.internal.Layer;

import org.amanzi.neo.core.utils.ActionUtil;

/**
 * <p>
 * Adding layer listener
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class NeoLayerAdded implements LayerInterceptor {

    @Override
    public void run(Layer layer) {
        // run in main thread
        ActionUtil.getInstance().runTask(new NeoRunner(layer), true);
    }

    /**
     * <p>
     * wrapper - adding layer in Runnable
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private static class NeoRunner implements Runnable {
        private final Layer layer;

        /**
         * @param layer
         */
        public NeoRunner(Layer layer) {
            super();
            this.layer = layer;
        }

        @Override
        public void run() {
            // TODO remove comment code if link with new project is not necessary
            // Transaction tx = null;
            // try {
            // tx = NeoServiceProvider.getProvider().getService().beginTx();
            // IGeoResource resource = layer.findGeoResource(GeoNeo.class);
            // if (resource != null) {
            // GeoNeo geo = resource.resolve(GeoNeo.class, null);
            // if (geo.getGisType() == GisTypes.Network) {
            // IMap map = layer.getMap();
            // IProject project = map.getProject();
            // String aweProjectName = project.getName();
            // Set<Node> networkSet = geo.getChildByType(NetworkElementTypes.NETWORK.toString());
            // if (!networkSet.isEmpty()) {
            // NeoCorePlugin.getDefault().getProjectService().addNetworkToProject(aweProjectName,
            // networkSet.iterator().next());
            // }
            // }
            // }
            // } catch (IOException e) {
            // // TODO Handle IOException
            // throw (RuntimeException)new RuntimeException().initCause(e);
            // } finally {
            // tx.finish();
            // }
        }

    }
}
