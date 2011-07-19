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
import java.util.List;

/**
 * <p>
 * Abstract class for Rrc Model handler
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public abstract class RrcModelHandler {

	protected long computeTime;
	protected static final String COUNT_INTRA_ECNO_DELTA_3 = "count of intra EC_NO delta  <=3 Dbm";
	protected static final String COUNT_INTRA_ECNO_DELTA_6 = "count of intra EC_NO delta  <=6 Dbm";
	protected static final String COUNT_INTRA_ECNO_DELTA_9 = "count of intra EC_NO delta  <=9 Dbm";
	protected static final String COUNT_INTRA_ECNO_DELTA_12 = "count of intra EC_NO delta  <=12 Dbm";
	protected static final String COUNT_INTRA_ECNO_DELTA_15 = "count of intra EC_NO delta  <=15 Dbm";
	protected static final String STATISTIC_PROPERTY_TYPE = "statistic property type";

	protected static final String COUNT_INTRA_RSCP_DELTA_3 = "count of intra RSCP delta  <=3 Dbm";
	protected static final String COUNT_INTRA_RSCP_DELTA_6 = "count of intra RSCP delta  <=6 Dbm";
	protected static final String COUNT_INTRA_RSCP_DELTA_9 = "count of intra RSCP delta  <=9 Dbm";
	protected static final String COUNT_INTRA_RSCP_DELTA_12 = "count of intra RSCP delta  <=12 Dbm";
	protected static final String COUNT_INTRA_RSCP_DELTA_15 = "count of intra RSCP delta  <=15 Dbm";

	protected static final String NUM_INTRA_MES = "number of intra Measurements ";
	protected static final String NUM_INTER_MES = "number of inter Measurements ";
	protected static final String NUM_INTER_MES_BEST_CELL = "number of mesurement for  inter best cell";
	protected static final String NUM_INTRA_MES_BEST_CELL = "number of mesurement for  intra best cell";
	protected static final String NUM_IRAT_MES_BEST_CELL = "number of mesurement for  irat best cell";

	protected static final String COUNT_INTER_ECNO_6DB = "count inter EC_NO >=-6dB";
	protected static final String COUNT_INTER_ECNO_9DB = "count inter EC_NO >=-9dB";
	protected static final String COUNT_INTER_ECNO_12DB = "count inter EC_NO >=-12dB";
	protected static final String COUNT_INTER_ECNO_15DB = "count inter EC_NO >=-15dB";
	protected static final String COUNT_INTER_ECNO_18DB = "count inter EC_NO >=-18dB";

	protected static final String COUNT_INTER_RSCP_105DB_ECNO_14DB = "count inter RSCP <-105dB EC_NO>-14dB";
	protected static final String COUNT_INTER_RSCP_95DB_ECNO_14DB = "count inter RSCP <-95dB EC_NO>-14dB";
	protected static final String COUNT_INTER_RSCP_85DB_ECNO_14DB = "count inter RSCP <-85dB EC_NO>-14dB";
	protected static final String COUNT_INTER_RSCP_L75DB_ECNO_14DB = "count inter RSCP <-75dB EC_NO>-14dB";
	protected static final String COUNT_INTER_RSCP_M75DB_ECNO_14DB = "count inter RSCP >=-75dB EC_NO>-14dB";

	protected static final String COUNT_INTER_RSCP_105DB_ECNO_10DB = "count inter RSCP <-105dB EC_NO>-10dB";
	protected static final String COUNT_INTER_RSCP_95DB_ECNO_10DB = "count inter RSCP <-95dB EC_NO>-10dB";
	protected static final String COUNT_INTER_RSCP_85DB_ECNO_10DB = "count inter RSCP <-85dB EC_NO>-10dB";
	protected static final String COUNT_INTER_RSCP_L75DB_ECNO_10DB = "count inter RSCP <-75dB EC_NO>-10dB";
	protected static final String COUNT_INTER_RSCP_M75DB_ECNO_10DB = "count inter RSCP >=-75dB EC_NO>-10dB";

	protected static final String COUNT_POSITION_1 = "Position 1";
	protected static final String COUNT_POSITION_2 = "Position 2";
	protected static final String COUNT_POSITION_3 = "Position 3";
	protected static final String COUNT_POSITION_4 = "Position 4";
	protected static final String COUNT_POSITION_5 = "Position 5";
	protected static List<String> paramPositionList;
	protected static List<String> paramIntraEcnoListDelta;
	protected static List<String> paramIntraRSCPListDelta;
	protected static List<String> paramInterEcnoList;
	protected static List<String> paramInterRSCPECNO14;
	protected List<String> paramInterRSCPECNO10;

	protected void setLists() {
		if (paramPositionList == null) {
			paramPositionList = new LinkedList<String>();
			setPositionList();
		}
		if (paramIntraEcnoListDelta == null) {
			paramIntraEcnoListDelta = new LinkedList<String>();
			setIntraEcnoList();
		}
		if (paramIntraRSCPListDelta == null) {
			paramIntraRSCPListDelta = new LinkedList<String>();
			setIntraRSCPList();
		}
		if (paramInterEcnoList == null) {
			paramInterEcnoList = new LinkedList<String>();
			setInterEcnoList();
		}
		if (paramInterRSCPECNO14 == null) {
			paramInterRSCPECNO14 = new LinkedList<String>();
			setInterRSCPECNO14List();
		}
		if (paramInterRSCPECNO10 == null) {
			paramInterRSCPECNO10 = new LinkedList<String>();
			setInterRSCPECNO10List();
		}

	}

	private void setPositionList() {
		if (paramPositionList.isEmpty()) {
			paramPositionList.add(COUNT_POSITION_1);
			paramPositionList.add(COUNT_POSITION_2);
			paramPositionList.add(COUNT_POSITION_3);
			paramPositionList.add(COUNT_POSITION_4);
			paramPositionList.add(COUNT_POSITION_5);
		}
	}

	private void setIntraEcnoList() {
		if (paramIntraEcnoListDelta.isEmpty()) {
			paramIntraEcnoListDelta.add(COUNT_INTRA_ECNO_DELTA_3);
			paramIntraEcnoListDelta.add(COUNT_INTRA_ECNO_DELTA_6);
			paramIntraEcnoListDelta.add(COUNT_INTRA_ECNO_DELTA_9);
			paramIntraEcnoListDelta.add(COUNT_INTRA_ECNO_DELTA_12);
			paramIntraEcnoListDelta.add(COUNT_INTRA_ECNO_DELTA_15);
		}

	}

	private void setIntraRSCPList() {
		if (paramIntraRSCPListDelta.isEmpty()) {
			paramIntraRSCPListDelta.add(COUNT_INTRA_RSCP_DELTA_3);
			paramIntraRSCPListDelta.add(COUNT_INTRA_RSCP_DELTA_6);
			paramIntraRSCPListDelta.add(COUNT_INTRA_RSCP_DELTA_9);
			paramIntraRSCPListDelta.add(COUNT_INTRA_RSCP_DELTA_12);
			paramIntraRSCPListDelta.add(COUNT_INTRA_RSCP_DELTA_15);
		}
	}

	private void setInterEcnoList() {
		if (paramInterEcnoList.isEmpty()) {
			paramInterEcnoList.add(COUNT_INTER_ECNO_6DB);
			paramInterEcnoList.add(COUNT_INTER_ECNO_9DB);
			paramInterEcnoList.add(COUNT_INTER_ECNO_12DB);
			paramInterEcnoList.add(COUNT_INTER_ECNO_15DB);
			paramInterEcnoList.add(COUNT_INTER_ECNO_18DB);
		}
	}

	private void setInterRSCPECNO14List() {
		if (paramInterRSCPECNO14.isEmpty()) {
			paramInterRSCPECNO14.add(COUNT_INTER_RSCP_105DB_ECNO_14DB);
			paramInterRSCPECNO14.add(COUNT_INTER_RSCP_95DB_ECNO_14DB);
			paramInterRSCPECNO14.add(COUNT_INTER_RSCP_85DB_ECNO_14DB);
			paramInterRSCPECNO14.add(COUNT_INTER_RSCP_L75DB_ECNO_14DB);
			paramInterRSCPECNO14.add(COUNT_INTER_RSCP_M75DB_ECNO_14DB);
		}
	}

	private void setInterRSCPECNO10List() {
		if (paramInterRSCPECNO10.isEmpty()) {
			paramInterRSCPECNO10.add(COUNT_INTER_RSCP_105DB_ECNO_10DB);
			paramInterRSCPECNO10.add(COUNT_INTER_RSCP_95DB_ECNO_10DB);
			paramInterRSCPECNO10.add(COUNT_INTER_RSCP_85DB_ECNO_10DB);
			paramInterRSCPECNO10.add(COUNT_INTER_RSCP_L75DB_ECNO_10DB);
			paramInterRSCPECNO10.add(COUNT_INTER_RSCP_M75DB_ECNO_10DB);
		}
	}

	/**
	 * check - model contains data or not
	 * 
	 * @return true, if successful
	 */
	public abstract boolean haveData();

	/**
	 * Form line of Object from data
	 * 
	 * @return the list
	 */
	public abstract List<Object> formLine();

	/**
	 * Clear data in model
	 */
	public abstract void clearData();

	/**
	 * Sets the time of handling
	 * 
	 * @param computeTime
	 *            the new time
	 */
	public void setTime(long computeTime) {
		this.computeTime = computeTime;
		clearData();

	}

	/**
	 * Gets the compute time
	 * 
	 * @return the compute time
	 */
	public long getComputeTime() {
		return computeTime;
	}

	/**
	 * Sets the data in model
	 * 
	 * @param bestCell
	 *            the best cell
	 * @param interfCell
	 *            the interference cell
	 * @return true, if data sets successful
	 */
	public abstract boolean setData(CellNodeInfo bestCell,
			InterfCellInfo interfCell);
}
