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

import org.amanzi.neo.loader.core.newparser.IParser;
import org.amanzi.neo.loader.core.newsaver.IData;
import org.amanzi.neo.loader.core.newsaver.ISaver;

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
     * set saver for selected loader
     * 
     * @param saver
     */
    @SuppressWarnings("rawtypes")
    void setSaver(ISaver saver);

    /**
     * set parser for selected loader;
     * 
     * @param parser
     */
    @SuppressWarnings("rawtypes")
    void setParser(IParser parser);

    /**
     * run loader
     */
    void run();

    /**
     * set validator for selected loader
     * 
     * @param validator
     */
    void setValidator(IValidator validator);

    /**
     * configure loader
     * 
     * @param config
     */
    void init(T2 config);

    /**
     * get validator for current loader;
     * 
     * @return validator
     */
    IValidator getValidator();

    /**
     * get loader info
     * 
     * @return
     */
    ILoaderInfo getLoaderInfo();

    /**
     * set loader info
     * 
     * @return
     */
    void setLoaderInfo(ILoaderInfo info);
}
