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

import java.io.File;

import org.amanzi.neo.loader.core.IMappedStringData;
import org.amanzi.neo.loader.core.IMultiFileConfiguration;
import org.amanzi.neo.loader.core.ISingleFileConfiguration;
import org.amanzi.neo.loader.core.impl.SingleFileConfiguration;
import org.amanzi.neo.loader.core.parser.impl.internal.MultiStreamParser;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class MultiCSVParser
        extends
            MultiStreamParser<ISingleFileConfiguration, CSVParser, IMultiFileConfiguration, IMappedStringData> {

    @Override
    protected CSVParser createParserInstance() {
        return new CSVParser();
    }

    @Override
    protected ISingleFileConfiguration createSingleFileConfiguration(final File file, final IMultiFileConfiguration configuration) {
        SingleFileConfiguration singleFileConfiguration = new SingleFileConfiguration();
        singleFileConfiguration.setDatasetName(configuration.getDatasetName());
        singleFileConfiguration.setFile(file);
        return singleFileConfiguration;
    }

    @Override
    protected void onNewFileParsingStarted(final File file) {
        // TODO Auto-generated method stub

    }

}
