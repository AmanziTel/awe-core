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

package org.amanzi.awe.statistics.service;

import java.util.Iterator;

import org.amanzi.awe.statistics.model.DimensionType;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.internal.IService;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface IStatisticsService extends IService {

    Node findStatisticsNode(Node parentNode, String templateName, String aggregationPropertyName) throws ServiceException;

    Node getStatisticsLevel(Node parentNode, DimensionType dimensionType, String propertyName) throws ServiceException;

    Node findStatisticsLevel(Node parentNode, DimensionType dimensionType, String propertyName) throws ServiceException;

    Node getGroup(Node propertyLevelNode, Node periodLevelNode) throws ServiceException;

    void addSourceNode(Node node, Node sourceNode) throws ServiceException;

    String getStatisticsLevelName(Node groupNode, DimensionType dimensionType) throws ServiceException;

    Iterator<Node> findAllStatisticsNode(Node parentNode) throws ServiceException;

    Iterator<Node> findAllStatisticsLevelNode(Node parentNode, DimensionType dimensionType) throws ServiceException;
}
