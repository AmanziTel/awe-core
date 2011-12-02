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

import org.amanzi.neo.services.ui.enums.EventsType;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.IEventsListener;
import org.amanzi.neo.services.ui.events.UpdateDataEvent;
import org.apache.log4j.Logger;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.neo4j.neoclipse.view.NeoGraphViewPart;

/**
 * <p>
 * manage neoclipse view
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class NeoclipseListenerManager {
    private static final Logger LOGGER = Logger.getLogger(NeoclipseListenerManager.class);
    private final static String REL_TYPE__VIEW_ID = "org.neo4j.neoclipse.reltype.RelationshipTypeView";
    /**
     * NeoclipseViewerManager instance
     */
    private static NeoclipseListenerManager manager;
    /**
     * neoclipse view instance
     */
    private NeoGraphViewPart neoGraphView;
    /**
     * events manager
     */
    private static EventManager eventManager;
    /**
     * instance of relationships view
     */
    private IViewPart relationshipView;

    /**
     * return manager instance
     * 
     * @return manager instance
     */
    public static void initialiseNeoclipseListeners(EventManager eventManager2) {
        if (manager == null) {
            eventManager = eventManager2;
            manager = new NeoclipseListenerManager();
        }
    }

    /**
     * registrate neoclipse views. and add required listeners
     */
    @SuppressWarnings("unchecked")
    private NeoclipseListenerManager() {
        try {
            eventManager.addListener(EventsType.UPDATE_DATA, new RefreshNeoclipseView());
        } catch (Exception e) {
            LOGGER.info("Cann't initialize views because:", e);
            e.printStackTrace();
        }
    }

    /**
     * get relationships view instance
     */
    private void initRelationshipView() {
        if (relationshipView == null) {
            // intialize relationships view
            relationshipView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(REL_TYPE__VIEW_ID);
        }
    }

    private NeoGraphViewPart getNeoGraphView() {
        initRelationshipView();
        if (neoGraphView == null) {
            // initialize neoclipse view
            neoGraphView = (NeoGraphViewPart)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .findView(NeoGraphViewPart.ID);
        }
        return neoGraphView;
    }

    /**
     * <p>
     * listener to refresh neoclipse view
     * </p>
     * 
     * @author Vladislav_Kondratenko
     * @since 1.0.0
     */
    private class RefreshNeoclipseView implements IEventsListener<UpdateDataEvent> {

        @Override
        public void handleEvent(UpdateDataEvent data) {
            refreshNeoclipseView();
        }

        @Override
        public Object getSource() {
            return null;
        }

    }

    /**
     * refresh neoclipse view
     */
    private void refreshNeoclipseView() {
        try {
            getNeoGraphView().refresh();
        } catch (Exception e) {
            LOGGER.error("Cann't refresh view because:", e);
        }
    }
}
