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

package org.amanzi.awe.wizards.geoptima.export;

import org.neo4j.graphdb.GraphDatabaseService;

/**
 * <p>
 * Export model from neo4j
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public abstract class AbstractNeoExportModel extends AbstractExportModel {
    
    /** The service. */
    protected final GraphDatabaseService service;

    /**
     * Instantiates a new neo export model.
     *
     * @param service the service
     */
    public AbstractNeoExportModel(GraphDatabaseService service) {
        super();
        this.service = service;
    }

}
