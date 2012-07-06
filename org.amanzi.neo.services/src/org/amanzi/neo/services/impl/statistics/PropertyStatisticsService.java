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

package org.amanzi.neo.services.impl.statistics;

import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.NodeService.NodeServiceRelationshipType;
import org.amanzi.neo.services.impl.internal.AbstractService;
import org.amanzi.neo.services.impl.statistics.internal.NodeTypeVault;
import org.amanzi.neo.services.impl.statistics.internal.PropertyVault;
import org.amanzi.neo.services.impl.statistics.internal.StatisticsVault;
import org.amanzi.neo.services.statistics.IPropertyStatisticsNodeProperties;
import org.amanzi.neo.services.statistics.IPropertyStatisticsService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class PropertyStatisticsService extends AbstractService implements IPropertyStatisticsService {

    public enum PropertyStatisticsRelationshipType implements RelationshipType {
        PROPERTY_STATISTICS;
    }

    private final INodeService nodeService;

    private final IPropertyStatisticsNodeProperties statisticsNodeProperties;

    /**
     * @param graphDb
     * @param generalNodeProperties
     */
    protected PropertyStatisticsService(final GraphDatabaseService graphDb, final IGeneralNodeProperties generalNodeProperties,
            final INodeService nodeService, final IPropertyStatisticsNodeProperties statisticsNodeProperties) {
        super(graphDb, generalNodeProperties);

        this.nodeService = nodeService;
        this.statisticsNodeProperties = statisticsNodeProperties;
    }

    @Override
    public synchronized void saveStatistics(final Node node, final IPropertyStatistics vault) throws ServiceException {
        assert node != null;
        assert vault != null;

        Node statisticsNode = getStatisticsNode(node);

        if (vault instanceof StatisticsVault) {
            saveStatisticsVault(statisticsNode, (StatisticsVault)vault);
        }
    }

    @Override
    public synchronized IPropertyStatistics loadStatistics(final Node rootNode) throws ServiceException {
        assert rootNode != null;

        Node statisticsNode = getStatisticsNode(rootNode);

        IPropertyStatistics result = loadStatisticsVault(statisticsNode);

        return result;
    }

    protected void saveStatisticsVault(Node node, StatisticsVault vault) throws ServiceException {
        if (vault.isChanged()) {
            updateStatisticsInfo(node, vault);

            for (NodeTypeVault nodeTypeVault : vault.getAllNodeTypeVaults()) {
                if (nodeTypeVault.isChanged()) {
                    saveNodeTypeVault(node, nodeTypeVault);
                }
            }
        }
    }

    protected void saveNodeTypeVault(Node statisticsNode, NodeTypeVault vault) throws ServiceException {
        Node nodeTypeVault = updateNodeTypeVault(statisticsNode, vault);

        for (PropertyVault propertyVault : vault.getAllPropertyVaults()) {
            if (propertyVault.isChanged()) {
                savePropertyStatistics(nodeTypeVault, propertyVault);
            }
        }
    }

    protected void savePropertyStatistics(Node nodeTypeVault, PropertyVault vault) {

    }

    protected Node updateNodeTypeVault(Node statisticsNode, NodeTypeVault vault) throws ServiceException {
        Node vaultNode = getChildVaultNode(statisticsNode, vault.getNodeType().getId());

        if (vaultNode == null) {
            vaultNode = createChildVaultNode(statisticsNode, vault.getNodeType().getId());
        }

        Transaction tx = getGraphDb().beginTx();
        try {
            vaultNode.setProperty(statisticsNodeProperties.getCountProperty(), vault.getCount());
            tx.success();
        } catch (Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

        return vaultNode;
    }

    protected void updateStatisticsInfo(Node node, StatisticsVault vault) throws ServiceException {
        Transaction tx = getGraphDb().beginTx();

        try {
            node.setProperty(statisticsNodeProperties.getCountProperty(), vault.getCount());

            tx.success();
        } catch (Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
    }

    protected StatisticsVault loadStatisticsVault(Node node) {
        return null;
    }

    protected Node getStatisticsNode(Node datasetNode) throws ServiceException {
        Node result = nodeService.getSingleChild(datasetNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);

        if (result == null) {
            result = nodeService.createNode(datasetNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                    PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
        }

        return result;
    }

    private Node getChildVaultNode(Node parentVaultNode, String name) throws ServiceException {
        return nodeService.getChildByName(parentVaultNode, name, PropertyStatisticsNodeType.STATISTICS_VAULT);
    }

    private Node createChildVaultNode(Node parentVaultNode, String name) throws ServiceException {
        return nodeService.createNode(parentVaultNode, PropertyStatisticsNodeType.STATISTICS_VAULT,
                NodeServiceRelationshipType.CHILD, name);
    }

}
