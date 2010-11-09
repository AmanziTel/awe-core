package org.amanzi.awe.report.editor;

import org.amanzi.neo.services.nodes.RubyProjectNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.part.FileEditorInput;

public class ReportInput extends FileEditorInput {
private RubyProjectNode node;
    public ReportInput(IFile file) {
        super(file);
    }
    /**
     * @return Returns the node.
     */
    public RubyProjectNode getNode() {
        return node;
    }
    /**
     * @param node The node to set.
     */
    public void setNode(RubyProjectNode node) {
        this.node = node;
    }

}
