package org.rubypeople.rdt.core.parser.warnings;

import java.util.ArrayList;
import java.util.List;

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
import org.jruby.ast.MultipleAsgnNode;
import org.jruby.ast.NewlineNode;
import org.jruby.ast.NextNode;
import org.jruby.ast.NilNode;
import org.jruby.ast.NotNode;
import org.jruby.ast.NthRefNode;
import org.jruby.ast.OpAsgnAndNode;
import org.jruby.ast.OpAsgnNode;
import org.jruby.ast.OpAsgnOrNode;
import org.jruby.ast.OpElementAsgnNode;
import org.jruby.ast.OrNode;
import org.jruby.ast.PostExeNode;
import org.jruby.ast.RedoNode;
import org.jruby.ast.RegexpNode;
import org.jruby.ast.RescueBodyNode;
import org.jruby.ast.RescueNode;
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
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.internal.core.parser.InOrderVisitor;
import org.rubypeople.rdt.internal.core.parser.warnings.ConstantReassignmentVisitor;
import org.rubypeople.rdt.internal.core.parser.warnings.CoreClassReOpening;
import org.rubypeople.rdt.internal.core.parser.warnings.EmptyStatementVisitor;
import org.rubypeople.rdt.internal.core.parser.warnings.Ruby19HashCommaSyntax;
import org.rubypeople.rdt.internal.core.parser.warnings.Ruby19WhenStatements;

/**
 * <p>DelegatingVisitor takes a list of visitors, traverse the AST in order, and at
 * each node calls the correct visitXXXNode method on every visitor. This allows
 * us to traverse the AST only once while having X number of visitors operate on
 * it.</p>
 * 
 * <p>Right now it is customized to RubyLintVisitors, which is the abstract
 * base class for all visitors that do coce analysis for Error/Warning markers.</p>
 * 
 * @author Christopher Williams
 * 
 */
public class DelegatingVisitor extends InOrderVisitor {

	private List<RubyLintVisitor> visitors;
	
	public static List<RubyLintVisitor> createVisitors(IRubyScript script, String contents) {
		List<RubyLintVisitor> visitors = new ArrayList<RubyLintVisitor>();
		visitors.add(new EmptyStatementVisitor(contents));
		visitors.add(new ConstantReassignmentVisitor(contents));
		if (script != null) {
			visitors.add(new CoreClassReOpening(script, contents));
		}
		visitors.add(new Ruby19WhenStatements(contents));
		visitors.add(new Ruby19HashCommaSyntax(contents));
		List<RubyLintVisitor> filtered = new ArrayList<RubyLintVisitor>();
		for (RubyLintVisitor visitor : visitors) {
			if (visitor.isIgnored()) continue;
			filtered.add(visitor);
		}
		return filtered;
	}
	
	public List<CategorizedProblem> getProblems() {
		List<CategorizedProblem> problems = new ArrayList<CategorizedProblem>();
		for (RubyLintVisitor visitor : visitors) {
			problems.addAll(visitor.getProblems());
		}
		return problems;
	}

	public DelegatingVisitor(List<RubyLintVisitor> visitors) {
		this.visitors = visitors;
	}

	@Override
	public Object visitAliasNode(AliasNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitAliasNode(iVisited);
		}
		return super.visitAliasNode(iVisited);
	}
	
	@Override
	public Object visitAndNode(AndNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitAndNode(iVisited);
		}
		return super.visitAndNode(iVisited);
	}

	@Override
	public Object visitArgsCatNode(ArgsCatNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitArgsCatNode(iVisited);
		}
		return super.visitArgsCatNode(iVisited);
	}

	@Override
	public Object visitArgsNode(ArgsNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitArgsNode(iVisited);
		}
		Object ins = super.visitArgsNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitArgsNode(iVisited);
		}
		return ins;
	}

	@Override
	public Object visitArgsPushNode(ArgsPushNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitArgsPushNode(iVisited);
		}
		return super.visitArgsPushNode(iVisited);
	}

	@Override
	public Object visitArrayNode(ArrayNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitArrayNode(iVisited);
		}
		return super.visitArrayNode(iVisited);
	}

	@Override
	public Object visitAttrAssignNode(AttrAssignNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitAttrAssignNode(iVisited);
		}
		return super.visitAttrAssignNode(iVisited);
	}

	@Override
	public Object visitBackRefNode(BackRefNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitBackRefNode(iVisited);
		}
		return super.visitBackRefNode(iVisited);
	}

	@Override
	public Object visitBeginNode(BeginNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitBeginNode(iVisited);
		}
		return super.visitBeginNode(iVisited);
	}

	@Override
	public Object visitBignumNode(BignumNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitBignumNode(iVisited);
		}
		return super.visitBignumNode(iVisited);
	}

	@Override
	public Object visitBlockArgNode(BlockArgNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitBlockArgNode(iVisited);
		}
		return super.visitBlockArgNode(iVisited);
	}

	@Override
	public Object visitBlockNode(BlockNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitBlockNode(iVisited);
		}
		Object ins = super.visitBlockNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitBlockNode(iVisited);
		}
		return ins;
	}

	@Override
	public Object visitBlockPassNode(BlockPassNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitBlockPassNode(iVisited);
		}
		return super.visitBlockPassNode(iVisited);
	}

	@Override
	public Object visitBreakNode(BreakNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitBreakNode(iVisited);
		}
		return super.visitBreakNode(iVisited);
	}

	@Override
	public Object visitCallNode(CallNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitCallNode(iVisited);
		}
		return super.visitCallNode(iVisited);
	}

	@Override
	public Object visitCaseNode(CaseNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitCaseNode(iVisited);
		}
		return super.visitCaseNode(iVisited);
	}

	@Override
	public Object visitClassNode(ClassNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitClassNode(iVisited);
		}
		Object ins = super.visitClassNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitClassNode(iVisited);
		}
		return ins;
	}

	@Override
	public Object visitClassVarAsgnNode(ClassVarAsgnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitClassVarAsgnNode(iVisited);
		}
		return super.visitClassVarAsgnNode(iVisited);
	}

	@Override
	public Object visitClassVarDeclNode(ClassVarDeclNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitClassVarDeclNode(iVisited);
		}
		return super.visitClassVarDeclNode(iVisited);
	}

	@Override
	public Object visitClassVarNode(ClassVarNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitClassVarNode(iVisited);
		}
		return super.visitClassVarNode(iVisited);
	}

	@Override
	public Object visitColon2Node(Colon2Node iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitColon2Node(iVisited);
		}
		return super.visitColon2Node(iVisited);
	}

	@Override
	public Object visitColon3Node(Colon3Node iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitColon3Node(iVisited);
		}
		return super.visitColon3Node(iVisited);
	}

	@Override
	public Object visitConstDeclNode(ConstDeclNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitConstDeclNode(iVisited);
		}
		return super.visitConstDeclNode(iVisited);
	}

	@Override
	public Object visitConstNode(ConstNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitConstNode(iVisited);
		}
		return super.visitConstNode(iVisited);
	}

	@Override
	public Object visitDAsgnNode(DAsgnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDAsgnNode(iVisited);
		}
		return super.visitDAsgnNode(iVisited);
	}

	@Override
	public Object visitDefinedNode(DefinedNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDefinedNode(iVisited);
		}
		return super.visitDefinedNode(iVisited);
	}

	@Override
	public Object visitDefnNode(DefnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDefnNode(iVisited);
		}
		Object ins = super.visitDefnNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitDefnNode(iVisited);
		}
		return ins;
	}

	@Override
	public Object visitDefsNode(DefsNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDefsNode(iVisited);
		}
		Object ins = super.visitDefsNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitDefsNode(iVisited);
		}
		return ins;
	}


	@Override
	public Object visitDotNode(DotNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDotNode(iVisited);
		}
		return super.visitDotNode(iVisited);
	}

	@Override
	public Object visitDRegxNode(DRegexpNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDRegxNode(iVisited);
		}
		return super.visitDRegxNode(iVisited);
	}

	@Override
	public Object visitDStrNode(DStrNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDStrNode(iVisited);
		}
		return super.visitDStrNode(iVisited);
	}

	@Override
	public Object visitDSymbolNode(DSymbolNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDSymbolNode(iVisited);
		}
		return super.visitDSymbolNode(iVisited);
	}

	@Override
	public Object visitDVarNode(DVarNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDVarNode(iVisited);
		}
		return super.visitDVarNode(iVisited);
	}

	@Override
	public Object visitDXStrNode(DXStrNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitDXStrNode(iVisited);
		}
		return super.visitDXStrNode(iVisited);
	}

	@Override
	public Object visitEnsureNode(EnsureNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitEnsureNode(iVisited);
		}
		return super.visitEnsureNode(iVisited);
	}

	@Override
	public Object visitEvStrNode(EvStrNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitEvStrNode(iVisited);
		}
		return super.visitEvStrNode(iVisited);
	}

	@Override
	public Object visitFalseNode(FalseNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitFalseNode(iVisited);
		}
		return super.visitFalseNode(iVisited);
	}

	@Override
	public Object visitFCallNode(FCallNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitFCallNode(iVisited);
		}
		return super.visitFCallNode(iVisited);
	}

	@Override
	public Object visitFixnumNode(FixnumNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitFixnumNode(iVisited);
		}
		return super.visitFixnumNode(iVisited);
	}

	@Override
	public Object visitFlipNode(FlipNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitFlipNode(iVisited);
		}
		return super.visitFlipNode(iVisited);
	}

	@Override
	public Object visitFloatNode(FloatNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitFloatNode(iVisited);
		}
		return super.visitFloatNode(iVisited);
	}

	@Override
	public Object visitForNode(ForNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitForNode(iVisited);
		}
		return super.visitForNode(iVisited);
	}

	@Override
	public Object visitGlobalAsgnNode(GlobalAsgnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitGlobalAsgnNode(iVisited);
		}
		return super.visitGlobalAsgnNode(iVisited);
	}

	@Override
	public Object visitGlobalVarNode(GlobalVarNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitGlobalVarNode(iVisited);
		}
		return super.visitGlobalVarNode(iVisited);
	}

	@Override
	public Object visitHashNode(HashNode iVisited) {	
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitHashNode(iVisited);
		}
		Object ins = super.visitHashNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitHashNode(iVisited);
		}
		return ins;
	}

	@Override
	public Object visitIfNode(IfNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitIfNode(iVisited);
		}
		Object ins = super.visitIfNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitIfNode(iVisited);
		}
		return ins;
	}

	@Override
	public Object visitInstAsgnNode(InstAsgnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitInstAsgnNode(iVisited);
		}
		return super.visitInstAsgnNode(iVisited);
	}

	@Override
	public Object visitInstVarNode(InstVarNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitInstVarNode(iVisited);
		}
		return super.visitInstVarNode(iVisited);
	}

	@Override
	public Object visitIterNode(IterNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitIterNode(iVisited);
		}
		return super.visitIterNode(iVisited);
	}

	@Override
	public Object visitLocalAsgnNode(LocalAsgnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitLocalAsgnNode(iVisited);
		}
		return super.visitLocalAsgnNode(iVisited);
	}

	@Override
	public Object visitLocalVarNode(LocalVarNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitLocalVarNode(iVisited);
		}
		return super.visitLocalVarNode(iVisited);
	}

	@Override
	public Object visitMatch2Node(Match2Node iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitMatch2Node(iVisited);
		}
		return super.visitMatch2Node(iVisited);
	}

	@Override
	public Object visitMatch3Node(Match3Node iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitMatch3Node(iVisited);
		}
		return super.visitMatch3Node(iVisited);
	}

	@Override
	public Object visitMatchNode(MatchNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitMatchNode(iVisited);
		}
		return super.visitMatchNode(iVisited);
	}

	@Override
	public Object visitModuleNode(ModuleNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitModuleNode(iVisited);
		}
		Object ins = super.visitModuleNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitModuleNode(iVisited);
		}
		return ins;
	}

	@Override
	public Object visitMultipleAsgnNode(MultipleAsgnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitMultipleAsgnNode(iVisited);
		}
		return super.visitMultipleAsgnNode(iVisited);
	}

	@Override
	public Object visitNewlineNode(NewlineNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitNewlineNode(iVisited);
		}
		return super.visitNewlineNode(iVisited);
	}

	@Override
	public Object visitNextNode(NextNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitNextNode(iVisited);
		}
		return super.visitNextNode(iVisited);
	}

	@Override
	public Object visitNilNode(NilNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitNilNode(iVisited);
		}
		return super.visitNilNode(iVisited);
	}

	@Override
	public Object visitNotNode(NotNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitNotNode(iVisited);
		}
		return super.visitNotNode(iVisited);
	}

	@Override
	public Object visitNthRefNode(NthRefNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitNthRefNode(iVisited);
		}
		return super.visitNthRefNode(iVisited);
	}

	@Override
	public Object visitOpAsgnAndNode(OpAsgnAndNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitOpAsgnAndNode(iVisited);
		}
		return super.visitOpAsgnAndNode(iVisited);
	}

	@Override
	public Object visitOpAsgnNode(OpAsgnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitOpAsgnNode(iVisited);
		}
		return super.visitOpAsgnNode(iVisited);
	}

	@Override
	public Object visitOpAsgnOrNode(OpAsgnOrNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitOpAsgnOrNode(iVisited);
		}
		return super.visitOpAsgnOrNode(iVisited);
	}

	@Override
	public Object visitOpElementAsgnNode(OpElementAsgnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitOpElementAsgnNode(iVisited);
		}
		return super.visitOpElementAsgnNode(iVisited);
	}

	@Override
	public Object visitOrNode(OrNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitOrNode(iVisited);
		}
		return super.visitOrNode(iVisited);
	}

	@Override
	public Object visitPostExeNode(PostExeNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitPostExeNode(iVisited);
		}
		return super.visitPostExeNode(iVisited);
	}

	@Override
	public Object visitRedoNode(RedoNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitRedoNode(iVisited);
		}
		return super.visitRedoNode(iVisited);
	}

	@Override
	public Object visitRegexpNode(RegexpNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitRegexpNode(iVisited);
		}
		return super.visitRegexpNode(iVisited);
	}

	@Override
	public Object visitRescueBodyNode(RescueBodyNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitRescueBodyNode(iVisited);
		}
		Object ins = super.visitRescueBodyNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitRescueBodyNode(iVisited);
		}
		return ins;
	}

	@Override
	public Object visitRescueNode(RescueNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitRescueNode(iVisited);
		}
		return super.visitRescueNode(iVisited);
	}

	@Override
	public Object visitRetryNode(RetryNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitRetryNode(iVisited);
		}
		return super.visitRetryNode(iVisited);
	}

	@Override
	public Object visitReturnNode(ReturnNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitReturnNode(iVisited);
		}
		return super.visitReturnNode(iVisited);
	}

	@Override
	public Object visitRootNode(RootNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitRootNode(iVisited);
		}
		return super.visitRootNode(iVisited);
	}

	@Override
	public Object visitSClassNode(SClassNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitSClassNode(iVisited);
		}
		Object ins = super.visitSClassNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitSClassNode(iVisited);
		}
		return ins;
	}

	@Override
	public Object visitSelfNode(SelfNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitSelfNode(iVisited);
		}
		return super.visitSelfNode(iVisited);
	}

	@Override
	public Object visitSplatNode(SplatNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitSplatNode(iVisited);
		}
		return super.visitSplatNode(iVisited);
	}

	@Override
	public Object visitStrNode(StrNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitStrNode(iVisited);
		}
		return super.visitStrNode(iVisited);
	}

	@Override
	public Object visitSuperNode(SuperNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitSuperNode(iVisited);
		}
		return super.visitSuperNode(iVisited);
	}

	@Override
	public Object visitSValueNode(SValueNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitSValueNode(iVisited);
		}
		return super.visitSValueNode(iVisited);
	}

	@Override
	public Object visitSymbolNode(SymbolNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitSymbolNode(iVisited);
		}
		return super.visitSymbolNode(iVisited);
	}

	@Override
	public Object visitToAryNode(ToAryNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitToAryNode(iVisited);
		}
		return super.visitToAryNode(iVisited);
	}

	@Override
	public Object visitTrueNode(TrueNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitTrueNode(iVisited);
		}
		return super.visitTrueNode(iVisited);
	}

	@Override
	public Object visitUndefNode(UndefNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitUndefNode(iVisited);
		}
		return super.visitUndefNode(iVisited);
	}

	@Override
	public Object visitUntilNode(UntilNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitUntilNode(iVisited);
		}
		return super.visitUntilNode(iVisited);
	}

	@Override
	public Object visitVAliasNode(VAliasNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitVAliasNode(iVisited);
		}
		return super.visitVAliasNode(iVisited);
	}

	@Override
	public Object visitVCallNode(VCallNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitVCallNode(iVisited);
		}
		return super.visitVCallNode(iVisited);
	}

	@Override
	public Object visitWhenNode(WhenNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitWhenNode(iVisited);
		}
		Object ins = super.visitWhenNode(iVisited);
		for (RubyLintVisitor visitor : visitors) {
			visitor.exitWhenNode(iVisited);
		}
		return ins;
	}

	@Override
	public Object visitWhileNode(WhileNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitWhileNode(iVisited);
		}
		return super.visitWhileNode(iVisited);
	}

	@Override
	public Object visitXStrNode(XStrNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitXStrNode(iVisited);
		}
		return super.visitXStrNode(iVisited);
	}

	@Override
	public Object visitYieldNode(YieldNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitYieldNode(iVisited);
		}
		return super.visitYieldNode(iVisited);
	}

	@Override
	public Object visitZArrayNode(ZArrayNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitZArrayNode(iVisited);
		}
		return super.visitZArrayNode(iVisited);
	}

	@Override
	public Object visitZSuperNode(ZSuperNode iVisited) {
		for (RubyLintVisitor visitor : visitors) {
			visitor.visitZSuperNode(iVisited);
		}
		return super.visitZSuperNode(iVisited);
	}
}