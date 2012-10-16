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

package org.amanzi.neo.providers.internal;

import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodeproperties.IMeasurementNodeProperties;
import org.amanzi.neo.nodeproperties.INetworkNodeProperties;
import org.amanzi.neo.nodeproperties.INodeProperties;
import org.amanzi.neo.nodeproperties.ITimePeriodNodeProperties;
import org.amanzi.neo.providers.ContextException;
import org.amanzi.neo.providers.IDriveModelProvider;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.amanzi.neo.providers.IProviderContext;
import org.amanzi.neo.providers.context.ProviderContextImpl;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractProviderPlugin extends AbstractUIPlugin {

    private static final Logger LOGGER = Logger.getLogger(AbstractProviderPlugin.class);

    private static final String PROJECT_MODEL_PROVIDER_ID = "org.amanzi.providers.ProjectModelProvider";

    private static final String PROPERTY_STATISTICS_MODEL_PROVIDER_ID = "org.amanzi.providers.PropertyStatisticsModelProvider";

    private static final String NETWORK_MODEL_PROVIDER_ID = "org.amanzi.provider.NetworkModelProvider";

    private static final String GENERAL_NODE_PROPERTIES_ID = "org.amanzi.nodeproperties.generalnodeproperties";

    private static final String NETWORK_NODE_PROPERTIES_ID = "org.amanzi.nodeproperties.NetworkNodeProperties";

    private static final String GEO_NODE_PROPERTIES_ID = "org.amanzi.nodeproperties.GeoNodeProperties";

    private static final String TIME_PERIOD_NODE_PROPERTIES_ID = "org.amanzi.nodeproperties.TimePeriodNodeProperties";

    private static final String MEASUREMENT_NODE_PROPERTIES_ID = "org.amanzi.nodeproperties.MeasurementNodeProperties";

    private static final String DRIVE_MODEL_PROVIDER_ID = "org.amanzi.provider.DriveModelProvider";

    private static class ProviderContextHolder {
        private static volatile IProviderContext context = new ProviderContextImpl();
    }

    protected IProviderContext getContext() {
        return ProviderContextHolder.context;
    }

    protected <T extends IModelProvider< ? >> T getModelProvider(final String id) {
        try {
            return getContext().get(id);
        } catch (final ContextException e) {
            logError(e);
        }

        return null;
    }

    private <T extends INodeProperties> T getNodeProperties(final String id) {
        try {
            return getContext().getProperties(id);
        } catch (final ContextException e) {
            logError(e);

            PlatformUI.getWorkbench().close();
        }

        return null;
    }

    private void logError(final ContextException e) {
        final String message = "An error occured on initialization Provider context";

        LOGGER.fatal(message, e);

        getLog().log(new Status(IStatus.ERROR, getPluginId(), message, e));
    }

    public IProjectModelProvider getProjectModelProvider() {
        return getModelProvider(PROJECT_MODEL_PROVIDER_ID);
    }

    public IPropertyStatisticsModel getPropertyStatisticsModelProvider() {
        return getModelProvider(PROPERTY_STATISTICS_MODEL_PROVIDER_ID);
    }

    public INetworkModelProvider getNetworkModelProvider() {
        return getModelProvider(NETWORK_MODEL_PROVIDER_ID);
    }

    public IGeneralNodeProperties getGeneralNodeProperties() {
        return getNodeProperties(GENERAL_NODE_PROPERTIES_ID);
    }

    public INetworkNodeProperties getNetworkNodeProperties() {
        return getNodeProperties(NETWORK_NODE_PROPERTIES_ID);
    }

    public IDriveModelProvider getDriveModelProvider() {
        return getModelProvider(DRIVE_MODEL_PROVIDER_ID);
    }

    public IGeoNodeProperties getGeoNodeProperties() {
        return getNodeProperties(GEO_NODE_PROPERTIES_ID);
    }

    public ITimePeriodNodeProperties getTimePeriodNodeProperties() {
        return getNodeProperties(TIME_PERIOD_NODE_PROPERTIES_ID);
    }

    public IMeasurementNodeProperties getMeasurementNodeProperties() {
        return getNodeProperties(MEASUREMENT_NODE_PROPERTIES_ID);
    }

    public abstract String getPluginId();
}
