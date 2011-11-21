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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * network saver
 * 
 * @author Kondratenko_Vladislav
 */
public class NewNetworkSaver extends AbstractCSVSaver<NetworkModel> {
    private static Logger LOGGER = Logger.getLogger(NewNetworkSaver.class);
    private Long lineCounter = 0l;
    private INetworkModel model;
    private final String CI_LAC = "CI_LAC";
    private IDataElement rootDataElement;
    private final int MAX_TX_BEFORE_COMMIT = 1000;
    private final String CITY = "city";
    private final String BSC = "bsc";
    private final String MSC = "msc";
    private final String SECTOR = "sector";
    private final String SITE = "site";

    protected NewNetworkSaver(INetworkModel model, ConfigurationDataImpl config, GraphDatabaseService service) {
        super(service);
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.NETWORK);
        columnSynonyms = new HashMap<String, Integer>();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        if (model != null) {
            this.model = model;
            rootDataElement = new DataElement(model.getRootNode());
            modelMap.put(model.getName(), model);
        } else {
            init(config, null);
        }
    }

    /**
     * 
     */
    public NewNetworkSaver() {
        super();
    }

    /**
     * find or create city node from row properties and pass the action down the chain, for creation
     * CITY->MSC->BSC->SITE->SECTOR nodes
     * 
     * @param row
     * @throws AWEException
     */
    private void createCity(List<String> row) throws AWEException {
        if (!isCorrect(CITY, row)) {
            createMSC(null, row);
            return;
        }

        IDataElement findedElement = createMainElements(row, rootDataElement, NetworkElementNodeType.CITY, CITY);
        createMSC(findedElement, row);
    }

    /**
     * find or create BSC node from row properties and pass the action down the chain, for creation
     * MSC->BSC->SITE->SECTOR nodes structure
     */
    private void createMSC(IDataElement root, List<String> row) throws AWEException {
        if (!isCorrect(MSC, row)) {
            if (root == null) {
                createSite(rootDataElement, row);
            } else {
                createSite(root, row);
            }
            return;
        }

        IDataElement findedElement = createMainElements(row, root, NetworkElementNodeType.MSC, MSC);
        createBSC(findedElement, row);
    }

    /**
     * find or create BSC node from row properties and pass the action down the chain, for creation
     * BSC->SITE->SECTOR nodes structure
     * 
     * @param row
     * @throws AWEException
     */
    private void createBSC(IDataElement root, List<String> row) throws AWEException {
        if (!isCorrect(BSC, row)) {
            if (root == null) {
                createSite(null, row);
            } else {
                createSite(root, row);
            }
            return;
        }
        IDataElement findedElement = createMainElements(row, root, NetworkElementNodeType.BSC, BSC);
        createSite(findedElement, row);
    }

    /**
     * find or create site node and pass the action down the chain for creation sector nodes.
     * 
     * @param city node
     * @param row
     * @throws AWEException
     */
    private void createSite(IDataElement root, List<String> row) throws AWEException {
        if (!isCorrect(INetworkModel.LATITUDE, row) || !isCorrect(INetworkModel.LONGITUDE, row)) {
            LOGGER.info("Missing site name on line:" + lineCounter);
            return;
        }

        Map<String, Object> siteMap = new HashMap<String, Object>();
        if (!collectSite(siteMap, row)) {
            return;
        }

        IDataElement findedElement;
        findedElement = model.findElement(siteMap);
        if (findedElement == null) {
            findedElement = model.createElement(root, siteMap);
            addedDatasetSynonyms(model, NetworkElementNodeType.SITE, NewAbstractService.NAME,
                    columnSynonyms.get(fileSynonyms.get(SITE)) == null ? SITE : getHeaderBySynonym(SITE));
            addedDatasetSynonyms(model, NetworkElementNodeType.SITE, INetworkModel.LONGITUDE,
                    getHeaderBySynonym(INetworkModel.LONGITUDE));
            addedDatasetSynonyms(model, NetworkElementNodeType.SITE, INetworkModel.LATITUDE,
                    getHeaderBySynonym(INetworkModel.LATITUDE));
        }
        resetRowValueBySynonym(row, INetworkModel.LONGITUDE);
        resetRowValueBySynonym(row, INetworkModel.LATITUDE);
        createSector(findedElement, row);
    }

    /**
     * close the chain with creation of sector if sector was found - pass to next line
     * 
     * @param findedElement site node
     * @param row
     * @throws AWEException
     */
    private void createSector(IDataElement root, List<String> row) throws AWEException {
        if (!isCorrect(SECTOR, row)) {
            return;
        }
        Map<String, Object> sectorMap = new HashMap<String, Object>();
        if (!collectSector(sectorMap, row)) {
            return;
        }

        IDataElement findedElement = model.findElement(sectorMap);
        if (findedElement == null) {
            model.createElement(root, sectorMap);
            addedDatasetSynonyms(model, NetworkElementNodeType.SECTOR, NewAbstractService.NAME, getHeaderBySynonym(SECTOR));
        } else {
            LOGGER.info("sector" + sectorMap.get(CI_LAC.toLowerCase()) + " is already exist;line: " + lineCounter);
        }
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        Map<String, Object> rootElement = new HashMap<String, Object>();
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.NETWORK);
        columnSynonyms = new HashMap<String, Integer>();
        setDbInstance();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        try {
            rootElement.put(NewAbstractService.NAME,
                    configuration.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME));
            model = getActiveProject().getNetwork(configuration.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME));
            rootDataElement = new DataElement(model.getRootNode());
            modelMap.put(configuration.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME), model);
            createExportSynonymsForModels();
        } catch (AWEException e) {
            rollbackTx();
            LOGGER.error("Exception on creating root Model", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveElement(CSVContainer dataElement) {
        commitTx();
        CSVContainer container = dataElement;
        try {
            if (fileSynonyms.isEmpty()) {
                headers = container.getHeaders();
                makeAppropriationWithSynonyms(headers);
                makeIndexAppropriation();
                lineCounter++;
            } else {
                lineCounter++;
                List<String> value = container.getValues();
                createCity(value);
            }
        } catch (DatabaseException e) {
            LOGGER.error("Error while saving element on line " + lineCounter, e);
            rollbackTx();
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (Exception e) {
            LOGGER.error("Exception while saving element on line " + lineCounter, e);
            commitTx();
        }
    }

    // /**
    // * Checks property for null or for empty
    // *
    // * @param row
    // * @param propertyName
    // * @return true if it is null or is empty
    // */
    // private boolean isCorrect(List<String> row, String propertyName) {
    // if (propertyName == null || getSynonymValue(row, propertyName) == null
    // || StringUtils.isEmpty(getSynonymValue(row, propertyName))) {
    // return false;
    // }
    // return true;
    // }

    /**
     * Create main elements - city, msc, bsc
     * 
     * @param row
     * @param root
     * @param nodeType
     * @param type
     * @return element
     * @throws AWEException
     */
    private IDataElement createMainElements(List<String> row, IDataElement root, INodeType nodeType, String type)
            throws AWEException {

        Map<String, Object> mapProperty = new HashMap<String, Object>();
        collectMainElements(mapProperty, row, nodeType, type);

        IDataElement findedElement;
        findedElement = model.findElement(mapProperty);
        if (findedElement == null) {
            if (root != null) {
                findedElement = model.createElement(root, mapProperty);
            } else {
                findedElement = model.createElement(rootDataElement, mapProperty);
            }
            addedDatasetSynonyms(model, nodeType, NewAbstractService.NAME, getHeaderBySynonym(type));
        }
        resetRowValueBySynonym(row, type);
        return findedElement;
    }

    /**
     * Collect main elements - city, msc, bsc
     * 
     * @param mapProperty
     * @param row
     * @param nodeType
     * @param type
     */
    private void collectMainElements(Map<String, Object> mapProperty, List<String> row, INodeType nodeType, String type) {
        mapProperty.put(NewAbstractService.TYPE, nodeType.getId());
        mapProperty.put(NewAbstractService.NAME, getSynonymValueWithAutoparse(type, row));
    }

    /**
     * Collect site
     * 
     * @param siteMap
     * @param row
     * @return true if site is collected
     */
    private boolean collectSite(Map<String, Object> siteMap, List<String> row) {
        siteMap.put(NewAbstractService.TYPE, NetworkElementNodeType.SITE.getId());
        siteMap.put(INetworkModel.LONGITUDE, getSynonymValueWithAutoparse(INetworkModel.LONGITUDE, row));
        siteMap.put(INetworkModel.LATITUDE, getSynonymValueWithAutoparse(INetworkModel.LATITUDE, row));
        String siteName;
        if (!isCorrect(SITE, row)) {
            if (isCorrect(SECTOR, row)) {
                siteName = getSynonymValueWithAutoparse(SECTOR, row).toString();
                siteMap.put(NewAbstractService.NAME,
                        autoParse(NewAbstractService.NAME, siteName.substring(0, siteName.length() - 1)));
            } else {
                LOGGER.info("Missing site name based on SectorName on line:" + lineCounter);
                return false;
            }

        } else {
            siteName = getSynonymValueWithAutoparse(SITE, row).toString();
            siteMap.put(NewAbstractService.NAME, siteName);
            resetRowValueBySynonym(row, SITE);
        }
        return true;
    }

    /**
     * Collect sector
     * 
     * @param sectorMap
     * @param row
     * @return true if sector is collected
     */
    private boolean collectSector(Map<String, Object> sectorMap, List<String> row) {

        for (String head : headers) {
            if (isCorrect(head, row) && !head.equals(fileSynonyms.get(SECTOR))) {
                sectorMap.put(head.toLowerCase(), getSynonymValueWithAutoparse(head, row));
                if (fileSynonyms.containsValue(head)) {
                    for (String key : fileSynonyms.keySet()) {
                        if (head.equals(fileSynonyms.get(key))) {
                            addedDatasetSynonyms(model, NetworkElementNodeType.SECTOR, key, head);
                        }
                    }
                }
            }
        }
        String sector = getSynonymValueWithAutoparse(SECTOR, row).toString();
        String sectorName = isCorrect(sector) ? sector.toString() : StringUtils.EMPTY;
        String ci = sectorMap.containsKey(NewNetworkService.CELL_INDEX) ? sectorMap.get(NewNetworkService.CELL_INDEX).toString()
                : StringUtils.EMPTY;
        String lac = sectorMap.containsKey(NewNetworkService.LOCATION_AREA_CODE) ? sectorMap.get(
                NewNetworkService.LOCATION_AREA_CODE).toString() : StringUtils.EMPTY;
        if ((!isCorrect(sectorName)) && (!isCorrect(ci) || !isCorrect(lac))) {
            LOGGER.info("Sector should have Name or CI + LAC properties on line: " + lineCounter);
            return false;
        }
        if (fileSynonyms.containsKey(SECTOR)) {
            sectorMap.put(NewAbstractService.NAME, sectorName);
        }
        sectorMap.put(NewAbstractService.TYPE, (NetworkElementNodeType.SECTOR.getId()));
        return true;
    }

}
