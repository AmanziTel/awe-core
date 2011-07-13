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

package org.amanzi.awe.views.reuse.mess_table;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.DriveTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public enum DataTypes {

    ROMES(DriveTypes.ROMES,NodeTypes.M),
    NEMO1(DriveTypes.NEMO1,NodeTypes.M),
    NEMO2(DriveTypes.NEMO2,NodeTypes.M),
    TEMS(DriveTypes.TEMS,NodeTypes.M),
    MS(DriveTypes.MS,NodeTypes.M),
    OSS(null,NodeTypes.GPEH_EVENT),
    NETWORK_PLAN(null,NodeTypes.TRX),
    NETWORK(null,NodeTypes.SECTOR);
    
    private DriveTypes type;
    private NodeTypes childType;
    
    private DataTypes(DriveTypes aType,NodeTypes aChildType) {
        type = aType;
        childType = aChildType;
    }
    
    /**
     * @return Returns the type.
     */
    public DriveTypes getType() {
        return type;
    }
    
    /**
     * @return Returns the childType.
     */
    public NodeTypes getChildType() {
        return childType;
    }
    
    public static DataTypes getTypeByNode(Node aNode){
        DriveTypes key = NeoUtils.getDatasetType(aNode);
        if(key==null){
            if(NodeTypes.OSS.checkNode(aNode)){
                return OSS;
            }
            Object type = aNode.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, "").toString();
            if(NodeTypes.NETWORK.checkNode(aNode)){
                return NETWORK;
            }
            Logger.getLogger(DataTypes.class).error("DriveTypes not found for node "+aNode);
            return null;
        }
        for(DataTypes curr : values()){
            if(key.equals(curr.type)){
                return curr;
            }
        }
        return null;
    }
}
