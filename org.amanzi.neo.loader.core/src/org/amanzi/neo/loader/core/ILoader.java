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

import java.io.File;
import java.util.List;

import org.amanzi.neo.loader.core.exception.LoaderException;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.validator.IValidationResult;
import org.amanzi.neo.models.exceptions.ModelException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 * @param <C>
 * @param <D>
 */
public interface ILoader<C extends IConfiguration, D extends IData> {

    public abstract void init(C configuration) throws LoaderException;

    public abstract void run(IProgressMonitor monitor) throws ModelException;

    public abstract IValidationResult validate(C configuration);

    public abstract boolean isAppropriate(List<File> filesToLoad);

}