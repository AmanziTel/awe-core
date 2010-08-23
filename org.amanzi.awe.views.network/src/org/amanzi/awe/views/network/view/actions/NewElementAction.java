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

package org.amanzi.awe.views.network.view.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.amanzi.awe.catalog.neo.NeoCatalogPlugin;
import org.amanzi.awe.catalog.neo.upd_layers.events.UpdatePropertiesAndMapEvent;
import org.amanzi.awe.views.network.NetworkTreePlugin;
import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.GisProperties;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.index.MultiPropertyIndex;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.lucene.LuceneIndexService;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public class NewElementAction extends Action {
    
    protected static final NodeTypes[] CREATE_ACTION_SUPPORTED_TYPES = new NodeTypes[] {NodeTypes.BSC, NodeTypes.SITE, NodeTypes.CITY};
    
    private Node selectedNode;
    
    protected GraphDatabaseService service;
    
    private NodeTypes type;
    
    protected HashMap<String, Object> defaultProperties = new HashMap<String, Object>();
    
    private String luceneIndexName;
    
    private Node networkNode;
    
    private String defaultValue;
    
    public NewElementAction(IStructuredSelection selection) {
        this(selection, CREATE_ACTION_SUPPORTED_TYPES, "Create new ");
    }
    
    protected NewElementAction(IStructuredSelection selection, NodeTypes[] supportedTypes, String actionPrefix) {
        service = NeoServiceProvider.getProvider().getService();
        
        //check is action should be enabled
        //action should work on ONE element
        boolean enabled = (selection != null) && (selection.size() == 1);
        
        //if not enabled - exit
        if (enabled) {
            //check content of selection - it should be NeoNode
            Object element = selection.getFirstElement();
            if (element instanceof NeoNode) {
                selectedNode = ((NeoNode)element).getNode();
                enabled = initialize(supportedTypes);
            }
            else {
                enabled = false;
            }
            
            if (enabled) {
                setActionText(actionPrefix);
            }
        }
        
        setEnabled(enabled);
    }
    
    private void initializeDefaultProperties() {
        networkNode = NeoUtils.getParentNode(selectedNode, NodeTypes.NETWORK.getId());
        luceneIndexName = NeoUtils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_NAME_NAME, type);
        
        defaultValue = "New " + type.getId();
        
        switch (type) {
        case SITE:
            defaultProperties.put(INeoConstants.PROPERTY_LAT_NAME, 0.0d);
            defaultProperties.put(INeoConstants.PROPERTY_LON_NAME, 0.0d);
            break;
        case SECTOR:
            defaultProperties.put("azimuth", 0.0d);
            defaultProperties.put("beamwidth", 0.0d);
            defaultProperties.put(INeoConstants.PROPERTY_SECTOR_CI, 0);
            defaultProperties.put(INeoConstants.PROPERTY_SECTOR_LAC, 0);
            break;
        }
        
        
    }
    
    private void setActionText(String actionPrefix) {
        setText(actionPrefix + type.getId());
    }
    
    private boolean initialize(NodeTypes[] supportedTypes) {
        boolean result = false;
        Transaction tx = service.beginTx();
        try {
            for (NodeTypes singleType : supportedTypes) {
                if (singleType.checkNode(selectedNode)) {
                    result = true;
                    type = singleType;
                    break;
                }
            }
            
            if (result) {
                updateNewElementType();
                initializeDefaultProperties();
            }
            
            tx.success();
        }
        finally {
            tx.finish();
        }
        
        return result;
    }
    
    protected void updateNewElementType() {
        switch (type) {
        case BSC:
        case CITY:
            type = NodeTypes.SITE;
            break;
        case SITE:
            type = NodeTypes.SECTOR;
            break;
        }
    }
    
    @Override
    public void run() {
        InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), getText(), "Enter name of new element", getNewElementName(defaultValue), null);
        int result = dialog.open();
        if (result != Dialog.CANCEL) {
            defaultProperties.put(INeoConstants.PROPERTY_NAME_NAME, dialog.getValue());
            createNewElement(selectedNode, defaultProperties);
            
            NeoServiceProvider.getProvider().commit();
        }
    }
    
    protected String getNewElementName(String pattern) {
        Integer counter = 2;
        
        String oldPattern = new String(pattern);
        
        Transaction tx = service.beginTx();
        try {
            LuceneIndexService indexService = NeoServiceProvider.getProvider().getIndexService();         
        
            while (indexService.getSingleNode(luceneIndexName, pattern) != null) {
                pattern = oldPattern + " " + counter.toString();
                counter++;                                
            }  
            tx.success();
        }
        finally {
            tx.finish();
        }
        
        return pattern;
    }
    
    protected void createNewElement(Node parentElement, HashMap<String, Object> properties) {
        Transaction tx = service.beginTx();
        try {
            Node child = service.createNode();
            type.setNodeType(child, service);
            for (String key : properties.keySet()) {
                child.setProperty(key, properties.get(key));
            }
            if (type == NodeTypes.SECTOR) {
                parentElement.createRelationshipTo(child, NetworkRelationshipTypes.CHILD);
            }
            else {
                NeoUtils.addChild(parentElement, child, null, service);
            }
            
            postCreating(child);
            
            tx.success();
        }
        catch (Exception e) {
            tx.failure();
        }
        finally { 
            tx.finish();
        }
    }
    
    /**
     * Some actions (like indexing/re-indexing) that should be done after creation of new element
     * 
     */
    protected void postCreating(Node newElement) {
        switch (type) {
        case BSC:
            indexElement(newElement);
            break;
        case SITE:
            indexElement(newElement);
            multiPropertyIndex(newElement);
            updateBounds(newElement);
            break;
        case SECTOR:
            //TODO: sectors need to have more flexible indexing
            indexElement(newElement);
            break;
        }
    }
    
    protected void updateBounds(Node newElement) {
        Node gis = NeoUtils.findGisNodeByChild(networkNode, service);
        
        GisProperties gisProperties = new GisProperties(gis);
        Double lat = (Double)newElement.getProperty(INeoConstants.PROPERTY_LAT_NAME);
        Double lon = (Double)newElement.getProperty(INeoConstants.PROPERTY_LON_NAME);
        gisProperties.updateBBox(lat, lon);
        gisProperties.saveBBox();
        
        UpdatePropertiesAndMapEvent event = new UpdatePropertiesAndMapEvent(gis, null, false);
        event.setNeedCentered(true);
        boolean autoZoom = true;
        if (!gis.getProperty(INeoConstants.PROPERTY_CRS_TYPE_NAME, "").toString().equalsIgnoreCase(("projected"))) {
            autoZoom = false;
        }
        event.setAutoZoom(autoZoom);
        event.setCoords(new double[] {lat, lon});
        ArrayList<Node> selection = new ArrayList<Node>();
        selection.add(newElement);
        event.setSelection(selection);
        NeoCatalogPlugin.getDefault().getLayerManager().sendUpdateMessage(event);
    }
    
    protected void multiPropertyIndex(Node newElement) {
        try {
            MultiPropertyIndex<Double> index = NeoUtils.getLocationIndexProperty(NeoUtils.getNodeName(networkNode, service));
            index.initialize(service, null);
            
            index.add(newElement);
            index.finishUp();
        }
        catch (IOException e) { 
            NetworkTreePlugin.getDefault().getLog().log(new Status(IStatus.ERROR, NetworkTreePlugin.PLUGIN_ID, e.getMessage(), e));
        }
    }
    
    protected void indexElement(Node newElement) {
        LuceneIndexService indexService = NeoServiceProvider.getProvider().getIndexService();         
        
        indexService.index(newElement, luceneIndexName, newElement.getProperty(INeoConstants.PROPERTY_NAME_NAME));
    }
    
    
}
