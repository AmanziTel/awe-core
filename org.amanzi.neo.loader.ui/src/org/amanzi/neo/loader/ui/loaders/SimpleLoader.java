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

package org.amanzi.neo.loader.ui.loaders;

import org.amanzi.neo.loader.core.parser.IConfigurationData;
import org.amanzi.neo.loader.core.parser.IDataElement;
import org.amanzi.neo.services.events.UpdateViewEventType;

/**
 * <p>
 * Simple loader implementation
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class SimpleLoader<T extends IDataElement, T2 extends IConfigurationData> extends Loader<T, T2> {
    @Override
    protected void finishup() {
        sendUpdateEvent(UpdateViewEventType.GIS);
    }
}
