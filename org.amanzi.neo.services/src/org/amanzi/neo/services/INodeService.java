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

import java.util.Iterator;
import java.util.Map;

import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeNotExistsException;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.internal.IService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

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
    String getNodeName(Node node) throws ServiceException;

    /**
     * Returns a NodeType property of Node
     * 
     * @param node
     * @return
     * @throws ServiceException in case of Type Property not found
     */
    INodeType getNodeType(Node node) throws ServiceException, NodeTypeNotExistsException;

    /**
     * Returns a Paret of Node Parent is a Node that stand on higher hierarchy level for provided
     * node Searching for a Parent based on 'parent' property of a Child Node containing ID of
     * Parent Node
     * 
     * @param child
     * @return
     * @throws ServiceException in case 'parent' property node found
     * @deprecated
     */
    @Deprecated
    Node getParent(Node child) throws ServiceException;

    Node getParent(Node child, RelationshipType relationshipType) throws ServiceException;

    Iterator<Node> getChildren(Node parentNode, INodeType nodeType) throws ServiceException;

    Iterator<Node> getChildren(Node parentNode, INodeType nodeType, RelationshipType relationshipType) throws ServiceException;

    Node getChildByName(Node parentNode, String name, INodeType nodeType) throws ServiceException;

    Node getChildByName(Node parentNode, String name, INodeType nodeType, RelationshipType relationshipType)
            throws ServiceException;

    Node getSingleChild(Node parentNode, INodeType nodeType, RelationshipType relationshipType) throws ServiceException;

    Node getReferencedNode() throws ServiceException;

    Node createNode(Node parentNode, INodeType nodeType, RelationshipType relationshipType) throws ServiceException;

    Node createNode(Node parentNode, INodeType nodeType, RelationshipType relationshipType, String name) throws ServiceException;

    Node createNode(Node parentNode, INodeType nodeType, RelationshipType relationshipType, String name,
            Map<String, Object> parameters) throws ServiceException;

    Node createNode(Node parentNode, INodeType nodeType, RelationshipType relationshipType, Map<String, Object> parameters)
            throws ServiceException;

    void updateProperty(Node node, String propertyName, Object newValue) throws ServiceException;

    <T extends Object> T getNodeProperty(final Node node, final String propertyName, final T defaultValue,
            final boolean throwExceptionIfNotExist) throws ServiceException;

    void removeNodeProperty(Node node, String propertyName, boolean throwExceptionIfNotExist) throws ServiceException;

    void renameNodeProperty(Node node, String oldPropertyName, String newPropertyName, boolean throwExceptionIfNotExist)
            throws ServiceException;
}
