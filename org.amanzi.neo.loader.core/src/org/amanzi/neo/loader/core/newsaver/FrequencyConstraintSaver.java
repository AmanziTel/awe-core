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

package org.amanzi.neo.loader.core.newsaver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;
import org.neo4j.graphdb.GraphDatabaseService;

//TODO: LN: comments
/**
 * saver for frequency constraint data
 * 
 * @author Vladislav_Kondratenko
 */
public class FrequencyConstraintSaver extends AbstractN2NSaver {
	/*
	 * FREQUENCY constraints
	 */
	private static final String FR_TRX_ID = "trx_id";
	private static final String FR_CH_TYPE = "channel type";
	private static final String FR_FREQUENCY = "frequency";
	private static final String FR_PENALTY = "penalty";
	private static final String FR_SCALLING_FACTOR = "scalling_factor";
	private static final String SECTOR = "sector";

	/*
	 * collections of elements properties
	 */
	private Map<String, Object> SECTOR_MAP = new HashMap<String, Object>();
	private Map<String, Object> TRX_MAP = new HashMap<String, Object>();
	private Map<String, Object> RELATIONS_PROPERTIES = new HashMap<String, Object>();

	protected FrequencyConstraintSaver(INodeToNodeRelationsModel model,
			INetworkModel networkModel, ConfigurationDataImpl data,
			GraphDatabaseService service) {
		super(model, networkModel, data, service);
	}

	/**
	 * create class instance
	 */
	public FrequencyConstraintSaver() {
		super();
	}

	@Override
	protected INodeToNodeRelationsModel getNode2NodeModel(String name)
			throws AWEException {
		return parametrizedModel.getNodeToNodeModel(
				N2NRelTypes.FREQUENCY_SPECTRUM, name,
				NetworkElementNodeType.SECTOR);
	}

	@Override
	protected Map<String, String[]> initializeSynonyms() {
		return preferenceManager.getFrequencySynonyms();

	}

	@Override
	protected void saveLine(List<String> row) throws AWEException {
		if (!isCorrect(SECTOR, row)) {
			LOGGER.error("Sector name not found on line: " + lineCounter);
			return;
		}
		clearTemporalyDataMaps();
		collectSectorMap(row);
		if (SECTOR_MAP.get(NewAbstractService.NAME) == null) {
			LOGGER.error("Incorrect sector name on line: " + lineCounter);
			return;
		}
		if (!isCorrect(FR_TRX_ID, row)) {
			LOGGER.error("TRX id  not found on line: " + lineCounter);
			return;
		}

		collectTrxMap(row);
		String trxId = TRX_MAP.get(FR_TRX_ID).toString();
		// TODO: LN: see findElementByPropertyValue
		IDataElement findedSector = parametrizedModel.findElement(SECTOR_MAP);
		if (findedSector == null) {
			LOGGER.error("sector " + SECTOR_MAP + " not found");
		}

		// link trx elements and frequency spectrum element
		List<IDataElement> listTRX = getRequiredTrxs(trxId, findedSector);
		if (listTRX.size() == 0) {
			LOGGER.info("There are no trx for sector " + SECTOR_MAP);
			return;
		}
		for (IDataElement trx : listTRX) {
			IDataElement frNode = n2nModel
					.getFrequencyElement((Integer) TRX_MAP.get(FR_FREQUENCY));
			collectRelationsProperties(row);
			n2nModel.linkNode(trx, frNode, RELATIONS_PROPERTIES);
		}
	}

	/**
	 * collect sector element properties
	 * 
	 * @param row
	 */
	private void collectSectorMap(List<String> row) {
		SECTOR_MAP.put(NewAbstractService.NAME,
				getSynonymValueWithAutoparse(SECTOR, row).toString());
		SECTOR_MAP.put(NewAbstractService.TYPE,
				NetworkElementNodeType.SECTOR.getId());
	}

	/**
	 * collect required sectors trx
	 * 
	 * @param trxId
	 * @param findedSector
	 * @return
	 */
	// TODO: LN: why not use Integer as trxId and null as '*'?
	// in this case you don't need to use toString() all the time
	private List<IDataElement> getRequiredTrxs(String trxId,
			IDataElement findedSector) {
		Iterable<IDataElement> listTRX = parametrizedModel
				.getChildren(findedSector);
		List<IDataElement> requiredTrx = new LinkedList<IDataElement>();
		for (IDataElement trx : listTRX) {
			if (trxId.equals("*")
					|| trxId.equals(trx.get(FR_TRX_ID).toString())) {
				requiredTrx.add(trx);
			}
		}
		return requiredTrx;
	}

	/**
     *
     */
	private void clearTemporalyDataMaps() {
		SECTOR_MAP.clear();
		TRX_MAP.clear();
		RELATIONS_PROPERTIES.clear();
	}

	/**
	 * collect properties for trx element
	 * 
	 * @param row
	 */
	private void collectTrxMap(List<String> row) {
		TRX_MAP.put(FR_FREQUENCY,
				getSynonymValueWithAutoparse(FR_FREQUENCY, row));
		TRX_MAP.put(FR_TRX_ID, getSynonymValueWithAutoparse(FR_TRX_ID, row));
	}

	/**
	 * collect relationsProperties
	 */
	private void collectRelationsProperties(List<String> row) {
		RELATIONS_PROPERTIES.put(FR_CH_TYPE,
				getSynonymValueWithAutoparse(FR_CH_TYPE, row));
		RELATIONS_PROPERTIES.put(FR_PENALTY,
				getSynonymValueWithAutoparse(FR_PENALTY, row));
		RELATIONS_PROPERTIES.put(FR_SCALLING_FACTOR,
				getSynonymValueWithAutoparse(FR_SCALLING_FACTOR, row));
	}

	@Override
	protected String getSourceElementName() {
		return null;
	}

	@Override
	protected String getNeighborElementName() {
		return null;
	}

}
