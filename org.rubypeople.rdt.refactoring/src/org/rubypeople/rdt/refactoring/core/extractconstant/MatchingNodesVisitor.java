package org.rubypeople.rdt.refactoring.core.extractconstant;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.jruby.ast.Node;
import org.jruby.ast.visitor.rewriter.DefaultFormatHelper;
import org.jruby.ast.visitor.rewriter.FormatHelper;
import org.jruby.ast.visitor.rewriter.ReWriteVisitor;
import org.jruby.ast.visitor.rewriter.utils.ReWriterContext;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.internal.core.parser.InOrderVisitor;

public class MatchingNodesVisitor extends InOrderVisitor {

	private Node toMatch;
	private String src;
	private String selectedNodeSrc;
	private List<Node> matches;

	public MatchingNodesVisitor(Node selection, String src) {
		this.toMatch = selection;
		this.src = src;
		this.selectedNodeSrc = getSource(selection);
		this.matches = new ArrayList<Node>();
	}
	
	@Override
	protected Instruction visitNode(Node iVisited) {
		if (iVisited != null && iVisited.getClass().equals(toMatch.getClass())) {
			// same type of node
			String currentNodeSrc = getSource(iVisited); // compare src of the nodes
			// FIXME This can be very expensive! We should compare node for node (the attributes we care about - value, args, etc)
			if (currentNodeSrc.equals(selectedNodeSrc)) {
				matches.add(iVisited);
			}
		}
		return super.visitNode(iVisited);
	}

	private String getSource(Node iVisited) {
		StringWriter writer = new StringWriter();
		FormatHelper helper = new DefaultFormatHelper();
		ReWriterContext context = new ReWriterContext(writer, src, helper);
		// FIXME Do this in a way that we don't care about difference between single and double quotes for strings!
		ReWriteVisitor visitor = new ReWriteVisitor(context);
		iVisited.accept(visitor);
		return writer.getBuffer().toString();
	}

	public List<Node> getMatches() {
		return matches;
	}

}
