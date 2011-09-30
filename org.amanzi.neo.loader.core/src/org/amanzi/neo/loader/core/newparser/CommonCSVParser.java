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
import org.amanzi.neo.loader.core.ProgressEventImpl;
import org.amanzi.neo.loader.core.newsaver.ISaver;
import org.amanzi.neo.services.model.IModel;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVParser;

/**
 * contain common parser method. Parse file by string than send string for saving.
 * 
 * @author Kondratenko_Vladislav
 */
public class CommonCSVParser<T1 extends ISaver<IModel, CSVContainer, T2>, T2 extends IConfiguration>
        extends
            AbstractParser<T1, T2, CSVContainer> {

    protected CSVParser parser;
    protected int MINIMAL_SIZE = 2;
    protected Character delimeters;
    protected String[] possibleFieldSepRegexes = new String[] {"\t", ",", ";"," ","\n"};
    protected Character quoteCharacter = 0;
    private CSVContainer container;
    private CountingFileInputStream is;
    private String charSetName = Charset.defaultCharset().name();
    protected BufferedReader reader;
    private File tempFile;
    private double persentageOld = 0;

    /**
     * 
     */
    public CommonCSVParser() {
        super();
        try {
            LOGGER = Logger.getLogger(CommonCSVParser.class);
            container = new CSVContainer(MINIMAL_SIZE);
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
    protected CSVContainer parseElement() {

        if (tempFile == null || tempFile != currentFile) {
            try {
                is = new CountingFileInputStream(currentFile);
                reader = new BufferedReader(new InputStreamReader(is, charSetName));
                tempFile = currentFile;
            } catch (FileNotFoundException e) {
                // TODO Handle FileNotFoundException
                throw (RuntimeException)new RuntimeException().initCause(e);
            } catch (UnsupportedEncodingException e) {
                // TODO Handle UnsupportedEncodingException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
        if (container.getHeaders() == null) {
            container.setHeaders(parseHeaders(currentFile));
            return container;
        }
        try {
            String lineStr;
            if ((lineStr = reader.readLine()) != null) {
                if (lineStr != null) {
                    String[] line = parser.parseLine(lineStr);
                    container.setValues(new LinkedList<String>(Arrays.asList(line)));
                    return container;
                }
            }
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        } finally {
            double percentage = is.percentage();
            if (percentage - persentageOld > PERCENTAGE_FIRE) {
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
            String fieldSepRegex = LoaderUtils.defineDelimeters(file, MINIMAL_SIZE, possibleFieldSepRegexes);
            delimeters = fieldSepRegex.charAt(0);
        }
        return delimeters;
    }

}
