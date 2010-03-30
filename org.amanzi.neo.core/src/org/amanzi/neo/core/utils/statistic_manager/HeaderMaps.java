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

package org.amanzi.neo.core.utils.statistic_manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;


/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class HeaderMaps {
        protected HashMap<Class< ? extends Object>, List<String>> typedProperties = null;
        protected ArrayList<Pattern> headerFilters = new ArrayList<Pattern>();
        protected LinkedHashMap<String, List<String>> knownHeaders = new LinkedHashMap<String, List<String>>();
        protected LinkedHashMap<String, Header> headers = new LinkedHashMap<String, Header>();
        protected TreeSet<String> dropStatsHeaders = new TreeSet<String>();
        protected TreeSet<String> nonDataHeaders = new TreeSet<String>();

        /**
         * @return true if we have parsed the header line and know the properties to load
         */
        protected boolean haveHeaders() {
            return headers.size() > 0;
        }


        public void clearCaches() {
            this.headers.clear();
            this.knownHeaders.clear();
        }

        public boolean headerAllowed(String header) {
            if (headerFilters == null || headerFilters.size() < 1) {
                return true;
            }
            for (Pattern filter : headerFilters) {
                if (filter.matcher(header).matches()) {
                    return true;
                }
            }
            return false;
        }
        /**
         * Add a number of regular expression strings to use as filters for deciding which properties to
         * save. If this method is never used, and the filters are empty, then all properties are
         * processed. Since the saving code is done in the specific loader, not using this method can
         * cause a lot more parsing of data than is necessary, so it is advised to use this. Note also
         * that the filter regular expressions are applied to the cleaned headers, not the original ones
         * found in the file.
         * 
         * @param filters
         */
        public void addHeaderFilters(String[] filters) {
            for (String filter : filters) {
                headerFilters.add(Pattern.compile(filter));
            }
        }
        public List<String> getProperties(Class< ? extends Object> klass) {
            List<String> result=new LinkedList<String>();
            for (Header singleHeader:headers.values()){
                if (singleHeader.headerTypes==klass){
                    result.add(singleHeader.key); 
                }
            }
            return result;

        }


        /**
         *
         * @param key
         * @return
         */
        public Header getHeader(String key) {
            return null;
        }

    }
