/*
 * Author Sachin Pathare
 * Date : 8 April 09
 */
package org.amanzi.awe.views.network.domain;

import org.eclipse.core.runtime.IAdaptable;
/**
 * Concrete instance of this class indicates that its a Leaf node of Tree 
 * @author sachinp
 *
 */
public class TreeObject implements IAdaptable {
	/**
	 * Holds the String value which showed on the tree
	 */
	private String name;
	/**
	 * Holds reference to parent of the leaf node or any node
	 */
	private TreeParent parent;
	/**
	 * Parameterized constructor 
	 * @param name
	 */
	public TreeObject(String name) {
		this.name = name;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the parent
	 */
	public TreeParent getParent() {
		return parent;
	}
	/**
	 * @param parent the parent to set
	 */
	public void setParent(TreeParent parent) {
		this.parent = parent;
	}
	/**
	 * toString is overriden so that it will return more meaningful data, and here it is name of the node.
	 */
	public String toString() {
		return getName();
	}
	/**
	 * 
	 */
	public Object getAdapter(Class key) {
		return null;
	}

	
}
