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

import org.amanzi.awe.wizards.AnalysisWizard;
import org.amanzi.awe.wizards.kpi.report.KPIReportWizard;
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
public class SelectAggregationPage extends WizardPage {

    public static final String PAGE_ID = SelectAggregationPage.class.getName();
    private Button btnWithoutAggr;
    private Button btnHourly;
    private Button btnDaily;
    private Button btnWeekly;

    private Button btnMonthly;

    public SelectAggregationPage(String pageName) {
        super(pageName);
        setTitle("Select level of time aggregation");
    }

    public SelectAggregationPage() {
        this(PAGE_ID);
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FormLayout());
        Group group = new Group(container, SWT.NONE);
        group.setText("Select time aggregation:");
        group.setLayout(new GridLayout());
        FormData layoutData = new FormData();
        layoutData.top = new FormAttachment(0, 2);
        layoutData.left = new FormAttachment(0, 2);
        layoutData.right = new FormAttachment(100, -2);
        group.setLayoutData(layoutData);

        SelectionAdapter listener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updatePageComplete();
            }

        };
        btnWithoutAggr = new Button(group, SWT.RADIO);
        btnWithoutAggr.setText("without aggregation");
        btnWithoutAggr.addSelectionListener(listener);

        btnHourly = new Button(group, SWT.RADIO);
        btnHourly.setText("hourly");
        btnHourly.addSelectionListener(listener);

        btnDaily = new Button(group, SWT.RADIO);
        btnDaily.setText("daily");
        btnDaily.addSelectionListener(listener);

        // btnWeekly = new Button(group,SWT.RADIO);
        // btnWeekly.setText("weekly");

        btnMonthly = new Button(group, SWT.RADIO);
        btnMonthly.setText("monthly");
        btnMonthly.addSelectionListener(listener);
        
        setPageComplete(true);
        setControl(container);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            btnWithoutAggr.setSelection(true);
            updatePageComplete();
        }
        super.setVisible(visible);
    }

    private void updatePageComplete() {
        AnalysisWizard wiz = (AnalysisWizard)getWizard();
        String aggregation;
        if (btnWithoutAggr.getSelection()) {
            aggregation = "none";
        } else if (btnHourly.getSelection()) {
            aggregation = "hourly";
        } else if (btnDaily.getSelection()) {
            aggregation = "daily";
        } else if (btnMonthly.getSelection()) {
            aggregation = "monthly";
        } else {
            aggregation = "none";
        }
        wiz.setAggregation(aggregation);
    }

}
