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


/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class MappedHeaderRule {
    protected final String name;
    protected String key;
    protected final PropertyMapper mapper;

    MappedHeaderRule(String name, String key, PropertyMapper mapper) {
        this.key = key;
        this.name = name;
        this.mapper = mapper;
    }

}
