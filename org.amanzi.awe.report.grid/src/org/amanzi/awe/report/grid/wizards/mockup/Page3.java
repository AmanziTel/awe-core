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

import org.amanzi.awe.report.pdf.PDFPrintingEngine;
import org.amanzi.awe.statistic.CallTimePeriods;
import org.eclipse.jface.preference.DirectoryFieldEditor;
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

/**
 * Page for Grid Report Wizard
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class Page3 extends WizardPage {

    public Page3() {
        super(Page3.class.getName());
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(4, false));

        Group groupContainer = new Group(container, SWT.NONE);
        groupContainer.setText("KPI selection:");
        groupContainer.setLayout(new GridLayout());


        Button btnSystem = new Button(groupContainer, SWT.RADIO);
        btnSystem.setText("System");
        btnSystem.setLayoutData(new GridData());
        btnSystem.setSelection(true);
        
        Button btnSite = new Button(groupContainer, SWT.RADIO);
        btnSite.setText("Site");
        btnSite.setLayoutData(new GridData());
        
        Button btnCell = new Button(groupContainer, SWT.RADIO);
        btnCell.setText("Cell");
        btnCell.setLayoutData(new GridData());
        

        
        setPageComplete(true);
        setControl(container);
    }
}
