/*
 * Author Sachin Pathare
 * Date : 8 April 09
 */
package org.amanzi.awe.views.network.utils;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.amanzi.awe.views.network.domain.TreeObject;
import org.amanzi.awe.views.network.domain.TreeParent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
/**
 * This class is responsible to populate actual object contents in a tree
 * @author sachinp
 *
 */
public class TreeViewContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {
	/**
	 * Constant for text NETWORK Which is root node
	 */
	private static String ROOT = "NETWORK";
	/**
	 * Parent of root node
	 */
	private TreeParent invisibleRoot;
	/**
	 * Holds reference to features part of JSON file
	 */
	private JSONArray features;
	/**
	 * Holds reference to complete parsed JSONObject
	 */
	private JSONObject jsonObject = null;
	/**
	 * Parameterized constructor responsible for to load the JSONObject from file
	 * @param jsonObject
	 */
	public TreeViewContentProvider(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
		this.features = jsonObject.getJSONArray("features");
	}
	
	/**
	 * This method would be called on load of tree and every time to get children of a particular node.
	 * 
	 */
	public Object[] getElements(Object parent) {

		// if (parent.equals(getViewSite())) {
		if (invisibleRoot == null) {
			initialize();
			return getChildren(invisibleRoot);
		}
		// }
		return getChildren(parent);
	}
	/**
	 * Given a input as child object this method returns parent object if exists otherwise returns null
	 */
	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject) child).getParent();
		}
		return null;
	}
	/**
	 * When user clicks on + sign below tree is populated, this method would be called that time.
	 */
	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			JSONObject propObj = null;
			TreeParent parentObj = (TreeParent) parent;

			for (int i = 0; i < parentObj.getChildren().length; i++) {
				Object object = parentObj.getChildren()[i];
				if (object instanceof TreeParent) {
					TreeParent node = (TreeParent) parentObj.getChildren()[i];
					if (node.getParent().getName().equals("")
							&& ROOT.equals(node.getName())) {
						Iterator<JSONObject> iter = features.iterator();
						TreeParent parent1 = null;
						while (iter.hasNext()) {
							JSONObject featureObj = iter.next();
							propObj = (JSONObject) featureObj.get("properties");
							String mainStation = propObj.getString("bsc");

							if (parent1 != null
									&& !parent1.getName().equals(mainStation)) {
								parent1 = new TreeParent(mainStation);
								node.addChild(parent1);
							} else if (parent1 == null) {
								parent1 = new TreeParent(mainStation);
								node.addChild(parent1);
							}
						}
					} else {
						TreeParent lowerNode = null;
						String parentName = node.getName();
						// if (node.hasChildren()) {
						Iterator<JSONObject> iter = features.iterator();
						while (iter.hasNext()) {
							JSONObject featureObj = iter.next();
							propObj = (JSONObject) featureObj.get("properties");
							if (parentName.equals(propObj.get("bsc"))) {
								lowerNode = createProfiles(propObj);
								node.addChild(lowerNode);
							}
						}
						// }
					}
				}
			}
			return parentObj.getChildren();
			// return ((TreeParent) parent).getChildren();
		}
		return new Object[0];
	}
	/**
	 * Prepares a node and its children for a input sector
	 * @param propObj
	 * @return TreeParent
	 */
	private TreeParent createProfiles(JSONObject propObj) {
		String subStation = propObj.getString("name");
		TreeParent parent = new TreeParent(subStation);
		JSONArray sectorsArray = propObj.getJSONArray("sectors");

		Iterator<JSONObject> iterator = sectorsArray.iterator();
		while (iterator.hasNext()) {
			JSONObject leafObj = iterator.next();
			String leafElement = leafObj.getString("name");
			TreeObject treeObject = new TreeObject(leafElement);
			parent.addChild(treeObject);
		}
		return parent;
	}
	/**
	 * This method checks if input object has any children or not, if it has return true otherwise false.
	 */
	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent)
			return ((TreeParent) parent).hasChildren();
		return false;
	}
	/**
	 * This method initializes basic starting point of the tree.
	 */
	private void initialize() {
		TreeParent root = new TreeParent("NETWORK");
		
		invisibleRoot = new TreeParent("");
		invisibleRoot.addChild(root);
	}
	 /**
     * Notifies this content provider that the given viewer's input
     * has been switched to a different element.
     * <p>
     * A typical use for this method is registering the content provider as a listener
     * to changes on the new input (using model-specific means), and deregistering the viewer 
     * from the old input. In response to these change notifications, the content provider
     * should update the viewer (see the add, remove, update and refresh methods on the viewers).
     * </p>
     * <p>
     * The viewer should not be updated during this call, as it might be in the process
     * of being disposed.
     * </p>
     *
     * @param viewer the viewer
     * @param oldInput the old input element, or <code>null</code> if the viewer
     *   did not previously have an input
     * @param newInput the new input element, or <code>null</code> if the viewer
     *   does not have an input
     */
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		
	}
	 /**
     * Disposes of this content provider.  
     * This is called by the viewer when it is disposed.
     * <p>
     * The viewer should not be updated during this call, as it is in the process
     * of being disposed.
     * </p>
     */
	public void dispose() {
		
	}

}
