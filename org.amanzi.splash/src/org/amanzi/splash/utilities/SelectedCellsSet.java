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

package org.amanzi.splash.utilities;

import java.io.Serializable;
import java.util.ArrayList;

import org.amanzi.splash.swing.Cell;

/**
 * <p>
 * Class for storing selected cells
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class SelectedCellsSet implements Serializable {
    int row;
    int column;
    ArrayList<Cell> cells;

    /**
     * @return Returns the row.
     */
    public int getRow() {
        return row;
    }

    /**
     * @param row The row to set.
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * @return Returns the column.
     */
    public int getColumn() {
        return column;
    }

    /**
     * @param column The column to set.
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * @return Returns the cells.
     */
    public ArrayList<Cell> getCells() {
        return cells;
    }

    /**
     * @param cells The cells to set.
     */
    public void setCells(ArrayList<Cell> cells) {
        this.cells = cells;
    }

}
