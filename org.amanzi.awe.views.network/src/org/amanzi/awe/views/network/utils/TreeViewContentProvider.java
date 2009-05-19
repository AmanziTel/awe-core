/*
 * Author Sachin Pathare
 * Date : 8 April 09
 */
package org.amanzi.awe.views.network.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import org.amanzi.awe.views.network.beans.ExtTreeNode;
import org.amanzi.awe.views.network.beans.ExtTreeNodes;
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

    private ExtTreeNode rootNode;
    /**
     * Parameterized constructor responsible for to load the JSONObject from file.
     * 
     * @param treeHref
     * @param baseUrl {@link URL} object
     */
    public TreeViewContentProvider( final String treeHref, final URL baseUrl ) {
        try {
            final URL newurl = new URL(baseUrl, new File(baseUrl.toString()).getParent() + treeHref);
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
     * @return
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
     * @return
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

            final String treeNodesString = readURL(new URL(address));
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
     * null.
     * 
     * @param child
     */
    public Object getParent( Object child ) {

        return null;
    }

    /**
     * TODO move to some util package since this method exists in JSONReader also.<br />
     * Read a file from given URL.
     * 
     * @param url {@link URL}object
     * @return file as string
     */
    public static String readURL( final URL url ) {
        final StringBuilder sb = new StringBuilder();
        Reader reader = null;
        try {
            reader = new InputStreamReader(url.openStream(), "UTF8");
            final char[] buffer = new char[1024];
            int bytesRead = 0;

            while( (bytesRead = reader.read(buffer)) >= 0 ) {
                if (bytesRead > 0) {
                    sb.append(buffer);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to get features from url '" + url + "': " + e);
            e.printStackTrace(System.err);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return sb.toString();
    }
}
