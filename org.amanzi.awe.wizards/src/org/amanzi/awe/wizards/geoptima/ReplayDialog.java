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

package org.amanzi.awe.wizards.geoptima;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.mapgraphic.internal.MapGraphicService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.awe.wizards.geoptima.VisualiseLayer.VisualiseParam;
import org.amanzi.neo.services.Pair;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 * Replay dialog
 * </p>.
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public class ReplayDialog extends ProcessDialog {

    /** The s start time. */
    private DateTime sStartTime;
    
    /** The s start date. */
    private DateTime sStartDate;
    
    /** The s end time. */
    private DateTime sEndTime;
    
    /** The s end date. */
    private DateTime sEndDate;
    
    /** The s time window. */
    private Spinner sTimeWindow;
    
    /** The c repeat. */
    private Button cRepeat;
    
    /** The b correlate. */
    private Button bCorrelate;
    
    /** The min max time. */
    private Pair<Long, Long> minMaxTime;

    /**
     * Instantiates a new replay dialog.
     *
     * @param parent the parent
     */
    public ReplayDialog(Shell parent) {
        super(parent, "Replay Subscriber Locations", "Start", SWT.DIALOG_TRIM  | SWT.CENTER);
    }

    /**
     * Formdata map.
     *
     * @param storedData the stored data
     */
    @Override
    protected void formdataMap(Set<Node> storedData) {
        datamap.clear();
        Transaction tx = service.beginTx();
        try {
            for (Node node : storedData) {
                // have gis node
                Pair<Long, Long> minMax = NeoUtils.getMinMaxTimeOfDataset(node, service);

                if (node.hasRelationship(GeoNeoRelationshipTypes.NEXT, Direction.INCOMING) && minMax != null && minMax.l() != null && minMax.r() != null) {
                    datamap.put(NeoUtils.getNodeName(node), node);
                }
            }
        } finally {
            tx.finish();
        }
    }

    /**
     * Creates the contents.
     *
     * @param shell the shell
     */
    @Override
    protected void createContents(final Shell shell) {
        this.shell = shell;
        shell.setLayout(new GridLayout(3, false));
        Label label = new Label(shell, SWT.NONE);
        label.setText("Select data:");
        cData = new Combo(shell, SWT.FILL | SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.horizontalSpan = 2;
        layoutData.grabExcessHorizontalSpace = true;
        cData.setLayoutData(layoutData);

        label = new Label(shell, SWT.NONE);
        label.setText("Start time:");
        sStartTime = new DateTime(shell, SWT.BORDER | SWT.TIME | SWT.LONG);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.widthHint = 100;
        sStartTime.setLayoutData(layoutData);
        sStartDate = new DateTime(shell, SWT.BORDER | SWT.DATE | SWT.LONG);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.widthHint = 100;
        sStartDate.setLayoutData(layoutData);
        label = new Label(shell, SWT.NONE);
        label.setText("End time:");
        sEndTime = new DateTime(shell, SWT.BORDER | SWT.TIME | SWT.LONG);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        sEndTime.setLayoutData(layoutData);
        sEndDate = new DateTime(shell, SWT.BORDER | SWT.DATE | SWT.LONG);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        sEndDate.setLayoutData(layoutData);
        label = new Label(shell, SWT.NONE);
        label.setText("Time window(sec):");
        sTimeWindow = new Spinner(shell, SWT.BORDER);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        // layoutData.horizontalSpan=2;
        sTimeWindow.setLayoutData(layoutData);
        sTimeWindow.setMinimum(5);
        sTimeWindow.setMaximum(Integer.MAX_VALUE);
        sTimeWindow.setDigits(1);
        sTimeWindow.setIncrement(10);
        sTimeWindow.setSelection(5);
        // new Label(shell, SWT.NONE);
        // label = new Label(shell, SWT.NONE);
        cRepeat = new Button(shell, SWT.CHECK);
        cRepeat.setText("Repeat");
        cRepeat.setSelection(true);
        // new Label(shell, SWT.NONE);
        new Label(shell, SWT.NONE);
        bCorrelate = new Button(shell, SWT.PUSH);
        // bCorrelate.setEnabled(false);
        GridData gdBtnOk = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gdBtnOk.widthHint = 70;
        bCorrelate.setLayoutData(gdBtnOk);
        bCorrelate.setText(getProcessButtonLabel());
        bCorrelate.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                processBtn();
            }

        });
        cData.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                formCorrelateData();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        Button btnOk = new Button(shell, SWT.PUSH);
        btnOk.setText("OK");
        gdBtnOk = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gdBtnOk.widthHint = 70;
        btnOk.setLayoutData(gdBtnOk);
        btnOk.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.OK;
                shell.close();
            }

        });
    }

    /**
     * Process btn.
     */
    @Override
    protected void processBtn() {
        final Node dataNode = datamap.get(cData.getText());
        if (dataNode != null) {
            Long timeBegin = getTime(sStartDate, sStartTime);
            long startTime = Math.max(timeBegin, minMaxTime.getLeft());
            if (startTime!=timeBegin){
                setTime(sStartDate, sStartTime, startTime);
            }
            Long timeEnd = getTime(sEndDate, sEndTime);
            long endTime = Math.min(timeEnd, minMaxTime.getRight());
            if (timeEnd!=endTime){
                setTime(sEndDate, sEndTime, endTime);
            }
            if (startTime <= endTime) {
                boolean isRepeat = cRepeat.getSelection();
                final Node gisNode = dataNode.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.INCOMING).getOtherNode(dataNode);
                launch(gisNode, startTime, endTime, isRepeat,sTimeWindow.getSelection()*100);

            }
        }
    }

    /**
     * Launch.
     * 
     * @param gisNode the gis node
     * @param startTime the start time
     * @param endTime the end time
     * @param isRepeat the is repeat
     * @param timeWindow 
     */
    private void launch(Node gisNode, long startTime, long endTime, boolean isRepeat, long timeWindow) {
        ILayer lDataset = launchLayer(gisNode);
        if (lDataset != null) {
            ILayer lGraphic = launchMapGraphic(gisNode, lDataset,startTime,endTime,isRepeat,timeWindow);
        }

    }


    /**
     * Launch map graphic.
     *
     * @param gisNode the gis node
     * @param lDataset the l dataset
     * @param startTime the start time
     * @param endTime the end time
     * @param isRepeat the is repeat
     * @param timeWindow the time window
     * @return the i layer
     */
    private ILayer launchMapGraphic(Node gisNode, ILayer lDataset, long startTime, long endTime, boolean isRepeat, long timeWindow) {
        try {
            ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
            IService service = localCatalog.getById(IService.class, MapGraphicService.SERVICE_URL, null);
            IGeoResource resourceToAdd = null;
            for (IGeoResource iGeoResource : service.resources(null)) {
                if (iGeoResource.canResolve(VisualiseLayer.class)) {
                    resourceToAdd = iGeoResource;
                    break;
                }
            }
            VisualiseParam param = new VisualiseLayer.VisualiseParam(startTime,endTime,isRepeat,timeWindow);
            List<ILayer> layers = ApplicationGIS.getActiveMap().getMapLayers();
            for (ILayer iLayer : layers) {
                if (iLayer.getGeoResource().canResolve(VisualiseLayer.class)) {
                    Object dataset = iLayer.getBlackboard().get(VisualiseLayer.DATASET_LAYER);
                    if (dataset != null && dataset.equals(lDataset)) {
                        setParamToLayer(iLayer,param);
                        return iLayer;
                    }
                }
            }
            List<IGeoResource> listGeoRes = new ArrayList<IGeoResource>();
            listGeoRes.add(resourceToAdd);
            VisualiseLayer.datasetLayer=lDataset;
            VisualiseLayer.datasetParam=param;
            List< ? extends ILayer> result = ApplicationGIS.addLayersToMap(lDataset.getMap(), listGeoRes, 0);
            return result.iterator().next();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sets the param to layer.
     *
     * @param iLayer the i layer
     * @param param the param
     */
    private void setParamToLayer(ILayer iLayer, VisualiseParam param) {
        iLayer.getBlackboard().put(VisualiseLayer.DATASET_PARAM,param);
    }

    /**
     * Launch layer.
     *
     * @param gisNode the gis node
     * @return the i layer
     */
    private ILayer launchLayer(Node gisNode) {
        try {
            String databaseLocation = NeoServiceProviderUi.getProvider().getDefaultDatabaseLocation();
            URL url = new URL("file://" + databaseLocation);
            List<ILayer> layers = ApplicationGIS.getActiveMap().getMapLayers();
            for (ILayer iLayer : layers) {
                if (iLayer.getGeoResource().canResolve(Node.class)) {
                    if (iLayer.getGeoResource().resolve(Node.class, new NullProgressMonitor()).equals(gisNode)) {
                        return iLayer;
                    }
                }
            }
            IService curService = CatalogPlugin.getDefault().getLocalCatalog().getById(IService.class, url, null);
            List<IGeoResource> listGeoRes = new ArrayList<IGeoResource>();
            for (IGeoResource iGeoResource : curService.resources(null)) {
                if (iGeoResource.canResolve(Node.class)) {
                    if (iGeoResource.resolve(Node.class, null).equals(gisNode)) {
                        listGeoRes.add(iGeoResource);
                        break;
                    }
                }
            };
            List< ? extends ILayer> result = ApplicationGIS.addLayersToMap(ApplicationGIS.getActiveMap(), listGeoRes, 0);
            return result.iterator().next();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Form correlate data.
     */
    @Override
    protected void formCorrelateData() {
        final Node dataNode = datamap.get(cData.getText());
        if (dataNode != null) {
            minMaxTime = NeoUtils.getMinMaxTimeOfDataset(dataNode, service);
            setTime(sStartDate, sStartTime, minMaxTime.getLeft());
            setTime(sEndDate, sEndTime, minMaxTime.getRight());
        }
    }

    /**
     * Gets the time.
     *
     * @param dateFild the date fild
     * @param timeFild the time fild
     * @return the time
     */
    private Long getTime(DateTime dateFild, DateTime timeFild) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0L);
        calendar.set(dateFild.getYear(), dateFild.getMonth(), dateFild.getDay(), timeFild.getHours(), timeFild.getMinutes(), timeFild.getSeconds());
        return calendar.getTimeInMillis();
    }

    /**
     * Sets the time.
     * 
     * @param date the date
     * @param time the time
     * @param timeinmillisec the timeinmillisec
     */
    private void setTime(DateTime date, DateTime time, Long timeinmillisec) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeinmillisec);
        date.setYear(calendar.get(Calendar.YEAR));
        date.setMonth(calendar.get(Calendar.MONTH));
        date.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        time.setHours(calendar.get(Calendar.HOUR_OF_DAY));
        time.setMinutes(calendar.get(Calendar.MINUTE));
        time.setSeconds(calendar.get(Calendar.SECOND));
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());

        Button open = new Button(shell, SWT.PUSH);
        open.setText("Open Dialog");
        open.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM);
                dialog.setLayout(new GridLayout(3, false));

                final DateTime calendar = new DateTime(dialog, SWT.CALENDAR | SWT.BORDER);
                final DateTime date = new DateTime(dialog, SWT.DATE);
                new DateTime(dialog, SWT.DATE | SWT.SHORT);
                new DateTime(dialog, SWT.DATE | SWT.MEDIUM);
                new DateTime(dialog, SWT.DATE | SWT.LONG);
                final DateTime time = new DateTime(dialog, SWT.TIME);
                new DateTime(dialog, SWT.TIME | SWT.SHORT);
                new DateTime(dialog, SWT.TIME | SWT.MEDIUM);
                new DateTime(dialog, SWT.TIME | SWT.LONG);

                new Label(dialog, SWT.NONE);
                new Label(dialog, SWT.NONE);
                Button ok = new Button(dialog, SWT.PUSH);
                ok.setText("OK");
                ok.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
                ok.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        System.out.println("Calendar date selected (MM/DD/YYYY) = " + (calendar.getMonth() + 1) + "/" + calendar.getDay() + "/" + calendar.getYear());
                        System.out.println("Date selected (MM/YYYY) = " + (date.getMonth() + 1) + "/" + date.getYear());
                        System.out.println("Time selected (HH:MM) = " + time.getHours() + ":" + time.getMinutes());
                        dialog.close();
                    }
                });
                dialog.setDefaultButton(ok);
                dialog.pack();
                dialog.open();
            }
        });
        shell.pack();
        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
