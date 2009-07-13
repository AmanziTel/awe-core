package org.neo4j.neoclipse.preference;

import java.io.File;

/**
 * Provides additional operations for neoclipse plug-in
 * 
 * @author Pechko_E
 * 
 */
public class NeoPreferenceHelper {
	/**
	 * Icon directories list
	 */
	private File[] iconDirectories;

	/**
	 * The constructor
	 */
	public NeoPreferenceHelper() {

	}

	/**
	 * Getter for list of available icon directories
	 * 
	 * @return list of icon directories
	 */
	public File[] getIconDirectories() {
		return iconDirectories;
	}

	/**
	 * Setter for list of available icon directories
	 * 
	 * @param iconDirectories
	 *            available icon directories
	 */
	public void setIconDirectories(File[] iconDirectories) {
		this.iconDirectories = iconDirectories;
	}

}
