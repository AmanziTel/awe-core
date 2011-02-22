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
package org.amanzi.neo.services.network;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * 
 * <p>
 *Model for frequency spectrum
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
//TODO now implemented for  using only one instance for one network
public class IllegalFrequencySpectrumModel {

    private final Node rootNode;
    private final Map<Integer,Node>cache=new HashMap<Integer, Node>();
    private NetworkService ns;
    IllegalFrequencySpectrumModel(Node frequencySpectrumRootNode) {
        ns=NeoServiceFactory.getInstance().getNetworkService();
        this.rootNode = frequencySpectrumRootNode;
        loadCache();
    }
    private void loadCache() {
        cache.clear();
        for(Relationship rel:rootNode.getRelationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)){
            Node node=rel.getOtherNode(rootNode);
            cache.put((Integer)node.getProperty(NetworkService.FREQUENCY), node);
        }
    }
    public Node getFrequencyNode(int frequency){
        Node result=cache.get(frequency);
        if (result!=null){
            return result;
        }
        result=ns.createFrSpectrimNode(rootNode,frequency);
        cache.put(frequency, result);
        return result;
    }
}
