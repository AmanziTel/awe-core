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

import java.io.IOException;
import java.io.PrintStream;

import org.amanzi.awe.gpeh.console.saver.MetaData;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public interface ISaver<T extends IDataElement> {
    void init(T element);
    void save(T element) throws IOException;
    void finishUp(T element);
    PrintStream getPrintStream();
    void setPrintStream(PrintStream outputStream);
    Iterable<MetaData> getMetaData();
}
