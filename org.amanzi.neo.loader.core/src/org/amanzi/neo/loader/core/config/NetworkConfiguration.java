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

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author lagutko_n
 * @since 1.0.0
 */
public class NetworkConfiguration extends AbstractConfiguration implements ISingleFileConfiguration {

    @Override
    public void setFile(File fileToLoad) {
        getFilesToLoad().clear();
        addFileToLoad(fileToLoad);
    }

    @Override
    public File getFile() {
        return getFilesToLoad().get(0);
    }

}
