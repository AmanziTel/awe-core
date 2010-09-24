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

package org.amanzi.neo.services.statistic.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.services.statistic.IPropertyHeader;
import org.amanzi.neo.services.statistic.ISinglePropertyStat;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.statistic.StatisticManager;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class PropertyHeaderImpl implements IPropertyHeader {

    private final Node node;
    private final String key;
    private IStatistic stat;


    public PropertyHeaderImpl(Node node, String key) {
        this.node = node;
        this.key = key;
        stat=StatisticManager.getStatistic(node);
    }

    @Override
    public String[] getNeighbourNumericFields(String neighbourName) {
        //TODO old version
        Node neighbour = NeoUtils.findNeighbour(node, neighbourName);
        if (neighbour == null) {
            return null;
        }
        String[] result = NeoUtils.getNumericFields(neighbour);
        return result;
    }

    @Override
    public String[] getNeighbourAllFields(String neighbourName) {
        Node neighbour = NeoUtils.findNeighbour(node, neighbourName);
        if (neighbour == null) {
            return null;
        }
        String[] result = NeoUtils.getAllFields(neighbour);
        return result;
    }

    @Override
    public String[] getTransmissionAllFields(String neighbourName) {
        Node neighbour = NeoUtils.findTransmission(node, neighbourName, NeoServiceProvider.getProvider().getService());
        if (neighbour == null) {
            return null;
        }
        String[] result = NeoUtils.getAllFields(neighbour);
        return result;
    }



    @Override
    public String[] getNumericFields(String nodeTypeId) {
        //TODO remove "-main-type-" from AWE code
        if ("-main-type-".equals(nodeTypeId)){
            if (NodeTypes.NETWORK.checkNode(node)){
                nodeTypeId=NodeTypes.SECTOR.getId();
            }else{
                nodeTypeId=NodeTypes.M.getId();
            }
        }
        
        Collection<String> result=stat.getPropertyNameCollection(key,nodeTypeId,new Comparable<Class>() {

            @Override
            public int compareTo(Class o) {
                return Number.class.isAssignableFrom(o)?0:-1;
            }
        });
        return result.toArray(new String[0]);
    }

    @Override
    public String[] getAllFields(String nodeTypeId) {
        //TODO remove "-main-type-" from AWE code
        if ("-main-type-".equals(nodeTypeId)){
            if (NodeTypes.NETWORK.checkNode(node)){
                nodeTypeId=NodeTypes.SECTOR.getId();
            }else{
                nodeTypeId=NodeTypes.M.getId();
            }
        }
        
        Collection<String> result=stat.getPropertyNameCollection(key,nodeTypeId,new Comparable<Class>() {

            @Override
            public int compareTo(Class o) {
                return 0;
            }
        });
        return result.toArray(new String[0]);
    }



    @Override
    public String[] getAllChannels() {
        //TODO implement!
        return null;
    }

    @Override
    public Collection<String> getNeighbourList() {
        List<String> result = new ArrayList<String>();
        Iterable<Relationship> neighb = node.getRelationships(NetworkRelationshipTypes.NEIGHBOUR_DATA, Direction.OUTGOING);
        for (Relationship relationship : neighb) {
            result.add(NeoUtils.getNeighbourPropertyName(NeoUtils.getSimpleNodeName(relationship.getOtherNode(node), "")));
        }
        return result;
    }

    @Override
    public String[] getNeighbourIntegerFields(String neighbourName) {
        Node neighbour = NeoUtils.findNeighbour(node, neighbourName);
        if (neighbour == null) {
            return null;
        }
        String[] result = (String[])neighbour.getProperty(INeoConstants.LIST_INTEGER_PROPERTIES, null);
        return result;
    }

    @Override
    public String[] getTransmissionIntegerFields(String neighbourName) {
        Node neighbour = NeoUtils.findTransmission(node, neighbourName, NeoServiceProvider.getProvider().getService());
        if (neighbour == null) {
            return null;
        }
        String[] result = (String[])neighbour.getProperty(INeoConstants.LIST_INTEGER_PROPERTIES, null);
        return result;
    }

    @Override
    public String[] getNeighbourDoubleFields(String neighbourName) {
        Node neighbour = NeoUtils.findNeighbour(node, neighbourName);
        if (neighbour == null) {
            return null;
        }
        String[] result = (String[])neighbour.getProperty(INeoConstants.LIST_DOUBLE_PROPERTIES, null);
        return result;
    }

    @Override
    public Collection<String> getEvents() {
        Set<String> result = new HashSet<String>();
        return result;
    }

    @Override
    public String[] getTransmissionDoubleFields(String neighbourName) {
        Node neighbour = NeoUtils.findTransmission(node, neighbourName, NeoServiceProvider.getProvider().getService());
        if (neighbour == null) {
            return null;
        }
        String[] result = (String[])neighbour.getProperty(INeoConstants.LIST_DOUBLE_PROPERTIES, null);
        return result;
    }



    @Override
    public ISinglePropertyStat getPropertyStatistic(String nodeTypeId, String propertyName) {
        return stat.findPropertyStatistic(key,nodeTypeId,propertyName);
    }

    @Override
    public boolean isHavePropertyNode() {
        return false;
    }

    @Override
    public Map<String, Object> getStatisticParams(NodeTypes type) {
        return null;
    }

}
