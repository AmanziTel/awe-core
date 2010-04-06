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

package org.amanzi.awe.report.handlers;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.awe.report.ReportPlugin;
import org.amanzi.awe.report.model.Report;
import org.amanzi.awe.report.model.ReportModel;
import org.amanzi.awe.report.pdf.PDFPrintingEngine;
import org.amanzi.awe.report.util.ReportUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.jruby.Ruby;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class GenerateReportCommandHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        final ReportModel reportModel = new ReportModel();
        final URL entry = Platform.getBundle(ReportPlugin.PLUGIN_ID).getEntry("ruby/reports");
        String pathToReportsFolder;
        try {
            pathToReportsFolder = FileLocator.resolve(entry).getFile();
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
        final File directory = new File(pathToReportsFolder);
        final PDFPrintingEngine engine = new PDFPrintingEngine();
        if (directory.isDirectory()){
            final File[] files = directory.listFiles();
            for (File file:files){
                System.out.println("[DEBUG] file:\n"+file);
                try {
                    final String script = ReportUtils.readScript(file.getPath());
                    System.out.println("[DEBUG] script:\n"+script);
                    reportModel.updateModel(script);
                    final Report report = reportModel.getReport();
                    engine.printReport(report);
                } catch (IOException e) {
                    // TODO Handle IOException
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }
            }
        }
        return null;
    }

}
