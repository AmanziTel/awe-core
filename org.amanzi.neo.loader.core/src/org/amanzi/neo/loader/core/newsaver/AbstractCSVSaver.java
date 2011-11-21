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

package org.amanzi.neo.loader.core.newsaver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceManager;
import org.amanzi.neo.services.model.IModel;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * @author Vladislav_Kondratenko
 */
public abstract class AbstractCSVSaver<T1 extends IModel> extends AbstractSaver<T1, CSVContainer, ConfigurationDataImpl> {
    /**
     * contains appropriation of header synonyms and name inDB
     * <p>
     * <b>key</b>- name in db ,<br>
     * <b>value</b>-file header key
     * </p>
     */
    protected Map<String, String> fileSynonyms = new HashMap<String, String>();
    /**
     * name inDB properties values
     */
    protected Map<String, Integer> columnSynonyms = new HashMap<String, Integer>();
    protected Map<String, Object> params = new HashMap<String, Object>();
    protected List<String> headers;

    /**
     * check value for null or empty or String value "NULL" or "?"
     * 
     * @param value
     * @return
     */
    protected boolean isCorrect(Object value) {
        if (value == null || value.toString().isEmpty() || value.toString().equals("?")
                || value.toString().equalsIgnoreCase("NULL") || value.toString().equalsIgnoreCase("default")
                || value.toString().equalsIgnoreCase("--")) {
            return false;
        }
        return true;
    }

    /**
     * @param service
     */
    public AbstractCSVSaver(GraphDatabaseService service) {
        super(service);
    }

    /**
     * 
     */
    public AbstractCSVSaver() {
        super();
    }

    /**
     * get synonym row value and autoparse it
     * 
     * @param synonym
     * @param value
     * @return
     */
    protected Object getSynonymValuewithAutoparse(String synonym, List<String> value) {
        Object findedValue = getValueFromRow(synonym, value);
        if (findedValue == null) {
            return null;
        } else
            return autoParse(synonym, findedValue.toString());
    }

    /**
     * get value from row without autoparse (like a string)
     * 
     * @param synonym
     * @param value
     * @return
     */
    protected String getValueFromRow(String synonym, List<String> value) {
        String requiredHeader = synonym;
        if (fileSynonyms.containsKey(synonym)) {
            requiredHeader = fileSynonyms.get(synonym);
        }
        return isCorrect(synonym, value) ? getSynonymValue(value, requiredHeader) : null;
    }

    /**
     * check if row value is correct
     * 
     * @param synonymName
     * @param row
     * @return
     */
    protected boolean isCorrect(String synonymName, List<String> row) {
        String requiredHeader = synonymName;
        if (fileSynonyms.containsKey(synonymName)) {
            requiredHeader = fileSynonyms.get(synonymName);
        }
        return requiredHeader != null && columnSynonyms.containsKey(requiredHeader) && row != null
                && isCorrect(row.get(columnSynonyms.get(requiredHeader)));
    }

    /**
     * get header name by synonymVale
     * 
     * @param synonymName
     * @return
     */
    protected String getHeaderBySynonym(String synonymName) {
        return headers.get(columnSynonyms.get((fileSynonyms.get(synonymName))));
    }

    /**
     * return synonym for header
     * 
     * @param header
     * @return
     */
    protected String getSynonymForHeader(String header) {
        for (String key : fileSynonyms.keySet()) {
            if (fileSynonyms.get(key).equals(header)) {
                return key;
            }
        }
        return null;
    }

    /**
     * Get row value by header
     * 
     * @param row
     * @param synonym
     * @return synonym value
     */
    private String getSynonymValue(List<String> row, String propertyName) {
        return row.get(columnSynonyms.get(propertyName));
    }

    /**
     * Null synonym value
     * 
     * @param row
     * @param synonymssaedwd
     */
    protected void resetRowValueBySynonym(List<String> row, String synonym) {
        row.set(columnSynonyms.get(fileSynonyms.get(synonym)), null);
    }

    protected int getHeaderId(String header) {
        return headers.indexOf(header);
    }

    protected void makeIndexAppropriation() {
        for (String synonyms : fileSynonyms.keySet()) {
            columnSynonyms.put(fileSynonyms.get(synonyms), getHeaderId(fileSynonyms.get(synonyms)));
        }
        for (String head : headers) {
            if (!columnSynonyms.containsKey(head)) {
                columnSynonyms.put(head, getHeaderId(head));
            }
        }
    }

    /**
     * make Appropriation with default synonyms and file header
     * 
     * @param keySet -header files;
     */
    protected void makeAppropriationWithSynonyms(List<String> keySet) {
        boolean isAppropriation = false;
        for (String header : keySet) {
            for (String posibleHeader : preferenceStoreSynonyms.keySet()) {
                for (String mask : preferenceStoreSynonyms.get(posibleHeader)) {
                    if (header.toLowerCase().matches(mask.toLowerCase())) {
                        isAppropriation = true;
                        String name = posibleHeader.substring(0, posibleHeader.indexOf(DataLoadPreferenceManager.INFO_SEPARATOR));
                        fileSynonyms.put(name, header);
                        break;
                    }
                }
                if (isAppropriation) {
                    isAppropriation = false;
                    break;
                }
            }
        }
    }
}
