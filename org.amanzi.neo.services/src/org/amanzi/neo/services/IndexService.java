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

package org.amanzi.neo.services;

import java.io.IOException;

import org.amanzi.neo.db.manager.NeoServiceProvider;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.indexes.MultiPropertyIndex;
import org.amanzi.neo.services.indexes.MultiPropertyIndex.MultiDoubleConverter;
import org.amanzi.neo.services.indexes.MultiPropertyIndex.MultiTimeIndexConverter;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * Service to work with MultiProperty Index
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class IndexService extends NewAbstractService {
    
    private static final Logger LOGGER = Logger.getLogger(IndexService.class);
    

    /**
     * Creates MultiPropertyIndex based on Timestamp property
     *
     * @param rootNode
     * @param nodeType
     * @return
     * @throws IOException
     */
    public MultiPropertyIndex<Long> createTimestampIndex(Node rootNode, INodeType nodeType) throws DatabaseException {
        Transaction tx = graphDb.beginTx();
        
        try {
            String indexName = getIndexKey(rootNode, nodeType);
            MultiPropertyIndex<Long> result = new MultiPropertyIndex<Long>(indexName, new String[] {DriveModel.TIMESTAMP},
                    new MultiTimeIndexConverter(), 10);
            result.initialize(NeoServiceProvider.getProvider().getService(), rootNode);
            
            tx.success();
            
            return result;
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error on creation MultiProperty Index on Timestamp for <" + rootNode + ", " + nodeType + ".", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
    }

    public MultiPropertyIndex<Double> createLocationIndex(Node rootNode, INodeType nodeType) throws DatabaseException {
Transaction tx = graphDb.beginTx();
        
        try {
            String indexName = getIndexKey(rootNode, nodeType);
            MultiPropertyIndex<Double> result = new MultiPropertyIndex<Double>(indexName, new String[] {DriveModel.LATITUDE,
                    DriveModel.LONGITUDE}, new MultiDoubleConverter(0.001), 10);
            result.initialize(NeoServiceProvider.getProvider().getService(), rootNode);
            return result;
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error on creation MultiProperty Index on Location for <" + rootNode + ", " + nodeType + ".", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
    }

}
