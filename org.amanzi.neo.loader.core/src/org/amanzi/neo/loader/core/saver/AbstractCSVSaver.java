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

package org.amanzi.neo.loader.core.saver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.parser.CSVContainer;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceManager;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataModel;
import org.amanzi.neo.services.model.IModel;
import org.apache.log4j.Logger;

/**
 * common actions for all csv savers
 * 
 * @author Vladislav_Kondratenko
 */
public abstract class AbstractCSVSaver<T1 extends IModel> extends AbstractSaver<T1, CSVContainer, ConfigurationDataImpl> {
    private static final Logger LOGGER = Logger.getLogger(AbstractCSVSaver.class);

    /**
     * minimum row size
     */
    protected static final int MINIMUM_COLUMN_NUMBER = 2;
    /**
     * maximum count of placebo commited transaction before Top-level commit
     */
    protected final int MAX_TX_BEFORE_COMMIT = 1000;
    /**
     * model used in top cases
     */
    protected T1 parametrizedModel;
    /**
     * set configuration data
     */
    protected ConfigurationDataImpl configuration;
    /**
     * line number
     */
    protected Long lineCounter = 0l;
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
    /**
     * collected parameters
     */
    protected Map<String, Object> params = new HashMap<String, Object>();
    /**
     * file headers
     */
    protected List<String> headers;
    /*
     * constants
     */
    protected static final String INCORRECT_VALUE_NULL = "NULL";
    protected static final String INCORRECT_VALUE_QUEST_SYMBOL = "?";
    protected static final String INCORRECT_VALUE_DEFAULT = "default";
    protected static final String INCORRECT_VALUE_NA = "N/A";
    protected static final String INCORRECT_VALUE_DOUBLE_DASH = "--";
    protected static final String INCORRECT_VALUE_DASH = "-";

    /**
     * check value for null or empty or String value "NULL" or "?"
     * 
     * @param value
     * @return
     */
    protected boolean isCorrect(Object value) {
        if (value == null) {
            return false;
        } else {
            String stringValue = value.toString();
            if (stringValue.isEmpty() || stringValue.equals(INCORRECT_VALUE_QUEST_SYMBOL)
                    || stringValue.equalsIgnoreCase(INCORRECT_VALUE_NULL) || stringValue.equalsIgnoreCase(INCORRECT_VALUE_DEFAULT)
                    || stringValue.equalsIgnoreCase(INCORRECT_VALUE_DOUBLE_DASH)
                    || stringValue.equalsIgnoreCase(INCORRECT_VALUE_DASH) || stringValue.equalsIgnoreCase(INCORRECT_VALUE_NA)) {
                return false;
            }
        }
        return true;
    }

    /**
     * collect synonyms from element properties
     * 
     * @param nodeType
     * @param collectedName
     */
    protected void addSynonyms(IDataModel model, Map<String, Object> collectedName) {
        String stringType = collectedName.get(AbstractService.TYPE).toString();
        INodeType type = NodeTypeManager.getType(stringType);
        for (String name : collectedName.keySet()) {
            String headerName = getHeaderBySynonym(name);
            if (headerName != null && !name.equals(AbstractService.NAME) && !name.equals(AbstractService.TYPE)) {
                addDatasetSynonyms(model, type, headerName, name);
            } else if (name.equals(AbstractService.NAME)) {
                headerName = getHeaderBySynonym(stringType);
                if (headerName != null) {
                    addDatasetSynonyms(model, type, AbstractService.NAME, headerName);
                }
            }

        }
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) throws AWEException {
        super.init(configuration, dataElement);
        this.configuration = configuration;
        clearHeaders();
        preferenceStoreSynonyms = initializeSynonyms();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        try {
            initializeNecessaryModels();
            useableModels.add((IDataModel)parametrizedModel);
            createExportSynonymsForModels();
        } catch (Exception e) {
            rollbackTx();
            AweConsolePlugin.error("Exception on creating root Model");
            LOGGER.info("Exception on creating root Model", e);
            throw new DatabaseException(e);
        }
    }

    protected void clearHeaders() {
        if (headers != null && !headers.isEmpty()) {
            headers.clear();
        }
    }

    /**
     * return preference store synonyms
     * 
     * @return
     */
    protected abstract Map<String, String[]> initializeSynonyms();

    /**
     * initialize necessary models
     * 
     * @return model used in top cases(parametrized model)
     * @throws AWEException
     */
    protected abstract void initializeNecessaryModels() throws AWEException;

    /**
     * get synonym row value and autoparse it
     * 
     * @param synonym
     * @param value
     * @return
     */
    protected Object getSynonymValueWithAutoparse(String synonym, List<String> value) {
        Object findedValue = getValueFromRow(synonym, value);
        if (findedValue == null) {
            return null;
        } else
            return autoParse(synonym.toLowerCase(), findedValue.toString());
    }

    @Override
    public void saveElement(CSVContainer dataElement) throws AWEException {
        try {
            commitTx();
            commonLinePreparationActions(dataElement);
            if (handleHeaders(dataElement)) {
                handleLine(dataElement);
            }
        } catch (DatabaseException e) {
            AweConsolePlugin.exception(e);
            LOGGER.error("Error while saving element on line " + lineCounter, e);
            rollbackTx();
            throw new DatabaseException(e);
        } catch (Exception e) {
            AweConsolePlugin.info("Exception while saving element on line " + lineCounter);
            LOGGER.info("Exception while saving element on line " + lineCounter, e);
            commitTx();
        }
    }

    /**
     * common prepare action
     * 
     * @param dataElement
     * @throws Exception
     */
    protected abstract void commonLinePreparationActions(CSVContainer dataElement) throws Exception;

    /**
     * handle line actions
     * 
     * @param dataElement
     * @throws AWEException
     */
    protected void handleLine(CSVContainer dataElement) throws AWEException {
        lineCounter++;
        List<String> value = dataElement.getValues();
        saveLine(value);

    }

    /**
     * intialize headers
     * 
     * @throws Exception
     */
    protected boolean handleHeaders(CSVContainer dataElement) throws Exception {
        if (fileSynonyms.isEmpty()) {
            headers = dataElement.getHeaders();
            makeAppropriationWithSynonyms(headers);
            makeIndexAppropriation();
            lineCounter++;
            return false;
        }
        return !fileSynonyms.isEmpty();
    }

    /**
     * save parsed line from csv container
     * 
     * @param value
     * @throws AWEException
     */
    protected abstract void saveLine(List<String> value) throws AWEException;

    /**
     * get value from row without autoparse (like a string)
     * 
     * @param synonym
     * @param value
     * @return
     */
    protected String getValueFromRow(String synonym, List<String> value) {
        String requiredHeader = checkHeaderInSynonyms(synonym);
        return isCorrect(synonym, value) ? getSynonymValue(value, requiredHeader) : null;
    }

    /**
     * check if header contains in synonyms
     * 
     * @return synonym if contains
     */
    private String checkHeaderInSynonyms(String header) {
        String result = fileSynonyms.get(header);

        if (result == null) {
            result = fileSynonyms.get(header.toLowerCase());
            if (result == null) {
                result = header;
            }
        }

        return result;
    }

    /**
     * check if row value is correct
     * 
     * @param synonymName
     * @param row
     * @return
     */
    protected boolean isCorrect(String synonymName, List<String> row) {
        String requiredHeader = checkHeaderInSynonyms(synonymName);
        Integer headerId = columnSynonyms.get(requiredHeader);
        return headerId != null && row != null && isCorrect(row.get(headerId));
    }

    /**
     * get header name by synonymVale
     * 
     * @param synonymName
     * @return
     */
    protected String getHeaderBySynonym(String synonymName) {
        if (fileSynonyms.containsKey(synonymName)) {
            return headers.get(columnSynonyms.get((fileSynonyms.get(synonymName))));
        }
        return null;
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
        return header;
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

    /**
     * return header number by header name
     * 
     * @param header
     * @return
     */
    private int getHeaderId(String header) {
        return headers.indexOf(header);
    }

    /**
     * make appropriation with headers and them indexes
     */
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
     * @param keySet - header files;
     */
    protected void makeAppropriationWithSynonyms(List<String> keySet) {
        boolean isAppropriation = false;
        for (String header : keySet) {
            for (String posibleHeader : preferenceStoreSynonyms.keySet()) {
                for (String mask : preferenceStoreSynonyms.get(posibleHeader)) {
                    if (header.toLowerCase().matches(mask.toLowerCase()) || header.toLowerCase().equals(mask.toLowerCase())) {
                        for (String key : posibleHeader.split(DataLoadPreferenceManager.INFO_SEPARATOR)) {
                            if (getSubType() == null || key.equalsIgnoreCase(getSubType())) {
                                isAppropriation = true;
                                String name = posibleHeader.substring(0,
                                        posibleHeader.indexOf(DataLoadPreferenceManager.INFO_SEPARATOR));
                                if (!fileSynonyms.containsKey(name)) {
                                    fileSynonyms.put(name, header);
                                    break;
                                }
                            }
                        }
                    }
                }
                if (isAppropriation) {
                    isAppropriation = false;
                    break;
                }
            }
        }
    }

    /**
     * @return subtype of dataset or null if not exist
     */
    protected abstract String getSubType();

    /**
     * get synonym row value and autoparse it
     * 
     * @param synonym
     * @param value
     * @return
     */
    protected Object getSynonymValuewithAutoparse(String synonym, List<String> value) {
        return isCorrect(synonym, value) ? autoParse(synonym, getValueFromRow(synonym, value)) : null;
    }
}
