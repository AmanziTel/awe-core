package org.amanzi.awe.render.sites;

import java.util.HashSet;
import java.util.Set;

import javax.media.jai.util.Range;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;

public class SitesRenderMetrics extends AbstractRenderMetrics {
    /**
     * Construct based on context (which includes map, layer and geoResource).
     * @param context
     * @param factory
     */
    public SitesRenderMetrics( IRenderContext context, IRenderMetricsFactory factory ) {
        super(context, factory);
    }

    /**
     * We only support rendering a single layer
     * @return false
     */
    public boolean canAddLayer( ILayer layer ) {
        return false;
    }

    /**
     * We cannot use styles, this is a raw Java2D renderer
     */
    public boolean canStyle( String styleID, Object value ) {
        return false;
    }

    /**
     * @return a new SitesRenderer
     */
    public Renderer createRenderer() {
        return new SitesRenderer();
    }

    /**
     * @return a new empty HashSet<Range>
     */
    public Set<Range> getValidScaleRanges() {
        return new HashSet<Range>();
    }
}
