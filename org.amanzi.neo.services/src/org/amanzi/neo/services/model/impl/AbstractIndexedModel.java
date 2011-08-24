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

package org.amanzi.neo.services.model.impl;

import java.util.Map;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.indexes.MultiPropertyIndex;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public abstract class AbstractIndexedModel extends PropertyStatisticalModel {

    private Map<String, Iterable<MultiPropertyIndex<Object>>> indexes;

    protected void addLocationIndex(INodeType nodeType) {
    }

    protected void addTimestampIndex(INodeType nodeType) {
    }

    protected void indexNode(Node node) {
    }

    protected void flushIndexes() {
    }

    private MultiPropertyIndex<Long> createTimestampIndex(INodeType nodeType) {
        return null;
    }

    private MultiPropertyIndex<Double> createLocationIndex(INodeType nodeType) {
        return null;
    }

    protected void updateTimestampBounds(long timestamp) {
    }

    protected void updateLocationBounds(Node sourceNode, double latitude, double longitude) {
    }

    protected long getMinTimestamp() {
        return 0;
    }

    protected long getMaxTimestamp() {
        return 0;
    }
}
