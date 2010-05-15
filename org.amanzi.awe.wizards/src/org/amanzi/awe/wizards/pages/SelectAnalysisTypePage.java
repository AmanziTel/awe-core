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

package org.amanzi.awe.wizards.pages;

import org.amanzi.awe.wizards.AnalysisType;
import org.amanzi.awe.wizards.AnalysisWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class SelectAnalysisTypePage extends WizardPage {
    

    public static final String PAGE_ID = SelectAnalysisTypePage.class.getName();

    private Button btnAnalyzeKPIs;
    private Button btnAnalyzeProperties;
    private Button btnAnalyzeCounters;

    public SelectAnalysisTypePage(String pageName) {
        super(pageName);
    }

    public SelectAnalysisTypePage() {
        this(PAGE_ID);
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FormLayout());

        // group of radio buttons
        Group grpAnalyze = new Group(container, SWT.NONE);
        grpAnalyze.setText("Analyze:");
        grpAnalyze.setLayout(new GridLayout());

        FormData formData = new FormData();
        formData.left = new FormAttachment(0, 2);
        formData.right = new FormAttachment(100, -2);
        grpAnalyze.setLayoutData(formData);
        SelectionAdapter listener = new SelectionAdapter(){

            @Override
            public void widgetSelected(SelectionEvent e) {
               update();
            }
            
        };

        btnAnalyzeKPIs = new Button(grpAnalyze, SWT.RADIO);
        btnAnalyzeKPIs.setText("KPIs");
        btnAnalyzeKPIs.setSelection(true);
        btnAnalyzeKPIs.addSelectionListener(listener);

        // btnAnalyzeProperties = new Button(grpAnalyze, SWT.RADIO);
        // btnAnalyzeProperties.setText("Properties");

        btnAnalyzeCounters = new Button(grpAnalyze, SWT.RADIO);
        btnAnalyzeCounters.setText("Counters");
        btnAnalyzeCounters.addSelectionListener(listener);
        //
        // btnAnalyzeEvents = new Button(grpAnalyze, SWT.RADIO);
        // btnAnalyzeEvents.setText("Events");
        // btnAnalyzeEvents.setEnabled(false);
        //
        setControl(container);
    }

    protected void update() {
        AnalysisWizard wiz = (AnalysisWizard)getWizard();

        if (btnAnalyzeKPIs.getSelection()) {
            wiz.setAnalysisType(AnalysisType.ANALYZE_KPIS);
            wiz.setDatasetType(AnalysisType.ANALYZE_KPIS.getType());
        }
        if (btnAnalyzeCounters.getSelection()) {
            wiz.setAnalysisType(AnalysisType.ANALYZE_COUNTERS);
            wiz.setDatasetType(AnalysisType.ANALYZE_COUNTERS.getType());
        }
    }

    @Override
    public IWizardPage getNextPage() {
        AnalysisWizard wiz = (AnalysisWizard)getWizard();

        if (btnAnalyzeKPIs.getSelection()) {
            return wiz.getPage(SelectKPIPage.PAGE_ID);
        }
        if (btnAnalyzeCounters.getSelection()) {
            return wiz.getPage(SelectDatasetPage.PAGE_ID);
        }
        return super.getNextPage();
    }
    @Override
    public void setVisible(boolean visible) {
        if (visible){
            update();
        }
        super.setVisible(visible);
    }
}
