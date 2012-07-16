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

package org.amanzi.neo.loader.core.synonyms;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.loader.core.internal.Activator;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.testing.AbstractMockitoTest;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtensionRegistry;
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
public class SynonymsManagerTest extends AbstractMockitoTest {

    /** String NETWORK_SYNONYMS field */
    private static final String NETWORK_SYNONYMS = "synonyms/network.synonyms";

    private static final String DRIVE_SYNONYMS = "synonyms/drive.synonyms";

    private static final String N2N_SYNONYMS = "synonyms/n2n.synonyms";

    private static final String DEFAULT_SUB_TYPE = "subtype";

    private static final INodeType DEFAULT_TYPE = new INodeType() {

        @Override
        public String getId() {
            return "network";
        }
    };

    private static final String[] SYNONYM_PATHES = {NETWORK_SYNONYMS, DRIVE_SYNONYMS, N2N_SYNONYMS};

    private static final String LOADER_PLUGIN_ID = Activator.getInstance().getPluginId();

    private SynonymsManager synonymsManager;

    private IExtensionRegistry registry;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        registry = mock(IExtensionRegistry.class);
    }

    @Test
    public void testCheckResultOnConstructor() {
        IConfigurationElement[] resouces = new IConfigurationElement[SYNONYM_PATHES.length];

        for (int i = 0; i < SYNONYM_PATHES.length; i++) {
            resouces[i] = generateResouceElement(LOADER_PLUGIN_ID, SYNONYM_PATHES[i]);
        }

        when(registry.getConfigurationElementsFor("org.amanzi.loaderSynonyms")).thenReturn(resouces);

        synonymsManager = new SynonymsManager(registry);

        assertEquals("unexpected number of resources", SYNONYM_PATHES.length, synonymsManager.getResources().size());
    }

    @Test
    public void testCheckResultOnGetResource() {
        IConfigurationElement resource = generateResouceElement(LOADER_PLUGIN_ID, NETWORK_SYNONYMS);

        when(registry.getConfigurationElementsFor("org.amanzi.loaderSynonyms")).thenReturn(new IConfigurationElement[] {resource});

        synonymsManager = new SynonymsManager(registry);

        URL url = synonymsManager.getResources().values().iterator().next().get(0);

        assertTrue("unexpected file part", url.getFile().contains(NETWORK_SYNONYMS));
    }

    @Test
    public void testCheckActivityOnGetSynonyms() {
        when(registry.getConfigurationElementsFor("org.amanzi.loaderSynonyms")).thenReturn(new IConfigurationElement[] {});

        synonymsManager = spy(new SynonymsManager(registry));

        doReturn(new HashMap<String, Synonyms>()).when(synonymsManager).initializeSynonymsCache(any(String.class));

        synonymsManager.getSynonyms(DEFAULT_TYPE);

        verify(synonymsManager).getSynonyms(DEFAULT_TYPE, "[ALL]");
    }

    @Test
    public void testCheckActivityOnGetSynonymsWithSubType() {
        when(registry.getConfigurationElementsFor("org.amanzi.loaderSynonyms")).thenReturn(new IConfigurationElement[] {});

        synonymsManager = spy(new SynonymsManager(registry));

        doReturn(new HashMap<String, Synonyms>()).when(synonymsManager).initializeSynonymsCache(any(String.class));

        synonymsManager.getSynonyms(DEFAULT_TYPE, DEFAULT_SUB_TYPE);

        verify(synonymsManager).initializeSynonymsCache(DEFAULT_TYPE.getId());
    }

    @Test
    public void testCheckCacheActivityOnGetSynonymsWithSubType() {
        when(registry.getConfigurationElementsFor("org.amanzi.loaderSynonyms")).thenReturn(new IConfigurationElement[] {});

        synonymsManager = spy(new SynonymsManager(registry));

        doReturn(new HashMap<String, Synonyms>()).when(synonymsManager).initializeSynonymsCache(any(String.class));

        synonymsManager.getSynonyms(DEFAULT_TYPE, DEFAULT_SUB_TYPE);
        synonymsManager.getSynonyms(DEFAULT_TYPE, DEFAULT_SUB_TYPE);
        synonymsManager.getSynonyms(DEFAULT_TYPE, DEFAULT_SUB_TYPE);

        verify(synonymsManager).initializeSynonymsCache(DEFAULT_TYPE.getId());
    }

    @Test
    public void testCheckResultWhenNoFilesFound() {
        when(registry.getConfigurationElementsFor("org.amanzi.loaderSynonyms")).thenReturn(new IConfigurationElement[] {});

        synonymsManager = spy(new SynonymsManager(registry));

        Map<String, Synonyms> result = synonymsManager.initializeSynonymsCache(DEFAULT_TYPE.getId());

        assertNotNull("result cannot be null", result);
        assertTrue("synonyms map should be empty", result.isEmpty());
    }

    @Test
    public void testCheckActivityWhenFilesFound() {
        IConfigurationElement resource = generateResouceElement(LOADER_PLUGIN_ID, NETWORK_SYNONYMS);

        when(registry.getConfigurationElementsFor("org.amanzi.loaderSynonyms")).thenReturn(new IConfigurationElement[] {resource});

        synonymsManager = spy(new SynonymsManager(registry));
        doReturn(new HashMap<String, Synonyms>()).when(synonymsManager).loadSynonyms(any(InputStream.class));

        synonymsManager.initializeSynonymsCache(DEFAULT_TYPE.getId());

        verify(synonymsManager).loadSynonyms(any(InputStream.class));
    }

    private IConfigurationElement generateResouceElement(String pluginId, String path) {
        IConfigurationElement element = mock(IConfigurationElement.class);
        IContributor contributor = mock(IContributor.class);

        when(contributor.getName()).thenReturn(pluginId);
        when(element.getContributor()).thenReturn(contributor);
        when(element.getAttribute("synonymsFile")).thenReturn(path);

        return element;
    }
}
