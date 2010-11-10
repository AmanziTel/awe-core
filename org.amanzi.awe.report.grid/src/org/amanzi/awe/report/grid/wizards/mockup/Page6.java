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

package org.amanzi.awe.report.grid.wizards.mockup;

import org.amanzi.awe.report.grid.wizards.PropertyListViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

/**
 * Page for Grid Report Wizard
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class Page6 extends WizardPage {

    private Combo cmbKPIs;
    private List cmbAvailableSites;
    private List cmbSelectedSites;
    private Combo cmbCategories;
    private Font errorFont;
    private Color redColor;

    public Page6() {
        super(Page6.class.getName());
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
        cmbKPIs.setLayoutData(gd);
        cmbKPIs.setText("Dispatch blocking queue rate");
        
        PropertyListViewer propertyComboViewer = new PropertyListViewer(container,"Select cell");
        propertyComboViewer.setInput(new String[] { "5 2 5 1 2 4 6104", "5 2 5 1 2 2 2880", "5 2 5 1 2 2 2870",
                "5 2 5 1 2 4 6043", "5 2 5 1 2 2 4302"});
        gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_BEGINNING, true, false,4, 1);
        propertyComboViewer.getContainer().setLayoutData(gd);
       
        
//        final Label lblSelectCategory = new Label(container, SWT.NONE);
//        lblSelectCategory.setText("Select category");
//        lblSelectCategory.setLayoutData(new GridData());
//        
//        cmbCategories = new Combo(container, SWT.NONE);
//        gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_BEGINNING, true, false, 3, 1);
//        cmbCategories.setLayoutData(gd);
//        cmbCategories.setItems(new String[]{"all cells","selected cell","top 10 cells", "top 20 cells","top 30 cells"});
//        cmbCategories.setText("all cells");
        
        final Label lblResults = new Label(container, SWT.NONE);
        lblResults.setText("Results:");
        lblResults.setLayoutData(new GridData());
      
        final Label lblSite1 = new Label(container, SWT.BORDER);
        redColor = new Color(getWizard().getContainer().getShell().getDisplay(), 255,0,0);
        lblSite1.setForeground(redColor);
        lblSite1.setText("5 2 5 1 2 2 4302: 15%");
        gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_FILL, true, true, 4, 1);
        gd.minimumWidth=50;
        gd.minimumHeight=50;
        lblSite1.setLayoutData(gd);
        
        final Button lblSite2 = new Button(container, SWT.BORDER| SWT.CHECK);
        lblSite2.setText("5 2 5 1 2 4 6104: 5%");
        gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_FILL, true, true, 4, 1);
        gd.minimumWidth=50;
        gd.minimumHeight=50;
        lblSite2.setLayoutData(gd);
        
// Image image = new Image(parent.getDisplay(), "");
        
        
        
        
        setPageComplete(true);
        setControl(container);
    }
}
