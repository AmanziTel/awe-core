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

package org.amanzi.awe.report.pdf;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.command.navigation.SetViewportBBoxCommand;
import net.refractions.udig.project.render.RenderException;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.ApplicationGIS.DrawMapParameter;
import net.refractions.udig.project.ui.SelectionStyle;

import org.amanzi.awe.report.charts.Charts;
import org.amanzi.awe.report.model.Chart;
import org.amanzi.awe.report.model.IReportPart;
import org.amanzi.awe.report.model.Report;
import org.amanzi.awe.report.model.ReportImage;
import org.amanzi.awe.report.model.ReportMap;
import org.amanzi.awe.report.model.ReportTable;
import org.amanzi.awe.report.model.ReportText;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.TextTitle;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.ImgTemplate;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * <p>
 * Engine that prints Amanzi report to a PDF file
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class PDFPrintingEngine {
    private static final Logger LOGGER = Logger.getLogger(PDFPrintingEngine.class);
    private static final String REPORT_DIRECTORY = "Amanzi report";
    public static final String DEFAULT_REPORT_DIRECTORY = System.getProperty("user.home") + File.separator + REPORT_DIRECTORY;
    private final int INDENTATION = 30;

    /**
     * Prints report. Uses the filename specified or generates the file name
     * 
     * @param report report to be printed
     */
    public void printReport(Report report) {
        final Rectangle paperSize = PageSize.A4;
        Document document = new Document(PageSize.A4, 30, 30,30, 30);
        String fileName = System.getProperty("user.home") + File.separator + REPORT_DIRECTORY;
        File directory = new File(fileName);
        if (!directory.exists()) {
            directory.mkdir();
        }
        String reportFileName = report.getFile();
        if (reportFileName == null || reportFileName.length() == 0) {
            fileName += File.separator + "report" + System.currentTimeMillis() + ".pdf";
            report.setFile(fileName);
        } else {
            if (!reportFileName.contains(File.separator)) {
                // the report file name doesn't contain the directory name
                // save it to default directory ('user.home'/Amanzi reports)
                fileName += File.separator + reportFileName;
            } else {
                // the report file name contain the directory name
                fileName = reportFileName;
            }
        }
        LOGGER.debug("filename " + fileName);
        File outputPdfFile = new File(fileName);

        PdfWriter writer = null;
        try {
            writer = PdfWriter.getInstance(document, new FileOutputStream(outputPdfFile));
            writer.setPageEvent(new AWEPageEvent());
            document.open();
            final Paragraph title = new Paragraph(report.getName());

            title.setIndentationLeft(INDENTATION);
            title.setIndentationRight(INDENTATION);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.getFont().setStyle(Font.BOLD);
            title.getFont().setSize(20);
            document.add(title);
            
            float maxHeight = (paperSize.getHeight() - 2 * INDENTATION);
            float maxWidth = (paperSize.getWidth() - 2 * INDENTATION);
            final List<IReportPart> parts = report.getParts();
            for (IReportPart part : parts) {
                if (part instanceof ReportMap) {
                    IMap map = ((ReportMap)part).getMap();

                    PdfContentByte cb = writer.getDirectContent();
                    PdfTemplate tp = cb.createTemplate(part.getWidth(), part.getHeight());
                    Graphics2D g2 = tp.createGraphics(part.getWidth(), part.getHeight(), new DefaultFontMapper());
                    draw(g2, map, part.getWidth(), part.getHeight());
                    g2.dispose();
                    addTemplateAsImage(document, tp, maxHeight, maxWidth);
                } else if (part instanceof Chart) {
                    Chart chart = (Chart)part;
                    final JFreeChart jFreeChart = Charts.createChart(chart);
                    jFreeChart.setTitle(chart.getTitle());
                    for (String subtitle : chart.getSubtitles()) {
                        jFreeChart.addSubtitle(new TextTitle(subtitle));
                    }
                    if (!chart.isShowLegend())
                        jFreeChart.removeLegend();
                    ChartUtilities.applyCurrentTheme(jFreeChart);

                    int width = Math.min((int)PageSize.A4.getWidth(), chart.getWidth());
                    int height = chart.getHeight();

                    PdfContentByte cb = writer.getDirectContent();
                    PdfTemplate tp = cb.createTemplate(width, height);
                    Graphics2D g2 = tp.createGraphics(width, height, new DefaultFontMapper());
                    jFreeChart.draw(g2, new java.awt.Rectangle(width, height));
                    g2.dispose();

                    addTemplateAsImage(document, tp, maxHeight, maxWidth);
                } else if (part instanceof ReportText) {
                    ReportText text = (ReportText)part;
                    final Paragraph paragraph = new Paragraph(text.getText());
                    paragraph.setIndentationLeft(INDENTATION);
                    paragraph.setIndentationRight(INDENTATION);
                    document.add(paragraph);
                } else if (part instanceof ReportImage) {
                    ReportImage image = (ReportImage)part;
                    Image img = Image.getInstance(image.getImageFileName());
                    document.add(img);
                } else if (part instanceof ReportTable) {
                    ReportTable table = (ReportTable)part;
                    String[] headers = table.getHeaders();
                    int n = headers.length;
                    Table pdfTable = new Table(n);
                    pdfTable.setPadding(5);
//                    pdfTable.setWidth(1);

                    Cell pdfCell = new Cell(table.getTitle());
                    pdfCell.setColspan(n);
                    pdfTable.addCell(pdfCell);
                    pdfCell.setHorizontalAlignment(Cell.ALIGN_CENTER);
                    

                    for (String header : headers) {
                        pdfCell = new Cell(header);
                        pdfCell.setHeader(true);
                        pdfCell.setGrayFill(0.751f);
                        pdfTable.addCell(pdfCell);
                    }
                    //force headers to  be printed on every page
                    pdfTable.endHeaders();
                    for (String[] row : table.getTableItems()) {
                        for (String cell : row) {
                            pdfTable.addCell(cell);
                        }
                    }
                    // large table have to be splitted
                    pdfTable.setTableFitsPage(false);
                    document.add(pdfTable);
                }
            }
            document.newPage();
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            document.close();
            writer.close();
        }
    }

    /**
     * Draws a map
     * 
     * @param graphics instance of Graphics2D
     * @param map uDig map
     * @param width width of the map
     * @param height height of the map
     */
    private void draw(Graphics2D graphics, IMap map, int width, int height) {
        try {
            Dimension size = new Dimension(width, height);
            // reduce set a 1 pixel clip bound around outside to prevent
            // some Graphics2D implementations (itext!) from bleeding into space
            // outside the graphics canvas
            graphics.setClip(1, 1, size.width - 2, size.height - 2);

            java.awt.Dimension awtSize = new java.awt.Dimension(size.width, size.height);
            IMap modifiedMap = null;
            // ApplicationGIS.drawMap(new DrawMapParameter(graphics, awtSize, getMap(), monitor,
            // true));
            modifiedMap = ApplicationGIS.drawMap(new DrawMapParameter(graphics, awtSize, map, null /*
                                                                                                     * use
                                                                                                     * current
                                                                                                     * scale
                                                                                                     */, 90, SelectionStyle.EXCLUSIVE_ALL_SELECTION, null, true, true));

            // ApplicationGIS.drawMap makes a copy of the map, and may change its bounds. If it does
            // change
            // the bounds then update the original map to match (this will force the mapgraphics to
            // update too)
            if (!map.getViewportModel().getBounds().equals(modifiedMap.getViewportModel().getBounds())) {
                SetViewportBBoxCommand cmdBBox = new SetViewportBBoxCommand(modifiedMap.getViewportModel().getBounds());
                map.sendCommandSync(cmdBBox);
            }

            // restore regular clip rectangle
            graphics.setClip(0, 0, size.width, size.height);

        } catch (RenderException e) {
            e.printStackTrace();
        }
    }
    /**
     * Adds the given template as an image
     * 
     * @param document the document
     * @param template the template
     * @param maxHeight maximum height
     * @param maxWidth maximum width
     * @throws BadElementException
     * @throws DocumentException
     */
    private void addTemplateAsImage(Document document, PdfTemplate template, float maxHeight, float maxWidth)
            throws BadElementException, DocumentException {
        ImgTemplate image = new ImgTemplate(template);
        image.setIndentationLeft(INDENTATION);
        image.setIndentationRight(INDENTATION);
        image.setAlignment(Image.ALIGN_MIDDLE);
        scaleImage(image, maxHeight, maxWidth);
        document.add(image);
    }

    /**
     * Scales an image to maximum height and width
     * 
     * @param image the image to scale
     * @param maxHeight maximum height
     * @param maxWidth maximum width
     */
    public static void scaleImage(Image image, float maxHeight, float maxWidth) {
        float scalefactorH = Math.min(1, maxHeight / image.getHeight());
        float scalefactorW = Math.min(1, maxWidth / image.getWidth());
        float scalefactor = Math.min(scalefactorH, scalefactorW);
        image.scalePercent(100 * scalefactor);
    }
}
