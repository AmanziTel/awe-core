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

import java.util.HashMap;

/**
 * This class allows for either replacing of duplicating properties. See addMappedHeader for
 * details.
 * 
 * @author craig
 * @since 1.0.0
 */
public class MappedHeader extends Header {
    protected PropertyMapper mapper;

    public MappedHeader(String key, int index, MappedHeaderRule mapRule) {
        super(key, index);
        this.key = mapRule.key;
        if (mapRule.name != null) {
            // We only replace the name if the new one is valid, otherwise
            // inherit from the old
            // header
            // This allows for support of header replacing rules, as well as
            // duplicating rules
        }
        this.mapper = mapRule.mapper;
        this.values = new HashMap<Object, Integer>(); // need to make a new
        // values list,
        // otherwise we share the same data as
        // the original
    }

    @Override
    public Object parse(String field) {
        if (invalid(field))
            return null;
        Object result = mapper.mapValue(field);
        return super.parse(result.toString());
    }
}
