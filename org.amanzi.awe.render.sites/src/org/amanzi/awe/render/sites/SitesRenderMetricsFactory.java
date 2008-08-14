package org.amanzi.awe.render.sites;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetrics;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;

import org.amanzi.awe.catalog.csv.CSV;
import org.amanzi.awe.catalog.json.JSONReader;

public class SitesRenderMetricsFactory implements IRenderMetricsFactory {

    /**
     * This metrics factory supports both the amanzi CSV and JSON (Geo-JSON)
     * data sources (IGeoResources). See the org.amanzi.awe.catalog.* packages.
     * Here we return true of the IGeoResource passed in the context canResolve
     * the CVS.class and JSONReader.class from the two amanzi catalog packages.
     * 
     * @see net.refractions.udig.project.render.IRenderMetricsFactory#canRender(net.refractions.udig.project.render.IRenderContext)
     */
    public boolean canRender( IRenderContext context ) throws IOException {
        for(IGeoResource resource : context.getLayer().getGeoResources()){
            if(resource.canResolve(CSV.class)){
                return true;
            }
            if(resource.canResolve(JSONReader.class)){
                return true;
            }
        }
        return false;
    }

    /**
     * @return a SitesRenderMetrics constructed on this context
     */
    public IRenderMetrics createMetrics( IRenderContext context ) {
        return new SitesRenderMetrics(context, this);
    }

    /**
     * @return SitesRenderer.class
     */
    public Class< ? extends IRenderer> getRendererType() {
        return SitesRenderer.class;
    }

}
