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

import java.io.File;
import java.io.IOException;

import org.amanzi.neo.loader.core.IData;
import org.amanzi.neo.loader.core.exception.impl.GeneralParsingException;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.testing.AbstractMockitoTest;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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

    /**
     * TODO Purpose of
     * <p>
     * </p>
     * 
     * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
     * @since 1.0.0
     */
    private final class TestParserIterator implements Answer<IData> {
        /** IData data field */
        private final IData data;

        /**
         * @param data
         */
        private TestParserIterator(final IData data) {
            this.data = data;
        }

        @Override
        public IData answer(final InvocationOnMock invocation) {
            if (++parsedTimes < NEXT_TIMES) {
                return data;
            }
            return null;
        }
    }

    public static class TestParser extends AbstractParser<IConfiguration, IData> {

        @Override
        protected IData parseNextElement() throws IOException {
            return null;
        }

        @Override
        protected File getFileFromConfiguration(final IConfiguration configuration) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    private TestParser parser;

    private int parsedTimes;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        parser = spy(new TestParser());
        parser.setProgressMonitor(StringUtils.EMPTY, new NullProgressMonitor());

        final IData data = mock(IData.class);

        parsedTimes = 0;

        when(parser.parseNextElement()).thenAnswer(new TestParserIterator(data));
    }

    @Test
    public void testCheckParsedFewTimeOnMultiNext() throws Exception {
        for (int i = 0; i < NEXT_TIMES; i++) {
            parser.next();
        }

        verify(parser, times(NEXT_TIMES)).parseNextElement();
    }

    @Test
    public void testCheckOneTimeParsingOnHasNext() throws Exception {
        for (int i = 0; i < NEXT_TIMES; i++) {
            parser.hasNext();
        }

        verify(parser).parseNextElement();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCheckExceptionOnRemove() {
        parser.remove();
    }

    @Test
    public void testCheckIterationForAllElements() {
        for (int i = 0; i < (NEXT_TIMES - 1); i++) {
            assertTrue("next element should exists", parser.hasNext());
            parser.next();
        }

        assertFalse("next element should not exists", parser.hasNext());
    }

    @Test
    public void testCheckActivityOnFinishUp() {
        IProgressMonitor monitor = mock(IProgressMonitor.class);
        parser.setProgressMonitor("Loader name", monitor);

        parser.finishUp();

        verify(monitor).done();
    }

    @Test(expected = GeneralParsingException.class)
    public void testCheckGeneralParsingException() throws Exception {
        doThrow(new IOException()).when(parser).parseNextElement();

        parser.parseToNextElement();
    }
}
