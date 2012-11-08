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

package org.amanzi.awe.nem.export;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.models.network.INetworkModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ExportedDataContainer {
    private final INetworkModel model;
    private final Charset charset;
    private final String separator;
    private final Map<ExportedDataItems, List<SynonymsWrapper>> synonyms;
    private final String directoryPath;

    /**
     * @param model
     * @param charset
     * @param separator
     * @param directoryPath
     */
    public ExportedDataContainer(final INetworkModel model, final Charset charset, final String separator,
            final String directoryPath) {
        super();
        this.model = model;
        this.charset = charset;
        this.separator = separator;
        this.directoryPath = directoryPath;
        synonyms = new HashMap<ExportedDataItems, List<SynonymsWrapper>>();
    }

    public void addToSynonyms(final ExportedDataItems key, final List<SynonymsWrapper> synonyms) {
        this.synonyms.put(key, synonyms);
    }

    /**
     * @return Returns the charset.
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * @return Returns the directoryPath.
     */
    public String getDirectoryPath() {
        return directoryPath;
    }

    /**
     * @return Returns the model.
     */
    public INetworkModel getModel() {
        return model;
    }

    /**
     * @return Returns the separator.
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * @return Returns the synonyms.
     */
    public Map<ExportedDataItems, List<SynonymsWrapper>> getSynonyms() {
        return synonyms;
    }
}
