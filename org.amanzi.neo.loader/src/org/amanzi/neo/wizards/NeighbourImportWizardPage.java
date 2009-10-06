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

import java.util.HashMap;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.LoadNetwork;
import org.eclipse.jface.preference.FileFieldEditor;
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
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.Transaction;

/**
 * <p>
 * Main page if NeighbourImportWizard
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class NeighbourImportWizardPage extends WizardPage {

    private String fileName;
    private Composite main;
    private Combo network;
    private FileFieldEditor editor;
    private HashMap<String, Node> members;
    protected Node networkNode;

    /**
     * Constructor
     * 
     * @param pageName page name
     * @param description page description
     */
    public NeighbourImportWizardPage(String pageName, String description) {
        super(pageName);
        setTitle(pageName);
        setDescription(description);
        setPageComplete(isValidPage());
        networkNode = null;
    }

    /**
     *check page
     * 
     * @return true if page valid
     */
    protected boolean isValidPage() {
        return fileName != null && networkNode != null;
    }

    @Override
    public void createControl(Composite parent) {
        main = new Group(parent, SWT.NULL);
        main.setLayout(new GridLayout(3, false));
        Label label = new Label(main, SWT.LEFT);
        label.setText("Network:");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        network = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        network.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        network.setItems(getGisItems());
        network.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                networkNode = network.getSelectionIndex() < 0 ? null : members.get(network.getText());
                setPageComplete(isValidPage());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        editor = new FileFieldEditor("fileSelect", "File: ", main); // NON-NLS-1
        editor.getTextControl(main).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setFileName(editor.getStringValue());
            }
        });
        editor.setFileExtensions(LoadNetwork.NETWORK_FILE_EXTENSIONS);
        setControl(main);
    }

    /**
     * Sets file name
     * 
     * @param fileName file name
     */
    protected void setFileName(String fileName) {
        this.fileName = fileName;
        setPageComplete(isValidPage());
    }

    /**
     * Forms list of GIS nodes
     * 
     * @return array of GIS nodes
     */
    private String[] getGisItems() {
        Transaction tx = NeoUtils.beginTransaction();
        try {
            NeoService service = NeoServiceProvider.getProvider().getService();
            Node refNode = service.getReferenceNode();
            members = new HashMap<String, Node>();
            String header = GisTypes.NETWORK.getHeader();
            for (Relationship relationship : refNode.getRelationships(Direction.OUTGOING)) {
                Node node = relationship.getEndNode();
                if (node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME)
                        && node.hasProperty(INeoConstants.PROPERTY_NAME_NAME)
                        && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).toString().equalsIgnoreCase(
                                INeoConstants.GIS_TYPE_NAME)
                        && header.equals(node.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, ""))) {
                    String id = node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
                    members.put(id, node);
                }
            }

            return members.keySet().toArray(new String[] {});
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
     * @return Returns the networkNode.
     */
    public Node getNetworkNode() {
        return networkNode;
    }

}
