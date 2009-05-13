package org.rubypeople.rdt.core.parser.warnings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jruby.ast.AliasNode;
import org.jruby.ast.AndNode;
import org.jruby.ast.ArgsCatNode;
import org.jruby.ast.ArgsNode;
import org.jruby.ast.ArgsPushNode;
import org.jruby.ast.ArrayNode;
import org.jruby.ast.AttrAssignNode;
import org.jruby.ast.BackRefNode;
import org.jruby.ast.BeginNode;
import org.jruby.ast.BignumNode;
import org.jruby.ast.BlockArgNode;
import org.jruby.ast.BlockNode;
import org.jruby.ast.BlockPassNode;
import org.jruby.ast.BreakNode;
import org.jruby.ast.CallNode;
import org.jruby.ast.CaseNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.ClassVarDeclNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.Colon3Node;
import org.jruby.ast.ConstDeclNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DAsgnNode;
import org.jruby.ast.DRegexpNode;
import org.jruby.ast.DStrNode;
import org.jruby.ast.DSymbolNode;
import org.jruby.ast.DVarNode;
import org.jruby.ast.DXStrNode;
import org.jruby.ast.DefinedNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.DotNode;
import org.jruby.ast.EnsureNode;
import org.jruby.ast.EvStrNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.FalseNode;
import org.jruby.ast.FixnumNode;
import org.jruby.ast.FlipNode;
import org.jruby.ast.FloatNode;
import org.jruby.ast.ForNode;
import org.jruby.ast.GlobalAsgnNode;
import org.jruby.ast.GlobalVarNode;
import org.jruby.ast.HashNode;
import org.jruby.ast.IfNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.IterNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.Match2Node;
import org.jruby.ast.Match3Node;
import org.jruby.ast.MatchNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.MultipleAsgn19Node;
import org.jruby.ast.MultipleAsgnNode;
import org.jruby.ast.NewlineNode;
import org.jruby.ast.NextNode;
import org.jruby.ast.NilNode;
import org.jruby.ast.Node;
import org.jruby.ast.NotNode;
import org.jruby.ast.NthRefNode;
import org.jruby.ast.OpAsgnAndNode;
import org.jruby.ast.OpAsgnNode;
import org.jruby.ast.OpAsgnOrNode;
import org.jruby.ast.OpElementAsgnNode;
import org.jruby.ast.OrNode;
import org.jruby.ast.PostExeNode;
import org.jruby.ast.PreExeNode;
import org.jruby.ast.RedoNode;
import org.jruby.ast.RegexpNode;
import org.jruby.ast.RescueBodyNode;
import org.jruby.ast.RescueNode;
import org.jruby.ast.RestArgNode;
import org.jruby.ast.RetryNode;
import org.jruby.ast.ReturnNode;
import org.jruby.ast.RootNode;
import org.jruby.ast.SClassNode;
import org.jruby.ast.SValueNode;
import org.jruby.ast.SelfNode;
import org.jruby.ast.SplatNode;
import org.jruby.ast.StrNode;
import org.jruby.ast.SuperNode;
import org.jruby.ast.SymbolNode;
import org.jruby.ast.ToAryNode;
import org.jruby.ast.TrueNode;
import org.jruby.ast.UndefNode;
import org.jruby.ast.UntilNode;
import org.jruby.ast.VAliasNode;
import org.jruby.ast.VCallNode;
import org.jruby.ast.WhenNode;
import org.jruby.ast.WhileNode;
import org.jruby.ast.XStrNode;
import org.jruby.ast.YieldNode;
import org.jruby.ast.ZArrayNode;
import org.jruby.ast.ZSuperNode;
import org.jruby.ast.visitor.NodeVisitor;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.core.compiler.IProblem;
import org.rubypeople.rdt.internal.core.parser.Error;
import org.rubypeople.rdt.internal.core.parser.Warning;
import org.rubypeople.rdt.internal.core.util.ASTUtil;

public abstract class RubyLintVisitor implements NodeVisitor {

	private String contents;
	protected Map<String, String> fOptions;
	private List<CategorizedProblem> problems;

	public RubyLintVisitor(String contents) {
		this(RubyCore.getOptions(), contents);
	}
	
	public RubyLintVisitor(Map<String, String> options, String contents) {
		this.problems = new ArrayList<CategorizedProblem>();
		this.contents = contents;
		this.fOptions = options;
	}
	
	protected String getSource(Node node) {
		return ASTUtil.getSource(contents, node);
	}
	
	protected String getSource(int start, int end) {
		if (contents.length() < end) end = contents.length();
		if (start < 0) start = 0;
		return contents.substring(start, end);
	}
	
	public List<CategorizedProblem> getProblems() {
		return problems;
	}
	
	public boolean isIgnored() {
		String value = getSeverity();
		if (value != null && value.equals(RubyCore.IGNORE))
			return true;
		return false;
	}

	protected void createProblem(ISourcePosition position, String message) {
		String value = getSeverity();
		if (value != null && value.equals(RubyCore.IGNORE))
			return;
		CategorizedProblem problem;
		if (value != null && value.equals(RubyCore.ERROR))
			problem = new Error(position, message, getProblemID());
		else
		  problem = new Warning(position, message, getProblemID());
		problems.add(problem);
	}

	protected String getSeverity() {
		return (String) fOptions.get(getOptionKey());
	}
	
	protected Object visitNode(Node iVisited) {
		return null;		
	}

	/**
	 * The key used to store the error/warning severity option.
	 * @return a String key
	 */
	abstract protected String getOptionKey();
	
	/**
	 * Meant to be overriden by classes needing to perform some action when a class definition was exited.
	 * @param iVisited
	 */
	public void exitClassNode(ClassNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a method definition.
	 * @param iVisited
	 */
	public void exitDefnNode(DefnNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitIfNode(IfNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitBlockNode(BlockNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitDefsNode(DefsNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitModuleNode(ModuleNode iVisited) {}

	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitWhenNode(WhenNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitSClassNode(SClassNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a method signature.
	 * @param iVisited
	 */
	public void exitArgsNode(ArgsNode iVisited) {}
	
	public void exitRescueBodyNode(RescueBodyNode iVisited) {}
	
	public void exitHashNode(HashNode iVisited) {}
	
	protected int getProblemID() {
		return IProblem.Uncategorized;
	}

	public Object visitAliasNode(AliasNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitAndNode(AndNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitArgsCatNode(ArgsCatNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitArgsNode(ArgsNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitArgsPushNode(ArgsPushNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitArrayNode(ArrayNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitAttrAssignNode(AttrAssignNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitBackRefNode(BackRefNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitBeginNode(BeginNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitBignumNode(BignumNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitBlockArgNode(BlockArgNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitBlockNode(BlockNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitBlockPassNode(BlockPassNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitBreakNode(BreakNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitCallNode(CallNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitCaseNode(CaseNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitClassNode(ClassNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitClassVarAsgnNode(ClassVarAsgnNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitClassVarDeclNode(ClassVarDeclNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitClassVarNode(ClassVarNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitColon2Node(Colon2Node arg0) {
		return visitNode(arg0);
		
	}

	public Object visitColon3Node(Colon3Node arg0) {
		return visitNode(arg0);
		
	}

	public Object visitConstDeclNode(ConstDeclNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitConstNode(ConstNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitDAsgnNode(DAsgnNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitDRegxNode(DRegexpNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitDStrNode(DStrNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitDSymbolNode(DSymbolNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitDVarNode(DVarNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitDXStrNode(DXStrNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitDefinedNode(DefinedNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitDefnNode(DefnNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitDefsNode(DefsNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitDotNode(DotNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitEnsureNode(EnsureNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitEvStrNode(EvStrNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitFCallNode(FCallNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitFalseNode(FalseNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitFixnumNode(FixnumNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitFlipNode(FlipNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitFloatNode(FloatNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitForNode(ForNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitGlobalAsgnNode(GlobalAsgnNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitGlobalVarNode(GlobalVarNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitHashNode(HashNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitIfNode(IfNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitInstAsgnNode(InstAsgnNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitInstVarNode(InstVarNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitIterNode(IterNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitLocalAsgnNode(LocalAsgnNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitLocalVarNode(LocalVarNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitMatch2Node(Match2Node arg0) {
		return visitNode(arg0);
		
	}

	public Object visitMatch3Node(Match3Node arg0) {
		return visitNode(arg0);
		
	}

	public Object visitMatchNode(MatchNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitModuleNode(ModuleNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitMultipleAsgnNode(MultipleAsgnNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitMultipleAsgnNode(MultipleAsgn19Node arg0) {
		return visitNode(arg0);
		
	}

	public Object visitNewlineNode(NewlineNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitNextNode(NextNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitNilNode(NilNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitNotNode(NotNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitNthRefNode(NthRefNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitOpAsgnAndNode(OpAsgnAndNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitOpAsgnNode(OpAsgnNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitOpAsgnOrNode(OpAsgnOrNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitOpElementAsgnNode(OpElementAsgnNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitOrNode(OrNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitPostExeNode(PostExeNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitPreExeNode(PreExeNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitRedoNode(RedoNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitRegexpNode(RegexpNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitRescueBodyNode(RescueBodyNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitRescueNode(RescueNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitRestArgNode(RestArgNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitRetryNode(RetryNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitReturnNode(ReturnNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitRootNode(RootNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitSClassNode(SClassNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitSValueNode(SValueNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitSelfNode(SelfNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitSplatNode(SplatNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitStrNode(StrNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitSuperNode(SuperNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitSymbolNode(SymbolNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitToAryNode(ToAryNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitTrueNode(TrueNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitUndefNode(UndefNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitUntilNode(UntilNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitVAliasNode(VAliasNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitVCallNode(VCallNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitWhenNode(WhenNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitWhileNode(WhileNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitXStrNode(XStrNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitYieldNode(YieldNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitZArrayNode(ZArrayNode arg0) {
		return visitNode(arg0);
		
	}

	public Object visitZSuperNode(ZSuperNode arg0) {
		return visitNode(arg0);
		
	}
	
}
