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

package org.amanzi.awe.cassidian.loader.parser;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.commons.io.input.CharSequenceReader;
import org.junit.Before;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVParser;

/**
 * <p>
 * Test for scanner parser (only part was implemented in ScannerParser)
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class ScannerParserTest {
    private final ScannerParser scnnerParser = new ScannerParser();
    private char quoteCharacter;
    private char delim;
    private CSVParser csvParser;
    private BufferedReader bufferedReader;

    private static final String parseHeader_Empty = "";
    private static final String parseHeader_Correct = "some \t pre \n header lines here\n" + "[DESCRIPTION]\n" + "Timestamp [hh:mm:ss]\n" + "GPS\\Position\\Longitude [°] : [1]\n" + "GPS\\Position\\Latitude [°] : [1]\n" + "TETRA Scan\\TOP N Pool 1\1. Rank[1] : [1]\n"
            + "TETRA Scan\\TOP N Pool 1\1. Carrier[1] : [1]\n" + "\n" + "[DATA]\n"
            + "11:20:34;13.355096;52.478212;1;3624;-61.1;-62.38;-3.0;1.2;3624;0.0;30.4;?;?;?;No;2;3757;-73.9;-73.13;-5.0;1.2;3757;0.0;30.1;?;?;?;No;3;3636;-83.3;-84.07;-1.0;1.7;3636;0.0;29.4;?;?;?;No;4;3622;-88.7;-89.77;-4.0;2.9;3622;0.0;25.1;?;?;?;No"
            + "11:20:34;13.355095;52.478212;1;3624;-61.1;-62.38;-3.0;1.2;3624;0.0;30.4;?;?;?;No;2;3757;-73.9;-73.13;-5.0;1.2;3757;0.0;30.1;?;?;?;No;3;3636;-83.3;-84.07;-1.0;1.7;3636;0.0;29.4;?;?;?;No;4;3622;-88.8;-89.77;-4.0;2.9;3622;0.0;25.1;?;?;?;No";

    @Before
    public void beforeTest() {
        csvParser = new CSVParser(delim, quoteCharacter, '\\', false, true);
    }

    @Test
    public void testParseHeaderEmpty() throws IOException {
        beforeParseHeaderEmpty();
        
        String[] result = scnnerParser.parseHeader(bufferedReader, csvParser);
        assertTrue(result == null);
    }

    public void beforeParseHeaderEmpty() {
        CharSequenceReader csr = new CharSequenceReader(parseHeader_Empty);
        bufferedReader = new BufferedReader(csr);
    }

    @Test
    public void testParseHeaderCorrect() throws IOException {
        beforeParseHeaderCorrect();
        
        String[] result = scnnerParser.parseHeader(bufferedReader, csvParser);
        assertTrue(result != null && result.length == 5);
    }
    
    public void beforeParseHeaderCorrect() {
        CharSequenceReader csr = new CharSequenceReader(parseHeader_Correct);
        bufferedReader = new BufferedReader(csr);
    }

}
