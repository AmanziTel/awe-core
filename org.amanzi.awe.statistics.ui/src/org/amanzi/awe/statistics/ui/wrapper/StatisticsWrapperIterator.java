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

package org.amanzi.awe.statistics.ui.wrapper;

import java.util.Iterator;

import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapper;
import org.amanzi.awe.ui.tree.wrapper.factory.impl.TreeWrapperIterator;

public class StatisticsWrapperIterator extends TreeWrapperIterator<IStatisticsModel> {

    /**
     * @param models
     */
    public StatisticsWrapperIterator(final Iterator<IStatisticsModel> models) {
        super(models);
    }

    @Override
    protected ITreeWrapper createTreeWrapper(final IStatisticsModel model) {
        return new StatisticsModelWrapper(model);
    }

}