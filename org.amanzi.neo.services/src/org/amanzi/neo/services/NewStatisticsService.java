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

import java.util.ArrayList;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateStatisticsException;
import org.amanzi.neo.services.exceptions.InvalidStatisticsParameterException;
import org.amanzi.neo.services.statistic.IVault;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kruglik_A
 * @since 1.0.0
 */
public class NewStatisticsService extends NewAbstractService {

    public static final String CLASS = "class";
    public static final String COUNT = "count";

    private static Logger LOGGER = Logger.getLogger(NewAbstractService.class);
    private Transaction tx;

    public enum StatisticsRelationships implements RelationshipType {
        STATISTICS, CHILD;
    }

    public enum StatisticsNodeTypes implements INodeType {
        VAULT;

        @Override
        public String getId() {
            return name();
        }
    }

    public void saveVault(Node rootNode, IVault vault) throws DatabaseException, InvalidStatisticsParameterException,
            DuplicateStatisticsException {
        LOGGER.debug("start method saveVault(Node rootNode, IVault vault)");
        if (rootNode == null) {
            throw new InvalidStatisticsParameterException("rootNode", rootNode);
        }
        if (vault == null) {
            throw new InvalidStatisticsParameterException("vault", vault);
        }
        if (rootNode.getRelationships(StatisticsRelationships.STATISTICS, Direction.OUTGOING).iterator().hasNext()) {
            throw new DuplicateStatisticsException("for this rootNode already exists statistics");
        }
        Node vaultNode = createNode(StatisticsNodeTypes.VAULT);
        tx = graphDb.beginTx();
        try {
            if (StatisticsNodeTypes.VAULT.getId().equals(getNodeType(rootNode))) {
                rootNode.createRelationshipTo(vaultNode, StatisticsRelationships.CHILD);
            } else {
                rootNode.createRelationshipTo(vaultNode, StatisticsRelationships.STATISTICS);
            }
            vaultNode.setProperty(PROPERTY_NAME_NAME, vault.getType());
            vaultNode.setProperty(COUNT, vault.getCount());
            vaultNode.setProperty(CLASS, vault.getClass().toString());
            for (IVault subVault : vault.getSubVaults()) {
                saveVault(vaultNode, subVault);
            }
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not create vault node in database", e);
            tx.failure();
            throw new DatabaseException(e);

        } finally {
            tx.finish();
            LOGGER.debug("finishmethod saveVault(Node rootNode, IVault vault)");
        }

    }

    public IVault loadVault(Node rootNode) {
        return null;
    }
}
