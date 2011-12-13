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

package org.amanzi.neo.loader.core;

/**
 * @author Konratenko_Vladislav
 */
public class LoaderInfoImpl implements ILoaderInfo {
    /**
     * loader name;
     */
    String name;
    /**
     * loader type
     */
    String type;
    /**
     * loader data type
     */
    String dataType;

    /**
     * set the loader info
     */
    public LoaderInfoImpl(String name, String type, String dataType) {
        this.name = name;
        this.type = type;
        this.dataType = dataType;

    }

    @Override
    public String getName() {
        return name;
    }

}
