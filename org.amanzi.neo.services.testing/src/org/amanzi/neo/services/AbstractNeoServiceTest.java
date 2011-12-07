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

package org.amanzi.neo.services;

import java.awt.Color;

import org.amanzi.testing.AbstractAWETest;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 * Abstract class for tests on NeoServices and Models
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public abstract class AbstractNeoServiceTest extends AbstractAWETest {
    
    /**
     * Cleans up all relationships from ReferenceNode
     */
    protected void cleanUpReferenceNode() throws Exception {
        Transaction tx = graphDatabaseService.beginTx();
        try {
            for (Relationship relationship : graphDatabaseService.getReferenceNode().getRelationships()) {
                relationship.delete();
            }
            tx.success();
        } catch (Exception e) {
            tx.failure();            
            throw e;
        } finally {
            tx.finish();
        }
    }
    
    protected int[] getColorArray(Color color) {
        if (color != null) {
            return new int[] {color.getRed(), color.getGreen(), color.getBlue()};
        } else {
            return null;
        }
    }

}
