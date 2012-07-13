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

package org.amanzi.awe.statistics.model;

import org.amanzi.awe.statistics.service.StatisticsService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * common Entity functional
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractEntity {
    private static final Logger LOGGER = Logger.getLogger(AbstractEntity.class);
    protected static final String PROPERTY_FLAGGED_NAME = "flagged";
    /*
     * statistics service
     */
    static StatisticsService statisticService;

    /*
     * service instantiation
     */
    static void setStatisticsService(StatisticsService service) {
        statisticService = service;
    }

    /*
     * /** initialize statistics services
     */
    static void initStatisticsService() {
        if (statisticService == null) {
            statisticService = StatisticsService.getInstance();
        }
    }

    protected String name;
    protected Node rootNode;
    protected INodeType nodeType;
    protected Node parentNode;

    /**
     * instantiation
     * 
     * @param nodeType
     */
    protected AbstractEntity(INodeType nodeType) {
        initStatisticsService();
        if (nodeType == null) {
            LOGGER.error("incorrect node Type");
            throw new IllegalArgumentException("nodeType is null");
        }
        this.nodeType = nodeType;
    }

    /**
     * instantiation constructor
     * 
     * @param parent
     * @param current
     */
    protected AbstractEntity(Node parent, Node current, INodeType type) {
        this(type);
        initStatisticsService();
        if (parent == null) {
            LOGGER.error("parentNode can't be null");
            throw new IllegalArgumentException("Parent node cann't be null");
        }
        parentNode = parent;
        if (current == null) {
            LOGGER.error("current node can't be null");
            throw new IllegalArgumentException("current node can't be null");
        }
        rootNode = current;
        if (!type.getId().equals(statisticService.getType(rootNode))) {
            LOGGER.error("Unexpected node Type. Expected " + type.getId());
            throw new IllegalArgumentException("validation failed");
        }
        if (!validate()) {
            LOGGER.error("can't validate");
            throw new IllegalArgumentException("validation failed");
        }
        name = (String)statisticService.getNodeProperty(current, DatasetService.NAME);
    }

    public String getName() {
        return name;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public INodeType getType() {
        return nodeType;
    }

    public String toString() {
        return getName();
    }

    /**
     * additional validation
     * 
     * @return
     */
    protected boolean validate() {
        return true;
    }

    /**
     * @return Returns the parentNode.
     */
    Node getParentNode() {
        return parentNode;
    }

    /**
     * load child Entities
     * 
     * @throws DatabaseException
     */
    protected abstract void loadChildIfNecessary() throws DatabaseException;
}
