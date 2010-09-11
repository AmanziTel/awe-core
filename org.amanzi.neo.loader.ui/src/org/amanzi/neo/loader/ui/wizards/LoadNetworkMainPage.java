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

package org.amanzi.neo.loader.ui.wizards;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NetworkTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.db.manager.DatabaseManager.DatabaseAccessType;
import org.amanzi.neo.loader.LoadNetwork;
import org.amanzi.neo.loader.LoaderUtils;
import org.amanzi.neo.loader.NeighbourLoader;
import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.amanzi.neo.wizards.FileFieldEditorExt;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.TraversalDescription;

/**
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class LoadNetworkMainPage extends LoaderPage<CommonConfigData> {
    /** The Constant PAGE_TITLE. */
    private static final String PAGE_TITLE = NeoLoaderPluginMessages.NetworkSiteImportWizard_PAGE_TITLE;
    
    /** The Constant PAGE_DESCR. */
    private static final String PAGE_DESCR = NeoLoaderPluginMessages.NetworkSiteImportWizard_PAGE_DESCR;
    private String fileName;
    private Composite main;
    protected Combo network;
    private FileFieldEditorExt editor;
    private HashMap<String, Node> members;
    private final Set<String> restrictedNames=new HashSet<String>();
    protected Node networkNode;
    private Label labNetworkDescr;
    private Combo networkType;
    protected String networkName=""; //$NON-NLS-1$
    
    private boolean needCheckFilds;

    /**
     * Instantiates a new load network main page.
     */
    public LoadNetworkMainPage() {
        super("mainNetworkPage");
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCR);
        networkNode = null;

    }


    @Override
    public void createControl(Composite parent) {
        needCheckFilds = false;
        main = new Group(parent, SWT.NULL);
        main.setLayout(new GridLayout(3, false));
        Label label = new Label(main, SWT.LEFT);
        label.setText(NeoLoaderPluginMessages.NetworkSiteImportWizard_NETWORK);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        network = new Combo(main, SWT.DROP_DOWN);
        network.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        network.setItems(getGisItems());
        network.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                networkName = network.getText();
                networkNode = members.get(networkName);   
                update();
            }
        });
        network.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                networkName = network.getText();
                networkNode = members.get(networkName);
                updateLabelNetwDescr();
                update();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        editor = new FileFieldEditorExt("fileSelectNeighb", NeoLoaderPluginMessages.NetworkSiteImportWizard_FILE, main); // NON-NLS-1 //$NON-NLS-1$
        editor.setDefaulDirectory(NeighbourLoader.getDirectory());

        editor.getTextControl(main).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setFileName(editor.getStringValue());
                needCheckFilds = true;
                if (networkName==null||networkName.trim().isEmpty()){
                    networkName=new java.io.File(getFileName()).getName(); 
                    network.setText(networkName);
                    networkNode = members.get(networkName);
                }
                update();
            }
        });
        
        editor.setFileExtensions(LoadNetwork.NETWORK_FILE_EXTENSIONS);
        editor.setFileExtensionNames(LoadNetwork.NETWORK_FILE_NAMES);
        label = new Label(main, SWT.LEFT);
        label.setText(NeoLoaderPluginMessages.NetworkSiteImportWizard_DATA_TYPE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        networkType = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        networkType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        networkType.setItems(getLoadersDescriptions());
        networkType.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectLoader(networkType.getSelectionIndex());
                update();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        new Label(main,SWT.NONE);
        final Button batchMode = new Button(main,SWT.CHECK);
        batchMode.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                setAccessType(batchMode.getSelection());
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        batchMode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        batchMode.setText("batch mode");
        labNetworkDescr = new Label(main, SWT.LEFT);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL, SWT.CENTER, true, false, 3, 1);
        layoutData.minimumWidth = 150;
        editor.setFocus();
        setControl(main);
        update();
    }
    
    /**
     * Sets the access type.
     *
     * @param batchMode the new access type
     */
    protected void setAccessType(final boolean batchMode) {
        ((AbstractLoaderWizard<?>)getWizard()).setAccessType(batchMode?DatabaseManager.DatabaseAccessType.BATCH:DatabaseAccessType.EMBEDDED);
    }
    /**
     *
     */
    protected void updateLabelNetwDescr() {
        String text = ""; //$NON-NLS-1$
        if (networkNode != null) {
            NetworkTypes type = NetworkTypes.getNodeType(networkNode, NeoServiceProvider.getProvider().getService());
            if (type != null) {
                text = "Network type: " + type.getId(); //$NON-NLS-1$
            }
        }
        labNetworkDescr.setText(text);
    }

    /**
     * Sets file name
     * 
     * @param fileName file name
     */
    protected void setFileName(String fileName) {
        this.fileName = fileName;
        update();
        // editor.store();
        NeighbourLoader.setDirectory(editor.getDefaulDirectory());
    }

    /**
     * Forms list of GIS nodes
     * 
     * @return array of GIS nodes
     */
    private String[] getGisItems() {
        // TODO find in active project?
        TraversalDescription td = NeoUtils.getTDRootNodes(null);
        Node refNode = DatabaseManager.getInstance().getCurrentDatabaseService().getReferenceNode();
        restrictedNames.clear();
        members = new HashMap<String, Node>();
        for (Node node : td.traverse(refNode).nodes()) {
            String id = node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
            if (NodeTypes.NETWORK.checkNode(node)) { //$NON-NLS-1$
                members.put(id, node);
            } else {
                restrictedNames.add(id);
            }
        }

        String[] result = members.keySet().toArray(new String[] {});
        Arrays.sort(result);
        return result;
    }

    /**
     * @return Returns the fileName.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return Returns the networkNode.
     */
    public Node getNetworkNode() {
        return networkNode;
    }

    /**
     * @return Returns the networkName.
     */
    public String getNetworkName() {
        return networkName;
    }


    @Override
    protected boolean validateConfigData(CommonConfigData configurationData) {
        //TODO must be refactoring after change loaders
        if(!needCheckFilds){
            return false;
        }
        if (fileName==null){
            setDescription(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_FILE); 
            return false;
        }
        File file = new File(fileName);
        if (!(file.isAbsolute() && file.exists())){
            setDescription(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_FILE); 
            return false;         
        }
        if ( StringUtils.isEmpty(networkName)){
            setDescription(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_NETWORK);  
            return false;
        }
        if (restrictedNames.contains(networkName)){
            setDescription(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_RESTRICTED_NETWORK_NAME); 
            return false;
        }
        if (getSelectedLoader() == null){
            setDescription(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_TYPE); 
            return false;           
        }
        configurationData.setProjectName(LoaderUtils.getAweProjectName());
        configurationData.setDbRootName(networkName);
        configurationData.setRoot(file);
        IValidateResult result = getSelectedLoader().getValidator().validate(configurationData);
        if (result.getResult()==Result.FAIL){
            setDescription(String.format(result.getMessages(), getSelectedLoader().getDescription())); 
            return false;          
        }
        setDescription(NeoLoaderPluginMessages.NetworkSiteImportWizard_PAGE_DESCR); //$NON-NLS-1$
        return true;
    }

}
