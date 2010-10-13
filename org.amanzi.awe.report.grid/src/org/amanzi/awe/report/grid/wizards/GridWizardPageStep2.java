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

package org.amanzi.awe.report.grid.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.amanzi.awe.report.charts.ChartType;
import org.amanzi.neo.loader.grid.IDENLoader;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class GridWizardPageStep2 extends WizardPage {
    private static final String DAILY = "daily";
    private static final String HOURLY = "hourly";
    private static final String SELECT_AGGREGATION = "Select aggregation:";
    private static final String SELECT_CHART_TYPE = "Select chart type:";
    private static final String BAR = "bar";
    private static final String LINE = "line";
    private static final String DIAL = "dial";
    private Button btnHourly;
    private Button btnDaily;
    private Button btnBar;
    private Button btnLine;
    private Button btnDial;
    protected Node datasetNode;

    protected GridWizardPageStep2() {
        super("GridWizardPageStep2");
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(4, false));

        

        setPageComplete(true);
        setControl(container);
    }

   

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            ((GridReportWizard)getWizard()).loadData();
        }
        super.setVisible(visible);
    }

    public Node getDatasetNode() {
        return datasetNode;
    }

}
