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

package org.amanzi.neo.loader.core.parser.impl.internal;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.amanzi.neo.loader.core.IData;
import org.amanzi.neo.loader.core.ISingleFileConfiguration;
import org.amanzi.neo.loader.core.exception.LoaderException;
import org.amanzi.neo.loader.core.exception.impl.FileNotFoundException;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractStreamParser<C extends ISingleFileConfiguration, D extends IData> extends AbstractParser<C, D> {

    private InputStream stream;

    private InputStreamReader reader;

    @Override
    public void init(C configuration) throws LoaderException {
        super.init(configuration);
        stream = initializeStream(configuration);
    }

    protected InputStream getStream() {
        return stream;
    }

    protected InputStreamReader getReader() {
        if (reader == null) {
            reader = initializeReader(getStream());
        }

        return reader;
    }

    protected InputStream initializeStream(C configuration) throws LoaderException {
        try {
            return new FileInputStream(configuration.getFile());
        } catch (Exception e) {
            throw new FileNotFoundException(configuration.getFile());
        }
    }

    protected InputStreamReader initializeReader(InputStream stream) {
        return new InputStreamReader(stream);
    }

}
