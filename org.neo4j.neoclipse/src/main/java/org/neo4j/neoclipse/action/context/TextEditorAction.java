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

package org.neo4j.neoclipse.action.context;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.neoclipse.action.AbstractGraphAction;
import org.neo4j.neoclipse.action.Actions;
import org.neo4j.neoclipse.property.NodeTypes;
import org.neo4j.neoclipse.property.RelationshipTypes;
import org.neo4j.neoclipse.view.NeoGraphViewPart;

/**
 * <p>
 * Open file from node in text editor.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class TextEditorAction extends AbstractGraphAction{
    
    private static final int MAX_FILE_SIZE = 102400;
    
    public TextEditorAction( NeoGraphViewPart neoGraphViewPart ) {
        super( Actions.TEXT_EDITOR, neoGraphViewPart );
        setEnabled( true );
    }

    @Override
    public void run() {
        List<Node> nodes = graphView.getCurrentSelectedNodes();
        int count = nodes.size();
        if ( count != 1 ) {
            MessageDialog.openWarning( null, "Open in text editor", count<1?"No nodes are selected.":"More the one node are selected" );
            return;
        }
        Node fileNode = getFileNode(nodes.get(0));
        if (fileNode == null) {
            showCanNotOpenMessage();
            return;
        }
        String filename = (String)fileNode.getProperty("filename", null);
        if (filename == null) {
            showCanNotOpenMessage();
            return;
        }
        File file = new File(filename);            
        if(!file.exists()){
            showCanNotOpenMessage();
            return;
        }
        if(file.length()>MAX_FILE_SIZE&&!userConfirmTooLarge(file.getName())){
            return;
        }
        IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(filename));
        FileStoreEditorInput editorInput = new FileStoreEditorInput(fileStore);
        IWorkbench workbench = PlatformUI.getWorkbench();
        IEditorDescriptor desc = workbench.getEditorRegistry().getDefaultEditor(fileStore.getName());
        
        try {            
            if(desc==null){
                graphView.getViewSite().getPage().openEditor(editorInput, "org.eclipse.ui.DefaultTextEditor");
            }else{
                graphView.getViewSite().getPage().openEditor(editorInput, desc.getId());
            }
        } catch (PartInitException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        }
    }

    /**
     * Get file node for selected node.
     *
     * @param node Node
     * @return Node
     */
    private Node getFileNode(Node node){
        NodeTypes nodeType = NodeTypes.getNodeType(node, null);
        if(node==null||nodeType==null){
            return null;
        }
        if(nodeType.equals(NodeTypes.FILE)){
            return node;
        }
        if(nodeType.equals(NodeTypes.CALL)){
            Iterator<Node> events = node.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {                
                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    NodeTypes type = NodeTypes.getNodeType(currentPos.currentNode(), null);
                    return type!=null&&type.equals(NodeTypes.HEADER_M);
                }
            }, RelationshipTypes.CALL_M,Direction.OUTGOING).iterator();
            return events.hasNext()?getFileNodeForSubNode(events.next(), NodeTypes.HEADER_M):null;
        }
        return getFileNodeForSubNode(node, nodeType);
    }
    
    /**
     * Get file node for its children.
     *
     * @param node
     * @param nodeType
     * @return
     */
    private Node getFileNodeForSubNode(Node node, NodeTypes nodeType) {
        while(node!=null&&nodeType!=null&&!nodeType.equals(NodeTypes.FILE)){
            node = getParent(node);
            nodeType = NodeTypes.getNodeType(node, null);
        }
        return node;
    }
    
    /**
     * Show message about problem in file opening.
     */
    protected void showCanNotOpenMessage(){
        MessageDialog.openInformation(null, "No file found", "No file found for this data");
    }
    
    /**
     * Ask user is it need to open a very large file.
     *
     * @param filemane String (file name)
     * @return boolean (true - file should be opened)
     */
    protected boolean userConfirmTooLarge(String filemane){
        return MessageDialog.openConfirm(null, "File is quite large", 
                "The file "+filemane+" is quite large, do you want to open it anyway?");
    }
    
    /**
     * get parent of current node.
     * 
     * @param service NeoService if null then new transaction created
     * @param childNode child node
     * @return parent node or null
     */
    public static Node getParent(Node childNode) {
        Iterator<Node> parentIterator = childNode.traverse(Order.BREADTH_FIRST, new StopEvaluator() {

            @Override
            public boolean isStopNode(TraversalPosition currentPos) {
                return currentPos.lastRelationshipTraversed() != null && currentPos.lastRelationshipTraversed().isType(RelationshipTypes.CHILD);
            }
        }, new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                return currentPos.lastRelationshipTraversed() != null && currentPos.lastRelationshipTraversed().isType(RelationshipTypes.CHILD);
            }
        }, RelationshipTypes.CHILD, Direction.INCOMING, RelationshipTypes.NEXT, Direction.INCOMING).iterator();
        return parentIterator.hasNext() ? parentIterator.next() : null;
    }
}
