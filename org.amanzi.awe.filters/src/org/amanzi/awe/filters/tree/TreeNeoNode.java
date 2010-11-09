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

package org.amanzi.awe.filters.tree;

import java.awt.Color;
import java.util.Collection;

import net.refractions.udig.ui.graphics.Glyph;

import org.amanzi.awe.filters.FilterUtil;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 * Basic tree node - wrapper of neo4j node
 * </p>
 * 
 * @author Tsinkel_a
 * @since 1.0.0
 */
public class TreeNeoNode implements IAdaptable {
    private  String name;
    private final Node node;
    private  NodeTypes type;
    private Image image;
    private RGB oldColor;

    public TreeNeoNode(Node node) {

        this.node = node;
        this.name = NeoUtils.getNodeName(node);
        this.type = NodeTypes.getNodeType(node, null);
        image=null;
        oldColor=null;
    }

    /**
     * @return node name
     */
    public String getName() {
        return name;
    }

    public TreeNeoNode getParent(GraphDatabaseService service) {
        if (isRootNode()){
            return null;
        }
        Transaction tx = NeoUtils.beginTx(service);
        try {
            final Node parent = NeoUtils.getParent(service, node);
            return parent==null?null:new TreeNeoNode(parent);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    /**
     *checks node - is root node
     * @return
     */
    private boolean isRootNode() {
        return type==NodeTypes.FILTER_ROOT;
    }

    public TreeNeoNode[] getChildren(GraphDatabaseService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            Collection<Node> childs = NeoUtils.getChildTraverser(node).getAllNodes();
            TreeNeoNode[] result = new TreeNeoNode[childs.size()];
            int i = 0;
            for (Node child : childs) {
                result[i++] = new TreeNeoNode(child);
            }
            return result;
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == TreeNeoNode.class) {
            return this;
        }
        if (adapter == Node.class) {
            return node;
        }
        return null;
    }

    /**
     * @return Returns the node.
     */
    public Node getNode() {
        return node;
    }

    /**
     * @return Returns the type.
     */
    public NodeTypes getType() {
        return type;
    }

    /**
     * @param service
     * @return
     */
    public boolean hasChildren(GraphDatabaseService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            return node.hasRelationship(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((node == null) ? 0 : node.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof IAdaptable)){
            return false;
        }
        Node othernode=(Node)((IAdaptable)obj).getAdapter(Node.class);
        if (node == null) {
            if (othernode != null)
                return false;
        } else if (!node.equals(othernode))
            return false;
        return true;
    }

    /**
     *
     */
    public void refresh(GraphDatabaseService service) {
        name = NeoUtils.getNodeName(node);
       type = NodeTypes.getNodeType(node, service);
    }

    /**
     *
     */
    public void formColorImage(GraphDatabaseService service) {
      Transaction tx = NeoUtils.beginTx(service);
      try{
          RGB newColor = getColor();
          if (newColor==null){
              if (image!=null){
                  image.dispose();
              }
              image=null;
              oldColor=null;
          }else if (!newColor.equals(oldColor)){
              if (image!=null){
                  image.dispose();
              }
              image=Glyph.palette(new Color[]{new Color(newColor.red,newColor.green,newColor.blue)}).createImage();
              oldColor=newColor;
          }
      }finally{
          NeoUtils.finishTx(tx);
      }
    }

    /**
     * Gets color of right bar
     * 
     */
    private RGB getColor() {
        if (node != null) {
            int[] colors = (int[])node.getProperty(FilterUtil.PROPERTY_FILTER_COLOR, null);
            if (colors != null) {
                return new RGB(colors[0], colors[1], colors[2]);
            }
        }
        return null;
    }
    /**
     *
     * @return
     */
    public Image getImage() {
        return image;
    }
@Override
protected void finalize() throws Throwable {
    super.finalize();
    if (image!=null){
        image.dispose();
    }
}
}
