/**
 * 
 */
package org.amanzi.neo.loader.core;

import java.io.File;
import java.util.List;

import org.amanzi.neo.loader.core.parser.IConfigurationData;

/**
 * @author Kasnitskij_V
 *
 */
public class CommonConfigDataMulti extends CommonConfigData implements IConfigurationData {
	
    /** The root. */
    private List<File> roots;

	/**
	 * @param roots the roots to set
	 */
	public void setMultiRoots(List<File> roots) {
		this.roots = roots;
	}

	/**
	 * @return the roots
	 */
	public List<File> getMultiRoots() {
		return roots;
	}
}
