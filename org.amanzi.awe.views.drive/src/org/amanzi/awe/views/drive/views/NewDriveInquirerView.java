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
package org.amanzi.awe.views.drive.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import net.refractions.udig.ui.PlatformGIS;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;

/**
 * <p>
 * New implementation of drive Inquirer View
 * </p>
 * 
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class NewDriveInquirerView extends ViewPart implements IPropertyChangeListener {
	public NewDriveInquirerView() {
	
	}
	
    private static final Logger LOGGER = Logger.getLogger(NewDriveInquirerView.class);

    /* Data constants */
    public static final String ID = "org.amanzi.awe.views.drive.views.NewDriveInquirerView"; //$NON-NLS-1$
    private static final int MIN_FIELD_WIDTH = 50;
    private static final long SLIDER_STEP = 1000;// 1 sek
    private static final String LOG_LABEL = Messages.DriveInquirerView_2;
    private static final String PALETTE_LABEL = Messages.DriveInquirerView_3;
    protected static final String EVENT = Messages.DriveInquirerView_4;

    /* Data keepers */
    private LinkedHashMap<String, IDriveModel> mapOfDriveModels;
    private final TreeMap<String, List<String>> propertyLists = new TreeMap<String, List<String>>();
    private List<String> currentProperies = new ArrayList<String>(0);

    /* Gui elements */
    private Combo cDrive;
    private Combo cEvent;
    private Combo cPropertyList;
    private JFreeChart chart;
    private TableViewer table;
    private TableLabelProvider labelProvider;
    private TableContentProvider provider;
    private Slider slider;
    private Composite buttonLine;
    private Button bLeft;
    private Button bLeftHalf;
    private Button bRight;
    private Button bRightHalf;
    private Button bReport;
    private Label lLogarithmic;
    private Button bLogarithmic;
    private Label lPalette;
    private Combo cPalette;
    private Label lPropertyPalette;
    private Combo cPropertyPalette;
    private Spinner sLength;

    /* Simple work fields */
    private Long beginGisTime;
    private Long selectedTime;
    private DateTime dateStart;
    private Button bAddPropertyList;
    private boolean validDrive;

    @Override
    public void createPartControl(Composite parent) {
        Composite frame = new Composite(parent, SWT.FILL);
        FormLayout formLayout = new FormLayout();
        formLayout.marginHeight = 0;
        formLayout.marginWidth = 0;
        formLayout.spacing = 0;
        frame.setLayout(formLayout);

        Composite child = new Composite(frame, SWT.FILL);
        FormData fData = new FormData();
        fData.top = new FormAttachment(0, 2);
        fData.left = new FormAttachment(0, 2);
        fData.right = new FormAttachment(100, -2);

        child.setLayoutData(fData);
        final GridLayout layout = new GridLayout(13, false);
        child.setLayout(layout);
        Label label = new Label(child, SWT.FLAT);
        label.setText(Messages.DriveInquirerView_label_drive);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cDrive = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);

        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        cDrive.setLayoutData(layoutData);

        label = new Label(child, SWT.FLAT);
        label.setText(Messages.DriveInquirerView_label_event);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cEvent = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);

        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        cEvent.setLayoutData(layoutData);

        label = new Label(child, SWT.NONE);
        label.setText(Messages.DriveInquirerView_6);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cPropertyList = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        cPropertyList.setLayoutData(layoutData);

        bAddPropertyList = new Button(child, SWT.PUSH);
        bAddPropertyList.setText(Messages.DriveInquirerView_7);
        bAddPropertyList.setEnabled(false);

        chart = createChart();
//        chartFrame = new ChartCompositeImpl(frame, SWT.NONE, chart, true);
        fData = new FormData();
        fData.top = new FormAttachment(child, 2);
        fData.left = new FormAttachment(0, 2);
        fData.right = new FormAttachment(100, -2);
        fData.bottom = new FormAttachment(100, -130);

//        chartFrame.setLayoutData(fData);

        slider = new Slider(frame, SWT.NONE);
        slider.setValues(MIN_FIELD_WIDTH, 0, 300, 1, 1, 1);
        fData = new FormData();
        fData.left = new FormAttachment(0, 0);
        fData.right = new FormAttachment(100, 0);
//        fData.top = new FormAttachment(chartFrame, 2);
        slider.setLayoutData(fData);
        slider.pack();
        table = new TableViewer(frame, SWT.BORDER | SWT.FULL_SELECTION);
        fData = new FormData();
        fData.left = new FormAttachment(0, 0);
        fData.right = new FormAttachment(100, 0);
        fData.top = new FormAttachment(slider, 2);
        fData.bottom = new FormAttachment(100, -30);
        table.getControl().setLayoutData(fData);

        labelProvider = new TableLabelProvider();
        labelProvider.createTableColumn();
        provider = new TableContentProvider();
        table.setContentProvider(provider);

        buttonLine = new Composite(frame, SWT.NONE);
        fData = new FormData();
        fData.left = new FormAttachment(0, 2);
        fData.right = new FormAttachment(100, -2);
        fData.bottom = new FormAttachment(100, -2);
        buttonLine.setLayoutData(fData);
        formLayout = new FormLayout();
        buttonLine.setLayout(formLayout);

        bLeft = new Button(buttonLine, SWT.PUSH);
        bLeft.setText(Messages.DriveInquirerView_8);
        bLeftHalf = new Button(buttonLine, SWT.PUSH);
        bLeftHalf.setText(Messages.DriveInquirerView_9);

        bRight = new Button(buttonLine, SWT.PUSH);
        bRight.setText(Messages.DriveInquirerView_10);
        bRightHalf = new Button(buttonLine, SWT.PUSH);
        bRightHalf.setText(Messages.DriveInquirerView_11);

        bReport = new Button(buttonLine, SWT.PUSH);
        bReport.setText(Messages.DriveInquirerView_12);

        FormData formData = new FormData();
        formData.left = new FormAttachment(0, 5);
        bLeft.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(bLeft, 5);
        bLeftHalf.setLayoutData(formData);

        formData = new FormData();
        formData.right = new FormAttachment(100, -5);
        bRight.setLayoutData(formData);

        formData = new FormData();
        formData.right = new FormAttachment(bRight, -5);
        bRightHalf.setLayoutData(formData);

        lLogarithmic = new Label(buttonLine, SWT.NONE);
        lLogarithmic.setText(LOG_LABEL);
        bLogarithmic = new Button(buttonLine, SWT.CHECK);
        bLogarithmic.setSelection(false);

        lPalette = new Label(buttonLine, SWT.NONE);
        lPalette.setText(PALETTE_LABEL);
        cPalette = new Combo(buttonLine, SWT.DROP_DOWN | SWT.READ_ONLY);
        String[] paletteNames = PlatformGIS.getColorBrewer().getPaletteNames();
        Arrays.sort(paletteNames);
        cPalette.setItems(paletteNames);
        cPalette.select(0);

        FormData dCombo = new FormData();
        dCombo.left = new FormAttachment(bLeftHalf, 10);
        dCombo.top = new FormAttachment(bLeftHalf, 0, SWT.CENTER);
        bLogarithmic.setLayoutData(dCombo);

        FormData dLabel = new FormData();
        dLabel.left = new FormAttachment(bLogarithmic, 2);
        dLabel.top = new FormAttachment(bLogarithmic, 5, SWT.CENTER);
        lLogarithmic.setLayoutData(dLabel);

        dCombo = new FormData();
        dCombo.left = new FormAttachment(lLogarithmic, 10);
        dCombo.top = new FormAttachment(cPalette, 5, SWT.CENTER);
        lPalette.setLayoutData(dCombo);

        dCombo = new FormData();
        dCombo.left = new FormAttachment(lPalette, 2);
        cPalette.setLayoutData(dCombo);

        FormData dReport = new FormData();
        dReport.left = new FormAttachment(cPalette, 2);
        bReport.setLayoutData(dReport);

        label = new Label(child, SWT.FLAT);
        label.setText(Messages.DriveInquirerView_label_start_time);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        dateStart = new DateTime(child, SWT.FILL | SWT.BORDER | SWT.TIME | SWT.LONG);
        GridData dateStartlayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dateStartlayoutData.minimumWidth = 75;
        dateStart.setLayoutData(dateStartlayoutData);

        label = new Label(child, SWT.FLAT);
        label.setText(Messages.DriveInquirerView_label_length);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        sLength = new Spinner(child, SWT.BORDER);
        sLength.setMinimum(1);
        sLength.setMaximum(1000);
        sLength.setSelection(5);
        GridData timeLenlayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        timeLenlayoutData.minimumWidth = 45;
        sLength.setLayoutData(timeLenlayoutData);

        lPropertyPalette = new Label(child, SWT.NONE);
        lPropertyPalette.setText(PALETTE_LABEL);
        cPropertyPalette = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);
        cPropertyPalette.setItems(PlatformGIS.getColorBrewer().getPaletteNames());
        cPropertyPalette.select(0);

        setsVisible(false);

        init();
    }

    /**
     * Creates the Chart based on a dataset
     */
    private JFreeChart createChart() {
    	return null;
    }

    /**
     * Init start data
     */
    private void init() {
        addListeners();
        cDrive.setItems(getDriveItems());

        formPropertyList();

        cPropertyList.setItems(propertyLists.keySet().toArray(new String[0]));

        // initializeIndex(cDrive.getText());

        initEvents();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Init events
     */
    private void initEvents() {

    }

    /**
     *Preparing existing property lists for display
     */
    private void formPropertyList() {
        propertyLists.clear();
        IDriveModel currentDriveModel = getDriveModel();
        INodeType primaryTypeOfModel = null;
        
        ArrayList<String> list = new ArrayList<String>();
        if (currentDriveModel != null) {
            primaryTypeOfModel = currentDriveModel.getPrimaryType();
        	String[] currentStatistics = currentDriveModel.getAllProperties(primaryTypeOfModel, Double.class);
        	for (String property : currentStatistics) {
        		list.add(property);
        	}
        	currentStatistics = currentDriveModel.getAllProperties(primaryTypeOfModel, Integer.class);
        	for (String property : currentStatistics) {
        		list.add(property);
        	}
        	currentStatistics = currentDriveModel.getAllProperties(primaryTypeOfModel, Float.class);
        	for (String property : currentStatistics) {
        		list.add(property);
        	}
        	String[] statistics = new String[list.size()];
        	list.toArray(statistics);
            Arrays.sort(statistics);
            cPropertyList.setItems(statistics);
        }
//        if (savedProperties != null) {
//            List<String> savedList = new ArrayList<String>(savedProperties.length);
//            for (Object savedProperty : savedProperties) {
//                savedList.add(savedProperty.toString());
//            }
//            List<String> filteredList = new PropertyFilterModel().filerProperties(cDrive.getText(), savedList);
//            for (Object savedProperty : filteredList) {
//                propertyLists.put(savedProperty.toString(), Arrays.asList(savedProperty.toString().split(", ")));
//            }
//        }
    }

    /**
     * Displays error message instead of throwing an exception
     * 
     * @param e exception thrown
     */
    private void displayErrorMessage(final String e) {
        final Display display = PlatformUI.getWorkbench().getDisplay();
        display.asyncExec(new Runnable() {

            @Override
            public void run() {
                MessageDialog.openError(display.getActiveShell(), Messages.DriveInquirerView_18, e);
            }

        });
    }

    /**
     * get Drive list
     * 
     * @return String[]
     */
    private String[] getDriveItems() {
    	Iterable<IDriveModel> driveModels = null;
		try {
			driveModels = ProjectModel.getCurrentProjectModel().findAllDriveModels();
		} catch (AWEException e) {
		}
        mapOfDriveModels = new LinkedHashMap<String, IDriveModel>();
        for (IDriveModel driveModel : driveModels) {
        	mapOfDriveModels.put(driveModel.getName(), driveModel);
        }
        String[] result = mapOfDriveModels.keySet().toArray(new String[] {});
        Arrays.sort(result);
        return result;
    }

    /**
     *add listeners
     */
    private void addListeners() {
        cDrive.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                changeDrive();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetDefaultSelected(e);
            }
        });
        cEvent.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
//                updateEvent();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cPropertyList.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updatePropertyList();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        dateStart.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
//                changeDate();
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
        });
        dateStart.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == '\r' || e.keyCode == SWT.KEYPAD_CR) {
//                    changeDate();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
        sLength.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
//                changeTimeLenght();
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
        });
        sLength.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == '\r' || e.keyCode == SWT.KEYPAD_CR) {
//                    changeTimeLenght();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });

        bRight.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
//                right();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        bLeft.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
//                left();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        bLeftHalf.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
//                leftHalf();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cPalette.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
//                fireEventUpdateChart();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cPropertyPalette.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
//                updatePropertyPalette();

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        slider.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
//                changeSlider();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
//        chart.addProgressListener(new ChartProgressListener() {
//
//            @Override
//            public void chartProgress(ChartProgressEvent chartprogressevent) {
//                if (chartprogressevent.getType() != 2) {
//                    return;
//                }
//                long domainCrosshairValue = (long)chart.getXYPlot().getDomainCrosshairValue();
//                if (domainCrosshairValue != selectedTime) {
//                    selectedTime = domainCrosshairValue;
//                    slider.setSelection((int)((selectedTime - beginGisTime) / SLIDER_STEP));
//                }
//                labelProvider.refreshTable();
//                table.setInput(0);
//                table.refresh();
//            }
//        });
        bReport.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
//                generateReport();
            }

        });
        bAddPropertyList.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
//                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
//                NewDriveInquirerPropertyConfig pdialog = new NewDriveInquirerPropertyConfig(shell, getDriveModel().getRootNode());;
//                if (pdialog.open() == SWT.OK) {
//                    formPropertyList();
//                    String[] result = propertyLists.keySet().toArray(new String[0]);
//                    Arrays.sort(result);
//                    cPropertyList.setItems(result);
//                    updatePropertyList();
//                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }



    /**
     * Update data after property list changed
     */
    protected void updatePropertyList() {
        currentProperies = propertyLists.get(cPropertyList.getText());
        if (currentProperies == null) {
            currentProperies = new ArrayList<String>(0);
        }
        updateDatasets();
//        updateChart();
    }

    /**
     * Update datasets
     */
    protected void updateDatasets() {

    }
    
    /**
     *change drive dataset
     */
    private void changeDrive() {
        if (cDrive.getSelectionIndex() < 0) {
            setsVisible(false);
            bAddPropertyList.setEnabled(false);
        } else {
            formPropertyLists();
            bAddPropertyList.setEnabled(true);
        }
    }

    /**
     *forms all property depends of gis
     */
    private void formPropertyLists() {
        formPropertyList();
    }

    /**
     * set chart visible
     * 
     * @param visible - is visible?
     */
    private void setsVisible(boolean visible) {
        table.getControl().setVisible(visible);
        buttonLine.setVisible(visible);
        slider.setVisible(visible);
    }

    /**
     * @return
     */
    private String getPropertyYAxisName(int propNum) {
        return ""; //$NON-NLS-1$
    }

    /**
     * get gis node
     * 
     * @return node
     */
    private IDriveModel getDriveModel() {
        return mapOfDriveModels == null ? null : mapOfDriveModels.get(cDrive.getText());
    }

    private int getCurrentPropertyCount() {
        return currentProperies.size();
    }

    /**
     * @param time
     * @return
     */
    public Long getPreviousTime(Long time) {
        XYPlot xyplot = (XYPlot)chart.getPlot();
        ValueAxis valueaxis = xyplot.getDomainAxis();
        Range range = valueaxis.getRange();

        return time == null ? null : (long)Math.max(time - 1000, range.getLowerBound());
    }

    /**
     * @param time
     * @return
     */
    public Long getNextTime(Long time) {
        XYPlot xyplot = (XYPlot)chart.getPlot();
        ValueAxis valueaxis = xyplot.getDomainAxis();
        Range range = valueaxis.getRange();

        return time == null ? null : (long)Math.min(time + 1000, range.getUpperBound());
    }

    @Override
    public void setFocus() {
    }

    
    private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

        private final ArrayList<TableColumn> columns = new ArrayList<TableColumn>();
        /** int DEF_SIZE field */
        protected static final int DEF_SIZE = 150;

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
			return null;
        }

        public void refreshTable() {
            Table tabl = table.getTable();
            TableViewerColumn column;
            TableColumn col;
            if (columns.isEmpty()) {
                column = new TableViewerColumn(table, SWT.LEFT);
                col = column.getColumn();
                col.setText(Messages.DriveInquirerView_99);
                columns.add(col);
                col.setWidth(DEF_SIZE);
                col.setResizable(true);

                column = new TableViewerColumn(table, SWT.LEFT);
                col = column.getColumn();
                col.setText(Messages.DriveInquirerView_100);
                columns.add(col);
                col.setWidth(DEF_SIZE);
                col.setResizable(true);
            }
            int i = 0;
            for (; i < getCurrentPropertyCount() && i < columns.size() - 2; i++) {
                col = columns.get(i + 2);
                col.setText(currentProperies.get(i));
                col.setWidth(DEF_SIZE);
                col.setResizable(true);
            }
            if (getCurrentPropertyCount() > columns.size() - 2) {
                for (; i < getCurrentPropertyCount(); i++) {
                    column = new TableViewerColumn(table, SWT.LEFT);
                    col = column.getColumn();
                    col.setText(currentProperies.get(i));
                    columns.add(col);
                    col.setWidth(DEF_SIZE);
                    col.setResizable(true);
                }
            } else if (getCurrentPropertyCount() < columns.size() - 2) {
                i += 2;
                for (; i < columns.size(); i++) {
                    col = columns.get(i);
                    col.setWidth(0);
                    col.setResizable(false);
                }
            }

            tabl.setHeaderVisible(true);
            tabl.setLinesVisible(true);
            table.setLabelProvider(this);
            table.refresh();
        }

        /**
         *create column table
         */
        public void createTableColumn() {
            Table tabl = table.getTable();
            TableViewerColumn column;
            TableColumn col;
            if (columns.isEmpty()) {
                column = new TableViewerColumn(table, SWT.LEFT);
                col = column.getColumn();
                col.setText(Messages.DriveInquirerView_101);
                columns.add(col);
                col.setWidth(DEF_SIZE);
                col.setResizable(true);

                column = new TableViewerColumn(table, SWT.LEFT);
                col = column.getColumn();
                col.setText(Messages.DriveInquirerView_102);
                columns.add(col);
                col.setWidth(DEF_SIZE);
                col.setResizable(true);
            }
            int i;
            for (i = 0; i < getCurrentPropertyCount() && i < columns.size() - 2; i++) {
                col = columns.get(i + 2);
                col.setText(Messages.DriveInquirerView_label_property + (i + 1));
                col.setWidth(DEF_SIZE);
                col.setResizable(true);
            }
            if (getCurrentPropertyCount() > columns.size() - 2) {
                for (; i < getCurrentPropertyCount(); i++) {
                    column = new TableViewerColumn(table, SWT.LEFT);
                    col = column.getColumn();
                    col.setText(Messages.DriveInquirerView_label_property + (i + 1));
                    columns.add(col);
                    col.setWidth(DEF_SIZE);
                    col.setResizable(true);
                }
            } else if (getCurrentPropertyCount() < columns.size() - 2) {
                for (; i < getCurrentPropertyCount(); i++) {
                    col = columns.get(i + 2);
                    columns.add(col);
                    col.setWidth(0);
                    col.setResizable(false);
                }
            }

            tabl.setHeaderVisible(true);
            tabl.setLinesVisible(true);
            table.setLabelProvider(this);
            table.refresh();
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
			return null;
        }
    }

    /*
     * The content provider class is responsible for providing objects to the view. It can wrap
     * existing objects in adapters or simply return objects as-is. These objects may be sensitive
     * to the current input of the view, or ignore it and always show the same content (like Task
     * List, for example).
     */

    private class TableContentProvider implements IStructuredContentProvider {

        public TableContentProvider() {
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return new Integer[] {0, 1, 2};
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        // if (propertyListsConstantValue !=
        // getPreferenceStore().getString(DataLoadPreferences.PROPERY_LISTS)) {
        formPropertyList();
        String[] result = propertyLists.keySet().toArray(new String[0]);
        Arrays.sort(result);
        cPropertyList.setItems(result);
        updatePropertyList();
        // }
    }


    /**
     * Contains all flags for that must be valid before update chart
     * 
     * @return is all valid
     */
    private boolean chartDataValid() {
        return validDrive;
    }

}
