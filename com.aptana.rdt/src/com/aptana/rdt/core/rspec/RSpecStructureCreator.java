package com.aptana.rdt.core.rspec;

import java.util.ArrayList;
import java.util.List;

import org.jruby.ast.FCallNode;
import org.rubypeople.rdt.internal.core.parser.InOrderVisitor;
import org.rubypeople.rdt.internal.core.util.ASTUtil;

public class RSpecStructureCreator extends InOrderVisitor {
	
	private List<Behavior> behaviors = new ArrayList<Behavior>();
	
	public Object visitFCallNode(FCallNode visited) {
		if (visited.getName().equals("describe"))
		{ // start of a behavior
			List<String> args = ASTUtil.getArgumentsFromFunctionCall(visited);
			String className = args.get(0);
			int start = visited.getPosition().getStartOffset();
			behaviors.add(new Behavior(className, start, visited.getPosition().getEndOffset() - start));
		}
		else if (visited.getName().equals("it"))
		{ // start of example for behavior
			List<String> args = ASTUtil.getArgumentsFromFunctionCall(visited);
			String description = args.get(0);
			int start = visited.getPosition().getStartOffset();
			behaviors.get(behaviors.size() - 1).addExample(new Example(description, start, visited.getPosition().getEndOffset() - start));
		}
		return super.visitFCallNode(visited);
	}

	public Object[] getBehaviors() {
		return behaviors.toArray(new Object[behaviors.size()]);
	}
}
