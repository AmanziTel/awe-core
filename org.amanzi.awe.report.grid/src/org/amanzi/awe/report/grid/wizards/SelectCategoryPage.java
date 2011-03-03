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

import org.amanzi.awe.report.grid.wizards.GridReportWizard.CategoryType;
import org.amanzi.awe.report.grid.wizards.GridReportWizard.Scope;
import org.amanzi.awe.report.pdf.PDFPrintingEngine;
import org.amanzi.awe.statistics.CallTimePeriods;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;

import com.google.protobuf.DescriptorProtos.FieldOptions.CType;

/**
 * Page for Grid Report Wizard
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class SelectCategoryPage extends WizardPage {

    private Button btnSystem;
    private Button btnSite;
    private Button btnCell;

    public SelectCategoryPage() {
        super(SelectCategoryPage.class.getName());
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(4, false));

        Group groupContainer = new Group(container, SWT.NONE);
        groupContainer.setText("Category selection:");
        groupContainer.setLayout(new GridLayout());


        btnSystem = new Button(groupContainer, SWT.RADIO);
        btnSystem.setText("System");
        btnSystem.setLayoutData(new GridData());
        btnSystem.setSelection(true);
        
        btnSite = new Button(groupContainer, SWT.RADIO);
        btnSite.setText("Site");
        btnSite.setLayoutData(new GridData());
        
        btnCell = new Button(groupContainer, SWT.RADIO);
        btnCell.setText("Cell");
        btnCell.setLayoutData(new GridData());
        

        
        setPageComplete(true);
        setControl(container);
    }

    @Override
    public IWizardPage getNextPage() {
        updateCategory();
        GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
        System.out.println("Category type "+gridReportWizard.getCategoryType());
        if (btnSystem.getSelection()){
            return gridReportWizard.getSelectSystemPage();
        }else if (btnSite.getSelection()){
            return gridReportWizard.getSelectSitePage();
        }else  if (btnCell.getSelection()){
            return gridReportWizard.getSelectCellPage();
        }
        
        return super.getNextPage();
    }

    private void updateCategory() {
        GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
        if (btnSystem.getSelection()) {
            gridReportWizard.setCategoryType(CategoryType.SYSTEM);
        } else if (btnSite.getSelection()) {
            gridReportWizard.setCategoryType(CategoryType.SITE);
        }  else if (btnCell.getSelection()) {
            gridReportWizard.setCategoryType(CategoryType.CELL);
        }
    }
    
}
