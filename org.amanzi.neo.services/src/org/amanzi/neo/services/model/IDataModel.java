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

package org.amanzi.neo.services.model;

import org.amanzi.neo.services.enums.INodeType;

/**
 * <p>
 * This interface encapsulates methods, that are common to models containing data.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface IDataModel extends IModel {

    /**
     * The method is supposed to find a parent for the node, contained in <code>childElement</code>,
     * and create an <code>IDataElement</code> based on result.
     * 
     * @param childElement
     * @return <code>IDataElement</code> based on found parent node or <code>null</code>.
     */
    public IDataElement getParentElement(IDataElement childElement);

    /**
     * Find all direct children or a children chain for the defined element.
     * 
     * @param parent
     * @return an <code>Iterable</code> of <code>IDataElement</code>s, based on the found nodes.
     */
    public Iterable<IDataElement> getChildren(IDataElement parent);

    /**
     * Find all elements with the defined type, that belong to the current model structure.
     * 
     * @param elementType
     * @return an <code>Iterable</code> of <code>IDataElement</code>s, based on the found nodes.
     */
    public Iterable<IDataElement> getAllElementsByType(INodeType elementType);

    public void finishUp();
}
