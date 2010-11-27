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

package org.amanzi.awe.afp.loaders;

import org.amanzi.neo.loader.core.parser.LineTransferData;
import org.amanzi.neo.loader.core.saver.AbstractSaver;
import org.amanzi.neo.loader.core.saver.IStructuredSaver;
import org.amanzi.neo.loader.core.saver.MetaData;

/**
 * <p>
 *Afp saver
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class AfpSaver extends AbstractSaver<LineTransferData> implements IStructuredSaver<LineTransferData> {
    private int fileNum;
    private AfpFileTypes type;
    private boolean skipLoadFile;
    
    @Override
    public void init(LineTransferData element) {
        super.init(element);
        fileNum=0;
        skipLoadFile=false;
    }
    @Override
    public void save(LineTransferData element) {
        switch (type) {
        case CELL:
            saveCellLine(element);
            break;

        default:
            break;
        }
    }

    /**
     *
     * @param element
     */
    private void saveCellLine(LineTransferData element) {
    }
    @Override
    public void finishUp(LineTransferData element) {
    }

    @Override
    public Iterable<MetaData> getMetaData() {
        return null;
    }

    @Override
    public boolean beforeSaveNewElement(LineTransferData element) {
        if (skipLoadFile){
            return true;
        }
        fileNum++;
        type=AfpFileTypes.valueOf(element.get("afpType"));
        if (fileNum==1&&type!=AfpFileTypes.CELL){
            error("Not found Cite file");
            skipLoadFile=true;
            return true;
        }
        return false;
    }

    @Override
    public void finishSaveNewElement(LineTransferData element) {
    }

}
