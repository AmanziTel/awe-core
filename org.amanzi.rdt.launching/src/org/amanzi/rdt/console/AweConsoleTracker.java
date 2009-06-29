package org.amanzi.rdt.console;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.console.IHyperlink;
import org.rubypeople.rdt.internal.debug.ui.console.RubyConsoleTracker;
import org.rubypeople.rdt.internal.debug.ui.console.RubyStackTraceHyperlink;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.util.StackTraceLine;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;

public class AweConsoleTracker extends RubyConsoleTracker {
	
	protected IProject getProject()
	{
		RubyConsole console = (RubyConsole)fConsole;
		
		if (fLastLaunch == null
				|| (console.getProject() != null && !fLastLaunch.equals(console.getLaunch())))
		{
			fLastLaunch = console.getLaunch();
			String projectName = null;
			try
			{
				if (console.getLaunch() != null
						&& console.getLaunch().getLaunchConfiguration() != null) 
					projectName = console.getLaunch().getLaunchConfiguration().getAttribute(
							IRubyLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
			}
			catch (Exception e)
			{
				RubyPlugin.log(e);
			}
			if (projectName == null)
				return null;
			fProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			console.setProject(fProject);
		}
		return fProject;
	}
	
	/**
	 * @see org.eclipse.debug.ui.console.IConsoleLineTracker#lineAppended(org.eclipse.jface.text.IRegion)
	 */
	public void lineAppended(IRegion line)
	{
		try
		{
			int prefix = 0;

			String text = getText(line);
			while (StackTraceLine.isTraceLine(text))
			{
				StackTraceLine stackTraceLine = new StackTraceLine(text, getProject());
				if (!existanceChecker.fileExists(stackTraceLine.getFilename()))
					return;
				IHyperlink link = new AweStackTraceHyperlink(fConsole, stackTraceLine);
				fConsole.addLink(link, line.getOffset() + prefix + stackTraceLine.offset(), stackTraceLine.length());

				prefix = stackTraceLine.offset() + stackTraceLine.length();
				int substring = stackTraceLine.offset() + stackTraceLine.length();
				if (text.length() < substring - 1)
				{
					text = "";
				}
				else
				{
					text = text.substring(substring);
					if (text.startsWith(":in `require':"))
					{
						text = text.substring(14);
						prefix += 14;
					}
				}
			}
		}
		catch (BadLocationException e)
		{
		}
	}
}
