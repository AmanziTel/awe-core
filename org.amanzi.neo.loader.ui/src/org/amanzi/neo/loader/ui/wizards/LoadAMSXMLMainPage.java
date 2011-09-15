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
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.ILoaderNew;
import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.core.newsaver.IData;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.TraversalDescription;

/**
 * <p>
 * gui for amsxml loader
 * </p>
 * 
 * @author Kondratenko_Vladsialv
 */
public class LoadAMSXMLMainPage extends LoaderPage<CommonConfigData> {

    public static final String[] AMS_XML_FILE_NAMES = {"All supported (*.*)", "eXtensible Markup Language Files (*.xml)"};

    /*
     * Extensions of supported files for Network
     */
    public static final String[] AMS_XML_FILE_EXTENSION = {"*.*", "*.xml"};

    /** The Constant PAGE_DESCR. */
    private String fileName;
    private Composite main;
    protected Combo network;
    private DirectoryEditor editor;
    private HashMap<String, Node> members;
    private final Set<String> restrictedNames = new HashSet<String>();
    protected Node networkNode;
    private Label labNetworkDescr;
    private Combo dataset;
    protected String networkName = ""; //$NON-NLS-1$
    protected String datasetName = "";
    private IConfiguration config;

    /**
     * Instantiates a new load network main page.
     */
    public LoadAMSXMLMainPage() {
        super("mainAmsXmlPage");
        setTitle(NeoLoaderPluginMessages.AMSImport_page_title);
        networkNode = null;
    }

    @Override
    public void createControl(Composite parent) {
        main = new Group(parent, SWT.NULL);
        main.setLayout(new GridLayout(3, false));
        Label label = new Label(main, SWT.LEFT);
        label.setText(NeoLoaderPluginMessages.AMSImport_network);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false, 1, 1));
        network = new Combo(main, SWT.DROP_DOWN);
        network.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
        network.setItems(getRootItems(NodeTypes.NETWORK));
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

        editor = new DirectoryEditor("SelectedProbesDirectory", NeoLoaderPluginMessages.AMSImport_directory, main); // NON-NLS-1 //$NON-NLS-1$

        editor.getTextControl(main).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                ILoaderNew< ? extends IData, IConfiguration> loader = setFileName(editor.getStringValue());
                    networkName = new java.io.File(getFileName()).getName();
                    datasetName = networkName;
                    network.setText(networkName + " Probes");
                    dataset.setText(datasetName);
                    changeNetworkName();
                    changeDatasetName();

            }

        });

        label = new Label(main, SWT.LEFT);
        label.setText(NeoLoaderPluginMessages.AMSImport_dataset);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false, 1, 3));
        dataset = new Combo(main, SWT.DROP_DOWN);
        dataset.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 3));
        dataset.setItems(getRootItems(NodeTypes.DATASET));
        dataset.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectNewLoader(dataset.getSelectionIndex());
                update();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        dataset.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                changeDatasetName();
            }
        });
        new Label(main, SWT.NONE);
        // LN, 28.02.2011, batch mode removed
        labNetworkDescr = new Label(main, SWT.LEFT);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL, SWT.CENTER, true, false, 3, 3);
        layoutData.minimumWidth = 100;
        editor.setFocus();
        setControl(main);
        update();
    }

    @Override
    protected void update() {
        super.update();
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
     * @param fileName file or directory name
     * @return configured loader or null if there was an error
     */
    protected ILoaderNew< ? extends IData, IConfiguration> setFileName(String fileName) {
        if (this.fileName != null && this.fileName.equals(fileName)) {
            return null;
        }
        this.fileName = fileName;
        networkName = new java.io.File(getFileName()).getName();
        datasetName = networkName;
        // CommonConfigData configurationData = getConfigurationData();
        getNewConfigurationData().setSourceFile(new File(fileName));

        // config.getFilesToLoad()
        // configurationData.setRoot(new File(fileName));
        ILoaderNew< ? extends IData, IConfiguration> loader = autodefineNew(getNewConfigurationData());
        int id = setSelectedLoaderNew(loader);
        if (id >= 0) {
            dataset.select(id);
        }
        update();
        editor.store();
        return loader;
    }

    /**
     * Forms list of GIS nodes
     * 
     * @return array of GIS nodes
     */
    private String[] getRootItems(NodeTypes type) {
        final String projectName = LoaderUiUtils.getAweProjectName();
        TraversalDescription td = Utils.getTDRootNodesOfProject(projectName, null);
        Node refNode = NeoServiceProviderUi.getProvider().getService().getReferenceNode();
        restrictedNames.clear();
        members = new HashMap<String, Node>();
        for (Node node : td.traverse(refNode).nodes()) {
            String id = node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
            if (type.checkNode(node)) { //$NON-NLS-1$
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
        // TODO must be refactoring after change loaders
        if (fileName == null) {
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_FILE, DialogPage.ERROR);
            return false;
        }
        File file = new File(fileName);
        if (!(file.isAbsolute() && file.exists())) {
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_FILE, DialogPage.ERROR);
            return false;
        }
        if (StringUtils.isEmpty(networkName)) {
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_NETWORK, DialogPage.ERROR);
            return false;
        }
        if (restrictedNames.contains(networkName)) {
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_RESTRICTED_NETWORK_NAME, DialogPage.ERROR);
            return false;
        }
        // if (getNewSelectedLoader() == null) {
        // setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_TYPE,
        // DialogPage.ERROR);
        // return false;
        // }
        networkName = file.getName();
        configurationData.setProjectName(LoaderUiUtils.getAweProjectName());
        configurationData.setDbRootName(networkName);
        configurationData.setRoot(file);
        // IConfiguration config=new Con
        IValidateResult.Result result = getNewSelectedLoader().getValidator().isValid(getNewConfigurationData().getFilesToLoad());
        if (result == Result.FAIL) {
            setMessage(String.format(getNewSelectedLoader().getValidator().getMessages()), DialogPage.ERROR);
            return false;
        } else if (result == Result.UNKNOWN) {
            setMessage(String.format(getNewSelectedLoader().getValidator().getMessages()), DialogPage.WARNING);
        } else {
            setMessage(""); //$NON-NLS-1$
        }
        return true;
    }

    protected void changeNetworkName() {
        networkName = network.getText();
        networkNode = members.get(networkName);
        getNewConfigurationData().getDatasetNames().put("Project", LoaderUiUtils.getAweProjectName());
        getNewConfigurationData().getDatasetNames().put("Network", networkName);
        update();
    }

    protected void changeDatasetName() {
        datasetName = dataset.getText();
        getNewConfigurationData().getDatasetNames().put("Dataset", datasetName);
        getNewConfigurationData().getDatasetNames().put("Calls", datasetName + " Calls");
        getNewConfigurationData().getDatasetNames().put("Pesq", datasetName + " Pesq");
        update();
    }
}
