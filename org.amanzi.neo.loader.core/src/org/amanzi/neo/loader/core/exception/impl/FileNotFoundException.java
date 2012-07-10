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

package org.amanzi.neo.loader.core.exception.impl;

import java.io.File;

import org.amanzi.neo.loader.core.exception.LoaderException;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class FileNotFoundException extends LoaderException {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 6754559139463208652L;

    private final File file;

    /**
     * 
     */
    public FileNotFoundException(final File file, final Exception e) {
        super(e);
        this.file = file;
    }

    public File getFile() {
        return file;
    }

}
