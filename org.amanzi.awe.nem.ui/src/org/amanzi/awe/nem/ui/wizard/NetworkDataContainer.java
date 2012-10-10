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

package org.amanzi.awe.nem.ui.wizard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.nem.properties.manager.PropertyContainer;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class NetworkDataContainer {

    private String name;

    private List<String> structure;

    private Map<String, List<PropertyContainer>> typeProperties = new HashMap<String, List<PropertyContainer>>();

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the structure.
     */
    public List<String> getStructure() {
        return structure;
    }

    public void putToTypeProperties(String type, List<PropertyContainer> container) {
        typeProperties.put(type, container);
    }

    /**
     * @return Returns the typeProperties.
     */
    public Map<String, List<PropertyContainer>> getTypeProperties() {
        return typeProperties;
    }

    /**
     * @param structure The structure to set.
     */
    public void setStructure(List<String> structure) {
        this.structure = structure;
    }
}
