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

package org.amanzi.neo.core.utils;

import org.amanzi.neo.core.enums.NodeTypes;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Wrapper of Node for using in views
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public abstract class NeoTreeElement implements IAdaptable {
    protected String text = null;
    protected Image image = null;
    protected NodeTypes type;
    protected final Node node;
    protected final GraphDatabaseService service;

    /**
     * Instantiates a new neo tree element.
     *
     * @param node the node
     * @param service the service
     */
    public NeoTreeElement(Node node, GraphDatabaseService service) {
        this.node = node;
        this.service = service;
        text = NeoUtils.getSimpleNodeName(node, "", service);
        type = NodeTypes.getNodeType(node, null);
        image = type == null ? null : type.getImage();
    }

    /**
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }

    /**
     * @return Returns the image.
     */
    public Image getImage() {
        return image;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((node == null) ? 0 : node.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == NeoTreeElement.class) {
            return this;
        }
        if (adapter == Node.class) {
            return node;
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof IAdaptable)) {
            return false;
        }
        Node othernode = (Node)((IAdaptable)obj).getAdapter(Node.class);
        if (node == null) {
            if (othernode != null)
                return false;
        } else if (!node.equals(othernode))
            return false;
        return true;
    }


    public abstract NeoTreeElement[] getChildren();

    public abstract NeoTreeElement getParent();

    public abstract boolean hasChildren();

    /**
     * @return Returns the type.
     */
    public NodeTypes getType() {
        return type;
    }

    /**
     * @return Returns the node.
     */
    public Node getNode() {
        return node;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImage(Image image) {
        this.image = image;
    }


}

