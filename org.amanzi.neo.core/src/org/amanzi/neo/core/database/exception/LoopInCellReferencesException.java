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
package org.amanzi.neo.core.database.exception;

import org.amanzi.neo.core.database.nodes.CellID;

/**
 * Exception that will be thrown if Loop in Cell References was detected
 * 
 * @author Lagutko_N
 */

public class LoopInCellReferencesException extends SplashDatabaseException {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 7405020949557607684L;
    
    public LoopInCellReferencesException(CellID currentNodeId) {        
        super(SplashDatabaseExceptionMessages.getFormattedString(SplashDatabaseExceptionMessages.Loop_In_References, currentNodeId.getFullID()));
    }

}
