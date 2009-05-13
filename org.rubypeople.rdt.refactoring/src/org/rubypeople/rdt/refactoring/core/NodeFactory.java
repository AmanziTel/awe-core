/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2006 Lukas Felber <lfelber@hsr.ch>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/

package org.rubypeople.rdt.refactoring.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.jruby.ast.ArgsCatNode;
import org.jruby.ast.ArgsNode;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.ArrayNode;
import org.jruby.ast.BlockArgNode;
import org.jruby.ast.BlockNode;
import org.jruby.ast.CallNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.Colon2ConstNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.Colon3Node;
import org.jruby.ast.CommentNode;
import org.jruby.ast.ConstDeclNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DVarNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.FCallOneArgNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.ListNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.MultipleAsgnNode;
import org.jruby.ast.NewlineNode;
import org.jruby.ast.Node;
import org.jruby.ast.RestArgNode;
import org.jruby.ast.SelfNode;
import org.jruby.ast.SuperNode;
import org.jruby.ast.SymbolNode;
import org.jruby.ast.VCallNode;
import org.jruby.ast.ZSuperNode;
import org.jruby.lexer.yacc.IDESourcePosition;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.parser.LocalStaticScope;
import org.jruby.parser.StaticScope;
import org.rubypeople.rdt.refactoring.nodewrapper.ArgsNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.AttrAccessorNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.VisibilityNodeWrapper;
import org.rubypeople.rdt.refactoring.util.Constants;

public class NodeFactory {

	public static final ISourcePosition pos = new IDESourcePosition();

	public static final Node NULL_POSITION_NODE = new NewlineNode(pos, null);

	public static FCallNode createSimpleAccessorNode(String definitionName, String attrName) {
		ArrayNode argsNode = new ArrayNode(pos);
		argsNode.add(new SymbolNode(pos, attrName));
		return new FCallOneArgNode(pos, definitionName, argsNode);
	}

	public static DefsNode createStaticMethodNode(String methodName, Collection<String> args, StaticScope scopeNode, Node body) {
		return createStaticMethodNode("self", methodName, createArgsNode(args.toArray(new String[args.size()])), scopeNode == null ? new LocalStaticScope(null) : scopeNode, body); //$NON-NLS-1$
	}
	
	public static DefnNode createMethodNode(String methodName, String[] args, Node scopeContentNode) {
		ArgsNode argsNode = createArgsNode(args);
		return createMethodNode(methodName, argsNode, scopeContentNode);
	}

	public static DefnNode createMethodNode(String methodName, ArgsNode argsNode, Node body) {
		ArgumentNode methodNameNode = new ArgumentNode(pos, methodName);
		return new DefnNode(pos, methodNameNode, argsNode, new LocalStaticScope(null), body != null ? new NewlineNode(body.getPosition(), body) : null);
	}
	
	public static DefnNode createMethodNodeWithoutNewline(String methodName, ArgsNode argsNode, Node body) {
		ArgumentNode methodNameNode = new ArgumentNode(pos, methodName);
		return new DefnNode(pos, methodNameNode, argsNode, new LocalStaticScope(null), body);
	}
	
	public static DefsNode createStaticMethodNode(String className, String methodName, ArgsNodeWrapper argsNode, StaticScope scopeNode) {
		return createStaticMethodNode(className, methodName, argsNode.getWrappedNode(), scopeNode, null);
	}	
	
	public static DefsNode createStaticMethodNode(String className, String methodName, ArgsNode argsNode, StaticScope scopeNode, Node body) {
		return new DefsNode(pos, createConstNode(className), createArgumentNode(methodName), argsNode, scopeNode, body);
	}

	public static ArgsNode createArgsNode(String...args) {
		return createArgsNode(args, null, -1, null, null);
	}

	public static ArgsNode createArgsNode(Collection<String> args) {
		return createArgsNode(args.toArray(new String[args.size()]));
	}
	
	public static ArgsNode createArgsNode(String[] args, ListNode optArgs, int restArgs, RestArgNode restArgNode, BlockArgNode blockArg) {
		ListNode argumentsList = null;
		if (args.length > 0) {
			argumentsList = new ListNode(pos);
			for (String arg : args)
				argumentsList.add(new ArgumentNode(pos, arg));
		}
		ArgsNode argsNode = new ArgsNode(pos, argumentsList, optArgs, restArgNode, null, blockArg);
		return argsNode;
	}

	public static DefnNode createConstructor(BlockNode content) {
		return createMethodNode(Constants.CONSTRUCTOR_NAME, new String[] {}, content);
	}

	public static DefnNode createDefaultConstructor() {
		return createConstructor(new BlockNode(pos));
	}


	public static InstAsgnNode createInstAsgnNode(String name, Node valueNode) {
		return new InstAsgnNode(pos, name, valueNode);
	}

	public static InstVarNode createInstVarNode(String name) {
		return new InstVarNode(pos, name);
	}

	public static Node createSuperNode(Collection<String> args) {
		if (args.isEmpty()) {
			return new ZSuperNode(pos);
		}
		ArrayNode argsNode = createArrayNodeWithLocalVarNodes(args);
		return new SuperNode(pos, argsNode);
	}

	private static ArrayNode createArrayNodeWithLocalVarNodes(Collection<String> args) {
		ArrayNode arrayNode = new ArrayNode(pos);
		for (String name : args) {
			arrayNode.add(new LocalVarNode(pos, 0, name));
		}
		return arrayNode;
	}

	public static BlockNode createBlockNode() {
		return new BlockNode(pos);
	}

	public static BlockNode createBlockNode(Node...content) {
		BlockNode blockNode = new BlockNode(pos);
		for (Node node : content) {
			blockNode.add(node);
		}
		return blockNode;
	}

	public static BlockNode createBlockNode(boolean needsNewLineAtEndOfBlock, Node... contentNodes) {
		return createBlockNode(true, needsNewLineAtEndOfBlock, contentNodes);
	}

	public static BlockNode createBlockNode(boolean leadingNewLine, boolean subsequentNewline, Node... contentNodes) {
		return createBlockNode(leadingNewLine, subsequentNewline, false, contentNodes);
	}
	
	public static BlockNode createBlockNode(boolean leadingNewLine, boolean subsequentNewline, boolean newLineBetweenNodes, Node... contentNodes) {
		BlockNode blockNode = createBlockNode();
		if (leadingNewLine) {
			blockNode.add(NodeFactory.createNewLineNode(null));
		}
		for (Node aktContentNode : contentNodes) {
			if(newLineBetweenNodes){
				blockNode.add(createNewLineNode(aktContentNode));
			}
			else{
				blockNode.add(aktContentNode);
			}
		}
		if (subsequentNewline) {
			blockNode.add(NodeFactory.createNewLineNode(null));
		}
		return blockNode;
	}

	public static NewlineNode createNewLineNode(Node nextNode) {
		return new NewlineNode(pos, nextNode);
	}

	public static ListNode createListNode(Collection<? extends Node> nodes) {
		ListNode listNode = new ListNode(pos);
		for (Node aktNode : nodes) {
			listNode.add(aktNode);
		}
		return listNode;
	}

	public static ListNode createListNode() {
		return new ListNode(pos);
	}

	public static ClassNode createClassNode(String className, Node scopeNode) {
		Colon3Node classNameNode = new Colon3Node(pos, className);
		return new ClassNode(pos, classNameNode, new LocalStaticScope(null), scopeNode, null);
	}

	public static Node createCommentNode(String commentValue) {
		return new CommentNode(pos, commentValue);
	}

	public static ClassVarNode createClassVarNode(String name) {
		return new ClassVarNode(pos, name);
	}

	public static ClassVarAsgnNode createClassVarAsgnNode(String name, Node valueNode) {
		return new ClassVarAsgnNode(pos, name, valueNode);
	}

	public static Node createMethodCallNode(String name, Collection<? extends Node> arguments) {
		if (arguments == null || arguments.isEmpty()) {
			return createVCallNode(name);
		}
		
		return createFCallNode(name, arguments);

	}
	
	public static Node createMethodCallNode(String name, Node argsNode) {
		if (argsNode == null) {
			return createVCallNode(name);
		}
		
		return createFCallNode(name, argsNode);
	}

	public static FCallNode createFCallNode(String name, Collection<? extends Node> arguments) {
		return new FCallNode(pos, name, createArrayNode(arguments));
	}
	
	public static FCallNode createFCallNode(String name, Node argumentNode){
		return new FCallNode(pos, name, argumentNode);
	}

	public static ArrayNode createArrayNode(Collection<? extends Node> nodes) {
		ArrayNode arrNode = new ArrayNode(pos);
		for (Node aktNode : nodes) {
			arrNode.add(aktNode);
		}
		return arrNode;
	}

	public static Node createVCallNode(String name) {
		return new VCallNode(pos, name);
	}

	public static LocalAsgnNode createLocalAsgnNode(String name, int id, Node valueNode) {
		return new LocalAsgnNode(pos, name, id, valueNode);
	}

	public static MultipleAsgnNode createMultipleAsgnNode(Collection<? extends Node> headNodes, Node valueNode) {
		ListNode listNode = createListNode(headNodes);
		MultipleAsgnNode multipleAsgnNode = new MultipleAsgnNode(pos, listNode, null);
		multipleAsgnNode.setValueNode(valueNode);
		return multipleAsgnNode;
	}

	public static SymbolNode createSymboleNode(String symbolName) {
		return new SymbolNode(pos, symbolName);
	}

	public static DVarNode createDVarNode(String name) {
		return new DVarNode(pos, 0, name);
	}

	public static ISourcePosition unionPositions(ISourcePosition first, ISourcePosition second) {
		 String fileName = first.getFile();
        int startOffset = first.getStartOffset();
        int endOffset = first.getEndOffset();
        int startLine = first.getStartLine();
        int endLine = first.getEndLine();
        

        if(startOffset > second.getStartOffset()){
            startOffset = second.getStartOffset();
            startLine = second.getStartLine();
        }
        
        if(endOffset < second.getEndOffset()){
            endOffset = second.getEndOffset();
            endLine = second.getEndLine();
        }

        return new IDESourcePosition(fileName, startLine, endLine, startOffset, endOffset);
	}

	public static BlockNode createGetterSetter(String attrName, boolean isWriterMethod, VisibilityNodeWrapper.METHOD_VISIBILITY visibility){
		return createGetterSetter(attrName, isWriterMethod, visibility, new ArrayList<CommentNode>());
	}
	
	public static BlockNode createGetterSetter(String attrName, boolean isWriterMethod, VisibilityNodeWrapper.METHOD_VISIBILITY visibility, Collection<CommentNode> comments) {
		String methodName = attrName + ((isWriterMethod) ? "=" : ""); //$NON-NLS-1$ //$NON-NLS-2$
		String[] args = (isWriterMethod) ? new String[] { attrName } : new String[] {};
		DefnNode methodNode = createGetterSetterNode(isWriterMethod, methodName, attrName, args);
		methodNode.addComments(comments);
		BlockNode block = createBlockNode();
		block.add(createNewLineNode(methodNode));
		if (!visibility.equals(VisibilityNodeWrapper.METHOD_VISIBILITY.NONE))
			block.add(createVisibilityNode(visibility, methodName));
		return block;
	}

	public static Node createVisibilityNode(VisibilityNodeWrapper.METHOD_VISIBILITY visibility, String... methodNames) {
		Collection<Node> arguments = new ArrayList<Node>();
		for(String methodName : methodNames) {
			SymbolNode symbolNode = NodeFactory.createSymboleNode(methodName);
			arguments.add(symbolNode);
		}
		FCallNode fCallNode = NodeFactory.createFCallNode(visibility.name().toLowerCase(Locale.ENGLISH), arguments);
		return NodeFactory.createNewLineNode(fCallNode);
	}

	private static DefnNode createGetterSetterNode(boolean isWriterMethod, String methodName, String attrName, String[] args) {
		Node bodyContent;
		if (isWriterMethod)
			bodyContent = NodeFactory.createInstAsgnNode('@' + attrName, NodeFactory.createLocalVarNode(attrName));
		else
			bodyContent = NodeFactory.createInstVarNode('@' + attrName);

		DefnNode methodNode = NodeFactory.createMethodNode(methodName, args, bodyContent);
		return methodNode;
	}

	public static CallNode createCallNode(Node receiverNode,String name,Node argsNode) {
		return new CallNode(pos, receiverNode, name, argsNode);
	}

	public static ArgumentNode createArgumentNode(String name) {
		return new ArgumentNode(pos, name);
	}
	
	public static FCallNode createAccessorNode(AttrAccessorNodeWrapper accessor) {
		ArrayNode argsNode = new ArrayNode(pos);
		argsNode.add(new SymbolNode(pos, accessor.getAttrName()));
		return new FCallOneArgNode(pos, accessor.getAccessorTypeName(), argsNode);
	}

	public static ConstNode createConstNode(String name) {
		return new ConstNode(pos, name);
	}

	public static ArgsCatNode createArgsCatNode(Node firstNode, Node secondNode) {
		return new ArgsCatNode(pos, firstNode, secondNode);
	}

	public static SelfNode createSelfNode() {
		return new SelfNode(pos);
	}

	public static ArrayNode createArrayNode() {
		return new ArrayNode(pos);
	}

	public static Node createLocalVarNode(String argName) {
		return new LocalVarNode(pos, 0, argName);
	}

	public static Node createConstDeclNode(String name, Node valueNode) {
		return new ConstDeclNode(pos, name, null, valueNode);
	}
}
