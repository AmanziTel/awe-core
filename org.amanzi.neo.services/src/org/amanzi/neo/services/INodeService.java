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

package org.amanzi.neo.services;

import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.internal.IService;
import org.amanzi.neo.services.nodetypes.INodeType;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface INodeService extends IService {

    /**
     * Returns a Name property of Node
     * 
     * @param node
     * @return
     * @throws ServiceException in case of Name Property not found
     */
    public String getNodeName(Node node) throws ServiceException;

    /**
     * Returns a NodeType property of Node
     * 
     * @param node
     * @return
     * @throws ServiceException in case of Type Property not found
     */
    public INodeType getNodeType(Node node) throws ServiceException;

}
