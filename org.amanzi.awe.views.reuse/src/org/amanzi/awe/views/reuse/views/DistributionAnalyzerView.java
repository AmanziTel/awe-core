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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
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
public class DistributionAnalyzerView extends ViewPart {

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
    
    private static final Color COLOR_SELECTED = Color.RED;

    private static final Color COLOR_LESS = Color.BLUE;

    private static final Color COLOR_MORE = Color.GREEN;
    
    

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
    private final Job UPDATE_BAR_COLORS_JOB = new Job("") {

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
    private Button colorProperties;
    
    /*
     * Combo to choose type of Chart
     */
    private Combo chartTypeCombo;
    
    /*
     * Label for Selection Adjacency spinner
     */
    private Label selectionAdjacencyLabel;
    
    /*
     * Spinner to set selection Adjacency
     */
    private Spinner selectionAdjacency;
    
    /*
     * Label for Selected Value Text
     */
    private Label selectedValueLabel;
    
    /*
     * Text for selected value
     */
    private Text selectedValue;
    
    public DistributionAnalyzerView() {
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

        addListeners();

        // initialize fields
        try {
            initializeFields();
        } catch (AWEException e) {
            // TODO: throw Runtime? show error message?
        }
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

        //chart composite
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

        colorProperties = new Button(parent, SWT.CHECK);

        // layout
        FormData dCombo = new FormData(); // bind to label and text
        dCombo.left = new FormAttachment(0, 2);
        dCombo.bottom = new FormAttachment(100, -2);
        colorProperties.setLayoutData(dCombo);

        FormData dLabel = new FormData();
        dLabel.left = new FormAttachment(colorProperties, 2);
        dLabel.top = new FormAttachment(colorProperties, 5, SWT.CENTER);
        colorPropertiesLabel.setLayoutData(dLabel);
        
        colorProperties.setEnabled(false);
        
        //combo for chart type
        chartTypeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        chartTypeCombo.setText(ChartType.getDefault().getTitle());
        
        //layout for combo
        dLabel = new FormData();
        dLabel.left = new FormAttachment(colorPropertiesLabel, 2);
        dLabel.top = new FormAttachment(colorPropertiesLabel, 5, SWT.CENTER);
        dLabel.width = 150;
        chartTypeCombo.setLayoutData(dLabel);
        
        chartTypeCombo.setVisible(false);
        
        //label and text for selected value
        selectedValueLabel = new Label(parent, SWT.NONE);
        selectedValueLabel.setText(SELECTED_VALUES_LABEL);
        
        selectedValue = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
        
        //layout label
        dLabel = new FormData();
        dLabel.left = new FormAttachment(chartTypeCombo, 15);
        dLabel.top = new FormAttachment(selectedValue, 5, SWT.CENTER);
        selectedValueLabel.setLayoutData(dLabel);
        
        //layout text
        FormData dText = new FormData();
        dText.left = new FormAttachment(selectedValueLabel, 5);
        dText.right = new FormAttachment(selectedValueLabel, 200);
        dText.bottom = new FormAttachment(100, -2);
        selectedValue.setLayoutData(dText);
        
        //label and spinner for selection adjacency
        selectionAdjacencyLabel = new Label(parent, SWT.NONE);
        selectionAdjacencyLabel.setText(SELECTION_ADJACENCY_LABEL);
        
        selectionAdjacency = new Spinner(parent, SWT.BORDER);
        selectionAdjacency.setDigits(0);
        selectionAdjacency.setIncrement(1);
        selectionAdjacency.setMinimum(0);
        selectionAdjacency.setSelection(1);

        //label layout
        dLabel = new FormData();
        dLabel.left = new FormAttachment(selectedValue, 5);
        dLabel.top = new FormAttachment(selectionAdjacency, 5, SWT.CENTER);
        selectionAdjacencyLabel.setLayoutData(dLabel);

        FormData dSpin = new FormData();
        dSpin.left = new FormAttachment(selectionAdjacencyLabel, 5);
        dSpin.top = new FormAttachment(selectedValue, 5, SWT.CENTER);
        selectionAdjacency.setLayoutData(dSpin);
        
        // layout chart
        FormData dChart = new FormData(); // bind to label and text
        dChart.left = new FormAttachment(0, 5);
        dChart.top = new FormAttachment(datasetCombo, 10);
        dChart.bottom = new FormAttachment(colorProperties, -2);
        dChart.right = new FormAttachment(100, -5);
        chartFrame.setLayoutData(dChart);
    }
    
    /**
     * Sets visibility for components for default coloring
     *
     * @param isVisible
     */
    private void setStandardStatusPanelVisisble(boolean isVisible) {
        selectedValue.setVisible(isVisible);
        selectedValueLabel.setVisible(isVisible);
        
        chartTypeCombo.setVisible(isVisible);
        
        selectionAdjacency.setVisible(isVisible);
        selectionAdjacencyLabel.setVisible(isVisible);
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
        
        setStandardStatusPanelVisisble(false);
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

                for (IDistribution< ? > singleDistribution : distribuitons) {
                    distributionTypes.put(singleDistribution.getName(), singleDistribution);
                }

                distributionCombo.setItems(distributionTypes.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
                distributionCombo.setEnabled(true);
                
                String[] chartTypeNames = new String[0];
                for (ChartType chartType : DistributionManager.getManager().getPossibleChartTypes(analyzedModel, analyzedNodeType, propertyName)) {
                    chartTypeNames = (String[])ArrayUtils.add(chartTypeNames, chartType.getTitle());
                }
                chartTypeCombo.setItems(chartTypeNames);
            }
        } catch (AWEException e) {
            // TODO: handle exception
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
                    }

                    distributionModel = analyzedModel.getDistributionModel(currentDistributionType);

                    // mark new distribution model as current
                    distributionModel.setCurrent(true);

                    distributionModel.getDistributionBars(monitor);
                } catch (AWEException e) {
                    // TODO: handle exception
                    return new Status(IStatus.ERROR, ReusePlugin.PLUGIN_ID, getName(), e);
                }

                ActionUtil.getInstance().runTask(new Runnable() {

                    @Override
                    public void run() {
                        updateChart();
                    }
                }, true);

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

                if (needRedraw && currentDistributionType.canChangeColors()) {
                    updateChartColors();
                }

                // TODO: also it should open NetworkTreeView with this Distribution
            }
        });
    }

    private void updateChart() {
        try {
            // set name of chart
            distributionChart.setTitle(distributionModel.getName());

            // update dataset
            dataset.setDistributionBars(distributionModel.getDistributionBars());

            distributionChart.fireChartChanged();
        } catch (AWEException e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            // show a chart
            chartFrame.setVisible(true);
            // enable main view
            mainView.setEnabled(true);
            
            //show color properties
            setStandardStatusPanelVisisble(true);
        }
    }

    /**
     * Updates colors of Chart
     */
    private void updateChartColors() {
        int selectedIndex = dataset.getColumnIndex(selectedBar);
        
        for (int i = 0; i < dataset.getDistributionBars().size(); i++) {
            Color barColor = null;
            IDistributionBar currentBar = (IDistributionBar)dataset.getColumnKey(i);
            if (currentDistributionType.canChangeColors()) {
                if (colorProperties.getSelection()) {
                    // use color properties
                } else {
                    if (selectedIndex >= 0) {
                        // just color selected and near bar
                        if (selectedIndex == i) {
                            barColor = COLOR_SELECTED;
                        } else if (Math.abs(selectedIndex - i) <= selectionAdjacency.getSelection()) {
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

    @Override
    public void setFocus() {
    }

}
