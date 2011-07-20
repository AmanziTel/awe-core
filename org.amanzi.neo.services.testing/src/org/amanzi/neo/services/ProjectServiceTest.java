package org.amanzi.neo.services;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class ProjectServiceTest {
	private static Logger LOGGER = Logger.getLogger(ProjectServiceTest.class);
	
	private static ProjectService projectService;
	private static GraphDatabaseService graphDb;
	private static final String prName = "Project";
	private static final String databasePath = System.getProperty("user.home")
			+ File.separator + ".amanzi" + File.separator + "test";
	private static Transaction tx;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		graphDb = new EmbeddedGraphDatabase(databasePath);
		LOGGER.info("Database created in folder " + databasePath);
		projectService = new ProjectService(graphDb);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (graphDb != null) {
			graphDb.shutdown();
			LOGGER.info("Database shut down");
		}
	}

	@Before
	public void setUp() throws Exception {
		tx = graphDb.beginTx();
		LOGGER.info("Begin database transaction...");
	}

	@After
	public void tearDown() throws Exception {
		if (tx != null) {
			tx.success();
			tx.finish();
			LOGGER.info("Finish database transaction...");
		}
	}

	@Test
	public void testCreateProject() {
		LOGGER.info("testCreateProject() started");
		// check that the node is created, it has the defined name
		Node project = projectService.createProject(prName);
		Assert.assertNotNull(project);
		Assert.assertEquals(prName,
				project.getProperty(INeoConstants.PROPERTY_NAME_NAME));
		// check that type is 'project'
		// check that it's attached to the reference node and relation type is
		// correct
		assertProjectRelatedToRefNode(project);
		// check exception
		try {
			projectService.createProject("");
			fail("Exception not thrown on empty string argument");
		} catch (IllegalNodeDataException e) {
		}
		try {
			projectService.createProject(null);
			fail("Exception not thrown on null argument");
		} catch (IllegalNodeDataException e) {
		}
		try {
			projectService.createProject(prName);
			fail("Exception not thrown when trying to create a project with the same name");
		} catch (Exception e) {
		}
		LOGGER.info("testCreateProject() finished");
	}

	@Test
	public void testFindProject() {
		LOGGER.info("testFindProject() started");
		// TODO: check that the node with defined name is found, of project type
		// and connected to ref node
		Node project = projectService.createProject(prName + "1");
		graphDb.createNode().setProperty(INeoConstants.PROPERTY_NAME_NAME,
				prName + "1");
		Node found = projectService.findProject(prName + "1");
		Assert.assertEquals(prName + "1",
				found.getProperty(INeoConstants.PROPERTY_NAME_NAME));
		Assert.assertEquals(project, found); // TODO: shall it work?
		assertProjectRelatedToRefNode(found);
		// null is returned if not found
		found = projectService.findProject(prName + "2");
		Assert.assertNull(found);
		// check exception
		try {
			projectService.findProject("");
			fail("Exception not thrown on empty string argument");
		} catch (IllegalNodeDataException e) {
		}
		try {
			projectService.findProject(null);
			fail("Exception not thrown on null argument");
		} catch (IllegalNodeDataException e) {
		}
		LOGGER.info("testFindProject() finished");
	}

	@Test
	public void testFindAllProjects() {
		LOGGER.info("testFindAllProjects() started");
		// check the number, names and type of returned nodes
		int count = 0;
		List<String> projectNames = new ArrayList<String>();
		projectNames.add(prName);
		projectNames.add(prName + "1");

		for (Node node : projectService.findAllProjects()) {
			count++;
			Assert.assertTrue(projectNames.contains(node
					.getProperty(INeoConstants.PROPERTY_NAME_NAME)));
			assertProjectRelatedToRefNode(node);
		}
		Assert.assertTrue(2 == count);
		LOGGER.info("testFindAllProjects() finished");
	}

	@Test
	public void testGetProject() {
		LOGGER.info("testGetProject() started");
		// TODO: check that the returned node is not recreated if already exists
		Node project = projectService.createProject(prName + "2");
		Node got = projectService.createProject(prName + "2");
		Assert.assertEquals(project, got);
		LOGGER.info("testGetProject() finished");
	}

	private void assertProjectRelatedToRefNode(Node node) {
		Assert.assertEquals(ProjectService.PROJECT_NODE_TYPE,
				node.getProperty(INeoConstants.PROPERTY_TYPE_NAME));
		Node refNode = graphDb.getReferenceNode();
		for (Relationship rel : node.getRelationships(
				ProjectService.RelType.PROJECT, Direction.INCOMING)) {
			Assert.assertTrue(rel.getOtherNode(node).equals(refNode));
		}
	}

}
