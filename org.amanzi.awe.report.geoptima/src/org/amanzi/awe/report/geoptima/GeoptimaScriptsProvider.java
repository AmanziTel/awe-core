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

package org.amanzi.awe.report.geoptima;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;

import org.amanzi.awe.reports.geoptima.GeoptimaReportsPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.rubypeople.rdt.ui.extensions.IScriptsProvider;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Pechko_E
 * @since 1.0.0
 */
public class GeoptimaScriptsProvider implements IScriptsProvider {

    /**
     * 
     */
    public GeoptimaScriptsProvider() {
    }

    public String getRdtDirectoryName() {
        return "geoptima";
    }

    public File[] getScriptsToCopy() {
        URL fileURL;
        try {
            fileURL = FileLocator.toFileURL(GeoptimaReportsPlugin.getDefault().getBundle().getEntry("ruby/kpi"));
            File directory = new File(fileURL.getPath());
            FileFilter fileFilter = new FileFilter(){

                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".rb");
                }
                
            };
            File[] files = directory.listFiles(fileFilter);
            for (File file: files){
                System.out.println("File to be copied: "+file.getPath());
            }
            return files;
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Handle IOException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }

}
