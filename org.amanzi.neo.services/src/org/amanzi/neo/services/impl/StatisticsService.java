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

package org.amanzi.neo.services.impl;

import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.IStatisticsService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.internal.AbstractService;
import org.amanzi.neo.services.impl.statistics.IPropertyStatistics;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class StatisticsService extends AbstractService implements IStatisticsService {

    private final INodeService nodeService;

    /**
     * @param graphDb
     * @param generalNodeProperties
     */
    protected StatisticsService(final GraphDatabaseService graphDb, final IGeneralNodeProperties generalNodeProperties,
            final INodeService nodeService) {
        super(graphDb, generalNodeProperties);

        this.nodeService = nodeService;
    }

    @Override
    public synchronized void saveStatistics(final Node node, final IPropertyStatistics vault) throws ServiceException {
        // TODO Auto-generated method stub

    }

    @Override
    public synchronized IPropertyStatistics loadStatistics(final Node rootNode) throws ServiceException {
        // TODO Auto-generated method stub
        return null;
    }

}
