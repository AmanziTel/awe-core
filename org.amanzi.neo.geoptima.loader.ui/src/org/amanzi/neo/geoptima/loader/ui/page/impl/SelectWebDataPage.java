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

package org.amanzi.neo.geoptima.loader.ui.page.impl;

import org.amanzi.neo.geoptima.loader.ui.internal.Messages;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class SelectWebDataPage extends SelectRemoteDataPage {

    /**
     * @param name
     */
    public SelectWebDataPage() {
        super(Messages.selectWebSource_PageName);
    }

    @Override
    protected String getDefaultHost() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getDefaulUserName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getDefaulPassword() {
        // TODO Auto-generated method stub
        return null;
    }

}
