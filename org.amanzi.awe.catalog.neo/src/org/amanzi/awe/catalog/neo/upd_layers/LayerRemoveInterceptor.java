package org.amanzi.awe.catalog.neo.upd_layers;

import org.amanzi.awe.catalog.neo.NeoCatalogPlugin;

import net.refractions.udig.project.interceptor.LayerInterceptor;
import net.refractions.udig.project.internal.Layer;

public class LayerRemoveInterceptor implements LayerInterceptor {

    @Override
    public void run(Layer layer) {
        NeoCatalogPlugin.getDefault().getLayerManager().removeListener(layer);
    }

}
