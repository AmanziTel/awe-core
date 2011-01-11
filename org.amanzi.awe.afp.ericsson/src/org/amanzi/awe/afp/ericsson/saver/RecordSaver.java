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

package org.amanzi.awe.afp.ericsson.saver;

import org.amanzi.awe.afp.ericsson.DataType;
import org.amanzi.awe.afp.ericsson.parser.RecordTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.IStructuredSaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Node;

/**
 * <p>
 *Saver of MainRecords
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
//TODO maybe extend from AbstractSaver instead AbstractHeaderSaver?
public class RecordSaver extends AbstractHeaderSaver<RecordTransferData> implements IStructuredSaver<RecordTransferData>{

    private DataType dataType;

    @Override
    public void save(RecordTransferData element) {
        //TODO implement
    }

    @Override
    public Iterable<MetaData> getMetaData() {
        return null;
    }

    @Override
    protected void fillRootNode(Node rootNode, RecordTransferData element) {
    }

    @Override
    protected String getRootNodeType() {
        return NodeTypes.NETWORK.getId();
    }

    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return NodeTypes.SITE.getId();
    }

    @Override
    public boolean beforeSaveNewElement(RecordTransferData element) {
        dataType=element.getType();
        
        return dataType==null;
    }

    @Override
    public void finishSaveNewElement(RecordTransferData element) {
    }

}
