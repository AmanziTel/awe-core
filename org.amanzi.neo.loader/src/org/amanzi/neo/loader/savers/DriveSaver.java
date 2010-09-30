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

package org.amanzi.neo.loader.savers;

import java.util.Calendar;

import org.amanzi.neo.loader.core.parser.HeaderTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.IStructuredSaver;

/**
 * <p>
 * 
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public abstract class DriveSaver<T extends HeaderTransferData> extends AbstractHeaderSaver<T> implements IStructuredSaver<T> {
    protected boolean newElem;
    protected Calendar workDate;
    protected boolean applyToAll;
    private long count;
    
    @Override
    public void init(T element) {
        super.init(element);
        initBeforeStartTX();
        startMainTx(4000);
        initializeIndexes();
        if (cleanHeaders()){
            element.put("cleanHeaders", "true");
        }
    }

public void save(T element) {
    if (newElem){
        handleFirstRow(element);
        newElem=false;
    }
    
};


/**
 * Handle first row.
 *
 * @param element the element
 */
protected  void handleFirstRow(T element){
    propertyMap.clear();
    definePropertyMap(element);
}


protected abstract void definePropertyMap(T element);

    /**
     * Clean headers.
     *
     * @return true, if successful
     */
    protected boolean cleanHeaders() {
        return true;
    }



    /**
     * Inits the before start tx.
     */
    protected void initBeforeStartTX() {
        count=0;
        newElem=true;
        workDate=null;
        applyToAll=false;
        addDriveIndexes();
    }


    protected abstract void addDriveIndexes();
}
