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

package org.amanzi.awe.neighbours.gpeh;

import java.util.Set;

import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 *Contains information about interference cell
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class InterfCellInfo extends CellNodeInfo {

    private final String psc;
    private Boolean defNeigh;


    /**
     * Instantiates a new interf cell info.
     *
     * @param cellSector the cell sector
     * @param cellSectorInfo the cell sector info
     * @param psc the psc
     */
    public InterfCellInfo(Node cellSector, Node cellSectorInfo,String psc) {
        super(cellSector, cellSectorInfo);
        this.psc = psc;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((psc == null) ? 0 : psc.hashCode());
        return result;
    }

    /**
     * Gets the psc.
     *
     * @return the psc
     */
    public String getPsc() {
        return psc;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        InterfCellInfo other = (InterfCellInfo)obj;
        if (psc == null) {
            if (other.psc != null)
                return false;
        } else if (!psc.equals(other.psc))
            return false;
        return true;
    }


    /**
     * Checks if is difined neighbour.
     *
     * @param cellSector the cell sector
     * @param service the service
     * @return true, if is difined neighbour
     */
    public boolean isDifinedNeighbour(Node cellSector,GraphDatabaseService service) {
        if (defNeigh==null){
            Set<Relationship> relations = NeoUtils.getRelations(cellSector, getCellSector(), NetworkRelationshipTypes.NEIGHBOUR);
            defNeigh = !relations.isEmpty();
        }
        return defNeigh;
    }

}
