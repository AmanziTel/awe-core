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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.amanzi.neo.loader.core.parser.CSVParser;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class ScannerParser extends CSVParser {
    private static final String START_HEADER_MARK = "[DESCRIPTION]";

    /**
     * Parses the header. Reading the file while not found START_HEDAR_MARK. The header continues
     * until the fist empty line. At the empty line header is considered ended.
     * 
     * @param reader the BufferedReader
     * @param parser the CSVParser
     * @return string[] array of header fields or null if header not found
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    protected String[] parseHeader(BufferedReader reader, au.com.bytecode.opencsv.CSVParser parser) throws IOException {
        List<String> headerFields = new ArrayList<String>();
        String line;
        boolean headerStated = false;
        while ((line = reader.readLine()) != null) {
            lineNumber++;

            if (START_HEADER_MARK.equals(line)) {
                headerStated = true;
                continue;
            }
            if (headerStated) {
                if (line.isEmpty()) {
                    // header is ended
                    break;
                }
                String[] fields = parser.parseLine(line);
                headerFields.addAll(Arrays.asList(fields));
            }
        }
        if (headerFields.isEmpty()) {
            return null;
        }
        return headerFields.toArray(new String[0]);
    }
}
