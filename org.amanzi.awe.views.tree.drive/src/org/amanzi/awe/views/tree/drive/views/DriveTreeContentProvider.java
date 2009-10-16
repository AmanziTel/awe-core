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
package org.amanzi.awe.views.tree.drive.views;

import org.amanzi.awe.awe.views.view.provider.NetworkTreeContentProvider;
import org.amanzi.awe.views.network.proxy.Root;
import org.amanzi.neo.core.service.NeoServiceProvider;

/**
 * <p>
 * Content provider for Drive tree
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class DriveTreeContentProvider extends NetworkTreeContentProvider {
    /**
     * Constructor
     * 
     * @param neoProvider neoServiceProvider for this ContentProvider
     */
    public DriveTreeContentProvider(NeoServiceProvider neoProvider) {
        super(neoProvider);
    }

    @Override
    protected Root getRoot() {
        return new DriveRoot(neoServiceProvider);
    }
}
