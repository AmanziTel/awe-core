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

package org.amanzi.awe.reports.geoptima.wizard;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.reports.geoptima.wizard.GeoptimaReportWizard;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.Traverser;
/**
 * Page for GeOptima Report Wizard
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class SelectDataPage extends WizardPage {

    private static final String SELECT_DATASET = "Select dataset:";
    private Combo cmbDatasets;
    private Map<String,Node> datasets = new HashMap<String,Node>();
    public SelectDataPage(String pageName) {
        super(pageName);
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent,SWT.NONE);
        container.setLayout(new GridLayout(2, false));
        Label label = new Label(container,SWT.NONE);
        label.setText(SELECT_DATASET);
        label.setLayoutData(new GridData());
        
        cmbDatasets = new Combo(container,SWT.NONE);
        cmbDatasets.setLayoutData(new GridData());
        cmbDatasets.addSelectionListener(new SelectionAdapter(){

            @Override
            public void widgetSelected(SelectionEvent e) {
                ((GeoptimaReportWizard)getWizard()).setDataset(datasets.get(cmbDatasets.getText()));
                setPageComplete(true);
            }});
        
        setPageComplete(false);
        setControl(parent);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible){
            DatasetService datasetService = NeoServiceFactory.getInstance().getDatasetService();
            
            cmbDatasets.removeAll();
            for (Node node:datasetService.getAllDatasetNodes().nodes()){
                String datasetName = (String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME);
                cmbDatasets.add(datasetName);
                datasets.put(datasetName,node);
            }
        }
        super.setVisible(visible);
    }
    
}
