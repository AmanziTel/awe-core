package org.amanzi.awe.render.network;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.RenderException;

//import org.amanzi.awe.neo.views.network.utils.ITreeSelectionChanged;
//import org.amanzi.awe.neo.views.network.views.NetworkTreeView;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.opengis.referencing.operation.MathTransform;

public class NeoRenderer extends RendererImpl //implements ITreeSelectionChanged
{
	    private static final Color SELECTION_COLOR = Color.RED;
	    private AffineTransform base_transform = null; // save original graphics transform for repeated
	    // re-use
	    private Color drawColor = Color.DARK_GRAY;
	    private Color fillColor = new Color(120, 255, 170);
	    private MathTransform transform_d2w;
	    private MathTransform transform_w2d;
	    private List<String> selectedTreeItems = new ArrayList<String>();
	    

	public NeoRenderer() 
	{
        Display display = PlatformUI.getWorkbench().getDisplay();
        //TODO: Re-enable when the cyclic dependencies are resolved
        /*
        display.syncExec(new Runnable(){

            public void run() 
            {
                final IWorkbenchWindow window = PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow();
                try 
                {
                    final NetworkTreeView viewPart = (NetworkTreeView) window.getActivePage()
                            .showView(NetworkTreeView.NETWORK_VIEW_ID);
                    viewPart.addChangeListeners(NeoRenderer.this);
                } 
                catch (Exception e) 
                {
                    e.printStackTrace();
                }
            }
        });
        */
    }

	

	@Override
	public void render(Graphics2D destination, IProgressMonitor monitor)
			throws RenderException {
		// TODO Auto-generated method stub
		
	}

	
	 private void renderSector( final Graphics2D g, final java.awt.Point p, final double azimuth,
	            final double beamwidth ) {
	        renderSector(g, p, azimuth, beamwidth, false);
	    }

	
	 private void renderSector( final Graphics2D g, final java.awt.Point p, final double azimuth,
	            double beamwidth, final boolean selected ) {
	        if (base_transform == null) {
	            base_transform = g.getTransform();
	        }
	        if (beamwidth < 10) {
	            beamwidth = 10;
	        }

	        g.setTransform(base_transform);
	        g.translate(p.x, p.y);
	        g.rotate(Math.toRadians(-90 + azimuth - beamwidth / 2.0));
	        g.setColor(fillColor);
	        if (selected) {
	            g.setColor(SELECTION_COLOR);
	        }

	        g.fillArc(-20, -20, 40, 40, 0, -(int) beamwidth);
	        g.setColor(drawColor);

	        g.drawArc(-20, -20, 40, 40, 0, -(int) beamwidth);
	        g.drawLine(0, 0, 20, 0);
	        g.rotate(Math.toRadians(beamwidth));
	        g.drawLine(0, 0, 20, 0);

	        g.setColor(drawColor);
	    }
	
	 
	 /**
	     * This one is very simple, just draw a circle at the site location.
	     * 
	     * @param g
	     * @param p
	     */
	    private void renderSite( final Graphics2D g, final java.awt.Point p )
	    {
	        g.fillOval(p.x - 5, p.y - 5, 10, 10);
	    }

	    /**
	     * Draws a red circle round the point that represents selection of site.
	     * 
	     * @param g {@link Graphics2D} object
	     * @param p {@link java.awt.Point} object
	     * @param labelString
	     */
	    private void renderSelector( final Graphics2D g, final java.awt.Point p, final boolean selected ) 
	    {
	        if (selected) {
	            g.setColor(SELECTION_COLOR);
	            g.drawOval(p.x - 10, p.y - 10, 20, 20);
	            g.setColor(drawColor);
	        }
	    }
	    
	    @Override
	    public final void render( final IProgressMonitor monitor ) throws RenderException {

	        final Graphics2D g = getContext().getImage().createGraphics();
	        render(g, monitor);
	    }

	    public void update( List<String> selectedTreeItems ) {
	        this.selectedTreeItems = selectedTreeItems;
	        setState(RENDER_REQUEST);
	    }
	    
	    
}
