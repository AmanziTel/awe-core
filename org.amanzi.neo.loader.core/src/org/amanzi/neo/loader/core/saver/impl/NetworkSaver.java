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

package org.amanzi.neo.loader.core.saver.impl;

import java.io.File;
import java.util.Map;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.loader.core.IMappedStringData;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.internal.LoaderCorePlugin;
import org.amanzi.neo.loader.core.saver.impl.internal.AbstractSynonymsSaver;
import org.amanzi.neo.loader.core.synonyms.SynonymsManager;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.network.NetworkElementType;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.INetworkNodeProperties;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NetworkSaver extends AbstractSynonymsSaver<IConfiguration> {

    public static final String SYNONYMS_TYPE = "network";

    private final INetworkModelProvider networkModelProvider;

    private INetworkModel networkModel;

    private final IGeneralNodeProperties generalNodeProperties;

    private final INetworkNodeProperties networkNodeProperties;

    public NetworkSaver() {
        this(LoaderCorePlugin.getInstance().getProjectModelProvider(), LoaderCorePlugin.getInstance().getNetworkModelProvider(),
                SynonymsManager.getInstance(), LoaderCorePlugin.getInstance().getGeneralNodeProperties(), LoaderCorePlugin
                        .getInstance().getNetworkNodeProperties());
    }

    protected NetworkSaver(final IProjectModelProvider projectModelProvider, final INetworkModelProvider networkModelProvider,
            final SynonymsManager synonymsManager, final IGeneralNodeProperties generalNodeProperties,
            final INetworkNodeProperties networkNodeProperties) {
        super(projectModelProvider, synonymsManager);
        this.networkModelProvider = networkModelProvider;
        this.generalNodeProperties = generalNodeProperties;
        this.networkNodeProperties = networkNodeProperties;
    }

    protected INetworkModel createNetworkModel(final String networkName) throws ModelException {
        INetworkModel model = networkModelProvider.create(getCurrentProject(), networkName);
        addProcessedModel(model);

        return model;
    }

    private IDataElement findElement(final NetworkElementType elementType, final String elementName) throws ModelException {
        return networkModel.findElement(elementType, elementName);
    }

    private IDataElement findSector(final String elementName, final Map<String, Object> properties) throws ModelException {
        Integer ci = (Integer)properties.get(networkNodeProperties.getCIProperty());
        Integer lac = (Integer)properties.get(networkNodeProperties.getLACProperty());
        return networkModel.findSector(elementName, ci, lac);
    }

    @Override
    public void finishUp() {
        try {
            networkModel.createSynonyms(getSynonymsMap());
        } catch (ModelException e) {
        } finally {
            super.finishUp();
        }

    }

    @Override
    protected String getSynonymsType() {
        return SYNONYMS_TYPE;
    }

    @Override
    public void init(final IConfiguration configuration) throws ModelException {
        super.init(configuration);

        networkModel = createNetworkModel(configuration.getDatasetName());
    }

    @Override
    public void onFileParsingStarted(final File file) {
        // do nothing
    }

    // TODO: shouldn't throw exception
    @Override
    protected void saveInModel(final IMappedStringData dataElement) throws ModelException {
        IDataElement parent = networkModel.asDataElement();

        for (NetworkElementType elementType : NetworkElementType.getGeneralNetworkElements()) {
            Map<String, Object> properties = getElementProperties(elementType, dataElement,
                    elementType == NetworkElementType.SECTOR);

            if (!properties.isEmpty()) {
                String elementName = (String)properties.get(generalNodeProperties.getNodeNameProperty());
                if (elementName != null) {
                    IDataElement child = null;
                    switch (elementType) {
                    case SECTOR:
                        child = findSector(elementName, properties);
                        break;
                    default:
                        child = findElement(elementType, elementName);
                        break;
                    }

                    if (child == null) {
                        child = networkModel.createElement(elementType, parent, elementName, properties);
                    }

                    parent = child;
                } else {
                    // TODO: error!!!
                }
            }
        }
    }

}
