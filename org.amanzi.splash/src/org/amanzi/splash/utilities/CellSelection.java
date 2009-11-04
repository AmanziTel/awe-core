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
    public static final DataFlavor CELL_DATA_FLAVOR = new DataFlavor(Cell.class, "Splash Cell");
    private static final DataFlavor[] flavors = {CELL_DATA_FLAVOR};
    private SelectedCellsSet cell;

    /**
     * @param cell
     */
    public CellSelection(SelectedCellsSet cell) {
        super();
        this.cell = cell;
    }

@Override
public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(flavors[CELL])) {
            return (Object)cell;
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
