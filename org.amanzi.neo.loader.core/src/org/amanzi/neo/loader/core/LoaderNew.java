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

package org.amanzi.neo.loader.core;

import org.amanzi.neo.loader.core.newparser.IParser;
import org.amanzi.neo.loader.core.newsaver.IConfiguration;
import org.amanzi.neo.loader.core.newsaver.IData;
import org.amanzi.neo.loader.core.newsaver.ISaver;

/**
 * <p>
 * class for loader setUp;
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class LoaderNew implements ILoaderNew<IData, IConfiguration> {

    @SuppressWarnings("rawtypes")
    ISaver saver;
    @SuppressWarnings("rawtypes")
    IParser parser;

    @SuppressWarnings("rawtypes")
    @Override
    public void setSaver(ISaver saver) {
        this.saver = saver;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void setParser(IParser parser) {
        this.parser = parser;
    }

    @Override
    public void run() {
        parser.run();
    }

    @Override
    public void setValidator(IValidator validator) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(IConfiguration config) {
        saver.init(config, null);
        parser.init(config, saver);
    }

    @Override
    public IValidator getValidator() {
        return null;
    }

}
