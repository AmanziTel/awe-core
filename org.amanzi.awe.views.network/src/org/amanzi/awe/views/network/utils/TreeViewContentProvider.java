/*
 * Author Sachin Pathare
 * Date : 8 April 09
 */
package org.amanzi.awe.views.network.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.amanzi.awe.catalog.json.JSONReader;
import org.amanzi.awe.catalog.json.beans.ExtTreeNode;
import org.amanzi.awe.catalog.json.beans.ExtTreeNodes;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewSite;

/**
 * This class is responsible to populate actual object contents in a tree.
 * 
 * @author sachinp
 */
public class TreeViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {
    /**
     * Constant for text NETWORK Which is root node.
     */
    private static final String ROOT = "NETWORK";

    /**
     * base url.
     */
    private URL newurl;
    private ExtTreeNode rootNode;
    /**
     * Parameterized constructor responsible for to load the JSONObject from file.
     * 
     * @param jsonReader {@link JSONReader} object
     */
    public TreeViewContentProvider( final JSONReader jsonReader ) {
        try {
            final String href = jsonReader.getExtJSON().getExtTree().getHref();
            newurl = new URL(jsonReader.getUrl(), new File(jsonReader.getUrl().toString())
                    .getParent()
                    + href);
            rootNode = new ExtTreeNode(ROOT, newurl.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method would be called on load of tree and every time to get children of a particular
     * node.
     * 
     * @param parent
     */
    public Object[] getElements( Object parent ) {
        if (parent instanceof IViewSite) {
            final ExtTreeNode[] elements = {rootNode};
            return elements;
        }

        return getChildren(parent);
    }
    /**
     * When user clicks on + sign below tree is populated, this method would be called that time.
     * 
     * @param parent
     */
    public Object[] getChildren( Object parent ) {
        ExtTreeNodes nodes = new ExtTreeNodes();
        final ExtTreeNode treeNode = (ExtTreeNode) parent;
        try {
            String address;

            if (treeNode.getId() == null) {
                address = rootNode.getHref();
            } else {
                address = rootNode.getHref() + "_node=" + treeNode.getId();
            }

            System.out.println(address);
            final String treeNodesString = JSONReader.readURL(new URL(address));
            if (treeNodesString != null) {
                nodes = new ExtTreeNodes(treeNodesString);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return nodes.getList().toArray();
    }
    /**
     * This method checks if input object has any children or not, if it has return true otherwise
     * false.
     * 
     * @param parent
     */
    public boolean hasChildren( Object parent ) {
        final ExtTreeNode extTreeNode = (ExtTreeNode) parent;
        return !extTreeNode.isLeaf();
    }

    /**
     * Notifies this content provider that the given viewer's input has been switched to a different
     * element.
     * <p>
     * A typical use for this method is registering the content provider as a listener to changes on
     * the new input (using model-specific means), and deregistering the viewer from the old input.
     * In response to these change notifications, the content provider should update the viewer (see
     * the add, remove, update and refresh methods on the viewers).
     * </p>
     * <p>
     * The viewer should not be updated during this call, as it might be in the process of being
     * disposed.
     * </p>
     * 
     * @param viewer the viewer
     * @param oldInput the old input element, or <code>null</code> if the viewer did not
     *        previously have an input
     * @param newInput the new input element, or <code>null</code> if the viewer does not have an
     *        input
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {

    }
    /**
     * Disposes of this content provider. This is called by the viewer when it is disposed.
     * <p>
     * The viewer should not be updated during this call, as it is in the process of being disposed.
     * </p>
     */
    public void dispose() {

    }
    /**
     * Given a input as child object this method returns parent object if exists otherwise returns
     * null
     */
    public Object getParent( Object child ) {

        return null;
    }
}
