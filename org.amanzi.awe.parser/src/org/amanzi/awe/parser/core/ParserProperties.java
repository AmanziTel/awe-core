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

package org.amanzi.awe.parser.core;

import java.io.File;

/**
 * Class keep properties data that needed to init parser
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class ParserProperties{
    private File file;


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
