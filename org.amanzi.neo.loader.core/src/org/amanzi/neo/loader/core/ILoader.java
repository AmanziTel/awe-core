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

package org.amanzi.neo.loader.core;

import java.io.PrintStream;

import org.amanzi.neo.db.manager.DatabaseManager.DatabaseAccessType;
import org.amanzi.neo.loader.core.parser.IConfigurationData;
import org.amanzi.neo.loader.core.parser.IDataElement;
import org.amanzi.neo.loader.core.parser.IParser;
import org.amanzi.neo.loader.core.saver.ISaver;

/**
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public interface ILoader<T extends IDataElement,T2 extends IConfigurationData> {
    
    void setup(DatabaseAccessType accessType, T2 data);
    void setParser(IParser<T,T2> parser);
    void setSaver(ISaver<T> saver);
    void addProgressListener(ILoaderProgressListener listener);
    void removeProgressListener(ILoaderProgressListener listener);
    void setValidator(ILoaderInputValidator<T2>validator);
    ILoaderInputValidator<T2> getValidator();
    void load();
    String getDescription();
    PrintStream getPrintStream();
    void setPrintStream(PrintStream outputStream);
}
