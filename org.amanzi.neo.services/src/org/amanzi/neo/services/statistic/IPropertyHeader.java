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

import org.amanzi.neo.core.enums.NodeTypes;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public interface IPropertyHeader {

    String[] getNeighbourNumericFields(String neighbourName);

    /**
     *
     * @param neighbourName
     * @return
     */
    String[] getNeighbourAllFields(String neighbourName);

    /**
     *
     * @param neighbourName
     * @return
     */
    String[] getTransmissionAllFields(String neighbourName);

    /**
     *
     * @return
     */
    String[] getStringFields();

    /**
     *
     * @return
     */
    String[] getNumericFields();

    /**
     *
     * @return
     */
    String[] getAllFields();

    /**
     *
     * @return
     */
    String[] getIdentityFields();

    /**
     *
     * @return
     */
    String[] getAllChannels();

    /**
     *
     * @return
     */
    Collection<String> getNeighbourList();

    /**
     *
     * @param neighbourName
     * @return
     */
    String[] getNeighbourIntegerFields(String neighbourName);

    /**
     *
     * @param neighbourName
     * @return
     */
    String[] getTransmissionIntegerFields(String neighbourName);

    /**
     *
     * @param neighbourName
     * @return
     */
    String[] getNeighbourDoubleFields(String neighbourName);

    /**
     *
     * @return
     */
    Collection<String> getEvents();

    /**
     *
     * @param neighbourName
     * @return
     */
    String[] getTransmissionDoubleFields(String neighbourName);

    /**
     *
     * @param propertyName
     * @return
     */
    @Deprecated
    //TODO refactor for remove
    Node getPropertyNode(String propertyName);

    /**
     *
     * @return
     */
    String[] getSectorOrMeasurmentNames();

    /**
     *
     * @param propertyName
     * @return
     */
    @Deprecated
    //TODO refactor for remove
    PropertyHeader.PropertyStatistics getPropertyStatistic(String propertyName);

    /**
     *
     * @return
     */
    boolean isHavePropertyNode();

    /**
     *
     * @param type
     * @return
     */
    Map<String, Object> getStatisticParams(NodeTypes type);

}
