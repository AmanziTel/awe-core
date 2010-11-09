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

import java.util.Set;

import org.amanzi.neo.services.enums.CallProperties.CallResult;
import org.amanzi.neo.services.enums.CallProperties.CallType;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * .
 * 
 * @author TsAr
 * @since 1.0.0
 */
public interface IAmsCall {
    /**
     * Checks if is inconclusive.
     * 
     * @return true, if is inconclusive
     */
    boolean isInconclusive();

    /**
     * get call node.
     * 
     * @return Node
     */
    Node getNode();

    /**
     * get call result.
     * 
     * @return call result
     */
    CallResult getCallResult();

    /**
     * Set caller probe.
     * 
     * @param callerProbe - caller Probe
     */
    void setCallerProbe(Node callerProbe);

    /**
     * Gets the caller probe.
     * 
     * @return the caller probe
     */
    Node getCallerProbe();

    /**
     * Gets the callee probes.
     * 
     * @return the callee probes
     */
    Set<Node> getCalleeProbes();

    /**
     * Adds the callee probe.
     * 
     * @param calleeProbe the callee probe
     */
    void addCalleeProbe(Node calleeProbe);

    /**
     * Gets the timestamp.
     * 
     * @return the timestamp
     */
    long getTimestamp();

    /**
     * Gets the call type.
     * 
     * @return the call type
     */
    CallType getCallType();

    // Node saveCall(GraphDatabaseService service);

    /**
     * Gets the related nodes.
     * 
     * @return the related nodes
     */
    Set<Node> getRelatedNodes();

    /**
     * Adds the related node.
     * 
     * @param mNode the m node
     */
    void addRelatedNode(Node mNode);

}
