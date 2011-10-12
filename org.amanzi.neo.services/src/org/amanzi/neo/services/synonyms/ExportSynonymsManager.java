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

import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataModel;
import org.amanzi.neo.services.synonyms.ExportSynonymsService.ExportSynonyms;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Manager to work with Export Synonyms
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public class ExportSynonymsManager {

    private static final Logger LOGGER = Logger.getLogger(ExportSynonymsManager.class);

    /*
     * Instance of this Manager
     */
    private static ExportSynonymsManager manager = null;

    private ExportSynonymsService synonymsService = null;

    /**
     * Private constructor - hide it from another classes
     */
    private ExportSynonymsManager() {
        synonymsService = NeoServiceFactory.getInstance().getExportSynonymsService();
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
     * Method to initizlied ExportSynonymsService in tests
     * 
     * @param service
     */
    static void initializeService(ExportSynonymsService service) {
        manager.synonymsService = service;
    }

    /**
     * Returns Synonym on Export First will try to find it in DATASET synonyms, if it not found -
     * will try to find in GLOBAL synonyms, otherwise will just return original property name
     * 
     * @param datasetName name of exported Dataset
     * @param nodeType type of exported Node
     * @param propertyName name of Property to export
     * @return header for export
     */
    public String getExportHeader(IDataModel dataModel, INodeType nodeType, String propertyName) throws DatabaseException {
        LOGGER.debug("start getExportHeader(<" + dataModel + ">, <" + nodeType + ">, <" + propertyName + ">)");

        // validate input parameters
        if (dataModel == null) {
            LOGGER.error("Input DataModel is null");
            throw new IllegalArgumentException("Input DataModel is null");
        }
        if (nodeType == null) {
            LOGGER.error("Input NodeType is null");
            throw new IllegalArgumentException("Input NodeType is null");
        }
        if ((propertyName == null) || (propertyName.equals(StringUtils.EMPTY))) {
            LOGGER.error("PropertyName is null or empty");
            throw new IllegalArgumentException("PropertyName is null or empty");
        }

        ExportSynonyms synonyms = synonymsService.getDatasetExportSynonyms(dataModel.getRootNode());
        String outputSynonym = synonyms.getSynonym(nodeType, propertyName);

        if (outputSynonym == null) {
            LOGGER.debug("No Dataset Synonym. Trying Global Synonyms");

            synonyms = synonymsService.getGlobalExportSynonyms();
            outputSynonym = synonyms.getSynonym(nodeType, propertyName);

            if (outputSynonym == null) {
                LOGGER.debug("No Global Synonym. Using original propertyName");
                outputSynonym = propertyName;
            } else {
                LOGGER.debug("Found in Global Synonyms");
            }
        } else {
            LOGGER.debug("Found in Dataset Synonyms");
        }

        LOGGER.debug("finish getExportHeader");
        return outputSynonym;
    }

    /**
     * try to find export synonyms node in database. if node not found that create new node
     * 
     * @param model
     * @return ExportSynonyms
     * @throws DatabaseException
     */
    public ExportSynonyms createExportSynonym(IDataModel model) throws DatabaseException {
        // validate input parameters
        if (model == null) {
            LOGGER.error("Input DataModel is null");
            throw new IllegalArgumentException("Input DataModel is null");
        }
        ExportSynonyms synonyms = synonymsService.getDatasetExportSynonyms(model.getRootNode());
        return synonyms;
    }

    public void saveExportSynonyms(IDataModel model, ExportSynonyms synonyms) throws DatabaseException {
        synonymsService.saveExportSynonyms(model.getRootNode(), synonyms);
    }
}
