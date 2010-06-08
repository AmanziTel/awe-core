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

package org.amanzi.awe.wizards.geoptima;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.commands.DeleteLayerCommand;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.IAnimation;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;

import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class VisualiseLayer implements MapGraphic {
//TODO check synchronize
    //TODO add listener on remove map!
    private final Set<VisualiseCommand> animation = new HashSet<VisualiseCommand>();
    private final GraphDatabaseService service=NeoServiceProvider.getProvider().getService();

    @Override
    public void draw(MapGraphicContext context) {
        ILayer datasetLayer = findDatasetLayer(context);
        if (datasetLayer == null) {
            return;
        }

        ILayer layer = context.getLayer();
        VisualiseCommand command;
        try {
            command = new VisualiseCommand(layer, datasetLayer, context);
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
        if (animation.contains(command)) {
            return;
        }
        animation.add(command);
        AnimationUpdater.runTimer(context.getMapDisplay(), command);
    }

    /**
     * @param context
     * @return
     */
    private ILayer findDatasetLayer(MapGraphicContext context) {
        ILayer result = null;
        try {
            for (ILayer layer : context.getMapLayers()) {
                if (layer.getGeoResource().canResolve(Node.class)) {
                    Node node = layer.getGeoResource().resolve(Node.class, null);
                    node = NeoUtils.findRoot(node, service);
                    if (NeoUtils.isDatasetNode(node) || NodeTypes.OSS.checkNode(node)) {
                        result = layer;
                        break;
                    }
                }
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class VisualiseCommand extends AbstractDrawCommand implements IAnimation {
        private final  ILayer datasetLayer;
        private final ILayer layer;
        private final MapGraphicContext context;
        private boolean cansel;
        private IProgressMonitor monitor=new NullProgressMonitor();
        private final Node rootNode;

        /**
         * @param layer
         * @param context
         * @throws IOException 
         */
        public VisualiseCommand(ILayer layer, ILayer datasetLayer,MapGraphicContext context) throws IOException {
            this.layer = layer;
            this.datasetLayer = datasetLayer;
            this.context = context;
             cansel=false;
             Node node = datasetLayer.getGeoResource().resolve(Node.class, null);
             rootNode = NeoUtils.findRoot(node, service);

        }



        public short getFrameInterval() {
            return 100;
        }

        public boolean hasNext() {
            List<ILayer> layers = context.getMapLayers();
            
            System.out.println(layers);
            boolean result = layers.contains(layer)&&layers.contains(datasetLayer);
            return result;
        }

        public void nextFrame() {

        }

        public void run(IProgressMonitor monitor) throws Exception {
            this.monitor = monitor;
            cansel=monitor.isCanceled();
            // only draw if layer is visible
            if( layer.isVisible() ){

    
                graphics.drawOval(0, 0, 30, 30);
            }

        }

        public Rectangle getValidArea() {
            // to be more efficient we could set the area to draw so only the
            // area affected by this animation would be updated in the map
            // display but
            // for simplicity lets just trigger the entire viewport to redraw
            // (note that this is not
            // a re-render.
            return null;
        }

        @Override
        public void dispose() {
            if (context.getMapLayers().contains(layer)){
                context.getMap().sendCommandASync( new DeleteLayerCommand( (Layer)layer ) );
            }

            System.out.println("dispose");
            animation.remove(this);
//                // we don't want to be disposed so lets run again
//                AnimationUpdater.runTimer(context.getMapDisplay(), this);
        }



        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((datasetLayer == null) ? 0 : datasetLayer.hashCode());
            result = prime * result + ((layer == null) ? 0 : layer.hashCode());
            return result;
        }



        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            VisualiseCommand other = (VisualiseCommand)obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (datasetLayer == null) {
                if (other.datasetLayer != null)
                    return false;
            } else if (!datasetLayer.equals(other.datasetLayer))
                return false;
            if (layer == null) {
                if (other.layer != null)
                    return false;
            } else if (!layer.equals(other.layer))
                return false;
            return true;
        }



        private VisualiseLayer getOuterType() {
            return VisualiseLayer.this;
        }
        

    }
}
