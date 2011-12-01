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

package org.amanzi.neo.services.ui.neoclipse.manager;

import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.apache.log4j.Logger;
import org.eclipse.ui.PlatformUI;
import org.neo4j.neoclipse.reltype.RelationshipTypeView;
import org.neo4j.neoclipse.view.NeoGraphViewPart;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * manage neoclipse view
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class NeoclipseViewerManager {
    private static final Logger LOGGER = Logger.getLogger(NeoclipseViewerManager.class);
    /**
     * NeoclipseViewerManager instance
     */
    private static NeoclipseViewerManager manager;
    /**
     * neoclipse view instance
     */
    private NeoGraphViewPart neoGraphView;

    /**
     * return manager instance
     * 
     * @return manager instance
     */
    public static NeoclipseViewerManager getInstance() {
        if (manager == null) {
            manager = new NeoclipseViewerManager();

        }
        return manager;
    }

    /**
     * initialize views
     */
    private NeoclipseViewerManager() {
        try {
            // intialize relationships view
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(RelationshipTypeView.ID);
            // initialize neoclipse view
            neoGraphView = (NeoGraphViewPart)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .findView(NeoGraphViewPart.ID);
        } catch (Exception e) {
            LOGGER.error("Cann't initialize views because:", e);
        }
    }

    /**
     * refresh neoclipse view
     */
    public void refreshNeoeclipseView() {
        try {
            neoGraphView.refresh();
        } catch (Exception e) {
            LOGGER.error("Cann't refresh view because:", e);
        }
    }

    /**
     * show required element which based on Node in database
     * 
     * @param element
     */
    public void showInDatabase(IDataElement element) {
        Node nodeToShow = ((DataElement)element).getNode();
        if (nodeToShow == null) {
            LOGGER.error("There is no node in element " + element);
        }
        try {
            neoGraphView.showNode(nodeToShow);
        } catch (Exception e) {
            LOGGER.error("Cann't show element on view because:", e);
        }
    }
}
