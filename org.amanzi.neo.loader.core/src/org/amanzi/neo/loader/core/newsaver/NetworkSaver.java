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
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
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
public class NetworkSaver extends AbstractCSVSaver<NetworkModel> {

    // TODO: LN: comments
    private final String CI_LAC = "CI_LAC";

    private final static String CITY = NetworkElementNodeType.CITY.getId();
    private final static String BSC = NetworkElementNodeType.BSC.getId();
    private final static String MSC = NetworkElementNodeType.MSC.getId();
    private final static String SECTOR = NetworkElementNodeType.SECTOR.getId();
    private final static String SITE = NetworkElementNodeType.SITE.getId();
    private final static String[] DEFAULT_NETWORK_STRUCTURE = {CITY, MSC, BSC, SITE, SECTOR};
    private static final Logger LOGGER = Logger.getLogger(NetworkSaver.class);

    protected NetworkSaver(INetworkModel model, ConfigurationDataImpl config, GraphDatabaseService service) {
        super(service);
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.NETWORK);
        columnSynonyms = new HashMap<String, Integer>();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        if (model != null) {
            this.networkModel = model;
            rootDataElement = new DataElement(model.getRootNode());
            modelMap.put(model.getName(), model);
        } else {
            init(config, null);
        }
    }

    // TODO: LN: comments
    /**
     * 
     */
    public NetworkSaver() {
        super();
    }

    /**
     * find or create city node from row properties and pass the action down the chain, for creation
     * CITY->MSC->BSC->SITE->SECTOR nodes
     * 
     * @param row
     * @throws AWEException
     */
    @Override
    protected void saveLine(List<String> row) throws AWEException {
        IDataElement parentElement = rootDataElement;

        // TODO: LN: WHY STRINGS?????????????????? we have Enum for this
        // working with switch and enums much more faster
        // than with string:
        // for (NetworkElementNodeType type : DEFAULT_NETWORK_STRUCTURE) {
        // switch (type) {
        // case MSC:
        // case BSC:
        // case CITY:
        // ..
        // break;
        // case SECTOR:
        // ..
        // break;
        for (String stuctureElement : DEFAULT_NETWORK_STRUCTURE) {
            if (stuctureElement.equals(CITY) || stuctureElement.equals(MSC) || stuctureElement.equals(BSC)) {
                if (isCorrect(stuctureElement, row)) {
                    // create city msc bsc elements
                    parentElement = createMainElements(row, parentElement, NodeTypeManager.getType(stuctureElement),
                            stuctureElement);
                }
            } else if (stuctureElement.equals(SITE)) {
                parentElement = createSite(parentElement, row);
            } else {
                createSector(parentElement, row);
            }
        }
    }

    /**
     * find or create site node and pass the action down the chain for creation sector nodes.
     * 
     * @param city node
     * @param row
     * @throws AWEException
     */
    private IDataElement createSite(IDataElement root, List<String> row) throws AWEException {
        if (!isCorrect(INetworkModel.LATITUDE, row) || !isCorrect(INetworkModel.LONGITUDE, row)) {
            LOGGER.info("Missing site name on line:" + lineCounter);
            return null;
        }

        Map<String, Object> siteMap = new HashMap<String, Object>();
        if (!collectSite(siteMap, row)) {
            return null;
        }

        IDataElement findedElement;
        findedElement = networkModel.findElement(siteMap);
        if (findedElement == null) {
            findedElement = networkModel.createElement(root, siteMap);
            // add synonyms
            addedDatasetSynonyms(networkModel, NetworkElementNodeType.SITE, NewAbstractService.NAME,
                    columnSynonyms.get(fileSynonyms.get(SITE)) == null ? SITE : getHeaderBySynonym(SITE));
            addSynonyms(networkModel, siteMap);
        }
        resetRowValueBySynonym(row, INetworkModel.LONGITUDE);
        resetRowValueBySynonym(row, INetworkModel.LATITUDE);
        return findedElement;
    }

    /**
     * close the chain with creation of sector if sector was found - pass to next line
     * 
     * @param findedElement site node
     * @param row
     * @throws AWEException
     */
    private void createSector(IDataElement root, List<String> row) throws AWEException {
        if (root == null) {
            LOGGER.info("there is no parent element for sector on line: " + lineCounter);
            return;
        }
        if (!isCorrect(SECTOR, row)) {
            return;
        }
        Map<String, Object> sectorMap = new HashMap<String, Object>();
        if (!collectSector(sectorMap, row)) {
            return;
        }

        IDataElement findedElement = networkModel.findElement(sectorMap);
        if (findedElement == null) {
            networkModel.createElement(root, sectorMap);
            addSynonyms(networkModel, sectorMap);
        } else {
            // TODO: LN: use also Name
            LOGGER.info("sector" + sectorMap.get(CI_LAC.toLowerCase()) + " is already exist;line: " + lineCounter);
        }
    }

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
        // TODO: LN: use findElementByPropertyValue
        findedElement = networkModel.findElement(mapProperty);
        if (findedElement == null) {
            if (root != null) {
                findedElement = networkModel.createElement(root, mapProperty);
            } else {
                // TODO: LN: refactor rootDataElement - in case of
                findedElement = networkModel.createElement(rootDataElement, mapProperty);
            }
        }
        //TODO: LN: WTF? 
        addSynonyms(networkModel, mapProperty);
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
        String sector = getSynonymValueWithAutoparse(SECTOR, row).toString();
        String sectorName = isCorrect(sector) ? sector.toString() : StringUtils.EMPTY;
        String ci = sectorMap.containsKey(NewNetworkService.CELL_INDEX) ? sectorMap.get(NewNetworkService.CELL_INDEX).toString()
                : StringUtils.EMPTY;
        String lac = sectorMap.containsKey(NewNetworkService.LOCATION_AREA_CODE) ? sectorMap.get(
                NewNetworkService.LOCATION_AREA_CODE).toString() : StringUtils.EMPTY;
        //TODO: LN: we don't need this check
        //service method will check it
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
