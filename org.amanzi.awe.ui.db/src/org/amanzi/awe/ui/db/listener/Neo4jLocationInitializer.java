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

package org.amanzi.awe.ui.db.listener;

import org.amanzi.awe.ui.db.dialog.ChooseDatabaseLocationDialog;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ui.PlatformUI;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class Neo4jLocationInitializer implements IAWEEventListenter {
	@Override
	public void onEvent(final IEvent event) {
		switch (event.getStatus()) {
		case INITIALISATION:
			relocateDatabase(null);
			break;
		default:
			break;
		}

	}

	private void relocateDatabase(final String path) {
		boolean isUsed = false;
		if (StringUtils.isEmpty(path)) {
			isUsed = DatabaseManagerFactory.getDatabaseManager().isAlreadyUsed();
		} else {
			isUsed = DatabaseManagerFactory.getDatabaseManager(path, true).isAlreadyUsed();
		}
		if (isUsed) {
			ChooseDatabaseLocationDialog dialog = new ChooseDatabaseLocationDialog(PlatformUI.getWorkbench().getDisplay()
					.getActiveShell());
			dialog.open();
			if (dialog.isCanceled()) {
				//TODO: LN: 07.08.2012, what happened if User Cancels this Dialog????
				return;
			}
			relocateDatabase(dialog.getDatabaseLocation());
		}
	}
}
