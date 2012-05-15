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

package org.amanzi.neo.services.ui.events;

/**
 * <p>
 * UPDATE_DATA, SHOW_ON_MAP, CHANGE_PROJECT, DRILL_DOWN, ANALYSE, SHOW_IND_GRAPH_DB, UPDATE_LAYER
 * events listener common interface
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public interface IEventsListener<T extends AbstractEvent> {
    /**
     * handle required event
     */
    public void handleEvent(T data);

    /**
     * get source for validate fireing
     * 
     * @return
     */
    public Object getSource();
}
