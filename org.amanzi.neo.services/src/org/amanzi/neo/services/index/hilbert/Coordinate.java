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
package org.amanzi.neo.services.index.hilbert;

/**
 * Enum for indexing coordinates in simple Hilbert Square 2x2
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
enum Coordinate {
	 
	 COORDINATE_0_0(0, 0),
	 COORDINATE_0_1(0, 1),
	 COORDINATE_1_0(1, 0),
	 COORDINATE_1_1(1, 1);
	
	
	 private int x;

	 private int y;

	 private Coordinate(int x, int y) {		 
		 this.x = x;
		 this.y = y;            
	 }
	
	 public int getX() {
	 	 return x;
	 }
	
	 public int getY() {
		 return y;
	 }
	 
	 public static Coordinate get(int x, int y) {
		 for (Coordinate coordinate : values()) {
			 if ((coordinate.getX() == x) && 
			     (coordinate.getY() == y)) {
				 return coordinate;
			 }
		 }
		 return null;
	 }

}
