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
import java.util.Map;

import org.amanzi.awe.catalog.neo.NeoCatalogPlugin;
import org.amanzi.awe.catalog.neo.upd_layers.events.UpdatePropertiesAndMapEvent;
import org.amanzi.awe.views.network.NetworkTreePlugin;
import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.awe.views.network.proxy.Root;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.indexes.MultiPropertyIndex;
import org.amanzi.neo.services.statistic.IPropertyHeader;
import org.amanzi.neo.services.statistic.PropertyHeader;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
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
 * </p>
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class NewElementAction extends Action {

    protected static final NodeTypes[] CREATE_ACTION_SUPPORTED_TYPES = new NodeTypes[] {NodeTypes.SECTOR, NodeTypes.NETWORK};

    protected Node selectedNode;

    protected GraphDatabaseService service;

    protected INodeType type;

    protected HashMap<String, Object> defaultProperties = new HashMap<String, Object>();

    private String luceneIndexName;

    private Node networkNode;

    private String defaultValue;

    private final boolean askType;

    private String newType;

    public NewElementAction(IStructuredSelection selection, boolean askType) {
        this(selection, CREATE_ACTION_SUPPORTED_TYPES, "Create new ", askType);
    }

    protected NewElementAction(IStructuredSelection selection, NodeTypes[] unsupportedTypes, String actionPrefix, boolean askType) {
        service = NeoServiceProviderUi.getProvider().getService();
        this.askType = askType;

        // check is action should be enabled
        // action should work on ONE element
        boolean enabled = (selection != null) && (selection.size() == 1);

        // if not enabled - exit
        if (enabled) {
            // check content of selection - it should be NeoNode
            Object element = selection.getFirstElement();
            if (element instanceof Root)
                enabled = false;
            else if (element instanceof NeoNode) {
                selectedNode = ((NeoNode)element).getNode();
                DatasetService ds = NeoServiceFactory.getInstance().getDatasetService();
                type = ds.getNodeType(selectedNode);
                // type = NodeTypes.getNodeType(selectedNode, service);
                enabled = initialize(unsupportedTypes);
            } else {
                enabled = false;
            }

            if (enabled) {
                if (askType) {
                    setText("Create new network element");
                } else {
                    setActionText(actionPrefix);
                }
            }
        }

        setEnabled(enabled);
    }

    private void initializeDefaultProperties() {
        networkNode = NeoUtils.getParentNode(selectedNode, NodeTypes.NETWORK.getId());
        Node gis = NeoUtils.getGisNodeByDataset(networkNode);
        GisProperties prop = new GisProperties(gis);
        double[] bb = prop.getBbox();
        luceneIndexName = NeoUtils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_NAME_NAME, type);

        if (askType) {
            defaultValue = "New element";
        } else {
            defaultValue = "New " + type.getId();
        }

        if (type == NodeTypes.SITE) {
            if (bb != null) {
                defaultProperties.put(INeoConstants.PROPERTY_LAT_NAME, (bb[2] + bb[3]) / 2D);
                defaultProperties.put(INeoConstants.PROPERTY_LON_NAME, (bb[0] + bb[1]) / 2D);
            } else {
                defaultProperties.put(INeoConstants.PROPERTY_LAT_NAME, 0.0d);
                defaultProperties.put(INeoConstants.PROPERTY_LON_NAME, 0.0d);
            }
        } else if (type == NodeTypes.SECTOR) {
            defaultProperties.put("azimuth", 0.0d);
            defaultProperties.put("beamwidth", 0.0d);
            defaultProperties.put(INeoConstants.PROPERTY_SECTOR_CI, 0);
            defaultProperties.put(INeoConstants.PROPERTY_SECTOR_LAC, 0);
        }

    }

    private void setActionText(String actionPrefix) {
        setText(actionPrefix + type.getId());
    }

    private boolean initialize(NodeTypes[] supportedTypes) {
        boolean result = true;
        Transaction tx = service.beginTx();
        try {
            for (NodeTypes singleType : supportedTypes) {
                if (singleType.checkNode(selectedNode)) {
                    result = false;
                    break;
                }
            }

            if (result) {
                initializeDefaultProperties();
            }

            tx.success();
        } finally {
            tx.finish();
        }

        return result;
    }

    @Override
    public void run() {
        if (askType) {
            InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), getText(), "Enter type of new element", "", null);
            int result = dialog.open();
            if (result != Dialog.CANCEL) {
                newType = dialog.getValue();
                type = NodeTypes.getEnumById(newType);
            }
        }

        InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), getText(), "Enter name of new element", getNewElementName(defaultValue), null);
        int result = dialog.open();
        if (result != Dialog.CANCEL) {
            defaultProperties.put(INeoConstants.PROPERTY_NAME_NAME, dialog.getValue());
            createNewElement(selectedNode, defaultProperties);

            NeoServiceProviderUi.getProvider().commit();
        }
    }

    protected String getNewElementName(String pattern) {
        Integer counter = 2;

        String startValue = new String(pattern);

        Transaction tx = service.beginTx();
        try {
            LuceneIndexService indexService = NeoServiceProviderUi.getProvider().getIndexService();

            while (indexService.getSingleNode(luceneIndexName, pattern) != null) {
                pattern = startValue + " " + counter.toString();
                counter++;
            }
            tx.success();
        } finally {
            tx.finish();
        }

        return pattern;
    }

    protected void setType(Node element) {
        if (type != null) {
            DatasetService ds = NeoServiceFactory.getInstance().getDatasetService();
            ds.setNodeType(element, type);
            // type.setNodeType(element, service);
        } else {
            element.setProperty(INeoConstants.PROPERTY_TYPE_NAME, newType);
        }
    }

    protected void createNewElement(Node parentElement, HashMap<String, Object> properties) {
        Transaction tx = service.beginTx();
        try {
            Node child = service.createNode();
            // NeoUtils.getParentNode(parentElement, NodeTypes.NETWORK.getId());
            IPropertyHeader ph = PropertyHeader.getPropertyStatistic(networkNode);
            Map<String, Object> statisticProperties = ph.getStatisticParams(type);
            for (String key : statisticProperties.keySet()) {
                child.setProperty(key, statisticProperties.get(key));
            }
            child.setProperty(INeoConstants.PROPERTY_NAME_NAME, properties.get(INeoConstants.PROPERTY_NAME_NAME));

            setType(child);
            if (type == NodeTypes.SECTOR) {
                parentElement.createRelationshipTo(child, NetworkRelationshipTypes.CHILD);
            } else {
                NeoUtils.addChild(parentElement, child, null, service);
                parentElement.createRelationshipTo(child, NetworkRelationshipTypes.CHILD);
            }
            for (String key : properties.keySet()) {
                if (!child.hasProperty(key))
                    child.setProperty(key, properties.get(key));
            }
            postCreating(child);

            tx.success();
        } catch (Exception e) {
            e.printStackTrace();
            tx.failure();
        } finally {
            tx.finish();
        }
    }

    /**
     * Some actions (like indexing/re-indexing) that should be done after creation of new element
     */
    protected void postCreating(Node newElement) {
        if (type != null) {
            if (type == NodeTypes.BSC || type == NodeTypes.CITY || type == NodeTypes.SECTOR) {
                indexElement(newElement);
            } else if (type == NodeTypes.SITE) {
                indexElement(newElement);
                multiPropertyIndex(newElement);
                updateBounds(newElement);
            }
            // switch (type) {
            // case BSC:
            // case CITY:
            // indexElement(newElement);
            // break;
            // case SITE:
            // indexElement(newElement);
            // multiPropertyIndex(newElement);
            // updateBounds(newElement);
            // break;
            // case SECTOR:
            // // TODO: sectors need to have more flexible indexing
            // indexElement(newElement);
            // break;
            // }
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
        } catch (IOException e) {
            NetworkTreePlugin.getDefault().getLog().log(new Status(IStatus.ERROR, NetworkTreePlugin.PLUGIN_ID, e.getMessage(), e));
        }
    }

    protected void indexElement(Node newElement) {
        LuceneIndexService indexService = NeoServiceProviderUi.getProvider().getIndexService();

        indexService.index(newElement, luceneIndexName, newElement.getProperty(INeoConstants.PROPERTY_NAME_NAME));
    }

}
