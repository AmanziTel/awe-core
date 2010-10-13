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

package org.amanzi.awe.report.grid.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import org.amanzi.awe.report.grid.GridReportPlugin;
import org.amanzi.awe.statistics.database.entity.DatasetStatistics;
import org.amanzi.awe.statistics.database.entity.Statistics;
import org.amanzi.awe.statistics.database.entity.StatisticsCell;
import org.amanzi.awe.statistics.database.entity.StatisticsGroup;
import org.amanzi.awe.statistics.database.entity.StatisticsRow;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.eclipse.core.runtime.FileLocator;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class XlsStatisticsExporter implements IStatisticsExporter {
    public enum ExportMode {
        PER_GROUP, PER_CELL;
    }

    private String outputDirectory;

    /**
     * 
     */
    public XlsStatisticsExporter(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * @param outputDirectory TODO
     * @param postfix TODO
     * @param templateName TODO
     */
    public String copyTemplate(String outputDirectory, String postfix, String templateName) {
        URL entry = GridReportPlugin.getDefault().getBundle().getEntry(String.format("templates/%s",templateName));
        try {
            URL template = FileLocator.toFileURL(entry);
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(template.getPath()));
            String reportName = outputDirectory + File.separatorChar + "report" + postfix + ".xls";
            FileOutputStream fileOut = new FileOutputStream(reportName);
            wb.write(fileOut);
            fileOut.close();
            return reportName;

        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    public void export(Node datasetNode, String networkLevel, String timeLevel, String templateName) {
        Iterator<Relationship> iterator = datasetNode.getRelationships(GeoNeoRelationshipTypes.ANALYSIS).iterator();
        if (iterator.hasNext()) {
            Node node = iterator.next().getEndNode();
            DatasetStatistics dsStatistics = new DatasetStatistics(node);
            Statistics statistics = dsStatistics.getNetworkDimension().getLevelByKey(networkLevel).getStatistics(timeLevel);
            Collection<StatisticsGroup> groups = statistics.getGroups().values();
            HSSFWorkbook wb;
            try {
                for (StatisticsGroup group : groups) {
                    String wbName = copyTemplate(outputDirectory, group.getGroupName(), templateName);
                    wb = new HSSFWorkbook(new FileInputStream(wbName));
                    exportGroup(wb, group);
                    FileOutputStream stream = new FileOutputStream(wbName);
                    wb.write(stream);
                    stream.close();
                }
            } catch (FileNotFoundException e) {
                // TODO Handle FileNotFoundException
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (IOException e) {
                // TODO Handle IOException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }

    }

    public void exportTotals(Statistics statistics) {
        // Node node = null;
        // DatasetStatistics dsStatistics = new DatasetStatistics(node);
        // Statistics statistics =
        // dsStatistics.getNetworkDimension().getLevelByKey("site").getStatistics("site", "hourly");
        for (StatisticsGroup group : statistics.getGroups().values()) {
            StatisticsRow total = group.getRowByKey("total");
            for (StatisticsCell cell : total.getCells().values()) {

            }
        }

    }

    public void exportGroupTotals(StatisticsGroup group, ExportMode mode) {
        switch (mode) {
        case PER_GROUP:
        case PER_CELL:
        }
        StatisticsRow total = group.getRowByKey("total");
        for (StatisticsCell cell : total.getCells().values()) {

        }

    }

    public void exportGroup(HSSFWorkbook wb, StatisticsGroup group) {
        int ind = wb.getNameIndex("Start");
        HSSFName name = wb.getNameAt(ind);
        AreaReference aref = new AreaReference(name.getRefersToFormula());
        org.apache.poi.ss.util.CellReference[] crefs = aref.getAllReferencedCells();
        Sheet sheet = wb.getSheet(crefs[0].getSheetName());
        int rowNum = crefs[0].getRow();
        Row r = sheet.getRow(rowNum);
        short colNum = crefs[0].getCol();
        Cell c = r.getCell(colNum);
        c.setCellValue(new HSSFRichTextString(group.getGroupName()));
        rowNum++;
        colNum++;
        int i = 0;
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
        CellStyle style = createSimpleStyle(wb);

        for (StatisticsRow statRow : group.getRows().values()) {
            if (!statRow.isSummaryNode()) {
                Cell headerCell = r.getCell(colNum + i, Row.CREATE_NULL_AS_BLANK);
                String rowName = statRow.getName();
                headerCell.setCellValue(new HSSFRichTextString(rowName));
                headerCell.setCellStyle(style);
                int j = 0;
                for (StatisticsCell statCell : statRow.getCells().values()) {
                    Row row = sheet.getRow(rowNum + j);
                    if (row == null) {
                        row = sheet.createRow(rowNum + j);
                    }
                    if (i == 0) {
                        Cell cell = row.getCell(colNum - 1, Row.CREATE_NULL_AS_BLANK);
                        cell.setCellValue(new HSSFRichTextString(statCell.getName()));
                        cell.setCellStyle(style);
                    }
                    Cell cell = row.getCell(colNum + i, Row.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(statCell.getValue().doubleValue());
                    cell.setCellStyle(style);
                    j++;
                }
                i++;
            }
        }
        reevaluateCell(wb, evaluator, "Count");
    }

    private void reevaluateCell(Workbook wb, FormulaEvaluator evaluator, String indexName) {
        int ind = wb.getNameIndex(indexName);
        Name name = wb.getNameAt(ind);
        AreaReference aref = new AreaReference(name.getRefersToFormula());
        org.apache.poi.ss.util.CellReference[] crefs = aref.getAllReferencedCells();
        Sheet sheet = wb.getSheet(crefs[0].getSheetName());
        int rowNum = crefs[0].getRow();
        Row r = sheet.getRow(rowNum);
        short colNum = crefs[0].getCol();
        Cell c = r.getCell(colNum);
        evaluator.evaluateFormulaCell(c);
    }

    /**
     * @param wb
     */
    private CellStyle createSimpleStyle(HSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        return style;
    }
}
