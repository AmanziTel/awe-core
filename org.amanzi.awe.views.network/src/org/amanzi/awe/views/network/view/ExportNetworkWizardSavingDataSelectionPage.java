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

package org.amanzi.awe.views.network.view;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class ExportNetworkWizardSavingDataSelectionPage extends WizardPage {

    private Group main;
    private List freqList; 
    private String fileWithPrefixName = "";
    private Button checkboxFrequencyConstraintData;
    private Button checkboxSeparationConstraintData;
    private Button checkboxTrafficData;
    private Button checkboxTrxData;
    private Button checkboxInterferenceMatrix;
    private Button checkboxNeigboursData;
    private Button checkboxNetworkSectorData;
    private ArrayList<Boolean> checkboxesState = new ArrayList<Boolean>();
    private ArrayList<Button> arrayOfCheckboxes = new ArrayList<Button>();
    private HashMap<Integer, Boolean> mapOfCheckboxesState = new HashMap<Integer, Boolean>();
    
    protected ExportNetworkWizardSavingDataSelectionPage(String pageName) {
        super(pageName);
        setDescription("Choose data that should be exported and prefix of target files for export");
        //setPageComplete(false);
    }
     
    @Override
    public void createControl(Composite parent) {
        main = new Group(parent, SWT.NULL);
        main.setLayout(new GridLayout(3, false));

        GridData checkboxLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
        
        checkboxNetworkSectorData = new Button(main, SWT.CHECK);
        checkboxNetworkSectorData.setText(ColumnsConfigPageTypes.NETWORK_SECTOR_DATA.getName());
        checkboxNetworkSectorData.setLayoutData(checkboxLayoutData);
        checkboxNetworkSectorData.setSelection(true);
        
        checkboxNeigboursData = new Button(main, SWT.CHECK);
        checkboxNeigboursData.setText(ColumnsConfigPageTypes.NEIGBOURS_DATA.getName());
        checkboxNeigboursData.setLayoutData(checkboxLayoutData);
        checkboxNeigboursData.setSelection(true);
        
        checkboxFrequencyConstraintData = new Button(main, SWT.CHECK);
        checkboxFrequencyConstraintData.setText(ColumnsConfigPageTypes.FREQUENCY_CONSTRAINT_DATA.getName());
        checkboxFrequencyConstraintData.setLayoutData(checkboxLayoutData);
        checkboxFrequencyConstraintData.setSelection(true);
        
        checkboxInterferenceMatrix = new Button(main, SWT.CHECK);
        checkboxInterferenceMatrix.setText(ColumnsConfigPageTypes.INTERFERENCE_MATRIX.getName());
        checkboxInterferenceMatrix.setLayoutData(checkboxLayoutData);
        checkboxInterferenceMatrix.setSelection(true);
        
        checkboxSeparationConstraintData = new Button(main, SWT.CHECK);
        checkboxSeparationConstraintData.setText(ColumnsConfigPageTypes.SEPARATION_CONSTRAINT_DATA.getName());
        checkboxSeparationConstraintData.setLayoutData(checkboxLayoutData);
        checkboxSeparationConstraintData.setSelection(true);
        
        checkboxTrafficData = new Button(main, SWT.CHECK);
        checkboxTrafficData.setText(ColumnsConfigPageTypes.TRAFFIC_DATA.getName());
        checkboxTrafficData.setLayoutData(checkboxLayoutData);
        checkboxTrafficData.setSelection(true);
        
        checkboxTrxData = new Button(main, SWT.CHECK);
        checkboxTrxData.setText(ColumnsConfigPageTypes.TRX_DATA.getName());
        checkboxTrxData.setLayoutData(checkboxLayoutData);
        checkboxTrxData.setSelection(true);
        checkboxTrxData.addMouseListener(new MouseListener() {
            
            @Override
            public void mouseUp(MouseEvent e) {
                if (checkboxTrxData.getSelection()) {
                    freqList.setVisible(true);
                }
                else {
                    freqList.setVisible(false);
                }
            }
            
            @Override
            public void mouseDown(MouseEvent e) {
            }
            
            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });
        
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
        freqList = new List(main, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
        gridData.heightHint = 200;
        freqList.setLayoutData(gridData);
        freqList.setItems(ExportNetworkWizard.getFrequencyPlanModelNames().toArray(new String[0]));
        freqList.addMouseListener(new MouseListener() {
            
            @Override
            public void mouseUp(MouseEvent e) {
                ExportNetworkWizard.setSelectionFrequencyPlanModelNames(freqList.getSelection());
            }
            
            @Override
            public void mouseDown(MouseEvent e) {
            }
            
            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });
        freqList.setSelection(0);
        
        arrayOfCheckboxes.clear();
        arrayOfCheckboxes.add(checkboxNeigboursData);
        arrayOfCheckboxes.add(checkboxFrequencyConstraintData);
        arrayOfCheckboxes.add(checkboxSeparationConstraintData);
        arrayOfCheckboxes.add(checkboxTrafficData);
        arrayOfCheckboxes.add(checkboxTrxData);
        arrayOfCheckboxes.add(checkboxInterferenceMatrix);
        arrayOfCheckboxes.add(checkboxNetworkSectorData);
        
        setControl(main);
    }

    /**
     * Gets the file name.
     * 
     * @return the file name
     */
    public String getFileWithPrefixName() {
        return this.fileWithPrefixName;
    }
    
    /**
     * Gets checkboxes state.
     *
     * @return the hashmap with checkboxes state
     */
    public ArrayList<Boolean> getCheckBoxesState() {
        checkboxesState.clear();
        checkboxesState.add(checkboxNeigboursData.getSelection());
        checkboxesState.add(checkboxFrequencyConstraintData.getSelection());
        checkboxesState.add(checkboxSeparationConstraintData.getSelection());
        checkboxesState.add(checkboxTrafficData.getSelection());
        checkboxesState.add(checkboxTrxData.getSelection());
        checkboxesState.add(checkboxInterferenceMatrix.getSelection());
        return checkboxesState;
    }
    
    public void setCheckBoxesState(int index, Boolean state) {
        checkboxesState = new ArrayList<Boolean>();
        if (checkboxNeigboursData != null) {
            checkboxesState.add(checkboxNeigboursData.getSelection());
            checkboxesState.add(checkboxFrequencyConstraintData.getSelection());
            checkboxesState.add(checkboxSeparationConstraintData.getSelection());
            checkboxesState.add(checkboxTrafficData.getSelection());
            checkboxesState.add(checkboxTrxData.getSelection());
            checkboxesState.add(checkboxInterferenceMatrix.getSelection());
            
            checkboxesState.set(index, state);
            for (Button button : arrayOfCheckboxes) {
                if (button.getText().equals(getNameOfPages().get(index))) {
                    button.setSelection(state);
                    button.setEnabled(state);
                    break;
                }
            }
        }
    }
    
    public void setMapOfCheckboxesState(Integer index, Boolean state) {
        mapOfCheckboxesState.put(index, state);
    }
    
    public HashMap<Integer, Boolean> getMapOfCheckboxesState() {
        return mapOfCheckboxesState;
    }
    /**
     * Gets checkboxes state.
     *
     * @return the hashmap with checkboxes state
     */
    public ArrayList<Boolean> getDefaultCheckBoxesState() {
        ArrayList<Boolean> checkboxesState = new ArrayList<Boolean>();
        for (int i = 0; i < 6; i++) {
            checkboxesState.add(true);
        }
        
        return checkboxesState;
    }
    
    public ArrayList<String> getNameOfPages() {
        ArrayList<String> nameOfPages = new ArrayList<String>();

        nameOfPages.add(ColumnsConfigPageTypes.NEIGBOURS_DATA.getName());
        nameOfPages.add(ColumnsConfigPageTypes.FREQUENCY_CONSTRAINT_DATA.getName());
        nameOfPages.add(ColumnsConfigPageTypes.SEPARATION_CONSTRAINT_DATA.getName());
        nameOfPages.add(ColumnsConfigPageTypes.TRAFFIC_DATA.getName());
        nameOfPages.add(ColumnsConfigPageTypes.TRX_DATA.getName());
        nameOfPages.add(ColumnsConfigPageTypes.INTERFERENCE_MATRIX.getName());
        
        return nameOfPages;
    }
}
