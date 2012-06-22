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

import java.util.HashMap;
import java.util.Map;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NodePropertiesFactory {

    private static NodePropertiesFactory instance;

    private Map<Class<INodeProperties>, INodeProperties> propertiesMap = new HashMap<Class<INodeProperties>, INodeProperties>();

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

    @SuppressWarnings("unchecked")
    public <T1 extends INodeProperties> T1 getNodeProperties(Class<T1> propertiesClass) {
        return (T1)propertiesMap.get(propertiesClass);
    }

    @SuppressWarnings("unchecked")
    synchronized <T1 extends INodeProperties> void registerNodeProperties(Class<T1> propertiesClass, T1 instance) throws Exception {
        if (!propertiesMap.containsKey(propertiesClass)) {
            propertiesMap.put((Class<INodeProperties>)propertiesClass, instance);
        }
    }
}
