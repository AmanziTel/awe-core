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

package org.amanzi.awe.statistics;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.rubypeople.rdt.ui.extensions.IScriptsProvider;

/**
 * Provides script files to be copied to RDT
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class ScriptsProvider implements IScriptsProvider {
    private static final String RDT_DIRECTORY_NAME = "statistics";
    private static final Logger LOGGER = Logger.getLogger(ScriptsProvider.class);

    public ScriptsProvider() {
    }

    public String getRdtDirectoryName() {
        return RDT_DIRECTORY_NAME;
    }

    public File[] getScriptsToCopy() {
        URL fileURL;
        try {
            fileURL = FileLocator.toFileURL(StatisticPlugin.getDefault().getBundle().getEntry("ruby/kpi"));
            File directory = new File(fileURL.getPath());
            FileFilter fileFilter = new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    final String name = pathname.getName();
                    return name.endsWith(".rb") || name.endsWith(".t");
                }

            };
            return directory.listFiles(fileFilter);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

}
