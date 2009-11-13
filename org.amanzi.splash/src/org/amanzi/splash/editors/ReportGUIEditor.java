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

package org.amanzi.splash.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.splash.chart.Charts;
import org.amanzi.splash.report.IReportPart;
import org.amanzi.splash.report.model.Chart;
import org.amanzi.splash.report.model.Report;
import org.amanzi.splash.report.model.ReportImage;
import org.amanzi.splash.report.model.ReportModel;
import org.amanzi.splash.report.model.ReportText;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * Class for the report GUI editor
 * 
 * @author Pechko E.
 * @since 1.0.0
 */
public class ReportGUIEditor extends EditorPart {

    private boolean isDirty;
    private Composite frame;
    private Composite parent;
    private List<Composite> parts = new ArrayList<Composite>(0);
    private ReportModel reportModel;
    private boolean isReportDataModified;
    private ScrolledComposite sc;
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
        sc = new ScrolledComposite(parent, SWT.V_SCROLL);
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
        if (report!=null){
        List<IReportPart> reportParts = report.getParts();
        for (Control control : frame.getChildren()) {
            control.dispose();
        }
        parts.clear();
        // create all composites
        parts = new ArrayList<Composite>(reportParts.size());
        for (int i = 0; i < reportParts.size(); i++) {
            IReportPart part = reportParts.get(i);
            if (part instanceof ReportText) {
                addTextPart((ReportText)part, false);
            } else if (part instanceof Chart) {
                addChartPart((Chart)part);
            } else if (part instanceof ReportImage) {
                addImagePart((ReportImage)part);
            } else {
                Composite currComposite = new Composite(frame, SWT.NONE);
                FillLayout mainLayout = new FillLayout(SWT.VERTICAL);
                currComposite.setLayout(mainLayout);
                Label label = new Label(currComposite, SWT.LEFT);
                label.setText("Composite for " + part.getClass().getName() + " is not implemented yet");
                currComposite.layout();
                parts.add(currComposite);
            }
        }
        forceRepaint();
        }
    }

    /**
     * Adds chart
     * 
     * @param chart chart to be added
     */
    private void addChartPart(Chart chart) {
        final Composite currComposite = createContainer(chart);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        currComposite.setLayoutData(data);
        DefaultCategoryDataset chartDataset = reportModel.getChartDataset(chart);
        JFreeChart jFreeChart = Charts.createBarChart(chartDataset);
        ChartComposite chartComposite = new ChartComposite(currComposite, SWT.NONE, jFreeChart, true);

        GridData data1 = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data1.widthHint = 600;
        data1.heightHint = 300;
        chartComposite.setLayoutData(data1);

        Button btnEdit = new Button(currComposite, SWT.PUSH);
        btnEdit.setText("Edit");
        btnEdit.setEnabled(false);
        currComposite.layout();
        parts.add(currComposite);
        // parent.layout(true);
    }

    /**
     * Adds new text
     */
    public void addNewText() {
        ReportText reportText = new ReportText("Type new text here");
        reportModel.getReport().addPart(reportText);
        addTextPart(reportText, true);
        isReportDataModified = true;
        forceRepaint();

    }

    /**
     * Adds text
     * 
     * @param part text to be added
     * @param isEditable true if text is editable, false otherwise
     */
    private void addTextPart(ReportText part, boolean isEditable) {
        final Composite currComposite = createContainer(part);
        final Text text = new Text(currComposite, SWT.MULTI);
        text.setEditable(isEditable);
        text.setText(part.getText());
        text.setBackground(new Color(text.getDisplay(), new RGB(255, 255, 255)));

        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = 600;
        text.setLayoutData(data);

        final Button btnEdit = new Button(currComposite, SWT.PUSH);
        btnEdit.setText(isEditable ? SAVE : EDIT);
        btnEdit.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Object data = currComposite.getData();
                boolean isEditable = text.getEditable();
                if (isEditable) {
                    btnEdit.setText(EDIT);
                    if (data instanceof ReportText) {
                        ReportText reportText = ((ReportText)data);
                        if (!reportText.getText().equals(text.getText())) {
                            isReportDataModified = true;
                            reportText.setText(text.getText());
                            forceRepaint();
                        }
                    }
                } else
                    btnEdit.setText(SAVE);
                text.setEditable(!isEditable);
            }

        });
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
     * Adds new image
     */
    public void addNewImage() {
        String imageFileName = openFileDialog();
        if (imageFileName != null) {
            ReportImage imagePart = new ReportImage(imageFileName);
            reportModel.getReport().addPart(imagePart);
            addImagePart(imagePart);
            isReportDataModified = true;
            forceRepaint();
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
        Button btnEdit = new Button(currComposite, SWT.PUSH);
        btnEdit.setText("Edit");
        btnEdit.setEnabled(false);
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
     * Actions needed for repainting
     */
    private void forceRepaint() {
        sc.setContent(frame);
        frame.pack();
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

}
