/*
 * Author Sachin Pathare
 * Date : 8 April 09
 */
package org.amanzi.awe.views.network.domain;

import java.util.ArrayList;
/**
 * This class holds the child objects if node has child
 * @author sachinp
 *
 */
public class TreeParent extends TreeObject {
	/**
	 * Holds the children for particular node
	 */
	private ArrayList children;
	/**
	 * Parameterized constructor
	 * @param name
	 */
	public TreeParent(String name) {
		super(name);
		children = new ArrayList();
	}
	/**
	 * Link method to add child
	 * @param child
	 */
	public void addChild(TreeObject child) {
		children.add(child);
		child.setParent(this);
	}
	/**
	 * Link method to remove child
	 * @param child
	 */
	public void removeChild(TreeObject child) {
		children.remove(child);
		child.setParent(null);
	}
	/**
	 * Returns array of all children for a particular node
	 * @return array of TreeObject
	 */
	public TreeObject[] getChildren() {
		return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
	}
	/**
	 * Checks if particular node has any children
	 * @return
	 */
	public boolean hasChildren() {
		return children.size() > 0;
	}

}
