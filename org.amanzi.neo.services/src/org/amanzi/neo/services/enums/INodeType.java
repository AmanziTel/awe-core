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

package org.amanzi.neo.services.enums;

import java.io.Serializable;

import org.amanzi.neo.services.NodeTypeManager;

/**
 * <p>
 * Interface for node type The classes implementing <code>INodeType</code> must be registered in
 * <code>NodeTypeManager</code>.
 * </p>
 * 
 * 
 * Example:<br>
 * static {<br>
 *     NodeTypeManager.registerNodeType(ImplementNodeType.class);<br> 
 * }
 * 
 * @author TsAr
 * @since 1.0.0
 */
public interface INodeType extends Serializable {
    String getId();
}
