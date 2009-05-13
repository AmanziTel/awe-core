package com.aptana.rdt.internal.debug.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.contexts.DebugContextEvent;
import org.eclipse.debug.ui.contexts.IDebugContextListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.debug.ui.IEvaluationContextManager;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;

public class EvaluationContextManager implements IEvaluationContextManager, IDebugContextListener, IWindowListener
{

	private static EvaluationContextManager fgManager;

	/**
	 * System property indicating a stack frame is selected in the debug view with an <code>IJavaStackFrame</code>
	 * adapter.
	 */
	private static final String DEBUGGER_ACTIVE = RdtDebugUiPlugin.getUniqueIdentifier() + ".debuggerActive"; //$NON-NLS-1$
	/**
	 * System property indicating an element is selected in the debug view that is an instanceof
	 * <code>RubyStackFrame</code> or <code>RubyThread</code>.
	 */
	private static final String INSTANCE_OF_IRUBY_STACK_FRAME = RdtDebugUiPlugin.getUniqueIdentifier()
			+ ".instanceof.RubyStackFrame"; //$NON-NLS-1$

	private Map<IWorkbenchPage, RubyStackFrame> fContextsByPage = null;
	private IWorkbenchWindow fActiveWindow;

	public EvaluationContextManager()
	{
		DebugUITools.getDebugContextManager().addDebugContextListener(this);
	}

	public RubyStackFrame getEvaluationContext(IWorkbenchPart part)
	{
		IWorkbenchPage page = part.getSite().getPage();
		RubyStackFrame frame = getContext(page);
		if (frame == null)
		{
			return getEvaluationContext(page.getWorkbenchWindow());
		}
		return frame;
	}

	public RubyStackFrame getEvaluationContext(IWorkbenchWindow window)
	{
		List<IWorkbenchWindow> alreadyVisited = new ArrayList<IWorkbenchWindow>();
		if (window == null)
		{
			window = fgManager.fActiveWindow;
		}
		return getEvaluationContext(window, alreadyVisited);
	}

	private static RubyStackFrame getEvaluationContext(IWorkbenchWindow window, List<IWorkbenchWindow> alreadyVisited)
	{
		IWorkbenchPage activePage = window.getActivePage();
		RubyStackFrame frame = null;
		if (activePage != null)
		{
			frame = getContext(activePage);
		}
		if (frame == null)
		{
			IWorkbenchPage[] pages = window.getPages();
			for (int i = 0; i < pages.length; i++)
			{
				if (activePage != pages[i])
				{
					frame = getContext(pages[i]);
					if (frame != null)
					{
						return frame;
					}
				}
			}

			alreadyVisited.add(window);

			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			for (int i = 0; i < windows.length; i++)
			{
				if (!alreadyVisited.contains(windows[i]))
				{
					frame = getEvaluationContext(windows[i], alreadyVisited);
					if (frame != null)
					{
						return frame;
					}
				}
			}
			return null;
		}
		return frame;
	}

	private static RubyStackFrame getContext(IWorkbenchPage page)
	{
		if (fgManager != null)
		{
			if (fgManager.fContextsByPage != null)
			{
				return fgManager.fContextsByPage.get(page);
			}
		}
		return null;
	}

	public void contextActivated(ISelection selection, IWorkbenchPart part)
	{
		if (part != null)
		{
			IWorkbenchPage page = part.getSite().getPage();
			if (selection instanceof IStructuredSelection)
			{
				IStructuredSelection ss = (IStructuredSelection) selection;
				if (ss.size() == 1)
				{
					Object element = ss.getFirstElement();
					if (element instanceof IAdaptable)
					{
						RubyStackFrame frame = (RubyStackFrame) ((IAdaptable) element).getAdapter(RubyStackFrame.class);
						boolean instOf = element instanceof RubyStackFrame || element instanceof RubyThread;
						if (frame != null)
						{
							// do not consider scrapbook frames
							// if
							// (frame.getLaunch().getAttribute(ScrapbookLauncher
							// .SCRAPBOOK_LAUNCH) == null) {
							setContext(page, frame, instOf);
							return;
							// }
						}
					}
				}
			}
			// no context in the given view
			removeContext(page);
		}
	}

	public void contextChanged(ISelection selection, IWorkbenchPart part)
	{
	}

	/**
	 * Sets the evaluation context for the given page, and notes that a valid execution context exists.
	 * 
	 * @param page
	 * @param frame
	 */
	private void setContext(IWorkbenchPage page, RubyStackFrame frame, boolean instOf)
	{
		if (fContextsByPage == null)
		{
			fContextsByPage = new HashMap<IWorkbenchPage, RubyStackFrame>();
		}
		fContextsByPage.put(page, frame);
		System.setProperty(DEBUGGER_ACTIVE, "true"); //$NON-NLS-1$
		if (instOf)
		{
			System.setProperty(INSTANCE_OF_IRUBY_STACK_FRAME, "true"); //$NON-NLS-1$
		}
		else
		{
			System.setProperty(INSTANCE_OF_IRUBY_STACK_FRAME, "false"); //$NON-NLS-1$
		}
	}

	/**
	 * Removes an evaluation context for the given page, and determines if any valid execution context remain.
	 * 
	 * @param page
	 */
	private void removeContext(IWorkbenchPage page)
	{
		if (fContextsByPage != null)
		{
			fContextsByPage.remove(page);
			if (fContextsByPage.isEmpty())
			{
				System.setProperty(DEBUGGER_ACTIVE, "false"); //$NON-NLS-1$
				System.setProperty(INSTANCE_OF_IRUBY_STACK_FRAME, "false"); //$NON-NLS-1$
			}
		}
	}

	public void startup()
	{
		Runnable r = new Runnable()
		{
			public void run()
			{
				if (fgManager == null)
				{
					fgManager = new EvaluationContextManager();
					IWorkbench workbench = PlatformUI.getWorkbench();
					IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
					for (int i = 0; i < windows.length; i++)
					{
						fgManager.windowOpened(windows[i]);
					}
					workbench.addWindowListener(fgManager);
					fgManager.fActiveWindow = workbench.getActiveWorkbenchWindow();
				}
			}
		};
		RdtDebugUiPlugin.getStandardDisplay().asyncExec(r);
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.ui.IWindowListener#windowActivated(org.eclipse.ui. IWorkbenchWindow)
	 */
	public void windowActivated(IWorkbenchWindow window)
	{
		fActiveWindow = window;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWindowListener#windowClosed(org.eclipse.ui.IWorkbenchWindow )
	 */
	public void windowClosed(IWorkbenchWindow window)
	{
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.ui.IWindowListener#windowDeactivated(org.eclipse.ui. IWorkbenchWindow)
	 */
	public void windowDeactivated(IWorkbenchWindow window)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWindowListener#windowOpened(org.eclipse.ui.IWorkbenchWindow )
	 */
	public void windowOpened(IWorkbenchWindow window)
	{
	}

	public void debugContextChanged(DebugContextEvent event)
	{
		if ((event.getFlags() & DebugContextEvent.ACTIVATED) > 0)
		{
			IWorkbenchPart part = event.getDebugContextProvider().getPart();
			if (part != null)
			{
				IWorkbenchPage page = part.getSite().getPage();
				ISelection selection = event.getContext();
				if (selection instanceof IStructuredSelection)
				{
					IStructuredSelection ss = (IStructuredSelection) selection;
					if (ss.size() == 1)
					{
						Object element = ss.getFirstElement();
						if (element instanceof IAdaptable)
						{
							RubyStackFrame frame = (RubyStackFrame) ((IAdaptable) element)
									.getAdapter(RubyStackFrame.class);
							boolean instOf = element instanceof RubyStackFrame || element instanceof RubyThread;
							if (frame != null)
							{
								setContext(page, frame, instOf);
								return;
							}
						}
					}
				}
				// no context in the given view
				removeContext(page);
			}
		}
	}

}
