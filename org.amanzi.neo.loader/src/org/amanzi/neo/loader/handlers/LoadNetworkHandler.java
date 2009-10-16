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
package org.amanzi.neo.loader.handlers;

import org.amanzi.neo.loader.LoadNetwork;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * <p>
 * Load network command handler
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class LoadNetworkHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
            LoadNetwork loadNetwork = new LoadNetwork(HandlerUtil.getActiveWorkbenchWindowChecked(event).getWorkbench()
                    .getDisplay());
            loadNetwork.run();
            return null;
    }

}
