package org.amanzi.awe.catalog.neo.listeners;

import java.io.IOException;
import java.util.Set;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.IProject;
import net.refractions.udig.project.interceptor.LayerInterceptor;
import net.refractions.udig.project.internal.Layer;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NetworkElementTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.ActionUtil;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;

/**
 * <p>
 * Adding layer listener
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
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
     * @since 1.1.0
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
            Transaction tx = null;
            try {
                tx = NeoServiceProvider.getProvider().getService().beginTx();
                IGeoResource resource = layer.findGeoResource(GeoNeo.class);
                if (resource != null) {
                    GeoNeo geo = resource.resolve(GeoNeo.class, null);
                    if (geo.getGisType() == GisTypes.Network) {
                        IMap map = layer.getMap();
                        IProject project = map.getProject();
                        String aweProjectName = project.getName();
                        Set<Node> networkSet = geo.getChildByType(NetworkElementTypes.NETWORK.toString());
                        if (!networkSet.isEmpty()) {
                            NeoCorePlugin.getDefault().getProjectService().addNetworkToProject(aweProjectName,
                                    networkSet.iterator().next());
                        }
                    }
                }
            } catch (IOException e) {
                // TODO Handle IOException
                throw (RuntimeException)new RuntimeException().initCause(e);
            } finally {
                tx.finish();
            }
        }

    }
}
