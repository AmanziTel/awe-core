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

package org.amanzi.neo.loader.core.validator.impl.network;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import org.amanzi.neo.loader.core.ISingleFileConfiguration;
import org.amanzi.neo.loader.core.internal.LoaderCorePlugin;
import org.amanzi.neo.loader.core.internal.Messages;
import org.amanzi.neo.loader.core.saver.impl.NetworkSaver;
import org.amanzi.neo.loader.core.validator.IValidationResult;
import org.amanzi.neo.loader.core.validator.IValidationResult.Result;
import org.amanzi.neo.loader.core.validator.ValidationResult;
import org.amanzi.neo.loader.core.validator.impl.internal.AbstractHeadersValidator;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NetworkValidator extends AbstractHeadersValidator<ISingleFileConfiguration> {

    private static final Logger LOGGER = Logger.getLogger(NetworkValidator.class);

    private final INetworkModelProvider networkModelProvider;

    private final IProjectModelProvider projectModelProvider;

    protected NetworkValidator(final IProjectModelProvider projectModelProvider, final INetworkModelProvider networkModelProvider) {
        super();
        this.networkModelProvider = networkModelProvider;
        this.projectModelProvider = projectModelProvider;
    }

    public NetworkValidator() {
        this(LoaderCorePlugin.getInstance().getProjectModelProvider(), LoaderCorePlugin.getInstance().getNetworkModelProvider());
    }

    @Override
    protected IValidationResult checkModelExists(final ISingleFileConfiguration configuration) {
        LOGGER.info("Validating Configuration to load Network"); //$NON-NLS-1$
        try {
            IProjectModel currentProject = projectModelProvider.getActiveProjectModel();
            if (networkModelProvider.findByName(currentProject, configuration.getDatasetName()) != null) {
                return new ValidationResult(Result.FAIL, Messages.format(Messages.NetworkValidator_DuplicatedNetworkName,
                        configuration.getDatasetName()));
            }
        } catch (ModelException e) {
            LOGGER.error("Database error on Network Validation", e); //$NON-NLS-1$
        }
        return IValidationResult.SUCCESS;
    }

    @Override
    protected Iterator<File> getFilesFromConfiguration(final ISingleFileConfiguration configuration) {
        return Arrays.asList(configuration.getFile()).iterator();
    }

    @Override
    protected String getSynonyms() {
        return NetworkSaver.SYNONYMS_TYPE;
    }

    @Override
    public String[] getSupportedFileExtensions() {
        return new String[] {"txt", "csv"};
    }

}
