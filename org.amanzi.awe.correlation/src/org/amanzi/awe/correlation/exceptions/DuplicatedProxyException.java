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

package org.amanzi.awe.correlation.exceptions;

import java.text.MessageFormat;

import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.exceptions.enums.ServiceExceptionReason;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class DuplicatedProxyException extends ServiceException {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 6486175491123927070L;

    private static final String MESSAGE_TEMPLATE = "Proxy for sector <{0}>. and measurement <{1}> already exist in correlation model <{2}>.";

    private final String rootNode;

    private final String sectorNode;

    private final String measurementNode;

    public DuplicatedProxyException(final Node rootNode, final Node sectorNode, final Node measurementNode) {
        super(ServiceExceptionReason.DUPLICATED_NODE);
        this.rootNode = rootNode.toString();
        this.sectorNode = sectorNode.toString();
        this.measurementNode = measurementNode.toString();
    }

    /**
     * @return Returns the measurementNode.
     */
    public String getMeasurementNode() {
        return measurementNode;
    }

    @Override
    public String getMessage() {
        return MessageFormat.format(MESSAGE_TEMPLATE, getSectorNode(), getMeasurementNode(), getRootNode());
    }

    /**
     * @return Returns the rootNode.
     */
    public String getRootNode() {
        return rootNode;
    }

    /**
     * @return Returns the sectorNode.
     */
    public String getSectorNode() {
        return sectorNode;
    }

}
