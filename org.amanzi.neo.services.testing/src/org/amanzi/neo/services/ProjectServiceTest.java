package org.amanzi.neo.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
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
	private static final String databasePath = "z:\\test";
	// System.getProperty("user.home") + File.separator + ".amanzi" +
	// File.separator + "test";
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

	@Test
	public void testCreateProject() {
		LOGGER.info("testCreateProject() started");
		// check that the node is created, it has the defined name
		Node project = projectService.createProject(prName);
		Assert.assertNotNull(project);
		Assert.assertEquals(prName,
				project.getProperty(INeoConstants.PROPERTY_NAME_NAME, null));
		// check that type is 'project'
		// check that it's attached to the reference node and relation type is
		// correct
		assertProjectRelatedToRefNode(project);
		LOGGER.info("testCreateProject() finished");
	}

	@Test(expected = IllegalNodeDataException.class)
	public void testCreateProjectEmptyName() {
		LOGGER.info("testCreateProjectEmptyName() started");
		projectService.createProject("");
		LOGGER.info("testCreateProjectEmptyName() finished");
	}

	@Test(expected = IllegalNodeDataException.class)
	public void testCreateProjectNullName() {
		LOGGER.info("testCreateProjectNullName() started");
		projectService.createProject(null);
		LOGGER.info("testCreateProjectNullName() finished");
	}

	@Test(expected = IllegalDBOperationException.class)
	public void testCreateProjectExisting() {
		LOGGER.info("testCreateProjectExisting() started");
		projectService.createProject(prName);
		LOGGER.info("testCreateProjectExisting() finished");
	}

	@Test
	public void testFindProject() {
		LOGGER.info("testFindProject() started");
		// check that the node with defined name is found, of project type
		// and connected to ref node
		Node project = projectService.createProject(prName + "1");

		// create a node of another type with the same name
		tx = graphDb.beginTx();
		graphDb.createNode().setProperty(INeoConstants.PROPERTY_NAME_NAME,
				prName + "1");
		tx.success();
		tx.finish();

		Node found = projectService.findProject(prName + "1");
		Assert.assertEquals(prName + "1",
				found.getProperty(INeoConstants.PROPERTY_NAME_NAME, null));
		Assert.assertEquals(project, found); // TODO: shall it work?
		assertProjectRelatedToRefNode(found);
		LOGGER.info("testFindProject() finished");
	}

	@Test
	public void testFindProjectNotFound() {
		LOGGER.info("testFindProjectNotFound() started");
		// null is returned if not found
		Node found = projectService.findProject(prName + "2");
		Assert.assertNull(found);
		LOGGER.info("testFindProjectNotFound() finished");
	}

	@Test(expected = IllegalNodeDataException.class)
	public void testFindProjectEmptyName() {
		LOGGER.info("testFindProjectEmptyName() started");
		projectService.findProject("");
		LOGGER.info("testFindProjectEmptyName() finished");
	}

	@Test(expected = IllegalNodeDataException.class)
	public void testFindProjectNullName() {
		LOGGER.info("testFindProjectNullName() started");
		projectService.findProject(null);
		LOGGER.info("testFindProjectNullName() finished");
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
			Assert.assertTrue(projectNames.contains(node.getProperty(
					INeoConstants.PROPERTY_NAME_NAME, null)));
			assertProjectRelatedToRefNode(node);
		}
		Assert.assertTrue(2 == count);
		LOGGER.info("testFindAllProjects() finished");
	}

	@Test
	public void testGetProject() {
		LOGGER.info("testGetProject() started");
		// check that the node is created, it has the defined name
		// check that type is 'project'
		// check that it's attached to the reference node and relation type is
		// correct
		Node got = projectService.getProject(prName + "2");
		Assert.assertNotNull(got);
		Assert.assertEquals(prName + "2",
				got.getProperty(INeoConstants.PROPERTY_NAME_NAME, null));
		assertProjectRelatedToRefNode(got);
		LOGGER.info("testGetProject() finished");
	}

	@Test
	public void testGetProjectNotRecreated() {
		LOGGER.info("testGetProjectNotRecreated() started");
		// check that the returned node is not recreated if already exists
		Node project = projectService.createProject(prName + "3");
		Node got = projectService.getProject(prName + "3");
		Assert.assertEquals(project, got);
		LOGGER.info("testGetProjectNotRecreated() finished");
	}

	@Test(expected = IllegalNodeDataException.class)
	public void testGetProjectEmptyName() {
		LOGGER.info("testGetProjectEmptyName() started");
		projectService.getProject("");
		LOGGER.info("testGetProjectEmptyName() finished");
	}

	@Test(expected = IllegalNodeDataException.class)
	public void testGetProjectNullName() {
		LOGGER.info("testGetProjectNullName() started");
		projectService.getProject(null);
		LOGGER.info("testGetProjectNullName() finished");
	}

	private void assertProjectRelatedToRefNode(Node node) {
		Assert.assertEquals(ProjectService.ProjectNodeType.PROJECT.getId(),
				node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, null));
		Node refNode = graphDb.getReferenceNode();
		for (Relationship rel : node.getRelationships(
				ProjectService.ProjectRelationshipType.PROJECT,
				Direction.INCOMING)) {
			Assert.assertTrue(rel.getOtherNode(node).equals(refNode));
		}
	}

}
