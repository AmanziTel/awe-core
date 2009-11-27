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

package org.amanzi.splash.importer;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.amanzi.splash.utilities.SpreadsheetCreator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Abstract class that provides basic functionality for importing data into Splash
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public abstract class AbstractImporter extends SpreadsheetCreator implements IRunnableWithProgress {
    
    /*
     * Content of file to import
     */
    protected InputStream fileContent;
    
    /*
     * Size of File
     */
    protected long fileSize;
    
    /**
     * Constructor 
     * 
     * @param containerPath path to Project that will contain new Spreadsheet
     * @param fileName name of File to import
     * @param stream content of File to import
     * @param fileSize size of File to import
     */
    public AbstractImporter(IPath containerPath, String fileName, InputStream stream, long fileSize) {
        super(containerPath, fileName);
        this.fileContent = stream;
        this.fileSize = fileSize;
    }
    
    @Override
    public abstract void run(IProgressMonitor monitor) throws InvocationTargetException;
    
}
