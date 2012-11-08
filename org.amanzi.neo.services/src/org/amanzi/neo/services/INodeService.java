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
// TODO: LN: 10.10.2012, add comments
public interface INodeService extends IService {

    Node createNode(Node parentNode, INodeType nodeType, RelationshipType relationshipType) throws ServiceException;

    Node createNode(Node parentNode, INodeType nodeType, RelationshipType relationshipType, Map<String, Object> parameters)
            throws ServiceException;

    Node createNode(Node parentNode, INodeType nodeType, RelationshipType relationshipType, String name) throws ServiceException;

    Node createNode(Node parentNode, INodeType nodeType, RelationshipType relationshipType, String name,
            Map<String, Object> parameters) throws ServiceException;

    Node createNodeInChain(Node parentNode, INodeType nodeType) throws ServiceException;

    Node createNodeInChain(Node parentNode, INodeType nodeType, Map<String, Object> parameters) throws ServiceException;

    Node createNodeInChain(Node parentNode, INodeType nodeType, String name) throws ServiceException;

    Node createNodeInChain(Node parentNode, INodeType nodeType, String name, Map<String, Object> parameters)
            throws ServiceException;

    void deleteChain(Node node) throws ServiceException;

    void deleteRelationship(Relationship relation) throws ServiceException;

    void deleteSingleNode(Node node) throws ServiceException;

    Relationship findLinkBetweenNodes(Node startNode, Node endNode, RelationshipType relationshipType, Direction direction)
            throws ServiceException;

    Iterator<Node> getAllChildren(Node node) throws ServiceException;

    Node getChainParent(Node node) throws ServiceException;

    Node getChildByName(Node parentNode, String name, INodeType nodeType) throws ServiceException;

    Node getChildByName(Node parentNode, String name, INodeType nodeType, RelationshipType relationshipType)
            throws ServiceException;

    Node getChildInChainByName(Node parentNode, String name, INodeType nodeType) throws ServiceException;

    /**
     * return all children from parent node with CHILD {@link RelationshipType}
     * 
     * @param parentNode
     * @return
     * @throws ServiceException
     */
    Iterator<Node> getChildren(Node parentNode) throws ServiceException;

    Iterator<Node> getChildren(Node parentNode, INodeType nodeType) throws ServiceException;

    Iterator<Node> getChildren(Node parentNode, INodeType nodeType, RelationshipType relationshipType) throws ServiceException;

    /**
     * get all children from parent node with {@link RelationshipType}
     * 
     * @param parentNode
     * @param relationshipType
     * @return
     * @throws ServiceException
     */
    Iterator<Node> getChildren(Node parentNode, RelationshipType relationshipType) throws ServiceException;

    Iterator<Node> getChildrenChain(Node parentNode) throws ServiceException;

    Iterator<Node> getChildrenChain(Node parentNode, INodeType nodeType) throws ServiceException;

    TraversalDescription getChildrenChainTraversal(final Node node);

    TraversalDescription getChildrenTraversal(final INodeType nodeType, final RelationshipType relationshipType);

    /**
     * Returns a Name property of Node
     * 
     * @param node
     * @return
     * @throws ServiceException in case of Name Property not found
     */
    String getNodeName(Node node) throws ServiceException;

    // TODO: LN: 10.10.2012, split getNodeProperty and getRelationshipProperty to one method (since
    // both Node nad Relationship extends PropertyContainer)
    <T extends Object> T getNodeProperty(final Node node, final String propertyName, final T defaultValue,
            final boolean throwExceptionIfNotExist) throws ServiceException;

    /**
     * Returns a NodeType property of Node
     * 
     * @param node
     * @return
     * @throws ServiceException in case of Type Property not found
     */
    INodeType getNodeType(Node node) throws ServiceException, NodeTypeNotExistsException;

    Node getParent(Node child, RelationshipType relationshipType) throws ServiceException;

    Node getReferencedNode() throws ServiceException;

    <T extends Object> T getRelationshipProperty(final Relationship relationship, final String propertyName, final T defaultValue,
            final boolean throwExceptionIfNotExist) throws ServiceException;

    Node getSingleChild(Node parentNode, INodeType nodeType, RelationshipType relationshipType) throws ServiceException;

    Relationship linkNodes(Node startNode, Node endNode, RelationshipType relationshipType) throws ServiceException;

    void removeNodeProperty(Node node, String propertyName, boolean throwExceptionIfNotExist) throws ServiceException;

    void renameNodeProperty(Node node, String oldPropertyName, String newPropertyName, boolean throwExceptionIfNotExist)
            throws ServiceException;

    /**
     * @param node
     * @param properties
     * @throws ServiceException
     */
    void updateProperties(Node node, Map<String, ? > properties) throws ServiceException;

    // TODO: LN: 10.10.2012, split updateProperty for nodes and updateProperty for Relationships to
    // one method (since both Node nad Relationship extends PropertyContainer)
    void updateProperty(Node node, String propertyName, Object newValue) throws ServiceException;

    void updateProperty(Relationship relationship, String propertyName, Object newValue) throws ServiceException;
}
