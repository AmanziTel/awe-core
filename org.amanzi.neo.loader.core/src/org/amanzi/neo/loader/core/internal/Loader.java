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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.loader.core.IData;
import org.amanzi.neo.loader.core.exception.LoaderException;
import org.amanzi.neo.loader.core.parser.IParser;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.loader.core.validator.IValidationResult;
import org.amanzi.neo.loader.core.validator.IValidator;
import org.amanzi.neo.models.exceptions.ModelException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class Loader<C extends IConfiguration, D extends IData> {

    private IValidator<C> validator;

    private IParser<C, D> parser;

    private final List<ISaver<C, D>> savers = new ArrayList<ISaver<C, D>>();

    public void init(C configuration) throws LoaderException {
        parser.init(configuration);

        for (ISaver<C, D> saver : savers) {
            saver.init(configuration);
        }
    }

    public void run(IProgressMonitor monitor) throws ModelException {
        parser.setProgressMonitor(monitor);

        try {
            while (parser.hasNext()) {
                D data = parser.next();

                for (ISaver<C, D> saver : savers) {
                    saver.save(data);
                }
            }
        } finally {
            finishUp();
        }
    }

    public void setValidator(IValidator<C> validator) {
        this.validator = validator;
    }

    public void setParser(IParser<C, D> parser) {
        this.parser = parser;
    }

    public void addSaver(ISaver<C, D> saver) {
        this.savers.add(saver);
    }

    public IValidationResult validate(C configuration) {
        return validator.validate(configuration);
    }

    public boolean isAppropriate(List<File> filesToLoad) {
        IValidationResult result = validator.appropriate(filesToLoad);

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
        }
    }
}
