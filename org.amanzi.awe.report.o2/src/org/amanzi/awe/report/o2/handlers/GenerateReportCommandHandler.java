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

package org.amanzi.awe.report.o2.handlers;

import java.io.IOException;
import java.net.URL;

import org.amanzi.awe.report.model.ReportModel;
import org.amanzi.awe.report.o2.O2ReportPlugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

/**
 * <p>
 * Command handler for automatic report generation
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class GenerateReportCommandHandler extends AbstractHandler {
    // private static final Logger LOGGER = Logger.getLogger(GenerateReportCommandHandler.class);
    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        URL entry = Platform.getBundle(O2ReportPlugin.PLUGIN_ID).getEntry("ruby");
        String path = "";
        URL scriptURL = null;
        try {
            scriptURL = FileLocator.toFileURL(O2ReportPlugin.getDefault().getBundle().getEntry("ruby/report_aggregation.rb"));
            path = scriptURL.getPath();
             ReportModel reportModel = new ReportModel(new String[] {FileLocator.resolve(entry).getFile()}, new String[] {path});
            reportModel.updateModel("automation");
        } catch (IOException e) {
            // LOGGER.error(e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        return null;
    }

}
