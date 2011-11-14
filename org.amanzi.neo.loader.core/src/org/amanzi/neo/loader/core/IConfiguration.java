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

import java.io.File;
import java.util.Collection;
import java.util.List;
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
    List<File> getFilesToLoad();

    /**
     * names of dataset;
     * 
     * @return dataset Names;
     */
    Map<Object, String> getDatasetNames();

    /**
     * set source file
     */
    void setSourceFile(Collection<File> file);

    /**
     * set dataset names map
     */
    void setDatasetNames(Map<Object, String> datasetNames);
}
