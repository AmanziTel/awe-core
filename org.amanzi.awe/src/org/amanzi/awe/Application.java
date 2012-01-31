/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.amanzi.awe;

import net.refractions.udig.internal.ui.UDIGApplication;
import net.refractions.udig.internal.ui.UDIGWorkbenchAdvisor;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.db.manager.impl.Neo4jDatabaseManager;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * This is the default application for the Amanzi Wireless Explorer. It is based directly on uDIG,
 * and uses its advisor.
 * 
 * @since AWE 1.0.0
 * @author craig
 */
public class Application extends UDIGApplication implements IApplication {

    /**
     * Create the AWE workbench advisor by using the UDIGWorkbenchAdvisor with only the perspective
     * changed to match the AWE requirements.
     * 
     * @see net.refractions.udig.internal.ui.UDIGApplication#createWorkbenchAdvisor()
     */
    @Override
    protected WorkbenchAdvisor createWorkbenchAdvisor() {
        AWEWorkbenchAdivsor aweWorkbenchAdivsor = new AWEWorkbenchAdivsor() {
            /**
             * @see org.eclipse.ui.application.WorkbenchAdvisor#initialize(org.eclipse.ui.application.IWorkbenchConfigurer)
             */
            @Override
            public void initialize(IWorkbenchConfigurer configurer) {
                super.initialize(configurer);
                configurer.setSaveAndRestore(true);
            }
        };

        return aweWorkbenchAdivsor;
    }

    private class AWEWorkbenchAdivsor extends UDIGWorkbenchAdvisor {
        @Override
        public void preStartup() {
            GraphDatabaseService databaseService = DatabaseManagerFactory.getDatabaseManager().getDatabaseService();
            if (databaseService == null) {
                String dbPath = new DBLocatinInputDialog(PlatformUI.getWorkbench().getDisplay().getShells()[0]).open();
                if (dbPath == null) {
                    // Cancel click
                    System.exit(0);
                }

                setDBLocation(dbPath);
            }
            // PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(new Shell(),
            // "org.neo4j.neoclipse.preference.PreferencePage", null, null);
            // dialog.open();
            //
        }

        @Override
        public String getInitialWindowPerspectiveId() {

            return PerspectiveFactory.AWE_PERSPECTIVE;
        }

        @Override
        public void postStartup() {
            super.postStartup();
        }

        @Override
        public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
            configurer.setShowPerspectiveBar(true);
            return super.createWorkbenchWindowAdvisor(configurer);
        }
    }

    public static boolean setDBLocation(String dbPath) {

        try {
            Neo4jDatabaseManager databaseManager = new Neo4jDatabaseManager(dbPath);
            GraphDatabaseService databaseService = databaseManager.getDatabaseService();
            if (databaseService == null) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        IPreferenceStore preferenceStore = org.neo4j.neoclipse.Activator.getDefault().getPreferenceStore();
        preferenceStore.setValue(org.neo4j.neoclipse.preference.Preferences.DATABASE_LOCATION, dbPath);

        return true;
    }
}

class DBLocatinInputDialog extends Dialog {
    String path;

    /**
     * @param parent
     */
    public DBLocatinInputDialog(Shell parent) {
        super(parent);
    }

    /**
     * @param parent
     * @param style
     */
    public DBLocatinInputDialog(Shell parent, int style) {
        super(parent, style);
    }

    /**
     * Makes the dialog visible.
     * 
     * @return
     */
    public String open() {
        Shell parent = getParent();
        final Shell shell = new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
        shell.setText("Neo4j Database Location");

        shell.setLayout(new GridLayout(1, false));

        new Label(shell, SWT.NONE).setText("There is running AWE instance. Please, specify another Neo4j Database Location:");

        final Composite inputGroup = new Composite(shell, SWT.NONE);
        inputGroup.setLayout(new GridLayout(2, false));
        inputGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));        

        // Create the text box extra wide to show long paths
        final Text text = new Text(inputGroup, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(data);

        IPreferenceStore preferenceStore = org.neo4j.neoclipse.Activator.getDefault().getPreferenceStore();
        text.setText(preferenceStore.getString(org.neo4j.neoclipse.preference.Preferences.DATABASE_LOCATION));
        

        final Composite errorGroup = new Composite(shell, SWT.NONE);
        errorGroup.setLayout(new GridLayout(2, false));
        errorGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        Image image = JFaceResources.getImage(TitleAreaDialog.DLG_IMG_MESSAGE_ERROR);
        Label imageLabel = new Label(errorGroup, SWT.NONE);
        imageLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        imageLabel.setImage(image);

        Label errorLabel = new Label(errorGroup, SWT.WRAP);
        errorLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
        errorLabel.setText("Invalid location");
        
        // Clicking the button will allow the user
        // to select a directory
        Button button = new Button(inputGroup, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                DirectoryDialog dlg = new DirectoryDialog(shell);

                // Set the initial filter path according
                // to anything they've selected or typed in
                dlg.setFilterPath(text.getText());

                // Change the title bar text
                dlg.setText("SWT's DirectoryDialog");

                // Customizable message displayed in the dialog
                dlg.setMessage("Select a directory");

                String dir = dlg.open();
                if (dir != null) {
                    // Set the text box to the new selection
                    text.setText(dir);
                    boolean isValidPath = Application.setDBLocation(dir);
                    errorGroup.setVisible(!isValidPath);
                    
                    shell.pack();
                }
            }
        });

        final Composite buttonGroup = new Composite(shell, SWT.NONE);
        buttonGroup.setLayout(new GridLayout(2, false));
        buttonGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));        

        
        final Button buttonOK = new Button(buttonGroup, SWT.NONE);
        buttonOK.setText("Ok");
        buttonOK.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        Button buttonCancel = new Button(buttonGroup, SWT.NONE);
        buttonCancel.setText("Cancel");

        buttonOK.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                path = text.getText();
                shell.dispose();
            }
        });

        buttonCancel.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                path = null;
                shell.dispose();
            }
        });

        shell.addListener(SWT.Traverse, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == SWT.TRAVERSE_ESCAPE)
                    event.doit = false;
            }
        });

        shell.pack();
        shell.open();

        Display display = parent.getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

        return path;
    }
}