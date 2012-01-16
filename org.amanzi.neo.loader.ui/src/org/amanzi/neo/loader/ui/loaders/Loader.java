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

import org.amanzi.neo.loader.core.config.IConfiguration;
import org.amanzi.neo.loader.core.parser.IParser;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.loader.ui.validators.IValidator;
import org.amanzi.neo.services.exceptions.AWEException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * <p>
 * class for loader setUp;
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class Loader implements ILoader {
	
    /**
	 * saver for current Loader
	 */
	@SuppressWarnings("rawtypes")
    List<ISaver> savers;
	/**
	 * parser for current Loader
	 */
	@SuppressWarnings("rawtypes")
    IParser parser;
	/**
	 * validator for currentLoader;
	 */
	IValidator validator;
	
	private String loaderName;

	@SuppressWarnings("rawtypes")
    @Override
	public void setSavers(List<ISaver> savers) {
		this.savers = savers;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setParser(IParser parser) {
		this.parser = parser;
	}

	@Override
	public void run(IProgressMonitor monitor) throws AWEException {
		parser.run(monitor);
	}

	@Override
	public void setValidator(IValidator validator) {
		this.validator = validator;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
    @Override
	public void init(IConfiguration config) throws AWEException {
		for (ISaver saverMem : savers) {
			saverMem.init(config, null);
		}
		parser.init(config, savers);
	}

	@Override
	public IValidator getValidator() {
		return validator;
	}

    @Override
    public void setName(String newName) {
        loaderName = newName;
    }

    @Override
    public String getName() {
        return loaderName;
    }

}
