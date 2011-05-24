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

import org.amanzi.awe.afp.models.AfpModelNew;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public abstract class AbstractAfpWizardPage extends WizardPage {
    
    protected AfpModelNew model;
    
    protected AfpWizard wizard;
    
    /**
     * @param pageName
     */
    protected AbstractAfpWizardPage(String pageName, AfpWizard wizard) {
        super(pageName);
        
        this.wizard = wizard;
        this.model = wizard.getModel();
    }
    
    protected static Group createStepsGroup(Composite parent, int stepNumber){
        Group stepsGroup = new Group(parent, SWT.NONE);
        stepsGroup.setLayout(new GridLayout(1, false));
        GridData gridData = new GridData(GridData.FILL, GridData.FILL, false, true, 1 ,2);
        gridData.widthHint = 220;
        stepsGroup.setLayoutData(gridData);
        
        String steps[] = {"Step 1 - Optimization Goals  ",
                          "Step 2 - Available Resources  ",
                          "Step 3 - Frequency Type  ",
                          "Step 4 - SY Hopping MALs  ",
                          "Step 5 - Separation Rules  ",
                          "Step 6 - Scaling Rules  ",
                          "Step 7 - Summary  "};
        
        for (int i = 0; i < steps.length; i++){
            Label label = new Label(stepsGroup, SWT.LEFT_TO_RIGHT);
            label.setText(steps[i]);
            if (i == stepNumber - 1)
                makeFontBold(label);
        }
        
        
        return stepsGroup;
    }
    
    protected static void makeFontBold(Control label){
        FontData[] fD = label.getFont().getFontData();
        fD[0].setStyle(SWT.BOLD);
        Font font = new Font(label.getDisplay(),fD[0]);
        label.setFont(font);
        font.dispose();
    }
    
    protected abstract int getStepNumber();
    
    protected void refreshPage() {
        model = wizard.getModel();
    }
    
    protected abstract boolean isStepAvailable();
    
    
}
