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

package org.amanzi.neo.loader.savers;

import org.amanzi.neo.core.utils.GisProperties;
import org.amanzi.neo.loader.core.parser.HeaderTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.IStructuredSaver;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class RoamesSaver extends AbstractHeaderSaver<HeaderTransferData> implements IStructuredSaver<HeaderTransferData> {

    @Override
    public void save(HeaderTransferData element) {
    }

    @Override
    public boolean beforeSaveNewElement(HeaderTransferData element) {
        return false;
    }

    @Override
    public void finishSaveNewElement(HeaderTransferData element) {
    }

    @Override
    protected void fillRootNode(Node rootNode, HeaderTransferData element) {
    }

    @Override
    protected String getRootNodeType() {
        return null;
    }

    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return null;
    }

}
