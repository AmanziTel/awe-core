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

package org.amanzi.awe.parser.csv;

import java.util.HashMap;

import org.amanzi.awe.parser.core.IDataElementOldVersion;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author NiCK
 * @since 1.0.0
 */
public class CSVDataElement extends HashMap<String, Object> implements IDataElementOldVersion {

    @Override
    public Long getLatitude() {
        return null;
    }

    @Override
    public Long getLongitude() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public long getSize() {
        return super.size();
    }

    @Override
    public Long getTimestamp() {
        return null;
    }

}
