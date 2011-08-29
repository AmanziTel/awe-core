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
package org.amanzi.awe;

import net.refractions.udig.internal.ui.UDIGApplication;
import net.refractions.udig.internal.ui.UDIGWorkbenchAdvisor;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;

/**
 * This is the default application for the Amanzi Wireless Explorer.
 * It is based directly on uDIG, and uses its advisor.
 * 
 * @since AWE 1.0.0
 * @author craig
 */
public class Application extends UDIGApplication implements IApplication {
    
	/**
	 * Create the AWE workbench advisor by using the UDIGWorkbenchAdvisor with
	 * only the perspective changed to match the AWE requirements.
	 * @see net.refractions.udig.internal.ui.UDIGApplication#createWorkbenchAdvisor()
	 */
	@Override
	protected WorkbenchAdvisor createWorkbenchAdvisor() {
		return new AWEWorkbenchAdivsor() {
			/**
			 * @see org.eclipse.ui.application.WorkbenchAdvisor#initialize(org.eclipse.ui.application.IWorkbenchConfigurer)
			 */
			@Override
            public void initialize(IWorkbenchConfigurer configurer) {
				super.initialize(configurer);
				configurer.setSaveAndRestore(true);
			}
		};
	}
	
	private class AWEWorkbenchAdivsor extends UDIGWorkbenchAdvisor {
		@Override
		public String getInitialWindowPerspectiveId() {
			return PerspectiveFactory.AWE_PERSPECTIVE;
		}        
		

	}

}
