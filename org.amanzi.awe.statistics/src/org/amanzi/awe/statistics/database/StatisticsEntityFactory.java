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

package org.amanzi.awe.statistics.database;

import org.amanzi.awe.statistics.CallTimePeriods;
import org.amanzi.awe.statistics.database.entity.DatasetStatistics;
import org.amanzi.awe.statistics.database.entity.Dimension;
import org.amanzi.awe.statistics.database.entity.Level;
import org.amanzi.awe.statistics.database.entity.Statistics;
import org.amanzi.awe.statistics.database.entity.StatisticsCell;
import org.amanzi.awe.statistics.database.entity.StatisticsGroup;
import org.amanzi.awe.statistics.database.entity.StatisticsRow;
import org.amanzi.awe.statistics.template.Template;
import org.amanzi.awe.statistics.template.TemplateColumn;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * A factory that creates all types of statistics related entities
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class StatisticsEntityFactory {
    private static final StatisticsEntityFactory INSTANCE = new StatisticsEntityFactory();

    private StatisticsEntityFactory() {
    }

    public static StatisticsEntityFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a statistics root from scratch
     * 
     * @param service database service
     * @return the statistics group
     */
    public static DatasetStatistics createStatisticsRoot(GraphDatabaseService service,Template template,Node dataset) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            Node rootNode = service.createNode();
            rootNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.STATISTICS_ROOT.getId());
            DatasetStatistics datasetStatistics = new DatasetStatistics(rootNode);
            datasetStatistics.setDataset(dataset);
            datasetStatistics.setTemplateName(template.getTemplateName());
            
            Node networkDimensionNode = service.createNode();
            networkDimensionNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.DIMENSION.getId());
            
            Dimension networkDimension = new Dimension(networkDimensionNode);
            networkDimension.setName(DatasetStatistics.NETWORK_DIMENSION_NAME);
            
            Node timeDimensionNode = service.createNode();
            timeDimensionNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.DIMENSION.getId());
            
            Dimension timeDimension = new Dimension(timeDimensionNode);
            timeDimension.setName(DatasetStatistics.TIME_DIMENSION_NAME);
            
            datasetStatistics.addDimension(networkDimension);
            datasetStatistics.addDimension(timeDimension);
            
            return datasetStatistics; 
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    /**
     * Creates a statistics group from a node
     * 
     * @param node underlying node for the statistics group
     * @return the statistics group created
     */
    public static StatisticsGroup createStatisticsGroup(Node node) {
        return new StatisticsGroup(node);
    }

    public static StatisticsRow createStatisticsRow(GraphDatabaseService service, StatisticsGroup parent,Long startDate, CallTimePeriods period) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            Node rowNode = service.createNode();
            rowNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.S_ROW.getId());
            String name = NeoUtils.getFormatDateStringForSrow(startDate, period.addPeriod(startDate), "HH:mm", period.getId());
            rowNode.setProperty(INeoConstants.PROPERTY_TIME_NAME, startDate);
            rowNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
            return new StatisticsRow(rowNode,parent);
        } finally {
            NeoUtils.finishTx(tx);
        }

    }

    /**
     * Creates a statistics group
     * 
     * @return the statistics group
     */
    public static StatisticsGroup createStatisticsGroup(GraphDatabaseService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            Node groupNode = service.createNode();
            groupNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.S_GROUP.getId());
            return new StatisticsGroup(groupNode);
        } finally {
            NeoUtils.finishTx(tx);
        }

    }

    /**
     * Creates a statistics cell from scratch
     * 
     * @param service database service
     * @param column template column for cell to be created
     * @return the statistics cell
     */
    public static StatisticsCell createStatisticsCell(GraphDatabaseService service, StatisticsRow parent,TemplateColumn column) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            Node cellNode = service.createNode();
            cellNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, column.getName());
            cellNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.S_CELL.getId());
            return new StatisticsCell(cellNode, parent, column);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    public static StatisticsRow createSummaryRow(GraphDatabaseService service,StatisticsGroup parent) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            Node summaryNode = service.createNode();
            summaryNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, StatisticsRow.TOTAL);
            summaryNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.S_ROW.getId());
            summaryNode.setProperty(INeoConstants.PROPERTY_SUMMARY_NAME, true);
            //TODO use setters
            return new StatisticsRow(summaryNode,parent);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }
    public static Statistics createStatistics(GraphDatabaseService service, Level networkLevel, Level timeLevel) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            Node statisticsNode = service.createNode();
            statisticsNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, networkLevel.getName()+", "+timeLevel.getName());
//            networkLevel.addStatistics(stat)
            statisticsNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.STATISTICS.getId());
            //TODO use setters
            Statistics stat = new Statistics(statisticsNode);
            networkLevel.addStatistics(stat);
            timeLevel.addStatistics(stat);
            return stat;
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    public static Level createStatisticsLevel(GraphDatabaseService service, String levelKey) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            Node levelNode = service.createNode();
            levelNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, levelKey);
            levelNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.LEVEL.getId());
            //TODO use setters
            return new Level(levelNode);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }
}
