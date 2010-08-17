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

package org.amanzi.awe.neighbours.gpeh;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.amanzi.neo.core.utils.export.IExportHandler;
import org.amanzi.neo.core.utils.export.IExportProvider;
import org.eclipse.core.runtime.IProgressMonitor;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * <p>
 * Common CSV handler
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
// TODO add monitor handling
public class CommonCSVHandler implements IExportHandler {

    private final File output;
    private final IProgressMonitor monitor;
    private CSVWriter writer;
    private final char delimeter;

    /**
     * @param output
     * @param monitor
     * @param delimeter 
     */
    public CommonCSVHandler(File output, IProgressMonitor monitor, char delimeter) {
        this.output = output;
        this.monitor = monitor;
        this.delimeter = delimeter;
    }

    @Override
    public void init() {

        try {
            writer = new CSVWriter(new FileWriter(output),delimeter,CSVWriter.NO_QUOTE_CHARACTER);
        } catch (IOException e) {
            writer = null;
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    public void handleHeaders(IExportProvider provider) {
        if (writer == null || monitor.isCanceled()) {
            return;
        }
        String[] out = new String[] {provider.getDataName()};
        writer.writeNext(out);
        writer.writeNext(provider.getHeaders().toArray(new String[0]));
    }

    @Override
    public void handleData(List<Object> data) {
        if (writer == null || monitor.isCanceled()) {
            return;
        }
        String[] out = new String[data.size()];
        int i = 0;
        for (Object elem : data) {
            if (null == elem) {
                out[i++] = "";
            } else {
                out[i++] = String.valueOf(elem);
            }
        }
        writer.writeNext(out);
    }

    @Override
    public void finish() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                // TODO Handle IOException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
    }

}
