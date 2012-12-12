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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.distribution.dto.IAggregationRelation;
import org.amanzi.awe.distribution.dto.impl.AggregationRelation;
import org.amanzi.awe.distribution.model.DistributionNodeType;
import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.distribution.model.bar.IDistributionBar;
import org.amanzi.awe.distribution.model.bar.impl.DistributionBar;
import org.amanzi.awe.distribution.model.type.IDistributionType.Select;
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
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeManager;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.statistics.IPropertyStatisticsNodeProperties;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import com.google.common.collect.Iterables;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionModel extends AbstractAnalyzisModel<IPropertyStatisticalModel> implements IDistributionModel {

    private static final Logger LOGGER = Logger.getLogger(DistributionModel.class);

    /**
     * Default color for Left Bar
     */
    private static final Color DEFAULT_LEFT_COLOR = Color.RED;

    /**
     * Default color for Right Bar
     */
    private static final Color DEFAULT_RIGHT_COLOR = Color.GREEN;

    /**
     * Default color for Selected Bar
     */
    private static final Color DEFAULT_MIDDLE_COLOR = Color.RED;

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

    private final IPropertyStatisticsNodeProperties countNodePropeties;

    private String propertyName;

    private Color leftColor;

    private Color rightColor;

    private Color middleColor;

    private Select select;

    private INodeType distributionNodeType;

    private final Map<Long, IDistributionBar> distributionBarCache = new HashMap<Long, IDistributionBar>();

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public DistributionModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties,
            final IDistributionService distributionService, final IDistributionNodeProperties distributionNodeProperties,
            final IPropertyStatisticsNodeProperties countNodeProperties) {
        super(nodeService, generalNodeProperties);

        this.distributionService = distributionService;
        this.distributionNodeProperties = distributionNodeProperties;
        this.countNodePropeties = countNodeProperties;
    }

    @Override
    public void initialize(final Node rootNode) throws ModelException {
        super.initialize(rootNode);

        try {
            leftColor = getColorFromDatabase(rootNode, distributionNodeProperties.getLeftColor(), DEFAULT_LEFT_COLOR);
            rightColor = getColorFromDatabase(rootNode, distributionNodeProperties.getRightColor(), DEFAULT_RIGHT_COLOR);
            middleColor = getColorFromDatabase(rootNode, distributionNodeProperties.getMiddleColor(), DEFAULT_MIDDLE_COLOR);

            propertyName = getNodeService().getNodeProperty(rootNode, distributionNodeProperties.getDistributionPropertyName(),
                    null, true);
            select = Select.valueOf(getNodeService().getNodeProperty(rootNode, distributionNodeProperties.getDistributionSelect(),
                    null, true).toString());
            final String sNodeType = getNodeService().getNodeProperty(rootNode,
                    distributionNodeProperties.getDistributionNodeType(), null, true);
            distributionNodeType = NodeTypeManager.getInstance().getType(sNodeType);
        } catch (final Exception e) {
            processException("Error on get default Distribution Model properties", e);
        }
    }

    private Color getColorFromDatabase(final Node node, final String propertyName, final Color defaultColor)
            throws ServiceException {
        final int[] rgbColor = getNodeService().getNodeProperty(node, propertyName, null, false);

        if (rgbColor == null) {
            return defaultColor;
        } else {
            return convertArrayToColor(rgbColor);
        }
    }

    @Override
    public void finishUp() throws ModelException {
        if (distributionBars != null) {
            for (final IDistributionBar bar : distributionBars) {
                updateBar(bar);
            }
        }

        try {
            getNodeService().updateProperty(getRootNode(), distributionNodeProperties.getLeftColor(),
                    convertColorToArray(leftColor));
            getNodeService().updateProperty(getRootNode(), distributionNodeProperties.getRightColor(),
                    convertColorToArray(rightColor));
            getNodeService().updateProperty(getRootNode(), distributionNodeProperties.getMiddleColor(),
                    convertColorToArray(middleColor));
        } catch (final ServiceException e) {
            processException("Error on finishing up Distribution Model", e);
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
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("initializeDistributionBars"));
        }

        distributionBars = new ArrayList<IDistributionBar>();

        try {
            final Iterator<Node> distributionNodes = getNodeService().getChildrenChain(getRootNode(),
                    DistributionNodeType.DISTRIBUTION_BAR);

            while (distributionNodes.hasNext()) {
                distributionBars.add(createDistributionBar(distributionNodes.next()));
            }
        } catch (final ServiceException e) {
            processException("Error on collecting Distribution Bars from Database", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("initializeDistributionBars"));
        }
    }

    @Override
    public void setCurrent(final boolean isCurrent) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("setCurrent", isCurrent));
        }

        try {
            distributionService.setCurrentDistribution(getParentNode(), getRootNode());
        } catch (final ServiceException e) {
            processException("Error occured on setting current Distribution Model for " + getSourceModel(), e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("setCurrent"));
        }
    }

    protected void updateBar(final IDistributionBar bar) throws ModelException {
        final Node barNode = ((DistributionBar)bar).getNode();

        try {
            if (bar.getColor() != null) {
                getNodeService().updateProperty(barNode, distributionNodeProperties.getBarColor(),
                        convertColorToArray(bar.getColor()));
            }

            getNodeService().updateProperty(barNode, getGeneralNodeProperties().getSizeProperty(), bar.getCount());
            getNodeService().updateProperty(barNode, getGeneralNodeProperties().getNodeNameProperty(), bar.getName());
        } catch (final ServiceException e) {
            processException("Error occured on updating properties of bar <" + bar + ">", e);
        }
    }

    @Override
    public IDistributionBar createDistributionBar(final IRange range) throws ModelException {
        DistributionBar result = null;
        try {
            final Node barNode = distributionService.createDistributionBarNode(getRootNode(), range.getName(),
                    range.getColor() == null ? null : convertColorToArray(range.getColor()));

            result = new DistributionBar(barNode, collectBarSources);
            result.setNodeType(DistributionNodeType.DISTRIBUTION_BAR);

            if (range.getColor() != null) {
                result.setColor(range.getColor());
            }
            result.setName(range.getName());

            if (distributionBars == null) {
                distributionBars = new ArrayList<IDistributionBar>();
            }
            distributionBars.add(result);

            distributionBarCache.put(barNode.getId(), result);
        } catch (final ServiceException e) {
            processException("Exception on creating Distribution Bar", e);
        }

        return result;
    }

    @Override
    public IAggregationRelation createAggregation(final IDistributionBar bar, final IDataElement element) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("createAggregation", bar, element));
        }

        final DistributionBar distributionBar = (DistributionBar)bar;
        final DataElement dataElement = (DataElement)element;

        AggregationRelation result = null;

        try {
            final Relationship relation = getNodeService().linkNodes(distributionBar.getNode(), dataElement.getNode(),
                    DistributionRelationshipType.AGGREGATED);

            result = new AggregationRelation(relation);
        } catch (final ServiceException e) {
            processException("Error occured on adding Aggregation between nodes", e);
        }

        distributionBar.setCount(distributionBar.getCount() + 1);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("createAggregation"));
        }

        return result;
    }

    private static int[] convertColorToArray(final Color color) {
        return new int[] {color.getRed(), color.getGreen(), color.getBlue()};
    }

    private static Color convertArrayToColor(final int[] colorArray) {
        return new Color(colorArray[0], colorArray[1], colorArray[2]);
    }

    @Override
    protected RelationshipType getRelationToParent() {
        return DistributionRelationshipType.DISTRIBUTION;
    }

    /**
     * @return Returns the leftColor.
     */
    @Override
    public Color getLeftColor() {
        return leftColor;
    }

    /**
     * @param leftColor The leftColor to set.
     */
    @Override
    public void setLeftColor(final Color leftColor) {
        this.leftColor = leftColor;
    }

    /**
     * @return Returns the rightColor.
     */
    @Override
    public Color getRightColor() {
        return rightColor;
    }

    /**
     * @param rightColor The rightColor to set.
     */
    @Override
    public void setRightColor(final Color rightColor) {
        this.rightColor = rightColor;
    }

    /**
     * @return Returns the middleColor.
     */
    @Override
    public Color getMiddleColor() {
        return middleColor;
    }

    /**
     * @param middleColor The middleColor to set.
     */
    @Override
    public void setMiddleColor(final Color middleColor) {
        this.middleColor = middleColor;
    }

    @Override
    public IDistributionBar findDistributionBar(final IDataElement dataElement) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("findDistributionBar", dataElement));
        }

        // TODO: LN: 8.10.2012, validate input

        IDistributionBar result = null;

        try {
            final Node distributionBarNode = distributionService.findDistributionBar(getRootNode(),
                    ((DataElement)dataElement).getNode());

            if (distributionBarNode != null) {
                result = distributionBarCache.get(distributionBarNode.getId());

                if (result == null) {
                    result = createDistributionBar(distributionBarNode);

                    distributionBarCache.put(distributionBarNode.getId(), result);
                }
            }
        } catch (final ServiceException e) {
            processException("Error on computing Distribution Bar from Source Element", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("findDistributionBar"));
        }
        return result;
    }

    protected DistributionBar createDistributionBar(final Node distributionBarNode) throws ServiceException {
        final DistributionBar result = new DistributionBar(distributionBarNode, collectBarSources);

        result.setColor(getColorFromDatabase(distributionBarNode, distributionNodeProperties.getBarColor(), null));
        result.setNodeType(DistributionNodeType.DISTRIBUTION_BAR);
        result.setName(getNodeService().getNodeName(distributionBarNode));
        result.setCount((Integer)getNodeService().getNodeProperty(distributionBarNode,
                getGeneralNodeProperties().getSizeProperty(), null, true));

        return result;
    }

    @Override
    public IAggregationRelation findAggregationRelation(final IDataElement dataElement) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("findAggregationRelation", dataElement));
        }

        // TODO: LN: 9.10.2012, validate input

        final DataElement element = (DataElement)dataElement;

        IAggregationRelation result = null;

        try {
            final DistributionBar distributionBar = (DistributionBar)findDistributionBar(element);

            if (distributionBar != null) {
                final Relationship relation = getNodeService().findLinkBetweenNodes(element.getNode(), distributionBar.getNode(),
                        DistributionRelationshipType.AGGREGATED, Direction.INCOMING);

                if (relation != null) {
                    result = initializeAggregationRelation(relation);
                }
            }
        } catch (final ServiceException e) {

        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("findAggregationRelation"));
        }
        return result;
    }

    protected AggregationRelation initializeAggregationRelation(final Relationship relationship) throws ServiceException {
        final AggregationRelation result = new AggregationRelation(relationship);

        result.setCount(getNodeService().getRelationshipProperty(relationship, countNodePropeties.getCountProperty(), 0, false));
        result.setValue(getNodeService().getRelationshipProperty(relationship, countNodePropeties.getValuePrefix(), 0d, false));

        return result;
    }

    @Override
    public void updateAggregationRelation(final IDataElement dataElement, final IAggregationRelation relation,
            final IDistributionBar bar) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("updateAggregationRelation", relation, bar));
        }

        // TODO: LN: 9.10.2012, validate input

        AggregationRelation relationImpl = (AggregationRelation)relation;
        final DistributionBar barImpl = (DistributionBar)bar;
        final DataElement elementImpl = (DataElement)dataElement;

        try {
            if (!relationImpl.getRelation().getOtherNode(elementImpl.getNode()).equals(barImpl.getNode())) {
                getNodeService().deleteRelationship(relationImpl.getRelation());

                relationImpl = (AggregationRelation)createAggregation(barImpl, elementImpl);
            }

            getNodeService().updateProperty(relationImpl.getRelation(), countNodePropeties.getCountProperty(), relation.getCount());
            getNodeService().updateProperty(relationImpl.getRelation(), countNodePropeties.getValuePrefix(), relation.getValue());
        } catch (final ServiceException e) {
            processException("Error on updating Aggregation property", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("updateAggregationRelation"));
        }

    }

    @Override
    public IDataElement getParentElement(final IDataElement childElement) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getParentElement", childElement));
        }

        IDataElement result = null;

        if (childElement.getNodeType().equals(DistributionNodeType.DISTRIBUTION_BAR)) {
            try {
                final DataElement element = (DataElement)childElement;

                final Node parentNode = getNodeService().getChainParent(element.getNode());

                if (parentNode.equals(getRootNode())) {
                    result = asDataElement();
                }
            } catch (final ServiceException e) {
                processException("Error on computing parent of element", e);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getParentElement"));
        }
        return result;
    }

    @Override
    public Iterable<IDataElement> getChildren(final IDataElement parentElement) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getChildren", parentElement));
        }

        Iterable<IDataElement> result = null;

        if (parentElement.equals(asDataElement())) {
            result = new DataElementConverter<IDistributionBar>(getDistributionBars()).toIterable();
        } else if (parentElement.getNodeType().equals(DistributionNodeType.DISTRIBUTION_BAR)) {
            try {
                final DataElement element = (DataElement)parentElement;

                final Iterator<Node> nodeIterator = getNodeService().getChildren(element.getNode(), distributionNodeType,
                        DistributionRelationshipType.AGGREGATED);

                result = new DataElementIterator(nodeIterator).toIterable();
            } catch (final ServiceException e) {
                processException("Error on collecting Distribution Bar sources", e);
            }
        } else {
            result = Iterables.emptyIterable();
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getChildren"));
        }

        return result;
    }

    @Override
    public String getName() {
        final StringBuilder builder = new StringBuilder();

        builder.append(getSourceModel().getName() + " {" + propertyName + " - " + super.getName() + " - " + select + "}");

        return builder.toString();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append(super.getName());
        builder.append(" distribution for ");
        builder.append(getSourceModel().getName());
        builder.append(" on property ");
        builder.append(propertyName);

        if (select != null) {
            builder.append(" with selection ");
            builder.append(select);
        }

        return builder.toString();
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public INodeType getDistributionNodeType() {
        return distributionNodeType;
    }
}
