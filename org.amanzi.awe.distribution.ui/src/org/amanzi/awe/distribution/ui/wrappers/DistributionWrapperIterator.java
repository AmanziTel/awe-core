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

package org.amanzi.awe.distribution.ui.wrappers;

import java.util.Iterator;

import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapper;
import org.amanzi.awe.ui.tree.wrapper.factory.impl.TreeWrapperIterator;

class DistributionWrapperIterator extends TreeWrapperIterator<IDistributionModel> {

    /**
     * @param models
     */
    public DistributionWrapperIterator(final Iterator<IDistributionModel> models) {
        super(models);
    }

    @Override
    protected ITreeWrapper createTreeWrapper(final IDistributionModel model) {
        return new DistributionModelWrapper(model);
    }

}