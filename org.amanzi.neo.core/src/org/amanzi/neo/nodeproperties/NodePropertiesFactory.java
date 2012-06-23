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

package org.amanzi.neo.nodeproperties;

import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class NodePropertiesFactory {

    private static volatile NodePropertiesFactory instance;

    private volatile IGeneralNodeProperties generalNodeProperties;

    private NodePropertiesFactory() {
        // do nothing
    }

    public static NodePropertiesFactory getInstance() {
        if (instance == null) {
            synchronized (NodePropertiesFactory.class) {
                if (instance == null) {
                    instance = new NodePropertiesFactory();
                }
            }
        }

        return instance;
    }

    public IGeneralNodeProperties getGeneralNodeProperties() {
        if (generalNodeProperties == null) {
            synchronized (NodePropertiesFactory.class) {
                if (generalNodeProperties == null) {
                    generalNodeProperties = new GeneralNodeProperties();
                }
            }
        }

        return generalNodeProperties;
    }
}
