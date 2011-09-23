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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.loader.core.CountingFileInputStream;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.LoaderUtils;
import org.amanzi.neo.loader.core.newsaver.IData;
import org.amanzi.neo.loader.core.newsaver.ISaver;
import org.amanzi.neo.services.networkModel.IModel;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVParser;

/**
 * @author Kondratenko_Vladislav
 */
public class NewNetworkParser<T1 extends ISaver<IModel, T3, T2>, T2 extends IConfiguration, T3 extends IData>
        implements
            IParser<T1, T2, T3> {
    private static Logger LOGGER = Logger.getLogger(NewNetworkParser.class);
    private T2 config;
    private List<T1> saver;
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
    private List<String> parseHeaders(File file, CountingFileInputStream is) {
        char delim = getDelimiters(file);
        parser = new CSVParser(delim, quoteCharacter, '\\', false, true);
        String lineStr;
        ArrayList<String> header = new ArrayList<String>();
        try {
            while ((lineStr = reader.readLine()) != null) {
                if (lineStr != null) {
                    header.addAll(Arrays.asList(parser.parseLine(lineStr)));
                    break;
                }
            }

            return header;
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        NetworkRowContainer container;
        for (File file : config.getFilesToLoad()) {
            try {
                container = new NetworkRowContainer(MINIMAL_SIZE);
                CountingFileInputStream is = new CountingFileInputStream(file);
                String charSetName = Charset.defaultCharset().name();
                reader = new BufferedReader(new InputStreamReader(is, charSetName));
                container.setHeaders(parseHeaders(file, is));
                saveInAllSavers((T3)container);
                String lineStr;
                try {
                    while ((lineStr = reader.readLine()) != null) {
                        if (lineStr != null) {
                            String[] line = parser.parseLine(lineStr);
                            container.setValues(new LinkedList<String>(Arrays.asList(line)));
                            saveInAllSavers((T3)container);
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
            LOGGER.info("Saving network data finishing in: " + (System.currentTimeMillis() - startTime) + ": file "
                    + file.getName());

        }

    }

    private void saveInAllSavers(T3 data) {
        for (T1 saverMember : saver) {
            saverMember.saveElement(data);
        }
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

    @Override
    public void init(T2 configuration, List<T1> saver) {
        this.config=configuration;
        this.saver = saver;

    }

}
