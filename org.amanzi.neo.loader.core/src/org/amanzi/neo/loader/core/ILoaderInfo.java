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
 * common loader info interface
 * 
 * @author Kondratenko_Vladsialv
 */
public interface ILoaderInfo {
    /**
     * get loader name
     * 
     * @return
     */
    public String getName();

    /**
     * get loader type
     * 
     * @return
     */
    public String getType();

    /**
     * get loader data type
     * 
     * @return
     */
    public String getDataType();

}
