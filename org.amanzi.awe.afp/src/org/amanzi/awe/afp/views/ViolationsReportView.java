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

package org.amanzi.awe.afp.views;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.awe.afp.Activator;
import org.amanzi.awe.report.editor.ReportEditor;
import org.amanzi.awe.report.util.ReportUtils;
import org.amanzi.awe.ui.AweUiPlugin;
import org.amanzi.awe.ui.custom_table.CustomTable;
import org.amanzi.neo.loader.core.utils.ITableExporter;
import org.amanzi.neo.loader.ui.utils.ExportUtils;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.network.NetworkModel;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.internal.ui.wizards.NewRubyElementCreationWizard;

/**
 * <p>
 * Violations Report View
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class ViolationsReportView extends ViewPart {
    private static final Logger LOGGER=Logger.getLogger(ViolationsReportView.class);
    private NetworkService ns = NeoServiceFactory.getInstance().getNetworkService();
    private CustomTable<ViolationReportModel> table;
    private Map<String, FrequencyPlanModel> plans = new HashMap<String, FrequencyPlanModel>();
    private Combo fplan;
    private ViolationReportModel reportModel;
    private Button export;
    private Button report;
    @Override
    public void createPartControl(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout(4, false));
        Label label=new Label(main, SWT.LEFT);
        label.setText("Frequncy plan");
        fplan = new Combo(main, SWT.READ_ONLY | SWT.BORDER);
        GridData data = new GridData();
//        data.horizontalSpan = 2;
        fplan.setLayoutData(data);
        export = new Button(main,SWT.PUSH);
        export.setText("Export");
        export.setEnabled(false);
        export.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                exportReport();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        report=new Button(main,SWT.PUSH);
        report.setText("Summary report");
        report.setEnabled(false);
        report.addSelectionListener(new SelectionAdapter(){

            @Override
            public void widgetSelected(SelectionEvent e) {
                generateReport();
            }});
        table = new CustomTable<ViolationReportModel>(reportModel, SWT.BORDER | SWT.FULL_SELECTION);
        table.createPartControl(main);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 4);
        table.getViewer().getControl().setLayoutData(layoutData);
        fplan.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectPlan(fplan.getText());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        setPlans();
    }

    /**
     * Generates the summary report based on the template
     */
    private void generateReport() {
        URL url;
        try {
            url = FileLocator.toFileURL(Activator.getDefault().getBundle().getEntry(
            "ruby/summary_report.rb"));
            String reportFileTemplate = ReportUtils.readScript(url.getPath());
            
            IRubyProject rubyProject = NewRubyElementCreationWizard.configureRubyProject(null, ApplicationGIS
                    .getActiveProject().getName());
            
            final IProject project = rubyProject.getProject();
            StringBuilder sb = new StringBuilder();
            final String text = fplan.getText();
            final int separatorIndex = text.indexOf(':');
            sb.append("dataset_name=\"").append(text.substring(0, separatorIndex)).append("\"\n");
            sb.append("plan_name=\"").append(text.substring(separatorIndex+1)).append("\"\n");
            sb.append(reportFileTemplate);
            IFile file;int i=0;
            while ((file = project.getFile(new Path(("report" + i) + ".r"))).exists()) {
                i++;
            }
            InputStream is = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
            file.create(is, true, null);
            is.close();
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new FileEditorInput(file),
                    ReportEditor.class.getName());
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        } catch (CoreException e) {
            // TODO Handle CoreException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }

    /**
     *
     */
    protected void exportReport() {
        FileDialog fd = new FileDialog(getSite().getShell(), SWT.SAVE); 
        fd.setText("Export table to CSV file");
        fd.setFilterPath(LoaderUiUtils.getDefaultDirectory());
        String[] filterExt = { "*.txt", "*.csv","*.*" };
        fd.setFilterExtensions(filterExt);
        fd.setFileName(FilenameUtils.getName(fplan.getText().trim().replace(':','_')));
        final String selected = fd.open();
        if (selected!=null){
            fplan.setEnabled(false);
            Job job=new Job("export"){

                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    ExportUtils exp = new ExportUtils();
                    exp.setSeparator('\t');
                    ITableExporter tableModel=new ITableExporter() {
                        
                        @Override
                        public int getRowCount() {
                            return reportModel.getRowsCount();
                        }
                        
                        @Override
                        public String getItem(int row, int column) {
                            return reportModel.getDataTxt(column, row);
                        }
                        
                        @Override
                        public String getHeader(int column) {
                            return reportModel.getColumnTxt(column);
                        }
                        
                        @Override
                        public int getColumnCount() {
                            return reportModel.getColumnsCount();
                        }
                    };
                    try {
                        exp.exportTable(tableModel, monitor, null, new File(selected));
                        return Status.OK_STATUS;
                    } catch (IOException e) {
                        return new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(),e);
                    }
                }
                
            };
            IJobChangeListener listener=new JobChangeAdapter(){
                public void done(IJobChangeEvent event) {
                    if( PlatformUI.getWorkbench().isClosing())
                        return;
                    
                    Display.getDefault().asyncExec(new Runnable(){
                        public void run() {
                           fplan.setEnabled(true);
                        }
                    });
                };
            };
            job.addJobChangeListener(listener);

            job.schedule();
        }
    }

    /**
     * @param text
     */
    protected void selectPlan(String text) {
        FrequencyPlanModel fp = plans.get(text);
        if (fp != null) {
            reportModel.setFrequencyPlanModel(fp);
        }
        export.setEnabled(fp!=null);
        report.setEnabled(fp!=null);
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        reportModel = new ViolationReportModel();
        super.init(site);
    }
    /**
     *
     */
    private void setPlans() {
        fplan.setItems(formPlans());
    }
    private String[] formPlans() {
        Map<NetworkModel, Set<FrequencyPlanModel>> models = ns.findAllFrequencyPlanWithSource(AweUiPlugin.getDefault()
                .getUiService().getActiveProjectNode());
        plans.clear();
        for (Entry<NetworkModel, Set<FrequencyPlanModel>> entry : models.entrySet()) {
            String name = entry.getKey().getName();
            for (FrequencyPlanModel model : entry.getValue()) {
                plans.put(new StringBuilder(name).append(':').append(model.getName()).toString(), model);
            }
        }
        return plans.keySet().toArray(new String[0]);
    }
    @Override
    public void setFocus() {
    }

}
