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

package org.amanzi.neo.loader.core.parser.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class CSVUtils {

    private static final Logger LOGGER = Logger.getLogger(CSVUtils.class);

    private static final char[] POSSIBLE_SEPARATORS = new char[] {'\t', ',', ';', ' ', '\n'};

    private static final int MINIMAL_HEADERS_SIZE = 2;

    /**
     * 
     */
    private CSVUtils() {
    }

    public static char getSeparator(File file) {
        int max = 0;
        Character result = null;
        for (Entry<Character, Integer> candidate : defineDelimeters(file).entrySet()) {
            if (candidate.getValue() > max) {
                max = candidate.getValue();
                result = candidate.getKey();
            }
        }

        return result;
    }

    private static Map<Character, Integer> defineDelimeters(File file) {
        BufferedReader read = null;
        Map<Character, Integer> seaprators = new HashMap<Character, Integer>();
        String line;
        try {
            read = new BufferedReader(new FileReader(file));
            while ((line = read.readLine()) != null) {
                int maxMatch = 0;
                for (char regex : POSSIBLE_SEPARATORS) {
                    String[] fields = line.split(Character.toString(regex));
                    if (fields.length > maxMatch) {
                        maxMatch = fields.length;
                        seaprators.put(regex, maxMatch);
                    }
                }
                if (maxMatch >= MINIMAL_HEADERS_SIZE) {
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Can't define delimeters", e);
        } finally {
            IOUtils.closeQuietly(read);
        }
        return seaprators;
    }

}
