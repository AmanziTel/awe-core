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

package org.amanzi.awe.views.network.view;

import java.util.ArrayList;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 * Action that perform choosing loaded network selections.
 * </p>
 * 
 * @author ZaKoN
 * @since 1.0.0
 */
public class SetSelectionAction extends Action {

    private final Relationship selectionRel;
    private final TreeViewer networkTreeViewer;

    /**
     * Instantiates a new export network action.
     * 
     * @param selectionRel the selection
     * @param viewer
     */
    public SetSelectionAction(Relationship selectionRel, TreeViewer viewer) {
        this.selectionRel = selectionRel;
        this.networkTreeViewer = viewer;
        DatasetService ds = NeoServiceFactory.getInstance().getDatasetService();
        Node selectionNode = selectionRel.getEndNode();
        setText(ds.getNodeName(selectionNode));

        setEnabled(selectionNode.getRelationships(NetworkRelationshipTypes.SELECTED, Direction.OUTGOING).iterator().hasNext());
    }

    @Override
    public void run() {
        ArrayList<NeoNode> selectedNodes = new ArrayList<NeoNode>();
        for (Relationship rel : selectionRel.getEndNode().getRelationships(NetworkRelationshipTypes.SELECTED, Direction.OUTGOING)) {
            selectedNodes.add(new NeoNode(rel.getEndNode(), 0));
        }
        networkTreeViewer.setSelection(new StructuredSelection(selectedNodes.toArray(new NeoNode[0])), false);
        for (NeoNode node : selectedNodes) {
            networkTreeViewer.reveal(node);
        }
    }

}
