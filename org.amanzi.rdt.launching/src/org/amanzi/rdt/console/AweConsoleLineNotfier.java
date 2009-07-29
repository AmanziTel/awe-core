package org.amanzi.rdt.console;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.rdt.internal.launching.AweLaunchingPlugin;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.debug.ui.console.IConsoleLineTrackerExtension;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

/**
 * Class for ConsoleLineNotifier that will work with RubyConsole
 * 
 * @author Lagutko_N
 *
 */

public class AweConsoleLineNotfier implements IPatternMatchListener,
		IPropertyChangeListener {
	
	/**
	 * Listeners for notifier
	 */
	private List<IConsoleLineTracker> fListeners = new ArrayList<IConsoleLineTracker>(2);
	
	/**
	 * Console of this notifier
	 */
	private RubyConsole fConsole = null;

	public int getCompilerFlags() {
		return 0;
	}

	public String getLineQualifier() {
		return "\\n|\\r"; //$NON-NLS-1$
	}

	public String getPattern() {
		return ".*\\r(\\n?)|.*\\n"; //$NON-NLS-1$
	}
	
	/**
	 * Connect to console if it's RubyConsole
	 * 
	 * @param console console for connecting
	 */

	public void connect(TextConsole console) {
		if (console instanceof RubyConsole) {
			fConsole = (RubyConsole)console;
		
			IConsoleLineTracker[] lineTrackers = ConsoleRuntime.getConsoleLineTrackers(fConsole.getType());
		
			for (int i = 0; i < lineTrackers.length; i++) {
				lineTrackers[i].init(fConsole);
				addConsoleListener(lineTrackers[i]);
			}
			
			fConsole.addPropertyChangeListener(this);
		}
	}
	
	/**
	 * Adds the given listener to the list of listeners notified when a line of
	 * text is appended to the console.
	 * 
	 * @param listener
	 */
	public void addConsoleListener(IConsoleLineTracker listener) {
		if (!fListeners.contains(listener))
				fListeners.add(listener);
	}


	public synchronized void disconnect() {
		try {
			IDocument document = fConsole.getDocument();
			if (document != null) {
				int lastLine = document.getNumberOfLines() - 1;
				if (document.getLineDelimiter(lastLine) == null) {
					IRegion lineInformation = document.getLineInformation(lastLine);
					lineAppended(lineInformation);
				}
			}
		} catch (BadLocationException e) {
		    AweLaunchingPlugin.log(null, e);
		}		
	}
	
	/**
	 * Notify listeners that line was appended
	 * 
	 * @param region appended line
	 */
	
	public void lineAppended(IRegion region) {
		int size = fListeners.size();
		for (int i=0; i<size; i++) {
			IConsoleLineTracker tracker = (IConsoleLineTracker) fListeners.get(i);
			tracker.lineAppended(region);
		}
	}

	public void matchFound(PatternMatchEvent event) {
		try {
			IDocument document = fConsole.getDocument();
			int lineOfOffset = document.getLineOfOffset(event.getOffset());
			String  delimiter = document.getLineDelimiter(lineOfOffset);
			int strip = delimiter==null ? 0 : delimiter.length();
			Region region = new Region(event.getOffset(), event.getLength()-strip); 
			lineAppended(region);
		} 
		catch (BadLocationException e) {
		    AweLaunchingPlugin.log(null, e);
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		if(event.getProperty().equals(IConsoleConstants.P_CONSOLE_OUTPUT_COMPLETE)) {
			fConsole.removePropertyChangeListener(this);
			consoleClosed();
		}
	}
	
	 
	/**
	 * Notification the console's streams have been closed
	 */
	public synchronized void consoleClosed() {
		int size = fListeners.size();
		for (int i = 0; i < size; i++) {
			IConsoleLineTracker tracker = (IConsoleLineTracker) fListeners.get(i);
			if (tracker instanceof IConsoleLineTrackerExtension) {
				((IConsoleLineTrackerExtension) tracker).consoleClosed();
			}
			tracker.dispose();
		}
	
		fConsole = null;
		fListeners = null;
	}

}
