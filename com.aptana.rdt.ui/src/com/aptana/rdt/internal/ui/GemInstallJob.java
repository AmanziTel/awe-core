package com.aptana.rdt.internal.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.rubypeople.rdt.debug.ui.InstallDeveloperToolsDialog;
import org.rubypeople.rdt.internal.debug.ui.launcher.InstallGemsJob;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

import com.aptana.rdt.AptanaRDTPlugin;
import com.aptana.rdt.core.gems.ContributedGemRegistry;
import com.aptana.rdt.core.gems.Gem;
import com.aptana.rdt.core.gems.IGemManager;
import com.aptana.rdt.core.gems.LocalFileGem;
import com.aptana.rdt.ui.AptanaRDTUIPlugin;

/**
 * Automatically installs any relevant (by platform and VM type) contributed gems from com.aptana.rdt.gems extension
 * point with auto-install flag set to true.
 * 
 * @author Chris Williams
 */
public class GemInstallJob extends UIJob
{

	private static class SerialRule implements ISchedulingRule
	{

		public SerialRule()
		{
		}

		public boolean contains(ISchedulingRule rule)
		{
			return rule == this;
		}

		public boolean isConflicting(ISchedulingRule rule)
		{
			return rule instanceof SerialRule;
		}
	}

	public GemInstallJob()
	{
		super("Installing gems");
		setRule(new SerialRule());
	}

	public boolean shouldRun()
	{
		return !PlatformUI.getWorkbench().isClosing();
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor)
	{
		if (!getGemManager().isRubyGemsInstalled())
		{
			String key = "dont_bug_about_gems_not_installed";
			String dontBug = AptanaRDTUIPlugin.getDefault().getPreferenceStore().getString(key);
			if (dontBug != null && dontBug.equals(MessageDialogWithToggle.ALWAYS))
			{
				return Status.OK_STATUS;
			}
			MessageDialogWithToggle
					.openWarning(
							RubyPlugin.getActiveWorkbenchShell(),
							"RubyGems Not Installed",
							"You do not appear to have RubyGems installed. It is highly recommended that you install this, as it is the standard way of installing and managing ruby libraries. Please see http://rubygems.org/read/chapter/3.",
							"Don't bug me anymore.", false, AptanaRDTUIPlugin.getDefault().getPreferenceStore(), key);
			return Status.OK_STATUS;
		}
		monitor.beginTask("Getting auto-install gems...", 35);
		monitor.subTask("Getting contributed gems");
		Collection<Gem> gems = getContributedGems();
		monitor.worked(10);

		if (monitor.isCanceled())
			return Status.CANCEL_STATUS;
		monitor.subTask("Filtering by platform");
		gems = filterByPlatform(gems);
		monitor.worked(5);

		if (monitor.isCanceled())
			return Status.CANCEL_STATUS;
		monitor.subTask("Filtering out already installed gems");
		gems = filterOutInstalled(gems);
		monitor.worked(10);

		if (monitor.isCanceled())
			return Status.CANCEL_STATUS;
		monitor.subTask("Filtering out user ignored gems");
		gems = filterOutIgnored(gems);
		monitor.worked(10);

		// TODO This is a rails specific hack. We should be checking dependencies for gems we're asking as well as
		// what's installed here. If we have a gem like activeresource 2.0.2 alone and rails 1.2.6 installed, we
		// shouldn't even ask the user to install activeresource.
		gems = filterActiveResource(gems);

		if (gems.isEmpty())
		{
			monitor.done();
			return Status.OK_STATUS;
		}

		if (hasGemsWhichCompile(gems) && InstallDeveloperToolsDialog.shouldShow())
		{
			InstallDeveloperToolsDialog installToolsDialog = new InstallDeveloperToolsDialog(RubyPlugin
					.getActiveWorkbenchShell());
			installToolsDialog.open();
			// TODO Remove the compiling gems and any that depend on them
			return Status.CANCEL_STATUS;
		}

		GemAutoInstallDialog dialog = new GemAutoInstallDialog(RubyPlugin.getActiveWorkbenchShell(), gems);
		if (PlatformUI.getWorkbench().isClosing())
		{
			return Status.CANCEL_STATUS;
		}
		int code = dialog.open();
		if (code == Dialog.CANCEL)
		{
			monitor.setCanceled(true);
			return Status.CANCEL_STATUS;
		}

		Collection<Gem> finalGems = dialog.getSelectedGems();
		if (finalGems.isEmpty())
		{
			monitor.done();
			return Status.OK_STATUS;
		}

		Job job = new InstallGemsJob(finalGems);
		job.setSystem(true);
		job.schedule();
		monitor.done();
		return Status.OK_STATUS;
	}

	private Collection<Gem> filterByPlatform(Collection<Gem> gems)
	{
		return ContributedGemRegistry.filterByPlatform(gems);
	}

	private Collection<Gem> filterActiveResource(Collection<Gem> gems)
	{
		boolean containsActiveResource = contains(gems, "activeresource", "2.0.2");
		boolean containsRails2 = contains(gems, "rails", "2.0.2");
		if (containsActiveResource && !containsRails2)
		{
			return remove(gems, "activeresource", "2.0.2");
		}
		return gems;
	}

	private Collection<Gem> remove(Collection<Gem> gems, String name, String version)
	{
		Gem toRemove = get(gems, name, version);
		if (toRemove == null)
			return gems;
		Collection<Gem> copy = new ArrayList<Gem>(gems);
		copy.remove(toRemove);
		return copy;
	}

	private boolean contains(Collection<Gem> gems, String name, String version)
	{
		return get(gems, name, version) != null;
	}

	private Gem get(Collection<Gem> gems, String name, String version)
	{
		for (Gem gem : gems)
		{
			if (gem.getName().equals(name))
			{
				if (gem.getVersion().equals("2.0.2"))
					return gem;
			}
		}
		return null;
	}

	private boolean hasGemsWhichCompile(Collection<Gem> finalGems)
	{
		for (Gem gem : finalGems)
		{
			if (gem instanceof LocalFileGem)
			{
				LocalFileGem local = (LocalFileGem) gem;
				if (local.compiles())
					return true;
			}
		}
		return false;
	}

	private static Collection<Gem> filterOutIgnored(Collection<Gem> gems)
	{
		Collection<Gem> filtered = new ArrayList<Gem>();
		IPreferenceStore prefs = AptanaRDTUIPlugin.getDefault().getPreferenceStore();
		for (Gem gem : gems)
		{
			if (prefs.getBoolean(GemAutoInstallDialog.getIgnorePrefKey(gem)))
				continue; // user asked us to ignore this gem and version
			filtered.add(gem);
		}
		return filtered;
	}

	private Collection<Gem> filterOutInstalled(Collection<Gem> gems)
	{
		Collection<Gem> filtered = new ArrayList<Gem>();
		for (Gem gem : gems)
		{
			if (getGemManager().gemInstalled(gem.getName()))
				continue;
			filtered.add(gem);
		}
		return filtered;
	}

	private IGemManager getGemManager()
	{
		return AptanaRDTPlugin.getDefault().getGemManager();
	}

	private Collection<Gem> getContributedGems()
	{
		return ContributedGemRegistry.getContributedGems();
	}
}
