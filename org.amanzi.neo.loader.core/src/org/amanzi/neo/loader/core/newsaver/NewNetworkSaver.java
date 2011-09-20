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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.newparser.NetworkRowContainer;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceManager;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * network saver
 * 
 * @author Kondratenko_Vladislav
 */
public class NewNetworkSaver implements ISaver {
    private Long lineCounter = 0l;
    private NetworkModel model;
    private DataLoadPreferenceManager preferenceManager;
    private List<String> existsCILAC = new LinkedList<String>();
    private String CI_LAC = "CI_LAC";
    /**
     * contains appropriation of header synonyms and name inDB</br> <b>key</b>- name in db ,
     * <b>value</b>-file header key
     */
    private Map<String, String> fileSynonyms = new HashMap<String, String>();
    /**
     * name inDB properties values
     */
    private Map<String, Object> nameInDb = new HashMap<String, Object>();
    private static Logger LOGGER = Logger.getLogger(NewNetworkSaver.class);

    /**
     * find or create BSC node from row properties and pass the action down the chain, for creation
     * CIY->SITE->SECTOR structure
     * 
     * @param row
     */
    private void createBSC(Map<String, Object> row) {
        Map<String, Object> bscProperty = new HashMap<String, Object>();
        if (!nameInDb.containsKey(DataLoadPreferenceManager.BSC) || nameInDb.get(DataLoadPreferenceManager.BSC) == null) {
            createSite(null, row);
        }
        bscProperty.put(INeoConstants.PROPERTY_TYPE_NAME, DataLoadPreferenceManager.BSC);
        bscProperty.put(INeoConstants.PROPERTY_NAME_NAME, nameInDb.get(DataLoadPreferenceManager.BSC));

        IDataElement bscElement = new DataElement(bscProperty);
        IDataElement findedElement = model.findElement(bscElement);
        if (findedElement == null) {
            findedElement = bscElement;
            model.createElement(new DataElement(model.getRootNode()), bscElement);
        }
        row.remove(fileSynonyms.get(DataLoadPreferenceManager.BSC));
        createCity(findedElement, row);
    }

    /**
     * find or create city node from row properties and pass the action down the chain, for creation
     * SITE->SECTOR nodes
     * 
     * @param row
     */
    private void createCity(IDataElement root, Map<String, Object> row) {
        if (nameInDb.get(DataLoadPreferenceManager.CITY) == null
                || StringUtils.isEmpty(nameInDb.get(DataLoadPreferenceManager.CITY).toString())) {
            if (root == null) {
                LOGGER.info("Missing city name on line:" + lineCounter);
                return;
            } else {
                createSite(root, row);
                return;
            }
        }
        Map<String, Object> cityPropMap = new HashMap<String, Object>();

        cityPropMap.put(INeoConstants.PROPERTY_TYPE_NAME, DataLoadPreferenceManager.CITY);
        cityPropMap.put(INeoConstants.PROPERTY_NAME_NAME, nameInDb.get(DataLoadPreferenceManager.CITY));

        IDataElement cityElement = new DataElement(cityPropMap);
        IDataElement findedElement = model.findElement(cityElement);
        if (findedElement == null) {
            findedElement = cityElement;
            if (root == null) {
                model.createElement(new DataElement(model.getRootNode()), cityElement);
            } else {
                model.createElement(model.findElement(root), cityElement);
            }
        }
        row.remove(fileSynonyms.get(DataLoadPreferenceManager.CITY));
        createSite(findedElement, row);
    }

    /**
     * find or create site node and pass the action down the chain for creation sector nodes.
     * 
     * @param city node
     * @param row
     */
    private void createSite(IDataElement root, Map<String, Object> row) {
        if (nameInDb.get(DataLoadPreferenceManager.SITE) == null
                || StringUtils.isEmpty(nameInDb.get(DataLoadPreferenceManager.SITE).toString())) {
            LOGGER.info("Missing site name on line:" + lineCounter);
            return;
        }

        Map<String, Object> siteMap = new HashMap<String, Object>();
        siteMap.put(INeoConstants.PROPERTY_TYPE_NAME, DataLoadPreferenceManager.SITE);
        siteMap.put(INeoConstants.PROPERTY_NAME_NAME, nameInDb.get(DataLoadPreferenceManager.SITE));
        siteMap.put(INeoConstants.PROPERTY_LON_NAME, nameInDb.get(INeoConstants.PROPERTY_LON_NAME));
        siteMap.put(INeoConstants.PROPERTY_LAT_NAME, nameInDb.get(INeoConstants.PROPERTY_LAT_NAME));

        IDataElement siteElement = new DataElement(siteMap);
        IDataElement findedElement = model.findElement(siteElement);

        if (findedElement == null || !model.getParentElement(findedElement).equals(root)) {
            findedElement = siteElement;
            model.createElement(model.findElement(root), siteElement);
        }
        row.remove(fileSynonyms.get(DataLoadPreferenceManager.SITE));
        row.remove(fileSynonyms.get(INeoConstants.PROPERTY_LON_NAME));
        row.remove(fileSynonyms.get(INeoConstants.PROPERTY_LAT_NAME));
        createSector(findedElement, row);
    }

    /**
     * close the chain with creation of sector
     * 
     * @param findedElement site node
     * @param row
     */
    private void createSector(IDataElement root, Map<String, Object> row) {
        Map<String, Object> sectorMap = new HashMap<String, Object>();
        for (String head : row.keySet()) {
            sectorMap.put(head.toLowerCase(), row.get(head));
        }
        if (fileSynonyms.containsKey(DataLoadPreferenceManager.SECTOR)) {
            sectorMap.put(INeoConstants.PROPERTY_NAME_NAME, nameInDb.get(DataLoadPreferenceManager.SECTOR));
        }
        sectorMap.put(INeoConstants.PROPERTY_TYPE_NAME, DataLoadPreferenceManager.SECTOR);
        IDataElement sectorElement = new DataElement(sectorMap);
        IDataElement findedElement = model.findElement(sectorElement);
        if (sectorMap.containsKey("ci") && sectorMap.containsKey("lac")) {
            if (findedElement == null) {
                model.createElement(model.findElement(root), sectorElement);
            } else {
                LOGGER.info("sector " + sectorMap.get(CI_LAC) + " is alreade exist; line: " + lineCounter);
            }
        } else {
            LOGGER.info("invalid sector parameters on line: " + lineCounter);
        }

    }

    @Override
    public void init(IConfiguration configuration, IData dataElement) {
        preferenceManager = new DataLoadPreferenceManager();
        Map<String, Object> rootElement = new HashMap<String, Object>();

        rootElement.put("project", new DatasetService().findOrCreateAweProject(configuration.getDatasetNames().get("Project")));
        rootElement.put(INeoConstants.PROPERTY_NAME_NAME, configuration.getDatasetNames().get("Network"));
        rootElement.put(INeoConstants.PROPERTY_TYPE_NAME, DatasetTypes.NETWORK.getId());
        model = new NetworkModel(new DataElement(rootElement));
    }

    @Override
    public void saveElement(IData dataElement) {

        if (dataElement instanceof NetworkRowContainer) {
            NetworkRowContainer container = (NetworkRowContainer)dataElement;
            if (fileSynonyms.isEmpty()) {
                makeAppropriationWithSynonyms(container.getHeaderMap().keySet());
                lineCounter++;
            } else {
                lineCounter++;
                for (String fileHead : container.getHeaderMap().keySet()) {
                    if (fileSynonyms.containsKey(fileHead)) {
                        nameInDb.put(fileSynonyms.get(fileHead), container.getHeaderMap().get(fileHead));
                    }
                }
                createBSC(container.getHeaderMap());
            }
        }

    }

    /**
     * make Appropriation with default synonyms and file header
     * 
     * @param keySet -header files;
     */
    private void makeAppropriationWithSynonyms(Set<String> keySet) {
        boolean isAppropriation = false;
        for (String header : keySet) {
            for (String posibleHeader : preferenceManager.getNetworkPosibleValues().keySet()) {
                for (String mask : preferenceManager.getNetworkPosibleValues().get(posibleHeader)) {
                    if (header.toLowerCase().matches(mask.toLowerCase())) {
                        isAppropriation = true;
                        fileSynonyms.put(posibleHeader, header);
                        break;
                    }
                }
                if (isAppropriation) {
                    isAppropriation = false;
                    break;
                }
            }
        }
    }

    @Override
    public void finishUp() {
    }

}
