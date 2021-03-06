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

package org.amanzi.neo.nodeproperties.impl;

import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class GeneralNodeProperties implements IGeneralNodeProperties {

    private static final String NODE_NAME = "name";

    private static final String NODE_TYPE = "type";

    private static final String SIZE = "size";

    private static final String PARENT_ID = "parent_id";

    private static final String LAST_CHILD_ID = "last_child_id";

    @Override
    public String getNodeNameProperty() {
        return NODE_NAME;
    }

    @Override
    public String getNodeTypeProperty() {
        return NODE_TYPE;
    }

    @Override
    public String getSizeProperty() {
        return SIZE;
    }

    @Override
    public String getParentIDProperty() {
        return PARENT_ID;
    }

    @Override
    public String getLastChildID() {
        return LAST_CHILD_ID;
    }

}
