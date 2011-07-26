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

package org.amanzi.awe.neighbours.gpeh;

import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Pattern;

import org.amanzi.awe.gpeh.GpehReportUtil;
import org.amanzi.awe.statistics.CallTimePeriods;
import org.amanzi.neo.services.ui.NeoUtils;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * <p>
 * Handler for pilot polutions reports
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class PilotPolutionsHandler extends IntraModelHandler {

	/**
	 * Instantiates a new pilot polutions handler.
	 * 
	 * @param period
	 *            the period
	 * @param service
	 *            the service
	 */
	public PilotPolutionsHandler(CallTimePeriods period,
			GraphDatabaseService service) {
		super(period, service);
	}

	@Override
	public boolean setData(CellNodeInfo bestCell, InterfCellInfo interfCell) {
		// Pattern pat=Pattern.compile("^(\\D)(\\d)$");
		Pattern pat = Pattern.compile("^(intraMr)(\\d+)$");
		Set<Long> timestamps = defineTimestamps(interfCell.getCellSectorInfo(),
				pat, 2);
		if (timestamps.isEmpty()) {
			return false;
		}
		data = new LinkedList<Object>();
		data.add(NeoUtils.getGpehCellName(bestCell.getCellSector(), service));
		data.add((String) bestCell.getCellSector().getProperty(
				GpehReportUtil.PRIMARY_SCR_CODE, ""));
		data.add(NeoUtils.getGpehCellName(interfCell.getCellSector(), service));
		data.add((String) interfCell.getPsc());
		data.add(interfCell.isDifinedNeighbour(bestCell.getCellSector(),
				service));
		// Distance
		data.add(interfCell.getDistance());
		// Tier Distance
		String value = String.valueOf("N/A");
		data.add(value);
		int[] values = new int[5];
		computeArrayValue(values, interfCell.getCellSectorInfo(),
				paramPositionList, timestamps);
		int numMr = computeValue(bestCell.getCellSectorInfo(),
				NUM_INTRA_MES_BEST_CELL, timestamps);
		int pol;
		if (numMr != 0) {
			pol = 100 * (values[2] + values[3] + values[4]) / numMr;
		} else {
			pol = 0;
		}
		// PP_Impact
		data.add(pol);
		// # of MR for best cell
		data.add(numMr);
		// # of MR for Interfering cell
		data.add(computeValue(interfCell.getCellSectorInfo(),
				NUM_INTER_MES_BEST_CELL, timestamps));
		for (int i = 2; i < 5; i++) {
			data.add(values[i]);
		}

		return true;
	}
}
