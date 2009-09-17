/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
