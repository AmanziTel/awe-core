/*
 * Author Sachin Pathare
 * Date : 8 April 09
 */
package org.amanzi.awe.views.network.utils;

import java.io.IOException;
import java.io.InputStream;

import org.amanzi.awe.views.network.domain.TreeObject;
import org.amanzi.awe.views.network.domain.TreeParent;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
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
		return obj.toString();
	}
	/**
	 * This method will be invoked when in viewer for a particular node or child image icon needs to 
	 * be displayed
	 */
	public Image getImage(Object obj) {
		InputStream in = null;
		in = getClass().getResourceAsStream("/icons/world_16.png");
		if(obj.getClass().getName().equals(TreeObject.class.getName())){
			return new Image(Display.getCurrent(), in);
		}
		TreeParent node = (TreeParent)obj;
		if (node.getChildren().length > 0) {
			in = getClass().getResourceAsStream("/icons/awe_icon_16.gif");
			if (in != null) {
				try {
					return new Image(Display.getCurrent(), in);
				} catch (SWTException e) {
					if (e.code != SWT.ERROR_INVALID_IMAGE) {
						throw e;
					}
				} finally {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return null;
	}
}