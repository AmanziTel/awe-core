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

package org.amanzi.awe.afp.wizards.good;

import org.amanzi.awe.afp.models.parameters.OptimizationType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public class Step3FrequencyTypePage extends AbstractAfpWizardPage {
    
    private static final String PAGE_NAME = "Frequency Type";

    private static final int STEP_NUMBER = 3;

    private static final String PAGE_DESCRIPTION = "Step 3 - " + PAGE_NAME;

    /**
     * @param pageName
     * @param wizard
     */
    protected Step3FrequencyTypePage(AfpWizard wizard) {
        super(PAGE_NAME, wizard);

        setPageComplete(false);
        setTitle(AfpWizard.WIZARD_TITLE);
        setDescription(PAGE_DESCRIPTION);
    }

    @Override
    public void createControl(Composite parent) {
        Composite thisParent = new Composite(parent, SWT.NONE);
        thisParent.setLayout(new GridLayout(2, false));
        
        createStepsGroup(thisParent, getStepNumber());
        
        setControl(thisParent);
        setPageComplete(true);
    }

    @Override
    protected int getStepNumber() {
        return STEP_NUMBER;
    }

    @Override
    public boolean isStepAvailable() {
        return model.isOptimizationSupported(OptimizationType.FREQUENCIES);
    }

}
