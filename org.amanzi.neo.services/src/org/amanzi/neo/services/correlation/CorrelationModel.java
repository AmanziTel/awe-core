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

package org.amanzi.neo.services.correlation;

import java.util.Set;

import org.neo4j.graphdb.Node;

/**
 * <p>
 * Data keeper for correlation
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class CorrelationModel {
    private final Node network;
    private Set<Node> datasets;
    
    public CorrelationModel(Node networkNode, Set<Node> datasets){
        network = networkNode;
        this.datasets = datasets;
        
    }

    /**
     * @return Returns the network.
     */
    public Node getNetwork() {
        return network;
    }

    /**
     * @return Returns the datasets.
     */
    public Set<Node> getDatasets() {
        return datasets;
    }

    /**
     * @param datasets The datasets to set.
     */
    public void setDatasets(Set<Node> datasets) {
        this.datasets = datasets;
    }    
    
    
    
    
//    public static CorrelationModel getInstance(Node networkNode) {
//        CorrelationModel model = new CorrelationModel(networkNode);
//        model.init();
//        return model;
//    }
//
//    private final Node network;
//    private final CorrelationService correlationService;
//    private Set<Node> datasets;
//    
//    private CorrelationModel(Node networkNode) {
//        this.network = networkNode;
//        correlationService = NeoServiceFactory.getInstance().getCorrelationService();
//    }
//    
//    private void init() {
////        correlationService.loadCorrelation();
//    }
//    
//    public void createCorrelation(Set<Node> datasets){
//        correlationService.correlate(network, datasets);
//    }
//    
//    

}
