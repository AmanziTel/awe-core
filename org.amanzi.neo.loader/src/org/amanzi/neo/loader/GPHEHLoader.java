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

package org.amanzi.neo.loader;

import java.io.File;
import java.io.IOException;

import org.amanzi.neo.core.utils.NeoUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.neo4j.api.core.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class GPHEHLoader extends AbstractLoader{

    @Override
    protected Node getStoringNode(Integer key) {
        return null;
    }
@Override
public void run(IProgressMonitor monitor) throws IOException {
        if (monitor != null)
            monitor.subTask(basename);
        GPEHMainFile root = GPEHParser.parseMainFile(new File(filename));
        CountingFileInputStream is = new CountingFileInputStream(new File(filename));
        mainTx = neo.beginTx();
        NeoUtils.addTransactionLog(mainTx, Thread.currentThread(), "GPEHLoader");
        try {
            initializeIndexes();
            int perc = is.percentage();
            int prevPerc = 0;
            int prevLineNumber = 0;
            String line;
            commit(true);
//            reader.close();
            saveProperties();
            finishUpIndexes();
            finishUp();
        } finally {
            commit(false);
        }
    }

    @Override
    protected boolean needParceHeaders() {
        return false;
    }

    @Override
    protected void parseLine(String line) {
    }

}
