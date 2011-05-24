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

import org.amanzi.neo.db.manager.DatabaseManager.DatabaseAccessType;
import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.ILoader;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public abstract class AbstractLoadAction implements Runnable {
    
    private static Logger LOGGER = Logger.getLogger(AbstractLoadAction.class);

    protected File file;
    
    private String rootName;
    
    private String projectName;
    
    public AbstractLoadAction(File file, String projectName, String rootName) {
        this.file = file;
        this.rootName = rootName;
        this.projectName = projectName;
    }
    
    @Override
    public void run() {
        ILoader<?, CommonConfigData> loader = getLoader();
        
        loader.setup(DatabaseAccessType.EMBEDDED, getConfigData());
        
        LOGGER.info("Loading file <" + file.getName() + "> to dataset <" + rootName + ">");
        long before = System.currentTimeMillis();
        loader.load();
        long after = System.currentTimeMillis();
        LOGGER.info("Loading finished in " + (after - before) + " milliseconds");        
    }
    
    protected abstract ILoader<?, CommonConfigData> getLoader();
    
    protected CommonConfigData getConfigData() {
        CommonConfigData config = new CommonConfigData();
        config.setRoot(file);
        config.setDbRootName(rootName);
        config.setProjectName(projectName);
        
        return config;
    }

}
