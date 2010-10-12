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

package org.amanzi.awe.reports.geoptima.wizard;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.awe.report.model.ReportModel;
import org.amanzi.awe.reports.geoptima.GeoptimaReportsPlugin;
import org.amanzi.awe.views.reuse.Distribute;
import org.amanzi.awe.views.reuse.Select;
import org.amanzi.awe.views.reuse.views.ReuseAnalyserModel;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.TEMSLoader;
import org.amanzi.neo.loader.ui.utils.LoaderUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.Predicate;

import com.vividsolutions.jts.geom.Envelope;

/**
 * GeOptima report wizard that automatically loads the data, creates necessary distribution
 * analysis and generates reports
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class GeoptimaReportWizard extends Wizard implements IWizard {

    public static final String WIZARD_TITLE = "GeOptimA Report Wizard";
    private SelectDataPage selectDataPage;
    private ReportModel reportModel;
    private static final String MESSAGE = "%s (Step %d of %d)";
    private static final int STEP_COUNT = 5;
    private int step = 0;

    @Override
    public void addPages() {
        getContainer().getShell().setSize(600, 300);
        selectDataPage = new SelectDataPage(SelectDataPage.class.getName());
        addPage(selectDataPage);
    }

    @Override
    public boolean performFinish() {
        setNeedsProgressMonitor(true);
        initializeReportEngine();

        String directory = selectDataPage.getDirectory();
        final File[] files = getFilesToLoad(directory);
        final int filesCount = files.length;
        final GraphDatabaseService service = NeoServiceProvider.getProvider().getService();

        loadFiles(files);
        // build distribution analysis for all numeric properties except lat, long and time
        createDistribution(filesCount, service);
        // TODO build distribution analysis for some string properties like event_id
        // TODO zoom to business districts
        addLayersToMap(filesCount, service);
        generateReports();
        return true;
    }

    /**
     * Initializes the report engine with additional file
     */
    private void initializeReportEngine() {
        try {
            nextStep();
            URL entry = Platform.getBundle(GeoptimaReportsPlugin.PLUGIN_ID).getEntry("ruby");
            URL scriptURL = FileLocator.toFileURL(GeoptimaReportsPlugin.getDefault().getBundle().getEntry("ruby/automation.rb"));
            String path = scriptURL.getPath();
            reportModel = new ReportModel(new String[] {FileLocator.resolve(entry).getFile()}, new String[] {path});
        } catch (IOException e1) {
            // TODO Handle IOException
            e1.printStackTrace();
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }
    }

    /**
     * Generates reports for all 'drive' layers found in map
     */
    private void generateReports() {
        try {
            nextStep();
            selectDataPage.setTitle(String.format(MESSAGE, "Creating reports", step, STEP_COUNT));
            selectDataPage.setMessage("");
            getContainer().run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    reportModel.updateModel("automation");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } finally {
        }
    }

    /**
     * Adds layers to map
     * 
     * @param filesCount the quantity of files that were loaded
     * @param service database service
     */
    private void addLayersToMap(final int filesCount, final GraphDatabaseService service) {
        try {
            nextStep();
            selectDataPage.setTitle(String.format(MESSAGE, "Adding layers to map", step, STEP_COUNT));
            selectDataPage.setMessage("");
            getContainer().run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Add layers to map", filesCount);
                    IService curService;
                    try {
                        curService = LoaderUtils.getMapService();
                        System.out.print("MapService " + curService);
                        IMap map = ApplicationGIS.getActiveMap();
                        // map.getBounds(null).init(120.848, 121.147, 14.641, 14.765);
                        ViewportModel viewportModel = (ViewportModel)map.getViewportModel();
                        // // viewportModel.zoomToBox(new Envelope(120.0,121.1,14.6,14.7));
                        viewportModel.setBounds(new Envelope(120.848, 121.147, 14.641, 14.765));
                        // // viewportModel.setBounds(new Envelope(120.0, 121.1, 14.6, 14.7));
                        LinkedHashMap<String, Node> allDatasetNodes = NeoUtils.getAllDatasetNodes(service);
                        System.out.println("There were created " + allDatasetNodes.size() + " datasets");
                        for (Entry<String, Node> entry : allDatasetNodes.entrySet()) {
                            Node node = entry.getValue();
                            // add node to map
                            System.out.print("Adding dataset '" + node.getProperty("name") + "' to map...");
                            monitor.subTask("Adding dataset '" + node.getProperty("name") + "' to map");
                            List<ILayer> layerList = new ArrayList<ILayer>();
                            List<IGeoResource> listGeoRes = new ArrayList<IGeoResource>();
                            Node gisNode = NeoUtils.getGisNodeByDataset(node);
                            System.out.println("gisNode " + gisNode);
                            IGeoResource iGeoResource = LoaderUtils.getResourceForGis(curService, map, gisNode);
                            System.out.println("iGeoResource " + iGeoResource);
                            if (iGeoResource != null) {
                                listGeoRes.add(iGeoResource);
                            }
                            layerList.addAll(ApplicationGIS.addLayersToMap(map, listGeoRes, 0));
                            // LoaderUtils.zoomToLayer(layerList);
                            System.out.println("Finished.");
                            monitor.worked(1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // TODO Handle IOException
                        throw (RuntimeException)new RuntimeException().initCause(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Handle InterruptedException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Finds all csv files of the directory specified
     * 
     * @param directory
     * @return
     */
    private File[] getFilesToLoad(String directory) {
        File dir = new File(directory);
        final File[] files = dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".csv");
            }

        });
        return files;
    }

    /**
     * Creates distribution analysis for all datasets loaded
     * <p>
     * TODO process only newly loaded datasets
     * 
     * @param filesCount
     * @param service
     */
    private void createDistribution(final int filesCount, final GraphDatabaseService service) {
        nextStep();
        selectDataPage.setTitle(String.format(MESSAGE, "Buiding statistics", step, STEP_COUNT));
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Creation distributions", filesCount);
                    int i = 0;
                    for (Entry<String, Node> entry : NeoUtils.getAllDatasetNodes(service).entrySet()) {
                        i++;
                        Node node = entry.getValue();

                        String datasetName = node.getProperty("name").toString();
                        monitor.subTask(datasetName.toString());

                        // String[] fields = new String[] {"signal_strength"};
                        String[] fields = new String[] {"signal_strength", "imei", "rxqual", "ec_io"};
                        // String[] fields =
                        // PropertyHeader.getPropertyStatistic(node).getNumericFields(NodeTypes.M.getId());

                        for (String field : fields) {
                            updateMessage("Dataset (" + i + " of " + filesCount + "):\n" + datasetName
                                    + "\nBuiding statistics for '" + field);
                            ReuseAnalyserModel model = new ReuseAnalyserModel(new HashMap<String, String[]>(),
                                    getPropertyReturnableEvaluator(node), service);
                            Transaction tx = NeoUtils.beginTransaction();
                            try {
                                model.setCurrenTransaction(tx);
                                model.findOrCreateAggregateNode(node, field, false, Distribute.AUTO.toString(), Select.EXISTS
                                        .toString(), monitor);
                                tx = model.getCurrenTransaction();
                                tx.success();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                monitor.done();
                                tx.finish();
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            // TODO Handle InterruptedException
            e.printStackTrace();
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Loads files
     * 
     * @param files files to be loaded
     */
    private void loadFiles(final File[] files) {
        nextStep();
        final int n = files.length;
        selectDataPage.setTitle(String.format(MESSAGE, "Loading data", step, STEP_COUNT));
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        int i = 0;
                        for (File file : files) {
                            i++;
                            String filepath = file.getPath();
                            updateMessage("Loading " + i + " of " + n + ":" + filepath);

                            TEMSLoader loader = new TEMSLoader(null, filepath, getShell().getDisplay(), file.getName());
                            loader.setLimit(100);
                            loader.run(monitor);
                        }
                    } catch (IOException e) {
                        // TODO Handle IOException
                        e.printStackTrace();
                        throw (RuntimeException)new RuntimeException().initCause(e);
                    }
                }
            });
        } catch (Exception e) {
            // TODO Handle Exception
            e.printStackTrace();
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Creates a predicate that is necessary for distribution model creation
     * 
     * @param node
     * @return the predicate
     */
    private Predicate<org.neo4j.graphdb.Path> getPropertyReturnableEvaluator(final Node node) {
        return new Predicate<org.neo4j.graphdb.Path>() {

            @Override
            public boolean accept(org.neo4j.graphdb.Path item) {
                return item.endNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(NeoUtils.getPrimaryType(node));
            }
        };
    }

    /**
     * Increases step counter
     */
    private void nextStep() {
        step++;
    }

    /**
     * Updates the wizard's message in UI thread
     * 
     * @param message a new wizard message
     */
    private void updateMessage(final String message) {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                selectDataPage.setMessage(message);

            }
        });
    }

}
