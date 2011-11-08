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
package org.amanzi.awe.views.network.property;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.neo.services.ui.SelectionPropertyManager;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.neo4j.graphdb.Node;
import org.neo4j.neoclipse.property.NodePropertySource;
import org.neo4j.neoclipse.property.PropertyDescriptor;

/**
 * Class that creates a properties of given DataElement.
 *
 * @author Kasnitskij_V
 * @since 1.0.0
 */

public class NewNetworkPropertySource extends NodePropertySource implements IPropertySource {
    
    /**
     * Instantiates a new network property source.
     *
     * @param dataElement the dataElement
     */
    public NewNetworkPropertySource(IDataElement dataElement) {
        super(((DataElement)dataElement).getNode(), null);
    }
    
    /**
     * Returns the descriptors for the properties of the node.
     *
     * @return the property descriptors
     */
    @SuppressWarnings({ "unused" })
	public IPropertyDescriptor[] getPropertyDescriptors() {
        SelectionPropertyManager propertyManager = SelectionPropertyManager.getInstanse();
        
        List<INodeType> networkStructure = new ArrayList<INodeType>();
        NewDatasetService datasetService = NeoServiceFactory.getInstance().getNewDatasetService();
        Node parentNode = null;
        try {
			parentNode = datasetService.getParent((Node)container, false);
			while (datasetService.getParent(datasetService.getParent(parentNode, false), false) != null) {
				parentNode = datasetService.getParent(parentNode, false);
			}
        	String networkName = parentNode.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
			NetworkModel networkModel = (NetworkModel) ProjectModel.getCurrentProjectModel().findNetwork(networkName);
			networkStructure = networkModel.getNetworkStructure();
		} catch (AWEException e) {
			e.printStackTrace();
		}
        
        List<IPropertyDescriptor> descs = new ArrayList<IPropertyDescriptor>();
        for (IPropertyDescriptor descriptor : getHeadPropertyDescriptors()) {
            if (propertyManager.checkVisibility(descriptor.getDisplayName(), networkStructure))
                descs.add(descriptor);   
        }
        
        for (String key : container.getPropertyKeys()) {
            Object value = container.getProperty(key);
            Class< ? > c = value.getClass();
	        NodeTypes nt = NodeTypes.getNodeType(container,null);
	        if(nt == null || nt.isPropertyEditable(key))
	            descs.add(new PropertyDescriptor(key, key, PROPERTIES_CATEGORY));
	        else
	            descs.add(new PropertyDescriptor(key, key, NODE_CATEGORY));
        }
        return descs.toArray(new IPropertyDescriptor[descs.size()]);
    }
}
