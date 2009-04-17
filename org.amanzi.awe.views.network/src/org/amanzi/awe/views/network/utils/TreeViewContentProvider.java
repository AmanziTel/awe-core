/*
 * Author Sachin Pathare
 * Date : 8 April 09
 */
package org.amanzi.awe.views.network.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewSite;
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
	 * Holds reference to features part of JSON file
	 */
	private JSONArray features;
	/**
	 * This list contains all unique BSCs
	 */
	private List<JSONObject> bscList = null;

	/**
	 * Parameterized constructor responsible for to load the JSONObject from file
	 * @param jsonObject
	 */
	public TreeViewContentProvider(JSONObject jsonObject) {
		this.features = jsonObject.getJSONArray("features");
	}
	
	/**
	 * This method would be called on load of tree and every time to get children of a particular node.
	 * 
	 */
	public Object[] getElements(Object parent) {
		if (parent instanceof IViewSite) {
			JSONObject object = new JSONObject();
			object.put("ROOT", ROOT);
			JSONObject[] jsonObjArr = {object};
			return jsonObjArr; 
		}
		
		return getChildren(parent);
	}
	
	/**
	 * When user clicks on + sign below tree is populated, this method would be called that time.
	 */
	public Object[] getChildren(Object parent) {
		JSONObject jsonPropObj = null;
		JSONObject inObject = (JSONObject)parent;
		List<JSONObject> list = new ArrayList<JSONObject>();
		JSONObject propObj = (JSONObject) inObject.get("properties");
		if(propObj != null && propObj.containsKey("sectors")){
			JSONArray sectorsArray = propObj.getJSONArray("sectors");
			Iterator<JSONObject> iterator = sectorsArray.iterator();
			while (iterator.hasNext()) {
				JSONObject leafObj = iterator.next();
				list.add(leafObj);
			}
			return list.toArray();
		}
		if(bscList == null){
			bscList = new ArrayList<JSONObject>();
			createListOfBSCs();
			return bscList.toArray();
		}
		Iterator<JSONObject> iter = features.iterator();
		String bsc = inObject.getString("bsc");
		while (iter.hasNext()) {
			JSONObject featureObj = iter.next();
			jsonPropObj = (JSONObject) featureObj.get("properties");
			if(bsc.equals(jsonPropObj.getString("bsc")))
				list.add(featureObj);
		}
		return list.toArray();
	}
	/**
	 * This method just creates a list which will contain unique BSCs
	 */
	private void createListOfBSCs(){
		Iterator<JSONObject> iter = features.iterator();
		
		JSONObject jsonPropObj= null; 
		int count = 0; 
		while (iter.hasNext()) {
			JSONObject featureObj = iter.next();
			jsonPropObj = (JSONObject) featureObj.get("properties");
			String currentBsc = jsonPropObj.getString("bsc");
			if(count == 0){
				bscList.add(jsonPropObj);
				count++;
			}else if(!bscList.get(count-1).getString("bsc").equals(currentBsc)){
				bscList.add(jsonPropObj);
				count++;
			}
		}
	}
	
	/**
	 * This method checks if input object has any children or not, if it has return true otherwise false.
	 */
	public boolean hasChildren(Object parent) {
		JSONObject jsonObj = (JSONObject)parent;
		if(jsonObj.containsKey("properties")){
			JSONObject lJsonObj = (JSONObject)jsonObj.get("properties");
			if(lJsonObj.containsKey("height"))
				return false;
		}
		return true;
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
	/**
	 * Given a input as child object this method returns parent object if exists otherwise returns null
	 */
	public Object getParent(Object child) {

		return null;
	}
}
