/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.neo.loader.core;

import java.util.List;

import org.amanzi.neo.loader.core.parser.IParser;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.services.exceptions.AWEException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * <p>
 * Common Loader interface
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public interface ILoader {
	/**
	 * set saver for selected loader
	 * 
	 * @param saver
	 */
	@SuppressWarnings("rawtypes")
    public void setSavers(List<ISaver> saver);

	/**
	 * set parser for selected loader;
	 * 
	 * @param parser
	 */
	@SuppressWarnings("rawtypes")
	public void setParser(IParser parser);

	/**
	 * run loader
	 * 
	 * @throws AWEException
	 */
	public void run(IProgressMonitor monitor) throws AWEException;

	/**
	 * set validator for selected loader
	 * 
	 * @param validator
	 */
	public void setValidator(IValidator validator);

	/**
	 * configure loader
	 * 
	 * @param config
	 * @throws Exception
	 */
	public void init(IConfiguration config) throws AWEException;

	/**
	 * get validator for current loader;
	 * 
	 * @return validator
	 */
	public IValidator getValidator();
	
	public void setName(String newName);
	
	public String getName();
}
