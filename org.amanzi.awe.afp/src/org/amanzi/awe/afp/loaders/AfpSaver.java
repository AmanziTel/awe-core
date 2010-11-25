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

    @Override
    public void save(LineTransferData element) {
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
        return false;
    }

    @Override
    public void finishSaveNewElement(LineTransferData element) {
    }

}
