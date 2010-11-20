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

import java.io.IOException;
import java.util.List;

import org.amanzi.neo.services.enums.DriveTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class DingLiLoader extends DriveLoader {
    /**
 * 
 */
    public DingLiLoader(String filename, Display display, String dataset) {
        driveType = DriveTypes.TEMS;
        initialize("DingLi", null, filename, display, dataset);
        initializeLuceneIndex();
        addDriveIndexes();
        possibleFieldSepRegexes = new String[] {"\t"};
    }
@Override
protected boolean needParceHeaders() {
    return false;
}
    private void addDriveIndexes() {
        try {
            addIndex(NodeTypes.M.getId(), NeoUtils.getTimeIndexProperty(dataset));
            addIndex(NodeTypes.MP.getId(), NeoUtils.getLocationIndexProperty(dataset));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    protected void parseLine(String line) {
        if (parser == null) {
            determineFieldSepRegex(line);
        }

        List<String> parsedLine = splitLine(line);
        if (parsedLine.size() < 1) {
            return;
        }
        String elem=parsedLine.get(0);
        if ("FileInfo".equals(elem)){
            return;
        }
        if ("RegReport".equals(elem)){
            updatePropertyHeaders(parsedLine);
            return;
        }
        storeEvent(parsedLine);
    }

    /**
     *
     * @param parsedLine
     */
    private void storeEvent(List<String> parsedLine) {
    }
    /**
     *
     * @param parsedLine
     */
    private void updatePropertyHeaders(List<String> parsedLine) {
    }
    @Override
    protected String getPrymaryType(Integer key) {
        return NodeTypes.M.getId();
    }

    @Override
    protected Node getStoringNode(Integer key) {
        return datasetNode;
    }

}
