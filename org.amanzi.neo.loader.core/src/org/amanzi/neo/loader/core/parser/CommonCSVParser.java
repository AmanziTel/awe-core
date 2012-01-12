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

package org.amanzi.neo.loader.core.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.loader.core.CountingFileInputStream;
import org.amanzi.neo.loader.core.ProgressEventImpl;
import org.amanzi.neo.loader.core.config.IConfiguration;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.services.model.IModel;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVParser;

/**
 * contain common parser method. Parse file by string than send string for saving.
 * 
 * @author Kondratenko_Vladislav
 */
public class CommonCSVParser<T1 extends ISaver<IModel, MappedData, IConfiguration>>
        extends
            AbstractParser<T1, IConfiguration, MappedData> {
    private static final Logger LOGGER = Logger.getLogger(CommonCSVParser.class);

    // constants
    protected int MINIMAL_SIZE = 2;
    private static final char ESCAPE_SYMBOL = '\\';
    private String charSetName = Charset.defaultCharset().name();

    protected Character delimeters;
    protected String[] possibleFieldSepRegexes = new String[] {"\t", ",", ";", " ", "\n"};
    // high priority separator with
    protected String[] probableSeparator = new String[] {"\t", "\n"};
    private int TABULATION_SEPARATOR_INDEX = 0;
    protected Character quoteCharacter = 0;

    // common parser variables
    protected CSVParser parser;
    private CountingFileInputStream is;
    protected BufferedReader reader;
    private double persentageOld = 0;
    
    private List<String> headers;

    /**
     * create class instance
     */
    public CommonCSVParser() {
        super();
        try {
            if (currentFile != null) {
                is = new CountingFileInputStream(currentFile);
                reader = new BufferedReader(new InputStreamReader(is, charSetName));
            }
        } catch (FileNotFoundException e) {
            // TODO Handle FileNotFoundException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (UnsupportedEncodingException e) {
            // TODO Handle UnsupportedEncodingException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

    }

    /**
     * parse csv file headers
     * 
     * @param file
     * @param is
     * @return
     */
    private List<String> parseHeaders(File file) {
        char delim = getDelimiters(file);
        parser = new CSVParser(delim, quoteCharacter, ESCAPE_SYMBOL, false, true);
        String lineStr;
        ArrayList<String> header = new ArrayList<String>();
        try {
            while ((lineStr = reader.readLine()) != null) {
                if (lineStr != null && !lineStr.isEmpty()) {
                    header.addAll(Arrays.asList(parser.parseLine(lineStr)));
                    if (header.size() >= MINIMAL_SIZE) {
                        break;
                    } else {
                        header.clear();
                    }
                }
            }
            return header;
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    protected MappedData parseElement() {

        if (tempFile == null || tempFile != currentFile) {
            try {
                is = new CountingFileInputStream(currentFile);
                reader = new BufferedReader(new InputStreamReader(is, charSetName));
                tempFile = currentFile;
                persentageOld = 0;
                headers = null;
            } catch (FileNotFoundException e) {
                // TODO Handle FileNotFoundException
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (UnsupportedEncodingException e) {
                // TODO Handle UnsupportedEncodingException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
        if (headers == null) {
            headers = parseHeaders(currentFile);
        }
        try {
            String lineStr;
            if ((lineStr = reader.readLine()) != null) {
                if (lineStr != null) {
                    String[] line = parser.parseLine(lineStr);
                    
                    MappedData element = new MappedData();
                    for (int i = 0; i < headers.size(); i++) {
                        element.put(headers.get(i), line[i]);
                    }
                    
                    return element;
                }
            }
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        } finally {
            double percentage = is.percentage();
            if (percentage - persentageOld >= PERCENTAGE_FIRE) {
                persentageOld = percentage;
                fireSubProgressEvent(currentFile, new ProgressEventImpl(String.format(currentFile.getName()), percentage));
            }
        }
        return null;
    }

    /**
     * Gets the delimiters.
     * 
     * @param file the file
     * @return the delimiters
     */
    private char getDelimiters(File file) {
        if (delimeters == null) {
            Map<String, Integer> fieldSepRegex = defineDelimeters(file, MINIMAL_SIZE, possibleFieldSepRegexes);
            for (String sep : probableSeparator) {
                if (fieldSepRegex.containsKey(sep) && fieldSepRegex.get(sep) > 1) {
                    delimeters = sep.charAt(0);
                }
            }
            if (delimeters == null) {
                Object[] arr = fieldSepRegex.keySet().toArray();
                delimeters = arr[arr.length - 1].toString().charAt(0);
            }
        }

        return delimeters;
    }

    /**
     * Define delimeters.
     * 
     * @param file the file
     * @param minSize the min size
     * @param possibleFieldSepRegexes the possible field sep regexes
     * @return the string
     */
    public Map<String, Integer> defineDelimeters(File file, int minSize, String[] possibleFieldSepRegexes) {
        String fieldSepRegex = possibleFieldSepRegexes[TABULATION_SEPARATOR_INDEX];
        BufferedReader read = null;
        Map<String, Integer> seaprators = new LinkedHashMap<String, Integer>();
        String line;
        try {
            read = new BufferedReader(new FileReader(file));
            while ((line = read.readLine()) != null) {
                int maxMatch = 0;
                for (String regex : possibleFieldSepRegexes) {
                    String[] fields = line.split(regex);
                    if (fields.length > maxMatch) {
                        maxMatch = fields.length;
                        fieldSepRegex = regex;
                        seaprators.put(fieldSepRegex, maxMatch);
                    }
                }
                if (maxMatch >= minSize) {
                    break;
                }
            }
        } catch (IOException e) {
            AweConsolePlugin.error("Cann't define delimeters");
            LOGGER.error("Cann't define delimeters", e);
        } finally {
            try {
                read.close();
            } catch (IOException e) {
                AweConsolePlugin.error("Cann't close read stream");
                LOGGER.error("Cann't close read stream", e);
                e.printStackTrace();
            };
        }
        return seaprators;
    }

    @Override
    protected void finishUpParse() {
        try {
            is.close();
            reader.close();
        } catch (IOException e) {
            AweConsolePlugin.error("Cannt't close stream");
            LOGGER.error("cannt't close stream", e);
        }

    }
}
