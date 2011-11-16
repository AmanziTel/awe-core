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

package org.amanzi.awe.views.reuse.views;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.refractions.udig.project.ui.internal.dialogs.ColorEditor;
import net.refractions.udig.ui.PlatformGIS;

import org.amanzi.awe.views.reuse.ReusePlugin;
import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistribution.ChartType;
import org.amanzi.neo.model.distribution.IDistribution.Select;
import org.amanzi.neo.model.distribution.IDistributionBar;
import org.amanzi.neo.model.distribution.IDistributionModel;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.model.distribution.impl.DistributionManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.listeners.AbstractUIEvent;
import org.amanzi.neo.services.listeners.AbstractUIEventType;
import org.amanzi.neo.services.listeners.EventManager;
import org.amanzi.neo.services.listeners.IEventListener;
import org.amanzi.neo.services.listeners.ProjectChangedEvent;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel.DistributionItem;
import org.amanzi.neo.services.ui.utils.ActionUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.geotools.brewer.color.BrewerPalette;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * View for Distribution Analyzis
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class DistributionAnalyzerView extends ViewPart implements IEventListener {

    private static final String DATASET_LABEL = "Data";

    private static final String PROPERTY_LABEL = "Property";

    private static final String DISTRIBUTION_LABEL = "Distribution";

    private static final String SELECT_LABEL = "Select";

    private static final Color CHART_BACKGROUND = Color.WHITE;

    private static final Color PLOT_BACKGROUND = new Color(230, 230, 230);

    private static final String DISTRIBUTION_CHART_NAME = "Distribution Chart";

    private static final String VALUES_AXIS_NAME = "Values";

    private static final String NUMBERS_AXIS_NAME = "Numbers";

    private static final String COLOR_PROPERTIES_LABEL = "Color properties";

    private static final String SELECTED_VALUES_LABEL = "Selected values";

    private static final String SELECTION_ADJACENCY_LABEL = "Adjacency";

    private static final String BLEND_LABEL = "Blend";

    private static final String CHART_TYPE_LABEL = "Chart type";

    private static final String LEFT_COLOR_LABEL = "Left bar color";

    private static final String RIGHT_COLOR_LABEL = "Right bar color";

    private static final String MIDDLE_COLOR_LABEL = "Middle bar color";

    private static final String THIRD_COLOR_LABEL = "Third color label";

    private static final String PALETTE_LABEL = "Palette";

    private static final Color COLOR_SELECTED = Color.RED;

    private static final Color COLOR_LESS = Color.BLUE;

    private static final Color COLOR_MORE = Color.GREEN;
    
    private static final String LOAD_XML_LABEL = "Load Distribution Xml";
    
    private static final String SELECT_XML_DIALOG_LABEL = "Select Distribution XML";
    
    @SuppressWarnings("rawtypes")
    private class DistributionDataset extends AbstractDataset implements CategoryDataset {

        private final List<String> ROW_KEYS = new ArrayList<String>();

        private List<IDistributionBar> distributionBars = new ArrayList<IDistributionBar>();

        /** long serialVersionUID field */
        private static final long serialVersionUID = 1L;

        public DistributionDataset() {
            ROW_KEYS.add(VALUES_AXIS_NAME);
        }

        @Override
        public int getColumnIndex(Comparable arg0) {
            return distributionBars.indexOf(arg0);
        }

        @Override
        public Comparable getColumnKey(int arg0) {
            return distributionBars.get(arg0);
        }

        @Override
        public List getColumnKeys() {
            return distributionBars;
        }

        @Override
        public int getRowIndex(Comparable arg0) {
            return 0;
        }

        @Override
        public Comparable getRowKey(int arg0) {
            return VALUES_AXIS_NAME;
        }

        @Override
        public List getRowKeys() {
            return ROW_KEYS;
        }

        @Override
        public Number getValue(Comparable arg0, Comparable arg1) {
            if (arg1 instanceof IDistributionBar) {
                IDistributionBar bar = (IDistributionBar)arg1;

                return bar.getCount();
            }

            return 0;
        }

        @Override
        public int getColumnCount() {
            return distributionBars.size();
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public Number getValue(int arg0, int arg1) {
            return getValue(getRowKey(arg0), getColumnKey(arg1));
        }

        /**
         * @return Returns the ranges.
         */
        public List<IDistributionBar> getDistributionBars() {
            return distributionBars;
        }

        /**
         * @param ranges The ranges to set.
         */
        public void setDistributionBars(List<IDistributionBar> distributionBars) {
            this.distributionBars = distributionBars;
            fireDatasetChanged();
        }

    }

    private class DistributionBarRenderer extends BarRenderer {

        /** long serialVersionUID field */
        private static final long serialVersionUID = 1L;

        /**
         * Returns the paint for an item. Overrides the default behaviour inherited from
         * AbstractSeriesRenderer.
         * 
         * @param row the series.
         * @param column the category.
         * @return The item color.
         */
        @Override
        public Paint getItemPaint(final int row, final int column) {
            return dataset.getDistributionBars().get(column).getColor();
        }

    }

    /**
     * Job to update colors of Bar
     * 
     * @author gerzog
     * @since 1.0.0
     */
    private final Job UPDATE_BAR_COLORS_JOB = new Job(StringUtils.EMPTY) {

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            try {
                for (IDistributionBar bar : dataset.getDistributionBars()) {
                    distributionModel.updateBar(bar);
                }
            } catch (AWEException e) {
                return new Status(Status.ERROR, ReusePlugin.PLUGIN_ID, "Error on updating Distribution Bars", e);
            }

            return Status.OK_STATUS;
        }

    };

    /**
     * Listener for changing colors of blend
     * 
     * @author gerzog
     * @since 1.0.0
     */
    private class ChangeColorListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            distributionModel.setLeftColor(convertToColor(leftColor.getColorValue()));
            distributionModel.setRightColor(convertToColor(rightColor.getColorValue()));

            updateChartColors();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }

    }

    /*
     * Combo to choose DistributionItem
     */
    private Combo datasetCombo;

    /*
     * Combo to choose PropertyName
     */
    private Combo propertyCombo;

    /*
     * Distribution Combo
     */
    private Combo distributionCombo;

    /*
     * Select Combo
     */
    private Combo selectCombo;

    /*
     * Current project
     */
    private IProjectModel currentProject;

    /*
     * Current Analyzed Model
     */
    private IDistributionalModel analyzedModel;

    /*
     * Node Type to Analyzed
     */
    private INodeType analyzedNodeType;

    /*
     * Name of property to Analyze
     */
    private String propertyName;

    /*
     * Map with Distribution Items
     */
    private HashMap<String, DistributionItem> distributionItems = new HashMap<String, DistributionItem>();

    /*
     * Map with Distribution Types
     */
    private HashMap<String, IDistribution< ? >> distributionTypes = new HashMap<String, IDistribution< ? >>();

    /*
     * Currently available Distribution
     */
    private IDistribution< ? > currentDistributionType;

    /*
     * Distribution Model
     */
    private IDistributionModel distributionModel;

    /*
     * Distribution Chart
     */
    private JFreeChart distributionChart;

    /*
     * Dataset for Chart
     */
    private DistributionDataset dataset;

    /*
     * Composite for Chart
     */
    private ChartComposite chartFrame;

    /*
     * Parent composite
     */
    private Composite mainView;

    /*
     * Selected bar in the Chart
     */
    private IDistributionBar selectedBar;

    /*
     * Button for enabling/disabling color properties
     */
    private Button colorPropertiesButton;

    /*
     * Combo to choose type of Chart
     */
    private Combo chartTypeCombo;

    /*
     * Label for Chart Type combo
     */
    private Label chartTypeLabel;

    /*
     * Label for Selection Adjacency spinner
     */
    private Label selectionAdjacencyLabel;

    /*
     * Spinner to set selection Adjacency
     */
    private Spinner selectionAdjacencySpin;

    /*
     * Label for Selected Value Text
     */
    private Label selectedValueLabel;

    /*
     * Text for selected value
     */
    private Text selectedValueText;

    /*
     * Button for Blend option
     */
    private Button blendButton;

    /*
     * Label for Blend button
     */
    private Label blendLabel;

    /*
     * Editor of Left Color for Blend
     */
    private ColorEditor leftColor;

    /*
     * Editor of Right Color for Blend
     */
    private ColorEditor rightColor;

    /*
     * Editor of Middle Color for Blend
     */
    private ColorEditor middleColor;

    /*
     * Label for Palette Combo
     */
    private Label paletteLabel;

    /*
     * Combo to choose Palette
     */
    private Combo paletteCombo;

    /*
     * Currently selected palette
     */
    private BrewerPalette currentPalette;

    /*
     * Label to choose Third Color option
     */
    private Label thirdColorLabel;

    /*
     * Button to choose Third Color option
     */
    private Button thirdColorButton;
    
    /*
     * Action to load distribution xml;
     */
    private Action actLoadXml;
    
    /*
     * Dialog for selecting distribution xml
     */
    private FileDialog xmlFileDialog;

    /**
     * Custom constructor
     */
    public DistributionAnalyzerView() {
        EventManager.getInstance().addListener(AbstractUIEventType.PROJECT_CHANGED, this);
        UPDATE_BAR_COLORS_JOB.setSystem(true);
    }

    @Override
    public void createPartControl(Composite parent) {
        mainView = parent;

        // layout for main composite
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        parent.setLayout(layout);

        createDistributionSelectionCombos(parent);
        createDistributionChart(parent);
        createColoringPropertiesControl(parent);
        
        createXmlFileDialog();
        initMenuManager();

        addListeners();

        // initialize fields
        try {
            initializeFields();
        } catch (AWEException e) {
            showErrorMessage(e.getMessage());
        }
    }
    
    private void createXmlFileDialog() {
        xmlFileDialog = new FileDialog(getViewSite().getShell());
        xmlFileDialog.setText(SELECT_XML_DIALOG_LABEL);
        xmlFileDialog.setFilterExtensions(new String[] { "*.xml" });
        xmlFileDialog.setFilterNames(new String[] { "XML File (*.xml)" });
    } 
    
    private void initMenuManager() {
        IMenuManager mm = getViewSite().getActionBars().getMenuManager();
        actLoadXml = new Action(LOAD_XML_LABEL){
            @Override
            public void run(){
                String selected = xmlFileDialog.open();
                try {
                    DistributionManager.getManager().createDistributionFromFile(selected);
                } catch (Exception e) {
                    showErrorMessage(e.getMessage());
                }
            }
        };

        mm.add(actLoadXml);
    }

    /**
     * Creates Composite for Distribution Chart
     */
    private void createDistributionChart(Composite parent) {
        // initialize a dataset
        dataset = new DistributionDataset();

        // create a chart
        distributionChart = ChartFactory.createBarChart(DISTRIBUTION_CHART_NAME, VALUES_AXIS_NAME, NUMBERS_AXIS_NAME, dataset,
                PlotOrientation.VERTICAL, false, false, false);

        // axis properties
        CategoryPlot plot = (CategoryPlot)distributionChart.getPlot();
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // update renderer
        CategoryItemRenderer renderer = new DistributionBarRenderer();
        renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        plot.setRenderer(renderer);

        // default background colors
        plot.setBackgroundPaint(PLOT_BACKGROUND);
        distributionChart.setBackgroundPaint(CHART_BACKGROUND);

        // chart composite
        chartFrame = new ChartComposite(parent, 0, distributionChart, true);
        chartFrame.pack();
    }

    /**
     * Creates Combos to choose Distribution (Dataset, Property Name, Distribution Type)
     * 
     * @param parent
     */
    private void createDistributionSelectionCombos(Composite parent) {
        // label and combo for Dataset
        Label datasetNameLabel = new Label(parent, SWT.NONE);
        datasetNameLabel.setText(DATASET_LABEL);

        datasetCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);

        // layout for label
        FormData dLabel = new FormData(); // bind to left & text
        dLabel.left = new FormAttachment(0, 5);
        dLabel.top = new FormAttachment(datasetCombo, 5, SWT.CENTER);
        datasetNameLabel.setLayoutData(dLabel);

        // layout for combo
        FormData dCombo = new FormData(); // bind to label and text
        dCombo.left = new FormAttachment(datasetNameLabel, 2);
        dCombo.top = new FormAttachment(0, 2);
        dCombo.right = new FormAttachment(20, -5);
        datasetCombo.setLayoutData(dCombo);

        // label and combo for Property
        Label propertyNameLabel = new Label(parent, SWT.NONE);
        propertyNameLabel.setText(PROPERTY_LABEL);

        propertyCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);

        // layout for label
        dLabel = new FormData(); // bind to left & text
        dLabel.left = new FormAttachment(datasetCombo, 10);
        dLabel.top = new FormAttachment(propertyCombo, 5, SWT.CENTER);
        propertyNameLabel.setLayoutData(dLabel);

        // layout for combo
        dCombo = new FormData(); // bind to label and text
        dCombo.left = new FormAttachment(propertyNameLabel, 2);
        dCombo.top = new FormAttachment(0, 2);
        dCombo.right = new FormAttachment(50, -5);
        propertyCombo.setLayoutData(dCombo);

        // label and combo for DistributionType
        Label distributionTypeLabel = new Label(parent, SWT.NONE);
        distributionTypeLabel.setText(DISTRIBUTION_LABEL);

        distributionCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);

        // layout for label
        dLabel = new FormData(); // bind to left & text
        dLabel.left = new FormAttachment(propertyCombo, 10);
        dLabel.top = new FormAttachment(distributionCombo, 5, SWT.CENTER);
        distributionTypeLabel.setLayoutData(dLabel);

        // layout for combo
        dCombo = new FormData(); // bind to label and text
        dCombo.left = new FormAttachment(distributionTypeLabel, 2);
        dCombo.top = new FormAttachment(0, 2);
        dCombo.right = new FormAttachment(68, -5);
        distributionCombo.setLayoutData(dCombo);

        // label and combo for Select
        Label selectLabel = new Label(parent, SWT.NONE);
        selectLabel.setText(SELECT_LABEL);

        selectCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);

        // layout for label
        dLabel = new FormData(); // bind to left & text
        dLabel.left = new FormAttachment(distributionCombo, 10);
        dLabel.top = new FormAttachment(selectCombo, 5, SWT.CENTER);
        selectLabel.setLayoutData(dLabel);

        dCombo = new FormData(); // bind to label and text
        dCombo.left = new FormAttachment(selectLabel, 2);
        dCombo.top = new FormAttachment(0, 2);
        dCombo.right = new FormAttachment(82, -5);
        selectCombo.setLayoutData(dCombo);
    }

    /**
     * Creates control for coloring properties
     */
    private void createColoringPropertiesControl(Composite parent) {
        // label and button for color properties
        Label colorPropertiesLabel = new Label(parent, SWT.NONE);
        colorPropertiesLabel.setText(COLOR_PROPERTIES_LABEL);

        colorPropertiesButton = new Button(parent, SWT.CHECK);

        // layout
        FormData dCombo = new FormData(); // bind to label and text
        dCombo.left = new FormAttachment(0, 2);
        dCombo.bottom = new FormAttachment(100, -2);
        colorPropertiesButton.setLayoutData(dCombo);

        FormData dLabel = new FormData();
        dLabel.left = new FormAttachment(colorPropertiesButton, 2);
        dLabel.top = new FormAttachment(colorPropertiesButton, 5, SWT.CENTER);
        colorPropertiesLabel.setLayoutData(dLabel);

        colorPropertiesButton.setEnabled(false);

        // label and combo for chart type
        chartTypeLabel = new Label(parent, SWT.NONE);
        chartTypeLabel.setText(CHART_TYPE_LABEL);

        chartTypeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        chartTypeCombo.setText(ChartType.getDefault().getTitle());

        // layout for label
        dLabel = new FormData();
        dLabel.left = new FormAttachment(colorPropertiesLabel, 10);
        dLabel.top = new FormAttachment(colorPropertiesLabel, 5, SWT.CENTER);
        chartTypeLabel.setLayoutData(dLabel);

        // layout for combo
        dLabel = new FormData();
        dLabel.left = new FormAttachment(chartTypeLabel, 5);
        dLabel.right = new FormAttachment(chartTypeLabel, 200);
        dLabel.bottom = new FormAttachment(100, -2);
        chartTypeCombo.setLayoutData(dLabel);

        // label and text for selected value
        selectedValueLabel = new Label(parent, SWT.NONE);
        selectedValueLabel.setText(SELECTED_VALUES_LABEL);

        selectedValueText = new Text(parent, SWT.BORDER | SWT.READ_ONLY);

        // layout label
        dLabel = new FormData();
        dLabel.left = new FormAttachment(chartTypeCombo, 15);
        dLabel.top = new FormAttachment(selectedValueText, 5, SWT.CENTER);
        selectedValueLabel.setLayoutData(dLabel);

        // layout text
        FormData dText = new FormData();
        dText.left = new FormAttachment(selectedValueLabel, 5);
        dText.right = new FormAttachment(selectedValueLabel, 200);
        dText.bottom = new FormAttachment(100, -2);
        selectedValueText.setLayoutData(dText);

        // label and spinner for selection adjacency
        selectionAdjacencyLabel = new Label(parent, SWT.NONE);
        selectionAdjacencyLabel.setText(SELECTION_ADJACENCY_LABEL);

        selectionAdjacencySpin = new Spinner(parent, SWT.BORDER);
        selectionAdjacencySpin.setDigits(0);
        selectionAdjacencySpin.setIncrement(1);
        selectionAdjacencySpin.setMinimum(0);
        selectionAdjacencySpin.setSelection(1);

        // label layout
        dLabel = new FormData();
        dLabel.left = new FormAttachment(selectedValueText, 5);
        dLabel.top = new FormAttachment(selectionAdjacencySpin, 5, SWT.CENTER);
        selectionAdjacencyLabel.setLayoutData(dLabel);

        FormData dSpin = new FormData();
        dSpin.left = new FormAttachment(selectionAdjacencyLabel, 5);
        dSpin.top = new FormAttachment(selectedValueText, 5, SWT.CENTER);
        selectionAdjacencySpin.setLayoutData(dSpin);

        // layout chart
        FormData dChart = new FormData(); // bind to label and text
        dChart.left = new FormAttachment(0, 5);
        dChart.top = new FormAttachment(datasetCombo, 10);
        dChart.bottom = new FormAttachment(colorPropertiesButton, -2);
        dChart.right = new FormAttachment(100, -5);
        chartFrame.setLayoutData(dChart);

        // label and button for blend
        blendButton = new Button(parent, SWT.CHECK);

        blendLabel = new Label(parent, SWT.NONE);
        blendLabel.setText(BLEND_LABEL);

        // layout label
        dLabel = new FormData();
        dLabel.left = new FormAttachment(blendButton, 5);
        dLabel.top = new FormAttachment(blendButton, 5, SWT.CENTER);
        blendLabel.setLayoutData(dLabel);

        dText = new FormData();
        dText.left = new FormAttachment(selectedValueText, 15);
        dText.bottom = new FormAttachment(100, -2);
        blendButton.setLayoutData(dText);

        // right and left colors
        leftColor = new ColorEditor(parent);
        leftColor.getButton().setToolTipText(LEFT_COLOR_LABEL);
        rightColor = new ColorEditor(parent);
        rightColor.getButton().setToolTipText(RIGHT_COLOR_LABEL);

        // layout color editors
        dLabel = new FormData();
        dLabel.left = new FormAttachment(blendLabel, 15);
        dLabel.bottom = new FormAttachment(100, -2);
        leftColor.getButton().setLayoutData(dLabel);

        dLabel = new FormData();
        dLabel.left = new FormAttachment(leftColor.getButton(), 15);
        dLabel.bottom = new FormAttachment(100, -2);
        rightColor.getButton().setLayoutData(dLabel);

        // label and combo for Palette
        paletteLabel = new Label(parent, SWT.NONE);
        paletteLabel.setText(PALETTE_LABEL);

        paletteCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);

        // layout for label
        dLabel = new FormData();
        dLabel.left = new FormAttachment(blendLabel, 15);
        dLabel.top = new FormAttachment(paletteCombo, 5, SWT.CENTER);
        paletteLabel.setLayoutData(dLabel);

        // layout for combo
        dText = new FormData();
        dText.left = new FormAttachment(paletteLabel, 5);
        dText.right = new FormAttachment(paletteLabel, 200);
        dText.bottom = new FormAttachment(100, -2);
        paletteCombo.setLayoutData(dText);

        // label and button for third color option
        thirdColorLabel = new Label(parent, SWT.NONE);
        thirdColorLabel.setText(THIRD_COLOR_LABEL);

        thirdColorButton = new Button(parent, SWT.CHECK);

        // layout for label
        dLabel = new FormData();
        dLabel.left = new FormAttachment(thirdColorButton, 5);
        dLabel.top = new FormAttachment(thirdColorButton, 5, SWT.CENTER);
        thirdColorLabel.setLayoutData(dLabel);

        // layout for button
        dText = new FormData();
        dText.left = new FormAttachment(rightColor.getButton(), 15);
        dText.bottom = new FormAttachment(100, -2);
        thirdColorButton.setLayoutData(dText);

        // middle color editor
        middleColor = new ColorEditor(parent);
        middleColor.getButton().setToolTipText(MIDDLE_COLOR_LABEL);

        // layout for middle color editor
        dLabel = new FormData();
        dLabel.left = new FormAttachment(thirdColorLabel, 15);
        dLabel.bottom = new FormAttachment(100, -2);
        middleColor.getButton().setLayoutData(dLabel);
    }

    /**
     * Sets visibility for components for default coloring
     * 
     * @param isVisible
     */
    private void setStandardStatusPanelVisisble(boolean isVisible) {
        colorPropertiesButton.setVisible(isVisible);
        colorPropertiesButton.setEnabled(isVisible);

        selectedValueText.setVisible(isVisible);
        selectedValueLabel.setVisible(isVisible);

        chartTypeCombo.setVisible(isVisible);
        chartTypeLabel.setVisible(isVisible);

        selectionAdjacencySpin.setVisible(isVisible);
        selectionAdjacencyLabel.setVisible(isVisible);
    }

    /**
     * Set visibility for components related to Blend Coloring
     * 
     * @param isVisible
     */
    private void setBlendPanelVisible(boolean isVisible) {
        colorPropertiesButton.setVisible(isVisible);
        colorPropertiesButton.setEnabled(isVisible);

        selectedValueText.setVisible(isVisible);
        selectedValueLabel.setVisible(isVisible);

        chartTypeCombo.setVisible(isVisible);
        chartTypeLabel.setVisible(isVisible);

        blendButton.setVisible(isVisible);
        blendLabel.setVisible(isVisible);

        leftColor.getButton().setVisible(isVisible);
        rightColor.getButton().setVisible(isVisible);

        thirdColorButton.setVisible(isVisible);
        thirdColorLabel.setVisible(isVisible);

        if (isVisible) {
            leftColor.setColorValue(convertToRGB(distributionModel.getLeftColor()));
            rightColor.setColorValue(convertToRGB(distributionModel.getRightColor()));

            setThirdColorPanelVisible(thirdColorButton.getSelection());
        } else {
            setThirdColorPanelVisible(isVisible);
        }
    }

    /**
     * Set visibility for components related to Middle Color of Blend
     * 
     * @param isVisible
     */
    private void setThirdColorPanelVisible(boolean isVisible) {
        middleColor.getButton().setVisible(isVisible);

        if (isVisible) {
            middleColor.setColorValue(convertToRGB(distributionModel.getMiddleColor()));
        }
    }

    /**
     * Set visibility for components related to Palette coloring
     */
    private void setPalettePanelVisible(boolean isVisible) {
        colorPropertiesButton.setVisible(isVisible);
        colorPropertiesButton.setEnabled(isVisible);

        selectedValueText.setVisible(isVisible);
        selectedValueLabel.setVisible(isVisible);

        chartTypeCombo.setVisible(isVisible);
        chartTypeLabel.setVisible(isVisible);

        blendButton.setVisible(isVisible);
        blendLabel.setVisible(isVisible);

        paletteLabel.setVisible(isVisible);
        paletteCombo.setVisible(isVisible);
    }

    /**
     * Converts Color to RGB
     * 
     * @param color
     * @return
     */
    private RGB convertToRGB(Color color) {
        return new RGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Converts RGB to Color
     * 
     * @param rgb
     * @return
     */
    private Color convertToColor(RGB rgb) {
        return new Color(rgb.red, rgb.green, rgb.blue);
    }

    /**
     * Pre-initializations of all fields
     * 
     * @throws AWEException
     */
    private void initializeFields() throws AWEException {
        // initialize current project
        currentProject = ProjectModel.getCurrentProjectModel();

        // get all distributional models
        distributionItems.clear();
        for (DistributionItem singleItem : currentProject.getAllDistributionalModels()) {
            distributionItems.put(singleItem.toString(), singleItem);
        }
        datasetCombo.setItems(distributionItems.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));

        // property combo
        propertyCombo.setItems(ArrayUtils.EMPTY_STRING_ARRAY);
        propertyCombo.setEnabled(false);

        // distribution combo
        distributionCombo.setItems(ArrayUtils.EMPTY_STRING_ARRAY);
        distributionCombo.setEnabled(false);

        // select combo
        selectCombo.setItems(ArrayUtils.EMPTY_STRING_ARRAY);
        selectCombo.setEnabled(false);

        // chart
        chartFrame.setVisible(false);

        // blend
        blendButton.setSelection(true);

        // palettes
        String[] paletteName = PlatformGIS.getColorBrewer().getPaletteNames();
        paletteCombo.setItems(paletteName);
        paletteCombo.select(0);

        setStandardStatusPanelVisisble(false);
        setBlendPanelVisible(false);
        setPalettePanelVisible(false);
    }

    /**
     * Initialized PropertyList for choosen Dataset
     */
    private void initializePropertyList() {
        String itemName = datasetCombo.getText();
        if (!StringUtils.isEmpty(itemName)) {
            DistributionItem distributionItem = distributionItems.get(itemName);

            analyzedModel = distributionItem.getModel();
            analyzedNodeType = distributionItem.getNodeType();

            String[] propertyNames = analyzedModel.getAllPropertyNames(analyzedNodeType);
            Arrays.sort(propertyNames);
            propertyCombo.setItems(propertyNames);
            propertyCombo.setEnabled(true);
            distributionCombo.setItems(new String[] {});
            selectCombo.setItems(new String[] {});
            distributionCombo.setEnabled(false);
            selectCombo.setEnabled(false);
        }
    }

    /**
     * Initialized list of Distribution Types available for selected property and data
     */
    private void initializeDistributionCombo() {
        try {
            propertyName = propertyCombo.getText();
            if (!StringUtils.isEmpty(propertyName)) {
                List<IDistribution< ? >> distribuitons = DistributionManager.getManager().getDistributions(analyzedModel,
                        analyzedNodeType, propertyName, ChartType.getDefault());

                distributionTypes.clear();
                for (IDistribution< ? > singleDistribution : distribuitons) {
                    distributionTypes.put(singleDistribution.getName(), singleDistribution);
                }

                String[] distributionItems = distributionTypes.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY);
                selectCombo.setItems(new String[] {});
                distributionCombo.setItems(distributionItems);
                distributionCombo.setEnabled(true);

                String[] chartTypeNames = new String[0];
                String defChartType = StringUtils.EMPTY;
                for (ChartType chartType : DistributionManager.getManager().getPossibleChartTypes(analyzedModel, analyzedNodeType,
                        propertyName)) {
                    chartTypeNames = (String[])ArrayUtils.add(chartTypeNames, chartType.getTitle());
                    if (chartType.equals(ChartType.getDefault())) {
                        defChartType = chartType.getTitle();
                    }
                }
                chartTypeCombo.setItems(chartTypeNames);
                chartTypeCombo.setText(defChartType);

                if (distributionItems.length == 1) {
                    distributionCombo.setText(distributionItems[0]);
                    initializeDistributionType();
                } else {
                    selectCombo.setEnabled(false);
                }

            }
        } catch (AWEException e) {
            showErrorMessage(e.getMessage());
        }
    }

    /**
     * Initialized Distribution Type
     */
    private void initializeDistributionType() {
        String distribution = distributionCombo.getText();
        if (!StringUtils.isEmpty(distribution)) {
            // get distribution
            currentDistributionType = distributionTypes.get(distribution);

            // fill Select combo
            Select[] possibleSelects = currentDistributionType.getPossibleSelects();
            String[] selectNames = new String[possibleSelects.length];
            for (int i = 0; i < possibleSelects.length; i++) {
                selectNames[i] = possibleSelects[i].name();
            }
            selectCombo.setItems(selectNames);
            selectCombo.setEnabled(true);
            selectCombo.setText(selectNames[0]);

            // run analizys
            runAnalyzis();
        }
    }

    private void enableColorPropertiesButton() {
        String distribution = distributionCombo.getText();
        if (!StringUtils.isEmpty(distribution)) {
            currentDistributionType = distributionTypes.get(distribution);

            if (colorPropertiesButton.getSelection()) {
                colorPropertiesButton.setSelection(false);
            }
            colorPropertiesButton.setEnabled(currentDistributionType.canChangeColors());
            colorPropertiesButton.setVisible(currentDistributionType.canChangeColors());
        }
    }

    /**
     * Starts analyzis
     */
    private void runAnalyzis() {
        mainView.setEnabled(false);

        Job distributionJob = new Job("Create Distribution model <" + currentDistributionType + ">") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                // initialize current distribution

                try {
                    if (distributionModel != null) {
                        // if old distribution model exists - mark it as not-current
                        distributionModel.setCurrent(false);
                        distributionModel.finishUp();
                    }

                    distributionModel = analyzedModel.getDistributionModel(currentDistributionType);

                    // mark new distribution model as current
                    distributionModel.setCurrent(true);

                    distributionModel.getDistributionBars(monitor);
                    ActionUtil.getInstance().runTask(new Runnable() {

                        @Override
                        public void run() {
                            updateChart();
                        }
                    }, true);

                } catch (AWEException e) {
                    showErrorMessage(e.getMessage());
                    return new Status(IStatus.ERROR, ReusePlugin.PLUGIN_ID, getName(), e);
                }

                return Status.OK_STATUS;
            }
        };

        // run a job and wait until it finishes
        distributionJob.schedule();
    }

    /**
     * Add listeners on Components
     */
    private void addListeners() {
        // listener for Dataset combo
        datasetCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                initializePropertyList();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        // listener for Property combo
        propertyCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                initializeDistributionCombo();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        // listener for Distribution combo
        distributionCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                initializeDistributionType();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        // listener for Chart View
        chartFrame.addChartMouseListener(new ChartMouseListener() {

            @Override
            public void chartMouseMoved(ChartMouseEvent arg0) {
                // not interested
            }

            @Override
            public void chartMouseClicked(ChartMouseEvent arg0) {
                boolean needRedraw = true;
                ChartEntity entity = arg0.getEntity();
                if (entity instanceof CategoryItemEntity) {
                    CategoryItemEntity itemEntity = (CategoryItemEntity)entity;
                    IDistributionBar newSelectedBar = (IDistributionBar)itemEntity.getColumnKey();

                    if (selectedBar != null && newSelectedBar.equals(selectedBar)) {
                        needRedraw = false;
                    } else {
                        selectedBar = newSelectedBar;
                    }
                } else {
                    // skip selection
                    selectedBar = null;
                }

                if (needRedraw) {
                    updateChartColors();
                }

                // TODO: also it should open NetworkTreeView with this Distribution
            }
        });

        // selection adjacency
        selectionAdjacencySpin.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                updateChartColors();
            }
        });

        // color properties
        colorPropertiesButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (colorPropertiesButton.getSelection()) {
                    setStandardStatusPanelVisisble(false);
                    setBlendPanelVisible(true);
                } else {
                    setBlendPanelVisible(false);
                    setStandardStatusPanelVisisble(true);
                }

                updateChartColors();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        SelectionListener colorListener = new ChangeColorListener();
        leftColor.addSelectionListener(colorListener);
        rightColor.addSelectionListener(colorListener);
        middleColor.addSelectionListener(colorListener);

        // listener for Blend button
        blendButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (blendButton.getSelection()) {
                    setPalettePanelVisible(false);
                    setBlendPanelVisible(true);
                } else {
                    setBlendPanelVisible(false);
                    setPalettePanelVisible(true);

                    updatePalette();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        // selection listener for palette
        paletteCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updatePalette();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        // selection listener for Third Color Option button
        thirdColorButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setThirdColorPanelVisible(thirdColorButton.getSelection());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        // selection listener for Chart Type combo
        chartTypeCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }

    /**
     * Updates a Chart
     */
    private void updateChart() {
        try {
            // set name of chart
            distributionChart.setTitle(distributionModel.getName());

            // update dataset
            dataset.setDistributionBars(distributionModel.getDistributionBars());

            distributionChart.fireChartChanged();
        } catch (AWEException e) {
            showErrorMessage(e.getMessage());
            e.printStackTrace();
        } finally {
            // show a chart
            chartFrame.setVisible(true);
            // enable main view
            mainView.setEnabled(true);

            // show color properties
            setBlendPanelVisible(false);
            setStandardStatusPanelVisisble(true);

            enableColorPropertiesButton();
            updateChartColors();
        }
    }

    /**
     * Updates colors of Chart
     */
    private void updateChartColors() {
        if (!currentDistributionType.canChangeColors())
            return;
        int selectedIndex = dataset.getColumnIndex(selectedBar);

        RGB leftRGB = leftColor.getColorValue();
        RGB rightRGB = rightColor.getColorValue();
        RGB middleRGB = middleColor.getColorValue();

        int size = distributionModel.getBarCount();
        int midColumnIndex = size / 2;

        float ratio = 0;
        float ratio2 = 0;
        float perc = size <= 0 ? 1 : (float)1 / size;
        float percMid1 = midColumnIndex == 0 ? 1 : (float)1 / (midColumnIndex);
        float percMid2 = size - midColumnIndex == 0 ? 1 : (float)1 / (size - midColumnIndex);

        Color[] paletteColors = null;
        if (currentPalette != null) {
            paletteColors = currentPalette.getColors(currentPalette.getMaxColors());
        }

        for (int i = 0; i < dataset.getDistributionBars().size(); i++) {
            Color barColor = null;
            IDistributionBar currentBar = (IDistributionBar)dataset.getColumnKey(i);
            if (currentDistributionType.canChangeColors()) {
                if (colorPropertiesButton.getSelection()) {
                    // use color properties
                    if (blendButton.getSelection()) {
                        if (thirdColorButton.getSelection()) {
                            // three color blend
                            if (i < midColumnIndex) {
                                barColor = blend(leftRGB, middleRGB, ratio);
                                ratio += percMid1;
                            } else if (i == midColumnIndex) {
                                barColor = convertToColor(middleRGB);
                            } else {
                                barColor = blend(middleRGB, rightRGB, ratio2);
                                ratio2 += percMid2;
                            }
                        } else {
                            // two color blen
                            barColor = blend(leftRGB, rightRGB, ratio);
                            ratio += perc;
                        }
                    } else {
                        // choose color from palette
                        if (paletteColors != null) {
                            barColor = paletteColors[i % paletteColors.length];
                        }
                    }
                } else {
                    if (selectedIndex >= 0) {
                        // just color selected and near bar
                        if (selectedIndex == i) {
                            barColor = COLOR_SELECTED;
                        } else if (Math.abs(selectedIndex - i) <= selectionAdjacencySpin.getSelection()) {
                            barColor = i > selectedIndex ? COLOR_MORE : COLOR_LESS;
                        }
                    }
                }
            }
            currentBar.setColor(barColor);
        }

        UPDATE_BAR_COLORS_JOB.schedule();

        distributionChart.fireChartChanged();
    }

    /**
     * Blend color
     * 
     * @param bg left
     * @param fg right
     * @param factor factor (0-1)
     * @return RGB
     */
    private Color blend(RGB bg, RGB fg, float factor) {
        if (factor < 0.0)
            factor = 0F;
        if (factor > 1.0)
            factor = 1F;
        float complement = 1.0F - factor;
        RGB rgb = new RGB((int)(complement * bg.red + factor * fg.red), (int)(complement * bg.green + factor * fg.green),
                (int)(complement * bg.blue + factor * fg.blue));

        return convertToColor(rgb);
    }

    /**
     * Updates Palette from UI
     */
    private void updatePalette() {
        if (paletteCombo.getSelectionIndex() >= 0) {
            String paletteName = paletteCombo.getText();
            currentPalette = PlatformGIS.getColorBrewer().getPalette(paletteName);
        } else {
            currentPalette = null;
        }

        distributionModel.setPalette(currentPalette);
        updateChartColors();
    }

    @Override
    public void setFocus() {    
    }
    
    private void showErrorMessage(final String message) {        
        showMessage(message, SWT.ICON_ERROR);
    }

    private void showMessage(final String message, final int style) {
        ActionUtil.getInstance().runTask(new Runnable() {

            @Override
            public void run() {
                MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(), style);
                msgBox.setMessage(message);
                msgBox.open();
            }
        }, false);
    }

    @Override
    public void handleEvent(AbstractUIEvent event) {
        if (event instanceof ProjectChangedEvent) {
            showMessage("New Project: " + ((ProjectChangedEvent)event).getProjectName(), SWT.ICON_INFORMATION);
        }
    }

}
