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

package org.amanzi.awe.catalog.neo.upd_layers.events;

import org.amanzi.awe.ui.IGraphModel;
import org.neo4j.graphdb.Node;

/**
 * <p>
 *Change Graph Model
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class ChangeModelEvent extends UpdateLayerEvent {

    private final IGraphModel model;

    /**
     * @param aType
     * @param aGisNode
     */
    public ChangeModelEvent(Node aGisNode, IGraphModel model) {
        super(UpdateLayerEventTypes.CHANGE_GRAPH_MODEL, aGisNode);
        this.model = model;
    }

    public IGraphModel getModel() {
        return model;
    }

}
