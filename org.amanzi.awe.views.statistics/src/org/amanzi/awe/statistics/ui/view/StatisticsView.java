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

package org.amanzi.awe.statistics.ui.view;

import java.util.Calendar;

import org.amanzi.awe.statistics.ui.Messages;
import org.amanzi.awe.statistics.ui.StatisticsPlugin;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

/**
 * <p>
 * Statistics view
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsView extends ViewPart {

    private static final ControlsFactory CONTROLS_FACTORY = ControlsFactory.getInstance();
    private static final Logger LOGGER = Logger.getLogger(StatisticsView.class);
    /*
     * components
     */
    private Label lDataset;
    private Combo cDataset;
    private Button bRefreshDatasets;
    private Label lTemplate;
    private Combo cTemplate;
    private Label lAggregation;
    private Combo cAggreagation;
    private Button bBuild;

    private Label lPeriod;
    private Combo cPeriod;
    private Label lStartTime;
    private DateTime dDateStart;
    private DateTime dTimeStart;
    private Label lEndTime;
    private DateTime dDateEnd;
    private DateTime dTimeEnd;
    private Button bResetStart;
    private Button bResetEnd;
    private Button bReport;
    private Button bExport;
    private Button bChartView;
    private TableViewer tableViewer;
    /*
     * composites
     */
    private Composite mainComposite;
    private Composite controlComposite;
    private Composite topControlsComposite;
    private Composite bottomControlsComposite;

    @Override
    public void createPartControl(Composite parent) {
        LOGGER.info("Create statistics view");
        createComposites(parent);
        createComponents();
        fillComponents();
        addListeners();
    }

    /**
     * add listener for controls
     */
    private void addListeners() {
        LOGGER.info("added listeners to components");
        cDataset.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setEnabled(true, lTemplate, cTemplate);
                setDateTimes();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cTemplate.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setEnabled(true, lAggregation, cAggreagation);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cAggreagation.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setEnabled(true, lPeriod, cPeriod, lStartTime, dDateStart, dTimeStart, lEndTime, dDateEnd, dTimeEnd);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetDefaultSelected(e);
            }
        });
        cPeriod.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setEnabled(true, bBuild);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetDefaultSelected(e);
            }
        });
    }

    /**
     * set date time when new dataset selected
     */
    private void setDateTimes() {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        /*
         * TODO KV: initialize with dataset timestamp values
         */
        dDateStart.setDate(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DATE));
        dTimeStart.setHours(start.get(Calendar.HOUR_OF_DAY));
        dTimeStart.setSeconds(start.get(Calendar.SECOND));
        dDateStart.setDate(end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DATE));
        dTimeStart.setHours(end.get(Calendar.HOUR_OF_DAY));
        dTimeStart.setSeconds(end.get(Calendar.SECOND));
    }

    /**
     * create components
     */
    private void createComponents() {
        LOGGER.info("create views components");
        /*
         * top controls
         */
        lDataset = CONTROLS_FACTORY.getLabel(topControlsComposite, Messages.statisticsViewLabel_DATASET);
        cDataset = CONTROLS_FACTORY.getCombobox(topControlsComposite);
        bRefreshDatasets = CONTROLS_FACTORY.getButton(topControlsComposite, StringUtils.EMPTY);
        bRefreshDatasets.setImage(StatisticsPlugin.getImageDescriptor(Messages.PATH_TO_REFRESH_BUTTON_IMG).createImage());
        lTemplate = CONTROLS_FACTORY.getLabel(topControlsComposite, Messages.statisticsViewLabel_TEMPLATE);
        cTemplate = CONTROLS_FACTORY.getCombobox(topControlsComposite);
        lAggregation = CONTROLS_FACTORY.getLabel(topControlsComposite, Messages.statisticsViewLabel_AGGREGATION);
        cAggreagation = CONTROLS_FACTORY.getCombobox(topControlsComposite);
        bBuild = CONTROLS_FACTORY.getButton(topControlsComposite, Messages.statisticsViewLabel_BUILD);
        /*
         * bottom controls
         */
        lPeriod = CONTROLS_FACTORY.getLabel(bottomControlsComposite, Messages.statisticsViewLabel_PERIOD);
        cPeriod = CONTROLS_FACTORY.getCombobox(bottomControlsComposite);
        lStartTime = CONTROLS_FACTORY.getLabel(bottomControlsComposite, Messages.statisticsViewLabel_START_TIME);
        dDateStart = CONTROLS_FACTORY.getDateTime(bottomControlsComposite);
        dTimeStart = CONTROLS_FACTORY.getDateTime(bottomControlsComposite);
        bResetStart = CONTROLS_FACTORY.getButton(bottomControlsComposite, Messages.statisticsViewLabel_RESET_BUTTON);
        lEndTime = CONTROLS_FACTORY.getLabel(bottomControlsComposite, Messages.statisticsViewLabel_END_TIME);
        dDateEnd = CONTROLS_FACTORY.getDateTime(bottomControlsComposite);
        dTimeEnd = CONTROLS_FACTORY.getDateTime(bottomControlsComposite);
        bResetEnd = CONTROLS_FACTORY.getButton(bottomControlsComposite, Messages.statisticsViewLabel_RESET_BUTTON);
        bReport = CONTROLS_FACTORY.getButton(bottomControlsComposite, Messages.statisticsViewLabel_REPORT);
        bExport = CONTROLS_FACTORY.getButton(bottomControlsComposite, Messages.statisticsViewLabel_EXPORT);
        bChartView = CONTROLS_FACTORY.getButton(bottomControlsComposite, Messages.statisticsViewLabel_CHART_VIEW);
        setEnabled(false, mainComposite);
        setEnabled(true, lDataset, cDataset);
    }

    /**
     * set enable aggregation to composites elements
     * 
     * @param aggregation
     * @param composites
     */
    protected void setEnabled(boolean aggregation, Composite... composites) {
        for (Composite composite : composites) {
            for (Control element : composite.getChildren()) {
                if (element.getClass() == mainComposite.getClass()) {
                    setEnabled(aggregation, (Composite)element);
                } else {
                    setEnabled(aggregation, element);
                }
            }
        }
    }

    /**
     * set enable aggregation to controls elements
     * 
     * @param aggregation
     * @param composites
     */
    protected void setEnabled(boolean aggregation, Control... controls) {
        for (Control element : controls) {
            element.setEnabled(aggregation);
        }
    }

    /**
     * put components to composites
     */
    private void fillComponents() {
        LOGGER.info("layout and fill components");
        Control[] topControls = {lDataset, cDataset, bRefreshDatasets, lTemplate, cTemplate, lAggregation, cAggreagation, bBuild};
        Control[] bottomControls = {lPeriod, cPeriod, lStartTime, dDateStart, dTimeStart, bResetStart, lEndTime, dDateEnd,
                dTimeEnd, bResetEnd, bReport, bExport, bChartView};
        Control[] elements = new Control[topControls.length + bottomControls.length];
        System.arraycopy(topControls, NumberUtils.INTEGER_ZERO, elements, NumberUtils.INTEGER_ZERO, topControls.length);
        System.arraycopy(bottomControls, NumberUtils.INTEGER_ZERO, elements, topControls.length, bottomControls.length);

        for (Control control : elements) {
            GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, true);
            control.setLayoutData(gridData);
        }
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        // cDataset.add("Hello");
        // cAggreagation.add("ololo");
        // cTemplate.add("hi");
        // cPeriod.add(Period.HOURLY.getId());
        tableViewer.getTable().setLayoutData(gridData);
        tableViewer.setLabelProvider(new StatisticsLabelProvider());
        tableViewer.setContentProvider(new StatisticsContentProvider());
        tableViewer.setInput(StringUtils.EMPTY);
    }

    /**
     * create view composites
     * 
     * @param parent
     */
    private void createComposites(Composite parent) {
        LOGGER.info("init views composite");
        mainComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        mainComposite.setLayout(layout);

        controlComposite = new Composite(mainComposite, SWT.NONE);
        layout = new GridLayout(1, false);
        GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
        controlComposite.setLayout(layout);
        controlComposite.setLayoutData(gridData);

        topControlsComposite = new Composite(controlComposite, SWT.NONE);
        gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
        layout = new GridLayout(8, false);
        layout.verticalSpacing = NumberUtils.INTEGER_ZERO;
        topControlsComposite.setLayout(layout);
        topControlsComposite.setLayoutData(gridData);

        bottomControlsComposite = new Composite(controlComposite, SWT.FILL);
        gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
        layout = new GridLayout(13, false);
        layout.marginTop = -5;
        layout.verticalSpacing = NumberUtils.INTEGER_ZERO;
        bottomControlsComposite.setLayout(layout);
        bottomControlsComposite.setLayoutData(gridData);

        // ------- table
        tableViewer = new TableViewer(mainComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

    }

    /**
     * TODO Purpose of StatisticsView
     * <p>
     * label provider for statistics table
     * </p>
     * 
     * @author Vladislav_Kondratenko
     * @since 1.0.0
     */
    class StatisticsLabelProvider implements ITableLabelProvider {

        @Override
        public void addListener(ILabelProviderListener listener) {
        }

        @Override
        public void dispose() {
        }

        @Override
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        @Override
        public void removeListener(ILabelProviderListener listener) {
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            return null;
        }

    }

    /**
     * TODO Purpose of StatisticsView
     * <p>
     * Statistics Table content Provider
     * </p>
     * 
     * @author Vladislav_Kondratenko
     * @since 1.0.0
     */
    class StatisticsContentProvider implements IStructuredContentProvider {

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return new String[0];
        }
    }

    @Override
    public void setFocus() {
    }

}
