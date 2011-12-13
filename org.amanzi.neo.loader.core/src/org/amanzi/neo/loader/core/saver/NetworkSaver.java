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

package org.amanzi.neo.loader.core.saver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * network saver
 * 
 * @author Kondratenko_Vladislav
 */
public class NetworkSaver extends AbstractNetworkSaver {
    private static final Logger LOGGER = Logger.getLogger(NetworkSaver.class);

    // Constants
    private final static int SECTOR_STRUCTURE_ID = 5;
    // Default network structure
    private final static NetworkElementNodeType[] DEFAULT_NETWORK_STRUCTURE = {NetworkElementNodeType.CITY,
            NetworkElementNodeType.MSC, NetworkElementNodeType.BSC, NetworkElementNodeType.SITE, NetworkElementNodeType.SECTOR};

    /**
     * create saver instance
     */
    public NetworkSaver() {
        super();
    }

    /**
     * Constructor for tests
     * 
     * @param model
     * @param config
     */
    NetworkSaver(INetworkModel model, ConfigurationDataImpl config) {
        preferenceStoreSynonyms = initializeSynonyms();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        if (model != null) {
            this.parametrizedModel = model;
            useableModels.add(model);
        }
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
        IDataElement parentElement = null;
        for (NetworkElementNodeType stuctureElement : DEFAULT_NETWORK_STRUCTURE) {
            switch (stuctureElement) {
            case CITY:
            case MSC:
            case BSC:
                if (isCorrect(stuctureElement.getId(), row)) {
                    // create city msc bsc elements
                    parentElement = createMainElements(row, parentElement, stuctureElement, stuctureElement.getId());
                }
                break;
            case SITE:
                parentElement = createSite(parentElement, row, stuctureElement.getId());
                break;
            case SECTOR:
                createSector(parentElement, row, stuctureElement.getId());
                break;
            }

        }
    }

    /**
     * find or create site node and pass the action down the chain for creation sector nodes.
     * 
     * @param city node
     * @param row
     * @param siteElementId
     * @throws AWEException
     */
    private IDataElement createSite(IDataElement root, List<String> row, String siteElementId) throws AWEException {
        if (!isCorrect(INetworkModel.LATITUDE, row) || !isCorrect(INetworkModel.LONGITUDE, row)) {
            LOGGER.info("Missing site name on line:" + lineCounter);
            return null;
        }

        Map<String, Object> siteMap = new HashMap<String, Object>();
        if (!collectSite(siteMap, row, siteElementId)) {
            return null;
        }

        IDataElement findedElement;
        findedElement = parametrizedModel.findElement(siteMap);
        if (findedElement == null) {
            findedElement = parametrizedModel.createElement(root, siteMap);
        }
        resetRowValueBySynonym(row, INetworkModel.LONGITUDE);
        resetRowValueBySynonym(row, INetworkModel.LATITUDE);
        addSynonyms(parametrizedModel, siteMap);
        return findedElement;
    }

    /**
     * close the chain with creation of sector if sector was found - pass to next line
     * 
     * @param findedElement site node
     * @param row
     * @param sectorElementId
     * @throws AWEException
     */
    private void createSector(IDataElement root, List<String> row, String sectorElementId) throws AWEException {
        if (root == null) {
            LOGGER.info("there is no parent element for sector on line: " + lineCounter);
            return;
        }
        if (!isCorrect(sectorElementId, row)) {
            return;
        }
        Map<String, Object> sectorMap = new HashMap<String, Object>();
        if (!collectSector(sectorMap, row, sectorElementId)) {
            return;
        }

        IDataElement findedElement = parametrizedModel.findElement(sectorMap);
        if (findedElement == null) {
            parametrizedModel.createElement(root, sectorMap);
            addSynonyms(parametrizedModel, sectorMap);
        } else {
            LOGGER.info("sector" + sectorMap + " is already exist;line: " + lineCounter);
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
        String name = getSynonymValueWithAutoparse(type, row).toString();

        Set<IDataElement> findedElement;
        findedElement = parametrizedModel.findElementByPropertyValue(nodeType, AbstractService.NAME, name);
        if (findedElement.isEmpty()) {
            Map<String, Object> mapProperty = new HashMap<String, Object>();
            collectMainElements(mapProperty, name, nodeType);

            findedElement.add(parametrizedModel.createElement(root, mapProperty));

            addSynonyms(parametrizedModel, mapProperty);
        }
        resetRowValueBySynonym(row, type);
        return findedElement.iterator().next();
    }

    /**
     * Collect main elements - city, msc, bsc
     * 
     * @param mapProperty
     * @param row
     * @param nodeType
     * @param type
     */
    private void collectMainElements(Map<String, Object> mapProperty, String name, INodeType nodeType) {
        mapProperty.put(AbstractService.TYPE, nodeType.getId());
        mapProperty.put(AbstractService.NAME, name);
    }

    /**
     * Collect site
     * 
     * @param siteMap
     * @param row
     * @param siteElementId
     * @return true if site is collected
     */
    private boolean collectSite(Map<String, Object> siteMap, List<String> row, String siteElementId) {
        siteMap.put(AbstractService.TYPE, siteElementId);
        siteMap.put(INetworkModel.LONGITUDE, getSynonymValueWithAutoparse(INetworkModel.LONGITUDE, row));
        siteMap.put(INetworkModel.LATITUDE, getSynonymValueWithAutoparse(INetworkModel.LATITUDE, row));
        String siteName;
        if (!isCorrect(siteElementId, row)) {
            if (isCorrect(DEFAULT_NETWORK_STRUCTURE[SECTOR_STRUCTURE_ID - 1].getId(), row)) {
                siteName = getSynonymValueWithAutoparse(DEFAULT_NETWORK_STRUCTURE[SECTOR_STRUCTURE_ID - 1].getId(), row).toString();
                siteMap.put(AbstractService.NAME, autoParse(AbstractService.NAME, siteName.substring(0, siteName.length() - 1)));
            } else {
                LOGGER.info("Missing site name based on SectorName on line:" + lineCounter);
                return false;
            }

        } else {
            siteName = getSynonymValueWithAutoparse(siteElementId, row).toString();
            siteMap.put(AbstractService.NAME, siteName);
            resetRowValueBySynonym(row, siteElementId);
        }
        return true;
    }

    /**
     * Collect sector
     * 
     * @param sectorMap
     * @param row
     * @param sectorElementId
     * @return true if sector is collected
     */
    private boolean collectSector(Map<String, Object> sectorMap, List<String> row, String sectorElementId) {
        for (String head : headers) {
            if (isCorrect(head, row) && !head.equals(fileSynonyms.get(sectorElementId))) {
                String synonym = getSynonymForHeader(head);
                sectorMap.put(synonym.toLowerCase(), getSynonymValueWithAutoparse(synonym, row));
            }
        }
        String sector = getSynonymValueWithAutoparse(sectorElementId, row).toString();
        String sectorName = isCorrect(sector) ? sector.toString() : StringUtils.EMPTY;
        if (fileSynonyms.containsKey(sectorElementId)) {
            sectorMap.put(AbstractService.NAME, sectorName);
        }
        sectorMap.put(AbstractService.TYPE, (NetworkElementNodeType.SECTOR.getId()));
        return true;
    }

    @Override
    protected boolean isRenderable() {
        return true;
    }

}
