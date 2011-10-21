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

import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel.DistributionItem;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

/**
 * View for Distribution Analyzis
 * @author gerzog
 * @since 1.0.0
 */
public class DistributionAnalyzerView extends ViewPart {
    
    private static final String DATASET_LABEL = "Data";
    
    private static final String PROPERTY_LABEL = "Property";
    
    /*
     * Combo to choose DistributionItem
     */
    private Combo datasetCombo;
    
    /*
     * Combo to choose PropertyName
     */
    private Combo propertyCombo;
    
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
     * Map with Distribution Items
     */
    private HashMap<String, DistributionItem> distributionItems = new HashMap<String, DistributionItem>();

    @Override
    public void createPartControl(Composite parent) {
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
        
        //label and combo for Property
        Label propertyNameLabel = new Label(parent, SWT.NONE);
        propertyNameLabel.setText(PROPERTY_LABEL);
        
        propertyCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        
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
        
    }
    
    
    @Override
    public void setFocus() {
    }

}
