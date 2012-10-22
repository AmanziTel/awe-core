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

package org.amanzi.neo.services.statistics;

import java.util.Map;

import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeNotExistsException;
import org.amanzi.neo.services.IService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.statistics.internal.StatisticsVault;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
// TODO: LN: 10.10.2012, add
public interface IPropertyStatisticsService extends IService {

    void saveStatistics(Node node, StatisticsVault vault) throws ServiceException;

    StatisticsVault loadStatistics(Node rootNode) throws ServiceException, NodeTypeNotExistsException;

    void renameProperty(Node rootNode, INodeType nodeType, String propertyName, Object oldValue, Object newValue)
            throws ServiceException;

    void deleteProeprty(Node rootNode, INodeType nodeType, Map<String, Object> asMap) throws ServiceException;

}
