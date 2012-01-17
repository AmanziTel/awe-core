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

package org.amanzi.neo.loader.core.saver;

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IModel;

/**
 * common saver Interface
 * 
 * @author Kondratenko_Vladislav
 */
public interface ISaver<M extends IModel, D extends IData, C extends IConfiguration> {
    /**
     * initialize required saver data;
     * 
     * @param configuration
     * @param dataElement
     */
    public void init(C configuration, D dataElement) throws Exception;

    /**
     * save dataElement to database;
     * 
     * @param dataElement
     * @throws AWEException
     */
    public void saveElement(D dataElement) throws AWEException;

    /**
     * common finishing actions
     */
    public void finishUp() throws AWEException;
}
