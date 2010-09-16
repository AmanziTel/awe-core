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

package org.amanzi.awe.views.network.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.AbstractDialog;
import org.amanzi.neo.core.utils.NeoUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class NewTypeDialog extends AbstractDialog<Integer> {

    private static final int MIN_FIELD_WIDTH = 50;
    private final Node node;
    private final GraphDatabaseService service;
    private Combo cNetwork;
    private Combo cNodeTypes;
    private Text tNewNode;
    private final LinkedHashMap<String, Node> gisNetworkNodes = new LinkedHashMap<String, Node>();
    private Button bOk;
    private Button bCancel;
    private Shell shell;
    private Label errorLabel;

    // public NewTypeDialog(Shell parent, String title, int style) {
    // super(parent, "Dataset properties configura\tion", SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL |
    // SWT.CENTER);
    // // this.dataset = dataset;
    // status = SWT.CANCEL;
    // service = NeoServiceProvider.getProvider().getService();
    // }

    public NewTypeDialog(Shell parent, String title, Node node, int style) {
        super(parent, title, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        this.node = node;
        status = SWT.CANCEL;
        service = NeoServiceProvider.getProvider().getService();
    }

    @Override
    protected void createContents(Shell shell) {
        this.shell = shell;

        shell.setImage(NodeTypes.DATASET.getImage());
        shell.setLayout(new GridLayout(2, true));

        // errorLabel = new Label(shell, SWT.FLAT);
        // errorLabel.setText("no_errors");
        // errorLabel.setForeground(new org.eclipse.swt.graphics.Color(shell.getDisplay(),255,0,0));
        // errorLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,2,1));
        // errorLabel.setVisible(false);

        Label label = new Label(shell, SWT.NONE);
        label.setText("Target network");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cNetwork = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);

        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        cNetwork.setLayoutData(layoutData);

        label = new Label(shell, SWT.NONE);
        label.setText("Parent node");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cNodeTypes = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        cNodeTypes.setLayoutData(layoutData);

        label = new Label(shell, SWT.NONE);
        label.setText("New node type");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        tNewNode = new Text(shell, SWT.BORDER);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        tNewNode.setLayoutData(layoutData);

        bOk = new Button(shell, SWT.PUSH);
        bOk.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        bOk.setText("OK");
        bCancel = new Button(shell, SWT.PUSH);
        bCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        bCancel.setText("Cancel");
        addListeners();
        load();
    }

    /**
     *
     */
    private void addListeners() {
        cNetwork.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                changeNetwork();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        bOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.OK;
                perfomSave();
                shell.close();
            }
        });
        bCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.CANCEL;
                shell.close();
            }
        });
        tNewNode.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
                checkTypeName();
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
    }

    /**
     *
     */
    protected void checkTypeName() {
        String newType = tNewNode.getText();
        if (NodeTypes.getEnumById(newType) != null) {
            bOk.setEnabled(false);
        } else {
            bOk.setEnabled(true);
        }
    }

    /**
     *
     */
    protected void perfomSave() {
        Transaction tx = service.beginTx();
        try {
            Node selectedNode = gisNetworkNodes.get(cNetwork.getText());
            String[] structure = (String[])selectedNode.getProperty(org.amanzi.neo.core.INeoConstants.PROPERTY_STRUCTURE_NAME, new String[] {});
            List<String> newStructure = new ArrayList<String>(structure.length + 1);
            String selectedParent = cNodeTypes.getText();
            int i = 0;
            for (; i < structure.length; i++) {
                newStructure.add(structure[i]);
                if (selectedParent.equals(structure[i])) {
                    newStructure.add(tNewNode.getText());
                }
            }
            selectedNode.setProperty(INeoConstants.PROPERTY_STRUCTURE_NAME, newStructure.toArray(new String[] {}));

        } finally {
            tx.finish();
        }
    }

    /**
     *
     */
    protected void changeNetwork() {
        Node selectedNode = gisNetworkNodes.get(cNetwork.getText());
        String[] structure = (String[])selectedNode.getProperty(org.amanzi.neo.core.INeoConstants.PROPERTY_STRUCTURE_NAME, new String[] {});
        cNodeTypes.setItems(structure);
    }

    /**
     *
     */
    private void load() {
        loadNetwork();
    }

    private void loadNetwork() {
        Node refNode = service.getReferenceNode();
        gisNetworkNodes.clear();

        Transaction tx = service.beginTx();
        try {
            for (Relationship relationship : refNode.getRelationships(Direction.OUTGOING)) {
                Node node = relationship.getEndNode();
                Object type = node.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, "").toString();
                if (NeoUtils.isGisNode(node)) {
                    String id = NeoUtils.getSimpleNodeName(node, null);

                    /*
                     * if (type.equals(GisTypes.DRIVE.getHeader())) { gisDriveNodes.put(id, node); }
                     * else
                     */
                    if (type.equals(GisTypes.NETWORK.getHeader())) {
                        for (Relationship rel : node.getRelationships(Direction.OUTGOING)) {
                            node = rel.getEndNode();
                            break;
                        }
                        gisNetworkNodes.put(id, node);
                    }
                }
            }
        } finally {
            tx.finish();
        }
        String[] items = gisNetworkNodes.keySet().toArray(new String[] {});
        Arrays.sort(items);
        cNetwork.setItems(items);

    }

}
