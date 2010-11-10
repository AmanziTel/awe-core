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

import java.text.DecimalFormat;
import java.util.Collection;

import org.amanzi.awe.report.grid.wizards.GridReportWizard.OutputType;
import org.amanzi.awe.statistics.database.entity.Statistics;
import org.amanzi.awe.statistics.database.entity.StatisticsCell;
import org.amanzi.awe.statistics.database.entity.StatisticsGroup;
import org.amanzi.awe.statistics.database.entity.StatisticsRow;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Page for Grid Report Wizard
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class ResultingPage extends WizardPage {

    private Text results;
    private Label lblResult;

    public ResultingPage() {
        super(ResultingPage.class.getName());
        setTitle("Results");
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        lblResult = new Label(container, SWT.NONE);
        lblResult.setLayoutData(new GridData());

        results = new Text(container, SWT.MULTI | SWT.V_SCROLL);
        results.setLayoutData(new GridData(350,350));

        setPageComplete(true);
        setControl(container);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
            OutputType outputType = gridReportWizard.getOutputType();
            switch (outputType) {
            case PDF:
                gridReportWizard.generatePdfReports();
                break;
            case XLS:
                gridReportWizard.exportToXls();
                break;
            case PNG:
                gridReportWizard.exportChartsToJpeg();
                break;

            default:
                break;
            }
            
            setTitle("Alerts for "+gridReportWizard.getCategoryType().getNetworkType()+"s (period: "+gridReportWizard.getAggregation().getId()+")");
            
            
            StringBuilder sb = new StringBuilder();
            Statistics statistics = gridReportWizard.getStatistics();
            Collection<StatisticsGroup> groups = statistics.getGroups().values();
            for (StatisticsGroup group : groups) {
                if (group.isFlagged()) {
                    sb.append(group.getGroupName()).append("\n");
                    Collection<StatisticsRow> rows = group.getRows().values();
                    for (StatisticsRow row : rows) {
                        if (row.isFlagged()) {
                            sb.append(" - ").append(row.getName()).append("\n");
                            Collection<StatisticsCell> cells = row.getCells().values();
                            for (StatisticsCell cell : cells) {
                                if (cell.isFlagged()) {
                                    sb.append("   ").append(" - ").append(cell.getName()).append(": ").append(new DecimalFormat("#0.0").format(cell.getValue()))
                                            .append("\n");
                                }
                            }
                        }
                    }
                }
            }
            String result = sb.toString();
            if (!result.isEmpty()){
                setMessage("Alerts were sent out for the following KPI Deviations:");
                results.setText(result);
            }else{
                setMessage("No problems");
            }
        }
        super.setVisible(visible);
    }
}
