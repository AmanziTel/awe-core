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

package org.amanzi.neo.core.amscall;

import java.util.HashSet;
import java.util.Set;

import org.amanzi.neo.services.enums.CallProperties.CallResult;
import org.amanzi.neo.services.enums.CallProperties.CallType;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * AMS call interface implement
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class AmsCall implements IAmsCall {
    protected static final String TIME_FORMAT = "HH:mm:ss";

    protected CallResult callResult;
    protected CallType callType;
    protected Set<Node> calleeProbes = new HashSet<Node>();
    protected Node callerProbe;
    protected Set<Node> relatedNodes = new HashSet<Node>();
    protected Node node;
    protected long timestamp;

    protected boolean inconclusive = false;

    @Override
    public void addCalleeProbe(Node calleeProbe) {
        calleeProbes.add(calleeProbe);
    }

    @Override
    public void addRelatedNode(Node mNode) {
        relatedNodes.add(mNode);
    }

    @Override
    public CallResult getCallResult() {
        return callResult;
    }

    @Override
    public CallType getCallType() {
        return callType;
    }

    @Override
    public Set<Node> getCalleeProbes() {
        return calleeProbes;
    }

    @Override
    public Node getCallerProbe() {
        return callerProbe;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public Set<Node> getRelatedNodes() {
        return relatedNodes = new HashSet<Node>();
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setCallerProbe(Node callerProbe) {
        this.callerProbe = callerProbe;
    }

    public void setCallResult(CallResult callResult) {
        this.callResult = callResult;
    }

    // @Override
    // public Node saveCall(GraphDatabaseService service) {
    // Transaction tx = NeoUtils.beginTx(service);
    // try {
    // node = service.createNode();
    // NodeTypes.CALL.setNodeType(node, service);
    // node.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, getTimestamp());
    // String probeName = NeoUtils.getNodeName(getCallerProbe(), service);
    // node.setProperty(INeoConstants.PROPERTY_NAME_NAME, getCallName(probeName, getTimestamp()));
    // // create relationship to M node
    // for (Node mNode : getRelatedNodes()) {
    // node.createRelationshipTo(mNode, ProbeCallRelationshipType.CALL_M);
    // }
    // NeoUtils.successTx(tx);
    // return node;
    // } finally {
    // NeoUtils.finishTx(tx);
    // }
    // }

    // public static String getCallName(String probeName, long timestamp) {
    // SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
    // StringBuffer result = new
    // StringBuffer(probeName.split(" ")[0]).append("_").append(timeFormat.format(new
    // Date(timestamp)));
    // return result.toString();
    // }

    @Override
    public boolean isInconclusive() {
        return inconclusive;
    }

    public void setInconclusive(boolean inconclusive) {
        this.inconclusive = inconclusive;
    }

    /**
     * @param result
     */
    public void setNode(Node result) {
        node = result;
    }

    protected void setCallType(CallType callType) {
        this.callType = callType;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
