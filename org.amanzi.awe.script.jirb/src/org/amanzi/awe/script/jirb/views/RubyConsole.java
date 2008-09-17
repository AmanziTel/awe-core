package org.amanzi.awe.script.jirb.views;


import org.amanzi.awe.script.jirb.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.*;
import org.eclipse.jface.action.*;
import org.eclipse.ui.*;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class RubyConsole extends ViewPart {
	private SWTIRBConsole ex;
	private Action action0;
	private Action action1;
	private Action action2;

	/**
	 * The constructor.
	 */
	public RubyConsole() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		ex = new SWTIRBConsole(parent);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(ex, "org.amanzi.awe.script.jirb.viewer");
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				RubyConsole.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(ex);
		ex.setMenu(menu);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action0);
		manager.add(new Separator());
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action0);
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action0);
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action0 = new Action() {
			public void run() {
				try {
					ex.restart();
				} catch(Throwable t){
					System.err.println("Failed to re-start IRBConsole: "+t.getMessage());
					t.printStackTrace(System.err);
				}
			}
		};
		action0.setText("Restart IRB Session");
		action0.setToolTipText("Restart IRB Session");
		action0.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
		
		final Display display = this.getSite().getShell().getDisplay();
		action1 = new Action() {
			public void run() {
				try {
					SwingIRBConsole.start(null);
				} catch(Throwable t){
					System.err.println("Failed to start swing-based IRBConsole: "+t.getMessage());
					t.printStackTrace(System.err);
				}
			}
		};
		action1.setText("Swing-based IRBConsole");
		action1.setToolTipText("Start a Swing-based IRBConsole");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
		
		action2 = new Action() {
			public void run() {
				try {
					SWTIRBConsole.start(display);
				} catch(Throwable t){
					System.err.println("Failed to start swing-based IRBConsole: "+t.getMessage());
					t.printStackTrace(System.err);
				}
			}
		};
		action2.setText("SWT/AWT based IRBConsole");
		action2.setToolTipText("Start an SWT/AWT based IRBConsole");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		//viewer.getControl().setFocus();
		ex.setFocus();
	}
}