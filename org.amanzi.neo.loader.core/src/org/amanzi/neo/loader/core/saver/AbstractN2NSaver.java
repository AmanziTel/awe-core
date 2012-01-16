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

import java.util.Map;

import org.amanzi.neo.loader.core.config.IConfiguration;
import org.amanzi.neo.loader.core.parser.MappedData;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsType;

/**
 * Abstract Loader for N2N Relationship Data Type
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public abstract class AbstractN2NSaver extends AbstractNetworkSaver<INodeToNodeRelationsModel, IConfiguration> {
    
    /*
     * Name of Dataset Synonyms
     */
    private static final String SYNONYMS_DATASET_TYPE = "n2n";

    @Override
    public void saveElement(MappedData dataElement) throws AWEException {
        Map<String, Object> values = getDataElementProperties(getMainModel(), null, dataElement, true);

        IDataElement servingElement = getNetworkElement(getN2NNodeType(), "serving_name", values);
        IDataElement targetElement = getNetworkElement(getN2NNodeType(), "target_element", values);

        getMainModel().linkNode(servingElement, targetElement, values);
    }

    @Override
    protected boolean isRenderable() {
        return false;
    }

    @Override
    protected INodeToNodeRelationsModel createMainModel(IConfiguration configuration) throws AWEException {
        networkModel = getActiveProject().getNetwork(configuration.getDatasetName());
        
        String n2nName = configuration.getFilesToLoad().get(0).getName();

        return networkModel.getNodeToNodeModel(getN2NType(), n2nName, getN2NNodeType());
    }

    /**
     * Returns Type of NodeToNode Relations
     * 
     * @return
     */
    protected abstract INodeToNodeRelationsType getN2NType();

    /**
     * 
     */
    protected abstract INodeType getN2NNodeType();

    @Override
    protected String getDatasetType() {
        return SYNONYMS_DATASET_TYPE;
    }

    @Override
    protected String getSubType() {
        return null;
    }

}
