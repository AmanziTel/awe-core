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

package org.amanzi.neo.nodetypes;

public class NodeTypeNotExistsException extends Exception {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 8996538121125391000L;

    private final String id;

    public NodeTypeNotExistsException(final String id) {
        this.id = id;
    }

    public String getNodeTypeId() {
        return id;
    }

}