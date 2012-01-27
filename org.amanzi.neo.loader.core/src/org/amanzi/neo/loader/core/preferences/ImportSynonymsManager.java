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

package org.amanzi.neo.loader.core.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import org.amanzi.neo.loader.core.internal.NeoLoaderPlugin;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author lagutko_n
 * @since 1.0.0
 */
public class ImportSynonymsManager {
    
    private static final String ALL_SUBTYPES_NAME = "[ALL]";
    
    private static final int DATASET_TYPE_INDEX = 0;
    
    private static final int NODE_TYPE_OFFSET = 2;
    
    private static final int PROPERTY_NAME_OFFSET = 1;
    
    private static final int SUBTYPE_INDEX = 1;
    
    private static final String KEY_PART_SEPARATOR = "\\.";
    
    private static final String TYPE_PART_SEPARATOR = "@";
    
    private static final int KEY_PARTS_SIZE_WITHOUT_SUBTYPE = 3;
    
    private static final int KEY_PARTS_SIZE_WITHOUT_NODE_TYPE = 2;
    
    private static abstract class SynonymsCache<K, V> extends HashMap<K, V> {
        
        /** long serialVersionUID field */
        private static final long serialVersionUID = 3906501278563655847L;

        @SuppressWarnings("unchecked")
        @Override
        public V get(Object key) {
            if (key == null) {
                key = ALL_SUBTYPES_NAME;
            }
            
            V result = super.get(key);
            
            if (result == null) {
                result = createInstance();
                super.put((K)key, result);
            }
            
            return result;
        }
        
        protected abstract V createInstance();
        
    }
    
    public static class Synonym {
        
        private PossibleTypes type;
        
        private String name;
        
        /**
         * @param type
         * @param name
         */
        public Synonym(String name, PossibleTypes type) {
            super();
            this.type = type;
            this.name = name;
        }        
        
        public Synonym(String name) {
            this(name, PossibleTypes.AUTO);
        }

        /**
         * @return Returns the type.
         */
        public PossibleTypes getType() {
            return type;
        }
        
        public void setType(PossibleTypes type) {
            this.type = type;
        }

        /**
         * @return Returns the name.
         */
        public String getName() {
            return name;
        }
        
        @Override
        public boolean equals(Object anotherObject) {
            if (anotherObject != null && anotherObject instanceof Synonym) {
                Synonym anotherSynonym = (Synonym)anotherObject;
                
                return anotherSynonym.getName().equals(getName());
                       
            }
            
            return false;
        }
        
        @Override
        public int hashCode() {
            return name.hashCode();
        }
        
        @Override
        public String toString() {
            return getName() + " <" + getType() + ">";
        }
        
    }
    
    public static class PropertySynonyms extends SynonymsCache<Synonym, String[]> {

        /** long serialVersionUID field */
        private static final long serialVersionUID = 8952149724511738716L;

        @Override
        protected String[] createInstance() {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        
    }
    
    
    public static class NodeTypeSynonyms extends SynonymsCache<String, PropertySynonyms> {

        /** long serialVersionUID field */
        private static final long serialVersionUID = 2345318175891881387L;
        
        @Override
        protected PropertySynonyms createInstance() {
            return new PropertySynonyms();
        }
        
    }
    
    private static class SubTypeSynonyms extends SynonymsCache<String, NodeTypeSynonyms> {
        
        /** long serialVersionUID field */
        private static final long serialVersionUID = -1718856437030398550L;

        @Override
        protected NodeTypeSynonyms createInstance() {
            return new NodeTypeSynonyms();
        }
        
    }
    
    private static class DatasetSynonyms extends SynonymsCache<String, SubTypeSynonyms> {

        /** long serialVersionUID field */
        private static final long serialVersionUID = -7093388520941469288L;

        @Override
        protected SubTypeSynonyms createInstance() {
            return new SubTypeSynonyms();
        }
        
    }
    
    private DatasetSynonyms synonymsCache = new DatasetSynonyms();
    
    private IPreferenceStore preferenceStore = NeoLoaderPlugin.getDefault().getPreferenceStore();
    
    private static ImportSynonymsManager instance;
    
    public static synchronized ImportSynonymsManager getManager() {
        if (instance == null) {
            instance = new ImportSynonymsManager();
        }
        
        return instance;
    }
    
    private ImportSynonymsManager() {
        initializeDefaultSynonyms();
    }
    
    private void initializeDefaultSynonyms() {
        String synonymKeys = preferenceStore.getString(SynonymsInitializer.SYNONYM_KEYS);
        
        String[] keyArray = synonymKeys.split(SynonymsInitializer.SEPARATOR);
        for (String singleKey : keyArray) {
            initializeSynonym(singleKey, preferenceStore.getString(singleKey));
        }
    }
    
    private void initializeSynonym(String key, String synonyms) {
        initializeSynonym(key, synonyms, false);
    }
    
    public void updateSynonyms(String key, String synonyms) { 
        initializeSynonym(key, synonyms, true);
    }
    
    private void initializeSynonym(String key, String synonyms, boolean updatePreferenceStore) {
        String[] typeParts = key.split(TYPE_PART_SEPARATOR);
        
        PossibleTypes dataType = PossibleTypes.AUTO;
        if (typeParts.length > 1) {
            String sDataType = typeParts[1].toUpperCase();
            dataType = PossibleTypes.valueOf(sDataType);
        }
        
        key = typeParts[0];
        
        String[] keyParts = key.split(KEY_PART_SEPARATOR);
        int keyPartsSize = keyParts.length;
        
        String datasetType = keyParts[DATASET_TYPE_INDEX];
        String propertyName = keyParts[keyPartsSize - PROPERTY_NAME_OFFSET];
        String nodeType = null;
        String subType = null;
        
        if (keyPartsSize > KEY_PARTS_SIZE_WITHOUT_NODE_TYPE) {
            nodeType = keyParts[keyPartsSize - NODE_TYPE_OFFSET];
        
            if (keyPartsSize > KEY_PARTS_SIZE_WITHOUT_SUBTYPE) {
                subType = keyParts[SUBTYPE_INDEX];
            }
        }
        
        String[] synonymsArray = synonyms.split(SynonymsInitializer.SEPARATOR);
        
        Synonym synonym = new Synonym(propertyName, dataType);
        
        String[] possibleHeaders = getPropertySynonyms(datasetType, subType, nodeType).get(synonym);
        synonymsArray = splitPossibleHeaders(possibleHeaders, synonymsArray);
        
        synonymsCache.get(datasetType).get(subType).get(nodeType).put(synonym, synonymsArray);
        
        if (updatePreferenceStore) {
            preferenceStore.setValue(key, Arrays.toString(synonymsArray));
        }
    }
    
    public PropertySynonyms getPropertySynonyms(String datasetType, String subType, String nodeType) {
        return synonymsCache.get(datasetType).get(subType).get(nodeType);
    }
    
    public NodeTypeSynonyms getNodeTypeSynonyms(String datasetType, String subType) {
        return synonymsCache.get(datasetType).get(subType);
    }    
    
    private String[] splitPossibleHeaders(String[] possibleHeaders, String[] newHeaders) {
        String[] result = Arrays.copyOf(possibleHeaders, possibleHeaders.length);
        
        for (String newHeader : newHeaders) {
            if (possibleHeaders.length > 0) {
                for (String possibleHeader : possibleHeaders) {
                    if (!newHeader.toLowerCase().matches(possibleHeader.toLowerCase())) {
                        result = (String[])ArrayUtils.add(result, newHeader);
                        break;
                    }
                }
            } else {
                result = (String[])ArrayUtils.add(result, newHeader);
            }
        }
        
        return result;
    }
    
    public void addFromFile(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        
        for (Entry<Object, Object> synonymEntry : properties.entrySet()) {
            initializeSynonym(synonymEntry.getKey().toString(), synonymEntry.getValue().toString(), true);
        }
    }
    
    public void addFromFile(File propertiesFile) throws IOException {
        addFromFile(new FileInputStream(propertiesFile));
    }
}

