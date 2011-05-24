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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.amanzi.awe.afp.models.FrequencyDomain;
import org.amanzi.awe.afp.models.parameters.ChannelType;
import org.amanzi.awe.afp.models.parameters.FrequencyBand;
import org.amanzi.awe.afp.models.parameters.OptimizationType;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * Page for Step 1 of AFP Wizard. Optimization Goals
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class Step1OptimizationGoalsPage extends AbstractAfpWizardPage {
    
    private enum SummaryType {
        SECTOR("Selected Sectors"),
        TRX("Selected TRXs"),
        BCCH("BCCH TRXs"),
        TCH("TCH Non/BB Hopping TRXs"),
        SY("TCH SY Hopping TRXs");
        
        private String text;
        
        private SummaryType(String text) {
            this.text = text;
        }
        
        public String getText() {
            return text;
        }
    }
    
    private interface ISummaryData {
        
        public String getHeader();
        
        public String getText(SummaryType summaryType);
        
        public Label getLabel();
        
    }

    private static final String PAGE_NAME = "Optimization Goals";

    private static final int STEP_NUMBER = 1;

    private static final String PAGE_DESCRIPTION = "Step 1 - " + PAGE_NAME;

    private HashMap<OptimizationType, Button> optimizationTypeButtons = new HashMap<OptimizationType, Button>();

    private HashMap<FrequencyBand, Button> bandTypeButtons = new HashMap<FrequencyBand, Button>();

    private HashMap<ChannelType, Button> channelTypeButtons = new HashMap<ChannelType, Button>();
    
    private LinkedList<ISummaryData> summaryData = new LinkedList<Step1OptimizationGoalsPage.ISummaryData>();
    
    private Group summaryGroup;

    /**
     * @param pageName
     * @param wizard
     */
    protected Step1OptimizationGoalsPage(AfpWizard wizard) {
        super(PAGE_NAME, wizard);

        setPageComplete(false);
        setTitle(AfpWizard.WIZARD_TITLE);
        setDescription(PAGE_DESCRIPTION);
        
        initializeHeaders();
    }
    
    private void initializeHeaders() {
        //empty header 
        ISummaryData empty = new ISummaryData() {
            
            private Label label = null;
            
            @Override
            public String getHeader() {
                return StringUtils.EMPTY;
            }
            
            @Override
            public String getText(SummaryType summaryType) {
                return summaryType.getText();
            }

            @Override
            public Label getLabel() {
                if (label == null) {
                    label =  new Label(summaryGroup, SWT.LEFT);
                }
                return label;
            }
        };
        
        //total summary
        ISummaryData totalSummary = new ISummaryData() {
            
            private Label label;
            
            @Override
            public String getHeader() {
                return "Total";
            }
            
            @Override
            public String getText(SummaryType summaryType) {
                int count = 0;
                switch (summaryType) {
                case BCCH:
                    count = model.getChannelCount().get(ChannelType.BCCH);
                    break;
                case SECTOR:
                    count = model.getSectorCount();
                    break;
                case SY:
                    count = model.getChannelCount().get(ChannelType.SY);
                    break;
                case TCH:
                    count = model.getChannelCount().get(ChannelType.TCH);
                    break;
                case TRX:
                    count = model.getTrxCount();
                    break;
                }
                
                return Integer.toString(count);
            }
            
            @Override
            public Label getLabel() {
                if (label == null) {
                    label =  new Label(summaryGroup, SWT.LEFT);
                }
                return label;
            }
        };
        
        summaryData.add(empty);
        summaryData.add(totalSummary);
        
        for (final FrequencyBand band : FrequencyBand.valuesSorted()) {
            ISummaryData bandSummary = new ISummaryData() {
                
                private Label label;
                
                @Override
                public String getHeader() {
                    return band.getText();
                }
                
                @Override
                public String getText(SummaryType summaryType) {
                    FrequencyDomain domain = model.getFreeFrequencyDomains().get(band.getText());
                    
                    int count = 0;
                    
                    switch (summaryType) {
                    case BCCH:
                        count = domain.getChannelCount().get(ChannelType.BCCH);
                        break;
                    case SECTOR:
                        count = domain.getSectorCount();
                        break;
                    case SY:
                        count = domain.getChannelCount().get(ChannelType.SY);
                        break;
                    case TCH:
                        count = domain.getChannelCount().get(ChannelType.TCH);
                        break;
                    case TRX:
                        count = domain.getTrxCount();
                        break;
                    }
                    
                    return Integer.toString(count);
                }
                
                @Override
                public Label getLabel() {
                    if (label == null) {
                        label =  new Label(summaryGroup, SWT.LEFT);
                    }
                    return label;
                }
            };
            
            summaryData.add(bandSummary);
        }
        
        
    }

    @Override
    public void createControl(Composite parent) {
        Composite thisParent = new Composite(parent, SWT.NONE);
        thisParent.setLayout(new GridLayout(2, false));

        createStepsGroup(thisParent, getStepNumber());

        Group main = new Group(thisParent, SWT.NONE);
        main.setLayout(new GridLayout(3, true));
        main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));

        Group paramGroup = new Group(main, SWT.NONE);
        paramGroup.setLayout(new GridLayout(1, false));
        paramGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4));
        paramGroup.setText("Optimization Parameters");

        for (final OptimizationType type : OptimizationType.values()) {
            Button button = new Button(paramGroup, SWT.CHECK);
            button.setText(type.getText());
            button.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    updateOptimizationType(type);
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                }
            });

            optimizationTypeButtons.put(type, button);
        }

        Group frequencyBandGroup = new Group(main, SWT.NONE);
        frequencyBandGroup.setLayout(new GridLayout(1, false));
        frequencyBandGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4));
        frequencyBandGroup.setText("Frequency Band");

        for (final FrequencyBand band : FrequencyBand.values()) {
            Button button = new Button(frequencyBandGroup, SWT.CHECK);
            button.setText(band.getText());
            button.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    updateFrequencyBandSupport(band);
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                }
            });
            bandTypeButtons.put(band, button);
        }

        Group channelGroup = new Group(main, SWT.NONE);
        channelGroup.setLayout(new GridLayout(1, false));
        channelGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4));
        channelGroup.setText("Channel Type");

        for (final ChannelType channel : ChannelType.values()) {
            Button button = new Button(channelGroup, SWT.CHECK);
            button.setText(channel.getText());
            button.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    updateChannelTypeSupport(channel);
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                }
            });
            channelTypeButtons.put(channel, button);
        }

        summaryGroup = new Group(main, SWT.NONE);
        summaryGroup.setLayout(new GridLayout(6, false));
        summaryGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
        summaryGroup.setText("Summary");

        for (ISummaryData summaryHeader : summaryData) {
            Label label = new Label(summaryGroup, SWT.LEFT);
            label.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 1));
            label.setText(summaryHeader.getHeader());
            makeFontBold(label);
        }
        
        setControl(thisParent);
        setPageComplete(true);
    }

    @Override
    protected int getStepNumber() {
        return STEP_NUMBER;
    }

    private void updateOptimizationType(OptimizationType type) {
        model.setOptimizationSupport(type, optimizationTypeButtons.get(type).getSelection());
    }

    private void updateFrequencyBandSupport(FrequencyBand band) {
        model.setFrequencyBandSupport(band, bandTypeButtons.get(band).getSelection());
    }

    private void updateChannelTypeSupport(ChannelType channelType) {
        model.setChannelTypeSupported(channelType, channelTypeButtons.get(channelType).getSelection());
    }

    @Override
    protected void refreshPage() {
        super.refreshPage();

        for (Entry<OptimizationType, Button> entry : optimizationTypeButtons.entrySet()) {
            entry.getValue().setSelection(model.isOptimizationSupported(entry.getKey()));
        }

        for (Entry<FrequencyBand, Button> entry : bandTypeButtons.entrySet()) {
            entry.getValue().setSelection(model.isFrequencyBandSupported(entry.getKey()));
        }

        for (Entry<ChannelType, Button> entry : channelTypeButtons.entrySet()) {
            entry.getValue().setSelection(model.isChannelTypeSupported(entry.getKey()));
        }

        for (SummaryType summaryType : SummaryType.values()) {
            for (ISummaryData summary : summaryData) {
                summary.getLabel().setText(summary.getText(summaryType));
            }
        }
        
        summaryGroup.layout();
    }

}
