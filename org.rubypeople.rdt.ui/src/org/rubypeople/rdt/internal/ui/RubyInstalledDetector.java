package org.rubypeople.rdt.internal.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.progress.UIJob;
import org.rubypeople.rdt.internal.launching.LaunchingPlugin;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.RubyRuntime;

public class RubyInstalledDetector extends UIJob
{

	private static final String WINDOWS_INSTALL_URL = "http://www.aptana.com/ruby/install/windows";
	private static final String LINUX_INSTALL_URL = "http://www.aptana.com/ruby/install/linux";
	private static final String MACOSX_INSTALL_URL = "http://www.aptana.com/ruby/install/macosx";

	private static final String STANDARD_VMTYPE = "org.rubypeople.rdt.launching.StandardVMType";
	private static final String RUBY_BROWSER_ID = RubyPlugin.getPluginId() + ".ruby.download.browser";
	private static final String INTERPRETER_PREF_PAGE_ID = "org.rubypeople.rdt.debug.ui.preferences.PreferencePageRubyInterpreter";

	public RubyInstalledDetector()
	{
		super("Detecting Ruby installation");
	}

	private boolean usingIncludedJRuby()
	{
		org.eclipse.core.runtime.Preferences store = LaunchingPlugin.getDefault().getPluginPreferences();
		if (store == null)
			return false;
		return store.getBoolean(LaunchingPlugin.USING_INCLUDED_JRUBY);
	}

	private boolean rubyInstalled()
	{
		return !rubyNotInstalled();
	}

	private boolean rubyNotInstalled()
	{
		IVMInstall[] cRubyInstalls = RubyRuntime.getVMInstallType(STANDARD_VMTYPE).getVMInstalls();
		return cRubyInstalls == null || cRubyInstalls.length == 0;
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor)
	{
		if (rubyInstalled() || usingIncludedJRuby())
			return Status.CANCEL_STATUS;
		popToolTip();
		monitor.done();
		return Status.OK_STATUS;
	}

	private void popToolTip()
	{
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		Display display = shell.getDisplay();
		Image image = null;
		final ToolTip tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);
		tip.setMessage(RubyUIMessages.RubyInstalledDetector_msg);
		tip.setText(RubyUIMessages.RubyInstalledDetector_title);
		final Tray tray = display.getSystemTray();
		if (tray != null)
		{
			final TrayItem item = new TrayItem(tray, SWT.NONE);
			image = RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBY);
			item.setImage(image);
			item.setToolTip(tip);
			item.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					super.widgetSelected(e);
					tip.setVisible(false);	
					item.setVisible(false);	
					popDialog();
					tip.dispose();
					item.dispose();
				}
			});
			// Close the system tray icon after some reasonable amount of time
			Job closeTrayItem = new UIJob("Close Ruby Detection tray item")
			{

				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					item.setVisible(false);
					item.dispose();
					return Status.OK_STATUS;
				}
			};
			closeTrayItem.setSystem(true);
			closeTrayItem.schedule(30000); // auto-close it in 30 seconds
		}
		else
		{
			// Where should I put the tool tip? Right now, just stick it where cursor is
			tip.setLocation(display.getCursorLocation());
		}
		tip.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				super.widgetSelected(e);
				tip.setVisible(false);
				popDialog();
				tip.dispose();
			}
		});
		tip.setVisible(true);
	}

	private void popDialog()
	{
		MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(),
				RubyUIMessages.RubyInstalledDetector_title, null, RubyUIMessages.bind(
						RubyUIMessages.RubyInstalledDetector_message, new Object[] {
								RubyUIMessages.RubyInstalledDetector_download_button,
								RubyUIMessages.RubyInstalledDetector_preferences_button }), MessageDialog.WARNING,
				new String[] { RubyUIMessages.RubyInstalledDetector_download_button,
						RubyUIMessages.RubyInstalledDetector_preferences_button,
						RubyUIMessages.RubyInstalledDetector_cancel_button }, 0);
		int result = dialog.open();
		if (result == 0)
		{ // download ruby
			openBrowser(getURL());
		}
		else if (result == 1)
		{ // send to interpreter pref page
			openPreferencePage(INTERPRETER_PREF_PAGE_ID);
		}
		else
		{ // use JRuby
			// Set a pref flag to avoid popping this prompt in the future!
			org.eclipse.core.runtime.Preferences store = LaunchingPlugin.getDefault().getPluginPreferences();
			if (store != null)
				store.setValue(LaunchingPlugin.USING_INCLUDED_JRUBY, true);
		}
	}

	private String getURL()
	{
		if (Platform.getOS().equals(Platform.OS_WIN32))
			return WINDOWS_INSTALL_URL;

		if (Platform.getOS().equals(Platform.OS_MACOSX))
			return MACOSX_INSTALL_URL;

		return LINUX_INSTALL_URL;
	}

	private void openPreferencePage(String pageId)
	{
		PreferenceDialog prefDialog = PreferencesUtil.createPreferenceDialogOn(Display.getDefault().getActiveShell(),
				pageId, null, null);
		prefDialog.setBlockOnOpen(false);
		prefDialog.open();
	}

	private void openBrowser(String url)
	{
		try
		{
			IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
			IWebBrowser browser = support.createBrowser(RUBY_BROWSER_ID);
			browser.openURL(new URL(url));
		}
		catch (PartInitException e)
		{
			RubyPlugin.log(e);
		}
		catch (MalformedURLException e)
		{
			RubyPlugin.log(e);
		}
	}
}
