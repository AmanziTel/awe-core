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
package org.amanzi.awe.neighbours.views;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;


public class NeighbourAnalyser extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.amanzi.awe.neighbours.views.NeighbourAnalyser"; //$NON-NLS-1$
    private Composite mainFrame;
    private Text text0;
    private Text text1;
    private Text text2;
    private Text text3;
    private Text text4;

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	@Override
    public void createPartControl(Composite parent) {
	    mainFrame = new ScrolledComposite (parent, SWT.BORDER
	            | SWT.H_SCROLL | SWT.V_SCROLL);
	    mainFrame.setLayout(new GridLayout(3  ,false));
	    Label label=new Label(mainFrame,SWT.NONE);
	    label.setText(Messages.NeighbourAnalyser_0);
	    text0 = new Text(mainFrame, SWT.BORDER);
	    GridData layoutData = new GridData();	    
	    layoutData.minimumWidth=50;
	    text0.setLayoutData(layoutData);
	    label=new Label(mainFrame, SWT.LEFT);
	    label.setText(Messages.NeighbourAnalyser_0_d);
        
        label=new Label(mainFrame,SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_1);
        text1 = new Text(mainFrame, SWT.BORDER);
        label=new Label(mainFrame, SWT.LEFT);
        label.setText(Messages.NeighbourAnalyser_1_d);
        
        label=new Label(mainFrame,SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_2);
        text2 = new Text(mainFrame, SWT.BORDER);
        label=new Label(mainFrame, SWT.LEFT);
        label.setText(Messages.NeighbourAnalyser_2_d);
        
        label=new Label(mainFrame,SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_3);
        label.setToolTipText(Messages.NeighbourAnalyser_3_1);
        text3 = new Text(mainFrame, SWT.BORDER);
        label=new Label(mainFrame, SWT.LEFT);
        label.setText(Messages.NeighbourAnalyser_3_d);
        
        label=new Label(mainFrame,SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_4);
        text4 = new Text(mainFrame, SWT.BORDER);
        label=new Label(mainFrame, SWT.LEFT);
        label.setText(Messages.NeighbourAnalyser_4_d);
        
        
        addListeners();
	}



	/**
     *
     */
    private void addListeners() {
    }



    private void showMessage(String message) {
		MessageDialog.openInformation(
		        mainFrame.getShell(),
			"NeighbourAnalyser", //$NON-NLS-1$
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
    public void setFocus() {
	}
}