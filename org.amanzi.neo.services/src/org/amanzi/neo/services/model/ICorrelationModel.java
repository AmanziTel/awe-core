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

package org.amanzi.neo.services.model;

import org.neo4j.graphdb.Node;

/**
 * <p>
 * Correlation model describes relationships between elements of two models.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface ICorrelationModel extends IModel {

    /**
     * Returns the network node, involved in current relationship.
     * 
     * @return a network node
     */
    public IDataElement getNetwork();

    /**
     * Returns the dataset node, involved in current relationship.
     * 
     * @return a dataset node
     */
    public IDataElement getDataset();

    /**
     * Traverses database to get all the sectors of current network, that have correlated dataset
     * nodes.
     * 
     * @return traverser over sectors
     */
    public Iterable<IDataElement> getSectors();

    /**
     * Traverses database to get all the nodes of the current dataset (measurements for drive data,
     * and something else for something else), that are correlated to sectors of current network.
     * 
     * @return traverser over dataset nodes
     */
    public Iterable<IDataElement> getMeasurements();

    /**
     * Traverses database to get the dataset nodes correlated to the defined sector node.
     * 
     * @param sector the sector to find correlations for
     * @return traverser over dataset nodes
     */
    public Iterable<IDataElement> getCorrelatedNodes(Node sector);

    /**
     * Find a node in the current network, that is correlated to the defined dataset node.
     * 
     * @param measurement the node to find correlation for
     * @return the correlated sector or <code>null</code>.
     */
    public IDataElement getCorrelatedSector(Node measurement);
}
