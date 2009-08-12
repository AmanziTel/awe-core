package org.amanzi.neo.core.database.services;

import java.util.Iterator;
import java.util.LinkedList;

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.exception.SplashDatabaseException;
import org.amanzi.neo.core.database.exception.SplashDatabaseExceptionMessages;
import org.amanzi.neo.core.database.nodes.AbstractNode;
import org.amanzi.neo.core.database.nodes.AweProjectNode;
import org.amanzi.neo.core.database.nodes.CellNode;
import org.amanzi.neo.core.database.nodes.RootNode;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.nodes.RubyScriptNode;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.Traverser.Order;

/**
 * Service class for working with Neo4j-Spreadsheet
 * 
 * @author Tsinkel_A
 */

public class AweProjectService {

	/*
	 * NeoService Provider
	 */
	private NeoServiceProvider provider;

	/*
	 * NeoService
	 */
	protected NeoService neoService;

	/**
	 * Constructor of Service.
	 * 
	 * Initializes NeoService and create a Root Element
	 */
	public AweProjectService() {
		provider = NeoServiceProvider.getProvider();
		neoService = provider.getService();
	}

	/**
	 * Returns RootNode for projects
	 * 
	 * @return root node
	 */
	public RootNode getRootNode() {
		Transaction tx = neoService.beginTx();
		try {

			RootNode root = new RootNode(neoService.getReferenceNode());
			tx.success();
			return root;
		} finally {
			tx.finish();
		}
	}

	/**
	 * Find ruby project
	 * 
	 * @param rubyProjectName
	 *            ruby project name
	 * @return RubyProjectNode
	 */
	public RubyProjectNode findRubyProject(String rubyProjectName) {
		RootNode root = getRootNode();
		Transaction tx = neoService.beginTx();
		try {
			Iterator<AweProjectNode> iterator = root.getAllProjects();
			while (iterator.hasNext()) {
				AweProjectNode project = iterator.next();
				Iterator<RubyProjectNode> itrRubyProject = project
						.getAllProjects();
				while (itrRubyProject.hasNext()) {
					RubyProjectNode rubyProject = itrRubyProject.next();
					if (rubyProjectName.equals(rubyProject.getName())) {
						tx.success();
						return rubyProject;
					}
				}
			}
			tx.success();
			return null;

		} finally {
			tx.finish();
		}
	}

	/**
	 * Finds or Creates a Spreadsheet
	 * 
	 * @param aweProjectName
	 *            awe project name
	 * @param rubyProjectName
	 *            ruby project name
	 * @param spreadsheetName
	 *            spreadsheet name
	 * @return Spreadsheet
	 */
	public SpreadsheetNode findOrCreateSpreadsheet(String aweProjectName,
			String rubyProjectName, String spreadsheetName) {
		assert aweProjectName != null;
		assert rubyProjectName != null;
		assert spreadsheetName != null;
		AweProjectNode project = findOrCreateAweProject(aweProjectName);
		RubyProjectNode rubyProject = findOrCreateRubyProject(project,
				rubyProjectName);
		return findOrCreateSpreadSheet(rubyProject, spreadsheetName);
	}

	/**
	 * Finds or Creates a Spreadsheet
	 * 
	 * @param rubyProject
	 *            ruby project node
	 * @param spreadsheetName
	 *            spreadsheet name
	 * @return Spreadsheet
	 */
	public SpreadsheetNode findOrCreateSpreadSheet(RubyProjectNode rubyProject,
			String spreadsheetName) {
		SpreadsheetNode result = null;

		Transaction tx = neoService.beginTx();

		try {
			Iterator<SpreadsheetNode> spreadsheetIterator = rubyProject
					.getSpreadsheets();

			while (spreadsheetIterator.hasNext()) {
				SpreadsheetNode spreadsheet = spreadsheetIterator.next();
				if (spreadsheet.getSpreadsheetName().equals(spreadsheetName)) {
					result = spreadsheet;
					break;
				}
			}
			if (result == null) {
				result = new SpreadsheetNode(neoService.createNode());
				result.setSpreadsheetName(spreadsheetName);
				rubyProject.addSpreadsheet(result);
			}
			tx.success();
			return result;
		} finally {
			tx.finish();
		}
	}

	/**
	 * Find or create a RubyProject
	 * 
	 * @param project
	 *            awe project node awe project node
	 * @param rubyProjectName
	 *            ruby project name ruby project name
	 * @return RubyProjectNode
	 */
	public RubyProjectNode findOrCreateRubyProject(AweProjectNode project,
			String rubyProjectName) {
		assert project != null;
		assert rubyProjectName != null;
		RubyProjectNode result = null;
		Transaction tx = neoService.beginTx();
		try {
			Iterator<RubyProjectNode> rubyProjects = project.getAllProjects();
			while (rubyProjects.hasNext()) {
				RubyProjectNode rubyProject = rubyProjects.next();

				if (rubyProjectName.equals(rubyProject.getName())) {
					result = rubyProject;
					break;
				}
			}
			if (result == null) {
				result = new RubyProjectNode(neoService.createNode());
				result.setName(rubyProjectName);
				project.addRubyProject(result);
			}
			tx.success();
			return result;
		} finally {
			tx.finish();
		}
	}

	/**
	 * Find or create Awe Project
	 * 
	 * @param aweProjectName
	 *            Awe project name
	 * @return AweProjectNode
	 */
	public AweProjectNode findOrCreateAweProject(String aweProjectName) {
		assert aweProjectName != null;
		AweProjectNode result = null;
		RootNode root = getRootNode();
		Transaction tx = neoService.beginTx();
		try {
			Iterator<AweProjectNode> aweProjects = root.getAllProjects();
			while (aweProjects.hasNext()) {
				AweProjectNode aweProject = aweProjects.next();

				if (aweProjectName.equals(aweProject.getName())) {
					result = aweProject;
					break;
				}
			}
			if (result == null) {
				result = new AweProjectNode(neoService.createNode());
				result.setName(aweProjectName);
				root.addProject(result);
			}
			tx.success();
			return result;
		} finally {
			tx.finish();
		}
	}

	/**
	 * Delete Node and all depends nodes from bd
	 * 
	 * @param node
	 *            node to delete
	 */
	public void deleteNode(AbstractNode node) {
		Transaction tx = neoService.beginTx();
		try {
			LinkedList<Node> nodeToDelete = new LinkedList<Node>();
			nodeToDelete.add(node.getUnderlyingNode());
			for (int i = 0; i < nodeToDelete.size(); i++) {
				Node deleteNode = nodeToDelete.get(i);
				Iterator<Relationship> relations = deleteNode.getRelationships(
						Direction.BOTH).iterator();
				while (relations.hasNext()) {
					Relationship relationship = relations.next();
					if (relationship.getStartNode().equals(deleteNode)) {
						nodeToDelete.addLast(relationship.getEndNode());
					}
					relationship.delete();
				}
				deleteNode.delete();
			}
			tx.success();
		} finally {
			tx.finish();
		}

	}

	/**
	 * Find script node
	 * 
	 * @param rubyProject
	 *            RubyProjectNode
	 * @param scriptName
	 *            script name
	 * @return RubyScriptNode or null
	 */
	public RubyScriptNode findScript(RubyProjectNode rubyProject,
			String scriptName) {
		Transaction tx = neoService.beginTx();
		try {
			Iterator<RubyScriptNode> scripts = rubyProject.getScripts();
			while (scripts.hasNext()) {
				RubyScriptNode rubyScriptNode = (RubyScriptNode) scripts.next();
				if (scriptName.equals(rubyScriptNode.getName())) {
					tx.success();
					return rubyScriptNode;
				}
			}
			tx.success();
			return null;
		} finally {
			tx.finish();
		}

	}

	/**
	 * Create script node
	 * 
	 * @param cellNode
	 *            cell node
	 * @param scriptName
	 *            script name
	 * @return created script node
	 */
	public RubyScriptNode createScript(CellNode cellNode, String scriptName) {

		RubyProjectNode rubyProject = getRubyProject(cellNode);
		RubyScriptNode result = findScript(rubyProject, scriptName);
		if (result != null) {
			String message = SplashDatabaseExceptionMessages
					.getFormattedString(
							SplashDatabaseExceptionMessages.Service_Method_Exception,
							"createScript");
			NeoCorePlugin.error(message, new SplashDatabaseException(message));
			return null;
		}
		Transaction tx = neoService.beginTx();
		try {
			result = new RubyScriptNode(neoService.createNode());
			result.setName(scriptName);
			rubyProject.addScript(result);
			result.addCell(cellNode);
			tx.success();
			return result;
		} finally {
			tx.finish();
		}

	}

	/**
	 * Get RubyProject node from cell node
	 * 
	 * @param cellNode
	 *            cell node
	 * @return RubyProjectNode
	 */
	private RubyProjectNode getRubyProject(CellNode cellNode) {
		Node spreadSheetNode = getSpreadSheet(cellNode);
		Iterator<Node> iterator = spreadSheetNode.traverse(Order.BREADTH_FIRST,
				StopEvaluator.DEPTH_ONE,
				ReturnableEvaluator.ALL_BUT_START_NODE,
				SplashRelationshipTypes.SPREADSHEET, Direction.INCOMING)
				.iterator();
		Node rubyProjectNode = iterator.next();
		return new RubyProjectNode(rubyProjectNode);
	}

	/**
	 * Get SpreadSheet from cell node
	 * 
	 * @param cellNode
	 *            cell node
	 * @return wrapper spreadsheet node
	 */
	public SpreadsheetNode getSpreadsheetByCell(CellNode cellNode) {
		Transaction tx = neoService.beginTx();
		try {
			Node result = getSpreadSheet(cellNode);
			tx.success();
			return result == null ? null : new SpreadsheetNode(result);
		} finally {
			tx.finish();
		}
	}

	/**
	 * Get SpreadSheet from cell node
	 * 
	 * @param cellNode
	 *            cell node
	 * @return SpreadSheet node
	 */
	private Node getSpreadSheet(CellNode cellNode) {
		Node rowNode = cellNode.getRow().getUnderlyingNode();
		Node spreadSheetNode = rowNode.traverse(Order.BREADTH_FIRST,
				StopEvaluator.DEPTH_ONE,
				ReturnableEvaluator.ALL_BUT_START_NODE,
				SplashRelationshipTypes.ROW, Direction.INCOMING).iterator()
				.next();
		return spreadSheetNode;
	}

	/**
	 * Find Cell depends from script
	 * 
	 * @param script
	 *            script node
	 * @return CellNode or null
	 */
	public CellNode findCellByScriptReference(RubyScriptNode script) {
		Transaction tx = neoService.beginTx();
		try {
			Iterator<Node> cellIterator = script.getUnderlyingNode().traverse(
					Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
					ReturnableEvaluator.ALL_BUT_START_NODE,
					SplashRelationshipTypes.SCRIPT_CELL, Direction.OUTGOING)
					.iterator();
			CellNode result = null;
			if (cellIterator.hasNext()) {
				result = new CellNode(cellIterator.next());
			}
			tx.success();
			return result;
		} finally {
			tx.finish();
		}
	}
}
