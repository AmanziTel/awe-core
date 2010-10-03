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

package org.amanzi.awe.views.network.view;

import java.util.Map;

import org.amanzi.neo.core.enums.INodeType;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.EditPropertiesPage;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.services.statistic.IPropertyHeader;
import org.amanzi.neo.services.statistic.PropertyHeader;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author NiCK
 * @since 1.0.0
 */
public class CreateNewNodeWizardPage extends EditPropertiesPage {

    private final Node sourceNode;
    /**
     * @param pageName
     * @param title
     * @param nodeType
     */
    public CreateNewNodeWizardPage(String pageName, String title, INodeType nodeType, Node sourceNode) {
        super(pageName, title, nodeType);
        this.sourceNode = sourceNode;
    }

    @Override
    protected void initProperty() {
        // super.initProperty();
        // propertyList.add(new PropertyWrapper("lat", Double.class, "", false));

        NodeTypes type = NodeTypes.getEnumById(nodeType.getId());
        if (type != null) {
            IPropertyHeader ph = PropertyHeader.getPropertyStatistic(NeoUtils.getParentNode(sourceNode, NodeTypes.NETWORK.getId()));
            Map<String, Object> statisticProperties = ph.getStatisticParams(type);
            for (String key : statisticProperties.keySet()) {
                propertyList.add(new NewNodePropertyWrapper(key, statisticProperties.get(key).getClass(), statisticProperties.get(key).toString(), false));
            }
        }

    }

    /**
     * The Class NewNodePropertyWrapper.
     */
    protected class NewNodePropertyWrapper extends PropertyWrapper {

        /**
         * Instantiates a new property wrapper.
         * 
         * @param name the name
         * @param type the type
         * @param defValue the def value
         * @param editable the editable
         */
        public NewNodePropertyWrapper(String name, Class< ? > type, String defValue, boolean editable) {
            super(name, type, defValue, editable);
        }

    }

}
