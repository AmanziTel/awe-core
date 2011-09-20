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

package org.amanzi.neo.services.model;

/**
 * <p>
 * This interface encapsulates methods for working with timestamp data.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface ITimelineModel extends IDriveInquirerableModel {

    public long getMinTimestamp();

    public long getMaxTimestamp();

    /**
     * Update the minimum and maximum timestamp values.
     * 
     * @param timestamp the new value of timestamp
     */
    public void updateTimestamp(long timestamp);
}
