package org.amanzi.neo.services.model.impl;

import org.amanzi.testing.AbstractAWETest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.neo4j.graphdb.Node;

public class DataElementTest extends AbstractAWETest {
	Mockery context = new Mockery();

	/**
	 * Test that DataElement fetches a property from Node object only once.
	 */
	@Test
	public void testGetString() {
		final String propertyName = "prop";
		final Node node = context.mock(Node.class);

		DataElement de = new DataElement(node);

		// expectations
		context.checking(new Expectations() {
			{
				atMost(1).of(node).getProperty(propertyName, null);
			}
		});

		// execute
		de.get(propertyName);
		de.get(propertyName);

		// verify
		context.assertIsSatisfied();

	}

}
