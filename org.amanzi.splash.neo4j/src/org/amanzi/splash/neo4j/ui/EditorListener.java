/**
 * 
 */
package org.amanzi.splash.neo4j.ui;

import java.util.Arrays;
import java.util.LinkedList;

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.nodes.CellNode;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.nodes.RubyScriptNode;
import org.amanzi.neo.core.database.services.AweProjectService;
import org.amanzi.splash.neo4j.utilities.ActionUtil;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;

/**
 * @author Cinkel_A Listener for change ruby script
 */
public class EditorListener implements IResourceChangeListener {

	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		LinkedList<IResourceDelta> listResourceDelta = new LinkedList<IResourceDelta>();
		listResourceDelta.add(delta);
		String projectName = null;
		for (int i = 0; i < listResourceDelta.size(); i++) {
			final IResourceDelta resourceDelta = listResourceDelta.get(i);
			IResource resource = resourceDelta.getResource();
			if (resource instanceof Project) {
				projectName = resource.getName();
			}
			if ("rb".equals(resourceDelta.getFullPath().getFileExtension())
					&& ((resourceDelta.getFlags() & IResourceDelta.CONTENT) != 0)) {
				ActionUtil.getInstance().runTask(
						new UpdateScript(projectName, resourceDelta), false);
			}
			listResourceDelta.addAll(Arrays.asList(resourceDelta
					.getAffectedChildren(IResourceDelta.CHANGED)));
		}

	}

	/**
	 * Runnable class for update script in bd
	 * 
	 * @author Cinkel_A
	 * 
	 */
	private static class UpdateScript implements Runnable {

		private final String projectName;
		private final String scriptName;

		public UpdateScript(String projectName, IResourceDelta resourceDelta) {
			this.projectName = projectName;
			// TODO Auto-generated constructor stub
			scriptName = resourceDelta.getResource().getName();
		}

		@Override
		public void run() {
			AweProjectService projectService = NeoCorePlugin.getDefault()
					.getProjectService();
			RubyProjectNode rubyProject = projectService
					.findRubyProject(projectName);
			if (rubyProject != null) {
				RubyScriptNode script = projectService.findScript(rubyProject,
						scriptName);
				if (script != null) {
					System.out.println();
					CellNode cell = projectService
							.findCellByScriptReference(script);
					if (cell != null) {
						NeoCorePlugin.getDefault().getUpdateBDManager()
								.updateCell(
										projectName,
										projectService.getSpreadsheetByCell(
												cell).getName(),
										SplashPlugin.getDefault()
												.getSpreadsheetService()
												.getFullId(cell));
					}
				}
			}

		}
	}
}
