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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.amanzi.awe.neighbours.gpeh.GpehReport;
import org.amanzi.awe.neighbours.gpeh.GpehReportType;
import org.amanzi.awe.statistic.CallTimePeriods;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class GPEHReportWizardPage2 extends WizardPage {
    private Combo cPeriods;
    private Combo cReport;
    private CheckboxTableViewer ctReportType;

    /** The period. */
    // private LinkedHashMap<String, CallTimePeriods> period;
    private LinkedHashMap<String, CallTimePeriods> period;

    /**
     * @param pageName
     */
    protected GPEHReportWizardPage2(String pageName) {
        super(pageName);
    }

    @Override
    public void createControl(Composite parent) {
        final Composite main = new Composite(parent, SWT.FILL);
        main.setLayout(new GridLayout(2, false));

        Label label = new Label(main, SWT.NONE);
        label.setText("Report");
        cReport = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.minimumWidth = 200;
        cReport.setLayoutData(layoutData);

        label = new Label(main, SWT.NONE);
        label.setText("Report type");

        ctReportType = CheckboxTableViewer.newCheckList(main, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
        GridData data = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
        data.heightHint = 100;
        data.widthHint= 207;
        createTable(ctReportType, "Avaliable report types");
        ctReportType.getControl().setLayoutData(data);
        ctReportType.setContentProvider(new GpehReportTypeTableContentProvider());
        ctReportType.setLabelProvider(new GpehReportTypeTableLabelProvider());

        label = new Label(main, SWT.NONE);
        label.setText("Report period");
        cPeriods = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
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

        setControl(main);

        init();
    }

    /**
     * Create table
     * 
     * @param tableView table
     * @param columnName name of column
     */
    private void createTable(TableViewer tableView, String columnName) {
        Table table = tableView.getTable();
        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setWidth(220);
        column.setText(columnName);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
    }

    private void validateFinish() {
        setPageComplete(isValidPage());
    }

    protected boolean isValidPage() {
        return ctReportType.getCheckedElements().length > 0;
    }

    private void init() {
        formReports();
        formPeriods();
        validateFinish();

    }

    /**
     * Form report type.
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
        // period = new LinkedHashMap<String, CallTimePeriods>();
        period = new LinkedHashMap<String, CallTimePeriods>();
        period.put("Hourly", CallTimePeriods.HOURLY);
        period.put("Daily", CallTimePeriods.DAILY);
        period.put("Total", CallTimePeriods.ALL);
        cPeriods.setItems(period.keySet().toArray(new String[0]));
    }

    /**
     * @return
     */
    public CallTimePeriods getPeriod() {
        return period.get(cPeriods.getText());
    }

    /**
     * @return
     */
    public Set<GpehReportType> getReportType() {
        HashSet<GpehReportType> result = new HashSet<GpehReportType>();

        Object[] checked = ctReportType.getCheckedElements();
        for (int i = 0; i < checked.length; i++) {
            GpehReportType type = ((GpehReportType)checked[i]);
            if (type != null){
                result.add(type);
            }
        }
        return result;
    }

    private class GpehReportTypeTableContentProvider implements IStructuredContentProvider {

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

}
