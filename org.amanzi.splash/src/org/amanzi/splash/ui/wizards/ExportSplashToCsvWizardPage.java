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

package org.amanzi.splash.ui.wizards;

import java.io.File;
import java.util.HashMap;

import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.viewers.TreeViewer;
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
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author NiCK
 * @since 1.0.0
 */
public class ExportSplashToCsvWizardPage extends WizardPage {

    private FileFieldEditor editor;
    private Group main;
    private Combo network;
    private String fileName;
    private HashMap<String, Node> members;
    private TreeViewer viewer;
    protected Node selectedNode;

    /**
     * @param pageName
     */
    protected ExportSplashToCsvWizardPage(String pageName) {
        // super(pageName);
        super(pageName, "Title", null);
        setDescription("Description");
        setPageComplete(false);
    }

    @Override
    public void createControl(Composite parent) {
        main = new Group(parent, SWT.NULL);
        main.setLayout(new GridLayout(3, false));
        // viewer = new TreeViewer(main);
        // GridData layoutData = new GridData(GridData.FILL_HORIZONTAL, GridData.FILL_VERTICAL,
        // false, false, 3, 10);
        // layoutData.minimumHeight = 200;
        // viewer.getControl().setLayoutData(layoutData);
        // ITreeContentProvider provider;
        // viewer.setContentProvider(new );
        // viewer.setLabelProvider(labelProvider);
        Label label = new Label(main, SWT.LEFT);
        label.setText("Splash:");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        network = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        network.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        network.setItems(getSplashItems());
        network.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedNode = members.get(network.getText());
                setPageComplete(isValidPage());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        editor = new FileFieldEditor("fileSelectNeighb", "File: ", main); // NON-NLS-1

        editor.getTextControl(main).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setFileName(editor.getStringValue());
            }
        });
        setControl(main);
    }

    private String[] getSplashItems() {
        Transaction tx = NeoUtils.beginTransaction();
        try {
            NeoService service = NeoServiceProvider.getProvider().getService();
            Node refNode = service.getReferenceNode();
            Traverser traverse = refNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    return NeoUtils.getNodeType(currentPos.currentNode(), "").equals(NodeTypes.SPREADSHEET.getId());
                }
            }, SplashRelationshipTypes.AWE_PROJECT, Direction.OUTGOING, SplashRelationshipTypes.RUBY_PROJECT, Direction.OUTGOING, SplashRelationshipTypes.SPREADSHEET,
                    Direction.OUTGOING);

            members = new HashMap<String, Node>();
            for (Node node : traverse) {
                members.put(NeoUtils.getSimpleNodeName(node, "", null), node);
            }
            return members.keySet().toArray(new String[] {});
        } finally {
            tx.finish();
        }
    }

    /**
     * @param fileName
     */
    protected void setFileName(String fileName) {
        try {
            new File(fileName);
            this.fileName = fileName;
        } catch (Exception e) {
            this.fileName = null;
        }
        setPageComplete(isValidPage());

    }

    /**
     * @return
     */
    protected boolean isValidPage() {
        return selectedNode != null && StringUtils.isNotEmpty(fileName);
    }

}
