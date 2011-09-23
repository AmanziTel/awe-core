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

package org.amanzi.neo.loader.core.impl;

import java.io.File;

import org.amanzi.neo.loader.core.IConfiguration;

/**
 * General Class for Configuration on loading data from Directory
 * 
 * 
 * 
 * @author gerzog
 * @since 1.0.0
 */
public abstract class AbstractDirectoryConfiguration implements IConfiguration {
    
    protected File directory;
    
    @Override
    public boolean isMultiFile() {
        return true;
    }
    
    @Override
    public void setSourceFile(File file) {
        directory = file;
    }
    
    

}
