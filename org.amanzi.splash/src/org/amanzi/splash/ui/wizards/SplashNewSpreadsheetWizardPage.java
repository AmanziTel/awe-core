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

import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.nodes.RubyProjectNode;
import org.amanzi.neo.services.ui.utils.ActionUtil;
import org.amanzi.neo.services.utils.RunnableWithResult;
import org.amanzi.splash.utilities.Messages;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
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
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (splash).
 */

public class SplashNewSpreadsheetWizardPage extends WizardPage {
	private Text containerText;
	private Text fileText;

	private ISelection selection;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public SplashNewSpreadsheetWizardPage(ISelection selection) {
		super(Messages.SpreadsheetNew_wizardPage_name);
		setTitle(Messages.SpreadsheetNew_wizardPage_title);
		setDescription(Messages.SpreadsheetNew_wizardPage_descr);
		this.selection = selection;
	}
	private int rowCount = 30, columnCount = 30;

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText(Messages.SpreadsheetNew_wizardPage_container);

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		Button button = new Button(container, SWT.PUSH);
		button.setText(Messages.SpreadsheetNew_wizardPage_browse);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText(Messages.SpreadsheetNew_wizardPage_sheet_name);

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		Button button1 = new Button(container, SWT.PUSH);
		button1.setText(Messages.SpreadsheetNew_wizardPage_new_project);
		button1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				OpenNewRubyProjectWizardAction rubyAction = new OpenNewRubyProjectWizardAction();
                rubyAction.run();
                if (rubyAction.getCreatedElement()!=null){
                    containerText.setText(rubyAction.getCreatedElement().getElementName());
                }
			}
		});
		
		initialize();
		dialogChanged();
		setControl(container);
	}
	
	

	public int getRowCount() {
		return rowCount;
	}



	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}



	public int getColumnCount() {
		return columnCount;
	}



	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}



	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			
			NeoSplashUtil.logn("ssel: "+ ssel.toString());
			
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			NeoSplashUtil.logn("obj: "+ obj.toString());
			
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
				
				NeoSplashUtil.logn("container: "+ container.getName());
				
				
				containerText.setText(container.getFullPath().toString());
			}else if (obj instanceof RubyProjectImpl){
				RubyProjectImpl rpi = (RubyProjectImpl) obj;
				if (!"".equals( rpi.getName()))
                    containerText.setText(rpi.getName());
			}
			//Lagutko, 23.06.2009, selection also can contains object of RubyProject type
            else if (obj instanceof RubyProject) {
                RubyProject rdtProject = (RubyProject)obj;
                if (!"".equals(rdtProject.getElementName())) {
                    containerText.setText(rdtProject.getElementName());
                }
            }
		}
		
		String sheetName = NeoSplashUtil.getFreeSpreadsheetName(Messages.Default_SpreadsheetName, getContainerName());
		fileText.setText(sheetName);
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowse() {
		NeoSplashUtil.logn("ResourcesPlugin.getWorkspace().getRoot(): " + ResourcesPlugin.getWorkspace().getRoot().toString());
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), true,
				Messages.SpreadsheetNew_wizardPage_new_container);
		
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
                containerText.setText((((Path)result[0]).toString()).substring(1));
			}
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName()));
		
		final String fileName = getFileName();
		
		NeoSplashUtil.logn(Platform.getLocation() + "/" + getContainerName() + "/" + fileName);

		if (getContainerName().length() == 0) {
			updateStatus(Messages.SpreadsheetNew_wizardPage_error_container_spec);
			return;
		}
				
		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus(Messages.SpreadsheetNew_wizardPage_error_container_exist);
			return;
		}
		
		if (!container.isAccessible()) {
			updateStatus(Messages.SpreadsheetNew_wizardPage_error_project);
			return;
		}
        if (!container.getName().equals(getContainerName())) {
            containerText.setText(container.getName());
        }
		//TODO: Lagutko: must be added computing for Root Node of Spreadsheet
        //Lagutko: it's a fake because for now Root Node is a Reference Node
        final RubyProjectNode root = NeoServiceFactory.getInstance().getProjectService().findRubyProject(getContainerName());// SplashPlugin.getDefault().getSpreadsheetService().getRootNode();
        boolean isExist = ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult<Boolean>() {

            private boolean result;
            
            public Boolean getValue() {
                return result;
            }

            public void run() {
                result = root!=null&&NeoServiceFactory.getInstance().getProjectService().findSpreadsheet(root, fileName) != null;
            }
            
        });
        if (isExist) {
            updateStatus(Messages.SpreadsheetNew_wizardPage_error_sheet);
            return;
        }
		
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getContainerName() {
		return containerText.getText();
	}

	public String getFileName() {
		return fileText.getText();
	}
}
