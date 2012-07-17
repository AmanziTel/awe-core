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

import org.amanzi.neo.loader.core.IMappedStringData;
import org.amanzi.neo.loader.core.internal.IConfiguration;
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

    private static abstract class Property<C extends Object> {

        private final boolean isReusable;

        private final String headerName;

        public Property(String headerName, final boolean isReusable) {
            this.isReusable = isReusable;
            this.headerName = headerName;
        }

        protected String getValue(IMappedStringData data) {
            if (isReusable) {
                return data.get(headerName);
            } else {
                return data.remove(headerName);
            }
        }

        protected abstract C parse(IMappedStringData data);

    }

    private static final class DoubleProperty extends Property<Double> {

        /**
         * @param headerName
         * @param isReusable
         */
        public DoubleProperty(String headerName) {
            super(headerName, false);
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
        public IntegerProperty(String headerName) {
            super(headerName, false);
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
            super(headerName, false);
        }

        @Override
        protected String parse(IMappedStringData data) {
            return null;
        }

    }

    private final Map<String, Property< ? >> headers = new HashMap<String, AbstractSynonymsSaver.Property< ? >>();

    private final SynonymsManager synonymsManager;

    protected AbstractSynonymsSaver() {
        synonymsManager = SynonymsManager.getInstance();
    }

    protected Map<String, Object> getElementProperties(INodeType nodeType, IMappedStringData data, boolean addNonMappedHeaders) {
        return null;
    }

    protected SynonymsManager getSynonymsManager() {
        return synonymsManager;
    }

    protected abstract String getDatasetType();

}
