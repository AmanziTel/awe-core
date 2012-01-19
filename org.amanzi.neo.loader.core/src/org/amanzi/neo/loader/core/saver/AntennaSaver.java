package org.amanzi.neo.loader.core.saver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.loader.core.config.AntennaConfiguration;
import org.amanzi.neo.loader.core.config.NetworkConfiguration;
import org.amanzi.neo.loader.core.parser.MappedData;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;

/**
 * TODO Purpose of
 * <p>
 * Saver for antenna data
 * </p>
 * 
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class AntennaSaver extends AbstractMappedDataSaver<INetworkModel, AntennaConfiguration> {

    /*
     * Name of Dataset Synonyms for antenna patterns
     */
    private static final String SYNONYMS_DATASET_TYPE = "network";

    private static final String TILT = "tilt";

    @Override
    public void saveElement(MappedData dataElement) throws AWEException {
        IDataElement site = null;
        String elementName = dataElement.get("name");
        Set<IDataElement> searchResult = getMainModel().findElementByPropertyValue(NetworkElementNodeType.SITE,
                NetworkService.NAME, elementName);

        if (!searchResult.isEmpty()) {
            if (searchResult.size() > 1) {
                throw new DuplicateNodeNameException(elementName, NetworkElementNodeType.SITE);
            } else {
                site = searchResult.iterator().next();
            }
        }
        if (site == null) {
            throw new NullPointerException("site cann't be null");
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.putAll(dataElement);

        if (site.keySet().contains(TILT)) {
            if (site.get(TILT).equals(map.get(TILT))) {
                getMainModel().completeProperties(site, map, true);
            }
        } else {
            getMainModel().completeProperties(site, map, true);
        }
    }

    @Override
    protected boolean isRenderable() {
        return false;
    }

    @Override
    protected INetworkModel createMainModel(AntennaConfiguration configuration) throws AWEException {
        return getActiveProject().getNetwork(configuration.getDatasetName());
    }

    @Override
    protected String getDatasetType() {
        return SYNONYMS_DATASET_TYPE;
    }

    @Override
    protected String getSubType() {
        return null;
    }
}
