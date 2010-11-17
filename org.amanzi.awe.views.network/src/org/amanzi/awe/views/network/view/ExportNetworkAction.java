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

package org.amanzi.awe.views.network.view;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.statistic.IPropertyHeader;
import org.amanzi.neo.services.statistic.PropertyHeader;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.helpers.Predicate;
import org.neo4j.kernel.Traversal;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * <p>
 * Export network sector
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class ExportNetworkAction extends Action {

    Node root = null;

    /**
     * @param selection
     */
    public ExportNetworkAction(IStructuredSelection selection) {
        setText("Export network sector data in csv file");
        if (selection.size() == 1) {
            Object elem = selection.getFirstElement();
            if (elem instanceof NeoNode) {
                Node node = ((NeoNode)elem).getNode();
                if (NodeTypes.NETWORK.checkNode(node)) {
                    root = node;
                }
            }
        }
        setEnabled(root != null);
    }

    @Override
    public void run() {
        Shell shell = new Shell(Display.getDefault());
        IPropertyHeader stat = PropertyHeader.getPropertyStatistic(root);
        DatasetService datasetService = NeoServiceFactory.getInstance().getDatasetService();
        List<INodeType> strtypes = datasetService.getSructureTypes(root);
        strtypes.remove(0);
        strtypes.remove(strtypes.size() - 1);
        List<String> fields = new ArrayList<String>();
        for (INodeType type : strtypes) {
            fields.add(type.getId());
        }
        String[] allFields = stat.getAllFields(NodeTypes.SECTOR.getId());
        fields.addAll(Arrays.asList(allFields));
        FileDialog dialog = new FileDialog(shell, SWT.SAVE /* or SAVE or MULTI */);
        String fileSelected = dialog.open();
        TraversalDescription descr = Traversal.description().depthFirst().uniqueness(Uniqueness.NONE).relationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)
                .filter(new Predicate<Path>() {

                    @Override
                    public boolean accept(Path paramT) {
                        return NodeTypes.SECTOR.checkNode(paramT.endNode());
                    }
                });
        Iterator<Path> iter = descr.traverse(root).iterator();
        if (fileSelected != null) {
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(fileSelected));
                try {

                    writer.writeNext(fields.toArray(new String[0]));
                    while (iter.hasNext()) {
                        fields.clear();
                        Path path = iter.next();
                        for (Node node : path.nodes()) {
                            INodeType nodeType = datasetService.getNodeType(node);
                            if (nodeType == NodeTypes.NETWORK || nodeType == NodeTypes.SECTOR) {
                                continue;
                            }
                            fields.add(datasetService.getNodeName(node));
                        }
                        for (String field:allFields){
                            fields.add(String.valueOf(path.endNode().getProperty(field,""))); 
                        }
                        writer.writeNext(fields.toArray(new String[0])); 
                    }
                } finally {
                    writer.close();
                }
            } catch (IOException e) {
                // TODO Handle IOException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
    }
}
