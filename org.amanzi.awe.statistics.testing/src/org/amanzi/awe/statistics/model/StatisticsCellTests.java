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

package org.amanzi.awe.statistics.model;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * tests for statistics cell
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsCellTests extends AbstractStatisticsModelTests {
    private static final Logger LOGGER = Logger.getLogger(StatisticsCellTests.class);

    @Test
    public void testAddSources() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testAddSources started ");
        Node mockedScell = getMockedScell(SCELL_NAME);
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE);
        StatisticsCell scell = new StatisticsCell(mockedSrow, mockedScell);
        int listSize = (int)(Math.random() * 100);
        List<IDataElement> generatedSources = generateSources(listSize);
        scell.addSources(generatedSources);
        verify(statisticsService, times(listSize)).addSource(eq(mockedScell), any(Node.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSourcesIfSourcesNull() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testAddSourcesIfSourcesNull started ");
        Node mockedScell = getMockedScell(SCELL_NAME);
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE);
        StatisticsCell scell = new StatisticsCell(mockedSrow, mockedScell);
        scell.addSources(null);
    }

    @Test
    public void testGetSources() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSources started ");
        Node mockedScell = getMockedScell(SCELL_NAME);
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE);
        StatisticsCell scell = new StatisticsCell(mockedSrow, mockedScell);
        int listSize = (int)(Math.random() * 100);
        List<IDataElement> generatedSources = generateSources(listSize);
        List<Node> generatedSourcesNodes = new ArrayList<Node>();
        for (IDataElement dataElement : generatedSources) {
            Node nodeSource = ((DataElement)dataElement).getNode();
            generatedSourcesNodes.add(nodeSource);
        }

        when(statisticsService.getSources(eq(mockedScell))).thenReturn(generatedSourcesNodes);
        Iterable<IDataElement> elements = scell.getSources();
        Assert.assertEquals("Expected the same sources list", generatedSources, elements);
    }
}
