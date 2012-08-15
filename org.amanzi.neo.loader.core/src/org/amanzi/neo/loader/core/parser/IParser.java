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
import java.util.Iterator;

import org.amanzi.neo.loader.core.IData;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface IParser<C extends IConfiguration, D extends IData> extends Iterator<D> {

    interface IFileParsingStartedListener {
        void onFileParsingStarted(File file);
    }

    void init(C configuration);

    void setProgressMonitor(String monitorName, IProgressMonitor monitor);

    void finishUp();

    void addFileParsingListener(IFileParsingStartedListener listener);

    File getLastParsedFile();

    int getLastParsedLineNumber();

}
