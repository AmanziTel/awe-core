package org.amanzi.awe.neo.views.network.utils;

import org.amanzi.awe.neo.views.network.beans.ExtTreeNode;
import org.amanzi.awe.neo.views.network.beans.ExtTreeNodes;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewSite;
import org.neo4j.api.core.EmbeddedNeo;

/**
 * This class is responsible to populate actual object contents in a tree.
 * 
 * @author Dalibor
 */
public class TreeViewContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {

	private EmbeddedNeo Neo;
	
	public TreeViewContentProvider(EmbeddedNeo Neo)
	{
		this.Neo=Neo;
	}
	
	
	@Override
	public Object[] getElements(Object inputElement) {
		 if (inputElement instanceof IViewSite) 
		 {
	         ExtTreeNodes elements=new ExtTreeNodes(Neo);
	         
	            return elements.getList().toArray();
	     }
	            return getChildren(inputElement);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getChildren(Object parentElement) 
	{
		ExtTreeNode treeNode = (ExtTreeNode) parentElement;
		ExtTreeNodes childrenNodes=new ExtTreeNodes(Neo,Neo.getNodeById(treeNode.getNodeId()));
		
		return childrenNodes.getList().toArray();
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		  final ExtTreeNode extTreeNode = (ExtTreeNode) element;
	        return !extTreeNode.isLeaf();
	}
	
}
