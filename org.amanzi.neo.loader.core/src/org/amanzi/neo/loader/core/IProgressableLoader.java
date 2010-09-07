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

import org.amanzi.awe.parser.core.IDataElementOldVersion;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
@Deprecated
public interface IProgressableLoader<T extends IDataElementOldVersion> {
    
    public void addProgressListener(IProgressListener listener);
    
    public void removeProgressListener(IProgressListener listener);
    
    public void fireProgressEvent(ProgressEventOld event);

}
