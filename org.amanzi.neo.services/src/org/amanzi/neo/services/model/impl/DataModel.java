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
    private NewNetworkService nwServ = NeoServiceFactory.getInstance().getNewNetworkService();

    protected void addChild(Node parent, Node child) {
        try {
            dsServ.addChild(parent, child);
        } catch (DatabaseException e) {
            LOGGER.error("Could not add child.", e);
        }
    }

    protected void addChild(Node parent, Node child, Node lastChild) {
        try {
            dsServ.addChild(parent, child, lastChild);
        } catch (DatabaseException e) {
            LOGGER.error("Could not add child.", e);
        }
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

    @Override
    public Iterable<IDataElement> getChildren(IDataElement parent) {
        // validate
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        Node parentNode = ((DataElement)parent).getNode();
        if (parentNode == null) {
            throw new IllegalArgumentException("Parent node is null.");
        }

        return new DataElementIterable(dsServ.getChildrenTraverser(parentNode));
    }

    /**
     * Traverses only over CHILD relationships.
     */
    @Override
    public Iterable<IDataElement> getAllElementsByType(INodeType elementType) {
        // validate
        if (elementType == null) {
            throw new IllegalArgumentException("Element type is null.");
        }

        return new DataElementIterable(nwServ.findAllNetworkElements(getRootNode(), elementType));
    }

    @Override
    public void finishUp() {
    }

    private class DataElementIterable implements Iterable<IDataElement> {
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
