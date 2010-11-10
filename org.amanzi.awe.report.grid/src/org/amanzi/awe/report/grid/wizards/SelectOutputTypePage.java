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

import org.amanzi.awe.report.grid.wizards.GridReportWizard.AnalysisType;
import org.amanzi.awe.report.grid.wizards.GridReportWizard.OutputType;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * Page for Grid Report Wizard
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class SelectOutputTypePage extends WizardPage {

    private Button btnXLS;
    private Button btnPDF;
    private Button btnJpeg;

    public SelectOutputTypePage() {
        super(SelectOutputTypePage.class.getName());
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(4, false));

        Group groupContainer = new Group(container, SWT.NONE);
        groupContainer.setText("Select output type:");
        groupContainer.setLayout(new GridLayout());

        btnXLS = new Button(groupContainer, SWT.RADIO);
        btnXLS.setText("Output to excel");
        btnXLS.setLayoutData(new GridData());
        btnXLS.setSelection(true);

        btnPDF = new Button(groupContainer, SWT.RADIO);
        btnPDF.setText("Output as pdf");
        btnPDF.setLayoutData(new GridData());
        
        btnJpeg = new Button(groupContainer, SWT.RADIO);
        btnJpeg.setText("Output as jpg");
        btnJpeg.setLayoutData(new GridData());
        
        
        setPageComplete(true);
        setControl(container);
    }

    @Override
    public void setVisible(boolean visible) {
        System.out.println(((GridReportWizard)getWizard()).getSelection());
        super.setVisible(visible);
    }

    @Override
    public IWizardPage getNextPage() {
        updateOutputType();
        return super.getNextPage();
    }

    private void updateOutputType() {
        GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
        if (btnPDF.getSelection()) {
            gridReportWizard.setOutputType(OutputType.PDF);
        } else if (btnXLS.getSelection()) {
            gridReportWizard.setOutputType(OutputType.XLS);
        } else if (btnJpeg.getSelection()) {
            gridReportWizard.setOutputType(OutputType.PNG);
        }
    }
    
}
