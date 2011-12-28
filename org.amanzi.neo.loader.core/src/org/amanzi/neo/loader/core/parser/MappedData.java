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

package org.amanzi.neo.loader.core.parser;

import java.io.File;
import java.util.HashMap;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author lagutko_n
 * @since 1.0.0
 */
public class MappedData extends HashMap<String, String> implements IData {
    
    private File file;

    /** long serialVersionUID field */
    private static final long serialVersionUID = -3753565510584074157L;

    /**
     * @return Returns the file.
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file The file to set.
     */
    public void setFile(File file) {
        this.file = file;
    }

}
