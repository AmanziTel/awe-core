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

package org.amanzi.awe.afp.ericsson.parser;

import java.util.Arrays;

import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.IStructuredSaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkConfigurationSaver extends AbstractHeaderSaver<BaseTransferData> implements IStructuredSaver<BaseTransferData>{
    private final MetaData metadata=new MetaData("network", MetaData.SUB_TYPE,"radio");
    private NetworkConfigurationFileTypes type;
    @Override
    public void init(BaseTransferData element) {
        super.init(element);
        startMainTx(2000);
    }
    @Override
    public void save(BaseTransferData element) {
    }

    @Override
    public Iterable<MetaData> getMetaData() {
        return Arrays.asList(new MetaData[]{metadata});
    }

    @Override
    public boolean beforeSaveNewElement(BaseTransferData element) {
         type=NetworkConfigurationFileTypes.valueOf(element.get("fileType"));
        return false;
    }

    @Override
    public void finishSaveNewElement(BaseTransferData element) {
    }

    @Override
    protected void fillRootNode(Node rootNode, BaseTransferData element) {
    }

    @Override
    protected String getRootNodeType() {
        return NodeTypes.NETWORK.getId();
    }

    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return null;
    }


}
