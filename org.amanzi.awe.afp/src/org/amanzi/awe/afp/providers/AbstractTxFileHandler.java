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

package org.amanzi.awe.afp.providers;

import org.amanzi.awe.afp.loaders.TxtFileImporter;
import org.amanzi.awe.afp.loaders.TxtFileImporter.TxtLineParameter;
import org.amanzi.neo.core.utils.importer.IImportHandler;
import org.amanzi.neo.core.utils.importer.IImportParameter;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Abstract handler of importing txt files
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public abstract class AbstractTxFileHandler implements IImportHandler {
    protected final Node rootNode;
    protected final GraphDatabaseService service;

    /**
     * Instantiates a new abstract tx file handler.
     * 
     * @param rootNode the root node
     * @param service the service
     */
    public AbstractTxFileHandler(Node rootNode, GraphDatabaseService service) {
        super();
        this.rootNode = rootNode;
        this.service = service;
    }

    @Override
    public void init() {
        // do nothing
    }

    @Override
    public void handleData(IImportParameter parameter) {
        assert parameter instanceof TxtLineParameter;
        String line = ((TxtLineParameter)parameter).line;
        storeLine(line);
    }

    /**
     * Store line.
     * 
     * @param line the line
     */
    protected abstract void storeLine(String line);

    @Override
    public void finish() {
        // do nothing
    }

}
