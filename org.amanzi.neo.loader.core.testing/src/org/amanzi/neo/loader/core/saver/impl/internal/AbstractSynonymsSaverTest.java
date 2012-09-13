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

package org.amanzi.neo.loader.core.saver.impl.internal;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.core.IMappedStringData;
import org.amanzi.neo.loader.core.impl.MappedStringData;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.saver.impl.internal.AbstractSynonymsSaver.BooleanProperty;
import org.amanzi.neo.loader.core.saver.impl.internal.AbstractSynonymsSaver.DoubleProperty;
import org.amanzi.neo.loader.core.saver.impl.internal.AbstractSynonymsSaver.IntegerProperty;
import org.amanzi.neo.loader.core.saver.impl.internal.AbstractSynonymsSaver.LongProperty;
import org.amanzi.neo.loader.core.saver.impl.internal.AbstractSynonymsSaver.Property;
import org.amanzi.neo.loader.core.saver.impl.internal.AbstractSynonymsSaver.StringProperty;
import org.amanzi.neo.loader.core.saver.impl.internal.AbstractSynonymsSaver.TimestampProperty;
import org.amanzi.neo.loader.core.saver.impl.internal.AbstractSynonymsSaver.UndefinedProperty;
import org.amanzi.neo.loader.core.synonyms.Synonyms;
import org.amanzi.neo.loader.core.synonyms.Synonyms.SynonymType;
import org.amanzi.neo.loader.core.synonyms.SynonymsManager;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeUtils;
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
public class AbstractSynonymsSaverTest extends AbstractMockitoTest {

    private static final String SYNONYMS_TYPE = "synonyms_type";

    private static final String HEADER_NAME = "header";

    private static final String PROPERTY_NAME = "property";

    private static final DateFormat DATE_TIME_PATTERN = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private long currentTimestamp;

    private enum TestNodeType implements INodeType {
        TEST_TYPE_FOR_SAVER;

        @Override
        public String getId() {
            return NodeTypeUtils.getTypeId(this);
        }
    }

    public class TestAbstractSynonymsSaver extends AbstractSynonymsSaver<IConfiguration> {

        /**
         * @param projectModelProvider
         * @param synonymsManager
         */
        protected TestAbstractSynonymsSaver(final IProjectModelProvider projectModelProvider, final SynonymsManager synonymsManager) {
            super(projectModelProvider, synonymsManager);
        }

        @Override
        public void save(final IMappedStringData dataElement) {
        }

        @Override
        protected String getSynonymsType() {
            return SYNONYMS_TYPE;
        }

        @Override
        protected void saveInModel(final IMappedStringData data) throws ModelException {

        }

        @Override
        public void onFileParsingStarted(final File file) {
            // TODO Auto-generated method stub

        }

    }

    private SynonymsManager synonymsManager;

    private AbstractSynonymsSaver<IConfiguration> saver;

    private IProjectModelProvider projectModelProvider;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        synonymsManager = mock(SynonymsManager.class);

        projectModelProvider = mock(IProjectModelProvider.class);

        saver = spy(new TestAbstractSynonymsSaver(projectModelProvider, synonymsManager));

        currentTimestamp = System.currentTimeMillis();
    }

    @Test
    public void testCheckActivityOnGetElement() {
        doReturn(AbstractSynonymsSaver.SKIPPED_PROPERTY).when(saver).createProperty(eq(TestNodeType.TEST_TYPE_FOR_SAVER),
                any(String.class), eq(false));

        saver.getElementProperties(TestNodeType.TEST_TYPE_FOR_SAVER, getData(5), false);

        verify(saver, times(5)).createProperty(eq(TestNodeType.TEST_TYPE_FOR_SAVER), any(String.class), eq(false));
    }

    @Test
    public void testCheckPropertyCachingOnGetElement() {
        doReturn(AbstractSynonymsSaver.SKIPPED_PROPERTY).when(saver).createProperty(eq(TestNodeType.TEST_TYPE_FOR_SAVER),
                any(String.class), eq(false));

        saver.getElementProperties(TestNodeType.TEST_TYPE_FOR_SAVER, getData(5), false);
        saver.getElementProperties(TestNodeType.TEST_TYPE_FOR_SAVER, getData(5), false);

        verify(saver, times(5)).createProperty(eq(TestNodeType.TEST_TYPE_FOR_SAVER), any(String.class), eq(false));
    }

    @Test
    public void testCheckCachingOnCreateProperty() {
        when(synonymsManager.getSynonyms(SYNONYMS_TYPE, TestNodeType.TEST_TYPE_FOR_SAVER)).thenReturn(new ArrayList<Synonyms>());

        saver.createProperty(TestNodeType.TEST_TYPE_FOR_SAVER, HEADER_NAME, false);
        saver.createProperty(TestNodeType.TEST_TYPE_FOR_SAVER, HEADER_NAME, false);

        verify(synonymsManager, times(1)).getSynonyms(SYNONYMS_TYPE, TestNodeType.TEST_TYPE_FOR_SAVER);
    }

    @Test
    public void testCheckSkippedProperty() {
        when(synonymsManager.getSynonyms(SYNONYMS_TYPE, TestNodeType.TEST_TYPE_FOR_SAVER)).thenReturn(new ArrayList<Synonyms>());

        Property< ? > result = saver.createProperty(TestNodeType.TEST_TYPE_FOR_SAVER, HEADER_NAME, false);

        assertEquals("unexpected property", AbstractSynonymsSaver.SKIPPED_PROPERTY, result);
    }

    @Test
    public void testCheckUndefinedProperty() {
        when(synonymsManager.getSynonyms(SYNONYMS_TYPE, TestNodeType.TEST_TYPE_FOR_SAVER)).thenReturn(new ArrayList<Synonyms>());

        Property< ? > result = saver.createProperty(TestNodeType.TEST_TYPE_FOR_SAVER, HEADER_NAME, true);

        assertEquals("unexpected property", UndefinedProperty.class, result.getClass());
        assertEquals("unexpected property name", HEADER_NAME, result.getPropertyName());
    }

    @Test
    public void testCheckClassDefinedProperty() {
        List<Synonyms> synonymsList = createSynonyms(true);
        when(synonymsManager.getSynonyms(SYNONYMS_TYPE, TestNodeType.TEST_TYPE_FOR_SAVER)).thenReturn(synonymsList);

        for (SynonymType type : SynonymType.values()) {
            String header = type.getSynonymClass() == null ? type.toString() : type.getSynonymClass().getSimpleName();

            Property< ? > result = saver.createProperty(TestNodeType.TEST_TYPE_FOR_SAVER, header, false);

            assertEquals("unexpected property", getPropertyClass(type), result.getClass());
            assertEquals("unexpected property name", type == SynonymType.UNKOWN ? header.toLowerCase() : PROPERTY_NAME,
                    result.getPropertyName());
        }
    }

    @Test
    public void testCheckFullParsing() {
        List<Synonyms> synonymsList = createSynonyms(false);
        when(synonymsManager.getSynonyms(SYNONYMS_TYPE, TestNodeType.TEST_TYPE_FOR_SAVER)).thenReturn(synonymsList);

        IMappedStringData data = getData();

        Map<String, Object> result = saver.getElementProperties(TestNodeType.TEST_TYPE_FOR_SAVER, data, false);

        assertEquals("unexpected size of result", data.size(), result.size());

        for (Synonyms synonym : synonymsList) {
            if (synonym.getSynonymType() != SynonymType.UNKOWN) {
                assertTrue("property should exists", result.containsKey(synonym.getPropertyName()));

                Object actualValue = result.get(synonym.getPropertyName());

                Object value = getValueForType(synonym.getSynonymType());

                assertEquals("unexpected value", value, actualValue);
            }
        }
    }

    @Test
    public void testCheckAutoparsing() {
        IMappedStringData data = getData();

        for (SynonymType type : SynonymType.values()) {
            if (type != SynonymType.UNKOWN) {
                Property< ? > undefinedProperty = new UndefinedProperty(type.getSynonymClass().getSimpleName());

                Object value = getValueForType(type);

                assertEquals("unexpected parsed value", value, undefinedProperty.parse(data));
            }
        }
    }

    private Object getValueForType(final SynonymType type) {
        Object value = null;
        switch (type) {
        case BOOLEAN:
            value = Boolean.FALSE;
            break;
        case DOUBLE:
            value = Double.MIN_VALUE;
            break;
        case INTEGER:
            value = Integer.MAX_VALUE;
            break;
        case LONG:
            value = Long.MAX_VALUE;
            break;
        case STRING:
            value = String.class.getSimpleName();
            break;
        case TIMESTAMP:
            value = currentTimestamp;
            break;
        default:
            value = null;
            break;
        }

        return value;
    }

    private IMappedStringData getData() {
        IMappedStringData result = new MappedStringData();

        for (SynonymType type : SynonymType.values()) {
            String value = null;
            switch (type) {
            case BOOLEAN:
                value = Boolean.FALSE.toString();
                break;
            case DOUBLE:
                value = Double.toString(Double.MIN_VALUE);
                break;
            case INTEGER:
                value = Integer.toString(Integer.MAX_VALUE);
                break;
            case LONG:
                value = Long.toString(Long.MAX_VALUE);
                break;
            case STRING:
                value = String.class.getSimpleName();
                break;
            case TIMESTAMP:
                value = DATE_TIME_PATTERN.format(new Date(currentTimestamp));
                break;
            default:
                value = null;
                break;
            }

            if (value != null) {
                result.put(type.getSynonymClass().getSimpleName(), value);
            }
        }

        return result;
    }

    private Class< ? extends Property< ? >> getPropertyClass(final SynonymType type) {
        switch (type) {
        case BOOLEAN:
            return BooleanProperty.class;
        case DOUBLE:
            return DoubleProperty.class;
        case INTEGER:
            return IntegerProperty.class;
        case LONG:
            return LongProperty.class;
        case STRING:
            return StringProperty.class;
        case TIMESTAMP:
            return TimestampProperty.class;
        default:
            return UndefinedProperty.class;
        }
    }

    private List<Synonyms> createSynonyms(final boolean useDefault) {
        List<Synonyms> result = new ArrayList<Synonyms>();
        for (SynonymType type : SynonymType.values()) {
            result.add(new Synonyms(useDefault ? PROPERTY_NAME : type.toString(), type, Boolean.FALSE, getPossibleHeaders(type)));
        }

        return result;
    }

    private String[] getPossibleHeaders(final SynonymType type) {
        return new String[] {type.getSynonymClass() == null ? type.toString() : type.getSynonymClass().getSimpleName()};
    }

    private IMappedStringData getData(final int count) {
        MappedStringData result = new MappedStringData();

        for (int i = 0; i < count; i++) {
            result.put("property" + i, "value" + i);
        }

        return result;
    }
}
