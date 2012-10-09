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
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.TraversalDescription;

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

    Node getParent(Node child, RelationshipType relationshipType) throws ServiceException;

    Iterator<Node> getChildren(Node parentNode, INodeType nodeType) throws ServiceException;

    Iterator<Node> getChildren(Node parentNode, INodeType nodeType, RelationshipType relationshipType) throws ServiceException;

    Node getChildInChainByName(Node parentNode, String name, INodeType nodeType) throws ServiceException;

    Node getChildByName(Node parentNode, String name, INodeType nodeType) throws ServiceException;

    Node getChildByName(Node parentNode, String name, INodeType nodeType, RelationshipType relationshipType)
            throws ServiceException;

    Node getSingleChild(Node parentNode, INodeType nodeType, RelationshipType relationshipType) throws ServiceException;

    Node getReferencedNode() throws ServiceException;

    Node createNode(Node parentNode, INodeType nodeType, RelationshipType relationshipType) throws ServiceException;

    Node createNodeInChain(Node parentNode, INodeType nodeType) throws ServiceException;

    Node createNode(Node parentNode, INodeType nodeType, RelationshipType relationshipType, String name) throws ServiceException;

    Node createNodeInChain(Node parentNode, INodeType nodeType, String name) throws ServiceException;

    Node createNode(Node parentNode, INodeType nodeType, RelationshipType relationshipType, String name,
            Map<String, Object> parameters) throws ServiceException;

    Node createNodeInChain(Node parentNode, INodeType nodeType, String name, Map<String, Object> parameters)
            throws ServiceException;

    Node createNode(Node parentNode, INodeType nodeType, RelationshipType relationshipType, Map<String, Object> parameters)
            throws ServiceException;

    Node createNodeInChain(Node parentNode, INodeType nodeType, Map<String, Object> parameters) throws ServiceException;

    void updateProperty(Node node, String propertyName, Object newValue) throws ServiceException;

    void updateProperty(Relationship relationship, String propertyName, Object newValue) throws ServiceException;

    <T extends Object> T getNodeProperty(final Node node, final String propertyName, final T defaultValue,
            final boolean throwExceptionIfNotExist) throws ServiceException;

    <T extends Object> T getRelationshipProperty(final Relationship relationship, final String propertyName, final T defaultValue,
            final boolean throwExceptionIfNotExist) throws ServiceException;

    void removeNodeProperty(Node node, String propertyName, boolean throwExceptionIfNotExist) throws ServiceException;

    void renameNodeProperty(Node node, String oldPropertyName, String newPropertyName, boolean throwExceptionIfNotExist)
            throws ServiceException;

    /**
     * get all children from parent node with {@link RelationshipType}
     * 
     * @param parentNode
     * @param relationshipType
     * @return
     * @throws ServiceException
     */
    Iterator<Node> getChildren(Node parentNode, RelationshipType relationshipType) throws ServiceException;

    /**
     * return all children from parent node with CHILD {@link RelationshipType}
     * 
     * @param parentNode
     * @return
     * @throws ServiceException
     */
    Iterator<Node> getChildren(Node parentNode) throws ServiceException;

    Iterator<Node> getChildrenChain(Node parentNode) throws ServiceException;

    Iterator<Node> getChildrenChain(Node parentNode, INodeType nodeType) throws ServiceException;

    Relationship linkNodes(Node startNode, Node endNode, RelationshipType relationshipType) throws ServiceException;

    TraversalDescription getChildrenTraversal(final INodeType nodeType, final RelationshipType relationshipType);

    Node getChainParent(Node node) throws ServiceException;

    TraversalDescription getChildrenChainTraversal(final Node node);

    void deleteChain(Node node) throws ServiceException;

    void deleteSingleNode(Node node) throws ServiceException;

    Iterator<Node> getAllChildren(Node node) throws ServiceException;

    Relationship findLinkBetweenNodes(Node startNode, Node endNode, RelationshipType relationshipType, Direction direction)
            throws ServiceException;

    void deleteRelationship(Relationship relation) throws ServiceException;
}
