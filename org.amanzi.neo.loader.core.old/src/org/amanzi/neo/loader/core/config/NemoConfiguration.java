/**
 * 
 */
package org.amanzi.neo.loader.core.config;

import java.io.File;

/**
 * Nemo configuration
 * 
 * @author Bondoronok_P
 */
public class NemoConfiguration extends AbstractConfiguration implements
		ISingleFileConfiguration {

	private static final int FIRST_FILE_INDEX = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.amanzi.neo.loader.core.config.ISingleFileConfiguration#setFile(java
	 * .io.File)
	 */
	@Override
	public void setFile(File fileToLoad) {
		getFilesToLoad().clear();
		addFileToLoad(fileToLoad);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.amanzi.neo.loader.core.config.ISingleFileConfiguration#getFile()
	 */
	@Override
	public File getFile() {
		return getFilesToLoad().get(FIRST_FILE_INDEX);
	}

}
