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

package org.amanzi.neo.core.utils;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * <p>
 * Lable provider implemenation of neoTree elements
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class NeoTreeLabelProvider extends LabelProvider {
    @Override
    public Image getImage(Object element) {
        return ((NeoTreeElement)element).getImage();
    }

    @Override
    public String getText(Object element) {
        return ((NeoTreeElement)element).getText();
    }
}
