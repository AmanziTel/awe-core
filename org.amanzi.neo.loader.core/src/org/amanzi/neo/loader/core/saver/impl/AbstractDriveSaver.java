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

package org.amanzi.neo.loader.core.saver.impl;

import java.io.File;
import java.util.Map;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.loader.core.IMappedStringData;
import org.amanzi.neo.loader.core.exception.impl.UnderlyingModelException;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.internal.LoaderCorePlugin;
import org.amanzi.neo.loader.core.saver.impl.internal.AbstractSynonymsSaver;
import org.amanzi.neo.loader.core.synonyms.SynonymsManager;
import org.amanzi.neo.models.drive.IDriveModel;
import org.amanzi.neo.models.drive.IDriveModel.IDriveType;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.render.IGISModel.ILocationElement;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodeproperties.ITimePeriodNodeProperties;
import org.amanzi.neo.providers.IDriveModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.commons.math3.util.Precision;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractDriveSaver extends AbstractSynonymsSaver<IConfiguration> {

	public static final String DRIVE_SYNONYMS = "drive";

	private static final double COORDINATE_VALUE_PRECISION = 0.0001;

	private final IDriveModelProvider driveModelProvider;

	private IDriveModel driveModel;

	private IDataElement rootElement;

	private IDataElement modelElement;

	private final ITimePeriodNodeProperties timePeriodNodeProperties;

	private final IGeoNodeProperties geoNodeProperties;

	private ILocationElement location;

	private double previousLat;

	private double previousLon;

	public AbstractDriveSaver() {
		this(LoaderCorePlugin.getInstance().getTimePeriodNodeProperties(), LoaderCorePlugin.getInstance().getGeoNodeProperties(),
				LoaderCorePlugin.getInstance().getDriveModelProvider(), LoaderCorePlugin.getInstance().getProjectModelProvider(),
				SynonymsManager.getInstance());
	}

	/**
	 * @param projectModelProvider
	 * @param synonymsManager
	 */
	protected AbstractDriveSaver(final ITimePeriodNodeProperties timePeriodNodeProperties,
			final IGeoNodeProperties geoNodeProperties, final IDriveModelProvider driveModelProvider,
			final IProjectModelProvider projectModelProvider, final SynonymsManager synonymsManager) {
		super(projectModelProvider, synonymsManager);
		this.driveModelProvider = driveModelProvider;
		this.geoNodeProperties = geoNodeProperties;
		this.timePeriodNodeProperties = timePeriodNodeProperties;
	}

	@Override
	public void init(final IConfiguration configuration) throws ModelException {
		super.init(configuration);

		driveModel = createDriveModel(configuration.getDatasetName());
		modelElement = driveModel.asDataElement();
		rootElement = driveModel.asDataElement();
	}

	@Override
	protected String getSynonymsType() {
		return DRIVE_SYNONYMS;
	}

	@Override
	protected void saveInModel(final IMappedStringData data) throws ModelException {
		Map<String, Object> properties = getElementProperties(driveModel.getMainMeasurementNodeType(), data, true);

		if (!properties.isEmpty()) {
			double lat = (Double)properties.remove(geoNodeProperties.getLatitideProperty());
			double lon = (Double)properties.remove(geoNodeProperties.getLongitudeProperty());
			long timestamp = (Long)properties.get(timePeriodNodeProperties.getTimestampProperty());

			IDataElement measurement = driveModel.addMeasurement(rootElement, properties);

			if ((location == null) || isCoordinatesChanged(lat, lon)) {
				location = driveModel.createLocation(measurement, lat, lon, timestamp);
				previousLat = lat;
				previousLon = lon;
			} else {
				driveModel.addToLocation(measurement, location);
			}
		}
	}

	private boolean isCoordinatesChanged(final double lat, final double lon) {
		return !Precision.equals(lat, previousLat, COORDINATE_VALUE_PRECISION)
				|| !Precision.equals(lon, previousLon, COORDINATE_VALUE_PRECISION);
	}

	protected IDriveModel createDriveModel(final String driveName) throws ModelException {
		IDriveModel model = driveModelProvider.create(getCurrentProject(), driveName, getDriveType());
		addProcessedModel(model);

		return model;
	}

	@Override
	public void onFileParsingStarted(final File file) {
		try {
			rootElement = driveModel.getFile(modelElement, file.getName(), file.getAbsolutePath());
		} catch (ModelException e) {
			throw new UnderlyingModelException(e);
		}
	}

	protected abstract IDriveType getDriveType();

}
