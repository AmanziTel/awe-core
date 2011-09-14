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

package org.amanzi.neo.loader.core.newparser;

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.newsaver.IData;
import org.amanzi.neo.loader.core.newsaver.ISaver;
import org.amanzi.neo.services.networkModel.IModel;

/**
 * common parser interface
 * 
 * @author Kondratenko_Vladislav
 */
public interface IParser<T1 extends ISaver<IModel, T3, T2>, T2 extends IConfiguration, T3 extends IData> {
    /**
     * initialize required parser data;
     * 
     * @param configuration common configuration data
     * @param saver which saver use for saving data to database
     */
    void init(T2 configuration, T1 saver);

    /**
     * run parser and save parsed files in database. For saving data parser use saver which it was
     * initialize in <B>init</B> method;
     */
    void run();
}
