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

package org.amanzi.printing;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import net.refractions.udig.printing.model.AbstractBoxPrinter;

import org.amanzi.awe.report.model.ReportTable;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * BoxPrinter that prints tables
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class TableBoxPrinter extends AbstractBoxPrinter {
    private ReportTable table;
    private static final int SPACE_AFTER_TITLE = 20;
    private static final int SPACE_AFTER_ROW = 2;
    
    private static final int DEFAULT_COLUMN_WIDTH = 40;

    @Override
    public void draw(Graphics2D graphics, IProgressMonitor monitor) {
        ReportTable tableToPrint = getTable();
        int boxWidth = getBox().getSize().width;
        int boxHeight = getBox().getSize().height;
        int y = 0;

        Font font = new Font("Arial", Font.PLAIN, 12);// TODO use different fonts
        graphics.setFont(font);
        graphics.setColor(Color.BLACK);
        // Font font = graphics.getFont();
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int ascent = fontMetrics.getAscent();// TODO use different fonts
        y += ascent;
        // draw title
        graphics.drawString(tableToPrint.getTitle(), 0, y);

        // add space between title and table
        y += SPACE_AFTER_TITLE;
        int n = tableToPrint.getHeaders().length;
        // x coordinates of columns
        int x_pos[] = new int[n];
        x_pos[0] = 0;

        Font headerFont = graphics.getFont().deriveFont(Font.BOLD);
        graphics.setFont(headerFont);
        ascent = graphics.getFontMetrics().getAscent();
        y += ascent;
        int nRow;
        int nCol;
        // SWTUtils.toAwtFont(device, font)
        String[] columns = tableToPrint.getHeaders();
        for (nCol = 0; nCol < n; nCol++) {
            String col = columns[nCol];
            int colWidth = Math.max(graphics.getFontMetrics().stringWidth(col), DEFAULT_COLUMN_WIDTH);
            if (x_pos[nCol] + colWidth > boxWidth) {
                n = nCol;
                break;
            }
            // calculate x coordinate of the next column
            if (nCol + 1 < n) {
                x_pos[nCol + 1] = x_pos[nCol] + colWidth;
            }
            graphics.drawString(col, x_pos[nCol], y);
        }
        y += graphics.getFontMetrics().getHeight();// height of the header
        // set font of table's content
        graphics.setFont(font);

        // calculate y coordinate of the next row
        int height = graphics.getFontMetrics().getHeight();
        int rowH = Math.max(height, 10);
        int maxRow = Math.min((boxHeight - y) / (rowH+SPACE_AFTER_ROW), tableToPrint.getTableItems().size());
        // TODO print according to current page number

        for (int r = 0; r < maxRow; r++) {
            String[] items = tableToPrint.getTableItems().get(r);
            for (int c = 0; c < nCol; c++) {
                graphics.drawString(items[c], x_pos[c], y);
            }
            y += rowH+SPACE_AFTER_ROW;
        }
        // System.gc();

    }

    @Override
    public String getExtensionPointID() {
        return "org.amanzi.splash.printing.TableBoxPrinter";
    }

    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }

    /**
     * @return Returns the table.
     */
    public ReportTable getTable() {
        if (table == null) {
            table = getTestTable();
        }
        return table;
    }

    /**
     * Creates a test table
     * 
     * @return table created
     */
    public static ReportTable getTestTable() {
        int col_num = 2 + (int)(Math.random() * 10);
        int row_num = 3 + (int)(Math.random() * 20);
        String[] headers = new String[col_num];
        String[][] rows = new String[row_num][col_num];
        for (int i = 0; i < col_num; i++) {
            headers[i] = "col" + i;
        }
        for (int i = 0; i < row_num; i++) {
            for (int j = 0; j < col_num; j++) {
                rows[i][j] = "row " + i + " " + j;
            }
        }
        ReportTable reportTable = new ReportTable("Title");
        for (String[] row : rows) {
            reportTable.addRow(row);
        }
        reportTable.setHeaders(headers);
        return reportTable;
    }

    /**
     * @param table The table to set.
     */
    public void setTable(ReportTable table) {
        this.table = table;
    }

}
