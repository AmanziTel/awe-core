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

import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.parser.IParser;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.loader.core.validator.IValidationResult;
import org.amanzi.neo.loader.core.validator.IValidator;
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

	void init(C configuration);

	void run(IProgressMonitor monitor);

	IValidationResult validate(C configuration);

	boolean isAppropriate(C configuration);

	void setValidator(final IValidator<C> validator);

	void setParser(final IParser<C, D> parser);

	void addSaver(final ISaver<C, D> saver);

	String getName();

	void setName(String name);
}