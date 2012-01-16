package org.amanzi.neo.loader.core.saver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
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
public class AntennaSaver extends AbstractMappedDataSaver<INetworkModel, ConfigurationDataImpl> {

    /*
     * Name of Dataset Synonyms
     */
    private static final String SYNONYMS_DATASET_TYPE = "network";

    @Override
    public void saveElement(MappedData dataElement) throws AWEException {
        IDataElement site = null;
        String elementName = dataElement.get(NetworkService.NAME);
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
        getMainModel().completeProperties(site, map, true);
    }

    @Override
    protected boolean isRenderable() {
        return false;
    }

    @Override
    protected INetworkModel createMainModel(ConfigurationDataImpl configuration) throws AWEException {
        return getActiveProject().getNetwork(configuration.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME));
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
