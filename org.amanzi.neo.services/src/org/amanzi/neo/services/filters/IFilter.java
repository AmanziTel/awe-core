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

package org.amanzi.neo.services.filters;

import java.io.Serializable;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.filters.exceptions.FilterTypeException;
import org.amanzi.neo.services.filters.exceptions.NotComparebleException;
import org.amanzi.neo.services.filters.exceptions.NullValueException;
import org.neo4j.graphdb.Node;

/**
 * <p>
 *Filter interface
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public interface IFilter extends Serializable {

    void setExpression(INodeType nodeType, String propertyName, Serializable value);
    
    void setExpression(INodeType nodeType, String propertyName) throws FilterTypeException;

    void addFilter(IFilter additionalFilter);

    boolean check(Node node) throws NotComparebleException, NullValueException;

    INodeType getNodeType();

}