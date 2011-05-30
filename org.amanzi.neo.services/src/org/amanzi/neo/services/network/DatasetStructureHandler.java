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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.amanzi.neo.services.IDatasetService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * NetworkModelHandler
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class DatasetStructureHandler {
    private final String PROPERTY_NAME = "structure";
    private List<String> structure=new ArrayList<String>();
    private final IDatasetService service;
    private final Node baseNode;
    public DatasetStructureHandler(Node baseNode,IDatasetService service) {
        this.baseNode = baseNode;
        this.service = service;
        String[] structureArr = (String[])baseNode.getProperty(PROPERTY_NAME, null);
        if (structureArr == null) {
            structure.add(NodeTypes.NETWORK.getId());
        }else{
            structure.addAll(Arrays.asList(structureArr));
        }
    }

    /**
     * Gets the structure like array.
     * 
     * @return the structure like array
     */
    public String[] getStructureLikeArray() {
        return structure.toArray(new String[0]);
    }
    /**
     *
     * @param network
     * @param bsc
     */
    public void addType(INodeType parent, INodeType child) {
        int id = structure.indexOf(parent.getId());
        if (id<0){
            throw new IllegalArgumentException("Type '"+parent.getId()+"' not found");
        }
        int idChild = structure.indexOf(child.getId());
        if (idChild<0){
            structure.add(++id, child.getId());
        }
    }

    /**
     *
     */
    public void store() {
        service.setStructure(baseNode,getStructureLikeArray());
    }

    /**
     *
     * @param type
     * @return
     */
    public boolean contain(INodeType type) {
        return structure.contains(type.getId());
    }
}
