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

package org.amanzi.neo.models.impl.statistics;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.statistics.internal.NodeTypeVault;
import org.amanzi.neo.services.impl.statistics.internal.StatisticsVault;
import org.amanzi.neo.services.statistics.IPropertyStatisticsService;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class PropertyStatisticsModel extends AbstractModel implements IPropertyStatisticsModel {

    private static final Logger LOGGER = Logger.getLogger(PropertyStatisticsModel.class);

    private final IPropertyStatisticsService statisticsService;

    private StatisticsVault statisticsVault;

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public PropertyStatisticsModel(final IGeneralNodeProperties generalNodeProperties,
            final IPropertyStatisticsService statisticsService) {
        super(null, generalNodeProperties);

        this.statisticsService = statisticsService;
    }

    @Override
    protected void initialize(final Node parentNode, final String name, final INodeType nodeType) throws ModelException {
        throw new UnsupportedOperationException("PropertyStatisticsModel can be initialized only with node");
    }

    @Override
    public void initialize(final Node rootNode) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("initialize", rootNode));
        }

        try {
            statisticsVault = statisticsService.loadStatistics(rootNode);

            setRootNode(rootNode);
        } catch (Exception e) {
            processException("An error occured on initialization of PropertyStatistics Model", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("initialize"));
        }
    }

    @Override
    public void finishUp() throws ModelException {
        LOGGER.info("Finishing up model <" + getName() + ">");
        assert statisticsVault != null;

        try {
            statisticsService.saveStatistics(getRootNode(), statisticsVault);
        } catch (ServiceException e) {
            processException("An error occured on saving Statistics", e);
        }
    }

    @Override
    public void indexElement(final INodeType nodeType, final Map<String, Object> properties) throws ServiceException {
        statisticsVault.indexElement(nodeType, properties);
    }

    @Override
    public Set<String> getPropertyNames() {
        return statisticsVault.getPropertyNames();
    }

    @Override
    public Set<String> getPropertyNames(final INodeType nodeType) {
        return statisticsVault.getPropertyNames(nodeType);
    }

    @Override
    public int getCount() {
        return statisticsVault.getCount();
    }

    @Override
    public int getCount(final INodeType nodeType) {
        return statisticsVault.getCount(nodeType);
    }

    @Override
    public Set<Object> getValues(final INodeType nodeType, final String property) {
        return statisticsVault.getValues(nodeType, property);
    }

    @Override
    public int getValueCount(final INodeType nodeType, final String property, final Object value) {
        return statisticsVault.getValueCount(nodeType, property, value);
    }

    @Override
    public Set<INodeType> getNodeTypes() {
        Set<INodeType> result = new HashSet<INodeType>();

        for (NodeTypeVault vault : statisticsVault.getAllNodeTypeVaults()) {
            result.add(vault.getNodeType());
        }

        return result;
    }

    @Override
    public Class<?> getPropertyClass(final INodeType nodeType, final String property) {
        return statisticsVault.getPropertyClass(nodeType, property);
    }

}
