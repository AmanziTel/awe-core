package org.amanzi.splash.report;

import org.amanzi.neo.core.database.nodes.RubyProjectNode;
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
