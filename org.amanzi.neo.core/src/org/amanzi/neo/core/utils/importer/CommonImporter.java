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

package org.amanzi.neo.core.utils.importer;

/**
 * <p>
 * Common import data
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class CommonImporter {
IImportHandler handler;
IImporter importer;

    /**
     * Instantiates a new common importer.
     * 
     * @param handler the handler
     * @param importer the importer
     */
public CommonImporter(IImportHandler handler, IImporter importer) {
    super();
    this.handler = handler;
    this.importer = importer;
}

    /**
     * Process import data.
     */
    public void process() {
        importer.init();
        try {
            while (importer.haveNext()) {
                handler.handleData(importer.getNextPart());
            }
        } finally {
            importer.finish();
        }
    }
}
