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

package org.amanzi.neo.impl.dto;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.dto.ISourcedElement;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class SourcedElement extends DataElement implements ISourcedElement {

    public interface ICollectFunction {

        Iterable<IDataElement> collectSourceElements(IDataElement parent);

    }

    private final ICollectFunction function;

    public SourcedElement(final Node node, final ICollectFunction collectFunction) {
        super(node);
        this.function = collectFunction;
    }

    @Override
    public Iterable<IDataElement> getSources() {
        return function.collectSourceElements(this);
    }

}
