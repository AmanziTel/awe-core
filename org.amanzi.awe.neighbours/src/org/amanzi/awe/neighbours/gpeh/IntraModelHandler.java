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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.awe.gpeh.GpehReportUtil;
import org.amanzi.awe.statistics.CallTimePeriods;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.ui.NeoUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Hondler for Intra ICDM reports
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class IntraModelHandler extends RrcModelHandler {
	protected List<Object> data;
	protected final CallTimePeriods period;
	protected final GraphDatabaseService service;

	/**
	 * Instantiates a new intra model handler.
	 * 
	 * @param period
	 *            the period
	 * @param service
	 *            the service
	 */
	public IntraModelHandler(CallTimePeriods period,
			GraphDatabaseService service) {
		super.setLists();
		this.period = period;
		this.service = service;

	}

	@Override
	public boolean haveData() {
		return data != null;
	}

	@Override
	public List<Object> formLine() {
		return data;
	}

	@Override
	public void clearData() {
		data = null;
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
		// # of MR for best cell

		data.add(computeValue(bestCell.getCellSectorInfo(),
				NUM_INTRA_MES_BEST_CELL, timestamps));
		// # of MR for Interfering cell
		data.add(computeValue(interfCell.getCellSectorInfo(), NUM_INTRA_MES,
				timestamps));
		int[] values = new int[5];
		computeArrayValue(values, interfCell.getCellSectorInfo(),
				paramIntraEcnoListDelta, timestamps);
		// Delta EcNo 1-5
		for (int i = 0; i < 5; i++) {
			data.add(values[i]);
		}
		values = new int[5];
		computeArrayValue(values, interfCell.getCellSectorInfo(),
				paramIntraRSCPListDelta, timestamps);
		for (int i = 0; i < 5; i++) {
			data.add(values[i]);
		}
		values = new int[5];
		computeArrayValue(values, interfCell.getCellSectorInfo(),
				paramPositionList, timestamps);
		for (int i = 0; i < 5; i++) {
			data.add(values[i]);
		}

		return true;
	}

	/**
	 * Compute array value.
	 * 
	 * @param result
	 *            the result array
	 * @param cellSectorInfo
	 *            the cell sector info
	 * @param string
	 *            the formatted property name
	 * @param timestamps
	 *            the timestamps
	 */
	protected void computeArrayValue(int result[], Node cellSectorInfo,
			String string, Set<Long> timestamps) {
		int[] others = new int[result.length];
		for (Long time : timestamps) {
			for (int i = 0; i < result.length; i++) {
				if (cellSectorInfo.hasProperty(String.format(string, (i * 3)))
						&& cellSectorInfo.getProperty(
								INeoConstants.PROPERTY_TIMESTAMP_NAME).equals(
								time)) {
					others[i] = (Integer) cellSectorInfo.getProperty(String
							.format(string, (i * 3)));
				}
			}

			if (others != null) {
				for (int i = 0; i < result.length; i++) {
					result[i] += others[i];
				}
			}
		}
		return;
	}

	protected void computeArrayValue(int result[], Node cellSectorInfo,
			List<String> paramList, Set<Long> timestamps) {
		int[] others = new int[result.length];
		for (Long time : timestamps) {
			for (int i = 0; i < result.length; i++) {
				if (cellSectorInfo.hasProperty(paramList.get(i))
						&& cellSectorInfo.getProperty(
								INeoConstants.PROPERTY_TIMESTAMP_NAME).equals(
								time)) {
					others[i] = (Integer) cellSectorInfo.getProperty(paramList
							.get(i));
				}
			}

			if (others != null) {
				for (int i = 0; i < result.length; i++) {
					result[i] += others[i];
				}
			}
		}
		return;
	}

	/**
	 * Compute value.
	 * 
	 * @param cellSectorInfo
	 *            the cell sector info
	 * @param string
	 *            the formatted property name
	 * @param timestamps
	 *            the timestamps
	 * @return the int
	 */
	protected int computeValue(Node cellSectorInfo, String string,
			Set<Long> timestamps) {
		int result = 0;
		for (Long time : timestamps) {
			if (cellSectorInfo.hasProperty(string)
					&& cellSectorInfo
							.hasProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME)) {
				if (cellSectorInfo.getProperty(
						INeoConstants.PROPERTY_TIMESTAMP_NAME).equals(time)) {
					result += (Integer) cellSectorInfo.getProperty(string, 0);
				}
			}
		}
		return result;
	}

	/**
	 * Define timestamps.
	 * 
	 * @param node
	 *            the node
	 * @param pat
	 *            the pat
	 * @param groupNum
	 *            the group num
	 * @return the set
	 */
	protected Set<Long> defineTimestamps(Node node, Pattern pat, int groupNum) {
		Set<Long> result = new HashSet<Long>();
		if(node.hasProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME)){
			Long time=(Long)node.getProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME);
			if (period.compareByPeriods(time, computeTime) == 0) {
				result.add(time);
			}
		}
		
//		for (String propertyName : node.getPropertyKeys()) {
//			Matcher mat = pat.matcher(propertyName);
//			if (mat.matches()) {
//				Long time = Long.valueOf(mat.group(groupNum));
//				if (period.compareByPeriods(time, computeTime) == 0) {
//					result.add(time);
//				}
//			}
//		}
		return result;
	}

	/**
	 * Gets the ecno rscp array.
	 * 
	 * @param node
	 *            the node
	 * @param timestamps
	 *            the timestamps
	 * @return the ecno rscp array
	 */
	protected int[][] getEcnoRscpArray(Node node, Set<Long> timestamps) {
		int[][] result = new int[92][50];
		int[] rspArr;
	     for (int rscp = 0; rscp <= 91; rscp++) {
	    	 rspArr = new int[50];
             for (int ecno = 0; ecno <= 50; ecno++) {
                 if (node.hasProperty(String.format("RSCP %d ECNO %d", rscp, ecno).toString())) {
                     rspArr[ecno] = Integer.parseInt(node.getProperty(String.format("RSCP %s ECNO %d", rscp, ecno))
                             .toString());
                 }

             }
             result[rscp] = rspArr;
         }
		return result;
	}
}
