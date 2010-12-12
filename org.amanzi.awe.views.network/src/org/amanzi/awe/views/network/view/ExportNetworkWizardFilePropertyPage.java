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

package org.amanzi.awe.views.network.view;

import java.nio.charset.Charset;
import java.util.Set;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author NiCK
 * @since 1.0.0
 */
public class ExportNetworkWizardFilePropertyPage extends WizardPage {
    public static final String SPACE = "SPACE";
    public static final String TAB = "TAB";
    private Group main;
    private Combo fieldDel;
    private Combo textDel;
    private Combo charset;
    private String charsetValue;
    private String fieldDelValue;
    private String textDelValue;

    /**
     * Instantiates a new cSV property wizard page.
     * 
     * @param pageName the page name
     * @param charset the charset
     * @param fieldDel the field del
     * @param textDel the text del
     */
    public ExportNetworkWizardFilePropertyPage(String pageName, String charset, String fieldDel, String textDel) {
        super(pageName);
        setTitle("CSV properties");
        setDescription("Select CSV properties");
        charsetValue = charset;
        fieldDelValue = fieldDel;

        textDelValue = textDel;
    }

    /**
     * Creates the control.
     * 
     * @param parent the parent
     */
    @Override
    public void createControl(Composite parent) {
        main = new Group(parent, SWT.FILL);
        main.setLayout(new GridLayout(3, false));

        Label label = new Label(main, SWT.LEFT);
        label.setText("Character set");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        charset = new Combo(main, SWT.BORDER | SWT.READ_ONLY);
        charset.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        label = new Label(main, SWT.LEFT);
        label.setText("Field delimeter");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        fieldDel = new Combo(main, SWT.BORDER);
        fieldDel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        label = new Label(main, SWT.LEFT);
        label.setText("Text delimeter");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        textDel = new Combo(main, SWT.BORDER);
        textDel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        addListeners();
        init();
        setControl(main);

    }

    /**
     * Adds the listeners.
     */
    private void addListeners() {
        charset.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                charsetValue = charset.getText();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        fieldDel.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                fieldDelValue = fieldDel.getText();
                if (TAB.equals(fieldDelValue)) {
                    fieldDelValue = "\t"; //$NON-NLS-1$
                } else if (SPACE.equals(fieldDelValue)) {
                    fieldDelValue = " "; //$NON-NLS-1$
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        textDel.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                textDelValue = textDel.getText();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }

    /**
     * Inits
     */
    private void init() {
        Set<String> set = Charset.availableCharsets().keySet();
        charset.setItems(set.toArray(new String[0]));
        charset.setText(charsetValue);
        fieldDel.setItems(new String[] {TAB, SPACE, ";", "."}); //$NON-NLS-1$ //$NON-NLS-2$
        if (fieldDelValue.equals("\t")) { //$NON-NLS-1$
            fieldDel.setText(TAB);
        } else if (fieldDelValue.equals(" ")) { //$NON-NLS-1$
            fieldDel.setText(SPACE);
        } else {
            fieldDel.setText(fieldDelValue);
        }
        textDel.setItems(new String[] {"\"", "'"}); //$NON-NLS-1$ //$NON-NLS-2$
        textDel.setText(textDelValue);
    }

    /**
     * Gets the charset value.
     * 
     * @return the charset value
     */
    public String getCharsetValue() {
        return charsetValue;
    }

    /**
     * Gets the field del value.
     * 
     * @return the field del value
     */
    public String getFieldDelValue() {
        return fieldDelValue;
    }

    /**
     * Gets the text del value.
     * 
     * @return the text del value
     */
    public String getTextDelValue() {
        return textDelValue;
    }
}