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

package org.amanzi.neo.loader.ui.loaders;

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.ILoaderInfo;
import org.amanzi.neo.loader.core.ILoaderNew;
import org.amanzi.neo.loader.core.IValidator;
import org.amanzi.neo.loader.core.newparser.IParser;
import org.amanzi.neo.loader.core.newsaver.IData;
import org.amanzi.neo.loader.core.newsaver.ISaver;
import org.amanzi.neo.loader.ui.validators.AMSXMLDataValidator;

/**
 * <p>
 * class for loader setUp;
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class LoaderNew implements ILoaderNew<IData, IConfiguration> {
    /**
     * contain some information about loader such as 
     * loader name 
     * loader type
     * and loader datatype
     */
    ILoaderInfo info;
    @SuppressWarnings("rawtypes")
    /**
     * saver for current Loader
     */
    ISaver saver;
    @SuppressWarnings("rawtypes")
    /**
     * parser for current Loader
     */
    IParser parser;
    /**
     * validator for currentLoader;
     */
    IValidator validator;

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
        if (validator == null) {
            this.validator = new AMSXMLDataValidator();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(IConfiguration config) {
        saver.init(config, null);
        parser.init(config, saver);
    }

    @Override
    public IValidator getValidator() {
        return validator;
    }

    @Override
    public ILoaderInfo getLoaderInfo() {
        return info;
    }

    @Override
    public void setLoaderInfo(ILoaderInfo info) {
        this.info = info;
    }

}
