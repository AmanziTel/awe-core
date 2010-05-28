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

package org.amanzi.awe.views.calls.views;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.amanzi.awe.views.calls.CallAnalyserPlugin;
import org.amanzi.awe.views.calls.Messages;
import org.amanzi.awe.views.calls.upload.StatisticsDataLoader;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser;

/**
 * <p>
 * Wizard for import AMS statistics data.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class AmsImportWizard extends Wizard implements IImportWizard {
    
    public static final String DEFAULT_DIRRECTORY_AMS_IMPORT = "DEFAULT_DIRRECTORY_AMS_IMPORT";
    
    private static final String PAGE_TITLE = Messages.AIW_PAGE_TITLE;
    private static final String PAGE_DESCR = Messages.AIW_PAGE_DESCR;

    private AmsImportWizardPage mainPage;

    @Override
    public boolean performFinish() {
        final String fileName = mainPage.getFileName();
        final String dataset = mainPage.getDataset();
        final String network = mainPage.getNetwork();
        Job job = new Job("Import AMS statistics '" + (new File(fileName)).getName() + "'") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {                
                StatisticsDataLoader loader = new StatisticsDataLoader(fileName, dataset, network, null);                
                try {
                    loader.run(monitor);
                } catch (IOException e) {
                    return new Status(Status.ERROR, "org.amanzi.awe.views.calls", e.getMessage());
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        mainPage = new AmsImportWizardPage(PAGE_TITLE, PAGE_DESCR);
    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }
    
    private class AmsImportWizardPage extends WizardPage {
        
        private Composite cMain; 
        private Combo cDataset;
        private Combo cNetwork;
        private DirectoryFieldEditor fileEditor;
        
        private String fileName;               
        private ArrayList<String> datasets;
        private String dataset;
        private ArrayList<String> wrongDatasets;
        
        private ArrayList<String> networks;
        private String network;
        
        /**
         * Constructor.
         * @param title
         * @param description
         */
        public AmsImportWizardPage(String title, String description){
            super(title);
            setTitle(title);
            setDescription(description);
        }
        
        /**
         * @return Returns the fileName.
         */
        public String getFileName() {
            return fileName;
        }
        
        /**
         * @return Returns the dataset.
         */
        public String getDataset() {
            return dataset;
        }
        
        /**
         * @return Returns the network.
         */
        public String getNetwork() {
            return network;
        }

        @Override
        public void createControl(Composite parent) {
            cMain = new Group(parent, SWT.NULL);
            cMain.setLayout(new GridLayout(3, false));
            
            Label label = new Label(cMain, SWT.LEFT);
            label.setText(Messages.AIW_DATASET);
            label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
            cDataset = new Combo(cMain, SWT.DROP_DOWN);
            cDataset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
            cDataset.setItems(getAllDatasets());
            cDataset.addModifyListener(new ModifyListener() {                
                @Override
                public void modifyText(ModifyEvent e) {
                    dataset = cDataset.getText();
                    setPageComplete(isValidPage());
                }
            });
            cDataset.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    boolean selected = cDataset.getSelectionIndex() < 0;
                    dataset = selected ? null : datasets.get(cDataset.getSelectionIndex());
                    setPageComplete(isValidPage());
                }
                
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                }
            }); 
            

            Label networkLabel = new Label(cMain, SWT.LEFT);
            networkLabel.setText(Messages.AIW_NETWORK);
            networkLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
            cNetwork = new Combo(cMain, SWT.DROP_DOWN);
            cNetwork.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 2));
            cNetwork.setItems(getGisItems());
            cNetwork.addModifyListener(new ModifyListener() {
                
                @Override
                public void modifyText(ModifyEvent e) {
                    network = cNetwork.getText();
                }
            });        
            cNetwork.addSelectionListener(new SelectionListener() {
                
                @Override
                public void widgetSelected(SelectionEvent e) {
                    boolean selected = cNetwork.getSelectionIndex() < 0;
                    network = selected ? null : networks.get(cNetwork.getSelectionIndex());
                }
                
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                }
            });
                        
            fileEditor = new DirectoryEditor(Messages.AIW_DIR_EDITOR_TITLE, Messages.AIW_DIRECTORY, cMain); 
            fileEditor.getTextControl(cMain).addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    String oldFileName = fileName;
                    setFileName(fileEditor.getStringValue());
                    if (needToGenerateDatasetName(oldFileName)) {
                        cDataset.setText(getDatasetDefaultName(fileName));
                    }
                    if (needToGenerateNetworkName(oldFileName)) {
                        cNetwork.setText(getNetworkDefaultName(fileName));
                    }
                }
            });
            setControl(cMain);
        }
        
        /**
         * Get all exists datasets.
         * 
         * @return String[]
         */
        private String[] getAllDatasets() {
            Transaction tx = NeoUtils.beginTransaction();
            try {           
                datasets = new ArrayList<String>();
                wrongDatasets = new ArrayList<String>();
                Traverser allDatasetTraverser = NeoCorePlugin.getDefault().getProjectService().getAllDatasetTraverser(
                        NeoServiceProvider.getProvider().getService().getReferenceNode());
                for (Node node : allDatasetTraverser) {
                    String name = (String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME);
                    if (NeoUtils.getDatasetType(node, null)!=DriveTypes.AMS){
                        wrongDatasets.add(name); 
                    }else{
                        datasets.add(name);
                    }
                }
                Collections.sort(datasets);
                return datasets.toArray(new String[] {});
            } finally {
                tx.finish();
            }
        }
        
        private String[] getGisItems() {
            Transaction tx = NeoUtils.beginTransaction();
            try {
                GraphDatabaseService service = NeoServiceProvider.getProvider().getService();
                Node refNode = service.getReferenceNode();
                networks = new ArrayList<String>();
                String header = GisTypes.NETWORK.getHeader();
                for (Relationship relationship : refNode.getRelationships(Direction.OUTGOING)) {
                    Node node = relationship.getEndNode();
                    if (node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME)
                            && node.hasProperty(INeoConstants.PROPERTY_NAME_NAME)
                            && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).toString().equalsIgnoreCase(
                                    NodeTypes.GIS.getId())
                            && header.equals(node.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, ""))) {
                        String id = node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
                        networks.add(id);
                    }
                }
                Collections.sort(networks);
                return networks.toArray(new String[] {});
            } finally {
                tx.finish();
            }
        }
        
        /**
         * @return true if page has all data
         */
        protected boolean isValidPage() {
            return fileName != null && dataset != null && !dataset.trim().isEmpty() && !wrongDatasets.contains(dataset);
        }
        
        /**
         * Sets file name.
         * 
         * @param fileName file name
         */
        protected void setFileName(String fileName) {
            this.fileName = fileName;
            setPageComplete(isValidPage());
            setDefaultDirectory(fileName);
        }
        
        /**
         * Check is need to generate new dataset name.
         *
         * @param aFileName String
         * @return boolean
         */
        private boolean needToGenerateDatasetName(String aFileName) {
            String text = cDataset.getText();
            if (text.isEmpty()){
                return true;
            }
            String defName = getDatasetDefaultName(aFileName);
            return text.equals(defName);
        }
        
        /**
         * Generate new dataset name.
         *
         * @param aFileName String
         * @return String
         */
        private String getDatasetDefaultName(String aFileName) {
            if(aFileName == null){
                return "";
            }
            String result = aFileName;
            int index = result.lastIndexOf(File.separator);
            if (index > 0) {
                return result.substring(index + 1);
            }
            return result;      
        }
        
        private boolean needToGenerateNetworkName(String aFileName) {
            String text = cNetwork.getText();
            if (text.isEmpty()){
                return true;
            }
            String defName = getNetworkDefaultName(aFileName);
            return text.equals(defName);
        }
        
        private String getNetworkDefaultName(String aFileName) {
            if(aFileName == null){
                return "";
            }
            String result = aFileName;
            int index = result.lastIndexOf(File.separator);
            if (index > 0) {
                result = result.substring(index + 1);
            }
            if ((result == null) || (result.length() == 0)) {
                return result;
            }
            return result + " Probes";      
        }
    }

    /**
     * <p>
     * Directory editor for wizard.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class DirectoryEditor extends DirectoryFieldEditor {
        
        /**
         * Constructor.
         * @param name the name of the preference this field editor works on
         * @param labelText the label text of the field editor
         * @param parent the parent of the field editor's control
         */
        public DirectoryEditor(String name, String labelText, Composite parent) {
            super(name, labelText, parent);
        }
        
        @Override
        protected String changePressed() {
            getTextControl().setText(getDefaultDirectory());            
            return super.changePressed();
        }
        
    }
    
    /**
     * @return default directory name from workspace.
     */
    public static String getDefaultDirectory() {
        return CallAnalyserPlugin.getDefault().getPluginPreferences().getString(DEFAULT_DIRRECTORY_AMS_IMPORT);
    }
    
    /**
     * Sets default directory name to workspace.
     *
     * @param newDirectory String
     */
    public static void setDefaultDirectory(String newDirectory) {
        CallAnalyserPlugin.getDefault().getPluginPreferences().setValue(DEFAULT_DIRRECTORY_AMS_IMPORT, newDirectory);
    }
}
