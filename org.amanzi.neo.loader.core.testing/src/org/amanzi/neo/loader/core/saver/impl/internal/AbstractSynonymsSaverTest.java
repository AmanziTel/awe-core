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

import org.amanzi.neo.loader.core.IMappedStringData;
import org.amanzi.neo.loader.core.impl.MappedStringData;
import org.amanzi.neo.loader.core.synonyms.SynonymsManager;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeUtils;
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

    private enum TestNodeType implements INodeType {
        TEST_TYPE_FOR_SAVER;

        @Override
        public String getId() {
            return NodeTypeUtils.getTypeId(this);
        }
    }

    public class TestAbstractSynonymsSaver extends AbstractSynonymsSaver {

        @Override
        public void save(IMappedStringData dataElement) {
        }

        @Override
        protected String getDatasetType() {
            return SYNONYMS_TYPE;
        }

    }

    private SynonymsManager synonymsManager;

    private AbstractSynonymsSaver saver;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        synonymsManager = mock(SynonymsManager.class);

        saver = spy(new TestAbstractSynonymsSaver());
    }

    @Test
    public void testCheckActivityOnGetElement() {
        doReturn(AbstractSynonymsSaver.SKIPPED_PROPERTY).when(saver).createProperty();

        saver.getElementProperties(TestNodeType.TEST_TYPE_FOR_SAVER, getData(5), false);

        verify(saver, times(5)).createProperty();
    }

    @Test
    public void testCheckPropertyCachingOnGetElement() {
        doReturn(AbstractSynonymsSaver.SKIPPED_PROPERTY).when(saver).createProperty();

        saver.getElementProperties(TestNodeType.TEST_TYPE_FOR_SAVER, getData(5), false);
        saver.getElementProperties(TestNodeType.TEST_TYPE_FOR_SAVER, getData(5), false);

        verify(saver, times(5)).createProperty();
    }

    private IMappedStringData getData(int count) {
        MappedStringData result = new MappedStringData();

        for (int i = 0; i < count; i++) {
            result.put("property" + i, "value" + i);
        }

        return result;
    }
}
