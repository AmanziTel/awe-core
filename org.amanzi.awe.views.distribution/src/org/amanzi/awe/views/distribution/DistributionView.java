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

package org.amanzi.awe.views.distribution;

import org.amanzi.awe.distribution.engine.internal.DistributionEnginePlugin;
import org.amanzi.awe.views.distribution.widgets.DistributionDatasetWidget;
import org.amanzi.awe.views.distribution.widgets.DistributionDatasetWidget.DistributionDataset;
import org.amanzi.awe.views.distribution.widgets.DistributionDatasetWidget.IDistributionDatasetSelectionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionView extends ViewPart implements IDistributionDatasetSelectionListener {

    private static final int FIRST_ROW_LABEL_WIDTH = 65;

    private static final int SECOND_ROW_LABEL_WIDTH = 75;

    private static final int THIRD_ROW_LABEL_WIDTH = 85;

    private DistributionDatasetWidget distributionDataset;

    /**
     * 
     */
    public DistributionView() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createPartControl(final Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout(3, false));
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        addDistributionTypeComposite(mainComposite);
    }

    private void addDistributionTypeComposite(final Composite parent) {
        addDistributionDatasetWidget(parent, this, FIRST_ROW_LABEL_WIDTH);
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    private DistributionDatasetWidget addDistributionDatasetWidget(final Composite parent, final IDistributionDatasetSelectionListener listener, final int minWidth) {
        DistributionDatasetWidget result = new DistributionDatasetWidget(parent, listener, "Dataset:", minWidth,
                DistributionEnginePlugin.getDefault().getProjectModelProvider(),
                DistributionEnginePlugin.getDefault().getNetworkModelProvider(),
                DistributionEnginePlugin.getDefault().getDriveModelProvider());
        result.initializeWidget();

        return result;
    }

    @Override
    public void onDistributionDatasetSelected(final DistributionDataset distributionDataset) {
        // TODO Auto-generated method stub

    }

}
