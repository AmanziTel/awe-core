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

package org.amanzi.splash.printing;

import net.refractions.udig.printing.ui.Template;
import net.refractions.udig.printing.ui.TemplateFactory;

/**
 * Implementation of TemplateFactory for ChartTemplate (it's necessary for an extension)
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class ChartTemplateFactory implements TemplateFactory {

    @Override
    public Template createTemplate() {
        return new ChartTemplate();
    }

    @Override
    public String getName() {
        return "Chart template";// this name will be displayed in the list with available templates
    }

}
