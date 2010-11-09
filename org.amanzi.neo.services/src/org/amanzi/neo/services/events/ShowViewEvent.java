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

package org.amanzi.neo.services.events;


/**
 * Simple show view event.
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class ShowViewEvent extends UpdateViewEvent {

    private String updatedView;
    
    /**
     * Constructor.
     * @param aView
     */
    public ShowViewEvent(String aView) {
        super(UpdateViewEventType.SHOW_VIEW);
        updatedView = aView;
    }
    
    /**
     * Constructor for subclasses.
     * @param aView String
     * @param aType UpdateViewEventType
     */
    protected ShowViewEvent(String aView, UpdateViewEventType aType) {
        super(aType);
        updatedView = aView;
    }

    /**
     * Check view for update
     *
     * @param viewId String
     * @return boolean
     */
    public boolean isViewNeedUpdate(String viewId){
        return updatedView.equals(viewId);
    }
    
    /**
     * @return Returns the updatedView.
     */
    public String getUpdatedView() {
        return updatedView;
    }
    
}
