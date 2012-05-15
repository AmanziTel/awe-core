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

package org.amanzi.neo.loader.ui.handlers;

import org.amanzi.neo.loader.ui.schema.WizardBuilder;
import org.amanzi.neo.loader.ui.wizards.AbstractLoaderWizard;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author lagutko_n
 * @since 1.0.0
 */
public class LoaderWizardHandler extends AbstractHandler {
    
    private static final String WIZARD_ID_PARAMETER = "org.amanzi.loader.wizard";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
      String wizardId= event.getParameter(WIZARD_ID_PARAMETER);
      
      try {
          IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
          AbstractLoaderWizard loaderWizard = WizardBuilder.getBuilder().getWizard(wizardId);
      
          loaderWizard.init(workbenchWindow.getWorkbench(), null);
          Shell parent = workbenchWindow.getShell();
          WizardDialog dialog = new WizardDialog(parent, loaderWizard);
          dialog.create();
          dialog.open();
      } catch (CoreException e) {
          throw new ExecutionException("Error", e);
      }
          
      return null;
    }

}
