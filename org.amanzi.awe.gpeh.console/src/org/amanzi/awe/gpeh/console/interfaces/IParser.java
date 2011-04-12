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

package org.amanzi.awe.gpeh.console.interfaces;

import java.io.PrintStream;

/**
 * <p>
 *Parser interface
 * </p>
 * @author NiCK
 * @since 1.0.0
 */
public interface IParser<T extends IDataElement,C extends IConfigurationData> {

    void init(C properties,ISaver<T> saver);
    void addProgressListener(ILoaderProgressListener listener);
    
    void removeProgressListener(ILoaderProgressListener listener);

    void parce();
    boolean  fireProgressEvent(IProgressEvent event);
    PrintStream getPrintStream();
    void setPrintStream(PrintStream outputStream);
}
