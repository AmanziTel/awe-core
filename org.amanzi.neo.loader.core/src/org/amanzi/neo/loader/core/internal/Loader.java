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

package org.amanzi.neo.loader.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.loader.core.IData;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.exception.LoaderException;
import org.amanzi.neo.loader.core.exception.impl.SaverInitializationException;
import org.amanzi.neo.loader.core.parser.IParser;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.loader.core.validator.IValidationResult;
import org.amanzi.neo.loader.core.validator.IValidator;
import org.amanzi.neo.models.exceptions.ModelException;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class Loader<C extends IConfiguration, D extends IData> implements ILoader<C, D> {

    private static final Logger LOGGER = Logger.getLogger(Loader.class);

    private IValidator<C> validator;

    private IParser<C, D> parser;

    private final List<ISaver<C, D>> savers = new ArrayList<ISaver<C, D>>();

    private String loaderName;

    @Override
    public void init(final C configuration) throws LoaderException {
        parser.init(configuration);

        try {
            for (ISaver<C, D> saver : savers) {
                saver.init(configuration);
            }
        } catch (ModelException e) {
            throw new SaverInitializationException(e);
        }
    }

    @Override
    public void run(final IProgressMonitor monitor) throws LoaderException {
        long timeBefore = System.currentTimeMillis();

        parser.setProgressMonitor(monitor);

        try {
            while (parser.hasNext()) {
                D data = parser.next();

                for (ISaver<C, D> saver : savers) {
                    saver.save(data);
                    parser.addFileParsingListener(saver);
                }
            }
        } finally {
            finishUp();
        }

        LOGGER.info("Loading time = " + (System.currentTimeMillis() - timeBefore));

    }

    @Override
    public void setValidator(final IValidator<C> validator) {
        this.validator = validator;
    }

    @Override
    public void setParser(final IParser<C, D> parser) {
        this.parser = parser;
    }

    @Override
    public void addSaver(final ISaver<C, D> saver) {
        this.savers.add(saver);
    }

    @Override
    public IValidationResult validate(final C configuration) {
        IValidationResult configurationValidation = configuration.isValid();

        if (configurationValidation == IValidationResult.SUCCESS) {
            return validator.validate(configuration);
        } else {
            return configurationValidation;
        }
    }

    @Override
    public boolean isAppropriate(final C configuration) {
        IValidationResult result = validator.appropriate(configuration);

        switch (result.getResult()) {
        case FAIL:
            return false;
        default:
            return true;
        }
    }

    protected void finishUp() {
        parser.finishUp();

        for (ISaver<C, D> saver : savers) {
            saver.finishUp();
            parser.removeFileParsingListener(saver);
        }
    }

    @Override
    public String getName() {
        return loaderName;
    }

    @Override
    public void setName(final String name) {
        this.loaderName = name;
    }
}
