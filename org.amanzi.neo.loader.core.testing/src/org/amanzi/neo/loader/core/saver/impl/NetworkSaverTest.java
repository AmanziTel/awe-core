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

package org.amanzi.neo.loader.core.saver.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.loader.core.IMappedStringData;
import org.amanzi.neo.loader.core.impl.MappedStringData;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.synonyms.Synonyms;
import org.amanzi.neo.loader.core.synonyms.Synonyms.SynonymType;
import org.amanzi.neo.loader.core.synonyms.SynonymsManager;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.network.NetworkElementType;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;
import org.amanzi.neo.nodeproperties.impl.NetworkNodeProperties;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.amanzi.testing.AbstractMockitoTest;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NetworkSaverTest extends AbstractMockitoTest {

    private static final IGeneralNodeProperties GENERAL_NODE_PROPERTIES = new GeneralNodeProperties();

    private static final String NETWORK_NAME = "network";

    private static final String NAME_VALUE = "some_name";

    private static final String SOME_VALUE = "some_value";

    private NetworkSaver saver;

    private IConfiguration configuration;

    private INetworkModelProvider networkModelProvider;

    private IProjectModelProvider projectModelProvider;

    private IProjectModel currentProject;

    private INetworkModel networkModel;

    private SynonymsManager synonymsManager;

    private IDataElement networkNode;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        configuration = mock(IConfiguration.class);
        when(configuration.getDatasetName()).thenReturn(NETWORK_NAME);

        currentProject = mock(IProjectModel.class);

        networkModelProvider = mock(INetworkModelProvider.class);

        networkModel = mock(INetworkModel.class);
        when(networkModelProvider.create(currentProject, NETWORK_NAME)).thenReturn(networkModel);

        networkNode = mock(IDataElement.class);
        when(networkModel.asDataElement()).thenReturn(networkNode);

        projectModelProvider = mock(IProjectModelProvider.class);
        when(projectModelProvider.getActiveProjectModel()).thenReturn(currentProject);

        synonymsManager = mock(SynonymsManager.class);

        saver = spy(new NetworkSaver(projectModelProvider, networkModelProvider, synonymsManager, GENERAL_NODE_PROPERTIES,
                new NetworkNodeProperties()));
        saver.init(configuration);
    }

    @Test
    public void testCheckNetworkCreationOnInitialization() throws Exception {
        verify(saver).createNetworkModel(NETWORK_NAME);
    }

    @Test
    public void testCheckNetworkCreation() throws Exception {
        verify(networkModelProvider).create(currentProject, NETWORK_NAME);
    }

    @Test
    public void testCheckOnlyCityCreated() throws Exception {
        checkSingleElementCreated(NetworkElementType.CITY);
    }

    @Test
    public void testCheckOnlyMSCCreated() throws Exception {
        checkSingleElementCreated(NetworkElementType.MSC);
    }

    @Test
    public void testCheckOnlyBSCCreated() throws Exception {
        checkSingleElementCreated(NetworkElementType.BSC);
    }

    @Test
    public void testCheckOnlySiteCreated() throws Exception {
        checkSingleElementCreated(NetworkElementType.SITE);
    }

    private void checkSingleElementCreated(final NetworkElementType type) throws Exception {
        List<Synonyms> synonyms = getSynonyms(type);

        when(synonymsManager.getSynonyms(saver.getSynonymsType(), type)).thenReturn(synonyms);

        when(networkModel.findElement(type, NAME_VALUE)).thenReturn(null);

        saver.save(getData(type));

        Map<String, Object> properties = new HashMap<String, Object>();

        verify(networkModel).findElement(type, NAME_VALUE);
        verify(networkModel).createElement(eq(type), eq(networkNode), eq(NAME_VALUE), eq(properties));
    }

    private List<Synonyms> getSynonyms(final NetworkElementType type) {
        List<Synonyms> result = new ArrayList<Synonyms>();

        result.add(new Synonyms(GENERAL_NODE_PROPERTIES.getNodeNameProperty(), SynonymType.STRING, Boolean.FALSE,
                new String[] {NAME_VALUE}));

        return result;
    }

    private IMappedStringData getData(final NetworkElementType type) {
        IMappedStringData result = new MappedStringData();

        result.put(NAME_VALUE, NAME_VALUE);
        result.put(SOME_VALUE, SOME_VALUE);

        return result;
    }
}
