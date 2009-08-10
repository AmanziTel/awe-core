package org.amanzi.neo.core.database.services;

import java.util.Iterator;

import org.amanzi.neo.core.database.nodes.AweProjectNode;
import org.amanzi.neo.core.database.nodes.RootNode;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Transaction;

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
}
