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

package org.amanzi.awe.afp.testing.engine.internal;

import java.io.File;

import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.ILoader;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public class LoadNetworkDataAction extends AbstractLoadAction {
    
    /**
     * @param file
     * @param projectName
     * @param rootName
     */
    public LoadNetworkDataAction(File file, String projectName, String rootName) {
        super(file, projectName, rootName);
    }

    @Override
    public ILoader<?, CommonConfigData> getLoader() {
        return FakeLoaderFactory.getNetworkLoader();
    }

}
