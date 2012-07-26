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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.neo.loader.core.internal.LoaderCorePlugin;
import org.amanzi.neo.loader.core.synonyms.Synonyms.SynonymType;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeManager;
import org.amanzi.neo.nodetypes.NodeTypeUtils;
import org.amanzi.testing.AbstractMockitoTest;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.junit.Before;
import org.junit.BeforeClass;
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

    /** int SYNONYMBS_NUMBER field */
    private static final int SYNONYMBS_NUMBER = 5;

    /** String NETWORK_SYNONYMS field */
    private static final String NETWORK_SYNONYMS = "synonyms/network.synonyms";

    private enum TestNodeTypes implements INodeType {
        TEST_TYPE_FOR_SYNONYMS, TEST_SYNONYMS_TYPE_1, TEST_SYNONYMS_TYPE_2;

        @Override
        public String getId() {
            return NodeTypeUtils.getTypeId(this);
        }
    }

    private static final INodeType DEFAULT_NODE_TYPE = TestNodeTypes.TEST_TYPE_FOR_SYNONYMS;

    private static final String DRIVE_SYNONYMS = "synonyms/drive.synonyms";

    private static final String N2N_SYNONYMS = "synonyms/n2n.synonyms";

    private static final String DEFAULT_TYPE = "network";

    private static final Class<Integer> DEFAULT_CLASS = Integer.class;

    private static final String DEFAULT_PROPERTY = "property";

    private static final String KEY_WITH_NODETYPE = DEFAULT_NODE_TYPE.getId() + "." + DEFAULT_PROPERTY;

    private static final String KEY_WITH_SUBTYPE_AND_CLASS = KEY_WITH_NODETYPE + "@" + DEFAULT_CLASS.getSimpleName();

    private static final String SYNONYMS_LINE_WITH_SUBTYPE = KEY_WITH_SUBTYPE_AND_CLASS + "=synonym1, synonym2, synonym3";

    private static final String SYNONYMS_LINE_WITHOUT_CLASS = KEY_WITH_NODETYPE + "=synonym7, synonym8, synonym9";

    private static final String SYNONYM_BASE = "synonym";

    private static final String[] SYNONYM_PATHES = {NETWORK_SYNONYMS, DRIVE_SYNONYMS, N2N_SYNONYMS};

    private static final String LOADER_PLUGIN_ID = LoaderCorePlugin.getInstance().getPluginId();

    private SynonymsManager synonymsManager;

    private IExtensionRegistry registry;

    @BeforeClass
    public static void setUpClass() throws IOException {
        AbstractMockitoTest.setUpClass();

        NodeTypeManager.getInstance().registerNodeType(TestNodeTypes.class);
    }

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
    public void testCheckActivityOnGetSynonymsWithSubType() {
        when(registry.getConfigurationElementsFor("org.amanzi.loaderSynonyms")).thenReturn(new IConfigurationElement[] {});

        synonymsManager = spy(new SynonymsManager(registry));

        doReturn(new HashMap<String, Synonyms>()).when(synonymsManager).initializeSynonymsCache(any(String.class));

        synonymsManager.getSynonyms(DEFAULT_TYPE, DEFAULT_NODE_TYPE);

        verify(synonymsManager).initializeSynonymsCache(DEFAULT_TYPE);
    }

    @Test
    public void testCheckCacheActivityOnGetSynonymsWithSubType() {
        when(registry.getConfigurationElementsFor("org.amanzi.loaderSynonyms")).thenReturn(new IConfigurationElement[] {});

        synonymsManager = spy(new SynonymsManager(registry));

        doReturn(new HashMap<String, Synonyms>()).when(synonymsManager).initializeSynonymsCache(any(String.class));

        synonymsManager.getSynonyms(DEFAULT_TYPE, DEFAULT_NODE_TYPE);
        synonymsManager.getSynonyms(DEFAULT_TYPE, DEFAULT_NODE_TYPE);
        synonymsManager.getSynonyms(DEFAULT_TYPE, DEFAULT_NODE_TYPE);

        verify(synonymsManager).initializeSynonymsCache(DEFAULT_TYPE);
    }

    @Test
    public void testCheckResultWhenNoFilesFound() {
        when(registry.getConfigurationElementsFor("org.amanzi.loaderSynonyms")).thenReturn(new IConfigurationElement[] {});

        synonymsManager = spy(new SynonymsManager(registry));

        Map<INodeType, List<Synonyms>> result = synonymsManager.initializeSynonymsCache(DEFAULT_TYPE);

        assertNotNull("result cannot be null", result);
        assertTrue("synonyms map should be empty", result.isEmpty());
    }

    @Test
    public void testCheckActivityWhenFilesFound() throws Exception {
        IConfigurationElement resource = generateResouceElement(LOADER_PLUGIN_ID, NETWORK_SYNONYMS);

        when(registry.getConfigurationElementsFor("org.amanzi.loaderSynonyms")).thenReturn(new IConfigurationElement[] {resource});

        synonymsManager = spy(new SynonymsManager(registry));
        doReturn(new HashMap<INodeType, Synonyms>()).when(synonymsManager).loadSynonyms(any(InputStream.class));

        synonymsManager.initializeSynonymsCache(DEFAULT_TYPE);

        verify(synonymsManager).loadSynonyms(any(InputStream.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckActivityOnLoadSynonyms() throws Exception {
        when(registry.getConfigurationElementsFor("org.amanzi.loaderSynonyms")).thenReturn(new IConfigurationElement[] {});

        synonymsManager = spy(new SynonymsManager(registry));

        doReturn(
                new ImmutablePair<INodeType, Synonyms>(DEFAULT_NODE_TYPE, new Synonyms(StringUtils.EMPTY,
                        ArrayUtils.EMPTY_STRING_ARRAY))).when(synonymsManager).parseSynonyms(any(Entry.class));

        String[] lines = {SYNONYMS_LINE_WITH_SUBTYPE, SYNONYMS_LINE_WITHOUT_CLASS};

        synonymsManager.loadSynonyms(getSynonymsStream(lines));

        verify(synonymsManager, times(lines.length)).parseSynonyms(any(Entry.class));
    }

    @Test
    public void testCheckParsingFullStatisticsLine() throws Exception {
        String[] synonymsArray = getSynonyms(SYNONYM_BASE, SYNONYMBS_NUMBER);

        Entry<Object, Object> entry = new ImmutablePair<Object, Object>(KEY_WITH_SUBTYPE_AND_CLASS, getSynonymsLine(synonymsArray));

        when(registry.getConfigurationElementsFor("org.amanzi.loaderSynonyms")).thenReturn(new IConfigurationElement[] {});

        synonymsManager = new SynonymsManager(registry);

        Pair<INodeType, Synonyms> result = synonymsManager.parseSynonyms(entry);

        assertEquals("unexpected subtype", DEFAULT_NODE_TYPE, result.getKey());

        Synonyms synonyms = result.getValue();

        assertNotNull("synonyms should not be null", synonyms);
        assertEquals("unexpected property", DEFAULT_PROPERTY, synonyms.getPropertyName());
        assertEquals("unexpected class", DEFAULT_CLASS, synonyms.getSynonymType().getSynonymClass());
        assertTrue("unexpected synonyms", Arrays.equals(synonymsArray, synonyms.getPossibleHeaders()));
    }

    @Test
    public void testCheckParsingStatisticsLineWithoutClass() throws Exception {
        String[] synonymsArray = getSynonyms(SYNONYM_BASE, SYNONYMBS_NUMBER);

        Entry<Object, Object> entry = new ImmutablePair<Object, Object>(KEY_WITH_NODETYPE, getSynonymsLine(synonymsArray));

        when(registry.getConfigurationElementsFor("org.amanzi.loaderSynonyms")).thenReturn(new IConfigurationElement[] {});

        synonymsManager = spy(new SynonymsManager(registry));

        Pair<INodeType, Synonyms> result = synonymsManager.parseSynonyms(entry);

        assertEquals("unexpected subtype", DEFAULT_NODE_TYPE, result.getKey());

        Synonyms synonyms = result.getValue();

        assertNotNull("synonyms should not be null", synonyms);
        assertEquals("unexpected property", DEFAULT_PROPERTY, synonyms.getPropertyName());
        assertEquals("unexpected class", SynonymType.UNKOWN, synonyms.getSynonymType());
        assertTrue("unexpected synonyms", Arrays.equals(synonymsArray, synonyms.getPossibleHeaders()));
    }

    @Test
    public void testCheckSynonymsFromTestFile() {
        synonymsManager = SynonymsManager.getInstance();

        for (INodeType nodeType : new INodeType[] {TestNodeTypes.TEST_SYNONYMS_TYPE_1, TestNodeTypes.TEST_SYNONYMS_TYPE_2}) {
            List<Synonyms> synonyms = synonymsManager.getSynonyms("test", nodeType);

            assertEquals("unexpected size of synonyms list", 2, synonyms.size());

            Collections.sort(synonyms, new Comparator<Synonyms>() {

                @Override
                public int compare(Synonyms o1, Synonyms o2) {
                    return o1.getPropertyName().compareTo(o2.getPropertyName());
                }

            });

            for (int i = 0; i < 2; i++) {
                String property = "property" + (i + 1);

                Synonyms synonym = synonyms.get(i);

                assertEquals("unexpected name", property, synonym.getPropertyName());
            }
        }
    }

    private IConfigurationElement generateResouceElement(String pluginId, String path) {
        IConfigurationElement element = mock(IConfigurationElement.class);
        IContributor contributor = mock(IContributor.class);

        when(contributor.getName()).thenReturn(pluginId);
        when(element.getContributor()).thenReturn(contributor);
        when(element.getAttribute("synonymsFile")).thenReturn(path);

        return element;
    }

    private InputStream getSynonymsStream(String... lines) {
        StringBuilder input = new StringBuilder();
        for (String line : lines) {
            input.append(line).append("\n");
        }

        // FIXME: replace with IOUtils.toInputStream(CharSequence) when uDIG will contain latest
        // version of Apache Commons IO
        return IOUtils.toInputStream(input.toString());
    }

    private String[] getSynonyms(String base, int number) {
        String[] result = new String[number];
        for (int i = 0; i < number; i++) {
            result[i] = base + i;
        }

        return result;
    }

    private String getSynonymsLine(String[] synonyms) {
        return StringUtils.join(synonyms, ",");
    }
}
