package org.rubypeople.rdt.internal.core.builder;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.core.compiler.BuildContext;

public class CleanRdtCompiler extends AbstractRdtCompiler  {

    private List<BuildContext> contexts;

    public CleanRdtCompiler(IProject project) {
        this(project, new MarkerManager());
    }
    
    public CleanRdtCompiler(IProject project, IMarkerManager markerManager) {
        super(project, markerManager);
    }

    protected void removeMarkers(IMarkerManager markerManager) {
        markerManager.removeProblemsAndTasksFor(project);
    }

    private void analyzeFiles() throws CoreException {
    	BuildContextCollector collector = new BuildContextCollector(project);
    	project.accept(collector, IResource.NONE);
    	contexts = collector.getContexts();
    }

	@Override
	protected BuildContext[] getBuildContexts() throws CoreException {
		if (contexts == null) {
			analyzeFiles();
		}
	    return contexts.toArray(new BuildContext[contexts.size()]);
	}
}
