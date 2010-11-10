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

package org.amanzi.awe.report.grid.wizards;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * Page for Grid Report Wizard
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class SystemSummaryPage extends WizardPage {

    private Combo cmbKPIs;

    public SystemSummaryPage() {
        super(SystemSummaryPage.class.getName());
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(4, false));

        final Label lblSelectKPI = new Label(container, SWT.NONE);
        lblSelectKPI.setText("Select KPI");
        lblSelectKPI.setLayoutData(new GridData());

        cmbKPIs = new Combo(container, SWT.NONE);
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_BEGINNING, true, false, 3, 1);
        gd.widthHint=250;
        cmbKPIs.setLayoutData(gd);
        
        setPageComplete(true);
        setControl(container);
    }
    
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
            gridReportWizard.buildStatistics();
            updateKPIs();
        }
        super.setVisible(visible);
    }

    private void updateKPIs() {
        GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
        ArrayList<String> idenKPIs = gridReportWizard.getIdenKPIs();
        Collections.sort(idenKPIs);
        String previous = cmbKPIs.getText();
        cmbKPIs.setItems(idenKPIs.toArray(new String[] {}));
        cmbKPIs.setText(previous != null && !previous.isEmpty()? previous : cmbKPIs.getItem(0));
    }
    @Override
    public IWizardPage getNextPage() {
        ((GridReportWizard)getWizard()).setKpi(cmbKPIs.getText());
        return ((GridReportWizard)getWizard()).getSelectOutputTypePage();
    }
}
