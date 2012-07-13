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

package org.amanzi.neo.models.impl;

import org.amanzi.neo.models.IIndexModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.IIndexService;
import org.amanzi.neo.services.impl.indexes.MultiPropertyIndex;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class IndexModel extends AbstractModel implements IIndexModel {

    private IIndexService indexService;

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public IndexModel(final IGeneralNodeProperties generalNodeProperties, final IIndexService indexService) {
        super(null, generalNodeProperties);
    }

    @Override
    public void finishUp() throws ModelException {

    }

    @Override
    public String getIndexKey(final Node rootNode, final INodeType nodeType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Node getSingleNode(final String indexKey, final String propertyName, final Object value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void index(final String key, final String proeprtyName, final Node node) {
        // TODO Auto-generated method stub

    }

    @Override
    public <T> MultiPropertyIndex<T> getMultiPropertyIndex(final INodeType nodeType, final Node rootNode,
            final String... propertyNames) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMultiPropertyIndexKey(final Node rootNode, final INodeType nodeType, final String indexName) {
        // TODO Auto-generated method stub
        return null;
    }

}
