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

package org.amanzi.awe.parser.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.parser.core.IParserOldVersion;

import au.com.bytecode.opencsv.CSVReader;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class CsvFileParser implements IParserOldVersion<CSVDataElement> {

    public static final char DEFAULT_SEP = ',';

    private final List<String> headers = new ArrayList<String>();// todo init

    private FileInputStream inputStream;
    private BufferedReader reader;
    private String nextLine;
    private CSVDataElement dataElement;

    private char fieldSep; // todo init

    public void parse() {
        try {

            CSVReader csvReader = new CSVReader(reader);
            String[] nextLine;

            while ((nextLine = csvReader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                // System.out.println(nextLine[0] + nextLine[1] + "etc...");

                dataElement = new CSVDataElement();
                for (int i = 0; i < nextLine.length; i++) {
                    dataElement.put(headers.get(i), nextLine[i]);
                    // todo save
                }
            }

        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

    }

    public boolean hasNext() {
        return nextLine != null;
    }

    public CSVDataElement next() {// todo cleanup
        // nextLine.

        StringBuffer sb = new StringBuffer();
        dataElement = new CSVDataElement();
        int i = 0;

        if (nextLine.length() == 0) {
            // dataElement.add("");
            return dataElement;
        }

        try {
            nextLine = reader.readLine();
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        return null;
    }


    public void remove() {
    }

//    @Override
//    public void init(ParserProperties properties) {
//        try {
//            inputStream = new FileInputStream(properties.getFile());
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//
//            nextLine = reader.readLine();
//        } catch (FileNotFoundException e) {
//            // TODO Handle FileNotFoundException
//            throw (RuntimeException)new RuntimeException().initCause(e);
//        } catch (IOException e) {
//            // TODO Handle IOException
//            throw (RuntimeException)new RuntimeException().initCause(e);
//        }
//    }

    @Override
    public void init(File file) {
    }

    @Override
    public long getFileSize() {
        return 0;
    }

    @Override
    public long getProccessedSize() {
        return 0;
    }

//    @Override
//    public long getFileSize() {
//        return 0;
//    }
//
//    @Override
//    public long getProccessedSize() {
//        return 0;
//    }

}
