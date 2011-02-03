package org.amanzi.awe.afp.ericsson.ui;

import java.io.File;
import java.util.Collection;

import org.amanzi.awe.afp.ericsson.parser.RirValidator;
import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.amanzi.neo.loader.ui.utils.FileSelection;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.amanzi.neo.loader.ui.wizards.LoaderPage;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
/**
 * 
 * <p>
 *Rir selection gui
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class RirSelectionGui extends LoaderPage<CommonConfigData> {

    private Group main;
    private FileSelection viewer;

    public RirSelectionGui() {
        super("rirSelectionGuiPage");
    }

    @Override
    public void createControl(Composite parent) {
        main = new Group(parent, SWT.NULL);
        main.setLayout(new GridLayout(1, false));
        viewer = new FileSelection();
        viewer.createPartControl(main);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        viewer.getTreeViewer().getTree().setLayoutData(gridData);
        viewer.getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                fileSelectionChanged(event);
            }
        });
        setControl(main);
        update();

    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            if (TreeSelection.EMPTY.equals(viewer.getTreeViewer().getSelection())) {
                String defDir = LoaderUiUtils.getDefaultDirectory();
                if (StringUtils.isNotEmpty(defDir)) {
                    viewer.getTreeViewer().reveal(new File(defDir));
                }
            }
        }
        super.setVisible(visible);
    }

    /**
     * File selection changed.
     * 
     * @param event the event
     */
    protected void fileSelectionChanged(SelectionChangedEvent event) {
        update();
    }

    @Override
    protected boolean validateConfigData(CommonConfigData configurationData) {
        Collection<File> files = viewer.getSelectedFiles(null);

        if (files.isEmpty()) {
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_FILE, DialogPage.INFORMATION);
            return true;
        }
        viewer.storeDefSelection(null);
        configurationData.getAdditionalProperties().put("RIR_FILES", files);
        new RirValidator().filterRir(configurationData);
        Collection<File> allLoadedFiles = (Collection<File>)configurationData.getAdditionalProperties().get("RIR_FILES");
        if (files.size() != allLoadedFiles.size()) {
            viewer.getTreeViewer().setSelection(new StructuredSelection(allLoadedFiles.toArray()), false);
            return validateConfigData(configurationData);
        }
        if (allLoadedFiles.isEmpty()) {
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_FILE, DialogPage.INFORMATION);
            return true;
        }
        setMessage("");
        return true;
    }

}
