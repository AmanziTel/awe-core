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

import org.amanzi.neo.loader.core.config.ISingleFileConfiguration;
import org.amanzi.neo.loader.core.parser.MappedData;
import org.amanzi.neo.loader.core.saver.neighbor.ConflictNeighboursModel;
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
public abstract class AbstractN2NSaver extends AbstractNetworkSaver<INodeToNodeRelationsModel, ISingleFileConfiguration> {

    /*
     * Name of Dataset Synonyms
     */
    private static final String SYNONYMS_DATASET_TYPE = "n2n";

    private static final String SERVING_NAME_PROPERTY = "serving_name";
    private static final String TARGET_ELEMENT_PROPERTY = "target_element";

    /**
     * Creates N2N relationship. If serving/target is missed then this pair will be stored in
     * neighbourConflicted map to transmit data to another Saver.
     */
    @Override
    public void saveElement(MappedData dataElement) throws AWEException {
        Map<String, Object> values = getDataElementProperties(getMainModel(), null, dataElement, true);

        String servingName = values.get(SERVING_NAME_PROPERTY).toString();
        String targetName = values.get(TARGET_ELEMENT_PROPERTY).toString();

        IDataElement servingElement = getNetworkElement(getN2NNodeType(), SERVING_NAME_PROPERTY, values);
        IDataElement targetElement = getNetworkElement(getN2NNodeType(), TARGET_ELEMENT_PROPERTY, values);

        if (servingElement != null && targetElement != null) {
            getMainModel().linkNode(servingElement, targetElement, values);
        } else {
            ConflictNeighboursModel<String> conflictModel = getConflictNeighboursModel();
            // try to resolve conflict
            boolean removed = conflictModel.removeRelation(servingName, targetName);
            if (removed) {
                // create link
                servingElement = getNetworkElement(getN2NNodeType(), servingName);
                targetElement = getNetworkElement(getN2NNodeType(), targetName);
                getMainModel().linkNode(servingElement, targetElement, values);
            } else {
                // pair doesn't exist.
                getConflictNeighboursModel().addRelation(servingName, targetName);
            }
        }
    }

    @Override
    protected boolean isRenderable() {
        return false;
    }

    @Override
    protected INodeToNodeRelationsModel createMainModel(ISingleFileConfiguration configuration) throws AWEException {
        networkModel = getActiveProject().getNetwork(configuration.getDatasetName());

        String n2nName = configuration.getFile().getName();

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
