package org.amanzi.awe.mapgraphic;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.util.List;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IMap;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import org.amanzi.awe.tool.coordinate.CoordinateTool;

import com.vividsolutions.jts.geom.Coordinate;

public class CoordinateGraphic implements MapGraphic {

    public void draw( MapGraphicContext context ) {
        //initialize the graphics handle
        ViewportGraphics g = context.getGraphics();
        g.setColor(Color.RED);
        g.setStroke(ViewportGraphics.LINE_SOLID, 2);

        //get the map blackboard
        IBlackboard blackboard = context.getLayer().getMap().getBlackboard();

        List<Coordinate> coordinates = (List<Coordinate>) blackboard.get(CoordinateTool.BLACKBOARD_KEY);

        if (coordinates == null) {
            return; //no coordinates to draw
        }

        //for each coordinate, create a circle and draw
        for( Coordinate coordinate : coordinates ) {
            //This code works for pan/zoom, but not re-projection.
            Point point = context.worldToPixel(coordinate);
            Ellipse2D e = new Ellipse2D.Double(point.x - 4, point.y - 4, 9, 9);
            g.draw(e);
        }
    }
}
