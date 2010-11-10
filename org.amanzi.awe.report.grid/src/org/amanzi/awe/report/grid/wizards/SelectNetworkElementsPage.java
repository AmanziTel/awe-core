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

import org.amanzi.awe.statistics.database.entity.Statistics;
import org.amanzi.awe.statistics.database.entity.StatisticsGroup;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Page for Grid Report Wizard
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class SelectNetworkElementsPage extends WizardPage {

    private Combo cmbKPIs;
    private String label;
    private PropertyListViewer propertyListViewer;

    public SelectNetworkElementsPage(String pageId, String label) {
        super(pageId);
        this.label = label;
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(2, false));

        final Label lblSelectKPI = new Label(container, SWT.NONE);
        lblSelectKPI.setText("Select KPI");
        lblSelectKPI.setLayoutData(new GridData());

        cmbKPIs = new Combo(container, SWT.NONE);
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_BEGINNING, true, false, 1, 1);
        gd.widthHint=250;
        cmbKPIs.setLayoutData(gd);
        cmbKPIs.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                ((GridReportWizard)getWizard()).setKpi(cmbKPIs.getText());
            }
        });

        propertyListViewer = new PropertyListViewer(container, label);
        gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_BEGINNING, true, false, 2, 1);
        propertyListViewer.getContainer().setLayoutData(gd);

        setPageComplete(true);
        setControl(container);
    }

    private void updateNetworkElements() {
        GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
        Statistics statistics = gridReportWizard.getStatistics();
        ArrayList<String> elements = new ArrayList<String>();
        for (StatisticsGroup group : statistics.getGroups().values()) {
            elements.add(group.getGroupName());
        }
        Collections.sort(elements);
        propertyListViewer.setInput(elements);
    }

    /**
     * @param gridReportWizard
     */
    private void updateKPIs() {
        GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
        ArrayList<String> idenKPIs = gridReportWizard.getIdenKPIs();
        Collections.sort(idenKPIs);
        String previous = cmbKPIs.getText();
        cmbKPIs.setItems(idenKPIs.toArray(new String[] {}));
        cmbKPIs.setText(previous != null && !previous.isEmpty()? previous : cmbKPIs.getItem(0));
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
            gridReportWizard.buildStatistics();
            updateNetworkElements();
            updateKPIs();
        }
        super.setVisible(visible);
    }

    @Override
    public IWizardPage getNextPage() {
        ((GridReportWizard)getWizard()).setSelection(getSelection());
        ((GridReportWizard)getWizard()).setKpi(cmbKPIs.getText());
        return ((GridReportWizard)getWizard()).getSelectOutputTypePage();
    }

    public java.util.List<String> getSelection() {
        return propertyListViewer.getSelection();
    }
}
