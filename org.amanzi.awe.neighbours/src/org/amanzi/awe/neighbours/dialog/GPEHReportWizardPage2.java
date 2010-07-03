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

package org.amanzi.awe.neighbours.dialog;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.amanzi.awe.neighbours.gpeh.GpehReport;
import org.amanzi.awe.neighbours.gpeh.GpehReportType;
import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.amanzi.neo.wizards.DirectoryEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * <p>
 * GPEHReportWizardPage2
 * </p>
 * .
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class GPEHReportWizardPage2 extends WizardPage {

    /** The c periods. */
    private Combo cPeriods;

    /** The c report. */
    private Combo cReport;

    /** The ct report type. */
    private CheckboxTableViewer ctReportType;

    /** The editor dir. */
    private DirectoryEditor editorDir;

    /** The period. */
    private LinkedHashMap<String, CallTimePeriods> period;

    /** The c file type. */
    private Combo cFileType;

    /**
     * Instantiates a new gPEH report wizard page2.
     * 
     * @param pageName the page name
     * @param pageDescription the page description
     */
    protected GPEHReportWizardPage2(String pageName, String pageDescription) {
        super(pageName);
        setTitle(pageName);
        setDescription(pageDescription);
    }

    /**
     * Creates the control.
     * 
     * @param parent the parent
     */
    @Override
    public void createControl(Composite parent) {
        final Composite main = new Composite(parent, SWT.FILL);
        main.setLayout(new GridLayout(3, false));

        Label label = new Label(main, SWT.NONE);
        label.setText("Report");
        cReport = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData layoutData = new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 1);
        layoutData.minimumWidth = 200;
        cReport.setLayoutData(layoutData);

        label = new Label(main, SWT.NONE);
        label.setText("Report type");

        ctReportType = CheckboxTableViewer.newCheckList(main, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
        GridData data = new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 1);
        data.heightHint = 100;
        data.widthHint = 207;
        createTable(ctReportType, "Avaliable report types");
        ctReportType.getControl().setLayoutData(data);
        ctReportType.setContentProvider(new GpehReportTypeTableContentProvider());
        ctReportType.setLabelProvider(new GpehReportTypeTableLabelProvider());

        label = new Label(main, SWT.NONE);
        label.setText("Report period");
        cPeriods = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 1);
        layoutData.minimumWidth = 200;
        cPeriods.setLayoutData(layoutData);

        SelectionListener listener = new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                GpehReportType[] reportTypes = GpehReport.getEnumById(cReport.getText()).getReportypes();
                ctReportType.setInput(reportTypes);
                ctReportType.refresh();
                validateFinish();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        };
        cReport.addSelectionListener(listener);

        ctReportType.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                validateFinish();
            }
        });

        editorDir = new DirectoryEditor("editor", NeoLoaderPluginMessages.AMSImport_directory, main);
        editorDir.getTextControl(main).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (!editorDir.getTextControl(main).isEnabled()) {
                    return;
                }
                validateFinish();
            }
        });

        label = new Label(main, SWT.NONE);
        label.setText("File type");
        cFileType = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 1);
        layoutData.minimumWidth = 200;
        cFileType.setLayoutData(layoutData);

        setControl(main);

        init();
    }

    /**
     * Creates the table.
     * 
     * @param tableView the table view
     * @param columnName the column name
     */
    private void createTable(TableViewer tableView, String columnName) {
        Table table = tableView.getTable();
        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setWidth(220);
        column.setText(columnName);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
    }

    /**
     * Validate finish.
     */
    private void validateFinish() {
        setPageComplete(isValidPage());
    }

    /**
     * Checks if is valid page.
     * 
     * @return true, if is valid page
     */
    protected boolean isValidPage() {
        try {
            String dir = editorDir.getStringValue();
            File file = new File(dir);
            if (!(file.isAbsolute() && file.exists() && !file.isFile())) {
                return false;
            }
            return ctReportType.getCheckedElements().length > 0;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Inits the.
     */
    private void init() {
        formReports();
        formPeriods();
        formFileTypes();
        validateFinish();
    }

    private void formFileTypes() {
        for (FileType fileType : FileType.values())
            cFileType.add(fileType.toString());
        cFileType.select(0);
        cFileType.setEnabled(false);
    }

    /**
     * Form reports.
     */
    private void formReports() {
        String[] gpeh = new String[GpehReport.values().length];
        int i = 0;
        for (GpehReport report : GpehReport.values()) {
            gpeh[i++] = report.toString();
        }
        cReport.setItems(gpeh);
    }

    /**
     * Form periods.
     */
    private void formPeriods() {
        period = new LinkedHashMap<String, CallTimePeriods>();
        period.put("Hourly", CallTimePeriods.HOURLY);
        period.put("Daily", CallTimePeriods.DAILY);
        period.put("Total", CallTimePeriods.ALL);
        cPeriods.setItems(period.keySet().toArray(new String[0]));
    }

    /**
     * Gets the period.
     * 
     * @return the period
     */
    public CallTimePeriods getPeriod() {
        return period.get(cPeriods.getText());
    }
    
    public FileType getFileType() {
        return FileType.findByString(cFileType.getText());
    }

    /**
     * Gets the report type.
     * 
     * @return the report type
     */
    public Set<GpehReportType> getReportType() {
        HashSet<GpehReportType> result = new HashSet<GpehReportType>();

        Object[] checked = ctReportType.getCheckedElements();
        for (int i = 0; i < checked.length; i++) {
            GpehReportType type = ((GpehReportType)checked[i]);
            if (type != null) {
                result.add(type);
            }
        }
        return result;
    }

    /**
     * Gets the target dir.
     * 
     * @return the target dir
     */
    public String getTargetDir() {
        return editorDir.getStringValue();
    }

    /**
     * <p>
     * GpehReportTypeTableContentProvider
     * </p>
     * .
     * 
     * @author NiCK
     * @since 1.0.0
     */
    private class GpehReportTypeTableContentProvider implements IStructuredContentProvider {

        /** The elements. */
        private final LinkedHashSet<GpehReportType> elements = new LinkedHashSet<GpehReportType>();

        @Override
        public Object[] getElements(Object inputElement) {
            return elements.toArray(new GpehReportType[0]);
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            if (newInput == null) {
                elements.clear();
            } else {
                GpehReportType[] input = (GpehReportType[])newInput;
                elements.clear();
                elements.addAll(Arrays.asList(input));
            }
        }

    }

    /**
     * <p>
     * GpehReportTypeTableLabelProvider
     * </p>
     * .
     * 
     * @author Saelenchits_N
     * @since 1.0.0
     */
    public class GpehReportTypeTableLabelProvider extends LabelProvider {

        @Override
        public Image getImage(Object element) {
            return null;
        }

        @Override
        public String getText(Object element) {
            return element.toString();
        }
    }

    public enum FileType {
        CSV, PDF, XLS;
        public String toString() {
            return this.name().toLowerCase();
        }
        
        public static FileType findByString(String string) {
            if (string == null) {
                return null;
            }
            for (FileType type : FileType.values()) {
                if (type.toString().equals(string.toLowerCase())) {
                    return type;
                }
            }
            return null;
        }

    }

}
