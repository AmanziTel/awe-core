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

package org.amanzi.awe.wizards.gpehreport;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class GPEHReportWizardPage extends WizardPage {

    private Combo cNetwork;
    private Combo cGpeh;

    /** The gpeh. */
    private LinkedHashMap<String, Node> gpeh;
    /** The network. */
    private LinkedHashMap<String, Node> network;
    /** The neo. */
    private final GraphDatabaseService neo = NeoServiceProvider.getProvider().getService();
    
    /**
     * @param pageName
     */
    protected GPEHReportWizardPage(String pageName) {
        super(pageName);
    }

    @Override
    public void createControl(Composite parent) {
        final Composite main = new Composite(parent, SWT.FILL);
        main.setLayout(new GridLayout(3, false));

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

    private void validateFinish() {
        setPageComplete(isValidPage());
    }

    protected boolean isValidPage() {
        return gpeh.get(cGpeh.getText()) != null && network.get(cNetwork.getText()) != null;
    }

    /**
     * Form gpeh.
     */
    private void formGPEH() {
        gpeh = new LinkedHashMap<String, Node>();
        for (Node node : NeoUtils.getAllGpeh(neo)) {
            gpeh.put(NeoUtils.getSimpleNodeName(node, ""), node);
        }
        String[] result = gpeh.keySet().toArray(new String[0]);
        Arrays.sort(result);
        cGpeh.setItems(result);
    }

    /**
     * forms Networks list.
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
     * initialize.
     */
    private void init() {
        formGPEH();
        formNetwork();
        
        validateFinish();
    }

}
