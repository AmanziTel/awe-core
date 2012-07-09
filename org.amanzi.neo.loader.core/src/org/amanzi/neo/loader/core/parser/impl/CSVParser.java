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

import java.io.InputStreamReader;

import org.amanzi.neo.loader.core.IMappedStringData;
import org.amanzi.neo.loader.core.ISingleFileConfiguration;
import org.amanzi.neo.loader.core.exception.LoaderException;
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

    @Override
    protected IMappedStringData parseNextElement() {
        return null;
    }

    @Override
    public void init(ISingleFileConfiguration configuration) throws LoaderException {
        super.init(configuration);
        csvReader = initializeCSVReader(getReader());
    }

    protected CSVReader initializeCSVReader(InputStreamReader stream) {
        return new CSVReader(stream);
    }

    protected CSVReader getCSVReader() {
        return csvReader;
    }
}
