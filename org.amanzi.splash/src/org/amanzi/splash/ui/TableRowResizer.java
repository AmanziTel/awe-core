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
package org.amanzi.splash.ui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.event.MouseInputAdapter;

/**
 * <p>
 * Listener for row resizing
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class TableRowResizer extends MouseInputAdapter {
    private static Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
    private int mouseYOffset, resizingRow;
    private Cursor otherCursor = resizeCursor;
    private JTable table;

    private final JTable mainTable;

    /**
     * Constructor
     * 
     * @param rowHeader row header table
     * @param mainTable main table
     */
    public TableRowResizer(JTable rowHeader, JTable mainTable) {
        this.table = rowHeader;
        this.mainTable = mainTable;
        rowHeader.addMouseListener(this);
        rowHeader.addMouseMotionListener(this);
    }

    /**
     * Gets resizing row
     * 
     * @param p - point
     * @return index of row
     */
    private int getResizingRow(Point p) {
        return getResizingRow(p, table.rowAtPoint(p));
    }

    /**
     * Gets resizing row
     * 
     * @param p - point
     * @param row index of row
     * @return
     */
    private int getResizingRow(Point p, int row) {
        if (row == -1) {
            return -1;
        }
        int col = table.columnAtPoint(p);
        if (col == -1)
            return -1;
        Rectangle r = table.getCellRect(row, col, true);
        r.grow(0, -3);
        if (r.contains(p))
            return -1;

        int midPoint = r.y + r.height / 2;
        int rowIndex = (p.y < midPoint) ? row - 1 : row;

        return rowIndex;
    }

    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();

        resizingRow = getResizingRow(p);
        mouseYOffset = p.y - table.getRowHeight(resizingRow);
    }

    /**
     * Swap cursor
     */
    private void swapCursor() {
        Cursor tmp = table.getCursor();
        table.setCursor(otherCursor);
        otherCursor = tmp;
    }

    public void mouseMoved(MouseEvent e) {
        if ((getResizingRow(e.getPoint()) >= 0) != (table.getCursor() == resizeCursor)) {
            swapCursor();
        }
    }

    public void mouseDragged(MouseEvent e) {
        int mouseY = e.getY();

        if (resizingRow >= 0) {
            int newHeight = mouseY - mouseYOffset;
            if (newHeight > 0) {
                table.setRowHeight(resizingRow, newHeight);
                mainTable.setRowHeight(resizingRow, newHeight);
            }
        }
    }
}
