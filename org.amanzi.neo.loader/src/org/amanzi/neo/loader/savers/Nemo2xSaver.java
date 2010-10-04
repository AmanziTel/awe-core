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

import java.util.Arrays;
import java.util.List;

import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.GisProperties;
import org.amanzi.neo.loader.NemoLoader.Event;
import org.amanzi.neo.loader.core.parser.LineTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.IStructuredSaver;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.hsqldb.lib.StringUtil;
import org.neo4j.graphdb.Node;

/**
 * <p>
 *Nemo 2x Saver
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class Nemo2xSaver extends AbstractHeaderSaver<LineTransferData> implements IStructuredSaver<LineTransferData>{

    @Override
    public void save(LineTransferData element) {
        String line=element.getStringLine();
        if (StringUtil.isEmpty(line)){
            return;
        }
        String[] parsedLineArr = splitLine(line);
        List<String> parsedLine = Arrays.asList(parsedLineArr);
        if (parsedLine.size() < 1) {
            return;
        }

    }

    /**
     *
     * @param line
     * @return
     */
    protected String[] splitLine(String line) {
        return line.split(",");
    }

    @Override
    protected void fillRootNode(Node rootNode, LineTransferData element) {
    }

    @Override
    protected String getRootNodeType() {
        return NodeTypes.DATASET.getId();
    }

    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return NodeTypes.MP.getId();
    }

    @Override
    public boolean beforeSaveNewElement(LineTransferData element) {
        return false;
    }

    @Override
    public void finishSaveNewElement(LineTransferData element) {
    }

}
