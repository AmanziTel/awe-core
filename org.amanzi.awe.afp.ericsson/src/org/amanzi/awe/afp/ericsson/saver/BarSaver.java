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

package org.amanzi.awe.afp.ericsson.saver;

import org.amanzi.awe.afp.ericsson.parser.RecordTransferData;
import org.amanzi.neo.loader.core.saver.Node2NodeSaver;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 *Bar saver
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class BarSaver extends Node2NodeSaver<RecordTransferData>{

    private NetworkModel networkModel;

    @Override
    public void init(RecordTransferData element) {
        super.init(element);
        networkModel = new NetworkModel(rootNode);
    }
    @Override
    protected Node defineNeigh(RecordTransferData element) {
        return null;
    }

    @Override
    protected Node defineServ(RecordTransferData element) {
         System.out.println(element.getRecord().getEvent().getProperties().keySet());
        return null;
    }

    @Override
    protected void storeHandledData(Relationship rel, RecordTransferData element) {
        
    }
    @Override
    protected void storeNonHandledData(Relationship rel, RecordTransferData element) {
       //do nothing;
    }

    @Override
    protected void definePropertyMap(RecordTransferData element) {
        
    }

    @Override
    public NodeToNodeRelationModel getModel(String neighbourName) {
        return networkModel.getInterferenceMatrix(neighbourName);
    }
    
}