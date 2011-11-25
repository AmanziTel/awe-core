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

import org.amanzi.neo.loader.core.newparser.IParser;
import org.amanzi.neo.loader.core.newsaver.IData;
import org.amanzi.neo.loader.core.newsaver.ISaver;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IModel;

/**
 * <p>
 * Common Loader interface
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public interface ILoaderNew<T extends IData, T2 extends IConfiguration> {
	/**
	 * set progress bar to loader
	 * 
	 * @param listener
	 */
	void addProgressListener(ILoaderProgressListener listener);

	/**
	 * remove progress Monitor
	 * 
	 * @param listener
	 */
	void removeProgressListener(ILoaderProgressListener listener);

	/**
	 * set saver for selected loader
	 * 
	 * @param saver
	 */
	public void setSaver(List<ISaver<? extends IModel, T, T2>> saver);

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
	public void run() throws AWEException;

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
	public void init(T2 config) throws Exception;

	/**
	 * get validator for current loader;
	 * 
	 * @return validator
	 */
	public IValidator getValidator();

	/**
	 * get loader info
	 * 
	 * @return
	 */
	public ILoaderInfo getLoaderInfo();

	/**
	 * set loader info
	 * 
	 * @return
	 */
	public void setLoaderInfo(ILoaderInfo info);
}
