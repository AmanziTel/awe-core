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
import java.util.Iterator;
import java.util.Map;

import org.amanzi.awe.correlation.model.CorrelationTypes;
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
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.INetworkNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
                Node measurement = correlationService.getMeasurementForProxy(node);

                return new ProxyElement(getRootNode(), new DataElement(sector), new DataElement(measurement));
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

    private final Map<Pair<IDataElement, IDataElement>, IProxyElement> proxiesCache = new HashMap<Pair<IDataElement, IDataElement>, IProxyElement>();

    private final ICorrelationProperties correlationNodeProperties;

    private String correlatedProperty;

    private String correlationProperty;

    public CorrelationModel(final ICorrelationService correlationService, final INodeService nodeService,
            final IGeneralNodeProperties generalNodeProperties, final INetworkNodeProperties networkNodeProperties,
            final ICorrelationProperties correlationNodeProperties) {
        super(nodeService, generalNodeProperties);

        this.networkNodeProperties = networkNodeProperties;
        this.correlationService = correlationService;
        this.correlationNodeProperties = correlationNodeProperties;
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
        proxiesCache.clear();
    }

    @Override
    public Iterable<IDataElement> getAllElementsByType(final INodeType nodeType) throws ModelException {
        return null;
    }

    @Override
    public String getCorrelatedProperty() {
        return correlatedProperty;
    }

    @Override
    public String getCorrelationProperty() {
        return correlationProperty;
    }

    /**
     * @return Returns the measurementModel.
     */
    public IMeasurementModel getMeasurementModel() {
        return measurementModel;
    }

    @Override
    protected INodeType getModelType() {
        return CorrelationTypes.CORRELATION_MODEL;
    }

    /**
     * @return Returns the networkModel.
     */
    public INetworkModel getNetworkModel() {
        return networkModel;
    }

    /**
     * @return Returns the networkNodeProperties.
     */
    public INetworkNodeProperties getNetworkNodeProperties() {
        return networkNodeProperties;
    }

    @Override
    public IProxyElement getProxy(final IDataElement sector, final IDataElement correlatedElement) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getProxy(" + sector + "," + correlatedElement + ")");
        }
        assert sector != null;
        assert correlatedElement != null;

        Pair<IDataElement, IDataElement> cacheKey = new ImmutablePair<IDataElement, IDataElement>(sector, correlatedElement);
        IProxyElement element = proxiesCache.get(cacheKey);
        Node sectorNode = ((DataElement)sector).getNode();
        Node measurementNode = ((DataElement)correlatedElement).getNode();

        try {
            if (element == null) {
                Node proxyNode = correlationService.createProxy(getRootNode(), sectorNode, measurementNode,
                        measurementModel.getName());
                element = new ProxyElement(proxyNode, sector, correlatedElement);
                proxiesCache.put(cacheKey, element);
            }
        } catch (ServiceException e) {
            processException("can't create proxy node for sector " + sector + " and measurement " + correlatedElement, e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getProxy(" + sector + "," + correlatedElement + ")"));
        }
        return element;
    }

    @Override
    public void initialize(final Node rootNode) throws ModelException {
        super.initialize(rootNode);
        try {
            this.correlatedProperty = getNodeService().getNodeProperty(rootNode,
                    correlationNodeProperties.getCorrelatedNodeProperty(), null, true);
            this.correlationProperty = getNodeService().getNodeProperty(rootNode,
                    correlationNodeProperties.getCorrelationNodeProperty(), null, true);
        } catch (ServiceException e) {
            processException("can't get property from model", e);
        }
    }

    /**
     * @param measurementModel
     */
    public void setCorrelatedModels(final INetworkModel networkModel, final IMeasurementModel measurementModel) {
        this.networkModel = networkModel;
        this.measurementModel = measurementModel;
    }

}
