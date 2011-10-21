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

import java.util.List;

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.ILoaderInfo;
import org.amanzi.neo.loader.core.ILoaderNew;
import org.amanzi.neo.loader.core.ILoaderProgressListener;
import org.amanzi.neo.loader.core.IValidator;
import org.amanzi.neo.loader.core.newparser.IParser;
import org.amanzi.neo.loader.core.newsaver.IData;
import org.amanzi.neo.loader.core.newsaver.ISaver;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IModel;

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
     * contain some information about loader such as loader name loader type and loader datatype
     */
    ILoaderInfo info;
    
    /**
     * saver for current Loader
     */
    List<ISaver<? extends IModel, IData, IConfiguration>> saver;
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
    public void setSaver(List<ISaver<? extends IModel, IData, IConfiguration>> saver) {
        this.saver = saver;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void setParser(IParser parser) {
        this.parser = parser;
    }

    @Override
    public void run() throws AWEException {
        parser.run();
    }

    @Override
    public void setValidator(IValidator validator) {
        this.validator = validator;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void init(IConfiguration config) {
        for (ISaver saverMem : saver) {
            saverMem.init(config, null);
        }
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

    @Override
    public void addProgressListener(ILoaderProgressListener listener) {
        parser.addProgressListener(listener);
    }

    @Override
    public void removeProgressListener(ILoaderProgressListener listener) {
        parser.removeProgressListener(listener);
    }

}
