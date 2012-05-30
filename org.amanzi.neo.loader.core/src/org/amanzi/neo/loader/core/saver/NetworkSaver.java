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

package org.amanzi.neo.loader.core.saver;

import java.util.Map;

import org.amanzi.awe.ui.AweUiPlugin;
import org.amanzi.neo.loader.core.config.NetworkConfiguration;
import org.amanzi.neo.loader.core.parser.MappedData;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.CRS;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.commons.lang.StringUtils;

/**
 * network saver
 * 
 * @author Kondratenko_Vladislav
 */
public class NetworkSaver extends
		AbstractMappedDataSaver<INetworkModel, NetworkConfiguration> {

	// Default network structure
	private final static NetworkElementNodeType[] DEFAULT_NETWORK_STRUCTURE = {
			NetworkElementNodeType.CITY, NetworkElementNodeType.MSC,
			NetworkElementNodeType.BSC, NetworkElementNodeType.SITE,
			NetworkElementNodeType.SECTOR };

	private static final String SITE_NAME_FROM_SECTOR_NAME = "SITE_SECTOR_NAME";

	private NetworkElementNodeType startNetworkElement;

	private NetworkElementNodeType allElementsFor;

	private String hint = StringUtils.EMPTY;

	/**
	 * create saver instance
	 */
	public NetworkSaver() {
		super();
	}

	/**
	 * Constructor for tests
	 * 
	 * @param model
	 * @param config
	 */
	NetworkSaver(INetworkModel model, NetworkConfiguration config) {
		commitTx();
		if (model != null) {
			setMainModel(model);
			addModel(model);
		}
	}

	@Override
	protected boolean isRenderable() {
		return true;
	}

	@Override
	protected String getDatasetType() {
		return DatasetTypes.NETWORK.getId();
	}

	private boolean shouldStart(NetworkElementNodeType currentElement) {
		if (StringUtils.isEmpty(getStartElement())) {
			return true;
		}

		if (startNetworkElement == null) {
			startNetworkElement = NetworkElementNodeType
					.valueOf(getStartElement());
		}

		return startNetworkElement == currentElement;
	}

	private boolean shouldAddAllElements(NetworkElementNodeType currentElement) {
		if (StringUtils.isEmpty(getAllElementsFor())) {
			return false;
		}

		if (allElementsFor == null) {
			allElementsFor = NetworkElementNodeType
					.valueOf(getAllElementsFor());
		}

		return currentElement == allElementsFor;
	}

	@Override
	public void saveElement(MappedData dataElement) throws AWEException {
		IDataElement parent = null;
		IDataElement element = null;

		boolean shouldStart = false;
		boolean firstTime = true;
		boolean siteNameFromSectorName = AweUiPlugin.getDefault()
				.getPreferenceStore().getBoolean(SITE_NAME_FROM_SECTOR_NAME);

		for (NetworkElementNodeType type : DEFAULT_NETWORK_STRUCTURE) {
			if (!shouldStart) {
				shouldStart = shouldStart(type);
			}

			if (!shouldStart) {
				continue;
			}

			Map<String, Object> values = getDataElementProperties(
					getMainModel(), type.getId(), dataElement,
					shouldAddAllElements(type), true);

			// trick for SITE name - it can be computed from Sector name
			if (type == NetworkElementNodeType.SITE) {
				if (siteNameFromSectorName) {
					Map<String, Object> sectorProperties = getDataElementProperties(
							getMainModel(),
							NetworkElementNodeType.SECTOR.getId(), dataElement,
							shouldAddAllElements(type), false);

					String sectorName = (String) sectorProperties
							.get(AbstractService.NAME);
					String siteName = sectorName.substring(0,
							sectorName.length() - 1);
					values.put(AbstractService.NAME, siteName);

				}

				if (firstTime) {
					Double lat = (Double) values.get(DriveModel.LATITUDE);
					Double lon = (Double) values.get(DriveModel.LONGITUDE);

					if (lat != null && lon != null) {
						getMainModel().updateCRS(
								CRS.fromLocation(lat, lon, hint).getEpsg());
						firstTime = false;
					}
				}
			}

			if (!values.isEmpty()) {
				values.put(AbstractService.TYPE, type.getId());

				try {
					element = getMainModel().findElement(values);

					if (element == null) {
						element = getMainModel().createElement(parent, values);
					} else {
						IDataElement oldParent = getMainModel()
								.getParentElement(element);
						if (parent != null && !oldParent.equals(parent)) {
							INodeType oldType = NodeTypeManager
									.getType(oldParent);
							INodeType newType = NodeTypeManager.getType(parent);

							int oldIndex = getMainModel().getNetworkStructure()
									.indexOf(oldType);
							int newIndex = getMainModel().getNetworkStructure()
									.indexOf(newType);

							if (newIndex < oldIndex) {
								getMainModel().replaceRelationship(parent,
										element);
							}
						}

						getMainModel()
								.completeProperties(element, values, true);
					}
					parent = element;
				} catch (IllegalArgumentException e) {
					continue;
				}
			}
		}

		commitTx();
	}

	@Override
	protected String getSubType() {
		return null;
	}

	@Override
	protected INetworkModel createMainModel(NetworkConfiguration configuration)
			throws AWEException {
		return getActiveProject().getNetwork(configuration.getDatasetName());
	}

	@Override
	public void init(NetworkConfiguration configuration) throws AWEException {
		super.init(configuration);

		hint = configuration.getFile().getName();
	}

}
