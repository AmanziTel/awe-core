package org.amanzi.splash.neo4j.handlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.List;

import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.splash.neo4j.swing.Cell;
import org.amanzi.splash.neo4j.swing.SplashTable;
import org.amanzi.splash.neo4j.swing.SplashTableModel;
import org.amanzi.splash.neo4j.ui.AbstractSplashEditor;
import org.amanzi.splash.neo4j.ui.SplashPlugin;
import org.amanzi.splash.neo4j.utilities.CSVParser;
import org.amanzi.splash.neo4j.utilities.NeoSplashUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;

import com.eteks.openjeks.format.CellFormat;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SplashToolbarHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SplashToolbarHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
				AbstractSplashEditor editor = (AbstractSplashEditor) window.getActivePage().getActiveEditor();
				editor.plotCellsBarChart();
		
		
		
		return null;
	}
	 
	

}
