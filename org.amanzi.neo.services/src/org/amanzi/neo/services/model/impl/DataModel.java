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

package org.amanzi.neo.services.model.impl;

import java.util.Iterator;

import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDataModel;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public abstract class DataModel extends AbstractModel implements IDataModel {

    private static Logger LOGGER = Logger.getLogger(DataModel.class);

    private NewDatasetService dsServ = NeoServiceFactory.getInstance().getNewDatasetService();

    /**
     * Gets node from <code>parent</code> element (the node must not be <code>null</code>), creates
     * a child node, using properties in <code>child</code>, creates CHILD relationship from parent
     * to child. Don't forget to set node type in <code>child</code>. See also
     * {@link NewDatasetService#addChild(Node, Node)}.
     * 
     * @param parent <code>DataElement</code> object, containing parent node.
     * @param child <code>DataElement</code> object, containing properties of the child node, that
     *        must be created
     * @return <code>IDataElement</code> based on the new child node
     */
    public IDataElement addChild(IDataElement parent, IDataElement child) {
        // validate
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        if (child == null) {
            throw new IllegalArgumentException("Child is null.");
        }
        Node parentNode = ((DataElement)parent).getNode();
        if (parentNode == null) {
            throw new IllegalArgumentException("Parent node is null.");
        }

        try {
            Node childNode = dsServ.createNode((DataElement)child);
            Node result = dsServ.addChild(parentNode, childNode);
            return result == null ? null : new DataElement(result);
        } catch (DatabaseException e) {
            LOGGER.error("Could not add child.", e);
        }
        return null;
    }

    /**
     * Gets node from <code>parent</code> element (the node must not be <code>null</code>), creates
     * a child node, using properties in <code>child</code>, adds child to the end of parent's
     * children chain; uses <code>lastChild</code> , if it is set. Don't forget to set node type in
     * <code>child</code>. See also {@link NewDatasetService#addChild(Node, Node, Node)}.
     * 
     * @param parent <code>DataElement</code> object, containing parent node.
     * @param child <code>DataElement</code> object, containing properties of the child node, that
     *        must be created
     * @param lastChild if set, must contain a node, that is the last in the parent's children chain
     * @return <code>IDataElement</code> based on the new child node
     */
    public IDataElement addChild(IDataElement parent, IDataElement child, IDataElement lastChild) {
        // validate
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        if (child == null) {
            throw new IllegalArgumentException("Child is null.");
        }
        Node parentNode = ((DataElement)parent).getNode();
        if (parentNode == null) {
            throw new IllegalArgumentException("Parent node is null.");
        }

        try {
            Node lastChildNode = lastChild == null ? null : ((DataElement)lastChild).getNode();
            Node childNode = dsServ.createNode((DataElement)child);
            Node result = dsServ.addChild(parentNode, childNode, lastChildNode);
            return result == null ? null : new DataElement(result);
        } catch (DatabaseException e) {
            LOGGER.error("Could not add child.", e);
        }
        return null;
    }

    @Override
    public IDataElement getParentElement(IDataElement childElement) {
        // validate
        if (childElement == null) {
            throw new IllegalArgumentException("childElement is null");
        }

        Node child = ((DataElement)childElement).getNode();

        Node parent = null;
        if (child != null) {
            try {
                parent = dsServ.getParent(child, false);
            } catch (DatabaseException e) {
                LOGGER.error("Could not get parent element.", e);
            }
        }
        IDataElement result = parent == null ? null : new DataElement(parent);
        return result;
    }

    public class DataElementIterable implements Iterable<IDataElement> {
        private class DataElementIterator implements Iterator<IDataElement> {

            private Iterator<Node> it;

            public DataElementIterator(Iterable<Node> nodeTraverse) {
                this.it = nodeTraverse.iterator();
            }

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public IDataElement next() {
                return new DataElement(it.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        }

        private Iterable<Node> nodeTraverse;

        public DataElementIterable(Iterable<Node> nodeTraverse) {
            this.nodeTraverse = nodeTraverse;
        }

        @Override
        public Iterator<IDataElement> iterator() {
            return new DataElementIterator(nodeTraverse);
        }
    }

}
