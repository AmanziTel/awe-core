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

package org.amanzi.awe.wizards.gpehreport;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class GPEHReportWizardPage extends WizardPage {

    private Control cPeriods;
    private Control cReportType;
    private Combo cNetwork;
    private Combo cGpeh;

    /**
     * @param pageName
     */
    protected GPEHReportWizardPage(String pageName) {
        super(pageName);
    }

    @Override
    public void createControl(Composite parent) {
        final Composite main = new Composite(parent, SWT.FILL);
        main.setLayout(new GridLayout(3, false));

        Label label = new Label(main, SWT.NONE);
        label.setText("GPEH data");
        cGpeh = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.minimumWidth = 200;
        cGpeh.setLayoutData(layoutData);

        label = new Label(main, SWT.NONE);
        label.setText("Network");
        cNetwork = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.minimumWidth = 200;
        cNetwork.setLayoutData(layoutData);

        label = new Label(main, SWT.NONE);
        label.setText("Report type");
        cReportType = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData();
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

        Button btnCancel = new Button(main, SWT.PUSH);
        btnCancel.setText("Cancel");
        GridData gdBtnCancel = new GridData();
        gdBtnCancel.horizontalAlignment = GridData.CENTER;
        btnCancel.setLayoutData(gdBtnCancel);

        setControl(main);
    }

}
