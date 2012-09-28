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

package org.amanzi.awe.distribution.model.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.distribution.model.bar.IDistributionBar;
import org.amanzi.awe.distribution.model.bar.impl.DistributionBar;
import org.amanzi.awe.distribution.model.type.IRange;
import org.amanzi.awe.distribution.properties.IDistributionNodeProperties;
import org.amanzi.awe.distribution.service.IDistributionService;
import org.amanzi.awe.distribution.service.impl.DistributionService.DistributionRelationshipType;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.impl.dto.DataElement;
import org.amanzi.neo.impl.dto.SourcedElement.ICollectFunction;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractAnalyzisModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionModel extends AbstractAnalyzisModel<IPropertyStatisticalModel> implements IDistributionModel {

    private static final Logger LOGGER = Logger.getLogger(DistributionModel.class);

    private final ICollectFunction collectBarSources = new ICollectFunction() {

        @Override
        public Iterable<IDataElement> collectSourceElements(final IDataElement parent) {
            // TODO Auto-generated method stub
            return null;
        }
    };

    private List<IDistributionBar> distributionBars;

    private final IDistributionService distributionService;

    private final IDistributionNodeProperties distributionNodeProperties;

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public DistributionModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties, final IDistributionService distributionService, final IDistributionNodeProperties distributionNodeProperties) {
        super(nodeService, generalNodeProperties);

        this.distributionService = distributionService;
        this.distributionNodeProperties = distributionNodeProperties;
    }

    @Override
    public void finishUp() throws ModelException {
        if (distributionBars != null) {
            for (IDistributionBar bar : distributionBars) {
                updateBar(bar);
            }
        }
    }

    @Override
    public List<IDistributionBar> getDistributionBars() throws ModelException {
        if (distributionBars == null) {
            initializeDistributionBars();
        }

        return distributionBars;
    }

    protected void initializeDistributionBars() throws ModelException {

    }

    @Override
    public void setCurrent(final boolean isCurrent) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("setCurrent", isCurrent));
        }

        try {
            distributionService.setCurrentDistribution(getParentNode(), getRootNode());
        } catch (ServiceException e) {
            processException("Error occured on setting current Distribution Model for " + getSourceModel(), e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("setCurrent"));
        }
    }

    protected void updateBar(final IDistributionBar bar) throws ModelException {
        Node barNode = ((DistributionBar)bar).getNode();

        try {
            if (bar.getColor() != null) {
                getNodeService().updateProperty(barNode, distributionNodeProperties.getBarColor(), convertColorToArray(bar.getColor()));
            }

            getNodeService().updateProperty(barNode, getGeneralNodeProperties().getSizeProperty(), bar.getCount());
            getNodeService().updateProperty(barNode, getGeneralNodeProperties().getNodeNameProperty(), bar.getName());
        } catch (ServiceException e) {
            processException("Error occured on updating properties of bar <" + bar + ">", e);
        }
    }

    @Override
    public IDistributionBar createDistributionBar(final IRange range) throws ModelException {
        DistributionBar result = null;
        try {
            Node barNode = distributionService.createDistributionBarNode(getRootNode(), range.getName(), range.getColor() == null ? null : convertColorToArray(range.getColor()));

            result = new DistributionBar(barNode, collectBarSources);

            if (range.getColor() != null) {
                result.setColor(range.getColor());
            }
            result.setName(range.getName());

            if (distributionBars == null) {
                distributionBars = new ArrayList<IDistributionBar>();
            }
            distributionBars.add(result);
        } catch (ServiceException e) {
            processException("Exception on creating Distribution Bar", e);
        }

        return result;
    }

    @Override
    public void createAggregation(final IDistributionBar bar, final IDataElement element) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("createAggregation", bar, element));
        }

        DistributionBar distributionBar = (DistributionBar)bar;
        DataElement dataElement = (DataElement)element;

        try {
            getNodeService().linkNodes(distributionBar.getNode(), dataElement.getNode(), DistributionRelationshipType.AGGREGATED);
        } catch (ServiceException e) {
            processException("Error occured on adding Aggregation between nodes", e);
        }

        distributionBar.setCount(distributionBar.getCount() + 1);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("createAggregation"));
        }
    }

    private static int[] convertColorToArray(final Color color) {
        return new int[] {color.getRed(), color.getGreen(), color.getBlue()};
    }

    @Override
    protected RelationshipType getRelationToParent() {
        return DistributionRelationshipType.DISTRIBUTION;
    }
}
