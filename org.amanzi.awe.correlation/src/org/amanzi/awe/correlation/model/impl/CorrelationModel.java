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

package org.amanzi.awe.correlation.model.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.amanzi.awe.correlation.model.CorrelationNodeTypes;
import org.amanzi.awe.correlation.model.ICorrelationModel;
import org.amanzi.awe.correlation.model.IProxyElement;
import org.amanzi.awe.correlation.nodeproperties.ICorrelationProperties;
import org.amanzi.awe.correlation.service.ICorrelationService;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.impl.dto.DataElement;
import org.amanzi.neo.impl.util.AbstractDataElementIterator;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractNamedModel;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.models.measurement.MeasurementNodeType;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.network.NetworkElementType;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.INetworkNodeProperties;
import org.amanzi.neo.nodeproperties.ITimePeriodNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeNotExistsException;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CorrelationModel extends AbstractNamedModel implements ICorrelationModel {

    protected final class ProxyIterator extends AbstractDataElementIterator<IProxyElement> {

        /**
         * @param nodeIterator
         */
        public ProxyIterator(final Iterator<Node> nodeIterator) {
            super(nodeIterator);
        }

        @Override
        protected IProxyElement createDataElement(final Node node) {
            try {
                Node sector = correlationService.getSectorForProxy(node);
                Iterator<Node> measurements = correlationService.getMeasurementForProxy(node, measurementModel.getName());

                return new ProxyElement(getRootNode(), new DataElement(sector), new DataElementIterator(measurements));
            } catch (ServiceException e) {
                LOGGER.error("can't create proxyElement", e);
                return null;
            }
        }
    }

    private static final Logger LOGGER = Logger.getLogger(CorrelationModel.class);

    private final INetworkNodeProperties networkNodeProperties;

    private final ICorrelationService correlationService;

    private IMeasurementModel measurementModel;

    private INetworkModel networkModel;

    private final Map<IDataElement, IProxyElement> proxiesCache = new HashMap<IDataElement, IProxyElement>();

    private final Map<IProxyElement, Set<IDataElement>> measurementCache = new HashMap<IProxyElement, Set<IDataElement>>();

    private final ICorrelationProperties correlationNodeProperties;

    private String correlatedProperty;

    private String correlationProperties;

    private Long startTime = Long.MAX_VALUE;

    private Long endTime = Long.MIN_VALUE;

    private int proxiesCount;

    private final ITimePeriodNodeProperties timePeriodNodeProperties;

    private Integer totalSectorsCount;

    private Integer correlatedMCount;

    private Integer totalMCount;

    public CorrelationModel(final ICorrelationService correlationService, final INodeService nodeService,
            final IGeneralNodeProperties generalNodeProperties, final INetworkNodeProperties networkNodeProperties,
            final ICorrelationProperties correlationNodeProperties, final ITimePeriodNodeProperties timePeriodNodeProperties) {
        super(nodeService, generalNodeProperties);

        this.networkNodeProperties = networkNodeProperties;
        this.correlationService = correlationService;
        this.correlationNodeProperties = correlationNodeProperties;
        this.timePeriodNodeProperties = timePeriodNodeProperties;
    }

    private void computeCorrelatedM() {
        for (Set<IDataElement> measurements : measurementCache.values()) {
            correlatedMCount += measurements.size();
        }

    }

    @Override
    public void delete() throws ModelException {
        try {
            correlationService.deleteModel(getRootNode());
        } catch (ServiceException e) {
            processException("Can't remove model" + getName(), e);
        } catch (NodeTypeNotExistsException e) {
            processException("Can't remove model" + getName(), e);
        }
    }

    @Override
    public Iterable<IProxyElement> findAllProxies() throws ModelException {
        Iterator<Node> proxies = null;
        try {
            getNodeService().getChildrenChain(getRootNode());
        } catch (ServiceException e) {
            processException("can't get proxies for model " + this, e);
        }
        return new ProxyIterator(proxies).toIterable();
    }

    @Override
    public void finishUp() throws ModelException {

        try {
            totalSectorsCount = networkModel.getPropertyStatistics().getCount(NetworkElementType.SECTOR);
            proxiesCount = proxiesCache.size();
            computeCorrelatedM();
            totalMCount = measurementModel.getPropertyStatistics().getCount(MeasurementNodeType.M);

            getNodeService().updateProperty(getRootNode(), correlationNodeProperties.getProxiesCountNodeProperty(), proxiesCount);
            getNodeService().updateProperty(getRootNode(), timePeriodNodeProperties.getStartDateTimestampProperty(), startTime);
            getNodeService().updateProperty(getRootNode(), timePeriodNodeProperties.getEndDateTimestampProperty(), endTime);
            getNodeService().updateProperty(getRootNode(), correlationNodeProperties.getTotalSectorsCount(), totalSectorsCount);
            getNodeService().updateProperty(getRootNode(), correlationNodeProperties.getCorrelatedMCountNodeProperty(),
                    correlatedMCount);
            getNodeService().updateProperty(getRootNode(), correlationNodeProperties.getTotalMCountNodeProperty(), totalMCount);
            proxiesCache.clear();
            measurementCache.clear();
        } catch (ServiceException e) {
            processException("can't update properties", e);
        }
    }

    @Override
    public Iterable<IDataElement> getAllElementsByType(final INodeType nodeType) throws ModelException {
        return null;
    }

    @Override
    public Integer getCorrelatedMCount() {
        return correlatedMCount;
    }

    @Override
    public String getCorrelatedProperty() {
        return correlatedProperty;
    }

    @Override
    public String getCorrelationProperty() {
        return correlationProperties;
    }

    @Override
    public Long getEndTime() {
        return endTime;
    }

    @Override
    public IMeasurementModel getMeasurementModel() {
        return measurementModel;
    }

    @Override
    protected INodeType getModelType() {
        return CorrelationNodeTypes.CORRELATION_MODEL;
    }

    /**
     * @return Returns the networkNodeProperties.
     */
    public INetworkNodeProperties getNetworkNodeProperties() {
        return networkNodeProperties;
    }

    @Override
    public INetworkModel getNetworModel() {
        return networkModel;
    }

    @Override
    public int getProxiesCount() {
        return proxiesCount;
    }

    @Override
    public IProxyElement getProxy(final IDataElement sector, final IDataElement correlatedElement) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getProxy(" + sector + "," + correlatedElement + ")");
        }
        assert sector != null;
        assert correlatedElement != null;

        IProxyElement proxy = proxiesCache.get(sector);
        Node sectorNode = ((DataElement)sector).getNode();
        Node measurementNode = ((DataElement)correlatedElement).getNode();
        try {
            Node proxyNode = correlationService.findProxy(sectorNode, measurementNode, measurementModel.getName());
            if (proxyNode == null) {
                proxyNode = correlationService.createProxy(getRootNode(), sectorNode, measurementNode, measurementModel.getName());
                Long timestamp = getNodeService().getNodeProperty(measurementNode, timePeriodNodeProperties.getTimestampProperty(),
                        startTime, false);
                updateTimestamp(timestamp);
            }
            if (proxy == null) {
                proxy = new ProxyElement(proxyNode, sector);
                proxiesCache.put(sector, proxy);
            }

            if (measurementCache.get(proxy) == null) {
                measurementCache.put(proxy, new HashSet<IDataElement>());
            }
            measurementCache.get(proxy).add(correlatedElement);
        } catch (ServiceException e) {
            processException("can't create proxy node for sector " + sector + " and measurement " + correlatedElement, e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getProxy(" + sector + "," + correlatedElement + ")"));
        }
        return proxy;
    }

    @Override
    public Long getStartTime() {
        return startTime;
    }

    @Override
    public Integer getTotalMCount() {
        return totalMCount;
    }

    @Override
    public Integer getTotalSectorsCount() {
        return totalSectorsCount;
    }

    @Override
    public void initialize(final Node rootNode) throws ModelException {
        super.initialize(rootNode);
        try {
            this.correlatedProperty = getNodeService().getNodeProperty(rootNode,
                    correlationNodeProperties.getCorrelatedNodeProperty(), null, true);
            this.correlationProperties = getNodeService().getNodeProperty(rootNode,
                    correlationNodeProperties.getCorrelationNodeProperty(), null, true);
            this.startTime = getNodeService().getNodeProperty(rootNode, timePeriodNodeProperties.getStartDateTimestampProperty(),
                    startTime, false);
            this.endTime = getNodeService().getNodeProperty(rootNode, timePeriodNodeProperties.getEndDateTimestampProperty(),
                    endTime, false);
            this.proxiesCount = getNodeService().getNodeProperty(rootNode, correlationNodeProperties.getProxiesCountNodeProperty(),
                    0, false);
            this.totalSectorsCount = getNodeService().getNodeProperty(rootNode, correlationNodeProperties.getTotalSectorsCount(),
                    0, false);
            this.correlatedMCount = getNodeService().getNodeProperty(rootNode,
                    correlationNodeProperties.getCorrelatedMCountNodeProperty(), 0, false);
            this.totalMCount = getNodeService().getNodeProperty(rootNode, correlationNodeProperties.getTotalMCountNodeProperty(),
                    0, false);
        } catch (ServiceException e) {
            processException("can't get property from model" + getName(), e);
        }
    }

    /**
     * @param measurementModel
     */
    public void setCorrelatedModels(final INetworkModel networkModel, final IMeasurementModel measurementModel) {
        this.networkModel = networkModel;
        this.measurementModel = measurementModel;
    }

    private void updateTimestamp(final long timestamp) {
        startTime = Math.min(startTime, timestamp);
        endTime = Math.max(endTime, timestamp);
    }
}
