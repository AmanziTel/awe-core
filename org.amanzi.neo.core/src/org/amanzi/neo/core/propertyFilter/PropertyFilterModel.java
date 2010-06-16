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

package org.amanzi.neo.core.propertyFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.preferences.NeoCorePreferencesConstants;

/**
 * <p>
 * Model that perfom property filtering.
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class PropertyFilterModel {

    private List<Filter> filters = new ArrayList<Filter>();
    protected static final int indEncludance = 0;
    protected static final int indDataset = 1;
    protected static final int indProperty = 2;

    public PropertyFilterModel() {

    }

    public List<String> filerProperties(String dataset, Collection<String> properties) {
        loadData();
        List<String> result = new ArrayList<String>(properties);
        List<String> filtered = new ArrayList<String>();
        if (!dataset.isEmpty()) {
            List<Filter> filteredFilters = new ArrayList<Filter>();
            for (Filter filter : filters)
                if (filter.getDataset().isEmpty() && dataset.toLowerCase().contains(filter.getDataset().toLowerCase()))
                    filteredFilters.add(filter);
            filters = filteredFilters;
        }

        for (Filter filter : filters) {
            // TODO may be this logic should live in enum
            if (filter.getOperationCase() == OperationCase.INCLUDE) {
                for (String property : result)
                    if (property.toUpperCase().contains(filter.getProperty().toUpperCase()))
                        filtered.add(property);
            } else if (filter.getOperationCase() == OperationCase.EXCLUDE) {
                for (String property : result)
                    if (!property.toUpperCase().contains(filter.getProperty().toUpperCase()))
                        filtered.add(property);
            }
            result.clear();
            result.addAll(filtered);
            filtered.clear();
        }
        return result;
    }

    private void loadData() {
        String val = NeoCorePlugin.getDefault().getPreferenceStore().getString(NeoCorePreferencesConstants.FILTER_RULES);
        int propertyIndex = indEncludance;
        Filter filter = null;
        for (String str : val.split(NeoCorePreferencesConstants.CRS_DELIMETERS)) {
            if (propertyIndex == indEncludance) {
                filter = new Filter(null, "", "");
                filter.setOperationCase(OperationCase.getEnumById(str));
                propertyIndex = indDataset;
            } else if (propertyIndex == indDataset) {
                filter.setDataset(str);
                propertyIndex = indProperty;
            } else if (propertyIndex == indProperty) {
                filter.setProperty(str);
                propertyIndex = indEncludance;
                filters.add(filter);
            }
        }
    }

    private class Filter {
        private OperationCase operationCase = OperationCase.NEW;
        private String dataset;
        private String property;

        /**
         * @param listName
         * @param properties
         */
        public Filter(OperationCase operationCase, String dataset, String property) {
            super();
            this.operationCase = operationCase;
            this.dataset = dataset;
            this.property = property;
        }

        /**
         * @return Returns the operationCase.
         */
        public OperationCase getOperationCase() {
            return operationCase;
        }

        /**
         * @param operationCase The operationCase to set.
         */
        public void setOperationCase(OperationCase operationCase) {
            this.operationCase = operationCase;
        }

        /**
         * @return Returns the dataset.
         */
        public String getDataset() {
            return dataset;
        }

        /**
         * @param dataset The dataset to set.
         */
        public void setDataset(String dataset) {
            this.dataset = dataset;
        }

        /**
         * @return Returns the property.
         */
        public String getProperty() {
            return property;
        }

        /**
         * @param property The property to set.
         */
        public void setProperty(String property) {
            this.property = property;
        }
    }

}
