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

package org.amanzi.neo.loader.core.saver.impl;

import org.amanzi.neo.loader.core.parser.LineTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.networkselection.NetworkSelectionModel;
import org.amanzi.neo.services.networkselection.SelectionModel;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author NiCK
 * @since 1.0.0
 */
public class NetworkSelectionSaver extends AbstractHeaderSaver<LineTransferData> {
    private Node selectionNode;
    private int skippedCount;
    
    private boolean headerNotHandled = true;
    
    private SelectionModel selectionModel;

    @Override
    protected String getRootNodeType() {
        return NodeTypes.NETWORK.getId();
    }

    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return null;
    }

    @Override
    public Iterable<MetaData> getMetaData() {
        return null;
    }

    @Override
    protected void fillRootNode(Node rootNode, LineTransferData element) {
    }

    @Override
    public void save(LineTransferData element) {
        if (headerNotHandled) {
            NetworkModel networkModel = new NetworkModel(rootNode);
            selectionModel = networkModel.getSelectionModel(element.getFileName());
            
            headerNotHandled = false;
            return;
        }
        
        String sectorName = element.getStringLine();
        Node sector = findSector(sectorName);
        if (sector == null) {
            getPrintStream().println(String.format("Sector with name '%s' is not found in network '%s'.", sectorName, rootname));
            skippedCount++;
            return;
        }
        if (!selectionModel.addToSelection(sector)) {
            getPrintStream().println(String.format("Sector with name '%s' already exist in Selection List '%s'.", sectorName, selectionModel.getName()));
        }
    }

    private Node findSector(String sectorName) {
        return service.findSector(rootNode, null, null, sectorName, true);
    }

}
