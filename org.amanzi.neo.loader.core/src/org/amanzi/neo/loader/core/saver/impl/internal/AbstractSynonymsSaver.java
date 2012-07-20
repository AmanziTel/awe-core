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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.neo.loader.core.IMappedStringData;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.synonyms.Synonyms;
import org.amanzi.neo.loader.core.synonyms.SynonymsManager;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.commons.lang3.BooleanUtils;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractSynonymsSaver<T extends IConfiguration> extends AbstractSaver<T, IMappedStringData> {

    protected static abstract class Property<C extends Object> {

        private final String headerName;

        private final String propertyName;

        public Property(final String propertyName, final String headerName) {
            this.propertyName = propertyName;
            this.headerName = headerName;
        }

        protected String getValue(final IMappedStringData data) {
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
        protected Object parse(final IMappedStringData data) {
            return null;
        }

    }

    protected static final class DoubleProperty extends Property<Double> {

        /**
         * @param headerName
         * @param isReusable
         */
        public DoubleProperty(final String headerName, final String propertyName) {
            super(headerName, propertyName);
        }

        @Override
        protected Double parse(final IMappedStringData data) {
            return Double.parseDouble(getValue(data));
        }

    }

    protected static final class StringProperty extends Property<String> {

        /**
         * @param propertyName
         * @param headerName
         */
        public StringProperty(final String propertyName, final String headerName) {
            super(propertyName, headerName);
        }

        @Override
        protected String parse(final IMappedStringData data) {
            return getValue(data);
        }

    }

    protected static final class BooleanProperty extends Property<Boolean> {

        /**
         * @param propertyName
         * @param headerName
         */
        public BooleanProperty(final String propertyName, final String headerName) {
            super(propertyName, headerName);
        }

        @Override
        protected Boolean parse(final IMappedStringData data) {
            return BooleanUtils.toBooleanObject(getValue(data));
        }

    }

    protected static final class LongProperty extends Property<Long> {

        /**
         * @param headerName
         * @param isReusable
         */
        public LongProperty(final String headerName, final String propertyName) {
            super(headerName, propertyName);
        }

        @Override
        protected Long parse(final IMappedStringData data) {
            return Long.parseLong(getValue(data));
        }

    }

    protected static final class IntegerProperty extends Property<Integer> {

        /**
         * @param headerName
         * @param isReusable
         */
        public IntegerProperty(final String headerName, final String propertyName) {
            super(headerName, propertyName);
        }

        @Override
        protected Integer parse(final IMappedStringData data) {
            return Integer.parseInt(getValue(data));
        }

    }

    protected static final class UndefinedProperty extends Property<Object> {

        private Property< ? > currentProperty = null;

        /**
         * @param headerName
         * @param isReusable
         */
        public UndefinedProperty(final String headerName) {
            super(headerName, headerName);
        }

        @Override
        protected Object parse(final IMappedStringData data) {
            Object result = null;

            if (currentProperty == null) {
                result = initializeProperty(data);
            }

            if (result == null) {
                try {
                    result = currentProperty.parse(data);
                } catch (NumberFormatException e) {
                    result = initializeProperty(data);
                }
            }

            return result;
        }

        private Object initializeProperty(final IMappedStringData data) {
            Object result = null;

            // try double
            currentProperty = new IntegerProperty(getPropertyName(), getPropertyName());
            try {
                result = currentProperty.parse(data);
            } catch (NumberFormatException e1) {
                // try long
                currentProperty = new LongProperty(getPropertyName(), getPropertyName());
                try {
                    result = currentProperty.parse(data);
                } catch (NumberFormatException e2) {
                    // try integer
                    currentProperty = new DoubleProperty(getPropertyName(), getPropertyName());
                    try {
                        result = currentProperty.parse(data);
                    } catch (NumberFormatException e3) {
                        currentProperty = new BooleanProperty(getPropertyName(), getPropertyName());

                        result = currentProperty.parse(data);
                        if (result == null) {
                            currentProperty = new StringProperty(getPropertyName(), getPropertyName());
                            result = currentProperty.parse(data);
                        }
                    }
                }
            }

            return result;
        }
    }

    protected static final Property< ? > SKIPPED_PROPERTY = new SkippedProperty();

    private final Map<INodeType, Map<String, Property< ? >>> headers = new HashMap<INodeType, Map<String, Property< ? >>>();

    private final Map<INodeType, List<Synonyms>> notHandledSynonyms = new HashMap<INodeType, List<Synonyms>>();

    private final SynonymsManager synonymsManager;

    protected AbstractSynonymsSaver(IProjectModelProvider projectModelProvider, SynonymsManager synonymsManager) {
        super(projectModelProvider);
        this.synonymsManager = synonymsManager;
    }

    protected Map<String, Object> getElementProperties(final INodeType nodeType, final IMappedStringData data,
            final boolean addNonMappedHeaders) {
        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Property< ? >> properties = headers.get(nodeType);
        if (properties == null) {
            properties = new HashMap<String, AbstractSynonymsSaver.Property< ? >>();

            headers.put(nodeType, properties);
        }

        for (Entry<String, String> dataEntry : data.entrySet()) {
            String headerName = dataEntry.getKey();

            Property< ? > property = properties.get(headerName);
            if (property == null) {
                property = createProperty(nodeType, headerName, addNonMappedHeaders);

                properties.put(headerName, property);
            }

            Object value = property.parse(data);
            if (value != null) {
                result.put(property.getPropertyName(), property.parse(data));
            }
        }

        return result;
    }

    protected Property< ? > createProperty(final INodeType nodeType, final String header, final boolean addNonMappedHeaders) {
        List<Synonyms> synonymsList = notHandledSynonyms.get(nodeType);

        if (synonymsList == null) {
            synonymsList = synonymsManager.getSynonyms(getSynonymsType(), nodeType);

            notHandledSynonyms.put(nodeType, synonymsList);
        }

        Synonyms synonym = null;

        check_synonyms: for (Synonyms singleSynonym : synonymsList) {
            for (String pattern : singleSynonym.getPossibleHeaders()) {
                if (header.matches(pattern)) {
                    synonym = singleSynonym;
                    break check_synonyms;
                }
            }
        }

        Property< ? > result = null;

        if (synonym != null) {
            result = createPropertyFromSynonym(synonym, header);
            synonymsList.remove(synonym);
        } else if (addNonMappedHeaders) {
            result = new UndefinedProperty(header);
        } else {
            result = SKIPPED_PROPERTY;
        }

        return result;
    }

    private Property< ? > createPropertyFromSynonym(final Synonyms synonym, final String header) {
        String propertyName = synonym.getPropertyName();

        switch (synonym.getSynonymType()) {
        case BOOLEAN:
            return new BooleanProperty(propertyName, header);
        case DOUBLE:
            return new DoubleProperty(propertyName, header);
        case INTEGER:
            return new IntegerProperty(propertyName, header);
        case LONG:
            return new LongProperty(propertyName, header);
        case STRING:
            return new StringProperty(propertyName, header);
        default:
            return new UndefinedProperty(header);
        }
    }

    protected abstract String getSynonymsType();

}
