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

package org.amanzi.neo.loader.core.parser.impl.internal;

import org.amanzi.neo.loader.core.IData;
import org.amanzi.neo.loader.core.internal.IConfiguration;
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
public class AbstractParserTest extends AbstractMockitoTest {

    private static final int NEXT_TIMES = 3;

    public static class TestParser extends AbstractParser<IConfiguration, IData> {

        @Override
        protected IData parseNextElement() {
            return null;
        }

    }

    private TestParser parser;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        parser = spy(new TestParser());

        IData data = mock(IData.class);
        when(parser.parseNextElement()).thenReturn(data);
    }

    @Test
    public void testCheckParsedFewTimeOnMultiNext() {
        for (int i = 0; i < NEXT_TIMES; i++) {
            parser.next();
        }

        verify(parser, times(NEXT_TIMES)).parseNextElement();
    }
}
