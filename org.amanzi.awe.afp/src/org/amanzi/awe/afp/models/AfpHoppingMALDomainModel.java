package org.amanzi.awe.afp.models;

import org.amanzi.neo.services.INeoConstants;
import org.neo4j.graphdb.Node;

public class AfpHoppingMALDomainModel extends AfpDomainModel{
	//Assuming max size can be 13
	//The array index corresponds to Hopping TRXs
	private int[] MALSize = new int[] {3, 3, 4, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};

	/**
	 * @return the mALSize array
	 */
	public int[] getMALSize() {
		return MALSize;
	}

	/**
	 * @param size the mALSize array to set
	 */
	public void setMALSize(int[] size) {
		MALSize = size;
	}
	public int getMALSize(int index) {
		if(index < MALSize.length)
			return MALSize[index];
		
		return -1;
	}
	public void setMALSize(int index, int size) {
		if(index < MALSize.length)
			MALSize[index] = size;
	}
	
	/**
	 * @return the mALSize for 0 hopping TRX
	 */
	public int getMALSize0() {
		return MALSize[0];
	}

	/**
	 * @param size the mALSize to set for 0 hopping TRX
	 */
	public void setMALSize0(int size) {
		MALSize[0] = size;
	}
	
	
	/**
	 * @return the mALSize for 1 hopping TRX
	 */
	public int getMALSize1() {
		return MALSize[1];
	}

	/**
	 * @param size the mALSize to set for 1 hopping TRX
	 */
	public void setMALSize1(int size) {
		MALSize[1] = size;
	}
	
	
	/**
	 * @return the mALSize for 2 hopping TRX
	 */
	public int getMALSize2() {
		return MALSize[2];
	}

	/**
	 * @param size the mALSize to set for 2 hopping TRX
	 */
	public void setMALSize2(int size) {
		MALSize[2] = size;
	}
	
	
	/**
	 * @return the mALSize for 3 hopping TRX
	 */
	public int getMALSize3() {
		return MALSize[3];
	}

	/**
	 * @param size the mALSize to set for 3 hopping TRX
	 */
	public void setMALSize3(int size) {
		MALSize[3] = size;
	}
	
	
	/**
	 * @return the mALSize for 4 hopping TRX
	 */
	public int getMALSize4() {
		return MALSize[4];
	}

	/**
	 * @param size the mALSize to set for 4 hopping TRX
	 */
	public void setMALSize4(int size) {
		MALSize[4] = size;
	}
	
	
	/**
	 * @return the mALSize for 5 hopping TRX
	 */
	public int getMALSize5() {
		return MALSize[5];
	}

	/**
	 * @param size the mALSize to set for 5 hopping TRX
	 */
	public void setMALSize5(int size) {
		MALSize[5] = size;
	}
	
	
	/**
	 * @return the mALSize for 6 hopping TRX
	 */
	public int getMALSize6() {
		return MALSize[6];
	}

	/**
	 * @param size the mALSize to set for 6 hopping TRX
	 */
	public void setMALSize6(int size) {
		MALSize[6] = size;
	}
	
	
	/**
	 * @return the mALSize for 7 hopping TRX
	 */
	public int getMALSize7() {
		return MALSize[7];
	}

	/**
	 * @param size the mALSize to set for 7 hopping TRX
	 */
	public void setMALSize7(int size) {
		MALSize[7] = size;
	}
	
	
	/**
	 * @return the mALSize for 8 hopping TRX
	 */
	public int getMALSize8() {
		return MALSize[8];
	}

	/**
	 * @param size the mALSize to set for 8 hopping TRX
	 */
	public void setMALSize8(int size) {
		MALSize[8] = size;
	}
	
	
	/**
	 * @return the mALSize for 9 hopping TRX
	 */
	public int getMALSize9() {
		return MALSize[9];
	}

	/**
	 * @param size the mALSize to set for 9 hopping TRX
	 */
	public void setMALSize9(int size) {
		MALSize[9] = size;
	}
	
	
	/**
	 * @return the mALSize for 10 hopping TRX
	 */
	public int getMALSize10() {
		return MALSize[10];
	}

	/**
	 * @param size the mALSize to set for 10 hopping TRX
	 */
	public void setMALSize10(int size) {
		MALSize[10] = size;
	}
	
	
	/**
	 * @return the mALSize for 11 hopping TRX
	 */
	public int getMALSize11() {
		return MALSize[11];
	}

	/**
	 * @param size the mALSize to set for 11 hopping TRX
	 */
	public void setMALSize11(int size) {
		MALSize[11] = size;
	}
	
	
	/**
	 * @return the mALSize for 12 hopping TRX
	 */
	public int getMALSize12() {
		return MALSize[12];
	}

	/**
	 * @param size the mALSize to set for 12 hopping TRX
	 */
	public void setMALSize12(int size) {
		MALSize[12] = size;
	}

	public static AfpHoppingMALDomainModel getModel(Node n) {

		try {
			String name = (String) n.getProperty(INeoConstants.PROPERTY_NAME_NAME);
			int[] malsize = (int[]) n.getProperty(INeoConstants.AFP_PROPERTY_MAL_SIZE_NAME);
			
			AfpHoppingMALDomainModel model = new AfpHoppingMALDomainModel();

			model.setName(name);
			model.setMALSize(malsize);
		
			return model;
		} catch (Exception e) {
			return null;
		}
	}
}
