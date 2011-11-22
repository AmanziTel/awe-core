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

package org.amanzi.awe.views.explorer.providers;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

/**
 * @author Vladislav_Kondratenko
 */
public class ProjectTreeLabelProvider extends LabelProvider {

    /**
     * Constructor. Gets an instance of IconManager
     * 
     * @param viewer of this LabelProvider
     */
    public ProjectTreeLabelProvider(Viewer viewer) {
        
    }

    @Override
    public String getText(Object element) {
        if (element instanceof IProjectModel) {
            return ((IModel)element).getName();
        } else if (element instanceof IDataElement) {
            return ((IDataElement)element).get(NewAbstractService.NAME).toString();
        } else
            return null;
    }

    /**
     * The <code>LabelProvider</code> implementation of this <code>ILabelProvider</code> method
     * returns <code>null</code>. Subclasses may override.
     */
    public Image getImage(Object element) {
        // else search for image by given type
        
    	return null;
    }
}
