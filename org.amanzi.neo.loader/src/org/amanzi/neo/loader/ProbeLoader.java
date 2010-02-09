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

package org.amanzi.neo.loader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.swt.widgets.Display;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser.Order;

/**
 * <p>
 *
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class ProbeLoader extends AbstractLoader{

    private boolean needParceHeader;
    private Node network;
    private Node gisNode;
    private GisProperties gisProperties;

    public ProbeLoader(String filename, Display display) {
        needParceHeader=true;
        network = null;
        initialize("Probe", null, filename, display);
        initializeKnownHeaders();
        addNetworkIndexes();
    }

    /**
     *add networ indexes
     */
    private void addNetworkIndexes() {
        // TODO check - use same index like sectors or new?
        try {
            addIndex(INeoConstants.PROBE_TYPE_NAME, NeoUtils.getLocationIndexProperty(basename));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

    }
    @Override
    protected Node getStoringNode(Integer key) {
        return gisNode;
    }

    @Override
    protected boolean needParceHeaders() {
        if (needParceHeader) {
            needParceHeader = false;
            return true;
        }
        return false;
    }
    /**
     * Build a map of internal header names to format specific names for types that need to be known
     * in the algorithms later.
     */
    private void initializeKnownHeaders() {
        needParceHeader = true;
        addKnownHeader(1, INeoConstants.PROPERTY_LAT_NAME, ".*latitude.*");
        addKnownHeader(1, "name", "probe");
        addKnownHeader(1, INeoConstants.PROPERTY_LON_NAME, ".*longitude.*");
        addKnownHeader(1, "probe_type", "type");
    }

    @Override
    protected void parseLine(String line) {
        if (network == null) {
            network = findOrCreateNetworkNode(network, false);
            gisNode = findOrCreateGISNode(network, GisTypes.NETWORK.getHeader());
            gisProperties = new GisProperties(gisNode);
        }
        List<String> fields = splitLine(line);
        if (fields.size() < 2)
            return;
        if (this.isOverLimit())
            return;
        Map<String, Object> lineData = makeDataMap(fields);
        saveProbe(lineData);
    }

    /**
     * @param lineData
     */
    private void saveProbe(Map<String, Object> lineData) {
        String probeName = (String)lineData.get("name");
        if (probeName == null) {
            NeoLoaderPlugin.error("Probe not stored:\t" + lineData);
            return;
        }

        Transaction transaction = neo.beginTx();
        try {
            Node probeNode = findOrCreateProbe(network, probeName);
            for (Map.Entry<String, Object> entry : lineData.entrySet()) {
                if (INeoConstants.PROPERTY_LAT_NAME.equals(entry.getKey()) || INeoConstants.PROPERTY_LON_NAME.equals(entry.getKey())) {
                    probeNode.setProperty(entry.getKey(), ((Number)entry.getValue()).doubleValue());
                } else {
                probeNode.setProperty(entry.getKey(), entry.getValue());
                }

            }
            index(probeNode);
            Double currentLatitude = (Double)probeNode.getProperty(INeoConstants.PROPERTY_LAT_NAME, null);
            Double currentLongitude = (Double)probeNode.getProperty(INeoConstants.PROPERTY_LON_NAME, null);
            if (currentLatitude != null && currentLongitude != null) {

                gisProperties.updateBBox(currentLatitude, currentLongitude);
                gisProperties.checkCRS(currentLatitude.floatValue(), currentLongitude.floatValue(), null);
                gisProperties.incSaved();
            }
            transaction.success();
        } finally {
            transaction.finish();
        }

    }

    /**
     * @param probeName
     * @param network2
     * @return
     */
    private Node findOrCreateProbe(Node networkNode, final String probeName) {
        //TODO use luciene index by name!
        Transaction tx=neo.beginTx();
        try{
            Iterator<Node> iterator = networkNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Node node = currentPos.currentNode();
                    return NeoUtils.isProbeNode(node) && probeName.equals(NeoUtils.getNodeName(node));
                }
            }, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
            Node result = neo.createNode();
            result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.PROBE_TYPE_NAME);
            result.setProperty(INeoConstants.PROPERTY_NAME_NAME, probeName);
            network.createRelationshipTo(result, GeoNeoRelationshipTypes.CHILD);
            tx.success();
            return result;
        }finally{
            tx.finish();
        }

    }

    @Override
    protected void finishUp() {
        super.finishUp();
        gisProperties.saveBBox();
        gisProperties.saveCRS();
        try {
            super.finishUpGis(gisNode);
        } catch (MalformedURLException e) {
            NeoLoaderPlugin.exception(e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }
}
