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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.neo.loader.core.LoaderUtils;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NodeTypes;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.helpers.Predicate;
import org.neo4j.kernel.Traversal;

import au.com.bytecode.opencsv.CSVWriter;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Export network sector
 * </p>.
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public class ExportNetworkAction extends Action {

    @Override
	public void run() {
        // TODO init
        IWorkbench workbench = null;

        Shell shell = Display.getDefault().getActiveShell();
        ExportNetworkWizard wizard = new ExportNetworkWizard();
        wizard.init(workbench, selection);
        WizardDialog dialog = new WizardDialog(shell, wizard);
        dialog.create();
        dialog.open();
    }


    /** The root. */
    Node root = null;
    private final IStructuredSelection selection;

    /**
     * Instantiates a new export network action.
     *
     * @param selection the selection
     */
    public ExportNetworkAction(IStructuredSelection selection) {
        this.selection = selection;
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

    /**
     * Run.
     */
    public void runn() {
        Shell shell = new Shell(Display.getDefault());

        FileDialog dialog = new FileDialog(shell, SWT.SAVE | SWT.APPLICATION_MODAL/* or SAVE or MULTI */);
        final String fileSelected = dialog.open();
        final Node rootNode = this.root;
        Job exportJob = new Job("Network export") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                	
                	runExport(fileSelected,rootNode);
                    
                    return Status.OK_STATUS;
                } finally {
//                    ActionUtil.getInstance().runTask(new Runnable() {
//
//                        @Override
//                        public void run() {
////                            button.setEnabled(true);
////                            tableControl.setEnabled(true);
//                        }
//                    }, true);
                }
            }
        };
        exportJob.schedule();
        
        
        
        
        System.out.println("Finished");
    }


	/**
	 * Run export.
	 *
	 * @param fileSelected the file selected
	 * @param rootNode 
	 */
	protected void runExport(final String fileSelected, Node rootNode) {
		
        DatasetService datasetService = NeoServiceFactory.getInstance().getDatasetService();
        String[] strtypes = datasetService.getSructureTypesId(rootNode);
        List<String> headers = new ArrayList<String>();
        
        for (int i = 1; i < strtypes.length; i++) {
            headers.add(strtypes[i]);
        }
		
		HashMap<String, Collection<String>> propertyMap = new HashMap<String, Collection<String>>();
		Map<String, String> originalHeaders = datasetService.getOriginalFileHeaders(rootNode);
        TraversalDescription descr = Traversal.description().depthFirst().uniqueness(Uniqueness.NONE).relationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)
                .filter(Traversal.returnAllButStartNode());
        for (Path path : descr.traverse(rootNode)) {
            Node node = path.endNode();
            INodeType type = datasetService.getNodeType(node);
            if (type != null && headers.contains(type.getId())) {
                Collection<String> coll = propertyMap.get(type.getId());
                if (coll == null) {
                    coll = new TreeSet<String>();
                    propertyMap.put(type.getId(), coll);
                }
                for (String propertyName : node.getPropertyKeys()) {
                    if (INeoConstants.PROPERTY_TYPE_NAME.equals(propertyName)){
                        continue;
                    }
                    coll.add(propertyName);
                }
            }
        }
        descr = descr.filter(new Predicate<Path>() {

            @Override
            public boolean accept(Path paramT) {
                return !paramT.endNode().hasRelationship(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING);
            }
        });
        Iterator<Path> iter = descr.traverse(rootNode).iterator();
        List<String> fields = new ArrayList<String>();
        if (fileSelected != null) {
            try {

            	String ext = "";
            	
                String extention = LoaderUtils.getFileExtension(fileSelected);
                if (extention.equals("")) {
                	ext= ".csv";
                }
                else if (extention.equals(".")) {
                	ext= "csv";
                }
                
                CSVWriter writer = new CSVWriter(new FileWriter(fileSelected + ext));
                try {
                    for (String headerType : headers) {
                        Collection<String> propertyCol = propertyMap.get(headerType);
                        String parcePrefix = "";
                        String writePrefix = "";
                        if(headerType.equals(NodeTypes.SITE.getId())){
                        	parcePrefix = INeoConstants.SITE_PROPERTY_NAME_PREFIX;
                            // writePrefix = "SITE_";
                        }else if(headerType.equals(NodeTypes.SECTOR.getId())){
                        	parcePrefix = INeoConstants.SECTOR_PROPERTY_NAME_PREFIX;
                        } else if (headerType.equals(NodeTypes.BSC.getId())) {
                            parcePrefix = INeoConstants.BSC_PROPERTY_NAME_PREFIX;
                        }
                        if (propertyCol != null) {
                            for (String propertyName : propertyCol) {
//                                fields.add(new StringBuilder(headerType).append("_").append(propertyName).toString());
                                fields.add(originalHeaders.get(parcePrefix + propertyName) == null ? writePrefix + propertyName : writePrefix
                                        + originalHeaders.get(parcePrefix + propertyName));
                            }
                        }
                    }

                    writer.writeNext(fields.toArray(new String[0]));
                    while (iter.hasNext()) {
                        fields.clear();
                        Path path = iter.next();
//                        int order = 1;
                        for (Node node : path.nodes()) {
                            INodeType nodeType = datasetService.getNodeType(node);
                            if (nodeType == NodeTypes.NETWORK) {
                                continue;
                            }
//                            while (order < strtypes.length && !strtypes[order++].equals(nodeType.getId())) {
//                                Collection<String> propertyCol = propertyMap.get(strtypes[order - 1]);
//                                if (propertyCol != null) {
//                                    for (@SuppressWarnings("unused")
//                                    String propertyName : propertyCol) {
//                                        fields.add("");
//                                    }
//                                }
//                            }
//                            if (order > strtypes.length) {
//                                fields.add("ERROR - incorrect node structure ");
//                                break;
//                            } else {
                                Collection<String> propertyCol = propertyMap.get(nodeType.getId());
                                if (propertyCol != null) {
                                    for (String propertyName : propertyCol) {
                                        fields.add(String.valueOf(node.getProperty(propertyName, "")));
                                    }
                                }
//                            }
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
