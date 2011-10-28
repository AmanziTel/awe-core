package org.amanzi.awe.catalog.neo.upd_layers;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.interceptor.LayerInterceptor;
import net.refractions.udig.project.internal.Layer;

import org.amanzi.awe.catalog.neo.NeoCatalogPlugin;
import org.amanzi.awe.models.catalog.neo.NewGeoResource;

public class LayerAddedInterceptor implements LayerInterceptor {

    @Override
    public void run(Layer layer) {
        IGeoResource resource = layer.findGeoResource(NewGeoResource.class);
        // TODO: verify
        if (resource != null) {
            NeoCatalogPlugin.getDefault().getLayerManager().registerListener(new UpdateLayerListener(layer), layer);
        }
    }

}
