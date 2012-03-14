package org.amanzi.awe.views.network.view;

import org.amanzi.neo.services.model.INetworkModel;
import org.eclipse.jface.wizard.Wizard;

public class ExportToFileSettingsWizard extends Wizard {

    private INetworkModel network;

    protected ExportToFileSettingsPage page;

    public ExportToFileSettingsWizard(INetworkModel network) {
        super();
        setNeedsProgressMonitor(true);
        setWindowTitle("Export to file");
        this.network = network;
    }

    @Override
    public void addPages() {
        page = new ExportToFileSettingsPage(network);
        addPage(page);
    }

    @Override
    public boolean performFinish() {

        return true;
    }

}
