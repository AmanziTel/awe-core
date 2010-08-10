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

import org.neo4j.graphdb.RelationshipType;
//TODO union with new DB relations
/**
 * <p>
 *Gpeh relationship type
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public enum GpehRelationshipType implements RelationshipType {
    GPEH_STATISTICS, Ul_RTWP, TOTAL_DL_TX_POWER, R99_DL_TX_POWER, HS_DL_TX_RequiredPower, UE_TX_POWER, RRC;
}
