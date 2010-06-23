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

package org.amanzi.awe.wizards.pages;

import java.util.Arrays;
import java.util.HashMap;

import org.amanzi.awe.wizards.AnalysisWizard;
import org.amanzi.awe.wizards.kpi.report.KPIReportWizard;
import org.amanzi.awe.wizards.utils.DBUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class SelectDatasetPage extends WizardPage {
    private  static final Logger LOGGER = Logger.getLogger(SelectDatasetPage.class);
    
    private static final String ALL_SITES = "all";
    public static final String PAGE_ID = SelectDatasetPage.class.getName();
    private Button btnNetwork;
    private Button btnDrive;
    private Button btnCounters;
    private Combo cmbDataset;
    private Button btnAnalyzeKPIs;
    private Button btnAnalyzeProperties;
    private Button btnAnalyzeCounters;
    private Button btnAnalyzeEvents;
    private HashMap<String, Node> datasets;
    private Combo cmbSite;

    public SelectDatasetPage(String pageName) {
        super(pageName);
        setTitle("Select dataset for analysis");
    }

    public SelectDatasetPage() {
        this(PAGE_ID);
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FormLayout());

        Group grpDatasetType = new Group(container, SWT.NONE);
        grpDatasetType.setText("Select dataset type:");
        grpDatasetType.setLayout(new GridLayout());

        FormData formData = new FormData();
        formData.left = new FormAttachment(0, 2);
        formData.right = new FormAttachment(100, -2);
        // formData.top = new FormAttachment(grpAnalyze, 2);
        // formData.top = new FormAttachment(0, 2);
        grpDatasetType.setLayoutData(formData);

        btnNetwork = new Button(grpDatasetType, SWT.RADIO);
        btnNetwork.setText("network");
        btnNetwork.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateDatasetCombo();
            }

        });

        btnDrive = new Button(grpDatasetType, SWT.RADIO);
        btnDrive.setText("drive");
        btnDrive.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateDatasetCombo();
            }

        });

        btnCounters = new Button(grpDatasetType, SWT.RADIO);
        btnCounters.setText("counters");
        btnCounters.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateDatasetCombo();
            }

        });

        Label lblSelectDataset = new Label(container, SWT.LEFT);
        lblSelectDataset.setText("Select dataset:");

        formData = new FormData();
        formData.left = new FormAttachment(0, 2);
        formData.top = new FormAttachment(grpDatasetType, 2);
        formData.right = new FormAttachment(20, 2);
        lblSelectDataset.setLayoutData(formData);

        cmbDataset = new Combo(container, SWT.READ_ONLY);
        formData = new FormData();
        formData.left = new FormAttachment(lblSelectDataset, 2);
        formData.right = new FormAttachment(100, -2);
        formData.top = new FormAttachment(grpDatasetType, 2);
        cmbDataset.setLayoutData(formData);
        cmbDataset.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateSites();
                boolean needsSelectSite = ((AnalysisWizard)getWizard()).isNeedsSelectSite();
                updatePageComplete(needsSelectSite?!cmbSite.getText().equalsIgnoreCase("all"):true);
            }

        });
        Label lblSelectSite = new Label(container, SWT.LEFT);
        lblSelectSite.setText("Select site:");

        formData = new FormData();
        formData.left = new FormAttachment(0, 2);
        formData.top = new FormAttachment(cmbDataset, 2);
        formData.right = new FormAttachment(20, 2);
        lblSelectSite.setLayoutData(formData);

        cmbSite = new Combo(container, SWT.READ_ONLY);
        formData = new FormData();
        formData.left = new FormAttachment(lblSelectSite, 2);
        formData.right = new FormAttachment(100, -2);
        formData.top = new FormAttachment(cmbDataset, 2);
        cmbSite.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updatePageComplete(true);
            }

        });
        cmbSite.setLayoutData(formData);

        // TODO
        // updateDatasetCombo();
        updatePageComplete(false);

        setControl(container);

    }

    protected void updateSites() {
        String selectedDataset = cmbDataset.getText();
        cmbSite.removeAll();
        if (selectedDataset != null && selectedDataset.length() != 0) {
            Node dataset = datasets.get(selectedDataset);
            String[] sitesFound = DBUtils.getAllSites(dataset).toArray(new String[] {});
            Arrays.sort(sitesFound);
            cmbSite.setItems(sitesFound);
            if (sitesFound.length != 0) {
                cmbSite.add(ALL_SITES, 0);
                cmbSite.setText(ALL_SITES);
            }
        }
    }

    /**
     * Updates combo with available datasets according to user choice
     */
    protected void updateDatasetCombo() {
        if (btnNetwork.getSelection()) {
            datasets = DBUtils.getAllNetworks();
            cmbSite.setVisible(true);
        }
        if (btnDrive.getSelection()) {
            datasets = DBUtils.getAllDrives();
        }
        if (btnCounters.getSelection()) {
            datasets = DBUtils.getAllCounters();
        }

        String[] array = datasets.keySet().toArray(new String[] {});
        Arrays.sort(array);
        cmbDataset.setItems(array);
        cmbSite.removeAll();

    }

    @Override
    public void setVisible(boolean visible) {
        IWizard wizard = getWizard();
        if (wizard instanceof KPIReportWizard) {
            KPIReportWizard wiz = (KPIReportWizard)wizard;
            if (visible) {
                String datasetType = wiz.getDatasetType();
                if (datasetType != null) {
                    if (datasetType.equalsIgnoreCase("network")) {
                        enableOnlyNetworks();

                    } else if (datasetType.equalsIgnoreCase("counters")) {
                        enableOnlyCounters();
                    } else if (datasetType.equalsIgnoreCase("drive")) {
                        enableOnlyDrives();
                    } else {
                        enableOnlyNetworks();
                        btnCounters.setEnabled(false);
                    }
                }
            }
        } else {
            try {
                AnalysisWizard wiz = (AnalysisWizard)wizard;
                switch (wiz.getAnalysisType()) {
                case ANALYZE_COUNTERS:
                    enableOnlyCounters();
                    break;
                case ANALYZE_EVENTS:
                    enableOnlyDrives();
                    break;
                case ANALYZE_KPIS:
                    enableOnlyCounters();
                    break;
                case ANALYZE_PROPERTIES:
                    break;
                default:
                }
            } catch (RuntimeException e) {
                // TODO Handle RuntimeException
                LOGGER.error("Error: ",e);
            }
        }
        updateDatasetCombo();

        super.setVisible(visible);
    }

    /**
     *
     */
    private void enableOnlyNetworks() {
        btnNetwork.setSelection(true);
        btnNetwork.setEnabled(true);
        btnDrive.setEnabled(false);
        btnCounters.setEnabled(false);
    }

    /**
     *
     */
    private void enableOnlyDrives() {
        btnDrive.setSelection(true);
        btnDrive.setEnabled(true);
        btnCounters.setEnabled(false);
        btnNetwork.setEnabled(false);
    }

    /**
     *
     */
    private void enableOnlyCounters() {
        btnCounters.setSelection(true);
        btnCounters.setEnabled(true);
        btnDrive.setEnabled(false);
        btnNetwork.setEnabled(false);
    }

    protected void updatePageComplete(boolean complete) {
        setPageComplete(complete);
        if (complete) {
            AnalysisWizard wiz = (AnalysisWizard)getWizard();
            wiz.setSelectedDataset(cmbDataset.getText());
            StringBuffer datasetScript = new StringBuffer();
            String parameter = wiz.getDatasetType();
            if (parameter.equalsIgnoreCase("counters")) {
                datasetScript.append(parameter).append("('oss'=>'%s'");
                if (!cmbSite.getText().equalsIgnoreCase("all")) {
                    datasetScript.append(",'site_name'=>'%s'");
                }
                datasetScript.append(")");
            }
            wiz.setDatasetScript(datasetScript.toString());
            wiz.setSelectedSite(cmbSite.getText());
            Node dataset = datasets.get(cmbDataset.getText());
            wiz.setAvailableProperties( DBUtils.getProperties(dataset));
        }
    }

    @Override
    public IWizardPage getNextPage() {
        AnalysisWizard wiz = (AnalysisWizard)getWizard();
        switch (wiz.getAnalysisType()) {
        case ANALYZE_COUNTERS:
        case ANALYZE_PROPERTIES:
            return wiz.getPage(SelectPropertyPage.PAGE_ID);
        default:
            return wiz.getPage(SelectAggregationPage.PAGE_ID);
        }
    }
    
}
