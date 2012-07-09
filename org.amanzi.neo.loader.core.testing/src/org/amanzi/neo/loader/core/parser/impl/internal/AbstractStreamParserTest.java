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
import java.io.InputStream;
import java.io.InputStreamReader;

import org.amanzi.neo.loader.core.IData;
import org.amanzi.neo.loader.core.ISingleFileConfiguration;
import org.amanzi.neo.loader.core.exception.impl.FileNotFoundException;
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
public class AbstractStreamParserTest extends AbstractMockitoTest {

    public static class TestStreamParser extends AbstractStreamParser<ISingleFileConfiguration, IData> {

        @Override
        protected IData parseNextElement() {
            return null;
        }

    }

    private ISingleFileConfiguration configuration;

    private TestStreamParser parser;

    private InputStream stream;

    private InputStreamReader reader;

    private File file;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        configuration = mock(ISingleFileConfiguration.class);

        file = File.createTempFile("aspt", "file");
        when(configuration.getFile()).thenReturn(file);

        stream = mock(InputStream.class);
        reader = mock(InputStreamReader.class);

        parser = spy(new TestStreamParser());
        parser.init(configuration);
    }

    @Test
    public void testCheckReaderInitialization() throws Exception {
        doReturn(stream).when(parser).getStream();
        doReturn(reader).when(parser).initializeReader(stream);

        parser.getReader();

        verify(parser).getStream();
        verify(parser).initializeReader(stream);
    }

    @Test
    public void testCheckSavedReaderInstance() throws Exception {
        doReturn(stream).when(parser).getStream();
        doReturn(reader).when(parser).initializeReader(stream);

        parser.getReader();
        parser.getReader();

        verify(parser).getStream();
        verify(parser).initializeReader(stream);
    }

    @Test
    public void testCheckResultOfGetReader() throws Exception {
        doReturn(stream).when(parser).getStream();
        doReturn(reader).when(parser).initializeReader(stream);

        InputStreamReader reader1 = parser.getReader();
        InputStreamReader reader2 = parser.getReader();

        assertEquals("unexpected created reader", reader, reader1);
        assertEquals("unexpected cached reader", reader, reader2);
    }

    @Test
    public void testCheckStreamInitialization() throws Exception {
        doReturn(stream).when(parser).initializeStream(configuration);

        parser.getStream();

        verify(parser).initializeStream(configuration);
    }

    @Test
    public void testCheckSavedStreamInstance() throws Exception {
        doReturn(stream).when(parser).initializeStream(configuration);

        parser.getStream();
        parser.getStream();

        verify(parser).initializeStream(configuration);
    }

    @Test
    public void testCheckResultOfGetStraem() throws Exception {
        doReturn(stream).when(parser).initializeStream(configuration);

        InputStream stream1 = parser.getStream();
        InputStream stream2 = parser.getStream();

        assertEquals("unexpected created stream", stream, stream1);
        assertEquals("unexpected cached stream", stream, stream2);
    }

    @Test
    public void testCheckStreamInitializationActivity() throws Exception {
        parser.initializeStream(configuration);

        verify(configuration).getFile();
    }

    @Test(expected = FileNotFoundException.class)
    public void testCheckStreamInitializationException() throws Exception {
        file = new File("/home/user/file");
        when(configuration.getFile()).thenReturn(file);

        parser.initializeStream(configuration);
    }

    @Test
    public void testCheckActivityOnFinishUp() throws Exception {
        doReturn(stream).when(parser).initializeStream(configuration);
        doReturn(reader).when(parser).initializeReader(stream);

        parser.getStream();
        parser.getReader();

        parser.finishUp();

        verify(reader).close();
        verify(stream).close();
    }

    @Test
    public void testCheckOnlyStreamActivityOnFinishUp() throws Exception {
        doReturn(stream).when(parser).initializeStream(configuration);
        doReturn(reader).when(parser).initializeReader(stream);

        parser.getStream();

        parser.finishUp();

        verify(reader, never()).close();
        verify(stream).close();
    }

    @Test
    public void testCheckNoActivityOnFinishUp() throws Exception {
        doReturn(stream).when(parser).initializeStream(configuration);
        doReturn(reader).when(parser).initializeReader(stream);

        parser.finishUp();

        verify(reader, never()).close();
        verify(stream, never()).close();
    }
}
