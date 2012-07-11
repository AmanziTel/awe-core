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

package org.amanzi.awe.statistics.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

/**
 * <p>
 * create field for statistics view
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ControlsFactory {
    private static ControlsFactory factory;

    public static final ControlsFactory getInstance() {
        if (factory == null) {
            factory = new ControlsFactory();
        }
        return factory;
    }

    private ControlsFactory() {
    }

    /**
     * create combobox control
     */
    public Combo getCombobox(Composite layout) {
        return new Combo(layout, SWT.NONE);
    }

    /**
     * create label control
     * 
     * @param layout
     * @param name
     * @return
     */
    public Label getLabel(Composite layout, String name) {
        Label label = new Label(layout, SWT.NONE);
        label.setText(name);
        return label;
    }

    /**
     * create combobox control
     */
    public Button getButton(Composite layout, String name) {
        Button button = new Button(layout, SWT.NONE);
        button.setText(name);
        return button;
    }

    /**
     * create DateTime control
     * 
     * @param layout
     * @return
     */
    public DateTime getDateTime(Composite layout) {
        return new DateTime(layout, SWT.NONE);
    }

}
