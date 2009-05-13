package org.rubypeople.rdt.internal.launching;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;

abstract class Sudo {

	private static final int FIVE_MINUTES = 5 * 60 * 1000;
	
	private static String password;
	private static long lastTimestamp = -1;
	
	synchronized static final String getPassword(final String msg) {
		if (password == null || looksLikeBadPasswordWasEntered() || !isWithinFiveMinutes()) { // if no stored password, or it's been over 5 minutes since it was stored, prompt user
			Display.getDefault().syncExec(new Runnable() {
			
				public void run() {
					PasswordDialog dialog = new PasswordDialog(null, "Enter sudo password", msg, null, null);
					if (dialog.open() == Dialog.OK) {
						password = dialog.getValue();
						lastTimestamp = System.currentTimeMillis();
					}			
				}
			
			});
		}
		return password;
	}

	/**
	 * If we get prompted within 1 second of last time, assume user entered a bad password, so don't use cached one
	 * @return
	 */
	private static boolean looksLikeBadPasswordWasEntered()
	{
		if (lastTimestamp == -1) return false; // we haven't been prompted yet, so it can't be bad
		long now = System.currentTimeMillis();
		return  (now - lastTimestamp) <= 5000; // been less than a second, probably was bad
	}

	private static boolean isWithinFiveMinutes() {
		if (password == null || password.trim().length() == 0) return false;
		if (lastTimestamp == -1) return false;
		long now = System.currentTimeMillis();
		return now < (lastTimestamp + FIVE_MINUTES);
	}

	public static void flushPassword()
	{
		password = null;
		lastTimestamp = -1;
	}
	
}
