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

package org.amanzi.neo.data_generator.data.calls;

import java.util.ArrayList;
import java.util.List;


/**
 * Data saver for probe data for one call.
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class ProbeData {
    
    private Long key;
    private String number;
    private String name;
    private List<CommandRow> commands;
    
    /**
     * Constructor.
     * @param aName String (probe name)
     * @param aNumber String (phone number)
     * @param aKey Long (file key)
     */
    public ProbeData(String aName, String aNumber, Long aKey) {
        name = aName;
        number = aNumber;
        key = aKey;
        commands = new ArrayList<CommandRow>();
    }
    
    /**
     * Getter for probe key.
     *
     * @return Long
     */
    public Long getKey() {
        return key;
    }
    
    /**
     * Getter for probe name.
     *
     * @return String
     */
    public String getName() {
        return name;
    }
    
    /**
     * Getter for phone number.
     *
     * @return String
     */
    public String getNumber() {
        return number;
    }
    
    /**
     * Getter for list of commands.
     *
     * @return String
     */
    public List<CommandRow> getCommands() {
        return commands;
    }

}
