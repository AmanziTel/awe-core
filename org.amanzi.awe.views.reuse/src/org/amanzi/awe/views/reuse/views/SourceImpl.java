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

package org.amanzi.awe.views.reuse.views;

import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class SourceImpl implements ISource {

    private Node source;
    private Object value;

    /**
     * @param source
     * @param value
     */
    public SourceImpl(Node source, Object value) {
        super();
        this.source = source;
        this.value = value;
    }

    @Override
    public Node getSource() {
        return source;
    }

    @Override
    public Object getValue() {
        return value;
    }

}
