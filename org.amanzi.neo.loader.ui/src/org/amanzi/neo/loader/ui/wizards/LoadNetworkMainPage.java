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

import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.core.parser.IDataElement;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.NetworkTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.amanzi.neo.services.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.DialogPage;
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
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class LoadNetworkMainPage extends LoaderPage<CommonConfigData> {
    /*
     * Names of supported files for Network
     */
    public static final String[] NETWORK_FILE_NAMES = {
        "All supported (*.*)",
        "Comma Separated Values Files (*.csv)",
        "Plain Text Files (*.txt)",
        "OpenOffice.org Spreadsheet Files (*.sxc)",
        "Microsoft Excel Spreadsheet Files (*.xls)",
        "eXtensible Markup Language Files (*.xml)"};
    
    /*
     * Extensions of supported files for Network
     */
    public static final String[] NETWORK_FILE_EXTENSIONS = {"*.*","*.csv", "*.txt", "*.sxc", "*.xls", "*.xml"};

    
    /** The Constant PAGE_DESCR. */
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
    

    private Button selectCRS;



    /**
     * Instantiates a new load network main page.
     */
    public LoadNetworkMainPage() {
        super("mainNetworkPage");
        setTitle(NeoLoaderPluginMessages.NetworkSiteImportWizard_PAGE_DESCR);
        networkNode = null;
    }


    @Override
    public void createControl(Composite parent) {
        main = new Group(parent, SWT.NULL);
        main.setLayout(new GridLayout(3, false));
        Label label = new Label(main, SWT.LEFT);
        label.setText(NeoLoaderPluginMessages.NetworkSiteImportWizard_NETWORK);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        network = new Combo(main, SWT.DROP_DOWN);
        network.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        network.setItems(getRootItems());
        network.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                changeNetworkName();
            }
        });
        network.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                changeNetworkName();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        selectCRS = new Button(main, SWT.FILL | SWT.PUSH);
        selectCRS.setAlignment(SWT.LEFT);
        GridData selCrsData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        selCrsData.widthHint=150;
        selectCRS.setLayoutData(selCrsData);
        selectCRS.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectCRS();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        
        editor = new FileFieldEditorExt("fileSelectNeighb", NeoLoaderPluginMessages.NetworkSiteImportWizard_FILE, main); // NON-NLS-1 //$NON-NLS-1$
        editor.setDefaulDirectory(LoaderUiUtils.getDefaultDirectory());

        editor.getTextControl(main).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setFileName(editor.getStringValue());
                if (networkName==null||networkName.trim().isEmpty()){
                    networkName=new java.io.File(getFileName()).getName(); 
                    network.setText(networkName);
                    changeNetworkName();
                    return;
                }
                update();
            }
        });
        
        editor.setFileExtensions(NETWORK_FILE_EXTENSIONS);
        editor.setFileExtensionNames(NETWORK_FILE_NAMES);
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
    @Override
    protected void update() {
        updateButtonLabel();
        super.update();
    }
    /**
     * Update button label.
     */
    private void updateButtonLabel() {
        CoordinateReferenceSystem crs = getSelectedCRS();
        selectCRS.setText(String.format("CRS: %s", crs.getName().toString()));
    }
 

    /**
     *
     */
    protected void updateLabelNetwDescr() {
        String text = ""; //$NON-NLS-1$
        if (networkNode != null) {
            NetworkTypes type = NetworkTypes.getNodeType(networkNode);
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
        if (this.fileName!=null&&this.fileName.equals(fileName)){
            return;
        }
        this.fileName = fileName;
        CommonConfigData configurationData = getConfigurationData();
        configurationData.setRoot(new File(fileName));
        ILoader< ? extends IDataElement, CommonConfigData> loader = autodefine(configurationData);
        int id=setSelectedLoader(loader);
        if (id>=0){
            networkType.select(id);
        }
        update();
        // editor.store();
        LoaderUiUtils.setDefaultDirectory(editor.getDefaulDirectory());
    }





    /**
     * Forms list of GIS nodes
     * 
     * @return array of GIS nodes
     */
    private String[] getRootItems() {
        final String  projectName = LoaderUiUtils.getAweProjectName();
        TraversalDescription td = Utils.getTDRootNodesOfProject(projectName, null);
        Node refNode = NeoServiceProviderUi.getProvider().getService().getReferenceNode();
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
        if (fileName==null){
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_FILE,DialogPage.ERROR); 
            return false;
        }
        File file = new File(fileName);
        if (!(file.isAbsolute() && file.exists())){
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_FILE,DialogPage.ERROR); 
            return false;         
        }
        if ( StringUtils.isEmpty(networkName)){
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_NETWORK,DialogPage.ERROR); 
            return false;
        }
        if (restrictedNames.contains(networkName)){
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_RESTRICTED_NETWORK_NAME,DialogPage.ERROR);  
            return false;
        }
        if (getSelectedLoader() == null){
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_TYPE,DialogPage.ERROR); 
            return false;           
        }
        configurationData.setProjectName(LoaderUiUtils.getAweProjectName());
        configurationData.setCrs(getSelectedCRS());
        configurationData.setDbRootName(networkName);
        configurationData.setRoot(file);
        IValidateResult result = getSelectedLoader().getValidator().validate(configurationData);
        if (result.getResult()==Result.FAIL){
            setMessage(String.format(result.getMessages(), getSelectedLoader().getDescription()),DialogPage.ERROR); 
            return false;          
        }else if (result.getResult()==Result.UNKNOWN){
            setMessage(String.format(result.getMessages(), getSelectedLoader().getDescription()),DialogPage.WARNING); 
        }else{
            setMessage(""); //$NON-NLS-1$
        }
        return true;
    }


    protected void changeNetworkName() {
        networkName = network.getText();
        networkNode = members.get(networkName);
        if (networkNode!=null){
            Node gis = NeoServiceFactory.getInstance().getDatasetService().findGisNode(networkNode);
            if (gis!=null){
                CoordinateReferenceSystem crs = NeoUtils.getCRS(gis, null);
                if (crs!=null){
                    selectCRS.setEnabled(false);
                    setSelectedCRS(crs);
                }else{
                    selectCRS.setEnabled(true);
                }
            }else{
                selectCRS.setEnabled(true);
            }
        }else{
            selectCRS.setEnabled(true);
        }
        getConfigurationData().setProjectName(LoaderUiUtils.getAweProjectName());
        getConfigurationData().setDbRootName(networkName);
        updateLabelNetwDescr();
        update();
    }


}
