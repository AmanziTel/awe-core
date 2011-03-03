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

import org.eclipse.swt.widgets.Composite;

/**
 * Viewer for formula based KPIs.
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class KPIFormulaViewer extends KPIViewer {

    public KPIFormulaViewer(Composite parent, int style) {
        super(parent, style);
    }

    @Override
    public Composite addControls(Composite parent) {
        return null;
    }

    @Override
    public void addListeners() {
    }

    @Override
    public void initControls() {
    }

}
