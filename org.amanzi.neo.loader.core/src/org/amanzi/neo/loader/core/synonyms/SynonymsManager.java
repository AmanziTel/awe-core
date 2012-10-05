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

package org.amanzi.neo.loader.core.synonyms;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.loader.core.synonyms.Synonyms.SynonymType;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class SynonymsManager {

    private static final Logger LOGGER = Logger.getLogger(SynonymsManager.class);

    private static final String SYNONYMS_EXTENSION_ID = "org.amanzi.loaderSynonyms";

    private static final String SYNONYMS_FILE_ATTRIBUTE = "synonymsFile";

    private static final String HEADERS_SEPARATOR = ",";

    private static final Pattern SYNONYM_KEY_PATTERN = Pattern
            .compile("(([a-zA-Z_0-9]+)\\.){1}([a-zA-Z_0-9]+){1}(@([a-zA-Z]+))?(!)?");

    private static final int NODETYPE_GROUP_INDEX = 2;

    private static final int PROPERTY_GROUP_INDEX = 3;

    private static final int CLASS_GROUP_INDEX = 5;

    private static final int IS_MANDATORY_GROUP_INDEX = 6;

    private static class SynonymsManagerInstanceHolder {
        private static volatile SynonymsManager instance = new SynonymsManager();
    }

    private final Map<String, Map<INodeType, List<Synonyms>>> synonymsCache = new HashMap<String, Map<INodeType, List<Synonyms>>>();

    private final IExtensionRegistry registry;

    private final Map<String, List<URL>> resources = new HashMap<String, List<URL>>();

    protected SynonymsManager(final IExtensionRegistry registry) {
        this.registry = registry;

        initializeSynonymsSources();
    }

    private SynonymsManager() {
        this(Platform.getExtensionRegistry());
    }

    public static SynonymsManager getInstance() {
        return SynonymsManagerInstanceHolder.instance;
    }

    private void initializeSynonymsSources() {
        for (IConfigurationElement singleSynonymResource : registry.getConfigurationElementsFor(SYNONYMS_EXTENSION_ID)) {
            URL url = getResource(singleSynonymResource);
            String name = FilenameUtils.getBaseName(url.getFile());

            List<URL> urlList = resources.get(name);
            if (urlList == null) {
                urlList = new ArrayList<URL>();
                resources.put(name, urlList);
            }

            if (!urlList.contains(url)) {
                urlList.add(url);
            }
        }
    }

    private URL getResource(final IConfigurationElement resouceElement) {
        String pluginId = resouceElement.getContributor().getName();
        Bundle bundle = Platform.getBundle(pluginId);

        String resourcePath = resouceElement.getAttribute(SYNONYMS_FILE_ATTRIBUTE);

        return bundle.getResource(resourcePath);
    }

    protected synchronized Map<INodeType, List<Synonyms>> initializeSynonymsCache(final String dataType) {
        HashMap<INodeType, List<Synonyms>> result = new HashMap<INodeType, List<Synonyms>>();

        List<URL> urlList = resources.get(dataType);
        if (urlList != null) {
            for (URL singleURL : urlList) {
                try {
                    Map<INodeType, List<Synonyms>> synonyms = loadSynonyms(singleURL.openStream());

                    for (Entry<INodeType, List<Synonyms>> synonymsEntry : synonyms.entrySet()) {
                        List<Synonyms> synonymsList = result.get(synonymsEntry.getKey());
                        if (synonymsList == null) {
                            synonymsList = new ArrayList<Synonyms>();

                            result.put(synonymsEntry.getKey(), synonymsList);
                        }
                        synonymsList.addAll(synonymsEntry.getValue());
                    }

                } catch (IOException e) {
                    LOGGER.error("Unable to load Synonyms", e);
                }
            }
        }

        return result;
    }

    protected Map<INodeType, List<Synonyms>> loadSynonyms(final InputStream stream) throws IOException {
        Map<INodeType, List<Synonyms>> result = new HashMap<INodeType, List<Synonyms>>();

        Properties properties = new Properties();
        properties.load(stream);

        for (Entry<Object, Object> propertyEntry : properties.entrySet()) {
            Pair<INodeType, Synonyms> pair = parseSynonyms(propertyEntry);

            List<Synonyms> synonymsList = result.get(pair.getLeft());
            if (synonymsList == null) {
                synonymsList = new ArrayList<Synonyms>();

                result.put(pair.getKey(), synonymsList);
            }
            synonymsList.add(pair.getRight());
        }

        return result;
    }

    protected Pair<INodeType, Synonyms> parseSynonyms(final Entry<Object, Object> propertyEntry) {
        // convert to string
        String keyPart = propertyEntry.getKey().toString();
        String valuePart = propertyEntry.getValue().toString();

        // get headers
        String[] headers = valuePart.split(HEADERS_SEPARATOR);

        // get subtype, property and class
        Matcher matcher = SYNONYM_KEY_PATTERN.matcher(keyPart);
        if (matcher.matches()) {
            String nodeTypeLine = matcher.group(NODETYPE_GROUP_INDEX);
            String propertyName = matcher.group(PROPERTY_GROUP_INDEX);
            String clazz = matcher.group(CLASS_GROUP_INDEX);
            String isMandatoryGroup = matcher.group(IS_MANDATORY_GROUP_INDEX);

            SynonymType synonymsType = clazz == null ? SynonymType.UNKOWN : SynonymType.findByClass(clazz);

            boolean isMandatory = !StringUtils.isEmpty(isMandatoryGroup);

            Synonyms synonyms = new Synonyms(propertyName, synonymsType, isMandatory, headers);

            try {
                INodeType nodeType = NodeTypeManager.getInstance().getType(nodeTypeLine);

                return new ImmutablePair<INodeType, Synonyms>(nodeType, synonyms);
            } catch (Exception e) {
                LOGGER.error("Error on parsing node type", e);
            }
        }

        LOGGER.error("Synonyms key <" + keyPart + "> doesn't match pattern");

        return null;
    }

    @SuppressWarnings("unchecked")
    public List<Synonyms> getSynonyms(final String synonymsType, final INodeType nodeType) {
        List<Synonyms> result = getSynonyms(synonymsType).get(nodeType);

        if (result == null) {
            result = ListUtils.EMPTY_LIST;
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public void updateSynonyms(final String synonymsType, final INodeType nodeType, final String propertyName,
            final String[] updatedSynonyms) {
        List<Synonyms> previousList = getSynonyms(synonymsType, nodeType);

        for (Synonyms singleSynonym : previousList) {
            if (singleSynonym.getPropertyName().equals(propertyName)) {
                Collection<String> previousSynonyms = Arrays.asList(singleSynonym.getPossibleHeaders());
                Collection<String> newSynonyms = Arrays.asList(updatedSynonyms);

                Collection<String> resultedSynonyms = CollectionUtils.union(previousSynonyms, newSynonyms);

                singleSynonym.setPossibleHeaders(resultedSynonyms.toArray(new String[resultedSynonyms.size()]));
            }
        }
    }

    public Map<INodeType, List<Synonyms>> getSynonyms(final String synonymsType) {
        Map<INodeType, List<Synonyms>> subTypeSynonyms = synonymsCache.get(synonymsType);

        if (subTypeSynonyms == null) {
            subTypeSynonyms = initializeSynonymsCache(synonymsType);

            synonymsCache.put(synonymsType, subTypeSynonyms);
        }

        return subTypeSynonyms;
    }

    protected Map<String, List<URL>> getResources() {
        return resources;
    }

}
