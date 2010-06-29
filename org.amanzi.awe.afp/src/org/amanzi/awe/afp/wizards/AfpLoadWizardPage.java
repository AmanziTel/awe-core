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

package org.amanzi.awe.afp.wizards;

import java.io.File;
import java.util.HashMap;

import org.amanzi.awe.afp.files.ControlFile;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.NeighbourLoader;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.amanzi.neo.wizards.FileFieldEditorExt;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 * Page for loading afp data
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class AfpLoadWizardPage extends WizardPage {
    private static final Logger LOGGER = Logger.getLogger(AfpLoadWizardPage.class);
    private Combo dataset;
    protected String datasetName;
    private HashMap<String, Node> members;
    protected Node datasetNode;
    private FileFieldEditorExt editor;
    private String fileName;
    private final GraphDatabaseService service;
    protected ControlFile controlFile = null;

    /**
     * Instantiates a new afp load wizard page.
     * 
     * @param pageName the page name
     * @param servise the servise
     */
    public AfpLoadWizardPage(String pageName, GraphDatabaseService servise) {
        super(pageName, "Load AFP data", null);
        this.service = servise;
    }

    @Override
    public void createControl(Composite parent) {
        Group main = new Group(parent, SWT.FILL);
        main.setLayout(new GridLayout(3, false));
        Label label = new Label(main, SWT.LEFT);
        label.setText("AFP Dataset");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        dataset = new Combo(main, SWT.DROP_DOWN);
        dataset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        dataset.setItems(getAfpDatasets());
        dataset.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                datasetName = dataset.getText();
                datasetNode = members.get(datasetName);
                setPageComplete(isValidPage());
            }
        });
        dataset.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                datasetName = dataset.getText();
                datasetNode = members.get(datasetName);
                setPageComplete(isValidPage());
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
                if (StringUtils.isEmpty(datasetName)) {
                    datasetName = new java.io.File(getFileName()).getName();
                    dataset.setText(datasetName);
                    datasetNode = members.get(datasetName);
                    setPageComplete(isValidPage());
                }
            }
        });
        editor.setFileExtensions(new String[] {"*.*"});
        editor.setFileExtensionNames(new String[] {"All Fiels(*.*)"});
        editor.setFocus();
        setControl(main);
    }


    /**
     * Checks if is valid page.
     * 
     * @return true, if is valid page
     */
    protected boolean isValidPage() {
        if (controlFile == null) {
            return false;
        }
        // if (StringUtils.isEmpty(getFileName())) {
        // return false;
        // }
        if (StringUtils.isEmpty(datasetName)) {
            return false;
        }
        if (datasetNode == null) {
            Node root = NeoUtils.findRootNodeByName(datasetName, service);
            if (root != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets file name
     * 
     * @param fileName file name
     */
    protected void setFileName(String fileName) {
        this.fileName = fileName;
        try {
            controlFile = new ControlFile(new File(fileName));
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            controlFile = null;
        }
        setPageComplete(isValidPage());
        // editor.store();
        NeighbourLoader.setDirectory(editor.getDefaulDirectory());
    }

    /**
     * Gets the afp datasets.
     * 
     * @return the afp datasets
     */
    private String[] getAfpDatasets() {
        members = new HashMap<String, Node>();
        Transaction tx = service.beginTx();
        try {
            for (Node root : NeoUtils.getAllRootTraverser(service, null)) {
                if (NodeTypes.AFP.checkNode(root)) {
                    members.put(NeoUtils.getNodeName(root, service), root);
                }
            }
        } finally {
            tx.finish();
        }
        return members.keySet().toArray(new String[0]);
    }

    /**
     * @return Returns the fileName.
     */
    public String getFileName() {
        return fileName;
    }

}
