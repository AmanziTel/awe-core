/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.amanzi.neo.services.model.impl;

import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.neo4j.graphdb.Node;

public class DataElementTest extends AbstractNeoServiceTest {
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
