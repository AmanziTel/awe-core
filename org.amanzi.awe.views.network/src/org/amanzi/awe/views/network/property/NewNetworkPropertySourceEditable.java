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

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewNetworkService;
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
import org.amanzi.neo.services.ui.SelectionPropertyManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
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

public class NewNetworkPropertySourceEditable extends NodePropertySource implements IPropertySource {

    private static String TITLE_COULD_NOT_CHANGE_PROPERTY = "Could not change property";
    private static String MESSAGE_COULD_NOT_CHANGE_PROPERTY = "Could not change this property, because it property is unique.\n";
    private static String PROPERTY_DEFINED_IN_ELEMENT = "In current moment this property define in element with type ";
    private static String PROPERTY_DEFINED_IN_ELEMENT_CI_LAC = "In current moment properties ci+lac unique and define in element with type ";

    public static boolean showMessageBox = true;

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
            NetworkModel networkModel = (NetworkModel)ProjectModel.getCurrentProjectModel().findNetwork(networkName);
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
            NodeTypes nt = NodeTypes.getNodeType(container, null);
            if (nt == null || nt.isPropertyEditable(key)) {
                descs.add(new PropertyDescriptor(key, key, PROPERTIES_CATEGORY, klass));
            } else {
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
        String propertyName = id.toString();
        INetworkModel networkModel = (INetworkModel)currentDataElement.get(INeoConstants.NETWORK_MODEL_NAME);
        try {
            boolean isReadyToUpdate = true;
            INodeType nodeType = NodeTypeManager.getType(currentDataElement.get(NewAbstractService.TYPE).toString());
            IDataElement dataElement = null;
            boolean isCIorLAC = false;
            // if property is unique then find is exist some element with equal property
            if (networkModel.isUniqueProperties(propertyName)) {
                if (nodeType.equals(NetworkElementNodeType.SECTOR)) {
                    // ci+lac in sector should be unique, not a ci_lac as parameter
                    // but ci together with lac should be unique
                    String ci_lac = null;
                    if (propertyName.equals(NewNetworkService.CELL_INDEX)) {
                        String lac = currentDataElement.get(NewNetworkService.LOCATION_AREA_CODE).toString();
                        ci_lac = value.toString() + "_" + lac;
                        isCIorLAC = true;
                    } else if (propertyName.equals(NewNetworkService.LOCATION_AREA_CODE)) {
                        String ci = currentDataElement.get(NewNetworkService.CELL_INDEX).toString();
                        ci_lac = ci + "_" + value.toString();
                        isCIorLAC = true;
                    }
                    if (isCIorLAC) {
                        dataElement = networkModel.findSector(propertyName, ci_lac);
                    } else {
                        dataElement = networkModel.findSector(propertyName, value.toString());
                    }
                    if (dataElement != null) {
                        isReadyToUpdate = false;
                    }
                } else {
                    Set<IDataElement> elements = networkModel.findElementByPropertyValue(nodeType, propertyName, value);
                    if (elements.size() > 0) {
                        dataElement = elements.iterator().next();
                        isReadyToUpdate = false;
                    }
                }
            }
            if (isReadyToUpdate) {
                networkModel.updateElement(currentDataElement, propertyName, value);
            } else {
                String propertyDefined = null;
                if (isCIorLAC) {
                    propertyDefined = PROPERTY_DEFINED_IN_ELEMENT_CI_LAC;
                } else {
                    propertyDefined = PROPERTY_DEFINED_IN_ELEMENT;
                }
                String message = MESSAGE_COULD_NOT_CHANGE_PROPERTY + propertyDefined + dataElement.get(NewNetworkService.TYPE)
                        + " and name " + dataElement.get(NewNetworkService.NAME);

                if (showMessageBox) {
                    showMessageBox = false;
                    synchronized (message) {
                        // if we will use this code then we will get a critical error
                        // MessageDialog.openWarning(null, TITLE_COULD_NOT_CHANGE_PROPERTY,
                        // message);
                        MessageBox msg = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK);
                        msg.setText(TITLE_COULD_NOT_CHANGE_PROPERTY);
                        msg.setMessage(message);
                        int result = msg.open();
                        if (result != SWT.OK) {
                            return;
                        }
        			}
        			showMessageBox = true;
        		}
        	}
        } catch (AWEException e) {
            MessageDialog.openError(null, TITLE_COULD_NOT_CHANGE_PROPERTY, TITLE_COULD_NOT_CHANGE_PROPERTY + "\n" + e);
        }
    }
}
