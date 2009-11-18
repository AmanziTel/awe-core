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

package org.amanzi.neo.index.hilbert.map;

import java.util.HashMap;

/**
 * Map class that mapps Coordinate and SquadMapping 
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class HilbertSqaud extends HashMap<Coordinate, SquadMapping> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7641139166436147897L;
	
	private static final HilbertSqaud rootSquad = new HilbertSqaud();
	
	static {
		HilbertSqaud squadB = new HilbertSqaud();
		HilbertSqaud squadC = new HilbertSqaud();
		HilbertSqaud squadD = new HilbertSqaud();
		
		//first		
		SquadMapping mappingA0 = new SquadMapping(0, squadD);
		rootSquad.put(Coordinate.COORDINATE_0_0, mappingA0);
		
		SquadMapping mappingA1 = new SquadMapping(1, rootSquad);
		rootSquad.put(Coordinate.COORDINATE_0_1, mappingA1);
		
		SquadMapping mappingA2 = new SquadMapping(3, squadB);
		rootSquad.put(Coordinate.COORDINATE_1_0, mappingA2);
		
		SquadMapping mappingA3 = new SquadMapping(2, rootSquad);
		rootSquad.put(Coordinate.COORDINATE_1_1, mappingA3);
		
		//second
		SquadMapping mappingB0 = new SquadMapping(2, squadB);
		squadB.put(Coordinate.COORDINATE_0_0, mappingB0);
		
		SquadMapping mappingB1 = new SquadMapping(1, squadB);
		squadB.put(Coordinate.COORDINATE_0_1, mappingB1);
		
		SquadMapping mappingB2 = new SquadMapping(3, rootSquad);
		squadB.put(Coordinate.COORDINATE_1_0, mappingB2);
		
		SquadMapping mappingB3 = new SquadMapping(0, squadC);
		squadB.put(Coordinate.COORDINATE_1_1, mappingB3);
		
		//third
		SquadMapping mappingC0 = new SquadMapping(2, squadC);
		squadC.put(Coordinate.COORDINATE_0_0, mappingC0);
		
		SquadMapping mappingC1 = new SquadMapping(3, squadD);
		squadC.put(Coordinate.COORDINATE_0_1, mappingC1);
		
		SquadMapping mappingC2 = new SquadMapping(1, squadC);
		squadC.put(Coordinate.COORDINATE_1_0, mappingC2);
		
		SquadMapping mappingC3 = new SquadMapping(0, squadB);
		squadC.put(Coordinate.COORDINATE_1_1, mappingC3);
		
		//fourth
		SquadMapping mappingD0 = new SquadMapping(0, rootSquad);
		squadD.put(Coordinate.COORDINATE_0_0, mappingD0);
		
		SquadMapping mappingD1 = new SquadMapping(3, squadC);
		squadD.put(Coordinate.COORDINATE_0_1, mappingD1);
		
		SquadMapping mappingD2 = new SquadMapping(1, squadD);
		squadD.put(Coordinate.COORDINATE_1_0, mappingD2);
		
		SquadMapping mappingD3 = new SquadMapping(2, squadD);
		squadD.put(Coordinate.COORDINATE_1_1, mappingD3);		
	}
	
	public HilbertSqaud() {
		super(4);
	}
	
	/**
	 * Calculates position in Hilbert Curve for given coordinates 
	 * 
	 * @param x coordinate X
	 * @param y coordinate Y
	 * @param maxOrder maxOrder of Hilbert Curve
	 * @param minOrder minOrder of Hilbert Curve
	 * @return position in Hilbert Curve
	 */
	public static int getHilbertPosition(int x, int y, int maxOrder, int minOrder) {
		HilbertSqaud currentSquad = rootSquad;
		
		int position = 0;
		
		for (int i = maxOrder - 1; i >= minOrder; i--) {
			position <<= 2;
			
			int quad_x = 1;
			if (((x & (1 << i)) == 0)) {
				quad_x = 0;
			}
			
			int quad_y = 1;
			if (((y & (1 << i)) == 0)) {
				quad_y = 0;
			}
			
			Coordinate coordinate = Coordinate.get(quad_x, quad_y);
			SquadMapping newMapping = currentSquad.get(coordinate);
			currentSquad = newMapping.getSquad();
			position |= newMapping.getIndex();
		}
		
		return position;
	}
	
}
