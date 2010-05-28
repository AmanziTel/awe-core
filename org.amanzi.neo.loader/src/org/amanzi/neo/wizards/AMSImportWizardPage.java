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
package org.amanzi.neo.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.dialogs.DriveDialog;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.eclipse.jface.preference.DirectoryFieldEditor;
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
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser;

/**
 * <p>
 * Main page if AMSImportWizard
 * </p>
 * 
 * @author Lagutko_n
 * @since 1.0.0
 */
public class AMSImportWizardPage extends WizardPage {
	
    private String fileName;
    private Composite main;
    private Combo dataset;
    private Combo network;
    private DirectoryFieldEditor editor;
    private ArrayList<String> datasetMembers;
    private ArrayList<String> networkMembers;
    private String datasetName;
    private String networkName;
    private ArrayList<String> wrongDatasetMembers;

    /**
     * Constructor
     * 
     * @param pageName page name
     * @param description page description
     */
    public AMSImportWizardPage(String pageName, String description) {
        super(pageName);
        setTitle(pageName);
        setDescription(description);
        setPageComplete(isValidPage());
    }

    /**
     *check page
     * 
     * @return true if page valid
     */
    protected boolean isValidPage() {
        return fileName != null && datasetName != null && !datasetName.trim().isEmpty()
                && !wrongDatasetMembers.contains(datasetName);
    }

    @Override
    public void createControl(Composite parent) {
        main = new Group(parent, SWT.NULL);
        main.setLayout(new GridLayout(3, false));
        Label label = new Label(main, SWT.LEFT);
        label.setText(NeoLoaderPluginMessages.AMSImport_dataset);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        dataset = new Combo(main, SWT.DROP_DOWN);
        dataset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        dataset.setItems(getAllDatasets());
        dataset.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
                datasetName = dataset.getText();
                setPageComplete(isValidPage());
            }
		});
        dataset.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selected = dataset.getSelectionIndex() < 0;
				datasetName = selected ? null : datasetMembers.get(dataset.getSelectionIndex());
                setPageComplete(isValidPage());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });        
        
        Label networkLabel = new Label(main, SWT.LEFT);
        networkLabel.setText(NeoLoaderPluginMessages.AMSImport_network);
        networkLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
        network = new Combo(main, SWT.DROP_DOWN);
        network.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 2));
        network.setItems(getGisItems());
        network.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				networkName = network.getText();
			}
		});        
        network.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selected = network.getSelectionIndex() < 0;
				networkName = selected ? null : networkMembers.get(network.getSelectionIndex());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
        
        editor = new DirectoryEditor(NeoLoaderPluginMessages.AMSImport_dir_editor_title, 
        		NeoLoaderPluginMessages.AMSImport_directory, main); // NON-NLS-1
        editor.getTextControl(main).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
            	String oldFileName = fileName;
                setFileName(editor.getStringValue());
                if (needToGenerateDatasetName(oldFileName)) {
                    dataset.setText(getDatasetDefaultName(fileName));
                }
                if (needToGenerateNetworkName(oldFileName)) {
                    network.setText(getNetworkDefaultName(fileName));
                }
            }
        });
        setControl(main);
    }
    
    private boolean needToGenerateDatasetName(String aFileName) {
		String text = dataset.getText();
		if (text.isEmpty()){
			return true;
		}
		String defName = getDatasetDefaultName(aFileName);
		return text.equals(defName);
	}
    
    private boolean needToGenerateNetworkName(String aFileName) {
		String text = network.getText();
		if (text.isEmpty()){
			return true;
		}
		String defName = getNetworkDefaultName(aFileName);
		return text.equals(defName);
	}
    
    public static String getDatasetDefaultName(String aFileName) {
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

    /**
     * Sets file name
     * 
     * @param fileName file name
     */
    protected void setFileName(String fileName) {
        this.fileName = fileName;
        setPageComplete(isValidPage());
        DriveDialog.setDefaultDirectory(fileName);
    }

    /**
     * Forms list of Datasets
     * 
     * @return array of Datasets nodes
     */
    private String[] getAllDatasets() {
        Transaction tx = NeoUtils.beginTransaction();
        try {        	
            datasetMembers = new ArrayList<String>();
        	wrongDatasetMembers = new ArrayList<String>();
        	Traverser allDatasetTraverser = NeoCorePlugin.getDefault().getProjectService().getAllDatasetTraverser(
                    NeoServiceProvider.getProvider().getService().getReferenceNode());
            for (Node node : allDatasetTraverser) {
                String name = (String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME);
                if (NeoUtils.getDatasetType(node, null)!=DriveTypes.AMS){
                    wrongDatasetMembers.add(name); 
                }else{
                    datasetMembers.add(name);
                }
            }
            Collections.sort(datasetMembers);
            return datasetMembers.toArray(new String[] {});
        } finally {
            tx.finish();
        }
    }
    
    /**
     * Forms list of GIS nodes
     * 
     * @return array of GIS nodes
     */
    private String[] getGisItems() {
        Transaction tx = NeoUtils.beginTransaction();
        try {
            GraphDatabaseService service = NeoServiceProvider.getProvider().getService();
            Node refNode = service.getReferenceNode();
            networkMembers = new ArrayList<String>();
            String header = GisTypes.NETWORK.getHeader();
            for (Relationship relationship : refNode.getRelationships(Direction.OUTGOING)) {
                Node node = relationship.getEndNode();
                if (node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME)
                        && node.hasProperty(INeoConstants.PROPERTY_NAME_NAME)
                        && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).toString().equalsIgnoreCase(
                                NodeTypes.GIS.getId())
                        && header.equals(node.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, ""))) {
                    String id = node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
                    networkMembers.add(id);
                }
            }
            Collections.sort(networkMembers);
            return networkMembers.toArray(new String[] {});
        } finally {
            tx.finish();
        }
    }

    /**
     * @return Returns the fileName.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return Returns the selected Dataset name.
     */
    public String getDatasetName() {
        return datasetName;
    }
    
    public String getNetworkName() {
    	return networkName;
    }
}
