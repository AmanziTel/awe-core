package org.amanzi.awe.neo.views.network.utils;

import java.io.InputStream;

import org.amanzi.awe.neo.views.network.beans.ExtTreeNode;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * This class represents Label Provider for viewer of Network tree view
 * 
 * @author Dalibor
 */
public class ViewLabelProvider extends LabelProvider {
	/**
	 * Fetches the text from {@link ExtTreeNode} object.
	 */
	public String getText(Object obj) {
		final ExtTreeNode object = (ExtTreeNode) obj;
		return object.getText();
	}

	/**
	 * This method will be invoked when in viewer for a particular node or child
	 * image icon needs to be displayed
	 */
	public Image getImage(Object obj) {
		InputStream in = null;
		final ExtTreeNode extTreeNode = (ExtTreeNode) obj;
		if (extTreeNode.isLeaf()) {
			in = getClass().getResourceAsStream("/icons/16/awe.gif");
		} else {
			in = getClass().getResourceAsStream("/icons/16/radio.png");
		}
		return new Image(Display.getCurrent(), in);
	}
}