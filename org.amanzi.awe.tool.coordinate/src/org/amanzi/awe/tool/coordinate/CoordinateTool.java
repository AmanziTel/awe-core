package org.amanzi.awe.tool.coordinate;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.commands.draw.DrawShapeCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.AbstractModalTool;

import com.vividsolutions.jts.geom.Coordinate;

public class CoordinateTool extends AbstractModalTool  {
    
    public static final String BLACKBOARD_KEY = "org.amanzi.awe.tool.coordinate"; //$NON-NLS-1$
    private DrawShapeCommand command;

    public CoordinateTool() {
        super(MOUSE);
    }

    /**
     * When the mouse is pressed, store the point in world coordinates and also
     * draw a little rectangle where the mouse pointer was, remembering the draw
     * command so that it can be removed on mouse release.
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mousePressed(net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void mousePressed( MapMouseEvent e ) {

        // throw a coordinate onto the current map blackboard
        IMap map = ApplicationGIS.getActiveMap();
        if (map == null) {
            return;
        }

        IBlackboard blackboard = map.getBlackboard();
        List<Coordinate> points = (List<Coordinate>) blackboard.get(BLACKBOARD_KEY);
        if (points == null) {
            points = new ArrayList<Coordinate>();
            blackboard.put(BLACKBOARD_KEY, points);
        }

        //This code works for pan/zoom, but not re-projection.
        points.add(getContext().pixelToWorld(e.x, e.y));

        Rectangle2D r = new Rectangle2D.Double(e.x-1, e.y-1, 3, 3);
        command = getContext().getDrawFactory().createDrawShapeCommand(r,Color.GRAY);

        getContext().sendASyncCommand(command);
        getContext().getSelectedLayer().refresh(null);
    }
    
    /**
     * Remove any temporary drawn rectangle.
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseReleased(net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent)
     */
    @Override
    public void mouseReleased( MapMouseEvent e ) {
        super.mouseReleased(e);
        if (command != null) {
            command.setValid(false);
            command = null;
        }
    }

}
