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

package org.amanzi.neo.data_generator.data.calls.csv;

import java.util.HashMap;

import org.amanzi.neo.data_generator.data.IGeneratedData;

/**
 * <p>
 * Data for csv statistics files.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class CsvData implements IGeneratedData {
    
    private HashMap<String, FileData> part1;
    private HashMap<String, FileData> part2;

    /**
     * @return Returns the part1.
     */
    public HashMap<String, FileData> getPart1() {
        return part1;
    }
    
    /**
     * @param part1 The part1 to set.
     */
    public void setPart1(HashMap<String, FileData> part1) {
        this.part1 = part1;
    }
    
    /**
     * @return Returns the part2.
     */
    public HashMap<String, FileData> getPart2() {
        return part2;
    }
    
    /**
     * @param part2 The part2 to set.
     */
    public void setPart2(HashMap<String, FileData> part2) {
        this.part2 = part2;
    }
    
}
