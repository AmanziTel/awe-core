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

package org.amanzi.awe.neighbours.dialog;

import java.util.LinkedHashMap;

import org.amanzi.awe.neighbours.gpeh.GpehReportType;
import org.amanzi.awe.statistic.CallTimePeriods;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class GPEHReportWizardPage2 extends WizardPage {
    private Combo cPeriods;
    private Combo cReportType;
    
    /** The period. */
//  private LinkedHashMap<String, CallTimePeriods> period;
    private LinkedHashMap<String, CallTimePeriods> period;


    /**
     * @param pageName
     */
    protected GPEHReportWizardPage2(String pageName) {
        super(pageName);
    }

    @Override
    public void createControl(Composite parent) {
        final Composite main = new Composite(parent, SWT.FILL);
        main.setLayout(new GridLayout(3, false));

        Label label = new Label(main, SWT.NONE);
        label.setText("Report type");
        cReportType = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.minimumWidth = 200;
        cReportType.setLayoutData(layoutData);

        label = new Label(main, SWT.NONE);
        label.setText("Report period");
        cPeriods = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.minimumWidth = 200;
        cPeriods.setLayoutData(layoutData);

        SelectionListener listener = new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                validateFinish();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        };
        cReportType.addSelectionListener(listener);

        setControl(main);

        init();
    }

    private void validateFinish() {
        setPageComplete(isValidPage());
    }

    protected boolean isValidPage() {
        return GpehReportType.getEnumById(cReportType.getText()) != null;
    }

    private void init() {
        formReportType();
        formPeriods();
        validateFinish();

    }

    /**
     * Form report type.
     */
    private void formReportType() {
        String[] gpeh = new String[GpehReportType.values().length];
        int i = 0;
        for (GpehReportType report : GpehReportType.values()) {
            gpeh[i++] = report.getId();
        }
        cReportType.setItems(gpeh);
    }

    /**
     * Form periods.
     */
    private void formPeriods() {
        // period = new LinkedHashMap<String, CallTimePeriods>();
        period = new LinkedHashMap<String, CallTimePeriods>();
        period.put("Hourly", CallTimePeriods.HOURLY);
        period.put("Daily", CallTimePeriods.DAILY);
        period.put("Total", CallTimePeriods.ALL);
        cPeriods.setItems(period.keySet().toArray(new String[0]));
    }

    /**
     * @return
     */
    public CallTimePeriods getPeriod() {
        return period.get(cPeriods.getText());
    }

    /**
     * @return
     */
    public GpehReportType getReportType() {
        return GpehReportType.getEnumById(cReportType.getText());
    }

}
