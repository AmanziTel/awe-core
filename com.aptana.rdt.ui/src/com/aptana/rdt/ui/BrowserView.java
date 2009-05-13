package com.aptana.rdt.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

public abstract class BrowserView extends ViewPart {

	private Browser browser;

	public BrowserView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		try {
			 browser = new Browser(parent, SWT.BORDER);
			 browser.setUrl(getURL());
		} catch (Exception e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Unable to create embedded browser", "It appears that you do not have an embeddable browser. Please see http://www.eclipse.org/swt/faq.php#browserlinux for more information if you are on Linux.");
		}
	}

	abstract protected String getURL();

	@Override
	public void setFocus() {
		browser.setFocus();
	}

}