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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import org.amanzi.splash.swing.Cell;

/**
 * <p>
 * Cell selection ClipboardOwner
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class CellSelection implements Transferable, ClipboardOwner {
    private static final int CELL = 0;
    private static final int STRING = 1;
    public static final DataFlavor CELL_DATA_FLAVOR = new DataFlavor(Cell.class, "Splash Cell");
    private static final DataFlavor[] flavors = {CELL_DATA_FLAVOR, DataFlavor.stringFlavor};
    private SelectedCellsSet cell;
    private String stringData;

    /**
     * @param cell
     */
    public CellSelection(SelectedCellsSet cell) {
        super();
        this.cell = cell;
        this.stringData = getStringDataByCellSet(cell);
    }

    /**
     * @param cell2
     * @return
     */
    private String getStringDataByCellSet(SelectedCellsSet cellsSet) {
        ArrayList<Cell> cels = cellsSet.getCells();
        StringBuilder result = new StringBuilder();
        int curRow = cellsSet.getRow();
        int curCol = cellsSet.getColumn();
        for (Cell curCell : cels) {
            int cellRow = curCell.getRow();
            int cellCol = curCell.getColumn();
            for (; curRow < cellRow; curRow++) {
                result.append("\n");
                curCol = cellsSet.getColumn();
            }
            for (; curCol < cellCol; curCol++) {
                result.append("\t");
            }
            result.append(curCell.getValue().toString());
        }
        return result.toString();
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(flavors[CELL])) {
            return (Object)cell;
        } else if (flavor.equals(flavors[STRING])) {
            return stringData;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return (DataFlavor[])flavors.clone();
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (int i = 0; i < flavors.length; i++) {
            if (flavor.equals(flavors[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}
