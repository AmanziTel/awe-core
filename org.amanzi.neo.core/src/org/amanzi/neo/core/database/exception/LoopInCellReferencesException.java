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
