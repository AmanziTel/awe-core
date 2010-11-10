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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.List;

/**
 * Page for Grid Report Wizard
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class SelectSitePage extends SelectNetworkElementsPage {

    private Combo cmbKPIs;
    private List cmbAvailableSites;
    private List cmbSelectedSites;
    private Combo cmbCategories;
    private Font errorFont;
    private Color redColor;
    private PropertyListViewer propertyListViewer;

    public SelectSitePage() {
        super(SelectSitePage.class.getName(),"Select site:");
    }

//    @Override
//    public void createControl(Composite parent) {
//        Composite container = new Composite(parent, SWT.NONE);
//        container.setLayout(new GridLayout(4, false));
//
//        final Label lblSelectKPI = new Label(container, SWT.NONE);
//        lblSelectKPI.setText("Select KPI");
//        lblSelectKPI.setLayoutData(new GridData());
//
//        cmbKPIs = new Combo(container, SWT.NONE);
//        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_BEGINNING, true, false, 3, 1);
//        cmbKPIs.setLayoutData(gd);
//        cmbKPIs.setText("Dispatch blocking queue rate");
//        cmbKPIs.addSelectionListener(new SelectionAdapter(){
//
//            @Override
//            public void widgetSelected(SelectionEvent e) {
//                ((GridReportWizard)getWizard()).setKpi(cmbKPIs.getText());
//            }});
//
//        propertyListViewer = new PropertyListViewer(container, "Select site");
////        propertyListViewer.setInput(new String[] {"S238_City_Link_Mall", "S080_Sims_Dr_62", "S161_ShangriLa", "S216_Xilinx",
////                "S027_AMK_601", "S180_WL_Ind_PK_E", "S098_Chai_Chee_54", "S005_Bedok_Ex", "S025_AMK_560", "S176_PioneerCont"});
//        gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_BEGINNING, true, false, 4, 1);
//        propertyListViewer.getContainer().setLayoutData(gd);
//
//        setPageComplete(true);
//        setControl(container);
//    }
//
//    private void updateNetworkElements() {
//        GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
//        Statistics statistics = gridReportWizard.getStatistics();
//        ArrayList<String> sites = new ArrayList<String>();
//        for (StatisticsGroup group : statistics.getGroups().values()) {
//            sites.add(group.getGroupName());
//        }
//        Collections.sort(sites);
//        propertyListViewer.setInput(sites);
//    }
//
//    /**
//     * @param gridReportWizard
//     */
//    private void updateKPIs() {
//        GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
//        ArrayList<String> idenKPIs = gridReportWizard.getIdenKPIs();
//        Collections.sort(idenKPIs);
//        cmbKPIs.setItems(idenKPIs.toArray(new String[] {}));
//        cmbKPIs.setText(cmbKPIs.getItem(0));
//    }
//
//    @Override
//    public void setVisible(boolean visible) {
//        if (visible){
//            GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
//            gridReportWizard.buildStatistics();
//            updateNetworkElements();
//            updateKPIs();
//        }
//        super.setVisible(visible);
//    }
//
//    @Override
//    public IWizardPage getNextPage() {
//        ((GridReportWizard)getWizard()).setSelection(getSelection());
//        ((GridReportWizard)getWizard()).setKpi(cmbKPIs.getText());
//        return ((GridReportWizard)getWizard()).getSelectOutputTypePage();
//    }
//
//    public java.util.List<String> getSelection() {
//        return propertyListViewer.getSelection();
//    }
}
