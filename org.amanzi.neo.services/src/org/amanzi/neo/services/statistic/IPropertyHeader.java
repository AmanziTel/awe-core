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

package org.amanzi.neo.services.statistic;

import java.util.Collection;
import java.util.Map;

import org.amanzi.neo.core.enums.INodeType;

// TODO: Auto-generated Javadoc
/**
 * TODO Purpose of
 * <p>
 * 
 * </p>.
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public interface IPropertyHeader {

    /**
     * Gets the neighbour numeric fields.
     *
     * @param neighbourName the neighbour name
     * @return the neighbour numeric fields
     */
    String[] getNeighbourNumericFields(String neighbourName);


    /**
     * Gets the neighbour all fields.
     *
     * @param neighbourName the neighbour name
     * @return the neighbour all fields
     */
    String[] getNeighbourAllFields(String neighbourName);

    /**
     * Gets the transmission all fields.
     *
     * @param neighbourName the neighbour name
     * @return the transmission all fields
     */
    String[] getTransmissionAllFields(String neighbourName);




    /**
     * Gets the numeric fields.
     *
     * @param nodeTypeId the node type id
     * @return the numeric fields
     */
    String[] getNumericFields(String nodeTypeId);


    /**
     * Gets the all fields.
     *
     * @param nodeTypeId the node type id
     * @return the all fields
     */
    String[] getAllFields(String nodeTypeId);



    /**
     * Gets the all channels.
     *
     * @return the all channels
     */
    String[] getAllChannels();

    /**
     * Gets the neighbour list.
     *
     * @return the neighbour list
     */
    Collection<String> getNeighbourList();

    /**
     * Gets the neighbour integer fields.
     *
     * @param neighbourName the neighbour name
     * @return the neighbour integer fields
     */
    String[] getNeighbourIntegerFields(String neighbourName);

    /**
     * Gets the transmission integer fields.
     *
     * @param neighbourName the neighbour name
     * @return the transmission integer fields
     */
    String[] getTransmissionIntegerFields(String neighbourName);

    /**
     * Gets the neighbour double fields.
     *
     * @param neighbourName the neighbour name
     * @return the neighbour double fields
     */
    String[] getNeighbourDoubleFields(String neighbourName);

    /**
     * Gets the events.
     *
     * @return the events
     */
    Collection<String> getEvents();

    /**
     * Gets the transmission double fields.
     *
     * @param neighbourName the neighbour name
     * @return the transmission double fields
     */
    String[] getTransmissionDoubleFields(String neighbourName);

    /**
     * Gets the property statistic.
     * @param nodeTypeId TODO
     * @param propertyName the property name
     *
     * @return the property statistic
     */
    ISinglePropertyStat getPropertyStatistic(String nodeTypeId, String propertyName);

    /**
     * Checks if is have property node.
     *
     * @return true, if is have property node
     */
    boolean isHavePropertyNode();

    /**
     * Gets the statistic params.
     *
     * @param type the type
     * @return the statistic params
     */
    Map<String, Object> getStatisticParams(INodeType type);
    
    /**
     * Gets identity field
     *
     * @return the identity fields
     */
    String[] getIdentityFields();
}
