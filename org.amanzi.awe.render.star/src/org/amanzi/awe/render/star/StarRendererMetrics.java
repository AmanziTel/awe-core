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

package org.amanzi.awe.render.star;

import java.util.HashSet;
import java.util.Set;

import javax.media.jai.util.Range;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class StarRendererMetrics extends AbstractRenderMetrics {

    /**
     * @param context
     * @param factory
     */
    public StarRendererMetrics(IRenderContext context, IRenderMetricsFactory factory) {
        super(context, factory);
    }

    @Override
    public boolean canAddLayer(ILayer layer) {
        return false;
    }

    @Override
    public boolean canStyle(String styleID, Object value) {
        return false;
    }

    @Override
    public Renderer createRenderer() {
        return new StarRenderer();
    }

    @Override
    public Set<Range> getValidScaleRanges() {
        return new HashSet<Range>();
    }



}
