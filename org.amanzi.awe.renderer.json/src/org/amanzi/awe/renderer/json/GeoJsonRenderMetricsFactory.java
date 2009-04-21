package org.amanzi.awe.renderer.json;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;

import org.amanzi.awe.catalog.json.JSONReader;

/**
 * Factory class for {@link GeoJsonRenderMetrics} object.
 * 
 * @author Milan Dinic
 * 
 */
public class GeoJsonRenderMetricsFactory implements IRenderMetricsFactory {
    /**
     * Provides information can this renderer render a resource.
     * 
     * @param context
     *                reference to implementation of {@link IRenderContext}
     *                interface
     * @return success indicator, true if rendered can render and false if not
     * @exception IOException
     */
    public final boolean canRender(final IRenderContext context) throws IOException {
        for (IGeoResource resource : context.getLayer().getGeoResources()) {
            if (resource.canResolve(JSONReader.class) && !resource.resolve(JSONReader.class, null).isNetworkGeoJSON()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates new instance of {@link GeoJsonRenderMetrics}.
     * 
     * @param context
     *                reference to implementation of {@link IRenderContext}
     *                interface
     * @return instance of {@link GeoJsonRenderMetrics} object
     * 
     */
    public final AbstractRenderMetrics createMetrics(final IRenderContext context) {
        return new GeoJsonRenderMetrics(context, this);
    }

    public final Class<? extends IRenderer> getRendererType() {
        return GeoJsonRenderer.class;
    }

}
