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
import java.util.Set;

import org.amanzi.awe.catalog.neo.NeoCatalogPlugin;
import org.amanzi.awe.catalog.neo.upd_layers.events.UpdateLayerEvent;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.IndexManager;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.neo.services.statistic.IPropertyHeader;
import org.amanzi.neo.services.statistic.PropertyHeader;
import org.amanzi.neo.services.ui.SelectionPropertyManager;
import org.amanzi.neo.services.utils.Utils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.neoclipse.property.NodePropertySource;
import org.neo4j.neoclipse.property.PropertyDescriptor;

/**
 * Class that creates a properties of given DataElement.
 *
 * @author Kasnitskij_V
 * @since 1.0.0
 */

public class NewNetworkPropertySourceEditable extends NodePropertySource implements IPropertySource {
    
	/**
	 * Current data element on which user clicked
	 */
	private IDataElement currentDataElement;
	
    /**
     * Instantiates a new network property source.
     *
     * @param dataElement the dataElement
     */
    public NewNetworkPropertySourceEditable(IDataElement dataElement) {
        super(((DataElement)dataElement).getNode(), null);
        currentDataElement = dataElement;
    }
    
    /**
     * Returns the descriptors for the properties of the node.
     *
     * @return the property descriptors
     */
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
            Class< ? > klass = value.getClass();
	        NodeTypes nt = NodeTypes.getNodeType(container,null);
	        if(nt == null || nt.isPropertyEditable(key)) {
        		descs.add(new PropertyDescriptor(key, key, PROPERTIES_CATEGORY, klass));
	        }
	        else {
	            descs.add(new PropertyDescriptor(key, key, NODE_CATEGORY));
	        }
        }
        return descs.toArray(new IPropertyDescriptor[descs.size()]);
    }
	
    /**
     * Sets the property value.
     *
     * @param id the id
     * @param value the value
     */
    @Override
    public void setPropertyValue(Object id, Object value) {
        INetworkModel networkModel = (INetworkModel)currentDataElement.get(INeoConstants.NETWORK_MODEL_NAME);
        try {
        	boolean isReadyToUpdate = false;
        	INodeType nodeType = NodeTypeManager.getType(currentDataElement.get(NewAbstractService.TYPE).toString());
        	// if property is unique then find is exist some element with equal property
        	if (networkModel.isUniqueProperties(id.toString())) {
        		if (nodeType.equals(NetworkElementNodeType.SECTOR)) {
        			IDataElement dataElement = networkModel.findSector(id.toString(), value.toString());
        			if (dataElement == null) {
        				isReadyToUpdate = true;
        			}
        			else {
        				isReadyToUpdate = false;
        			}
        		}
        		else {
	            	Set<IDataElement> elements = networkModel.findElementByPropertyValue(nodeType, id.toString(), value);
	            	if (elements.size() > 0) {
	            		isReadyToUpdate = false;
	            	}
	            	else {
	            		isReadyToUpdate = true;
	            	}
        		}
        	}
        	else {
        		isReadyToUpdate = true;
        	}
        	if (isReadyToUpdate) {
        		networkModel.updateElement(currentDataElement, id.toString(), value);
        	}
        	else {
        		MessageDialog.openInformation(null, "Can not change property", "Can not change this property, because it property is unique");
        	}
        } catch (AWEException e) {
            // TODO Handle AWEException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
//        if (!((String)id).startsWith("delta_")) {
//            Transaction tx = NeoServiceProviderUi.getProvider().getService().beginTx();
//            try {
//                DatasetService service = NeoServiceFactory.getInstance().getDatasetService();
//
//                Node root = service.findRootByChild((Node)container);
//                Object oldValue=null;
//                if (container.hasProperty((String)id)) {
//                    oldValue=container.getProperty((String)id);
//
//                    // try to keep the same type as the previous value
//                    Class< ? > c = container.getProperty((String)id).getClass();
//                    PropertyHandler propertyHandler = PropertyTransform.getHandler(c);
//                    if (propertyHandler == null) {
//                        MessageDialog.openError(null, "Error", "No property handler was found for type " + c.getSimpleName() + ".");
//                        return;
//                    }
//                    Object o = null;
//                    try {
//                        o = propertyHandler.parse(value);
//                    } catch (Exception e) {
//                        MessageDialog.openError(null, "Error", "Could not parse the input as type " + c.getSimpleName() + ".");
//                        return;
//                    }
//                    if (o == null) {
//                        MessageDialog.openError(null, "Error", "Input parsing resulted in null value.");
//                        return;
//                    }
//                    try {
//                        container.setProperty((String)id, o);
//                    } catch (Exception e) {
//                        MessageDialog.openError(null, "Error", "Error in Neo service: " + e.getMessage());
//                    }
//                    updateIndexes(root,container, (String)id,oldValue);
//                } else {
//                    // simply set the value
//                    try {
//                        container.setProperty((String)id, value);
//                    } catch (Exception e) {
//                        MessageDialog.openError(null, "Error", "Error in Neo service: " + e.getMessage());
//                    }
//                }
//                tx.success();
//                updateLayer();
//                updateStatistics(root,container, (String)id,oldValue);
//            } finally {
//                tx.finish();
//                NeoServiceProviderUi.getProvider().commit();
//                updateLayer();
//            }
//        }
    }
    
    /**
     * Update statistics.
     *
     * @param container the container
     * @param container 
     * @param id the id
     * @param oldValue the old value
     */
    private void updateStatistics(Node   root, PropertyContainer container, String id, Object oldValue) {
        if (container instanceof Node){
            DatasetService service = NeoServiceFactory.getInstance().getDatasetService();
            if (root!=null){
                IPropertyHeader stat = PropertyHeader.getPropertyStatistic(root);
                stat.updateStatistic(service.getNodeType((Node)container).getId(), id, container.getProperty(id, null), oldValue);
            }
        }
    }

    /**
     * Update indexes.
     *
     * @param container the container
     * @param propertyName the property name
     * @param oldValue the old value
     */
    private void updateIndexes(Node root,PropertyContainer container, String propertyName,Object oldValue) {
        if (container instanceof Node){
            DatasetService service = NeoServiceFactory.getInstance().getDatasetService();
            if (root!=null){
                IndexManager manager = service.getIndexManager(root);
                manager.updateIndexes(container,propertyName,oldValue);
            }
        }
    }

    /**
     * updates layer.
     */
    private void updateLayer() {
        Node gisNode = Utils.findGisNodeByChild((Node)container);
        NeoCatalogPlugin.getDefault().getLayerManager().sendUpdateMessage(new UpdateLayerEvent(gisNode));
    }
}
