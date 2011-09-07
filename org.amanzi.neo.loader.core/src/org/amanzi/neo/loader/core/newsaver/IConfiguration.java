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

package org.amanzi.neo.loader.core.newsaver;

import java.io.File;
import java.util.Map;

import org.amanzi.neo.loader.core.parser.IConfigurationData;

/**
 * common configuration data interface;
 * 
 * @author Kondratenko_Vladsialv
 */
public interface IConfiguration extends IConfigurationData {
    /**
     * check for few file selection
     * 
     * @return
     */
    boolean isMultiFile();

    /**
     * get selected files;
     * 
     * @return array of selected files
     */
    File[] getFilesToLoad();

    /**
     * names of dataset;
     * 
     * @return dataset Names;
     */
    Map<Object, String> getDatasetNames();
}
