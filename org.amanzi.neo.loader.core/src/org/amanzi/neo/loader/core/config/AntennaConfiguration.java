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

package org.amanzi.neo.loader.core.config;

import java.io.File;
import java.util.List;

/**
 * 
 * TODO Purpose of 
 * <p>
 * Configuration for loading of directory with files
 * </p>
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class AntennaConfiguration extends AbstractConfiguration implements IMultiFileConfiguration {

    @Override
    public void setFiles(List<File> files) {
        setFilesToLoad(files);
    }   

}
