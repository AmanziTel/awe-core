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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.INodeType;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.AbstractDialog;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.statistic.IPropertyHeader;
import org.amanzi.neo.services.statistic.PropertyHeader;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class NewNodeAction extends Action {
    private final INodeType iNodeType;
    private final Node sourceNode;
    private final GraphDatabaseService service;
    private Node targetNode;
    protected HashMap<String, Object> defaultProperties = new HashMap<String, Object>();

    public NewNodeAction(INodeType iNodeType, Node sourcedNode) {
        this.iNodeType = iNodeType;
        this.sourceNode = sourcedNode;
        service = NeoServiceProvider.getProvider().getService();
        setText(iNodeType.getId());
    }

    @Override
    public void run() {
        System.out.println("Action " + iNodeType.getId() + " runned.");
        InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), getText(), "Enter name of new element", "New " + iNodeType.getId(), null);
        int result = dialog.open();
        if (result != Dialog.CANCEL) {
            defaultProperties.put(INeoConstants.PROPERTY_NAME_NAME, dialog.getValue());
            createNewElement();
            postCreating();
            NeoServiceProvider.getProvider().commit();
        }

    }
    
    private class NewNodeDialog extends AbstractDialog<Integer> {

        /** The Constant MIN_FIELD_WIDTH. */
        private static final int MIN_FIELD_WIDTH = 50;

        /** The t new node. */
        private Text tNodeName;

        /** The b ok. */
        private Button bOk;

        /** The b cancel. */
        private Button bCancel;

        /** The shell. */
        private Shell shell;

        /** The ds. */
        private final DatasetService ds;

        /**
         * Instantiates a new new type dialog.
         * 
         * @param parent the parent
         * @param title the title
         * @param style the style
         */
        public NewNodeDialog(Shell parent, String title, int style) {
            super(parent, title, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
            status = SWT.CANCEL;
            ds = NeoServiceFactory.getInstance().getDatasetService();
        }

        /**
         * Creates the contents.
         * 
         * @param shell the shell
         */
        @Override
        protected void createContents(Shell shell) {
            this.shell = shell;

            shell.setImage(NodeTypes.DATASET.getImage());
            shell.setLayout(new GridLayout(2, true));

            Label label = new Label(shell, SWT.NONE);
            GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
            layoutData.minimumWidth = MIN_FIELD_WIDTH;

            label.setText("New node name");
            label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            tNodeName = new Text(shell, SWT.BORDER);
            layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
            layoutData.minimumWidth = MIN_FIELD_WIDTH;
            tNodeName.setLayoutData(layoutData);

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
         * Adds the listeners.
         */
        private void addListeners() {
            bOk.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    status = SWT.OK;
                    perfomrSave();
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
            tNodeName.addKeyListener(new KeyListener() {

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
         * Check type name.
         */
        protected void checkTypeName() {
            String newname = tNodeName.getText().toLowerCase().trim();
            if (ds.getNodeType(newname) != null) {
                bOk.setEnabled(false);
            } else {
                bOk.setEnabled(true);
            }
        }

        /**
         * Perform save.
         */
        protected void perfomrSave() {
            ds.saveDynamicNodeType(tNodeName.getText());
            NeoServiceProvider.getProvider().commit();
        }

        /**
         * Load.
         */
        private void load() {
            // nothing to load
        }

    }

    private void postCreating() {
        DatasetService ds = NeoServiceFactory.getInstance().getDatasetService();
        INodeType sourceType = ds.getNodeType(sourceNode);

        List<INodeType> structureTypes = ds.getSructureTypes(sourceNode);

        List<INodeType> userDefTypes = ds.getUserDefinedNodeTypes();
        userDefTypes.removeAll(structureTypes);

        if (userDefTypes.contains(iNodeType)) {
            String[] newStructureTypes = new String[structureTypes.size() + 1];
            int i = 0;
            for (INodeType type : structureTypes) {
                newStructureTypes[i++] = type.getId();
                if (type.equals(sourceType)) {
                    newStructureTypes[i++] = iNodeType.getId();
                }
            }
            ds.setStructure(NeoUtils.getParentNode(sourceNode, NodeTypes.NETWORK.getId()), newStructureTypes);
        }
    }

    private void createNewElement() {
        Transaction tx = service.beginTx();
        try {
            targetNode = service.createNode();
            targetNode.setProperty("type", iNodeType.getId());
            sourceNode.createRelationshipTo(targetNode, NetworkRelationshipTypes.CHILD);
            NodeTypes type = NodeTypes.getEnumById(iNodeType.getId());
            if (type != null) {
                IPropertyHeader ph = PropertyHeader.getPropertyStatistic(NeoUtils.getParentNode(sourceNode, NodeTypes.NETWORK.getId()));
                Map<String, Object> statisticProperties = ph.getStatisticParams(type);
                for (String key : statisticProperties.keySet()) {
                    targetNode.setProperty(key, statisticProperties.get(key));
                }
            }
            for (String key : defaultProperties.keySet()) {
                if (!targetNode.hasProperty(key))
                    targetNode.setProperty(key, defaultProperties.get(key));
            }

            tx.success();
        } catch (Exception e) {
            e.printStackTrace();
            tx.failure();
        } finally {
            tx.finish();
        }
    }
}
