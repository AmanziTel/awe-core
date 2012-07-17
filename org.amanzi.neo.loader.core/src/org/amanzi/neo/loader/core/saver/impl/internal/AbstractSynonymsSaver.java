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

package org.amanzi.neo.loader.core.saver.impl.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.neo.loader.core.IMappedStringData;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.synonyms.Synonyms;
import org.amanzi.neo.loader.core.synonyms.SynonymsManager;
import org.amanzi.neo.nodetypes.INodeType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractSynonymsSaver extends AbstractSaver<IConfiguration, IMappedStringData> {

    protected static abstract class Property<C extends Object> {

        private final String headerName;

        private final String propertyName;

        public Property(String propertyName, String headerName) {
            this.propertyName = propertyName;
            this.headerName = headerName;
        }

        protected String getValue(IMappedStringData data) {
            return data.get(headerName);
        }

        protected abstract C parse(IMappedStringData data);

        public String getPropertyName() {
            return propertyName;
        }

    }

    private static final class SkippedProperty extends Property<Object> {

        /**
         * @param headerName
         * @param isReusable
         */
        public SkippedProperty() {
            super(null, null);
        }

        @Override
        protected Object parse(IMappedStringData data) {
            return null;
        }

    }

    private static final class DoubleProperty extends Property<Double> {

        /**
         * @param headerName
         * @param isReusable
         */
        public DoubleProperty(String headerName, String propertyName) {
            super(headerName, propertyName);
        }

        @Override
        protected Double parse(IMappedStringData data) {
            return Double.parseDouble(getValue(data));
        }

    }

    private static final class IntegerProperty extends Property<Integer> {

        /**
         * @param headerName
         * @param isReusable
         */
        public IntegerProperty(String headerName, String propertyName) {
            super(headerName, propertyName);
        }

        @Override
        protected Integer parse(IMappedStringData data) {
            return Integer.parseInt(getValue(data));
        }

    }

    private static final class UndefinedProperty extends Property<String> {

        /**
         * @param headerName
         * @param isReusable
         */
        public UndefinedProperty(String headerName) {
            super(headerName, headerName);
        }

        @Override
        protected String parse(IMappedStringData data) {
            return getValue(data);
        }

    }

    protected static final Property< ? > SKIPPED_PROPERTY = new SkippedProperty();

    private final Map<INodeType, Map<String, Property< ? >>> headers = new HashMap<INodeType, Map<String, Property< ? >>>();

    private final Map<INodeType, Synonyms> notHandledSynonyms = new HashMap<INodeType, Synonyms>();

    private final SynonymsManager synonymsManager;

    protected AbstractSynonymsSaver() {
        synonymsManager = SynonymsManager.getInstance();
    }

    protected Map<String, Object> getElementProperties(INodeType nodeType, IMappedStringData data, boolean addNonMappedHeaders) {
        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Property< ? >> properties = headers.get(nodeType);
        if (properties == null) {
            properties = new HashMap<String, AbstractSynonymsSaver.Property< ? >>();

            headers.put(nodeType, properties);
        }

        for (Entry<String, String> dataEntry : data.entrySet()) {
            String propertyName = dataEntry.getKey();

            Property< ? > property = properties.get(propertyName);
            if (property == null) {
                property = createProperty();

                properties.put(propertyName, property);
            }

            result.put(property.getPropertyName(), property.parse(data));
        }

        return result;
    }

    protected SynonymsManager getSynonymsManager() {
        return synonymsManager;
    }

    protected Property< ? > createProperty() {
        return null;
    }

    protected abstract String getDatasetType();

}
