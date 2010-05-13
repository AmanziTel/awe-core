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

package org.amanzi.awe.wizards.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class SelectPropertyPage extends WizardPage {
    public static final int ANY = Integer.MAX_VALUE;
    private int numOfProperties;
    private List<String> availableProperties = new ArrayList<String>();
    private Combo[] combos;
    private String[] previousSelection;

    public SelectPropertyPage(String pageName, int numOfProperties) {
        super(pageName);
        setTitle("Select property to be analyzed");
        this.numOfProperties = numOfProperties;
        availableProperties.add("Property1");
        availableProperties.add("Property2");
        availableProperties.add("Property3");
        availableProperties.add("Property4");
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FormLayout());

        if (numOfProperties >= 1 && numOfProperties <= 5) {
            // create numOfProperties select boxes
            combos = new Combo[numOfProperties];
            for (int i = 0; i < numOfProperties; i++) {
                final Combo combo = new Combo(container, SWT.READ_ONLY);
                Label label = new Label(container, SWT.LEFT);
                label.setText("Select property #" + (i + 1) + ":");

                combo.setData(i);
                FormData formData = new FormData();
                formData.left = new FormAttachment(0, 2);
                formData.right = new FormAttachment(100, -2);
                if (i == 0) {
                    formData.top = new FormAttachment(0, 2);
                    label.setLayoutData(formData);
                    combo.setItems(availableProperties.toArray(new String[] {}));
                } else {
                    formData.top = new FormAttachment(combos[i - 1], 2);
                    label.setLayoutData(formData);
                }
                formData = new FormData();
                formData.left = new FormAttachment(0, 2);
                formData.right = new FormAttachment(100, -2);
                formData.top = new FormAttachment(label, 2);
                combo.setLayoutData(formData);

                // combo.setEnabled(i == 0);
                combo.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        String text = ((Combo)e.getSource()).getText();
                        Object data = e.data;
                        updateCombos((Integer)combo.getData());
                    }

                });
                combos[i] = combo;

            }

        } else {
            // create 2 lists: available and selected properties
            if (numOfProperties != ANY) {
                // add listener which checks number of selected properties
            }
        }
        setControl(container);
    }

    protected void updateCombos(int num) {
        String newValue = combos[num].getText();
        String previousValue = previousSelection[num];
        previousSelection[num] = newValue;
        for (int i = 0; i < combos.length; i++) {
            if (i != num) {
                Combo combo = combos[i];
                if (newValue != null && newValue.length() != 0)
                    combo.remove(newValue);
                if (previousValue != "")
                    combo.add(previousValue);
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {
        fillCombosWithDefaultValues();
        super.setVisible(visible);
    }

    private void fillCombosWithDefaultValues() {
        for (int i = 0; i < combos.length; i++) {
            ArrayList<String> values = new ArrayList<String>(availableProperties);
            combos[i].setItems(values.toArray(new String[] {}));
        }
        previousSelection = new String[combos.length];
        Arrays.fill(previousSelection, "");
    }

}
