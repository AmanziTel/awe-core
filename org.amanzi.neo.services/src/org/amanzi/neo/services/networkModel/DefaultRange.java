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

package org.amanzi.neo.services.networkModel;

import org.amanzi.neo.services.filters.Filter;
import org.amanzi.neo.services.filters.exceptions.NotComparebleException;
import org.amanzi.neo.services.filters.exceptions.NullValueException;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public abstract class DefaultRange implements IRange {
    protected  Filter filter;
    protected  Filter filterAddition;
    DefaultRange() {
        filter=null;
        filterAddition=null;
    }

    @Override
    public boolean includes(Node checkNode) {
        boolean res;
        try {
            res = filter.check(checkNode);
        } catch (NotComparebleException e) {
            // TODO Handle NotComparebleException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (NullValueException e) {
            // TODO Handle NullValueException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        return res;
    }


}
