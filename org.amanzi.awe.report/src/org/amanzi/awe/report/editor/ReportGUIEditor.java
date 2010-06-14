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

package org.amanzi.awe.report.editor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.lang.model.type.ErrorType;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.SelectionStyle;
import net.refractions.udig.project.ui.ApplicationGIS.DrawMapParameter;

import org.amanzi.awe.report.charts.Charts;
import org.amanzi.awe.report.model.Chart;
import org.amanzi.awe.report.model.IReportPart;
import org.amanzi.awe.report.model.Report;
import org.amanzi.awe.report.model.ReportImage;
import org.amanzi.awe.report.model.ReportMap;
import org.amanzi.awe.report.model.ReportModel;
import org.amanzi.awe.report.model.ReportTable;
import org.amanzi.awe.report.model.ReportText;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * Class for the report GUI editor
 * 
 * @author Pechko E.
 * @since 1.0.0
 */
public class ReportGUIEditor extends EditorPart  {
    private static final Logger LOGGER = Logger.getLogger(ReportGUIEditor.class);
    private boolean isDirty;
    private Composite frame;
    private Composite parent;
    private Text reportTitle;
    private List<Composite> parts = new ArrayList<Composite>(0);
    private ReportModel reportModel;
    private boolean isReportDataModified;
    private ScrolledComposite sc;
    private Font errorFont;
    private static final RGB RGB_WHITE = new RGB(255, 255, 255);
    private static final String SAVE = "Save";
    private static final String EDIT = "Edit";

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
    }

    @Override
    public boolean isDirty() {
        return this.isDirty;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void createPartControl(Composite parent) {
        this.parent = parent;
        sc = new ScrolledComposite(parent, SWT.V_SCROLL|SWT.H_SCROLL);
        GridLayout mainLayout = new GridLayout();
        sc.setLayout(new RowLayout(SWT.VERTICAL));
        frame = new Composite(sc, SWT.NONE);
        frame.setBackground(new Color(frame.getDisplay(), RGB_WHITE));
        frame.setLayout(mainLayout);
        sc.setBackground(new Color(parent.getDisplay(), RGB_WHITE));
        sc.setAlwaysShowScrollBars(true);
        parent.layout();
    }

    @Override
    public void setFocus() {
    }

    public void repaint() {
        // clear previous content
        Report report = reportModel.getReport();
        if (report != null) {
            List<IReportPart> reportParts = report.getParts();
            for (Control control : frame.getChildren()) {
                control.dispose();
            }
            parts.clear();
            // create all composites
            parts = new ArrayList<Composite>(reportParts.size());
            reportTitle = new Text(frame, SWT.MULTI);
            reportTitle.setFont(new Font(frame.getDisplay(), "Arial", 20, SWT.BOLD));
            reportTitle.setText(report.getName());
            // add errors if report has errors
            if (report.hasErrors()){
                errorFont = new Font(frame.getDisplay(), "Arial", 10, SWT.NORMAL);
                
                StringBuffer sb=new StringBuffer("Errors occurred during report creation:\n");
                for (String error:report.getErrors()){
                    sb.append("\n").append(error).append("\n");
                }
                Text error= new Text(frame, SWT.MULTI);
                error.setFont(errorFont);
                error.setForeground(new Color(frame.getDisplay(),255,0,0));
                error.setText(sb.toString()); 
            }
            for (int i = 0; i < reportParts.size(); i++) {
                IReportPart part = reportParts.get(i);
                createCompositeForPart(part);
            }
            forceRepaint();
        }
    }

    /**
     *
     * @param part
     */
    public void createCompositeForPart(IReportPart part) {
        if (part instanceof ReportText) {
            addTextPart((ReportText)part);
        } else if (part instanceof Chart) {
            addChartPart((Chart)part);
        } else if (part instanceof ReportImage) {
            addImagePart((ReportImage)part);
        } else if (part instanceof ReportTable) {
            addTablePart((ReportTable)part);
        } else if (part instanceof ReportMap) {
            addMapPart((ReportMap)part);
        }
        else {
            Composite currComposite = new Composite(frame, SWT.NONE);
            FillLayout mainLayout = new FillLayout(SWT.VERTICAL);
            currComposite.setLayout(mainLayout);
            Label label = new Label(currComposite, SWT.LEFT);
            label.setText("Composite for " + part.getClass().getName() + " is not implemented yet");
            currComposite.layout();
            parts.add(currComposite);
        }
    }

    private void addMapPart(ReportMap part) {
        final Composite currComposite = createComposite(part);
        IMap map = (part).getMap();

        BufferedImage bI = new BufferedImage(part.getWidth(), part.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics graphics2 = bI.getGraphics();
        try {
            File temp = File.createTempFile("map", ".png"); 
            Dimension size = new Dimension(part.getWidth(), part.getHeight());
            graphics2.setClip(1, 1, size.width - 2, size.height - 2);
            java.awt.Dimension awtSize = new java.awt.Dimension(size.width, size.height);
            ApplicationGIS.drawMap(new DrawMapParameter((Graphics2D)graphics2, awtSize, map, null, 90,
                    SelectionStyle.EXCLUSIVE_ALL_SELECTION, null, true, true));
            
            ImageIO.write(bI, "png", temp);
            
            GridData data = new GridData(GridData.CENTER);
//            data.widthHint = 600;

            Image image = new Image(currComposite.getDisplay(), temp.getPath());
            temp.delete();
            Label lbl = new Label(currComposite, SWT.CENTER);
            lbl.setImage(image);
            lbl.setBackground(new Color(frame.getDisplay(), RGB_WHITE));
            lbl.setLayoutData(data);
//            Button btnEdit = new Button(currComposite, SWT.PUSH);
//            btnEdit.setText("Edit");
//            btnEdit.setEnabled(false);
            parts.add(currComposite);
            createContextMenu(lbl, part);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTablePart(ReportTable reportTable) {
        final Composite currComposite = createComposite(reportTable);
        // title
        Text title = new Text(currComposite, SWT.SINGLE);
        title.setText(reportTable.getTitle());

        Table table = new Table(currComposite, SWT.BORDER | SWT.MULTI);
        final String[] headers = reportTable.getHeaders();
        try {
            TableColumn[] columns = new TableColumn[headers.length];
            for (int i = 0; i < headers.length; i++) {
                TableColumn tc = new TableColumn(table, SWT.LEFT);
                tc.setText(headers[i]);
                columns[i] = tc;
            }
            for (String[] row : reportTable.getTableItems()) {
                final TableItem item = new TableItem(table, SWT.NONE);
                item.setText(row);
            }
            //
            for (TableColumn column : columns) {
                column.pack();
            }
        } catch (RuntimeException e) {
            // TODO Handle RuntimeException
           LOGGER.debug("Table can't be created due to the error: "+e.getMessage());
           e.printStackTrace();
        }
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setRedraw(true);
        parts.add(currComposite);
        createContextMenu(table, reportTable);

    }

    /**
     * Adds chart
     * 
     * @param chart chart to be added
     */
    private void addChartPart(final Chart chart) {
        final Composite currComposite = createContainer(chart);
        GridData data = new GridData(GridData.CENTER);
        data.heightHint=chart.getHeight();
        data.widthHint=chart.getWidth();
        currComposite.setLayoutData(data);

        final JFreeChart jFreeChart = Charts.createChart(chart);
        jFreeChart.setTitle(chart.getTitle());
        ChartUtilities.applyCurrentTheme(jFreeChart);
        
        jFreeChart.addChangeListener(new ChartChangeListener() {

            @Override
            public void chartChanged(ChartChangeEvent arg0) {
                Object source = arg0.getSource();
                if (source instanceof TextTitle) {
                    String newTitleText = ((TextTitle)source).getText();
                    if (newTitleText != chart.getTitle()) {
                        chart.setTitle(newTitleText);
                        reportModel.getReport().firePartPropertyChanged(chart, Report.FIRST_ARGUMENT, chart.getTitle());
                        // fireEvent
                        LOGGER.debug("title\t" + chart.getTitle());
                    }
                }
                Plot plotOld = jFreeChart.getPlot();

                if (source instanceof CategoryPlot) {
                    PlotOrientation newOrientation=((CategoryPlot)plotOld).getOrientation();
                    if (!newOrientation.equals(chart.getOrientation())){
                        chart.setOrientation(newOrientation);
                        final String newValue = ":"+(newOrientation.equals(PlotOrientation.HORIZONTAL)?"horizontal":"vertical");
                        reportModel.getReport().firePartPropertyChanged(chart, "orientation", newValue);
                        LOGGER.debug("orientation\t" + (newOrientation.equals(PlotOrientation.HORIZONTAL)?"horizontal":"vertical"));
                    }
                    String newDomainAxisLabel = ((CategoryPlot)plotOld).getDomainAxis().getLabel();
                    if (!newDomainAxisLabel.equals(chart.getDomainAxisLabel())){
                        chart.setDomainAxisLabel(newDomainAxisLabel);
                        reportModel.getReport().firePartPropertyChanged(chart, "domain_axis", "'"+chart.getDomainAxisLabel()+"'");
                        LOGGER.debug("domain_axis\t" + chart.getDomainAxisLabel());
                    }
                    String newRangeAxisLabel = ((CategoryPlot)plotOld).getRangeAxis().getLabel();
                    if (!newRangeAxisLabel.equals(chart.getRangeAxisLabel())){
                        chart.setRangeAxisLabel(newRangeAxisLabel);
                        reportModel.getReport().firePartPropertyChanged(chart, "range_axis", "'"+chart.getRangeAxisLabel()+"'");
                        LOGGER.debug("range_axis\t" + chart.getRangeAxisLabel());
                    }
                }
                if (source instanceof XYPlot) {
                    //TODO
                }

            }
        });
        ChartComposite chartComposite = new ChartComposite(currComposite, SWT.NONE, jFreeChart, true);
//        createContextMenu(chartComposite, (IReportPart)chart);
        GridData data1 = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data1.widthHint = 600;
        data1.heightHint = 300;
        chartComposite.setLayoutData(data1);
       
        Composite buttonsPanel = new Composite(currComposite, SWT.NONE);
        buttonsPanel.setLayout(new GridLayout());
        buttonsPanel.setBackground(new Color(frame.getDisplay(), new RGB(255, 255, 255)));
        
        Button btnMoveUp = new Button(buttonsPanel, SWT.PUSH);
        btnMoveUp.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("org.amanzi.neo.loader", "/icons/16/Up.png").createImage());
        btnMoveUp.addSelectionListener(new SelectionAdapter(){

            @Override
            public void widgetSelected(SelectionEvent e) {
                movePartUp(chart);
            }
            
        });
        Button btnMoveDown = new Button(buttonsPanel, SWT.PUSH);
        btnMoveDown.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("org.amanzi.neo.loader", "/icons/16/Down.png").createImage());
        btnMoveDown.addSelectionListener(new SelectionAdapter(){

            @Override
            public void widgetSelected(SelectionEvent e) {
                movePartDown(chart);
            }
            
        });
        Button btnDelete = new Button(buttonsPanel, SWT.PUSH);
        btnDelete.setImage(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE).createImage());
        btnDelete.addSelectionListener(new SelectionAdapter(){

            @Override
            public void widgetSelected(SelectionEvent e) {
                removePart(chart);
            }
            
        });
        currComposite.layout();
        parts.add(currComposite);
    }

    /**
     * Adds new text
     */
    public void addNewText() {
        reportModel.updateModel( new StringBuffer("text '").append("Type new text here").append("'\n").toString());
    }

    /**
     * Adds text
     * 
     * @param part text to be added
     */
    private void addTextPart(ReportText part) {
        final Composite currComposite = createComposite(part);
        final Text text = new Text(currComposite, SWT.MULTI | SWT.WRAP);
        text.setText(part.getText());
        text.setBackground(new Color(text.getDisplay(), new RGB(255, 255, 255)));
        text.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                isReportDataModified = true;
                Object data = currComposite.getData();
                if (data instanceof ReportText) {
                    ReportText reportText = ((ReportText)data);
                    String newText = text.getText();
                    reportText.setText(newText);
                    reportModel.getReport().firePartPropertyChanged(reportText, Report.FIRST_ARGUMENT, newText);
                    
                }
                frame.pack();
            }

        });
        createContextMenu(text, part);
        
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = 600;
        text.setLayoutData(data);

        parts.add(currComposite);
    }

    /**
     * Opens file dialog to select image
     * 
     * @return file name if file has been selected
     */
    private String openFileDialog() {
        Shell shell = frame.getShell();
        FileDialog dialog = new FileDialog(shell, SWT.SAVE);
        dialog.setText("Select image file to be added");
        dialog.setFilterExtensions(new String[] {"*.jpg", "*.gif", "*.png"});
        dialog.setFilterNames(new String[] {"Image file (*.jpg)", "Image file (*.gif)", "Image file (*.png)"});
        if (dialog.open() != null) {
            return dialog.getFilterPath() + File.separator + dialog.getFileName();
        }
        return null;
    }
    /**
     * Adds a new chart
     */
    public void addNewChart(String script) {
            reportModel.updateModel(script);
      }
    /**
     * Invokes the ruby model builder to create a new part from the script
     */
    public void addPart(String script) {
            reportModel.updateModel(script);
      }


    /**
     * Adds new image
     */
    public void addNewImage() {
        String imageFileName = openFileDialog();
        if (imageFileName != null) {
            reportModel.updateModel(new StringBuffer("image '").append(imageFileName).append("'\n").toString());
        }

    }

    /**
     * Adds image
     * 
     * @param imagePart image part to be added
     */
    private void addImagePart(ReportImage imagePart) {
        Composite currComposite = createContainer(imagePart);

        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = 600;
        Image image = new Image(currComposite.getDisplay(), imagePart.getImageFileName());
        Label lbl = new Label(currComposite, SWT.CENTER);
        lbl.setImage(image);
        lbl.setBackground(new Color(frame.getDisplay(), RGB_WHITE));
        lbl.setLayoutData(data);
        createContextMenu(lbl, imagePart);
        Button btnEdit = new Button(currComposite, SWT.PUSH);
        btnEdit.setText("Edit");
        btnEdit.setEnabled(false);
        parts.add(currComposite);
    }

    /**
     * Creates a composite contains this report part
     * 
     * @param part report part
     * @return composite created
     */
    private Composite createContainer(IReportPart part) {
        Composite currComposite = new Composite(frame, SWT.NONE);
        GridLayout mainLayout = new GridLayout(2, false);
        currComposite.setLayout(mainLayout);
        currComposite.setBackground(new Color(frame.getDisplay(), new RGB(255, 255, 255)));
        currComposite.setData(part);
        return currComposite;
    }

    /**
     * Creates a composite contains this report part
     * 
     * @param part report part
     * @return composite created
     */
    private Composite createComposite(IReportPart part) {
        Composite currComposite = new Composite(frame, SWT.NONE);
        GridLayout mainLayout = new GridLayout();
        currComposite.setLayout(mainLayout);
        currComposite.setBackground(new Color(frame.getDisplay(), new RGB(255, 255, 255)));
        currComposite.setData(part);
        return currComposite;
    }

    /**
     * Actions needed for repainting
     */
    void forceRepaint() {
        sc.setContent(frame);
        frame.pack();
    }

    private void createContextMenu(Control control, final IReportPart part) {
        Menu menu = control.getMenu();
        if (menu == null)
            menu = new Menu(frame);
        MenuItem movePartUp = new MenuItem(menu, SWT.NONE);
        movePartUp.setText("Move up");
        movePartUp.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("org.amanzi.neo.loader", "/icons/16/Up.png").createImage());
        movePartUp.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                movePartUp(part);
            }

        });
        MenuItem movePartDown = new MenuItem(menu, SWT.NONE);
        movePartDown.setText("Move down");
        movePartDown.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("org.amanzi.neo.loader", "/icons/16/Down.png").createImage());
        movePartDown.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                movePartDown(part);
            }

        });
        MenuItem removePart = new MenuItem(menu, SWT.NONE);
        removePart.setText("Remove");
        removePart.setImage(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE).createImage());
        removePart.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                removePart(part);
            }

        });
        control.setMenu(menu);

    }

    /**
     * @return Returns the reportModel.
     */
    public ReportModel getReportModel() {
        return reportModel;
    }

    /**
     * @param reportModel The reportModel to set.
     */
    public void setReportModel(ReportModel reportModel) {
        this.reportModel = reportModel;
    }

    /**
     * Indicates whether report data modified or not
     * 
     * @return true if report data was modified, false otherwise
     */
    public boolean isReportDataModified() {
        return this.isReportDataModified;
    }

    /**
     * Setter
     * 
     * @param isReportDataModified The isReportDataModified to set.
     */
    public void setReportDataModified(boolean isReportDataModified) {
        this.isReportDataModified = isReportDataModified;
    }

    /**
     *
     * @param part
     */
    private void movePartUp(final IReportPart part) {
        int index = part.getIndex();
        if (index>0){
            reportModel.getReport().movePartUp(part);
            repaint();
        }
    }

    /**
     *
     * @param part
     */
    private void movePartDown(final IReportPart part) {
        int index = part.getIndex();
        if (index < parts.size() - 1) {
            reportModel.getReport().movePartDown(part);
            repaint();
        }
    }

    /**
     *
     * @param part
     */
    private void removePart(final IReportPart part) {
        parts.get(part.getIndex()).dispose();
        parts.remove(part.getIndex());
        forceRepaint();
        reportModel.getReport().removePart(part);
    }

    

}
