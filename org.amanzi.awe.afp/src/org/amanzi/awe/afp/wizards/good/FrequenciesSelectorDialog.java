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

import java.util.Arrays;
import java.util.Comparator;

import org.amanzi.awe.afp.models.FrequencyDomain;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class FrequenciesSelectorDialog extends Dialog {

    private String[] availableFrequencies;

    private String[] selectedFrequencies;

    private Shell shell;
    
    private List availableFrequenciesList;
    
    private List selectedFrequenciesList;
    
    private FrequencyDomain domain;

    /**
     * @param parent
     */
    public FrequenciesSelectorDialog(Shell parent, java.util.List<String> availableFrequencies, FrequencyDomain domain) {
        super(parent);
        
        this.domain = domain;

        prepareFrequenciesList(availableFrequencies, domain.getSelectedFrequencies());

        createDialog(parent);
    }

    private void createDialog(Shell parent) {
        shell = new Shell(parent, SWT.PRIMARY_MODAL | SWT.TITLE);
        shell.setText("Frequency Selector");
        shell.setLayout(new GridLayout(3, false));
        shell.setLocation(200, 200);

        Group freqGroup = new Group(shell, SWT.NONE);
        freqGroup.setLayout(new GridLayout(3, false));
        freqGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 3, 1));
        freqGroup.setText("Frequency Selector");

        Label freqLabel = new Label(freqGroup, SWT.LEFT);
        freqLabel.setText("Frequencies");
        freqLabel.setLayoutData(new GridData(GridData.FILL, SWT.LEFT, true, false, 2, 1));

        Label selectionLabel = new Label(freqGroup, SWT.LEFT);
        selectionLabel.setText(selectedFrequencies.length + " Frequencies Selected");
        selectionLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 1));

        availableFrequenciesList = new List(freqGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4);
        int listHeight = availableFrequenciesList.getItemHeight() * 12;
        int listWidth = selectionLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;

        Rectangle trim = availableFrequenciesList.computeTrim(0, 0, 0, listHeight);
        gridData.heightHint = trim.height;
        gridData.widthHint = listWidth;
        availableFrequenciesList.setLayoutData(gridData);
        availableFrequenciesList.setItems(availableFrequencies);

        Button leftDoubleArrowButton = new Button(freqGroup, SWT.NONE);
        GridData arrowGridData = new GridData(GridData.CENTER, GridData.END, true, false, 1, 1);
        arrowGridData.verticalIndent = trim.height / 10;
        leftDoubleArrowButton.setLayoutData(arrowGridData);
        leftDoubleArrowButton.setText("<<");

        selectedFrequenciesList = new List(freqGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
        gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4);
        gridData.heightHint = trim.height;
        gridData.widthHint = listWidth;
        selectedFrequenciesList.setLayoutData(gridData);
        selectedFrequenciesList.setItems(selectedFrequencies);

        Button leftArrowButton = new Button(freqGroup, SWT.NONE);
        leftArrowButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 1));
        leftArrowButton.setText("<");

        Button rightArrowButton = new Button(freqGroup, SWT.NONE);
        GridData gd = new GridData(GridData.FILL, GridData.END, true, false, 1, 1);
        gd.verticalIndent = trim.height / 5;
        rightArrowButton.setLayoutData(gd);
        rightArrowButton.setText(">");

        Button rightDoubleArrowButton = new Button(freqGroup, SWT.NONE);
        rightDoubleArrowButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 1));
        rightDoubleArrowButton.setText(">>");
        
        rightArrowButton.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectFrequencies();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetDefaultSelected(e);
            }
        });
        
        rightDoubleArrowButton.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectAllFrequencies();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetDefaultSelected(e);
            }
        });
        
        leftArrowButton.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                deselectFrequncies();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetDefaultSelected(e);
            }
        });
        
        leftDoubleArrowButton.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                deselectAllFrequencies();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetDefaultSelected(e);
            }
        });
        
        Button selectButton = new Button(shell, SWT.PUSH);
        selectButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, true, false, 2, 1));
        selectButton.setText("Select");
        selectButton.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent e) {
                setFrequencies();
                
                shell.dispose();
            }
        });
        
        Button cancelButton = new Button(shell, SWT.PUSH);
        cancelButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false, 1, 1));
        cancelButton.setText("Cancel");
        cancelButton.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.dispose();
            }
        });
    }   

    private void prepareFrequenciesList(java.util.List<String> availableFrequenciesList, java.util.List<String> selectedFrequenciesList) {
        selectedFrequencies = new String[selectedFrequenciesList.size()];

        int i = 0;
        for (String frequency : selectedFrequenciesList) {
            if (availableFrequenciesList.contains(frequency)) {
                availableFrequenciesList.remove(frequency);
            }
            selectedFrequencies[i++] = frequency;
        }

        availableFrequencies = new String[availableFrequenciesList.size()];
        for (String frequency : availableFrequenciesList) {
            availableFrequencies[i++] = frequency;
        }
    }

    public void open() {
        shell.pack();
        shell.open();
    }
    
    private void moveFrequencies(List target, List destination, boolean all) {
        String[] destFrequencies = destination.getItems();
        
        String[] targetFrequencies;
        if (all) {
            targetFrequencies = target.getItems();
        }
        else {
            targetFrequencies = target.getSelection();
        }
        
        for (String frequency : targetFrequencies) {
            destFrequencies = (String[])ArrayUtils.add(destFrequencies, frequency);
            destination.remove(frequency);
        }
        
        Arrays.sort(destFrequencies, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
            }
        });
        
        destination.setItems(destFrequencies);
    }
    
    private void selectFrequencies() {
        moveFrequencies(availableFrequenciesList, selectedFrequenciesList, false);
    }
    
    private void deselectFrequncies() {
        moveFrequencies(selectedFrequenciesList, availableFrequenciesList, false);
    }
    
    private void selectAllFrequencies() {
        moveFrequencies(availableFrequenciesList, selectedFrequenciesList, true);
    }
    
    private void deselectAllFrequencies() { 
        moveFrequencies(selectedFrequenciesList, availableFrequenciesList, true);
    }
    
    private void setFrequencies() {
        domain.setSelectedFrequencies(Arrays.asList(selectedFrequenciesList.getItems()));
    }

}
