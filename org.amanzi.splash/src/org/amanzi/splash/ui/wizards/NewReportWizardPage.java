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

import net.refractions.udig.project.internal.impl.RubyProjectImpl;

import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.ui.wizards.OpenNewRubyProjectWizardAction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class NewReportWizardPage extends WizardPage {

    private Text containerText;
    private Text reportText;
    private IStructuredSelection selection;
    public static final String REPORT_FILE_EXTENSION="r";

    protected NewReportWizardPage(IStructuredSelection selection) {
        super("New report");
        setTitle("Amanzi Report Wizard");
        setDescription("This wizard creates Amanzi Report file");
        this.selection = selection;
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        // layout.verticalSpacing = 9;
        Label label = new Label(container, SWT.NONE);
        label.setText("&Container:");

        containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        containerText.setLayoutData(gd);
        containerText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateInput();
            }
        });

        Button button = new Button(container, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                handleBrowse();
            }
        });

        label = new Label(container, SWT.NONE);
        label.setText("&Report name:");

        reportText = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        reportText.setLayoutData(gd);
        reportText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateInput();
            }
        });

        Button button1 = new Button(container, SWT.PUSH);
        button1.setText("New Ruby Project...");
        button1.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                new OpenNewRubyProjectWizardAction().run();
            }
        });

        initialize();
        // dialogChanged();
        setControl(container);
    }

    protected void validateInput() {
        final String containerName = containerText.getText();
        if (containerName.length() == 0) {
            updateStatus("File container must be specified");
            return;
        }
        IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(containerName));

        if (container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
            updateStatus("File container must exist");
            return;
        }

        if (!container.isAccessible()) {
            updateStatus("Project must be writable");
            return;
        }
        if (!container.getName().equals(containerName)) {
            containerText.setText(container.getName());
        }
        String fileName = getReportText().getText();
        if (!fileName.matches(".*\\."+REPORT_FILE_EXTENSION)) {
            fileName = new StringBuffer(fileName).append(".").append(REPORT_FILE_EXTENSION).toString();
        }
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        final IProject resource = root.getProject(containerName);
        if (resource.getFile(new Path(fileName)).exists()) {
            updateStatus("Report already exists");
            return;
        }

        updateStatus(null);
    }

    /**
     * Uses the standard container selection dialog to choose the new value for the container field.
     */

    private void handleBrowse() {
        ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), true,
                "Select new file container");

        if (dialog.open() == ContainerSelectionDialog.OK) {
            Object[] result = dialog.getResult();
            if (result.length == 1) {
                containerText.setText((((Path)result[0]).toString()).substring(1));
            }
        }
    }

    private void initialize() {
        if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection)selection;

            NeoSplashUtil.logn("ssel: " + ssel.toString());

            if (ssel.size() > 1)
                return;
            Object obj = ssel.getFirstElement();
            NeoSplashUtil.logn("obj: " + obj.toString());
            String containerName = "";
            if (obj instanceof IResource) {
                IContainer container;
                if (obj instanceof IContainer)
                    container = (IContainer)obj;
                else
                    container = ((IResource)obj).getParent();

                NeoSplashUtil.logn("container: " + container.getName());
                containerName = container.getFullPath().toString();

            } else if (obj instanceof RubyProjectImpl) {
                RubyProjectImpl rpi = (RubyProjectImpl)obj;
                if (!"".equals(rpi.getName()))
                    containerName = rpi.getName();
            } else if (obj instanceof RubyProject) {
                RubyProject rdtProject = (RubyProject)obj;
                if (!"".equals(rdtProject.getElementName())) {
                    containerName = rdtProject.getElementName();
                }
                if (containerName != null && containerName != "") {
                    containerText.setText(containerName);
                    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                    final IProject resource = root.getProject(containerName);
                    IFile file;
                    int i = 0;
                    while ((file = resource.getFile(new Path(("report" + i) + "."+REPORT_FILE_EXTENSION))).exists()) {
                        i++;
                    }
                    reportText.setText(file.getName().replaceFirst("\\."+REPORT_FILE_EXTENSION, ""));
                }
            }
        }

        validateInput();
    }

    /**
     * @return Returns the containerText.
     */
    public Text getContainerText() {
        return containerText;
    }

    /**
     * @param containerText The containerText to set.
     */
    public void setContainerText(Text containerText) {
        this.containerText = containerText;
    }

    /**
     * @return Returns the reportText.
     */
    public Text getReportText() {
        return reportText;
    }

    /**
     * @param reportText The reportText to set.
     */
    public void setReportText(Text reportText) {
        this.reportText = reportText;
    }

    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }
}
