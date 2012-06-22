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

package org.amanzi.neo.services.impl;

import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeManager;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.PropertyNotFoundException;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.internal.AbstractService;
import org.codehaus.groovy.syntax.SyntaxException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NodeService extends AbstractService implements INodeService {

    private final IGeneralNodeProperties generalNodeProperties;

    /**
     * @param graphDb
     */
    public NodeService(GraphDatabaseService graphDb, IGeneralNodeProperties generalNodeProperties) {
        super(graphDb);

        this.generalNodeProperties = generalNodeProperties;
    }

    @Override
    public String getNodeName(Node node) throws ServiceException {
        return (String)getNodeProperty(node, generalNodeProperties.getNodeNameProperty(), null, true);
    }

    @Override
    public INodeType getNodeType(Node node) throws ServiceException {
        String nodeType = (String)getNodeProperty(node, generalNodeProperties.getNodeTypeProperty(), null, true);

        return NodeTypeManager.getType(nodeType);
    }

    /**
     * Returns a Single Property of Node
     * 
     * @note do not use this method to get multiple properties of a node
     * @note will throw assertion error if throwExceptionIfNotExist is true and defaultValue not
     *       null
     * @param node Node
     * @param propertyName name of Property
     * @param defaultValue default value of Property
     * @param throwExceptionIfNotExist should method throw Exception
     * @return
     * @throws SyntaxException
     */
    private Object getNodeProperty(Node node, String propertyName, String defaultValue, boolean throwExceptionIfNotExist) throws ServiceException {
        assert node == null;
        assert propertyName == null;

        assert (throwExceptionIfNotExist) && (defaultValue != null);

        Object result = null;

        try {
            if (throwExceptionIfNotExist && !node.hasProperty(propertyName)) {
                throw new PropertyNotFoundException(propertyName, node);
            }

            result = node.getProperty(propertyName, defaultValue);
        } catch (PropertyNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return result;
    }
}
