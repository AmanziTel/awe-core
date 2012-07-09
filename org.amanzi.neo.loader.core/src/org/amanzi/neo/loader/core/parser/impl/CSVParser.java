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

import java.io.IOException;
import java.io.InputStreamReader;

import org.amanzi.neo.loader.core.IMappedStringData;
import org.amanzi.neo.loader.core.ISingleFileConfiguration;
import org.amanzi.neo.loader.core.exception.LoaderException;
import org.amanzi.neo.loader.core.impl.MappedStringData;
import org.amanzi.neo.loader.core.parser.impl.internal.AbstractStreamParser;

import au.com.bytecode.opencsv.CSVReader;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class CSVParser extends AbstractStreamParser<ISingleFileConfiguration, IMappedStringData> {

    private CSVReader csvReader;

    private boolean headersParsed;

    private String[] headers;

    @Override
    protected IMappedStringData parseNextElement() throws IOException {
        if (!headersParsed) {
            headers = getCSVReader().readNext();
            headersParsed = true;
        }

        return convertToMappedData(headers, getCSVReader().readNext());
    }

    @Override
    public void init(ISingleFileConfiguration configuration) throws LoaderException {
        super.init(configuration);
        csvReader = initializeCSVReader(getReader());

        headersParsed = false;
    }

    protected CSVReader initializeCSVReader(InputStreamReader reader) {
        return new CSVReader(reader);
    }

    protected CSVReader getCSVReader() {
        return csvReader;
    }

    protected IMappedStringData convertToMappedData(String[] headers, String[] values) {
        MappedStringData result = new MappedStringData();

        for (int i = 0; i < headers.length; i++) {
            String value = null;

            if (i < values.length) {
                value = values[i];
            }

            result.put(headers[i], value);
        }

        return result;
    }

}
