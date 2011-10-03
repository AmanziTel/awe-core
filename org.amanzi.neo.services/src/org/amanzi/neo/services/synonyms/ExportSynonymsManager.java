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

package org.amanzi.neo.services.synonyms;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.synonyms.ExportSynonymsService.ExportSynonyms;
import org.apache.log4j.Logger;

/**
 * Manager to work with Export Synonyms 
 * @author lagutko_n
 * @since 1.0.0
 */
public class ExportSynonymsManager {
    
    private static final Logger LOGGER = Logger.getLogger(ExportSynonymsManager.class);
    
    /*
     * Instance of this Manager
     */
    private static ExportSynonymsManager manager = null;
    
    
    
    /**
     * Private constructor - hide it from another classes
     */
    private ExportSynonymsManager() {
        //do nothing
    }
    
    /**
     * Returns instance of this Manager
     *
     * @return
     */
    public static ExportSynonymsManager getManager() {
        if (manager == null) {
            manager = new ExportSynonymsManager();
        }
        
        return manager;
    }
    
    /**
     * Returns Synonym on Export 
     * 
     * First will try to find it in DATASET synonyms, if it not found - will try to find in GLOBAL synonyms, otherwise will just return original property name
     *
     * @param datasetName name of exported Dataset 
     * @param nodeType type of exported Node 
     * @param propertyName name of Property to export
     * @return header for export 
     */
    public String getExportHeader(String datasetName, INodeType nodeType, String propertyName) {
        LOGGER.debug("start getExportHeader(<" + datasetName + ">, <" + nodeType + ">, <" + propertyName + ">)");
        
        LOGGER.info("Using original propertyName");
        LOGGER.debug("finish getExportHeader");
        return propertyName;
    }

}
