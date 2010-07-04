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

package org.amanzi.neo.core.utils.export;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * <p>
 * Common export mechanism
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class CommonExporter {
    private static final Logger LOGGER = Logger.getLogger(CommonExporter.class);
    IExportHandler handler;
    IExportProvider provider;

    /**
     * Instantiates a new common importer.
     * 
     * @param handler the handler
     * @param importer the importer
     */
    public CommonExporter(IExportHandler handler, IExportProvider provider) {
        super();
        this.handler = handler;
        this.provider = provider;
    }

    /**
     * Process import data.
     * 
     * @param monitor - Progress Monitor
     */
    public void process(IProgressMonitor monitor) {
        if (!provider.isValid()) {
            LOGGER.error(String.format("Provider %s not valid", provider));
            return;
        }
        handler.init();

        handler.handleHeaders(provider);
        try {
            while (provider.hasNextLine()) {
                handler.handleData(provider.getNextLine());
            }
        } finally {
            handler.finish();
        }
    }
}
