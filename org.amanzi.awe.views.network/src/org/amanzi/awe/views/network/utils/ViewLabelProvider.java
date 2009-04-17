/*
 * Author Sachin Pathare
 * Date : 8 April 09
 */
package org.amanzi.awe.views.network.utils;

import java.io.InputStream;

import net.sf.json.JSONObject;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
/**
 * This class represents Label Provider for viewer of Network tree view
 * @author sachinp
 *
 */
public class ViewLabelProvider extends LabelProvider {
	/**
	 * 
	 */
	public String getText(Object obj) {
		JSONObject object = (JSONObject)obj;
		if(object.containsKey("ROOT")){
			return object.getString("ROOT");
		}
		else if(object.containsKey("name") && (object.containsKey("bsc"))){
			return object.getString("bsc");
		}
		else if(object.containsKey("name")){
			return object.getString("name");
		}else if(object.containsKey("properties")){
			JSONObject propObj = (JSONObject) object.get("properties");
			return propObj.getString("name");
		}
		return "";
	}
	/**
	 * This method will be invoked when in viewer for a particular node or child image icon needs to 
	 * be displayed
	 */
	public Image getImage(Object obj) {
		InputStream in = null;
		JSONObject jsonObj = (JSONObject)obj;
		if(jsonObj.containsKey("properties")){
			JSONObject lJsonObj = (JSONObject)jsonObj.get("properties");
			if(lJsonObj.containsKey("height")){
				in = getClass().getResourceAsStream("/icons/world_16.png");
				return new Image(Display.getCurrent(), in);
			}
		}
		in = getClass().getResourceAsStream("/icons/awe_icon_16.gif");
		return new Image(Display.getCurrent(), in);
	}
}