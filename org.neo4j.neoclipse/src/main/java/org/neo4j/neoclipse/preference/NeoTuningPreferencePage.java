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

package org.neo4j.neoclipse.preference;

import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * <p>
 * The page for neo tuning preferences.
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class NeoTuningPreferencePage extends AbstractPreferencePage {

    private static final String LABEL_NODES = "Nodes";
    private static final String LABEL_RELATIONSHIPS = "Relationships";
    private static final String LABEL_PROPERTIES = "Properties";
    private static final String LABEL_PROPERTIES_INDEX = "Properties Index";
    private static final String LABEL_PROPERTIES_KEYS = "Properties Keys";
    private static final String LABEL_PROPERTIES_STRING = "String Properties";
    private static final String LABEL_PROPERTIES_ARRAY = "Array Properties";
    private static final String PROPTERTY_NOTE = "changes will only come into effect after restarting the application";

    @Override
    protected void createFieldEditors() {

        // Composite attributePanel = getFieldEditorParent();
        // attributePanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
        // attributePanel.setLayout(new GridLayout());

        Group attributeGroup = new Group(getFieldEditorParent(), SWT.NULL);
        // Group attributeGroup = new Group(getFieldEditorParent(), new GridData(SWT.FILL, SWT.TOP,
        // true, true));
        attributeGroup.setText("Memory mapped I/O cache sizes (MB)");
        attributeGroup.setLayout(new GridLayout());
        attributeGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
        Composite marginPanel = new Composite(attributeGroup, attributeGroup.getStyle());
        GridLayout layout = new GridLayout();
        // layout.marginHeight = 15;
        // layout.marginWidth = 15;
        marginPanel.setLayout(layout);
        marginPanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

        IntegerFieldEditor nodes = new IntegerFieldEditor(Preferences.NEOSTORE_NODES, LABEL_NODES, marginPanel);
        nodes.setValidRange(1, Integer.MAX_VALUE);
        // addField(nodes, PROPTERTY_NOTE);
        addField(nodes);
        IntegerFieldEditor relationships = new IntegerFieldEditor(Preferences.NEOSTORE_RELATIONSHIPS, LABEL_RELATIONSHIPS, marginPanel);
        relationships.setValidRange(1, Integer.MAX_VALUE);
        // addField(relationships, PROPTERTY_NOTE);
        addField(relationships);
        IntegerFieldEditor properties = new IntegerFieldEditor(Preferences.NEOSTORE_PROPERTIES, LABEL_PROPERTIES, marginPanel);
        properties.setValidRange(1, Integer.MAX_VALUE);
        // addField(properties, PROPTERTY_NOTE);
        addField(properties);
        IntegerFieldEditor properties_ind = new IntegerFieldEditor(Preferences.NEOSTORE_PROPERTIES_INDEX, LABEL_PROPERTIES_INDEX, marginPanel);
        properties_ind.setValidRange(1, Integer.MAX_VALUE);
        // addField(properties_ind, PROPTERTY_NOTE);
        addField(properties_ind);
        IntegerFieldEditor properties_keys = new IntegerFieldEditor(Preferences.NEOSTORE_PROPERTIES_KEYS, LABEL_PROPERTIES_KEYS, marginPanel);
        properties_keys.setValidRange(1, Integer.MAX_VALUE);
        // addField(properties_keys, PROPTERTY_NOTE);
        addField(properties_keys);
        IntegerFieldEditor properties_string = new IntegerFieldEditor(Preferences.NEOSTORE_PROPERTIES_STRING, LABEL_PROPERTIES_STRING, marginPanel);
        properties_string.setValidRange(1, Integer.MAX_VALUE);
        // addField(properties_string, PROPTERTY_NOTE);
        addField(properties_string);
        IntegerFieldEditor properties_arrays = new IntegerFieldEditor(Preferences.NEOSTORE_PROPERTIES_ARRAYS, LABEL_PROPERTIES_ARRAY, marginPanel);
        properties_arrays.setValidRange(1, Integer.MAX_VALUE);
        addField(properties_arrays);

        Composite note = createNoteComposite(getFieldEditorParent().getFont(), marginPanel, "Note:", PROPTERTY_NOTE);
        GridData spacerData = new GridData(GridData.FILL_HORIZONTAL);
        spacerData.horizontalSpan = 3;
        note.setLayoutData(spacerData);

        // addField(properties_arrays, PROPTERTY_NOTE);
    }

    @Override
    public void addSeparator() {
        // super.addSeparator();
        // nothing to do
    }

    @Override
    public boolean performOk() {
        super.performOk();

        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        Promt pdialog = new Promt(shell, "Restart");;
        if (pdialog.open()) {
            PlatformUI.getWorkbench().restart();
        }

        return true;
    }

    // @Override
    // protected Control createContents(Composite parent) {
    // Composite control = (Composite)super.createContents(parent);
    // Button b = new Button(control, SWT.PUSH);
    // b.setText("Restart");
    // GridData layoutData = new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false);
    // b.setLayoutData(layoutData);
    // return control;
    // }
    private class Promt extends Dialog {

        private final String title;
        protected boolean status;
        private Shell shell;
        private Button bOk;
        private Button bCancel;

        public Promt(Shell parent, String title) {
            this(parent, title, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        }

        public Promt(Shell parent, String title, int style) {
            super(parent, style);
            this.title = title;
        }

        public boolean open() {
            Shell parentShell = getParent();
            Shell shell = new Shell(parentShell, getStyle());
            shell.setText(getTitle());

            createContents(shell);
            shell.pack();

            // calculate location
            Point size = parentShell.getSize();
            int dlgWidth = shell.getSize().x;
            int dlgHeight = shell.getSize().y;
            shell.setLocation((size.x - dlgWidth) / 2, (size.y - dlgHeight) / 2);
            beforeOpen();
            shell.open();
            // wait
            Display display = getParent().getDisplay();
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
            dispose();
            return status;
        }

        protected void dispose() {
        }

        protected void beforeOpen() {
        }

        protected String getTitle() {
            return title;
        }

        protected void createContents(final Shell shell) {
            this.shell = shell;
            // shell.setImage(NodeTypes.DATASET.getImage());
            shell.setLayout(new GridLayout(2, false));

            Label label = new Label(shell, SWT.LEFT);
            label.setText("To apply the changes need to restart the application.");
            label.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));

            bOk = createButton(shell, "Restart now");
            bOk.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    status = true;
                    shell.close();
                }
            });

            bCancel = createButton(shell, "Restart later");
            bCancel.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    status = false;
                    shell.close();
                }
            });

        };

        /**
         * Create button
         * 
         * @param parent parent composite
         * @param name visible name
         * @return Button
         */
        private Button createButton(Composite parent, String name) {
            Button button = new Button(parent, SWT.PUSH);
            button.setText(name);
            button.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
            return button;
        }

    }
}
