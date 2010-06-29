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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.loader.CountingFileInputStream;

/**
 * <p>
 * Common
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class TxtFileImporter implements IImporter {
    private File file;
    private CountingFileInputStream is;
    private BufferedReader reader;
    private String line = null;
    private boolean haveNextReault;
    private boolean haveNextCall;


    /**
     * Instantiates a new txt file importer.
     * 
     * @param file the file
     */
    public TxtFileImporter(File file) {
        super();
        this.file = file;
    }

    @Override
    public boolean haveNext() {
        if (haveNextCall) {
            return haveNextReault;
        }
        haveNextCall = true;
        line = null;
        try {
            haveNextReault = reader != null && (line = reader.readLine()) != null;
        } catch (IOException e) {
            AweConsolePlugin.exception(e);
            haveNextReault = false;
            line = null;
        }
        return haveNextReault;
    }

    @Override
    public void init() {
        haveNextCall = false;
        try {

            is = new CountingFileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(is));
        } catch (FileNotFoundException e) {
            AweConsolePlugin.exception(e);
            reader = null;
        }

    }

    @Override
    public IImportParameter getNextPart() {
        if (haveNext()) {
            haveNextCall = false;
            return new TxtLineParameter(line);
        } else {
            return null;
        }

    }

    @Override
    public void finish() {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            AweConsolePlugin.exception(e);
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Gets the percentage.
     * 
     * @return the percentage
     */
    public int getPercentage() {
        return is.percentage();
    }

    /**
     * <p>
     * Wrapper of TxtLineParameter
     * </p>
     * 
     * @author TsAr
     * @since 1.0.0
     */
    public static class TxtLineParameter implements IImportParameter {
        public final String line;


        /**
         * Instantiates a new txt line parameter.
         * 
         * @param line the line
         */
        public TxtLineParameter(String line) {
            super();
            this.line = line;
        }

    }
}
