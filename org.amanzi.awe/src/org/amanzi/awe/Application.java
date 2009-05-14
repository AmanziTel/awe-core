package org.amanzi.awe;

import net.refractions.udig.internal.ui.UDIGApplication;
import net.refractions.udig.internal.ui.UDIGWorkbenchAdvisor;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.internal.ide.model.WorkbenchAdapterBuilder;

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
			public void initialize(IWorkbenchConfigurer configurer) {
				super.initialize(configurer);
				configurer.setSaveAndRestore(true);
			}
			/**
			 * @see org.eclipse.ui.application.WorkbenchAdvisor#preStartup()
			 */
		    public void preStartup() {
		        // Navigator view needs this
		        WorkbenchAdapterBuilder.registerAdapters();
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
