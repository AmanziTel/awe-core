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

import org.amanzi.awe.report.grid.wizards.GridReportWizard.PeriodType;
import org.amanzi.awe.report.pdf.PDFPrintingEngine;
import org.amanzi.awe.statistics.CallTimePeriods;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
public class LoadDataPage extends WizardPage {

    private static final String HARMONY = "Harmony";
    private static final String IDEN = "IDEN";
    private static final String SELECT_DIRECTORY = "Select directory";
    private static final String SELECT_OUTPUT_DIRECTORY = "Select output directory";

    private static final String EXPORT_STATISTICS_TO_EXCEL = "Export KPIs to Excel";
    private static final String EXPORT_STATISTICS_TO_PDF = "Export KPIs to PDF";
    private static final String DAILY = "daily";
    private static final String HOURLY = "hourly";
    private static final String SELECT_AGGREGATION = "Select aggregation:";

    
    private DirectoryFieldEditor directoryFieldEditor;
    private DirectoryFieldEditor outputDirectotyEditor;
   

    public LoadDataPage() {
        super(LoadDataPage.class.getName());
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(4, false));

        final ModifyListener listener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                updatePageComplete();
            }
        };
        directoryFieldEditor = new DirectoryFieldEditor("gridDataDir", SELECT_DIRECTORY, container);
        directoryFieldEditor.getTextControl(container).addModifyListener(listener);

        outputDirectotyEditor = new DirectoryFieldEditor("gridDataOutputDir", SELECT_OUTPUT_DIRECTORY, container);
        outputDirectotyEditor.getTextControl(container).addModifyListener(listener);
        outputDirectotyEditor.getTextControl(container).setText(PDFPrintingEngine.DEFAULT_REPORT_DIRECTORY);

        

        setPageComplete(false);
        setControl(container);
    }

    private void updatePageComplete() {
        setPageComplete(outputDirectotyEditor.getStringValue() != null && directoryFieldEditor.getStringValue() != null);
    }

    /**
     * Gets directory
     */
    public String getDirectory() {
        return directoryFieldEditor.getStringValue();
    }

    /**
     * Gets output directory
     */
    public String getOutputDirectory() {
        return outputDirectotyEditor.getStringValue();
    }

    @Override
    public IWizardPage getNextPage() {
        ((GridReportWizard)getWizard()).setDirectory(directoryFieldEditor.getStringValue());
        ((GridReportWizard)getWizard()).setOutputDirectory(outputDirectotyEditor.getStringValue());
        return super.getNextPage();
    }

    
}
