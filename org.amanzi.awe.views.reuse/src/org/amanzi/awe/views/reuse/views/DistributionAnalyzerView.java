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

import java.util.HashMap;
import java.util.List;

import org.amanzi.awe.views.reuse.ReusePlugin;
import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistribution.ChartType;
import org.amanzi.neo.model.distribution.IDistribution.Select;
import org.amanzi.neo.model.distribution.IDistributionModel;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.model.distribution.impl.DistributionManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel.DistributionItem;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.JFreeChart;

/**
 * View for Distribution Analyzis
 * @author gerzog
 * @since 1.0.0
 */
public class DistributionAnalyzerView extends ViewPart {
    
    private static final String DATASET_LABEL = "Data";
    
    private static final String PROPERTY_LABEL = "Property";
    
    private static final String DISTRIBUTION_LABEL = "Distribution";
    
    private static final String SELECT_LABEL = "Select";
    
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
    private HashMap<String, IDistribution<?>> distributionTypes = new HashMap<String, IDistribution<?>>();
    
    /*
     * Currently available Distribution
     */
    private IDistribution<?> currentDistributionType;
    
    /*
     * Distribution Model
     */
    private IDistributionModel distributionModel;
    
    /*
     * Distribution Chart
     */
    private JFreeChart distributionChart;

    @Override
    public void createPartControl(Composite parent) {
        //layout for main composite
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        parent.setLayout(layout);
        
        createDistributionSelectionCombos(parent);
        
        addListeners();
        
        //initialize fields
        try {
            initializeFields();
        } catch (AWEException e){
            //TODO: throw Runtime? show error message? 
        }
    }
    
    /**
     * Creates Combos to choose Distribution (Dataset, Property Name, Distribution Type)
     *
     * @param parent
     */
    private void createDistributionSelectionCombos(Composite parent) {
        //label and combo for Dataset
        Label datasetNameLabel = new Label(parent, SWT.NONE);
        datasetNameLabel.setText(DATASET_LABEL);
        
        datasetCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        
        //layout for label
        FormData dLabel = new FormData(); // bind to left & text
        dLabel.left = new FormAttachment(0, 5);
        dLabel.top = new FormAttachment(datasetCombo, 5, SWT.CENTER);
        datasetNameLabel.setLayoutData(dLabel);
        
        //layout for combo
        FormData dCombo = new FormData(); // bind to label and text
        dCombo.left = new FormAttachment(datasetNameLabel, 2);
        dCombo.top = new FormAttachment(0, 2);
        dCombo.right = new FormAttachment(20, -5);
        datasetCombo.setLayoutData(dCombo);
        
        //label and combo for Property
        Label propertyNameLabel = new Label(parent, SWT.NONE);
        propertyNameLabel.setText(PROPERTY_LABEL);
        
        propertyCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        
        //layout for label
        dLabel = new FormData(); // bind to left & text
        dLabel.left = new FormAttachment(datasetCombo, 10);
        dLabel.top = new FormAttachment(propertyCombo, 5, SWT.CENTER);
        propertyNameLabel.setLayoutData(dLabel);
        
        //layout for combo
        dCombo = new FormData(); // bind to label and text
        dCombo.left = new FormAttachment(propertyNameLabel, 2);
        dCombo.top = new FormAttachment(0, 2);
        dCombo.right = new FormAttachment(50, -5);
        propertyCombo.setLayoutData(dCombo);
        
        //label and combo for DistributionType
        Label distributionTypeLabel = new Label(parent, SWT.NONE);
        distributionTypeLabel.setText(DISTRIBUTION_LABEL);
        
        distributionCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        
        //layout for label
        dLabel = new FormData(); // bind to left & text
        dLabel.left = new FormAttachment(propertyCombo, 10);
        dLabel.top = new FormAttachment(distributionCombo, 5, SWT.CENTER);
        distributionTypeLabel.setLayoutData(dLabel);

        //layout for combo
        dCombo = new FormData(); // bind to label and text
        dCombo.left = new FormAttachment(distributionTypeLabel, 2);
        dCombo.top = new FormAttachment(0, 2);
        dCombo.right = new FormAttachment(68, -5);
        distributionCombo.setLayoutData(dCombo);
        
        //label and combo for Select
        Label selectLabel = new Label(parent, SWT.NONE);
        selectLabel.setText(SELECT_LABEL);
        
        selectCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        
        //layout for label
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
    
    private void createChartComposite(Composite parent) {
        
    }
    
    /**
     * Pre-initializations of all fields
     *
     * @throws AWEException
     */
    private void initializeFields() throws AWEException {
        //initialize current project
        currentProject = ProjectModel.getCurrentProjectModel();
        
        //get all distributional models
        distributionItems.clear();
        for (DistributionItem singleItem : currentProject.getAllDistributionalModels()) {
            distributionItems.put(singleItem.toString(), singleItem);
        }
        datasetCombo.setItems(distributionItems.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
        
        //property combo
        propertyCombo.setItems(ArrayUtils.EMPTY_STRING_ARRAY);
        propertyCombo.setEnabled(false);
        
        //distribution combo
        distributionCombo.setItems(ArrayUtils.EMPTY_STRING_ARRAY);
        distributionCombo.setEnabled(false);
        
        //select combo
        selectCombo.setItems(ArrayUtils.EMPTY_STRING_ARRAY);
        selectCombo.setEnabled(false);
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
            
            propertyCombo.setItems(analyzedModel.getAllPropertyNames(analyzedNodeType));
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
                List<IDistribution<?>> distribuitons = DistributionManager.getManager().
                        getDistributions(analyzedModel, analyzedNodeType, propertyName, ChartType.getDefault());
            
                for (IDistribution<?> singleDistribution : distribuitons) {
                    distributionTypes.put(singleDistribution.getName(), singleDistribution);
                }
            
                distributionCombo.setItems(distributionTypes.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
                distributionCombo.setEnabled(true);
            }
        } catch (AWEException e) {
            //TODO: handle exception
        }
    }
    
    /**
     * Initialized Distribution Type
     */
    private void initializeDistributionType() {
        String distribution = distributionCombo.getText();
        if (!StringUtils.isEmpty(distribution)) {
            //get distribution
            currentDistributionType = distributionTypes.get(distribution);
            
            //fill Select combo
            Select[] possibleSelects = currentDistributionType.getPossibleSelects();
            String[] selectNames = new String[possibleSelects.length];
            for (int i = 0; i < possibleSelects.length; i++) {
                selectNames[i] = possibleSelects[i].name();
            }
            selectCombo.setItems(selectNames);
            selectCombo.setEnabled(true);
            
            //run analizys
            runAnalyzis();
        }
    }
    
    /**
     * Starts analyzis
     */
    private void runAnalyzis() {
        Job distributionJob = new Job("Create Distribution model <" + currentDistributionType + ">") {
            
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                //initialize current distribution
                
                try {
                    if (distributionModel != null) {
                        //if old distribution model exists - mark it as not-current
                        distributionModel.setCurrent(false);
                    }
                    
                    distributionModel = analyzedModel.getDistributionModel(currentDistributionType);
                    
                    //mark new distribution model as current
                    distributionModel.setCurrent(true);
                    
                    distributionModel.getDistributionBars(monitor);
                } catch (AWEException e) {
                    //TODO: handle exception
                    return new Status(IStatus.ERROR, ReusePlugin.PLUGIN_ID, getName(), e);
                }
                
                return Status.OK_STATUS;
            }
        };
        
        //run a job and wait until it finishes
        distributionJob.schedule();
        
        try {
            distributionJob.join();
        } catch (InterruptedException e) {
            //TODO: handle exception
        }
    }
    
    /**
     * Add listeners on Components
     */
    private void addListeners() {
        //listener for Dataset combo
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
        
        
        //listener for Property combo
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
        
        //listener for Distribution combo
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
    }
    
    
    @Override
    public void setFocus() {
    }

}
