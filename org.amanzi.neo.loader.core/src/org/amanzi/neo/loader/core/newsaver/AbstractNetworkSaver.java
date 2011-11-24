package org.amanzi.neo.loader.core.newsaver;

import java.util.Map;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.neo4j.graphdb.GraphDatabaseService;

public abstract class AbstractNetworkSaver extends
		AbstractCSVSaver<INetworkModel> {

	public AbstractNetworkSaver(GraphDatabaseService service) {
		super(service);
	}

	public AbstractNetworkSaver() {
		super();
	}

	@Override
	protected void initializeNecessaryModels() throws AWEException {
		parametrizedModel = getActiveProject().getNetwork(
				configuration.getDatasetNames().get(
						ConfigurationDataImpl.NETWORK_PROPERTY_NAME));

	}

	@Override
	protected Map<String, String[]> initializeSynonyms() {
		return preferenceManager.getSynonyms(DatasetTypes.NETWORK);
	}

	@Override
	protected void commonLinePreparationActions(CSVContainer dataElement)
			throws Exception {

	}
}
