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

package org.amanzi.neo.loader;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.OssType;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class APDLoader extends AbstractLoader {
    private static boolean needParceHeader = true;
    private final LinkedHashMap<String, Header> headers;

    private Node ossRoot;
    private Node fileNode;
    private Node lastChild;

    /**
     * Constructor
     * 
     * @param directory
     * @param datasetName
     * @param display
     */
    public APDLoader(String directory, String datasetName, Display display) {
        initialize("APD", null, directory, display);
        basename = datasetName;
        headers = getHeaderMap(1).headers;
    }

    @Override
    protected Node getStoringNode(Integer key) {
        return ossRoot;
    }

    @Override
    protected boolean needParceHeaders() {
        if (needParceHeader) {
            needParceHeader = false;
            return true;
        }
        return false;
    }

    @Override
    protected void parseLine(String line) {
        if (fileNode == null) {
            ossRoot = LoaderUtils.findOrCreateOSSNode(OssType.APD, basename, neo);
            Pair<Boolean, Node> fileNodePair = NeoUtils.findOrCreateChildNode(neo, ossRoot, new File(basename).getName());
            fileNode = fileNodePair.getRight();
            lastChild = null;
            if (fileNodePair.getLeft()) {
                NodeTypes.FILE.setNodeType(fileNode, neo);
            }
        }
        List<String> fields = splitLine(line);
        if (fields.size() < 2)
            return;
        if (this.isOverLimit())
            return;
        Map<String, Object> lineData = makeDataMap(fields);
        saveStatistic(lineData);
    }

    /**
     * @param lineData
     */
    private void saveStatistic(Map<String, Object> lineData) {
        Transaction transaction = neo.beginTx();
        try {
            Node node = neo.createNode();
            NodeTypes.M.setNodeType(fileNode, neo);
            for (Map.Entry<String, Object> entry : lineData.entrySet()) {
                node.setProperty(entry.getKey(), entry.getValue());
            }
            index(node);
            NeoUtils.addChild(fileNode, node, lastChild, neo);
            lastChild = node;
        } finally {
            transaction.finish();
        }
    }

}
