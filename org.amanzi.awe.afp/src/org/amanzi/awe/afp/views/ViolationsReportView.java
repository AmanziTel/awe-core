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

package org.amanzi.awe.afp.views;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.amanzi.awe.ui.AweUiPlugin;
import org.amanzi.awe.ui.custom_table.CustomTable;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.network.NetworkModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * <p>
 * Violations Report View
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class ViolationsReportView extends ViewPart {
    private NetworkService ns = NeoServiceFactory.getInstance().getNetworkService();
    private CustomTable<ViolationReportModel> table;
    private Map<String, FrequencyPlanModel> plans = new HashMap<String, FrequencyPlanModel>();
    private Combo fplan;
    private ViolationReportModel reportModel;
    @Override
    public void createPartControl(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout(3, false));
        Label label=new Label(main, SWT.LEFT);
        label.setText("Frequncy plan");
        fplan = new Combo(main, SWT.READ_ONLY | SWT.BORDER);
        GridData data = new GridData();
        data.horizontalSpan = 2;
        fplan.setLayoutData(data);
        table = new CustomTable<ViolationReportModel>(reportModel, SWT.FILL);
        table.createPartControl(main);
        data = new GridData();
        data.horizontalSpan = 3;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        table.getViewer().getTable().setLayoutData(data);
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        reportModel = new ViolationReportModel();
        super.init(site);
    }
    /**
     *
     */
    private void setPlans() {
        fplan.setItems(formPlans());
    }
    private String[] formPlans() {
        Map<NetworkModel, Set<FrequencyPlanModel>> models = ns.findAllFrequencyPlanWithSource(AweUiPlugin.getDefault()
                .getUiService().getActiveProjectNode());
        plans.clear();
        for (Entry<NetworkModel, Set<FrequencyPlanModel>> entry : models.entrySet()) {
            String name = entry.getKey().getName();
            for (FrequencyPlanModel model : entry.getValue()) {
                plans.put(new StringBuilder(name).append(':').append(model.getName()).toString(), model);
            }
        }
        return plans.values().toArray(new String[0]);
    }
    @Override
    public void setFocus() {
    }

}
