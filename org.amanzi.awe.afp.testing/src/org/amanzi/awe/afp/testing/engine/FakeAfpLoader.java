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

package org.amanzi.awe.afp.testing.engine;

import org.amanzi.awe.afp.exporters.AfpExporter;
import org.amanzi.awe.afp.loaders.AfpOutputFileLoader;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public class FakeAfpLoader extends AfpOutputFileLoader {

    /**
     * @param networkRoot
     * @param afpDataset
     * @param exporter
     */
    public FakeAfpLoader(Node networkRoot, Node afpDataset, AfpExporter exporter) {
        super(networkRoot, afpDataset, exporter);
    }
    
    protected void defineRoot() {
        defineRoot("project");
    }

}
