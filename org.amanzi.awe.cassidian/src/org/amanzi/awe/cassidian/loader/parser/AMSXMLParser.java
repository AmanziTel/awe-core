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

package org.amanzi.awe.cassidian.loader.parser;

import java.io.File;

import org.amanzi.awe.cassidian.structure.TNSElement;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class AMSXMLParser {

    Handler handler;

    public AMSXMLParser() {
        handler = new Handler();
    }

    public TNSElement parse(String filePath) {
       File file = new File(filePath);
        if (file.exists()) {
            return handler.parseElement(file);
        }
        return null;
    }

}
