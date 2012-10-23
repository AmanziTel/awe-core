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

import org.amanzi.awe.correlation.model.ICorrelationElement;
import org.amanzi.awe.correlation.model.ICorrelationModel;
import org.amanzi.awe.correlation.service.ICorrelationService;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IMeasurementNodeProperties;
import org.amanzi.neo.nodeproperties.INetworkNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CorrelationModel extends AbstractModel implements ICorrelationModel {

    private final IMeasurementNodeProperties measurementNodeProperties;

    private final INetworkNodeProperties networkNodeProperties;

    private final ICorrelationService correlationService;

    private IMeasurementModel measurementModel;

    private INetworkModel networkModel;

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public CorrelationModel(final ICorrelationService correlationService, final INodeService nodeService,
            final IGeneralNodeProperties generalNodeProperties, final INetworkNodeProperties networkNodeProperties,
            final IMeasurementNodeProperties measurementNodeProperties) {
        super(nodeService, generalNodeProperties);

        this.networkNodeProperties = networkNodeProperties;
        this.measurementNodeProperties = measurementNodeProperties;
        this.correlationService = correlationService;
    }

    @Override
    public Iterable<ICorrelationElement> findAllCorrelations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void finishUp() throws ModelException {
        // TODO Auto-generated method stub

    }

    /**
     * @return Returns the correlationService.
     */
    public ICorrelationService getCorrelationService() {
        return correlationService;
    }

    /**
     * @return Returns the measurementModel.
     */
    public IMeasurementModel getMeasurementModel() {
        return measurementModel;
    }

    /**
     * @return Returns the measurementNodeProperties.
     */
    public IMeasurementNodeProperties getMeasurementNodeProperties() {
        return measurementNodeProperties;
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
    public void initialize(final Node rootNode) throws ModelException {
        // TODO Auto-generated method stub
        super.initialize(rootNode);
    }

    /**
     * @param measurementModel
     */
    public void setMeasurementModel(final INetworkModel networkModel, final IMeasurementModel measurementModel) {
        this.networkModel = networkModel;
        this.measurementModel = measurementModel;
    }

}
