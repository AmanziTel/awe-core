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

package org.amanzi.statistics.view.components;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;

/**
 * Viewer for property based KPIs.
 * @author Pechko_E
 * @since 1.0.0
 */
public class KPIPropertyViewer extends KPIViewer {

    private static final String PROPERTY = "Property:";
    private PropertyComboViewer propertyComboViewer;
    private List<String> availableProperties;

    public KPIPropertyViewer(Composite parent, int style, List<String> availableProperties) {
        super(parent, style);
        this.availableProperties=availableProperties;
        initControls();
    }

    @Override
    public Composite addControls(Composite parent) {
        propertyComboViewer = new PropertyComboViewer(parent,PROPERTY);
        return propertyComboViewer.getContainer();
    }

    @Override
    public void addListeners() {
        //nothing
    }

    @Override
    public void initControls() {
        propertyComboViewer.setInput(availableProperties);
    }
    public void updateAvailableProperties(String[] input){
        propertyComboViewer.setInput(input);
    }
    public void updateAvailableProperties(List<String> input){
        propertyComboViewer.setInput(input);
    }

}
