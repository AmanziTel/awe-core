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

package org.amanzi.awe.correlation.model.impl;

import org.amanzi.awe.correlation.model.IProxyElement;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.impl.dto.DataElement;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ProxyElement extends DataElement implements IProxyElement {

    private final IDataElement measurement;
    private final IDataElement sector;

    public ProxyElement(final Node rootNode, final IDataElement sector, final IDataElement measurement) {
        super(rootNode);
        this.sector = sector;
        this.measurement = measurement;
    }

    @Override
    public IDataElement getCorrelatedElement() {
        return measurement;
    }

    @Override
    public IDataElement getSectorElement() {
        return sector;
    }

}
