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

package org.amanzi.awe.neighbours.dialog;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.GisTypes;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;

/**
 * <p>
 * GPEHReportWizardPage
 * </p>
 * .
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class GPEHReportWizardPage extends WizardPage {

    /** The c network. */
    private Combo cNetwork;

    /** The c gpeh. */
    private Combo cGpeh;

    /** The gpeh. */
    private LinkedHashMap<String, Node> gpeh;
    /** The network. */
    private LinkedHashMap<String, Node> network;
    /** The neo. */
    private final GraphDatabaseService neo = NeoServiceProviderUi.getProvider().getService();

    /**
     * Instantiates a new gPEH report wizard page.
     * 
     * @param pageName the page name
     * @param pageDescription the page description
     */
    protected GPEHReportWizardPage(String pageName, String pageDescription) {
        super(pageName);
        setTitle(pageName);
        setDescription(pageDescription);
    }

    @Override
    public void createControl(Composite parent) {
        final Group main = new Group(parent, SWT.FILL);
        main.setLayout(new GridLayout(3, false));
        main.setText("Select data sources");

        Label label = new Label(main, SWT.NONE);
        label.setText("GPEH data");
        cGpeh = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.minimumWidth = 200;
        cGpeh.setLayoutData(layoutData);

        label = new Label(main, SWT.NONE);
        label.setText("Network");
        cNetwork = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.minimumWidth = 200;
        cNetwork.setLayoutData(layoutData);


        SelectionListener listener = new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                validateFinish();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        };
        cNetwork.addSelectionListener(listener);
        cGpeh.addSelectionListener(listener);

        setControl(main);

        init();
    }

    /**
     * Validate finish.
     */
    private void validateFinish() {
        setPageComplete(isValidPage());
    }

    /**
     * Checks if is valid page.
     * 
     * @return true, if is valid page
     */
    protected boolean isValidPage() {
        return gpeh.get(cGpeh.getText()) != null && network.get(cNetwork.getText()) != null;
    }

    /**
     * Form gpeh.
     */
    private void formGPEH() {
        gpeh = new LinkedHashMap<String, Node>();
        for (Node node : NeoUtils.getAllGpeh(neo)) {
            gpeh.put(NeoUtils.getNodeName(node), node);
        }
        String[] result = gpeh.keySet().toArray(new String[0]);
        Arrays.sort(result);
        cGpeh.setItems(result);
    }

    /**
     * Form network.
     */
    private void formNetwork() {
        network = new LinkedHashMap<String, Node>();
        Transaction tx = neo.beginTx();
        try {
            Traverser gisWithNeighbour = NeoUtils.getAllReferenceChild(neo, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Node node = currentPos.currentNode();
                    return NeoUtils.isGisNode(node) && GisTypes.NETWORK == NeoUtils.getGisType(node, null);
                }
            });
            for (Node node : gisWithNeighbour) {
                network.put((String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME),
                        node.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).getOtherNode(node));
            }
        } finally {
            tx.finish();
        }
        String[] result = network.keySet().toArray(new String[0]);
        Arrays.sort(result);
        cNetwork.setItems(result);
    }

    /**
     * Inits the.
     */
    private void init() {
        formGPEH();
        formNetwork();
        
        validateFinish();
    }

    /**
     * Gets the gpeh node.
     * 
     * @return the gpeh node
     */
    public Node getGpehNode() {
        return gpeh.get(cGpeh.getText());
    }

    /**
     * Gets the network node.
     * 
     * @return the network node
     */
    public Node getNetworkNode() {
        return network.get(cNetwork.getText());
    }

}
