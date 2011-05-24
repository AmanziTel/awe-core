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

package org.amanzi.awe.afp.wizards.good;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.afp.models.AfpModelNew;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.networkselection.SelectionModel;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * Page for Step 0 of AFP Wizard.
 * 
 * Select Network, Selection List and AFP Scenario
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class Step0SelectScenarioPage extends AbstractAfpWizardPage {
    
    private static final String PAGE_NAME = "Select Network Data and Scenario";
    
    private static final String PAGE_DESCRIPTION = PAGE_NAME;
    
    private static final int STEP_NUMBER = 0;
    
    private static final String ALL_SELECTION_LISTS = "[ALL]";
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    
    private Combo networkCombo;
    
    private Combo selectionCombo;
    
    private Combo scenarioCombo;
    
    private Map<String, NetworkModel> networkModels = new HashMap<String, NetworkModel>(0);
    
    private Map<String, SelectionModel> selectionModels = new HashMap<String, SelectionModel>(0);
    
    private Map<String, AfpModelNew> scenarioModels = new HashMap<String, AfpModelNew>(0);
    
    private String selectedNetwork;
    
    private String selectedSelection;
    
    private String selectedScenario;
    
    /**
     * @param pageName
     * @param wizard
     */
    protected Step0SelectScenarioPage(AfpWizard wizard) {
        super(PAGE_NAME, wizard);
        
        setPageComplete(false);
        setTitle(AfpWizard.WIZARD_TITLE);
        setDescription(PAGE_DESCRIPTION);
        
        initNetworks();
    }

    @Override
    public void createControl(Composite parent) {
        
        Group main = new Group(parent, SWT.FILL);
        main.setLayout(new GridLayout(2, false));
        
        new Label(main, SWT.LEFT).setText("Network: ");
        networkCombo = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        networkCombo.setLayoutData(new GridData(GridData.FILL, SWT.BEGINNING, true, false));
        networkCombo.setItems(networkModels.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
        networkCombo.setEnabled(!networkModels.isEmpty());
        networkCombo.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateCombos();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        
        new Label(main, SWT.LEFT).setText("Selection List:");
        
        selectionCombo = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        selectionCombo.setLayoutData(new GridData(GridData.FILL, SWT.BEGINNING, true, false));
        selectionCombo.setEnabled(!networkModels.isEmpty());
        selectionCombo.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectSelectionList();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        
        new Label(main, SWT.LEFT).setText("AFP Scenario:");
        
        scenarioCombo = new Combo(main, SWT.DROP_DOWN);
        scenarioCombo.setLayoutData(new GridData(GridData.FILL, SWT.BEGINNING, true, false));
        scenarioCombo.setEnabled(!networkModels.isEmpty());
        scenarioCombo.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectScenario();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        
//        updateCombos();
        setPageComplete(true);
        setControl(main);
    }
    
    private void initNetworks() {
        for (NetworkModel networkModel : NetworkModel.getAllNetworkModels()) {
            networkModels.put(networkModel.getName(), networkModel);
        }
    }
    
    private void selectSelectionList() {
        selectedSelection = selectionCombo.getText().trim();
    }
    
    private void selectScenario() {
        selectedScenario = scenarioCombo.getText().trim();
    }
    
    private void updateCombos() {
        selectedNetwork = networkCombo.getText().trim();
        
        NetworkModel networkModel = networkModels.get(selectedNetwork);
        
        //update selection lists
        selectionModels.clear();
        selectionModels.put(ALL_SELECTION_LISTS, null);
        selectionModels.putAll(networkModel.getAllSelectionModels());
        selectionCombo.setItems(selectionModels.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
        selectionCombo.setText(ALL_SELECTION_LISTS);
        selectedSelection = ALL_SELECTION_LISTS;
        
        //update scenarios
        scenarioModels.clear();
        for (AfpModelNew scenario : AfpModelNew.getAllAfpScenarios(networkModel)) {
            scenarioModels.put(scenario.getName(), scenario);
        }
        scenarioCombo.setItems(scenarioModels.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
        scenarioCombo.setText(DATE_FORMAT.format(new Date()));
        selectedScenario = scenarioCombo.getText();
        
        setPageComplete(canFlipToNextPage());
    }
    
    public AfpModelNew getScenario() {
        return scenarioModels.get(selectedScenario);
    }
    
    public NetworkModel getNetworkModel() {
        return networkModels.get(selectedNetwork);
    }
    
    public SelectionModel getSelectionModel() {
        return selectionModels.get(selectedSelection);
    }
    
    public String getScenarioName() {
        return selectedScenario;
    }

    @Override
    public boolean canFlipToNextPage(){
       if (isValidPage()) {
           return true;
       }
        return false;
    }

    /**
     * Checks if is valid page.
     * 
     * @return true, if is valid page
     */
    protected boolean isValidPage() {
        
        if (StringUtils.isEmpty(selectedNetwork)) {
            return false;
        }
        
        if (StringUtils.isEmpty(selectedScenario)) {
            return false;
        }
        
        return true;
    }

    @Override
    protected int getStepNumber() {
        return STEP_NUMBER;
    }

    @Override
    protected void refreshPage() {
        //do nothing
    }
}
