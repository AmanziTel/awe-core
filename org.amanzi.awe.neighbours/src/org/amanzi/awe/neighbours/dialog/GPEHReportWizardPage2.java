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
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
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

    /** The ct report type. */
    private CheckboxTableViewer cIntAnalysisType;

    /** The period. */
    private LinkedHashMap<String, CallTimePeriods> period;

    private Button bIntAnalys;

    private Button bRFAnalys;

    private CheckboxTableViewer cRFAnalysisType;

    private Button[] bPeriod;

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
        final Group main = new Group(parent, SWT.FILL | SWT.H_SCROLL);
        // parent.setLayout(new RowLayout());
        GridLayout layout = new GridLayout(3, true);
        // layout.
        main.setLayout(layout);
        main.setText("Select analysis type");
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        // data.heightHint = 100;
        main.setLayoutData(data);
        // new GridLa
        // main.setLayoutData(new GridL)

        bIntAnalys = new Button(main, SWT.CHECK);
        bIntAnalys.setText("Interference Analysis");
        bIntAnalys.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));

        bRFAnalys = new Button(main, SWT.CHECK);
        bRFAnalys.setText("RF Environment Analysis");
        bRFAnalys.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));

        cIntAnalysisType = CheckboxTableViewer.newCheckList(main, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
        createTable(cIntAnalysisType, "int");
        data = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        data.heightHint = 100;
        cIntAnalysisType.getControl().setLayoutData(data);
        cIntAnalysisType.setContentProvider(new GpehReportTypeTableContentProvider());
        cIntAnalysisType.setLabelProvider(new GpehReportTypeTableLabelProvider());

        cRFAnalysisType = CheckboxTableViewer.newCheckList(main, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
        createTable(cRFAnalysisType, "rf");
        data = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        data.heightHint = 100;
        cRFAnalysisType.getControl().setLayoutData(data);
        cRFAnalysisType.setContentProvider(new GpehReportTypeTableContentProvider());
        cRFAnalysisType.setLabelProvider(new GpehReportTypeTableLabelProvider());

        Group grPeriod = new Group(main, SWT.FILL);
        grPeriod.setLayout(new GridLayout(1, true));
        grPeriod.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        grPeriod.setText("Reports Resolution");

        bPeriod = new Button[4];

        bPeriod[0] = new Button(grPeriod, SWT.RADIO);
        bPeriod[0].setText("Total");
        bPeriod[0].setSelection(true);

        bPeriod[1] = new Button(grPeriod, SWT.RADIO);
        bPeriod[1].setText("Daily");

        bPeriod[2] = new Button(grPeriod, SWT.RADIO);
        bPeriod[2].setText("Hourly");

        bPeriod[3] = new Button(grPeriod, SWT.RADIO);
        bPeriod[3].setText("15min");

        ControlAdapter widthListener = new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                Table table = (Table)e.getSource();

                int width = table.getSize().x - table.getBorderWidth() - table.getVerticalBar().getSize().x - table.getColumnCount() * 2;
                table.getColumn(0).setWidth(width);

                // cIntAnalysisType.getTable().getColumn(0).setWidth(width);

                // Point oldSize = cIntAnalysisType.getTable().getSize();
                // if (oldSize.x > area.width) {
                // // table is getting smaller so make the columns
                // // smaller first and then resize the table to
                // // match the client area width
                // column1.setWidth(width/3);
                // column2.setWidth(width - column1.getWidth());
                // cIntAnalysisType.getTable().setSize(area.width, area.height);
                // } else {
                // // table is getting bigger so make the table
                // // bigger first and then make the columns wider
                // // to match the client area width
                // cIntAnalysisType.getTable().setSize(area.width, area.height);
                // column1.setWidth(width/3);
                // column2.setWidth(width - column1.getWidth());
                // }
            }
        };
        cIntAnalysisType.getTable().addControlListener(widthListener);
        cRFAnalysisType.getTable().addControlListener(widthListener);

        ICheckStateListener checkStateListener = new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                validateFinish();
            }
        };

        cIntAnalysisType.addCheckStateListener(checkStateListener);
        cRFAnalysisType.addCheckStateListener(checkStateListener);

        // main.pack();
        // main.setBackground(new Color(main.getDisplay(), 255, 0, 0));
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
        table.setLayout(new GridLayout());
        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setText(columnName);
        // column.setResizable(true);
        // table.setHeaderVisible(true);
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
        return cIntAnalysisType.getCheckedElements().length > 0 || cRFAnalysisType.getCheckedElements().length > 0;
    }

    /**
     * Inits the.
     */
    private void init() {
        formReports();
        validateFinish();
    }

    /**
     * Form reports.
     */
    private void formReports() {
        cIntAnalysisType.setInput(GpehReport.INTERFERENCE_ANALYSIS.getReportypes());
        cRFAnalysisType.setInput(GpehReport.RF_PERFORMANCE_ANALYSIS.getReportypes());
        cRFAnalysisType.setInput(GpehReportType.values());
        cRFAnalysisType.setInput(GpehReportType.values());
        cRFAnalysisType.setInput(GpehReportType.values());
        cRFAnalysisType.setInput(GpehReportType.values());
    }

    /**
     * Gets the period.
     * 
     * @return the period
     */
    public CallTimePeriods getPeriod() {
        int i = 0;
        for (; i < bPeriod.length; i++) {
            if (bPeriod[i].getSelection())
                break;
        }
        switch (i) {
        case 0:
            return CallTimePeriods.ALL;
        case 1:
            return CallTimePeriods.DAILY;
        case 2:
            return CallTimePeriods.HOURLY;
        default:
            return CallTimePeriods.QUATER_HOUR;
        }
    }

    /**
     * Gets the report type.
     * 
     * @return the report type
     */
    public Set<GpehReportType> getReportType() {
        HashSet<GpehReportType> result = new HashSet<GpehReportType>();

        Object[] checked = cIntAnalysisType.getCheckedElements();
        for (int i = 0; i < checked.length; i++) {
            GpehReportType type = ((GpehReportType)checked[i]);
            if (type != null) {
                result.add(type);
            }
        }
        checked = cRFAnalysisType.getCheckedElements();
        for (int i = 0; i < checked.length; i++) {
            GpehReportType type = ((GpehReportType)checked[i]);
            if (type != null) {
                result.add(type);
            }
        }
        return result;
    }

    /**
     * <p>
     * GpehReportTypeTableContentProvider
     * </p>
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
