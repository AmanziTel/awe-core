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
package org.amanzi.awe.views.drive.views;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.graphics.Glyph;

import org.amanzi.awe.catalog.neo.GeoConstant;
import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.PropertyHeader;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.geotools.brewer.color.BrewerPalette;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser.Order;

/**
 * <p>
 * Drive Inquirer View
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class DriveInquirerView extends ViewPart {

    /** Color COLOR_RIGHT_PROPERTY field */
    private static final Color COLOR_RIGHT_PROPERTY = Color.red;
    /** Color COLOR_LEFT_PROPERTY field */
    private static final Color COLOR_LEFT_PROPERTY = Color.black;
    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.amanzi.awe.views.drive_inquirer.views.DriveInquirerView";
    private static final String CHART_TITLE = "Drive";
    private static final String TITLE_X_AXIS = "Time";
    private static final String LOG_LABEL = "Logarithmic counts";
    private static final String PALETTE_LABEL = "Palette";
    private static final String ALL_EVENTS = "all events";
    protected static final String EVENT = "event_type";
    private JFreeChart chart;
    private ChartComposite chartFrame;
    private Combo cDrive;
    private Combo cEvent;
    private Combo cProperty1;
    private Combo cProperty2;
    private Button bLeft;
    private Button bLeftHalf;
    private Button bRight;
    private Button bRightHalf;
    private LinkedHashMap<String, Node> gisDriveNodes;
    private DateTime dateStart;
    private Spinner sLength;
    private Label lLogarithmic;
    private Button bLogarithmic;
    private Label lPalette;
    private Combo cPalette;
    private Composite buttonLine;
    private TimeDataset xydataset1;
    private TimeDataset xydataset2;
    private LogarithmicAxis axisLog1;
    private ValueAxis axisNumeric1;
    private ValueAxis axisNumeric2;
    private LogarithmicAxis axisLog2;
    private ArrayList<String> eventList;
    private DateAxis domainAxis;
    private Long beginGisTime;
    private Long endGisTime;
    private List<Node> dataset;
    private int currentIndex;
    private TableViewer table;
    private TableLabelProvider labelProvider;
    private TableContentProvider provider;

    public void createPartControl(Composite parent) {
        Composite frame = new Composite(parent, SWT.FILL);
        FormLayout formLayout = new FormLayout();
        formLayout.marginHeight = 0;
        formLayout.marginWidth = 0;
        formLayout.spacing = 0;
        frame.setLayout(formLayout);

        Composite child = new Composite(frame, SWT.FILL);
        final GridLayout layout = new GridLayout(12, false);
        child.setLayout(layout);
        Label label = new Label(child, SWT.FLAT);
        label.setText("Drive:");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cDrive = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);
        cDrive.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        label = new Label(child, SWT.FLAT);
        label.setText("Event:");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cEvent = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);
        cEvent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        label = new Label(child, SWT.NONE);
        label.setText("Property1:");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cProperty1 = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);
        cProperty1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        label = new Label(child, SWT.FLAT);
        label.setText("Property2:");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cProperty2 = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);
        cProperty2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        label = new Label(child, SWT.FLAT);
        label.setText("Start Time:");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        dateStart = new DateTime(child, SWT.FILL | SWT.BORDER | SWT.TIME | SWT.LONG);
        dateStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        label = new Label(child, SWT.FLAT);
        label.setText("Length:");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        sLength = new Spinner(child, SWT.BORDER);
        sLength.setMinimum(1);
        sLength.setMaximum(1000);
        sLength.setSelection(5);
        dateStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        chart = createChart();
        chartFrame = new ChartComposite(frame, SWT.NONE, chart, true);
        FormData fData = new FormData();
        fData.top = new FormAttachment(child, 2);
        fData.left = new FormAttachment(0, 2);
        fData.right = new FormAttachment(100, -2);
        fData.bottom = new FormAttachment(70, -2);

        chartFrame.setLayoutData(fData);
        table = new TableViewer(frame, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
        fData = new FormData();
        fData.left = new FormAttachment(0, 0);
        fData.right = new FormAttachment(100, 0);
        fData.top = new FormAttachment(chartFrame, 2);
        fData.bottom = new FormAttachment(100, -30);
        table.getControl().setLayoutData(fData);

        labelProvider = new TableLabelProvider();
        labelProvider.createTableColumn();
        provider = new TableContentProvider();
        table.setContentProvider(provider);

        buttonLine = new Composite(frame, SWT.NONE);
        fData = new FormData();
        fData.left = new FormAttachment(0, 2);
        fData.right = new FormAttachment(100, -2);
        fData.bottom = new FormAttachment(100, -2);
        buttonLine.setLayoutData(fData);
        formLayout = new FormLayout();
        buttonLine.setLayout(formLayout);

        bLeft = new Button(buttonLine, SWT.PUSH);
        bLeft.setText("<<");
        bLeftHalf = new Button(buttonLine, SWT.PUSH);
        bLeftHalf.setText("<");

        bRight = new Button(buttonLine, SWT.PUSH);
        bRight.setText(">>");
        bRightHalf = new Button(buttonLine, SWT.PUSH);
        bRightHalf.setText(">");

        FormData formData = new FormData();
        formData.left = new FormAttachment(0, 5);
        bLeft.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(bLeft, 5);
        bLeftHalf.setLayoutData(formData);

        formData = new FormData();
        formData.right = new FormAttachment(100, -5);
        bRight.setLayoutData(formData);

        formData = new FormData();
        formData.right = new FormAttachment(bRight, -5);
        bRightHalf.setLayoutData(formData);

        lLogarithmic = new Label(buttonLine, SWT.NONE);
        lLogarithmic.setText(LOG_LABEL);
        bLogarithmic = new Button(buttonLine, SWT.CHECK);
        bLogarithmic.setSelection(false);
        lPalette = new Label(buttonLine, SWT.NONE);
        lPalette.setText(PALETTE_LABEL);
        cPalette = new Combo(buttonLine, SWT.DROP_DOWN | SWT.READ_ONLY);
        cPalette.setItems(PlatformGIS.getColorBrewer().getPaletteNames());
        cPalette.select(0);

        FormData dCombo = new FormData();
        dCombo.left = new FormAttachment(bLeftHalf, 10);
        dCombo.top = new FormAttachment(bLeftHalf, 0, SWT.CENTER);
        bLogarithmic.setLayoutData(dCombo);

        FormData dLabel = new FormData();
        dLabel.left = new FormAttachment(bLogarithmic, 2);
        dLabel.top = new FormAttachment(bLogarithmic, 5, SWT.CENTER);
        lLogarithmic.setLayoutData(dLabel);

        dCombo = new FormData();
        dCombo.left = new FormAttachment(lLogarithmic, 10);
        dCombo.top = new FormAttachment(cPalette, 5, SWT.CENTER);
        lPalette.setLayoutData(dCombo);

        dCombo = new FormData();
        dCombo.left = new FormAttachment(lPalette, 2);
        cPalette.setLayoutData(dCombo);

        setsVisible(false);
        cDrive.setItems(getDriveItems());

        addListeners();

    }

    /**
     * set chart visible
     * 
     * @param visible - is visible?
     */
    private void setsVisible(boolean visible) {
        chartFrame.setVisible(visible);
        table.getControl().setVisible(visible);
        buttonLine.setVisible(visible);
    }

    /**
     *add listeners
     */
    private void addListeners() {
        cDrive.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                changeDrive();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetDefaultSelected(e);
            }
        });
        SelectionListener listener = new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateProperty();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        };
        cEvent.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateChart();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cProperty1.addSelectionListener(listener);
        cProperty2.addSelectionListener(listener);
        dateStart.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                changeDate();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        bLogarithmic.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                logarithmicSelection();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        bRightHalf.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                rightHalf();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        sLength.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateChart();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        bRight.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                right();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        bLeft.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                left();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        bLeftHalf.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                leftHalf();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cPalette.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                fireEventUpdateChart();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        // chart.addChangeListener(new ChartChangeListener() {
        //
        // @Override
        // public void chartChanged(ChartChangeEvent chartchangeevent) {
        // table.setInput(0);
        // table.refresh();
        // }
        // });
        chart.addProgressListener(new ChartProgressListener() {

            @Override
            public void chartProgress(ChartProgressEvent chartprogressevent) {
                if (chartprogressevent.getType() != 2) {
                    return;
                }
                table.setInput(0);
                table.refresh();
            }
        });
    }

    /**
     *begin time+=length
     */
    protected void rightHalf() {
        Long time = getBeginTime();
        int length = getLength();
        time += length / 2;
        if (time > endGisTime) {
            nextGis();
        } else {
            setBeginTime(time);
            updateChart();
        }
    }

    /**
     * Sets begin time
     * 
     * @param time - time
     */
    private void setBeginTime(Long time) {
        Date date = new Date(time);
        dateStart.setHours(date.getHours());
        dateStart.setMinutes(date.getMinutes());
        dateStart.setSeconds(date.getSeconds());
    }

    /**
     * go to right
     */
    private void right() {
        long time = Math.max(beginGisTime, endGisTime - getLength());
        if (getBeginTime() >= time) {
            nextGis();
        } else {
            setBeginTime(time);
            updateChart();
        }

    }

    /**
     * begin time-=length
     */
    private void leftHalf() {
        Long time = getBeginTime();
        int length = getLength();
        time -= length / 2;
        if (time < beginGisTime) {
            previosGis();
        } else {
            setBeginTime(time);
            updateChart();
        }
    }

    /**
     * go to left
     */
    private void left() {
        long time = beginGisTime;
        if (getBeginTime() <= time) {
            previosGis();
        } else {
            setBeginTime(time);
            updateChart();
        }

    }

    /**
     *go to previos gis node
     */
    private void previosGis() {
        int index = currentIndex;
        int size = dataset.size();
        if (index < 0 || size < 2) {
            return;
        }
        index--;

        if (index < 0) {
            index = size - 1;
        }
        currentIndex = index;
        Node root = dataset.get(currentIndex);
        Node mp = getFirstMpNode(root);
        Long time = NeoUtils.getNodeTime(mp);
        beginGisTime = time;
        mp = getLastMpNode(mp);
        endGisTime = NeoUtils.getNodeTime(mp);
        setBeginTime(time);;
        updateChart();
    }

    /**
     * get length from spin
     * 
     * @return length (milliseconds)
     */
    private int getLength() {
        return sLength.getSelection() * 60 * 1000;
    }

    /**
     *go to next gis node
     */
    private void nextGis() {
        int index = currentIndex;
        int size = dataset.size();
        if (index < 0 || size < 2) {
            return;
        }
        index++;

        if (index >= size) {
            index = 0;
        }
        currentIndex = index;
        Node root = dataset.get(currentIndex);
        Node mp = getFirstMpNode(root);
        Long time = NeoUtils.getNodeTime(mp);
        beginGisTime = time;
        mp = getLastMpNode(mp);
        endGisTime = NeoUtils.getNodeTime(mp);
        setBeginTime(time);
        updateChart();
    }

    /**
     *change drive
     */
    protected void changeDate() {
        Node gis = getGisNode();

        if (gis == null) {
            return;
        }
        updateChart();
    }

    /**
     *change logarithmicSelection
     */
    protected void logarithmicSelection() {
        XYPlot plot = (XYPlot)chart.getPlot();
        if (bLogarithmic.getSelection()) {
            plot.setRangeAxis(2, axisLog1);
            plot.setRangeAxis(1, axisLog2);
            axisLog1.autoAdjustRange();
            axisLog2.autoAdjustRange();
        } else {
            plot.setRangeAxis(2, axisNumeric1);
            plot.setRangeAxis(1, axisNumeric2);
        }
        chart.fireChartChanged();
    }

    /**
     *changed property
     */
    protected void updateProperty() {
        Node gis = getGisNode();
        Long beginTime = getBeginTime();
        String event = cEvent.getText();
        String propertyName = cProperty1.getText();
        if (!propertyName.equals(xydataset1.getPropertyName())) {
            xydataset1.updateDataset(propertyName, dataset.get(currentIndex), beginTime, sLength.getSelection(), propertyName,
                    event);
        }
        propertyName = cProperty2.getText();
        if (!propertyName.equals(xydataset2.getPropertyName())) {
            xydataset2.updateDataset(propertyName, dataset.get(currentIndex), beginTime, sLength.getSelection(), propertyName,
                    event);
        }
        fireEventUpdateChart();

    }

    /**
     * get begin time
     * 
     * @return Long
     */
    private Long getBeginTime() {
        Date date = new Date(0);
        date.setHours(dateStart.getHours());
        date.setMinutes(dateStart.getMinutes());
        date.setSeconds(dateStart.getSeconds());
        return date.getTime();
    }

    /**
     *change drive dataset
     */
    protected void changeDrive() {
        if (cDrive.getSelectionIndex() < 0) {
            setsVisible(false);
        } else {

            formPropertyLists();
            updateChart();
        }
    }

    /**
     *update chart
     */
    private void updateChart() {
        Node gis = getGisNode();
        String event = cEvent.getText();
        String property1 = cProperty1.getText();
        String property2 = cProperty1.getText();

        if (gis == null || event.isEmpty() || property1.isEmpty() || property2.isEmpty()) {
            setsVisible(false);
        }
        Node root = dataset.get(currentIndex);
        chart.setTitle(CHART_TITLE + " " + NeoUtils.getSimpleNodeName(root, ""));
        Integer length = sLength.getSelection();
        Long time = getBeginTime();
        Date date = new Date(time);
        domainAxis.setMinimumDate(date);
        domainAxis.setMaximumDate(new Date(time + length * 1000 * 60));


        xydataset1.updateDataset(cProperty1.getText(), root, time, length, cProperty1.getText(), cEvent
                .getText());
        xydataset2.updateDataset(cProperty2.getText(), root, time, length, cProperty2.getText(), cEvent
                .getText());

        setsVisible(true);
        fireEventUpdateChart();
    }

    /**
     *fires event for chart changed
     */
    private void fireEventUpdateChart() {
        IMap activeMap = ApplicationGIS.getActiveMap();
        Node gis = getGisNode();
        if (activeMap != ApplicationGIS.NO_MAP) {
            try {
                for (ILayer layer : activeMap.getMapLayers()) {
                    IGeoResource resourse = layer.findGeoResource(GeoNeo.class);
                    if (resourse != null) {
                        GeoNeo geo = resourse.resolve(GeoNeo.class, null);
                        if (gis != null && geo.getMainGisNode().equals(gis)) {
                            setProperty(geo);
                            layer.refresh(null);
                        } else {
                            dropProperty(geo);
                        }

                    }
                }
            } catch (IOException e) {
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
        chart.fireChartChanged();
    }

    /**
     *remove property from geo
     * 
     * @param geo
     */
    private void dropProperty(GeoNeo geo) {
        if (geo.getGisType() != GisTypes.DRIVE) {
            return;
        }
        Object map = geo.getProperties(GeoNeo.DRIVE_INQUIRER);
        if (map != null) {
            geo.setProperty(GeoNeo.DRIVE_INQUIRER, null);
        }
    }

    /**
     * Sets property in geo for necessary
     * 
     * @param geo
     */
    private void setProperty(GeoNeo geo) {
        Node gisNode = getGisNode();
        if (!geo.getMainGisNode().equals(gisNode)) {
            return;
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        Long beginTime = getBeginTime();
        map.put(GeoConstant.Drive.BEGIN_TIME, beginTime);
        map.put(GeoConstant.Drive.END_TIME, beginTime + getLength());
        double crosshair = ((XYPlot)chart.getPlot()).getDomainCrosshairValue();
        Long nodeId = getSelectedProperty1(crosshair);
        Long id = null;
        // gets id of parent mp node
        if (nodeId != null) {
            Node node = NeoUtils.getNodeById(nodeId);
            if (node != null) {
                Relationship singleRelationship = node.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.INCOMING);
                if (singleRelationship != null) {
                    node = singleRelationship.getOtherNode(node);
                    id = node.getId();
                }
            }
        }
        map.put(GeoConstant.Drive.SELECT_PROPERTY1, id);
        nodeId = getSelectedProperty2(crosshair);
        id = null;
        // gets id of parent mp node
        if (nodeId != null) {
            Node node = NeoUtils.getNodeById(nodeId);
            if (node != null) {
                Relationship singleRelationship = node.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.INCOMING);
                if (singleRelationship != null) {
                    node = singleRelationship.getOtherNode(node);
                    id = node.getId();
                }
            }
        }
        map.put(GeoConstant.Drive.SELECT_PROPERTY2, id);
        geo.setProperty(GeoNeo.DRIVE_INQUIRER, map);
    }

    /**
     * get id of selected ms node for property 2
     * 
     * @param crosshair - crosshair value
     * @return node id or null
     */
    private Long getSelectedProperty2(double crosshair) {
        Integer result = getCrosshairIndex(xydataset2, crosshair);
        if (result != null) {
            return xydataset1.collection.getSeries(0).getDataItem(result).getValue().longValue();
        } else {
            return null;
        }
    }

    /**
     * get id of selected ms node for property 1
     * 
     * @param crosshair - crosshair value
     * @return node id or null
     */
    private Long getSelectedProperty1(double crosshair) {
        Integer result = getCrosshairIndex(xydataset1, crosshair);
        if (result != null) {
            return xydataset1.collection.getSeries(0).getDataItem(result).getValue().longValue();
        } else {
            return null;
        }
    }

    /**
     * Gets index of crosshair data item
     * 
     * @param xydataset
     * @param crosshair
     * @return index or null
     */
    private Integer getCrosshairIndex(TimeDataset dataset, Number crosshair) {
        int[] item = dataset.collection.getSurroundingItems(0, crosshair.longValue());
        xydataset1.collection.getSeries(0).getItemCount();
        Integer result = null;
        if (item[0] >= 0) {
            result = item[0];
        } else if (item[1] >= 0) {
            result = item[1];
        }
        return result;
    }

    /**
     *forms all property depends of gis
     */
    private void formPropertyLists() {
        Transaction tx = NeoUtils.beginTransaction();
        try {
            Node gis = getGisNode();
            dataset = new ArrayList<Node>();
            dataset.addAll(NeoUtils.getAllFileNodes(gis).getAllNodes());
            currentIndex = 0;
            PropertyHeader propertyHeader = new PropertyHeader(gis);
            Collection<String> events = propertyHeader.getEvents();
            eventList = new ArrayList<String>();
            eventList.add(ALL_EVENTS);
            if (events != null) {
                eventList.addAll(events);
            }
            cEvent.setItems(eventList.toArray(new String[0]));
            cEvent.select(0);
            String[] propNum = propertyHeader.getDefinedNumericFields();
            // TODO why all drive numeric properties in 2 methods???
            String[] list = propertyHeader.getNumericFields();
            List<String> result = new ArrayList<String>();
            List<String> asList = Arrays.asList(propNum);
            result.addAll(asList);
            if (list != null) {
                result.addAll(Arrays.asList(list));
            }
            String[] array = result.toArray(new String[0]);
            cProperty1.setItems(array);
            cProperty2.setItems(array);
            if (propNum.length > 0) {
                cProperty1.select(0);
                cProperty2.select(0);
            }
            Node root = dataset.get(currentIndex);
            Node mp = getFirstMpNode(root);
            Long time = NeoUtils.getNodeTime(mp);
            beginGisTime = time;
            mp = getLastMpNode(mp);
            endGisTime = NeoUtils.getNodeTime(mp);
            setBeginTime(time);

        } finally {
            tx.finish();
        }

    }

    /**
     * get last mp node
     * 
     * @param root
     * @return
     */
    private Node getLastMpNode(Node root) {
        Node node = root;
        Relationship relation;
        while ((relation = node.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING)) != null) {
            node = relation.getOtherNode(node);
        }
        return node;
    }

    /**
     * get first mp node
     * 
     * @param gis - root node
     * @return node
     */
    private Node getFirstMpNode(Node root) {
        Node node = root;
        while (node != null) {
            if (NeoUtils.getNodeType(node, "").equals(INeoConstants.MP_TYPE_NAME)) {
                return node;
            }
            Relationship relation = node.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
            node = relation == null ? null : relation.getOtherNode(node);
        }
        return null;
    }

    /**
     * get gis node
     * 
     * @return node
     */
    private Node getGisNode() {
        return gisDriveNodes == null ? null : gisDriveNodes.get(cDrive.getText());
    }

    /**
     * get Drive list
     * 
     * @return String[]
     */
    private String[] getDriveItems() {
        NeoService service = NeoServiceProvider.getProvider().getService();
        Node refNode = service.getReferenceNode();
        gisDriveNodes = new LinkedHashMap<String, Node>();

        Transaction tx = service.beginTx();
        try {
            for (Relationship relationship : refNode.getRelationships(Direction.OUTGOING)) {
                Node node = relationship.getEndNode();
                Object type = node.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, "").toString();
                if (NeoUtils.isGisNode(node) && type.equals(GisTypes.DRIVE.getHeader())) {
                    String id = NeoUtils.getSimpleNodeName(node, null);
                    gisDriveNodes.put(id, node);
                }
            }

            return gisDriveNodes.keySet().toArray(new String[] {});
        } finally {
            tx.finish();
        }
    }

    public void setFocus() {
    }

    /**
     * Creates the Chart based on a dataset
     */
    private JFreeChart createChart() {

        xydataset1 = new TimeDataset();
        xydataset2 = new TimeDataset();
        XYAreaRenderer xyarearenderer = new EventRenderer();
        XYDataset eventDataset = new EventDataset();
        NumberAxis rangeAxis = new NumberAxis("Events");
        rangeAxis.setVisible(false);
        domainAxis = new DateAxis("Time");
        XYPlot xyplot = new XYPlot(eventDataset, domainAxis, rangeAxis, xyarearenderer);
        StandardXYItemRenderer standardxyitemrenderer = new StandardXYItemRenderer();
        standardxyitemrenderer.setBaseShapesFilled(true);
        xyplot.setDataset(2, xydataset1);
        xyplot.setRenderer(2, standardxyitemrenderer);
        standardxyitemrenderer = new StandardXYItemRenderer();
        standardxyitemrenderer.setBaseShapesFilled(true);
        NumberAxis numberaxis = new NumberAxis(getProperty1YAxisName());
        numberaxis.setAutoRangeIncludesZero(false);
        xyplot.setRangeAxis(2, numberaxis);
        xyplot.setRangeAxisLocation(2, AxisLocation.BOTTOM_OR_LEFT);
        xyplot.mapDatasetToRangeAxis(2, 2);

        xyplot.setDataset(1, xydataset2);
        xyplot.setRenderer(1, standardxyitemrenderer);
        numberaxis = new NumberAxis(getProperty2YAxisName());
        xyplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        numberaxis.setAutoRangeIncludesZero(false);
        xyplot.setRangeAxis(1, numberaxis);
        xyplot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_LEFT);
        xyplot.mapDatasetToRangeAxis(1, 1);
        xyplot.setDomainCrosshairVisible(true);
        xyplot.setDomainCrosshairLockedOnData(false);
        xyplot.setRangeCrosshairVisible(false);
        JFreeChart jfreechart = new JFreeChart(CHART_TITLE, JFreeChart.DEFAULT_TITLE_FONT, xyplot, true);
        ChartUtilities.applyCurrentTheme(jfreechart);

        axisNumeric1 = xyplot.getRangeAxis(2);
        axisNumeric2 = xyplot.getRangeAxis(1);
        axisLog1 = new LogarithmicAxis(axisNumeric1.getLabel());
        axisLog1.setAllowNegativesFlag(true);
        axisLog1.setAutoRange(true);
        axisLog2 = new LogarithmicAxis(axisNumeric2.getLabel());
        axisLog2.setAllowNegativesFlag(true);
        axisLog2.setAutoRange(true);

        axisLog1.setTickLabelPaint(COLOR_LEFT_PROPERTY);
        axisLog1.setLabelPaint(COLOR_LEFT_PROPERTY);
        axisNumeric1.setTickLabelPaint(COLOR_LEFT_PROPERTY);
        axisNumeric1.setLabelPaint(COLOR_LEFT_PROPERTY);
        axisLog2.setTickLabelPaint(COLOR_RIGHT_PROPERTY);
        axisLog2.setLabelPaint(COLOR_RIGHT_PROPERTY);
        axisNumeric2.setTickLabelPaint(COLOR_RIGHT_PROPERTY);
        axisNumeric2.setLabelPaint(COLOR_RIGHT_PROPERTY);
        xyplot.getRenderer(0).setSeriesPaint(0, new Color(0, 0, 0, 0));
        xyplot.getRenderer(2).setSeriesPaint(0, COLOR_LEFT_PROPERTY);
        xyplot.getRenderer(1).setSeriesPaint(0, COLOR_RIGHT_PROPERTY);
        return jfreechart;

    }

    /**
     * @return
     */
    private String getProperty2YAxisName() {
        return "";
    }

    /**
     * @return
     */
    private String getProperty1YAxisName() {
        return "";
    }

    /**
     * <p>
     * Dataset for event
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private class EventDataset extends AbstractXYDataset {

        @Override
        public int getSeriesCount() {
            return xydataset1.getSeriesCount();
        }

        @Override
        public Comparable getSeriesKey(int i) {
            return "";
        }

        @Override
        public int getItemCount(int i) {
           
            return xydataset1.getItemCount(i);
        }

        // /**
        // * get dataset with maximum length
        // * @return dataset
        // */
        // private TimeDataset getMaxDataSet() {
        // TimeDataset dataset=xydataset1;
        // if (dataset.getItemCount(0)<xydataset2.getItemCount(0)){
        // dataset=xydataset2;
        // }
        // return dataset;
        // }

        @Override
        public Number getX(int i, int j) {
            return xydataset1.getX(i, j);
        }

        @Override
        public Number getY(int i, int j) {
            return 1;
        }

    }

    /**
     * <p>
     * Event renderer
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private class EventRenderer extends XYAreaRenderer {

        public EventRenderer() {
            super();
        }

        @Override
        public Shape getItemShape(int row, int column) {
            return super.getItemShape(row, column);
        }

        @Override
        public Paint getItemFillPaint(int row, int column) {
            return super.getItemFillPaint(row, column);
        }

        @Override
        public Paint getItemPaint(int row, int column) {
            TimeSeriesDataItem item = xydataset1.series.getDataItem(column);
            Node node = NeoServiceProvider.getProvider().getService().getNodeById(item.getValue().longValue());
            Color color = getEventColor(node);
            return color;
        }

    }

    /**
     * <p>
     * Time dataset Now it simple wrapper of TimeSeriesCollection But if cache is not possible need
     * be refactored for use database access
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private class TimeDataset extends AbstractXYDataset {

        private Node root;
        private Long beginTime;
        private Long length;
        private TimeSeries series;
        private TimeSeriesCollection collection;
        private String propertyName;
        private String event;

        /**
         * @return Returns the propertyName.
         */
        public String getPropertyName() {
            return propertyName;
        }

        /**
         * update dataset with new data
         * 
         * @param name - dataset name
         * @param root - root node
         * @param beginTime - begin time
         * @param length - length
         * @param propertyName - property name
         * @param event - event value
         */
        public void updateDataset(String name, Node root, Long beginTime, int length, String propertyName, String event) {
            this.root = root;
            this.beginTime = beginTime;
            this.length = (long)length * 1000 * 60;
            this.propertyName = propertyName;
            this.event = event;
            collection = new TimeSeriesCollection();
            createSeries(name, propertyName, event);
            collection.addSeries(series);
            this.fireDatasetChanged();
        }

        /**
         * constructor
         */
        public TimeDataset() {
            super();
            root = null;
            beginTime = null;
            length = null;
            series = null;
            collection = new TimeSeriesCollection();
            propertyName = null;
            event = null;
        }

        /**
         * Create time series
         * 
         * @param name name of serie
         * @param propertyName property name
         * @param event event value
         */
        private void createSeries(String name, String propertyName, String event) {
            Transaction tx = NeoUtils.beginTransaction();
            try {
                series = new TimeSeries(name);
                Iterator<Node> nodeIterator = getNodeIterator(root, beginTime, length).iterator();
                while (nodeIterator.hasNext()) {
                    Node node = (Node)nodeIterator.next();
                    Long time = NeoUtils.getNodeTime(node);
                    node = getSubNode(node, event, propertyName);
                    if (node == null) {
                        continue;
                    }

                    series.addOrUpdate(new Millisecond(new Date(time)), node.getId());
                }
            } finally {
                tx.finish();
            }
        }

        /**
         * get iterable of necessary mp nodes
         * 
         * @param root - root
         * @param beginTime - begin time
         * @param length - end time
         * @return
         */
        private Iterable<Node> getNodeIterator(Node root, final Long beginTime, final Long length) {

            return root.traverse(Order.DEPTH_FIRST, new StopEvaluator() {

                @Override
                public boolean isStopNode(TraversalPosition currentPos) {
                    Node node = currentPos.currentNode();
                    if (!NeoUtils.getNodeType(node, "").equals(INeoConstants.MP_TYPE_NAME)) {
                        return false;
                    }
                    Long nodeTime = NeoUtils.getNodeTime(node);
                    return nodeTime == null ? true : (nodeTime - beginTime > length);
                }
            }, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Node node = currentPos.currentNode();
                    if (!NeoUtils.getNodeType(node, "").equals(INeoConstants.MP_TYPE_NAME)) {
                        return false;
                    }
                    Long nodeTime = NeoUtils.getNodeTime(node);
                    return nodeTime == null ? false : (nodeTime - beginTime <= length);
                }
            }, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
        }

        @Override
        public int getSeriesCount() {
            return collection.getSeriesCount();
        }

        @Override
        public Comparable getSeriesKey(int i) {
            return collection.getSeriesKey(i);
        }

        @Override
        public int getItemCount(int i) {
            return collection.getItemCount(i);
        }

        @Override
        public Number getX(int i, int j) {
            return collection.getX(i, j);
        }

        @Override
        public Number getY(int i, int j) {
            return (Number)NeoServiceProvider.getProvider().getService().getNodeById(collection.getY(i, j).longValue())
                    .getProperty(propertyName);
        }

    }

    /**
     * get event color
     * 
     * @param node node
     * @return color
     */
    public Color getEventColor(Node node) {
        String event = node.getProperty(EVENT, ALL_EVENTS).toString();
        int i = eventList.indexOf(event);
        if (i < 0) {
            i = 0;
        }
        BrewerPalette palette = PlatformGIS.getColorBrewer().getPalette(cPalette.getText());
        Color[] colors = palette.getColors(palette.getMaxColors());
        int index = i % colors.length;
        return colors[index];
    }

    /**
     * get necessary subnodes of mp node
     * 
     * @param node - node
     * @param event - event value
     * @param propertyName - property name
     * @return subnode
     */
    public Node getSubNode(Node node, final String event, final String propertyName) {
        Iterator<Node> iterator = node.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                if (currentPos.isStartNode()) {
                    return false;
                }
                Node node = currentPos.currentNode();
                return node.hasProperty(propertyName)
                        && (ALL_EVENTS.equals(event) || event.equalsIgnoreCase(node.getProperty(EVENT, "").toString()));
            }
        }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

        private ArrayList<String> columns = new ArrayList<String>();
        /** int DEF_SIZE field */
        protected static final int DEF_SIZE = 150;
        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            NodeWrapper wr = (NodeWrapper)element;
            if (columnIndex == 3 && wr.node != null) {
                Color eventColor = getEventColor(wr.node);
                return Glyph.palette(new Color[] {eventColor}).createImage();
            }
            return getImage(element);
        }

        /**
         *create column table
         */
        public void createTableColumn() {
            Table tabl = table.getTable();
            TableViewerColumn column;
            TableColumn col;
            if (columns.isEmpty()) {
                column = new TableViewerColumn(table, SWT.LEFT);
                col = column.getColumn();
                col.setText("Property");
                columns.add(col.getText());
                col.setWidth(DEF_SIZE);
                col.setResizable(true);

                column = new TableViewerColumn(table, SWT.LEFT);
                col = column.getColumn();
                col.setText("Enum");
                columns.add(col.getText());
                col.setWidth(DEF_SIZE);
                col.setResizable(true);

                column = new TableViewerColumn(table, SWT.LEFT);
                col = column.getColumn();
                col.setText("Property value");
                columns.add(col.getText());
                col.setWidth(DEF_SIZE);
                col.setResizable(true);

                column = new TableViewerColumn(table, SWT.LEFT);
                col = column.getColumn();
                col.setText("Event");
                columns.add(col.getText());
                col.setWidth(DEF_SIZE);
                col.setResizable(true);

            }
            tabl.setHeaderVisible(true);
            tabl.setLinesVisible(true);
            table.setLabelProvider(this);
            table.refresh();
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            NodeWrapper wr = (NodeWrapper)element;
            String result = "";
            if (columnIndex == 0) {
                result = wr.propertyName;
            } else if (columnIndex == 1) {
                result = wr.nodsEnum.name;
            } else if (wr.node != null) {
                if (columnIndex == 2) {
                    result = wr.node.getProperty(wr.propertyName, "").toString();
                } else if (columnIndex == 3) {
                    result = wr.node.getProperty(EVENT, ALL_EVENTS).toString();
                }
            }

            return result;
        }

    }

    /*
     * The content provider class is responsible for providing objects to the view. It can wrap
     * existing objects in adapters or simply return objects as-is. These objects may be sensitive
     * to the current input of the view, or ignore it and always show the same content (like Task
     * List, for example).
     */

    private class TableContentProvider implements IStructuredContentProvider {

        private List<NodeWrapper> list;

        public TableContentProvider() {
            list = new ArrayList<NodeWrapper>(3);
            NodeWrapper node = new NodeWrapper();
            node.setNodsEnum(NodesEnum.PREVIOUS);
            list.add(node);
            node = new NodeWrapper();
            node.setNodsEnum(NodesEnum.SELECTED);
            list.add(node);
            node = new NodeWrapper();
            node.setNodsEnum(NodesEnum.NEXT);
            list.add(node);

            node = new NodeWrapper();
            node.setNodsEnum(NodesEnum.PREVIOUS);
            list.add(node);
            node = new NodeWrapper();
            node.setNodsEnum(NodesEnum.SELECTED);
            list.add(node);
            node = new NodeWrapper();
            node.setNodsEnum(NodesEnum.NEXT);
            list.add(node);
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return list.toArray(new NodeWrapper[0]);
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            if (newInput == null) {
                return;
            }
            double crosshair = ((XYPlot)chart.getPlot()).getDomainCrosshairValue();
            for (int i = 0; i < list.size(); i++) {
                NodeWrapper nodewr = list.get(i);
                nodewr.setNode(null);
                nodewr.setPropertyName(getPropertyName(i));
            }
            if (crosshair < 0.1) {
                return;
            }
            Integer index1 = getCrosshairIndex(xydataset1, crosshair);
            if (index1 != null) {
                list.get(1).setNode(
                        NeoUtils.getNodeById(xydataset1.collection.getSeries(0).getDataItem(index1).getValue().longValue()));
                if (index1 > 0) {
                    list.get(0)
                            .setNode(
                                    NeoUtils.getNodeById(xydataset1.collection.getSeries(0).getDataItem(index1 - 1).getValue()
                                            .longValue()));
                }
                if (index1 + 1 < xydataset1.collection.getSeries(0).getItemCount()) {
                    list.get(2)
                            .setNode(
                                    NeoUtils.getNodeById(xydataset1.collection.getSeries(0).getDataItem(index1 + 1).getValue()
                                            .longValue()));
                }
            }
            if (list.size() > 3) {
                Integer index2 = getCrosshairIndex(xydataset2, crosshair);
                if (index2 != null) {
                    list.get(4).setNode(
                            NeoUtils.getNodeById(xydataset2.collection.getSeries(0).getDataItem(index2).getValue().longValue()));
                    if (index2 > 0) {
                        list.get(3).setNode(
                                NeoUtils.getNodeById(xydataset2.collection.getSeries(0).getDataItem(index2 - 1).getValue()
                                        .longValue()));
                    }
                    if (index2 + 1 < xydataset2.collection.getSeries(0).getItemCount()) {
                        list.get(5).setNode(
                                NeoUtils.getNodeById(xydataset2.collection.getSeries(0).getDataItem(index2 + 1).getValue()
                                        .longValue()));
                    }
                }
            }
        }

        /**
         * @param i
         * @return
         */
        private String getPropertyName(int i) {
            return i < 3 ? cProperty1.getText() : cProperty2.getText();
        }

    }

    private class NodeWrapper {
        NodesEnum nodsEnum;
        String propertyName;
        Node node;

        /**
         * @return Returns the nodsEnum.
         */
        public NodesEnum getNodsEnum() {
            return nodsEnum;
        }

        /**
         * @param nodsEnum The nodsEnum to set.
         */
        public void setNodsEnum(NodesEnum nodsEnum) {
            this.nodsEnum = nodsEnum;
        }

        /**
         * @return Returns the propertyName.
         */
        public String getPropertyName() {
            return propertyName;
        }

        /**
         * @param propertyName The propertyName to set.
         */
        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        /**
         * @return Returns the node.
         */
        public Node getNode() {
            return node;
        }

        /**
         * @param node The node to set.
         */
        public void setNode(Node node) {
            this.node = node;
        }

    }

    /**
     * <p>
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private enum NodesEnum {
        PREVIOUS("previous node"), SELECTED("selected node"), NEXT("next node");
        private final String name;

        /**
         * 
         */
        private NodesEnum(String name) {
            this.name = name;
        }

        /**
         * @return Returns the name.
         */
        public String getName() {
            return name;
        }

    }
}
