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

package org.amanzi.awe.reports.geoptima.wizard;

import org.eclipse.swt.widgets.Display;

import net.refractions.udig.project.internal.Layer;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class WizardUtils {
    public static void setLayerVisibility(final Layer layer, final boolean visible) {
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                layer.setVisible(visible);
            }
        });
    }

}
