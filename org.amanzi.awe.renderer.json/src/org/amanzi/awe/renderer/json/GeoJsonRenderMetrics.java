package org.amanzi.awe.renderer.json;

import java.util.HashSet;
import java.util.Set;

import javax.media.jai.util.Range;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;

/**
 * Contains information of metrics on GeoJsonRenderer extension.
 * 
 * @author Milan Dinic
 * 
 */
public class GeoJsonRenderMetrics extends AbstractRenderMetrics {

    public GeoJsonRenderMetrics(IRenderContext context, IRenderMetricsFactory factory) {
        super(context, factory);
        // super(context, factory, Arrays.asList(new String[0]));
        // this.timeToDrawMetric = DRAW_DATA_RAW;
        // this.latencyMetric = LATENCY_LOCAL;
        // this.resolutionMetric = RES_PIXEL;
    }

    public final boolean canAddLayer(final ILayer layer) {
        return false;
    }

    public final boolean canStyle(final String styleID, final Object value) {
        return false;
    }

    /**
     * Creates new instance of {@link GeoJsonRenderer}.
     * 
     * @return instance of {@link GeoJsonRenderer} object
     */
    public final Renderer createRenderer() {
        return new GeoJsonRenderer();
    }

    public Set<Range> getValidScaleRanges() {
        return new HashSet<Range>();
    }
}
