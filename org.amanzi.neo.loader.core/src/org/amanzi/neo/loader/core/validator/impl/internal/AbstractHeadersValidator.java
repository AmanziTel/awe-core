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

package org.amanzi.neo.loader.core.validator.impl.internal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.internal.Messages;
import org.amanzi.neo.loader.core.synonyms.Synonyms;
import org.amanzi.neo.loader.core.synonyms.SynonymsManager;
import org.amanzi.neo.loader.core.synonyms.SynonymsUtils;
import org.amanzi.neo.loader.core.validator.IValidationResult;
import org.amanzi.neo.loader.core.validator.IValidationResult.Result;
import org.amanzi.neo.loader.core.validator.ValidationResult;
import org.amanzi.neo.nodetypes.INodeType;

import au.com.bytecode.opencsv.CSVReader;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractHeadersValidator<T extends IConfiguration> extends AbstractValidator<T> {

    private final Map<INodeType, List<Synonyms>> mandatorySynonyms = new HashMap<INodeType, List<Synonyms>>();

    protected AbstractHeadersValidator() {
        loadSynonyms();
    }

    @Override
    protected IValidationResult checkFileContents(T configuration) {
        for (File singleFile : getFilesFromConfiguration(configuration)) {
            try {
                List<String> errors = checkFile(singleFile);

                if (!errors.isEmpty()) {
                    return new ValidationResult(Result.FAIL, Messages.format(Messages.AbstractHeadersValidator_SynonymsFailed,
                            singleFile.getName(), errors.toString()));
                }
            } catch (IOException e) {
                return new ValidationResult(Result.FAIL, Messages.format(Messages.AbstractHeadersValidator_IOError,
                        e.getLocalizedMessage()));
            }
        }

        return IValidationResult.SUCCESS;
    }

    protected abstract List<File> getFilesFromConfiguration(T configuration);

    protected abstract String getSynonyms();

    private List<String> checkFile(File file) throws IOException {
        List<String> failedSynonyms = new ArrayList<String>();

        String[] headers = getHeadersArray(file);

        for (Entry<INodeType, List<Synonyms>> entry : mandatorySynonyms.entrySet()) {
            INodeType nodeType = entry.getKey();

            for (Synonyms synonym : entry.getValue()) {
                if (!SynonymsUtils.checkHeaders(synonym, headers)) {
                    failedSynonyms.add(nodeType.getId() + "." + synonym.getPropertyName()); //$NON-NLS-1$
                }
            }
        }

        return failedSynonyms;
    }

    protected String[] getHeadersArray(File file) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(file), '\t');

        try {
            return reader.readNext();
        } finally {
            reader.close();
        }
    }

    private void loadSynonyms() {
        for (Entry<INodeType, List<Synonyms>> synonymsEntry : SynonymsManager.getInstance().getSynonyms(getSynonyms()).entrySet()) {
            List<Synonyms> synonymsList = new ArrayList<Synonyms>();

            for (Synonyms synonym : synonymsEntry.getValue()) {
                if (synonym.isMandatory()) {
                    synonymsList.add(synonym);
                }
            }

            mandatorySynonyms.put(synonymsEntry.getKey(), synonymsList);
        }
    }

}
