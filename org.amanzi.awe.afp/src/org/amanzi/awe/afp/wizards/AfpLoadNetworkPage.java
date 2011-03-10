package org.amanzi.awe.afp.wizards;

import java.util.Arrays;

import org.amanzi.awe.afp.models.AfpModel;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class AfpLoadNetworkPage extends AfpWizardPage {
	
	private Combo networkCombo;
	protected String datasetName;
	
	/*
	 * Combor for Selection Lists
	 */
	private Combo selectionListCombo;
		
	
	public AfpLoadNetworkPage(String pageName, AfpModel model, String desc) {
        super(pageName, model);
        
        setPageComplete(false);
        setTitle(AfpImportWizard.title);
        setDescription(desc);
    }
	
	@Override
	public void createControl(Composite parent) {
		
		Group main = new Group(parent, SWT.FILL);
		main.setLayout(new GridLayout(2, false));
		
		new Label(main, SWT.LEFT).setText("Network: ");
        
		networkCombo = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
		networkCombo.setLayoutData(new GridData(GridData.FILL, SWT.BEGINNING, true, false));
		networkCombo.setItems(model.getNetworkDatasets());
		networkCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
            	
            	datasetName = networkCombo.getText().trim();
            	model.setSelectNetworkDataSetName(datasetName);
            	//afpCombo.setItems(getAfpDatasets(datasetNode));
				setPageComplete(canFlipToNextPage());
				
				String[] items = model.getNetworkSelectionLists(datasetName);
				items = Arrays.copyOf(items, items.length + 1);
				items[items.length -1] = "[ALL]";
				Arrays.sort(items);
				selectionListCombo.setItems(items);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
		
		new Label(main, SWT.LEFT).setText("Selection List:");
		
		selectionListCombo = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
		selectionListCombo.setLayoutData(new GridData(GridData.FILL, SWT.BEGINNING, true, false));
		selectionListCombo.setItems(new String[] {"[ALL]"});
		selectionListCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                model.setNetworkSelectionName(selectionListCombo.getText().trim());
                setPageComplete(canFlipToNextPage());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
		
		setPageComplete(true);
		setControl(main);
	}
	
	@Override
    public void setVisible(boolean visible) {
        
        super.setVisible(visible);
    }
	
    
    @Override
    public boolean canFlipToNextPage(){
	   if (isValidPage()) {
		   return true;
	   }
	    return false;
	}

    /**
     * Checks if is valid page.
     * 
     * @return true, if is valid page
     */
    protected boolean isValidPage() {
        
        if (StringUtils.isEmpty(datasetName)) {
            return false;
        }
        
        if(!model.hasValidNetworkDataset()) {
        	return false;
        }
        
        
        return true;
    }

}
