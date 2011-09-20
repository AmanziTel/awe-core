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

package org.amanzi.neo.loader.core.newparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.amanzi.neo.loader.core.CountingFileInputStream;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.LoaderUtils;
import org.amanzi.neo.loader.core.newsaver.ISaver;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVParser;

/**
 * @author Kondratenko_Vladislav
 */
@SuppressWarnings("rawtypes")
public class NewNetworkParser implements IParser {
    private static Logger LOGGER = Logger.getLogger(NewNetworkParser.class);
    private IConfiguration config;
    private ISaver saver;
    private CSVParser parser;
    private final static int MINIMAL_SIZE = 2;
    protected Character delimeters;
    protected String[] possibleFieldSepRegexes = new String[] {"\t", ",", ";"};
    private Character quoteCharacter = 0;
    private BufferedReader reader;

    /**
     * parse csv file headers
     * 
     * @param file
     * @param is
     * @return
     */
    private String[] parseHeaders(File file, CountingFileInputStream is) {
        char delim = getDelimiters(file);
        parser = new CSVParser(delim, quoteCharacter, '\\', false, true);
        String lineStr;
        String[] header = null;
        try {
            while ((lineStr = reader.readLine()) != null) {
                if (lineStr != null) {
                    header = parser.parseLine(lineStr);
                    break;
                }
            }
            return header;
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        NetworkRowContainer container = null;
        for (File file : config.getFilesToLoad()) {
            try {
                container = new NetworkRowContainer(MINIMAL_SIZE);
                CountingFileInputStream is = new CountingFileInputStream(file);
                String charSetName = Charset.defaultCharset().name();
                reader = new BufferedReader(new InputStreamReader(is, charSetName));
                container.setHeaders(parseHeaders(file, is));
                String lineStr;
                try {
                    while ((lineStr = reader.readLine()) != null) {
                        if (lineStr != null) {
                            String[] line = parser.parseLine(lineStr);
                            container.setValues(line);
                        }
                    }
                } catch (IOException e) {
                    throw (RuntimeException)new RuntimeException().initCause(e);
                }
            } catch (UnsupportedEncodingException e) {
                LOGGER.error(String.format("UnsupportedEncodingException %s ", file.getName()), e);
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (FileNotFoundException e) {
                LOGGER.error(String.format("FILE %s not found", file.getName()));
                throw (RuntimeException)new RuntimeException().initCause(e);
            }

            saver.saveElement(container);
        }

    }

    @Override
    public void init(IConfiguration configuration, ISaver saver) {
        config = configuration;
        this.saver = saver;
    }

    /**
     * Gets the delimiters.
     * 
     * @param file the file
     * @return the delimiters
     */
    private char getDelimiters(File file) {
        if (delimeters == null) {

            String fieldSepRegex = LoaderUtils.defineDelimeters(file, MINIMAL_SIZE, possibleFieldSepRegexes);
            delimeters = fieldSepRegex.charAt(0);
        }
        return delimeters;
    }
}
