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

package org.amanzi.neo.core.database.services.events;


/**
 * Abstract event, that fire view update. 
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public abstract class UpdateViewEvent {
    
    private final UpdateViewEventType type;
    
    protected UpdateViewEvent(UpdateViewEventType aType) {
        type = aType;
    }
    
    /**
     * @return Returns the type.
     */
    public UpdateViewEventType getType() {
        return type;
    }

}
