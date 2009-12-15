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

package org.amanzi.splash.report.model;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.nodes.CellNode;
import org.amanzi.neo.core.database.nodes.ReportNode;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.services.AweProjectService;
import org.amanzi.scripting.jruby.EclipseLoadService;
import org.amanzi.scripting.jruby.ScriptUtils;
import org.amanzi.splash.chart.Charts;
import org.amanzi.splash.database.services.ReportService;
import org.amanzi.splash.job.InitializeSplashTask;
import org.amanzi.splash.job.SplashJob;
import org.amanzi.splash.report.IReportModelListener;
import org.amanzi.splash.report.IReportPart;
import org.amanzi.splash.ui.SplashPlugin;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.ui.PlatformUI;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.internal.runtime.ValueAccessor;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.load.LoadService;
import org.neo4j.api.core.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko E.
 * @since 1.0.0
 */
public class ReportModel {
    /**
     * Path to report script
     */
    private static final String REPORT_SCRIPT = "ruby/report.rb";
    private static final String REPORT_MODEL_RUBY_NAME = "report_model";
    private static final String RUBY_PROJECT_NODE_ID_NAME = "RUBY_PROJECT_NODE_ID";
    /*
     * Ruby Runtime
     */
    private Ruby runtime;
    private String scriptInput;
    private SplashJob splashJob;
    private ReportNode reportNode;
    private RubyProjectNode rootNode;
    private AweProjectService projectService;
    private ReportService service;
    private Report report;
    private List<IReportModelListener> listeners = new ArrayList<IReportModelListener>(0);

    public ReportModel(String rubyProjectName) {
        this.projectService = NeoCorePlugin.getDefault().getProjectService();
        this.service = SplashPlugin.getDefault().getReportService();
        this.rootNode = projectService.findRubyProject(rubyProjectName);
        // this.report = new Report();
        try {
            initializeJRubyInterpreter();
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    public void initializeJRubyInterpreter() throws IOException {
        RubyInstanceConfig config = null;
        config = new RubyInstanceConfig() {
            {
                setJRubyHome(ScriptUtils.getJRubyHome()); // this helps online
                // help work
                setObjectSpaceEnabled(true); // useful for code completion
                // inside the IRB
                setLoadServiceCreator(new LoadServiceCreator() {
                    public LoadService create(Ruby runtime) {
                        return new EclipseLoadService(runtime);
                    }
                });
                setLoader(this.getClass().getClassLoader());
            }
        };

        runtime = Ruby.newInstance(config);
        runtime.getLoadService().init(ScriptUtils.makeLoadPath(new String[] {}));

        String path = "";
        URL scriptURL = null;
        try {
            scriptURL = FileLocator.toFileURL(SplashPlugin.getDefault().getBundle().getEntry(REPORT_SCRIPT));
        } catch (IOException e) {
            e.printStackTrace();
        }

        path = scriptURL.getPath();

        scriptInput = NeoSplashUtil.getScriptContent(path);

        HashMap<String, Object> globals = new HashMap<String, Object>();
        globals.put(REPORT_MODEL_RUBY_NAME, this);
        globals.put(RUBY_PROJECT_NODE_ID_NAME, rootNode.getUnderlyingNode().getId());
        // globals.put(JRUBY_PATH_RUBY_NAME, ScriptUtils.getJRubyHome());
        makeRubyGlobals(runtime, globals);

        splashJob = new SplashJob();
        splashJob.schedule();
        splashJob.addTask(new InitializeSplashTask(runtime, scriptInput));
    }

    /**
     * Utility method that creates a Ruby Global Variables from Java Objects
     * 
     * @param rubyRuntime Ruby Environment
     * @param globals Map with Names of Variables and Java Objects
     */

    private void makeRubyGlobals(Ruby rubyRuntime, HashMap<String, Object> globals) {
        for (String name : globals.keySet()) {
            IRubyObject rubyObject = JavaEmbedUtils.javaToRuby(rubyRuntime, globals.get(name));
            rubyRuntime.getGlobalVariables().define("$" + name, new ValueAccessor(rubyObject));
        }
    }

    public void updateModel(String script) {
        this.scriptInput = script;
        splashJob.schedule();
        splashJob.addTask(new InitializeSplashTask(runtime, script));
    }

    /**
     * Method intended to be called from Ruby
     * 
     * @param part part created by ruby model builder
     */

    public void createPart(final IReportPart part) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                if (report != null) {
                    report.addPart(part);
                    report.firePartAdded(part, scriptInput);
                }
            }
        });
    }

    /**
     * @return Returns the reportNode.
     */
    public ReportNode getReportNode() {
        return reportNode;
    }

    /**
     * @param reportNode The reportNode to set.
     */
    public void setReportNode(ReportNode reportNode) {
        this.reportNode = reportNode;
    }

    /**
     * @return Returns the rootNode.
     */
    public RubyProjectNode getRootNode() {
        return rootNode;
    }

    /**
     * @param rootNode The rootNode to set.
     */
    public void setRootNode(RubyProjectNode rootNode) {
        this.rootNode = rootNode;
    }

    /**
     * @return Returns the report.
     */
    public Report getReport() {
        return report;
    }

    /**
     * @param report The report to set.
     */
    public void setReport(Report report) {
        this.report = report;
    }

    /**
     * @param listener
     */
    public void addReportListener(IReportModelListener listener) {
        listeners.add(listener);
    }

    /**
     * @param listener
     */

    public void removeReportListener(IReportModelListener listener) {
        listeners.remove(listener);
    }

    public void changeReportName(String newName) {
        String reportName = reportNode.getReportName();
        if (!reportName.equals(newName)) {
            projectService.updateReportName(reportName, newName, rootNode);
        }
    }

    public void updateReport(Report newReport) {
        if (report != null) {
            report.removeAllReportListeners();
        }
        this.report = newReport;
        for (IReportModelListener l : listeners) {
            this.report.addReportListener(l);
        }
        // if (this.report == null) {
        // System.out.println("report == null");
        // this.report = newReport;
        // service.updateReport(rootNode, this.report.getName(), newReport);// TODO createReport
        // // process
        // } else if (isReportModified(newReport)) {
        // service.updateReport(rootNode, this.report.getName(), newReport);
        // }
        // this.report = newReport;
        // System.out.println("updateReport executed");
    }

    private boolean isReportModified(Report reportToCheck) {
        System.out.println("isReportModified");
        return isReportNameModified(reportToCheck) || isAuthorModified(reportToCheck) || isDateModified(reportToCheck)
        /* || isAnyPartModified(reportToCheck) */;
    }

    private boolean isAnyPartModified(Report reportToCheck) {
        final List<IReportPart> parts = report.getParts();
        final int n = parts.size();
        final List<IReportPart> newParts = reportToCheck.getParts();
        if (n != newParts.size()) {
            return true;
        } else {
            for (int i = 0; i < n; i++)
                if (!parts.get(i).equals(newParts.get(i))) {
                    return true;
                }
        }
        return false;
    }

    private boolean isDateModified(Report reportToCheck) {
        return (report.getDate() == null && reportToCheck.getDate() != null) || !report.getDate().equals(reportToCheck.getDate());
    }

    private boolean isAuthorModified(Report reportToCheck) {
        return (report.getAuthor() == null && reportToCheck.getAuthor() != null)
                || !report.getAuthor().equals(reportToCheck.getAuthor());
    }

    private boolean isReportNameModified(Report reportToCheck) {
        return (report.getName() == null && reportToCheck.getName() != null) || !report.getName().equals(reportToCheck.getName());
    }

    public DefaultCategoryDataset getChartDataset(Chart chart) {
        if (chart.isSheetBased()) {
            ArrayList<CellNode> categories = service.getCellRange(rootNode, chart.getSheet(), chart.getCategories());
            ArrayList<CellNode> values = service.getCellRange(rootNode, chart.getSheet(), chart.getValues());
            return Charts.getBarChartDataset(categories, values);

        } else if (chart.isNodeRangeBased()) {
            ArrayList<Node> nodes = service.getNodes(rootNode, chart.getNodeIds());
            return Charts.getBarChartDataset(nodes, chart.getCategoriesProperty(), chart.getValuesProperties());
        }
        return null;
    }
}
