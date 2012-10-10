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

package org.amanzi.neo.models;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.nodetypes.INodeType;

/**
 * <p>
 * This interface encapsulates methods, that are common to models containing data.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
// TODO: LN: 10.10.2012, add comments
public interface IDataModel extends IModel {

    /**
     * The method is supposed to find a parent for the node, contained in <code>childElement</code>,
     * and create an <code>IDataElement</code> based on result.
     * 
     * @param childElement
     * @return <code>IDataElement</code> based on found parent node or <code>null</code>.
     */
    IDataElement getParentElement(IDataElement childElement) throws ModelException;

    Iterable<IDataElement> getChildren(IDataElement parentElement) throws ModelException;

    Iterable<IDataElement> getAllElementsByType(INodeType nodeType) throws ModelException;

    void deleteElement(IDataElement element) throws ModelException;

}
