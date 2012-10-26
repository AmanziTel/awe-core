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

package org.amanzi.awe.ui.events.impl.internal;

import org.amanzi.awe.ui.events.EventStatus;
import org.amanzi.awe.ui.events.IEvent;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractEvent implements IEvent {

    private final EventStatus status;

    private final boolean isAsync;

    private final Object source;

    private boolean isStopped = false;

    protected AbstractEvent(final EventStatus status, final boolean isAsync, final Object source) {
        this.status = status;
        this.isAsync = isAsync;
        this.source = source;
    }

    @Override
    public EventStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof IEvent) {
            return ((IEvent)o).getStatus().equals(status);
        }

        return false;
    }

    @Override
    public Object getSource() {
        return source;
    }

    @Override
    public int hashCode() {
        return status.name().hashCode();
    }

    @Override
    public boolean isAsync() {
        return isAsync;
    }

    @Override
    public void stop() {
        isStopped = true;
    }

    @Override
    public boolean isStopped() {
        return isStopped;
    }

    @Override
    public String toString() {
        return status.name();
    }

}
