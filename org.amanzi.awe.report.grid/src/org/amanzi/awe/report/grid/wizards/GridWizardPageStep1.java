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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * Page for Grid Report Wizard
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class GridWizardPageStep1 extends WizardPage {

    private static final String HARMONY = "Harmony";
    private static final String IDEN = "IDEN";
    private static final String SELECT_DIRECTORY = "Select directory";
    private static final String SELECT_OUTPUT_DIRECTORY = "Select output directory";

    private static final String EXPORT_STATISTICS_TO_EXCEL = "Export KPIs to Excel";
    private static final String EXPORT_STATISTICS_TO_PDF = "Export KPIs to PDF";
    private static final String DAILY = "daily";
    private static final String HOURLY = "hourly";
    private static final String SELECT_AGGREGATION = "Select aggregation:";

    private Button btnXLS;
    private Button btnPDF;

    private Button btnHourly;
    private Button btnDaily;
    private DirectoryFieldEditor directoryFieldEditor;
    private DirectoryFieldEditor outputDirectotyEditor;
    private Button btnNetwork;
    private Button btnSite;
    private Button btnCell;

    public GridWizardPageStep1(String pageName) {
        super(pageName);
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

        Group systemType = new Group(container, SWT.NONE);
        systemType.setText("Select the system:");
        systemType.setLayout(new GridLayout());

        Button btnIden = new Button(systemType, SWT.RADIO);
        btnIden.setText(IDEN);
        btnIden.setLayoutData(new GridData());
        btnIden.setSelection(true);

        Button btnHarmony = new Button(systemType, SWT.RADIO);
        btnHarmony.setText(HARMONY);
        btnHarmony.setLayoutData(new GridData());
        btnHarmony.setEnabled(false);

        Group aggregationTypeGroup = new Group(container, SWT.NONE);
        aggregationTypeGroup.setText(SELECT_AGGREGATION);
        aggregationTypeGroup.setLayout(new GridLayout());

        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 3;

        aggregationTypeGroup.setLayoutData(gd);

        btnHourly = new Button(aggregationTypeGroup, SWT.RADIO);
        btnHourly.setSelection(true);
        btnHourly.setText(HOURLY);
        btnHourly.setLayoutData(new GridData());

        btnDaily = new Button(aggregationTypeGroup, SWT.RADIO);
        btnDaily.setText(DAILY);
        btnDaily.setLayoutData(new GridData());

        Group aggregationLevel = new Group(container, SWT.NONE);
        aggregationLevel.setText("Select network level:");
        aggregationLevel.setLayout(new GridLayout());

        btnNetwork = new Button(aggregationLevel, SWT.RADIO);
        btnNetwork.setText("network");
        btnNetwork.setLayoutData(new GridData());

        btnSite = new Button(aggregationLevel, SWT.RADIO);
        btnSite.setText("site");
        btnSite.setLayoutData(new GridData());
        btnSite.setSelection(true);

        btnCell = new Button(aggregationLevel, SWT.RADIO);
        btnCell.setText("cell");
        btnCell.setLayoutData(new GridData());

        Composite settings = new Composite(container, SWT.NONE);
        gd = new GridData();
        gd.horizontalSpan = 4;
        settings.setLayoutData(gd);
        settings.setLayout(new GridLayout());

        btnXLS = new Button(settings, SWT.CHECK);
        btnXLS.setText(EXPORT_STATISTICS_TO_EXCEL);
        btnXLS.setSelection(true);
        btnXLS.setLayoutData(new GridData());

        btnPDF = new Button(settings, SWT.CHECK);
        btnPDF.setText(EXPORT_STATISTICS_TO_PDF);
        btnPDF.setLayoutData(new GridData());

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

    /**
     * Gets aggregation
     * 
     * @return aggregation
     */
    public CallTimePeriods getAggregation() {
        if (btnHourly.getSelection()) {
            return CallTimePeriods.HOURLY;
        } else {
            return CallTimePeriods.DAILY;

        }
    }

    public String getNetworkLevel() {
        if (btnSite.getSelection()) {
            return "site";
        } else if (btnCell.getSelection()) {
            return "cell";
        }
        return "network";
    }

    /**
     * Is export to Excel required
     * 
     * @return true if export to XLS is required
     */
    public boolean isExportToXlsRequired() {
        return btnXLS.getSelection();
    }

    /**
     * Is export to PDF required
     * 
     * @return true if export to PDF is required
     */
    public boolean isExportToPdfRequired() {
        return btnPDF.getSelection();
    }

}
