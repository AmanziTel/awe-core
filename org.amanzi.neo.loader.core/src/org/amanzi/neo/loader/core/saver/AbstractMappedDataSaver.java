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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.parser.MappedData;
import org.amanzi.neo.loader.core.preferences.ImportSynonymsManager;
import org.amanzi.neo.loader.core.preferences.PossibleTypes;
import org.amanzi.neo.loader.core.preferences.ImportSynonymsManager.NodeTypeSynonyms;
import org.amanzi.neo.loader.core.preferences.ImportSynonymsManager.PropertySynonyms;
import org.amanzi.neo.loader.core.preferences.ImportSynonymsManager.Synonym;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.model.IDataModel;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author lagutko_n
 * @since 1.0.0
 */
public abstract class AbstractMappedDataSaver<T1 extends IDataModel, T3 extends IConfiguration> extends AbstractSaver<T1, MappedData, T3> {
    
    private Map<INodeType, Map<String, Synonym>> synonymsCache = new HashMap<INodeType, Map<String, Synonym>>();
    
    protected Map<String, Object> getDataElementProperties(T1 model, INodeType nodeType, MappedData dataElement, boolean addNonMappedData) {
        Map<String, Synonym> synonymMapping = synonymsCache.get(nodeType);
        
        if (synonymMapping == null) {
            synonymMapping = createSynonyms(model, nodeType, dataElement.keySet(), addNonMappedData);
            synonymsCache.put(nodeType, synonymMapping);
        }
        
        HashMap<String, Object> values = new HashMap<String, Object>();
        
        for (Entry<String, String> dataEntry : dataElement.entrySet()) {
            Synonym synonym = synonymMapping.get(dataEntry.getKey());
            
            String textValue = dataEntry.getValue();
            Object value = synonym.getType().parse(textValue);
            if (synonym.getType() == PossibleTypes.AUTO) {
                PossibleTypes newType = PossibleTypes.getType(value.getClass());
                changeSynonymType(synonymMapping, dataEntry.getKey(), synonym.getName(), newType);
            }
            
            values.put(synonym.getName(), value);
        }
        
        return null;
    }
    
    private void changeSynonymType(Map<String, Synonym> synonymMapping, String header, String propertyName, PossibleTypes newType){
        Synonym newSynonym = new Synonym(propertyName, newType);
        synonymMapping.put(header, newSynonym);
    }
    
    private Map<String, Synonym> createSynonyms(T1 model, INodeType nodeType, Set<String> headerSet, boolean addNonMappedData) {
        PropertySynonyms synonyms = ImportSynonymsManager.getManager().getPropertySynonyms(getDatasetType(), getSubType(), nodeType.getId());
        
        HashMap<String, Synonym> result = new HashMap<String, Synonym>();
        
        for (String header : headerSet) {
            Synonym synonym = null;
            get_synonym: for (Entry<Synonym, String[]> synonymEntry : synonyms.entrySet()) {
                for (String possibleHeader : synonymEntry.getValue()) {
                    if (possibleHeader.matches(header)) {
                        synonym = synonymEntry.getKey();
                        
                        addDatasetSynonyms(model, nodeType, synonym.getName(), header);
                        
                        break get_synonym;
                    }
                }
            }
            
            boolean add = false;
            
            if (synonym == null && addNonMappedData && !hasSynonyms(header)  ) {
                add = true;
                synonym = new Synonym(header);
            }
            
            if (add) {
                result.put(header, synonym);
            }
        }
        
        return result;
    }
    
    private boolean hasSynonyms(String header) {
        NodeTypeSynonyms nodeTypeSynonyms = ImportSynonymsManager.getManager().getNodeTypeSynonyms(getDatasetType(), getSubType());
        
        for (PropertySynonyms propertySynonyms : nodeTypeSynonyms.values()) {
            for (String[] possibleHeadersArray : propertySynonyms.values()) {
                for (String possibleHeader : possibleHeadersArray) {
                    if (header.matches(possibleHeader)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

}
