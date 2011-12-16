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

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import net.refractions.udig.ui.PlatformGIS;

import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.neo.services.utils.Pair;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.geotools.brewer.color.BrewerPalette;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * <p>
 * Drive Inquirer View
 * </p>
 * 
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class DriveInquirerView extends ViewPart implements IPropertyChangeListener {
	public DriveInquirerView() {
	
	} 
	
//    private static final Logger LOGGER = Logger.getLogger(NewDriveInquirerView.class);

    /* Data constants */
    public static final String ID = "org.amanzi.awe.views.drive.views.NewDriveInquirerView"; //$NON-NLS-1$
    private static final int MIN_FIELD_WIDTH = 50;
    private static final long SLIDER_STEP = 1000;// 1 sek
    private static final String CHART_TITLE = "";
    private static final String LOG_LABEL = Messages.DriveInquirerView_2;
    private static final String PALETTE_LABEL = Messages.DriveInquirerView_3;
    protected static final String EVENT = Messages.DriveInquirerView_4;

    /* Data keepers */
    private LinkedHashMap<String, IDriveModel> mapOfDriveModels;
    private final TreeMap<String, List<String>> propertyLists = new TreeMap<String, List<String>>();
    private List<String> currentProperies = new ArrayList<String>(0);
    private DateAxis domainAxis;
    private List<LogarithmicAxis> axisLogs;
    private List<ValueAxis> axisNumerics;
    private List<TimeDataset> xydatasets;
    // HashMap<id of dataElement, IDataElement>
    private HashMap<Integer, IDataElement> dataElementsToChart;
    
    /* Gui elements */
    private Combo cDrive;
    private Combo cPropertyList;
    private JFreeChart chart;
    private ValueAxis valueAxis;
    private ChartCompositeImpl chartFrame;
    private EventDataset eventDataset;
    private TableViewer table;
    private TableLabelProvider labelProvider;
    private TableContentProvider provider;
    private Slider slider;
    private Composite buttonLine;
    private Label lLogarithmic;
    private Button bLogarithmic;
    private Label lPropertyPalette;
    private Combo cPropertyPalette;
    private Spinner sLength;

    /* Simple work fields */
    private Long beginGisTime;
    private Long endGisTime;
    private Long selectedTime;
    private DateTime dateStart;
    private Long dateStartTimestamp;
    private Long oldStartTime;
    private Integer oldTimeLength;
    private Button bAddPropertyList;
    private boolean validDrive;
    private boolean isLogarithmic;

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
        final GridLayout layout = new GridLayout(11, false);
        child.setLayout(layout);
        Label label = new Label(child, SWT.FLAT);
        label.setText(Messages.DriveInquirerView_label_drive);
        cDrive = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);
        cDrive.setTouchEnabled(true);

        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        cDrive.setLayoutData(layoutData);

        label = new Label(child, SWT.NONE);
        label.setText(Messages.DriveInquirerView_6);
        cPropertyList = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        cPropertyList.setLayoutData(layoutData);

        bAddPropertyList = new Button(child, SWT.PUSH);
        bAddPropertyList.setText(Messages.DriveInquirerView_7);

        chart = createChart();
        chartFrame = new ChartCompositeImpl(frame, SWT.NONE, chart, true);
        fData = new FormData();
        fData.top = new FormAttachment(child, 2);
        fData.left = new FormAttachment(0, 2);
        fData.right = new FormAttachment(100, -2);
        fData.bottom = new FormAttachment(100, -130);

        chartFrame.setLayoutData(fData);

        slider = new Slider(frame, SWT.NONE);
        slider.setValues(MIN_FIELD_WIDTH, 0, 300, 1, 1, 1);
        fData = new FormData();
        fData.left = new FormAttachment(0, 0);
        fData.right = new FormAttachment(100, 0);
        fData.top = new FormAttachment(chartFrame, 2);
        slider.setLayoutData(fData);
        slider.pack();
        table = new TableViewer(frame, SWT.BORDER | SWT.FULL_SELECTION);
        Table table_1 = table.getTable();
        table_1.setLayoutData(new FormData());
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

        lLogarithmic = new Label(buttonLine, SWT.NONE);
        lLogarithmic.setText(LOG_LABEL);
        bLogarithmic = new Button(buttonLine, SWT.CHECK);
        bLogarithmic.setSelection(false);
        isLogarithmic = false;
        
        FormData dCombo = new FormData();
        bLogarithmic.setLayoutData(dCombo);

        FormData dLabel = new FormData();
        dLabel.left = new FormAttachment(bLogarithmic, 2);
        dLabel.top = new FormAttachment(bLogarithmic, 5, SWT.CENTER);
        lLogarithmic.setLayoutData(dLabel);

        dCombo = new FormData();
        dCombo.left = new FormAttachment(lLogarithmic, 10);

        label = new Label(child, SWT.FLAT);
        label.setText(Messages.DriveInquirerView_label_start_time);
        dateStart = new DateTime(child, SWT.FILL | SWT.BORDER | SWT.TIME | SWT.LONG);
        GridData dateStartlayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dateStartlayoutData.minimumWidth = 75;
        dateStart.setLayoutData(dateStartlayoutData);

        label = new Label(child, SWT.FLAT);
        label.setText(Messages.DriveInquirerView_label_length);
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
     *add listeners
     */
    private void addListeners() {
    	cDrive.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {}
			
			@Override
			public void mouseDown(MouseEvent e) {
			    cDrive.setItems(getDriveItems());
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {}
		});
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
                changeDate();
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
        });
        dateStart.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == '\r' || e.keyCode == SWT.KEYPAD_CR) {
                    changeDate();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
        sLength.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                changeTimeLenght();
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
        });
        sLength.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == '\r' || e.keyCode == SWT.KEYPAD_CR) {
                    changeTimeLenght();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
        cPropertyPalette.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updatePropertyPalette();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        slider.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                changeSlider();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        chart.addProgressListener(new ChartProgressListener() {

            @Override
            public void chartProgress(ChartProgressEvent chartprogressevent) {
                if (chartprogressevent.getType() != 2) {
                    return;
                }
                long domainCrosshairValue = (long)chart.getXYPlot().getDomainCrosshairValue();
                if (domainCrosshairValue != selectedTime) {
                    selectedTime = domainCrosshairValue;
                    slider.setSelection((int)((selectedTime - beginGisTime) / SLIDER_STEP));
                }
                labelProvider.refreshTable();
                table.setInput(0);
                table.refresh();
            }
        });
        bAddPropertyList.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                DriveInquirerPropertyConfig pdialog = new DriveInquirerPropertyConfig(shell, getDriveModel());
                if (pdialog.open() == SWT.OK) {
                    formPropertyList();
                    changeDrive();
//                    String[] result = propertyLists.keySet().toArray(new String[0]);
//                    Arrays.sort(result);
//                    cPropertyList.setItems(result);
//                    updatePropertyList();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        bLogarithmic.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                changeValueAxis();
            }

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
        });
    }
    
    /**
     * Updates colors on chart after palette changed
     */
    protected void updatePropertyPalette() {
        XYPlot xyplot = chart.getXYPlot();
        for (int i = 1; i <= getCurrentPropertyCount(); i++) {
            ValueAxis axisNumeric = xyplot.getRangeAxis(i);
            LogarithmicAxis axisLog = new LogarithmicAxis(axisNumeric.getLabel());

            Color color = getColorForProperty(i - 1);
            axisLog.setTickLabelPaint(color);
            axisLog.setLabelPaint(color);
            axisNumeric.setTickLabelPaint(color);
            axisNumeric.setLabelPaint(color);

            xyplot.getRenderer(i).setSeriesPaint(0, color);

        }
    }
    
    
    /**
     *change slider position
     */
    protected void changeSlider() {
        chartFrame.dropAnchor();
        int i = slider.getSelection();
        XYPlot xyplot = (XYPlot)chart.getPlot();
        Double d = beginGisTime + (i / (double)(slider.getMaximum() - slider.getMinimum())) * (endGisTime - beginGisTime);
        selectedTime = d.longValue();
        xyplot.setDomainCrosshairValue(selectedTime.doubleValue());
        Long beginTime = getBeginTime();
        int timeWindowLen = getLength();
        Long endTime = beginTime + timeWindowLen;
        if (selectedTime < beginTime || selectedTime > endTime) {
            Long windowStartTime = selectedTime < beginTime ? Math.max(beginGisTime, beginTime - timeWindowLen) : Math.min(endGisTime, beginTime + timeWindowLen);
            if (selectedTime < windowStartTime || selectedTime > windowStartTime + timeWindowLen) {
                windowStartTime = selectedTime;
            }
            setBeginTime(windowStartTime);
            updateChart();
        }
        chart.fireChartChanged();
    }
    
    /**
     * get length from spin
     * 
     * @return length (milliseconds)
     */
    private int getLength() {
        return sLength.getSelection() * 60 * 1000;
    }
    
    /**
     * Change time length
     */
    protected void changeTimeLenght() {
        if (!isTimeLengthChanged()) {
            return;
        }
        updateChart();
        oldTimeLength = sLength.getSelection();
    }

    /**
     * @return isTimeLengthChanged
     */
    private boolean isTimeLengthChanged() {
        return oldTimeLength == null || sLength.getSelection() != oldTimeLength;
    }
    
    /**
     *change drive
     */
    protected void changeDate() {
        if (!isStartDateChanged()) {
            return;
        }
        setTimeFromField();
        IDriveModel driveModel = getDriveModel();

        if (driveModel == null) {
            return;
        }
        updateChart();
        oldStartTime = getBeginTime();
    }

    /**
     *Check changing start date
     * 
     * @return true if start date was changed
     */
    private boolean isStartDateChanged() {
        return oldStartTime == null || !getBeginTime().equals(oldStartTime);
    }
    
    /**
     * Sets time from datetime field
     */
    private void setTimeFromField() {
        GregorianCalendar cl = new GregorianCalendar();
        if (dateStartTimestamp != null) {
            cl.setTimeInMillis(dateStartTimestamp);
        }
        cl.set(Calendar.HOUR_OF_DAY, dateStart.getHours());
        cl.set(Calendar.MINUTE, dateStart.getMinutes());
        cl.set(Calendar.SECOND, dateStart.getSeconds());
        dateStartTimestamp = cl.getTimeInMillis();
    }
    
    /**
     * Creates the Chart based on a dataset
     */
    private JFreeChart createChart() {
        XYBarRenderer xyarearenderer = new EventRenderer();
        eventDataset = new EventDataset();
        
        valueAxis = new NumberAxis(Messages.DriveInquirerView_13);
        valueAxis.setVisible(false);
        domainAxis = new DateAxis(Messages.DriveInquirerView_14);
        XYPlot xyplot = new XYPlot(eventDataset, domainAxis, valueAxis, xyarearenderer);
        
        xydatasets = new ArrayList<TimeDataset>();

        xyplot.setDomainCrosshairVisible(true);
        xyplot.setDomainCrosshairLockedOnData(false);
        xyplot.setRangeCrosshairVisible(false);

        JFreeChart jfreechart = new JFreeChart(CHART_TITLE, JFreeChart.DEFAULT_TITLE_FONT, xyplot, true);

        ChartUtilities.applyCurrentTheme(jfreechart);
        jfreechart.getTitle().setVisible(false);

        axisNumerics = new ArrayList<ValueAxis>(0);
        axisLogs = new ArrayList<LogarithmicAxis>(0);
        xyplot.getRenderer(0).setSeriesPaint(0, new Color(0, 0, 0, 0));

        return jfreechart;
    }
    
    private void changeValueAxis() {
        isLogarithmic = (isLogarithmic == false) ? true : false;
        updateDatasets();
        updateChart();
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
        Pair<Long, Long> minMax = new Pair<Long, Long>(new Long(100000000), new Long(300000000));
        beginGisTime = minMax.getLeft();
        endGisTime = minMax.getRight();
        if (beginGisTime == null || endGisTime == null) {
            displayErrorMessage(Messages.DriveInquirerView_15);
            validDrive = false;
            return;
        }
        selectedTime = beginGisTime;
        slider.setMaximum((int)((endGisTime - beginGisTime) / SLIDER_STEP));
        slider.setSelection(0);
        selectedTime = beginGisTime;
        setBeginTime(beginGisTime);
        chart.getXYPlot().setDomainCrosshairValue(selectedTime);
    }
    
    /**
     * Returns the color from selected palette for property by index
     * 
     * @param propNum index
     * @return Color
     */
    private Color getColorForProperty(int propNum) {
        BrewerPalette palette = PlatformGIS.getColorBrewer().getPalette(cPropertyPalette.getText());
        Color[] colors = palette.getColors(palette.getMaxColors());
        int index = ((colors.length - 1) * propNum) / Math.max(1, getCurrentPropertyCount() - 1);
        Color color = colors[index];
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
    }

    /**
     *Preparing existing property lists for display
     */
    private void formPropertyList() {
        propertyLists.clear();
        IDriveModel currentDriveModel = getDriveModel();
        
        String[] statistics = null;
        if (currentDriveModel != null) {
	        Set<String> selectedProperties = currentDriveModel.getSelectedProperties();
	        
	        statistics = new String[selectedProperties.size()];
	    	selectedProperties.toArray(statistics);
	        Arrays.sort(statistics);
	        cPropertyList.setItems(statistics);
        }
        
        if (statistics != null) {
	    	for (String savedProperty : statistics) {
	            propertyLists.put(savedProperty, Arrays.asList(savedProperty.split(", ")));
	        }
        }
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
     * Update data after property list changed
     */
    protected void updatePropertyList() {
        currentProperies = propertyLists.get(cPropertyList.getText());
        if (currentProperies == null) {
            currentProperies = new ArrayList<String>(0);
        }
        updateDatasets();
        updateChart();
    }

    /**
     * Update datasets
     */
    protected void updateDatasets() {
        XYPlot xyplot = chart.getXYPlot();
        for (int i = 1; i <= xydatasets.size(); i++) {
            xyplot.setDataset(i, null);
            xyplot.setRenderer(i, null);
            xyplot.setRangeAxis(i, null);
            xyplot.setRangeAxisLocation(i, null);
        }
        xydatasets.clear();
        
        for (int i = 1; i <= getCurrentPropertyCount(); i++) {
            TimeDataset xydataset = new TimeDataset();
            StandardXYItemRenderer standardxyitemrenderer = new StandardXYItemRenderer();
            standardxyitemrenderer.setBaseShapesFilled(true);
            xyplot.setDataset(i, xydataset);
            xyplot.setRenderer(i, standardxyitemrenderer);
            
            NumberAxis valueAxis = null;
            if (isLogarithmic) {
            	valueAxis = new LogarithmicAxis(getPropertyYAxisName(i));
            }
            else {
            	valueAxis = new NumberAxis(getPropertyYAxisName(i));
            }
            if (isLogarithmic) {
            	((LogarithmicAxis)valueAxis).setAllowNegativesFlag(true);
            	((LogarithmicAxis)valueAxis).setAutoRange(true);
            }
            else {
            	valueAxis.setAutoRangeIncludesZero(true);
            }
            xyplot.setRangeAxis(i, valueAxis);
            xyplot.setRangeAxisLocation(i, AxisLocation.BOTTOM_OR_LEFT);
            xyplot.mapDatasetToRangeAxis(i, i);
            xydatasets.add(xydataset);

            ValueAxis axisNumeric = xyplot.getRangeAxis(i);
            LogarithmicAxis axisLog = new LogarithmicAxis(axisNumeric.getLabel());
            axisLog.setAllowNegativesFlag(true);
            axisLog.setAutoRange(true);

            Color color = getColorForProperty(i - 1);
            axisLog.setTickLabelPaint(color);
            axisLog.setLabelPaint(color);
            axisNumeric.setTickLabelPaint(color);
            axisNumeric.setLabelPaint(color);

            axisNumerics.add(axisNumeric);
            axisLogs.add(axisLog);
            xyplot.getRenderer(i).setSeriesPaint(0, color);
        }
    }
    
    /**
     *update chart
     */
    private void updateChart() {
        if (cDrive.getText().isEmpty() || cPropertyList.getText().isEmpty() || !chartDataValid()) {
            setsVisible(false);
            return;
        }
        if (getCurrentPropertyCount() < 1) {
            setsVisible(false);
        }
        chart.getTitle().setVisible(false);

        Integer length = sLength.getSelection();
        long lengthOfTime = (long) (length * 1000 * 60);
        Long time = getBeginTime();
        Date date = new Date(time);
        domainAxis.setMinimumDate(date);
        domainAxis.setMaximumDate(new Date(time + lengthOfTime));
        for (int i = 0; i < getCurrentPropertyCount(); i++) {
            TimeDataset xydataset = xydatasets.get(i);
            String property = currentProperies.get(i);
            xydataset.updateDataset(property, time, length, property);
        }
//        eventDataset.updateDataset(cEvent.getText(), time, length, cEvent.getText());
        setsVisible(true);
        
        chart.fireChartChanged();
//        fireEventUpdateChart();
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
        
        IDriveModel currentDriveModel = getDriveModel();
        Long minTimestamp = currentDriveModel.getMinTimestamp();
        Long maxTimestamp = currentDriveModel.getMaxTimestamp();
        
        beginGisTime = minTimestamp;
        endGisTime = maxTimestamp;

        if (beginGisTime == null || endGisTime == null) {
            displayErrorMessage(Messages.DriveInquirerView_97);
            validDrive = false;
            setsVisible(false);
            return;
        }
        validDrive = true;

        selectedTime = beginGisTime;
        slider.setMaximum((int)((endGisTime - beginGisTime) / SLIDER_STEP));
        slider.setSelection(0);
        selectedTime = beginGisTime;
        setBeginTime(beginGisTime);
        chart.getXYPlot().setDomainCrosshairValue(selectedTime);

        updateChart();
    }

    /**
     * set chart visible
     * 
     * @param visible - is visible?
     */
    private void setsVisible(boolean visible) {
    	chartFrame.setVisible(visible);
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
     * Sets begin time
     * 
     * @param time - time
     */
    @SuppressWarnings("deprecation")
    private void setBeginTime(Long time) {
        dateStartTimestamp = time;
        Date date = new Date(time);
        dateStart.setHours(date.getHours());
        dateStart.setMinutes(date.getMinutes());
        dateStart.setSeconds(date.getSeconds());
    }

    /**
     * get begin time
     * 
     * @return Long
     */

    @SuppressWarnings("deprecation")
    private Long getBeginTime() {
        if (dateStartTimestamp == null) {
            return null;
        }
        Date date = new Date(dateStartTimestamp);
        date.setHours(dateStart.getHours());
        date.setMinutes(dateStart.getMinutes());
        date.setSeconds(dateStart.getSeconds());
        return date.getTime();
    }
    
    /**
     * Gets index of crosshair data item
     * 
     * @param xydataset
     * @param crosshair
     * @return index or null
     */
    private Integer getCrosshairIndex(TimeDataset dataset, Number crosshair) {
        return getCrosshairIndex(dataset.collection, crosshair);
    }

    /**
     * Returns Crosshair Index
     * 
     * @param collection Time Series Collection
     * @param crosshair Number
     * @return Integer
     */
    private Integer getCrosshairIndex(TimeSeriesCollection collection, Number crosshair) {
        if (crosshair == null) {
            return null;
        }
        int[] item = collection.getSurroundingItems(0, crosshair.longValue());
        Integer result = null;
        if (item[0] >= 0) {
            result = item[0];
        }
        return result;
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
    
    /**
     * <p>
     * Event renderer
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private class EventRenderer extends XYBarRenderer {

        /** long serialVersionUID field */
        private static final long serialVersionUID = 1L;

        public EventRenderer() {
            super();
        }

        @Override
        public Shape getItemShape(int row, int column) {
            return super.getItemShape(row, column);
        }

        @Override
        public Paint getItemFillPaint(int row, int column) {
            return super.getItemFillPaint(row, column);
        }

        @Override
        public Paint getItemPaint(int row, int column) {
//            TimeSeriesDataItem item = eventDataset.series.getDataItem(column);
//            Node node = NeoServiceProviderUi.getProvider().getService().getNodeById(item.getValue().longValue());
//            Color color = getEventColor(node);
//            return color;
        	Color color = Color.RED;
        	return color;
        }

    }
    
    /**
     * <p>
     * Dataset for event
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    @SuppressWarnings("serial")
	private class EventDataset extends AbstractIntervalXYDataset {

        /**
         * constructor
         */
        public EventDataset() {
            super();
        }

		@Override
		public Number getEndX(int arg0, int arg1) {
			return null;
		}

		@Override
		public Number getEndY(int arg0, int arg1) {
			return null;
		}

		@Override
		public Number getStartX(int arg0, int arg1) {
			return null;
		}

		@Override
		public Number getStartY(int arg0, int arg1) {
			return null;
		}

		@Override
		public int getItemCount(int arg0) {
			return 0;
		}

		@Override
		public Number getX(int arg0, int arg1) {
			return null;
		}

		@Override
		public Number getY(int arg0, int arg1) {
			return null;
		}

		@Override
		public int getSeriesCount() {
			return 0;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Comparable getSeriesKey(int arg0) {
			return null;
		}
    }

    /**
     * <p>
     * temporary class for avoid bug: if anchor is set - the crosshair do not change by slider
     * changing remove if more correctly way will be found
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    public static class ChartCompositeImpl extends ChartComposite {

        public ChartCompositeImpl(Composite frame, int none, JFreeChart chart, boolean b) {
            super(frame, none, chart, b);
        }

        /**
         * drop anchor;
         */
        public void dropAnchor() {
            setAnchor(null);
        }
    }

    /**
     * <p>
     * Time dataset Now it simple wrapper of TimeSeriesCollection But if cache is not possible need
     * be refactored for use database access
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private class TimeDataset extends AbstractXYDataset implements CategoryDataset {

        /** long serialVersionUID field */
        private static final long serialVersionUID = 1L;

        private Long beginTime;
        private Long length;
        private TimeSeries series;
        private TimeSeriesCollection collection;
        private String propertyName;

        /**
         * update dataset with new data
         * 
         * @param name - dataset name
         * @param root - root node
         * @param beginTime - begin time
         * @param length - length
         * @param propertyName - property name
         * @param event - event value
         */
        public void updateDataset(String name, Long beginTime, int length, String propertyName) {
            this.beginTime = beginTime;
            this.length = (long)length * 1000 * 60;
            this.propertyName = propertyName;
            collection = new TimeSeriesCollection();
            createSeries(name, propertyName);
            collection.addSeries(series);
            this.fireDatasetChanged();
        }

        /**
         * constructor
         */
        public TimeDataset() {
            super();
            beginTime = null;
            length = null;
            series = null;
            collection = new TimeSeriesCollection();
            propertyName = null;
        }

        /**
         * Create time series
         * 
         * @param name name of series
         * @param propertyName property name
         */
        protected void createSeries(String name, String propertyName) {
        	dataElementsToChart = new HashMap<Integer, IDataElement>();
            series = new TimeSeries(name);
            
            IDriveModel driveModel = getDriveModel();
            Iterable<IDataElement> elements = 
            		driveModel.findAllElementsByTimestampPeriod(beginGisTime, beginGisTime + length);
            
            int id = 0;
            long timestamp = 0;
            boolean isNeedAdd = true;
            for (IDataElement dataElement : elements) {
            	isNeedAdd = true;
            	Object objectValue = dataElement.get(propertyName);
            	if (objectValue == null || objectValue.toString().isEmpty()) {
            		isNeedAdd = false;
            	}
            	Object timestampValue = dataElement.get(DriveModel.TIMESTAMP);
            	if (timestampValue != null && !timestampValue.toString().isEmpty()) {
            		timestamp = Long.parseLong(timestampValue.toString());
            	}
            	else {
            		isNeedAdd = false;
            	}
            	
            	if (isNeedAdd) {
	             	series.addOrUpdate(new Millisecond(new Date(timestamp)), id);
	             	dataElementsToChart.put(id, dataElement);
	            	id++;
            	}
            }
            System.out.println("Count = " + id);
        }

        @Override
        public int getSeriesCount() {
            return collection.getSeriesCount();
        }

        @SuppressWarnings("rawtypes")
		@Override
        public Comparable getSeriesKey(int i) {
            return collection.getSeriesKey(i);
        }

        @Override
        public int getItemCount(int i) {
            return collection.getItemCount(i);
        }

        @Override
        public Number getX(int i, int j) {
            return collection.getX(i, j);
        }

        @Override
        public Number getY(int i, int j) {
        	
        	Integer id = collection.getY(i, j).intValue();
        	IDataElement dataElement = dataElementsToChart.get(id);
        	
        	Double value = 0.0;
        	Object objectValue = dataElement.get(propertyName);
        	if (objectValue != null && !objectValue.toString().isEmpty()) {
        		value = Double.parseDouble(objectValue.toString());
        	}
            return (Number)(value);
        }

        @SuppressWarnings("rawtypes")
		@Override
        public int getColumnIndex(Comparable comparable) {
            return 0;
        }

        @SuppressWarnings("rawtypes")
		@Override
        public Comparable getColumnKey(int i) {
            return null;
        }

        @SuppressWarnings("rawtypes")
		@Override
        public List getColumnKeys() {
            return null;
        }

        @SuppressWarnings("rawtypes")
		@Override
        public int getRowIndex(Comparable comparable) {
            return 0;
        }

        @SuppressWarnings("rawtypes")
		@Override
        public Comparable getRowKey(int i) {
            return null;
        }

        @SuppressWarnings("rawtypes")
		@Override
        public List getRowKeys() {
            return null;
        }

        @SuppressWarnings("rawtypes")
		@Override
        public Number getValue(Comparable comparable, Comparable comparable1) {
            return null;
        }

        @Override
        public int getColumnCount() {
            return 0;
        }

        @Override
        public int getRowCount() {
            return 0;
        }

        @Override
        public Number getValue(int i, int j) {
            return null;
        }

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
            DataElementWrapper wr = provider.dataElementWrapper;
            int index = (Integer)element;
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss"); //$NON-NLS-1$
            if (columnIndex == 0) {
                if (index >= wr.time.length) {
                    return ""; //$NON-NLS-1$
                }
                Long time = wr.time[index];
                return time == null ? "" : df.format(new Date(time)); //$NON-NLS-1$
            }
            if (columnIndex == 1) {
                if (index < wr.nEvents.length && wr.nEvents[index] != null) {
                	if (wr.nEvents[index].get(EVENT) != null) {
                		return wr.nEvents[index].get(EVENT).toString();
                	}
                }
                return ""; //$NON-NLS-1$
            }
            if (columnIndex < getCurrentPropertyCount() + 2 && 
            		(columnIndex - 2) < wr.nProperties.size() && 
            		wr.nProperties.get(columnIndex - 2)[index] != null) {
            	
                return wr.nProperties.get(columnIndex - 2)[index].get(wr.propertyNames.get(columnIndex - 2)).toString(); //$NON-NLS-1$
            }
            return ""; //$NON-NLS-1$
        }
    }

    /*
     * The content provider class is responsible for providing objects to the view. It can wrap
     * existing objects in adapters or simply return objects as-is. These objects may be sensitive
     * to the current input of the view, or ignore it and always show the same content (like Task
     * List, for example).
     */

    private class TableContentProvider implements IStructuredContentProvider {

    	private DataElementWrapper dataElementWrapper = new DataElementWrapper();
    	
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
            if (newInput == null || cPropertyList.getText().isEmpty()) {
                return;
            }
            dataElementWrapper = new DataElementWrapper();

            labelProvider.refreshTable();
            Double crosshair = ((XYPlot)chart.getPlot()).getDomainCrosshairValue();
            dataElementWrapper.propertyNames.clear();
            dataElementWrapper.propertyNames.addAll(propertyLists.get(cPropertyList.getText()));
            for (int i = 0; i < getCurrentPropertyCount(); i++) {
            	dataElementWrapper.propertyNames.add(currentProperies.get(i));
                changeName(labelProvider.columns.get(i + 2), dataElementWrapper.propertyNames.get(i));
            }
            dataElementWrapper.eventName = "";
            changeName(labelProvider.columns.get(1), dataElementWrapper.eventName);

            // nodeWrapper.nEvents.clear();
            dataElementWrapper.nEvents = new IDataElement[3];
            dataElementWrapper.time = new Long[3];;

            if (crosshair < 0.1) {
                return;
            }
            // nodeWrapper.time.add(null);
            dataElementWrapper.time[1] = crosshair.longValue();
            dataElementWrapper.time[0] = getPreviousTime(dataElementWrapper.time[1]);
            dataElementWrapper.time[2] = getNextTime(dataElementWrapper.time[1]);

            for (int i = 0; i < getCurrentPropertyCount(); i++) {
                fillProperty(crosshair, xydatasets.get(i).collection, 
                		dataElementWrapper.nProperties.get(i), dataElementWrapper.time);
            }
//            fillProperty(crosshair, eventDataset.collection, dataElementWrapper.nEvents, dataElementWrapper.time);

        }

        /**
         * @param tableColumn
         * @param name
         */
        private void changeName(TableColumn tableColumn, String name) {
            if (!tableColumn.getText().equals(name)) {
                tableColumn.setText(name);
            }
        }

        /**
         * @param crosshair
         * @param dataset
         * @param nodes
         */
        private void fillProperty(double crosshair, TimeSeriesCollection dataset, IDataElement[] nodes, Long[] time) {
            Integer index1 = getCrosshairIndex(dataset, time[1]);
            Integer idOfDataElement = 0;
            if (index1 != null) {
                idOfDataElement = dataset.getSeries(0).getDataItem(index1).getValue().intValue();
                nodes[1] = dataElementsToChart.get(idOfDataElement);
                if (index1 > 0) {
                	idOfDataElement = dataset.getSeries(0).getDataItem(index1 - 1).getValue().intValue(); 
                    nodes[0] = dataElementsToChart.get(idOfDataElement);
                }
                if (index1 + 1 < dataset.getSeries(0).getItemCount()) {
                	idOfDataElement = dataset.getSeries(0).getDataItem(index1 + 1).getValue().intValue();
                    nodes[2] = dataElementsToChart.get(idOfDataElement);
                }
            }
        }

    }

    private class DataElementWrapper {
    	int currentPropertyCount = getCurrentPropertyCount();
        List<String> propertyNames = new ArrayList<String>(currentPropertyCount);
        String eventName;
        Long[] time = new Long[3];
        List<IDataElement[]> nProperties = new ArrayList<IDataElement[]>(currentPropertyCount);
        IDataElement[] nEvents = new IDataElement[3];

        /**
         * Constructor
         */
        public DataElementWrapper() {
            for (int i = 0; i < currentPropertyCount; i++) {
                nProperties.add(new IDataElement[3]);
            }
        }
    }

    @Override
    public void propertyChange(@SuppressWarnings("deprecation") PropertyChangeEvent event) {
        formPropertyList();
        String[] result = propertyLists.keySet().toArray(new String[0]);
        Arrays.sort(result);
        cPropertyList.setItems(result);
        updatePropertyList();
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
