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
import org.amanzi.awe.statistics.CallTimePeriods;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
public class Page0 extends WizardPage {

    public Page0() {
        super(Page0.class.getName());
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(4, false));

        Group groupContainer = new Group(container, SWT.NONE);
        groupContainer.setText("Period selection:");
        groupContainer.setLayout(new GridLayout(2,false));

        Button btnDaily = new Button(groupContainer, SWT.RADIO);
        btnDaily.setText("daily");
        btnDaily.setLayoutData(new GridData());
        btnDaily.setSelection(true);

        DateTime date = new DateTime(groupContainer, SWT.DATE | SWT.LONG);
        date.setLayoutData(new GridData());
        date.setDay(3);
        date.setMonth(10);
        date.setYear(2010);
        date.setEnabled(true);

        Button btnHourly = new Button(groupContainer, SWT.RADIO);
        btnHourly.setText("hourly");
        btnHourly.setLayoutData(new GridData());
        btnHourly.setEnabled(true);
        
        DateTime time = new DateTime(groupContainer, SWT.TIME | SWT.LONG);
        time.setLayoutData(new GridData());
        time.setDay(3);
        time.setMonth(10);
        time.setYear(2010);
        time.setEnabled(true);
        
        setPageComplete(true);
        setControl(container);
    }
}
