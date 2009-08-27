package org.amanzi.splash.ui.wizards;

import java.net.URI;

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.nodes.CellNode;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.splash.database.services.SpreadsheetService;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.ui.SplashPlugin;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.ui.wizards.NewFileCreationWizard;

/**
 * Wizard for exporting script that is base on New Ruby Script creation
 * 
 * @author Lagutko_N
 * 
 */

public class ExportScriptWizard extends NewFileCreationWizard {

	// Cell for export
	private final Cell cell;
	private final SpreadsheetNode spreadsheetNode;

	public ExportScriptWizard(SpreadsheetNode spreadsheetNode, Cell cell) {
		super();
		this.spreadsheetNode = spreadsheetNode;
		this.cell = cell;
		init(PlatformUI.getWorkbench(), null);
	}

	public void addPages() {
		if (fPage == null) {
			fPage = new ExportScriptWizardPage(cell);
			fPage.init(getSelection());

		}
		addPage(fPage);
	}

	public boolean performFinish() {
		final IResource modifiedResource = ((ExportScriptWizardPage) super
				.getPages()[0]).getModifiedResource();
		// add ScriptURI to exported script
		URI uri = modifiedResource.getLocationURI();
		ActionUtil.getInstance().runTask(new Runnable() {
			@Override
			public void run() {
				SpreadsheetService spreadsheetService = SplashPlugin
						.getDefault().getSpreadsheetService();
				CellNode cellNode = spreadsheetService.getCellNode(
						spreadsheetNode, cell.getCellID());
				if (cellNode == null) {
					cellNode = spreadsheetService.createCell(spreadsheetNode,
							cell.getCellID());
				}
				NeoCorePlugin.getDefault().getProjectService().createScript(
						cellNode, modifiedResource.getName());
			}
		}, false);
		cell.setScriptURI(uri);
		return super.performFinish();
	}
}
