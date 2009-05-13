package com.aptana.rdt.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallChangedListener;
import org.rubypeople.rdt.launching.PropertyChangeEvent;
import org.rubypeople.rdt.launching.RubyRuntime;

import com.aptana.rdt.AptanaRDTPlugin;
import com.aptana.rdt.core.gems.Gem;
import com.aptana.rdt.core.gems.GemListener;
import com.aptana.rdt.internal.ui.GemInstallJob;

/**
 * The activator class controls the plug-in life cycle
 */
public class AptanaRDTUIPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.rdt.ui";

	// The shared instance
	private static AptanaRDTUIPlugin plugin;

	/**
	 * The constructor
	 */
	public AptanaRDTUIPlugin()
	{
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);

		if (AptanaRDTPlugin.getDefault().getGemManager().isInitialized())
		{
			new GemInstallJob().schedule();
		}
		else
		{
			AptanaRDTPlugin.getDefault().getGemManager().addGemListener(new GemListener()
			{

				public void managerInitialized()
				{
					new GemInstallJob().schedule();
				}

				public void gemsRefreshed()
				{
					// ignore
				}

				public void gemRemoved(Gem gem)
				{
					// ignore
				}

				public void gemAdded(Gem gem)
				{
					// ignore
				}

			});
		}
		// FIX ROR-524 Auto install gem job doesn't run when user changes VMs
		RubyRuntime.addVMInstallChangedListener(new IVMInstallChangedListener()
		{

			public void vmRemoved(IVMInstall removedVm)
			{
			}

			public void vmChanged(PropertyChangeEvent event)
			{
			}

			public void vmAdded(IVMInstall newVm)
			{
			}

			public void defaultVMInstallChanged(IVMInstall previous, IVMInstall current)
			{
				if (current == null)
					return;
				// Wait until gem manager is refreshed again
				AptanaRDTPlugin.getDefault().getGemManager().addGemListener(new GemListener()
				{

					public void managerInitialized()
					{
					}

					public void gemsRefreshed()
					{
						new GemInstallJob().schedule();
						final GemListener self = this;
						Job removeListener = new Job("Remove gem listener")
						{

							@Override
							protected IStatus run(IProgressMonitor monitor)
							{
								AptanaRDTPlugin.getDefault().getGemManager().removeGemListener(self);
								return Status.OK_STATUS;
							}

						};
						removeListener.setSystem(true);
						removeListener.schedule();
					}

					public void gemRemoved(Gem gem)
					{
					}

					public void gemAdded(Gem gem)
					{
					}

				});

			}

		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static AptanaRDTUIPlugin getDefault()
	{
		return plugin;
	}

	public static void log(RubyModelException e)
	{
		getDefault().getLog().log(e.getStatus());
	}
}
