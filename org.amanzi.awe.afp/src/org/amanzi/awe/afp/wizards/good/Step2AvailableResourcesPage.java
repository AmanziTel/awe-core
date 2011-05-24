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
import java.util.HashMap;

import org.amanzi.awe.afp.models.AfpModelNew;
import org.amanzi.awe.afp.models.FrequencyDomain;
import org.amanzi.awe.afp.models.parameters.FrequencyBand;
import org.amanzi.neo.services.utils.Pair;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public class Step2AvailableResourcesPage extends AbstractAfpWizardPage {
    
    private static final String PAGE_NAME = "Available Sources";

    private static final int STEP_NUMBER = 2;

    private static final String PAGE_DESCRIPTION = "Step 2 - " + PAGE_NAME;
    
    private HashMap<FrequencyBand, Pair<Text, Button>> bandFields = new HashMap<FrequencyBand, Pair<Text,Button>>();
    
    private HashMap<String, Pair<Button, Button>> bsicFields = new HashMap<String, Pair<Button,Button>>();
    
    /**
     * @param pageName
     * @param wizard
     */
    protected Step2AvailableResourcesPage(AfpWizard wizard) {
        super(PAGE_NAME, wizard);

        setPageComplete(false);
        setTitle(AfpWizard.WIZARD_TITLE);
        setDescription(PAGE_DESCRIPTION);
    }

    @Override
    public void createControl(Composite parent) {
        Composite thisParent = new Composite(parent, SWT.NONE);
        thisParent.setLayout(new GridLayout(2, false));

        createStepsGroup(thisParent, getStepNumber());
        
        Group main = new Group(thisParent, SWT.NONE);
        main.setLayout(new GridLayout(1, true));
        main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 2));
        
        Group frequenciesGroup = new Group(main, SWT.NONE);
        frequenciesGroup.setLayout(new GridLayout(3, false));
        frequenciesGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 5));
        frequenciesGroup.setText("Frequencies");

        Label bandLabel = new Label(frequenciesGroup, SWT.LEFT);
        bandLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 3, 1));
        bandLabel.setText("Band");
        makeFontBold(bandLabel);
        
        for (final FrequencyBand frequencyBand : FrequencyBand.valuesSorted()) {
            Label frequenciesLabel = new Label(frequenciesGroup, SWT.LEFT);
            frequenciesLabel.setText(frequencyBand.getText() + ": ");
            
            final Text frequenciesText = new Text(frequenciesGroup, SWT.BORDER | SWT.SINGLE);
            frequenciesText.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
            
            final Button frequenciesButton = new Button(frequenciesGroup, GridData.END);
            frequenciesButton.setText("...");
            
            bandFields.put(frequencyBand, new Pair<Text, Button>(frequenciesText, frequenciesButton));
            
            frequenciesText.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    setFrequencies(frequencyBand, frequenciesText);
                    
                    setPageComplete(canFlipToNextPage());
                }

            });

            frequenciesButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    setFrequencies(frequencyBand, frequenciesButton, frequenciesText);
                }

            });
        }
        
        /** Create BSIC Group */
        Group bsicGroup = new Group(main, SWT.NONE);
        bsicGroup.setLayout(new GridLayout(9, false));
        bsicGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 5));
        bsicGroup.setText("BSIC");
        
        for (int i = 0; i <= AfpModelNew.BSIC_MAX_NUMBER + 1; i++) {
            String text;
            if (i == 0) {
                text = StringUtils.EMPTY;
            }
            else {
                text = Integer.toString(i - 1);
            }
            new Label(bsicGroup, GridData.BEGINNING).setText(text);
        }
        
        new Label(bsicGroup, GridData.BEGINNING).setText("Available NCCs: ");
        for (final String ncc : AfpModelNew.getAvailableBSIC()) {
            final Button nccButton = new Button(bsicGroup, SWT.CHECK);
            nccButton.addSelectionListener(new SelectionListener() {
                
                @Override
                public void widgetSelected(SelectionEvent e) {
                    setNCCSupport(ncc, nccButton.getSelection());
                }
                
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                }
            });
            
            bsicFields.put(ncc, new Pair<Button, Button>(nccButton, null));
        }

        new Label(bsicGroup, GridData.BEGINNING).setText("Available BCCs: ");
        for (final String bcc : AfpModelNew.getAvailableBSIC()) {
            final Button bccButton = new Button(bsicGroup, SWT.CHECK);
            bccButton.addSelectionListener(new SelectionListener() {
                
                @Override
                public void widgetSelected(SelectionEvent e) {
                    setBCCSupport(bcc, bccButton.getSelection());
                }
                
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                }
            });
            
            bsicFields.get(bcc).setRight(bccButton);
        }
        
        setControl(thisParent);
        setPageComplete(true);
    }
    
    private void setNCCSupport(String ncc, boolean value) {
        model.setSupportedNCC(ncc, value);
    }
    
    private void setBCCSupport(String bcc, boolean value) {
        model.setSupportedBCC(bcc, value);
    }
    
    private void setFrequencies(FrequencyBand band, Text frequenciesText) {
        String compressedText = frequenciesText.getText();
        
        FrequencyDomain domain = model.getFreeFrequencyDomains().get(band.getText());
        domain.setSelectedFrequencies(FrequenciesListUtils.decompressString(compressedText));
    }
    
    private void setFrequencies(FrequencyBand band, Button frequenciesButton, Text frequenciesText) {
        FrequencyDomain domain = model.getFreeFrequencyDomains().get(band.getText());
        
        FrequenciesSelectorDialog dialog = new FrequenciesSelectorDialog(getShell(), 
                                              Arrays.asList(band.getSupportedFrequencies()), 
                                              domain);
        
        dialog.open();
        
        frequenciesText.setText(FrequenciesListUtils.compressList(domain.getSelectedFrequencies()));
    }

    @Override
    protected int getStepNumber() {
        return STEP_NUMBER;
    }

    @Override
    public boolean isStepAvailable() {
        return true;
    }
    
    @Override
    protected void refreshPage() {
        super.refreshPage();

        for (FrequencyBand frequencyBand : FrequencyBand.values()) {
            Pair<Text, Button> field = bandFields.get(frequencyBand);
            
            field.left().setEnabled(model.isFrequencyBandSupported(frequencyBand));
            field.right().setEnabled(model.isFrequencyBandSupported(frequencyBand));
        }

        for (String bcc : AfpModelNew.getAvailableBSIC()) {
            bsicFields.get(bcc).right().setEnabled(model.isBCCSupported(bcc));
        }
        
        for (String ncc : AfpModelNew.getAvailableBSIC()) {
            bsicFields.get(ncc).right().setEnabled(model.isNCCSupported(ncc));
        }
    }

}
