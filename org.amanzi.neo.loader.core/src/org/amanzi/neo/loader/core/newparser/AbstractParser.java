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

import java.io.File;
import java.util.List;

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.newsaver.IData;
import org.amanzi.neo.loader.core.newsaver.ISaver;
import org.amanzi.neo.services.model.IModel;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public abstract class AbstractParser<T1 extends ISaver<? extends IModel, T3, T2>, T2 extends IConfiguration, T3 extends IData> implements IParser<T1, T2, T3> {
    
    /*
     * Configuraton data of Parser
     */
    protected T2 config;
    
    /*
     * Savers for Data
     */
    protected List<T1> savers;
    
    /*
     * Currently parsed file
     */
    protected File currentFile;

    @Override
    public void init(T2 configuration, List<T1> saver) {
        this.config = configuration;
        this.savers = saver;
    }
    
    /**
     * Parses single IData element
     *
     * @return next parsed IData object, or null in case if parsing finished
     */
    protected abstract T3 parseElement();
    
    /**
     * Parses single file
     *
     * @param file
     */
    protected void parseFile(File file) {
        T3 element = parseElement();
        
        while (element != null) {
            for (ISaver<?, T3, T2> saver : savers) {
                saver.saveElement(element);
            }
        }
    }

    @Override
    public void run() {
        T3 element = parseElement();
        
        while (element != null) {
            for (ISaver<?, T3, T2> saver : savers) {
                saver.saveElement(element);
            }
        }
    }
    
    

   

}
