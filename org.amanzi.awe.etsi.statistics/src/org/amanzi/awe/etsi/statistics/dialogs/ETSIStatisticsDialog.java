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

package org.amanzi.awe.etsi.statistics.dialogs;

import java.util.ArrayList;

import org.amanzi.awe.etsi.statistics.ETSIStatistics;
import org.amanzi.awe.etsi.statistics.Messages;
import org.amanzi.awe.etsi.statistics.ETSIStatistics.StatisticsPeriod;
import org.amanzi.integrator.awe.AWEProjectManager;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.Traverser;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.internal.ui.wizards.NewRubyElementCreationWizard;

/**
 * Dialog for starting collecting ETSI statistics
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class ETSIStatisticsDialog {
	
	/*
     * Minimum height of Shell
     */
    private static final int MINIMUM_HEIGHT = 180;
    
    /*
     * Minimum width of Shell
     */
    private static final int MINIMUM_WIDTH = 400;
	
	/*
	 * Shell of this Dialog
	 */
	private Shell dialogShell;
	
	/*
	 * Combos to choose datasets
	 */
	private Combo firstCombo, secondCombo;
	
	/*
	 * Buttons
	 */
	private Button calculateStatistics, cancel;
	
	public ETSIStatisticsDialog(Shell parentShell) {
		this(parentShell, true);
	}
	
	/**
	 * Creates a Shell and add GUI elements
	 * 
	 * @param shell shell
	 * @param createNewShell is true than create a child shell of given shell for dialog
	 */
	
	protected ETSIStatisticsDialog(Shell shell, boolean createNewShell) {
		if (createNewShell) {
			dialogShell = new Shell(shell);
		}
		else {
			dialogShell = shell;
		}
		
		dialogShell.setMinimumSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
		
		dialogShell.setText("Compute statistics for EADS data");
		
		createControl(dialogShell);
		createActions(dialogShell);
	}
	
	/**
	 * Opens a Dialog
	 */
	public void open() {
		dialogShell.pack();
		dialogShell.open();
	}
	
	private void updateRunButtonStatus() {
		boolean enabled = (firstCombo.getText() != null) &&
	    				 (firstCombo.getText().length() > 0) &&
	    				 (secondCombo.getText() != null) &&
	    				 (secondCombo.getText().length() > 0);
		calculateStatistics.setEnabled(enabled);
	}
	
	/**
	 * Creates actions for buttons
	 * 
	 * @param parentShell Dialog Shell
	 */
	
	private void createActions(final Shell parentShell) {
		firstCombo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateRunButtonStatus();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		secondCombo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateRunButtonStatus();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		calculateStatistics.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				runCollecting(firstCombo.getText(), secondCombo.getText());
				dialogShell.close();				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		cancel.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialogShell.close();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
					
			}
		});
	}
	
	private void runCollecting(final String datasetName, final String period) {
		final String spreadsheetName = Messages.getFormattedString(Messages.Statistics_Spreadsheet_name, period, datasetName);
		Job correlateJob = new Job(Messages.Statistics_Job_name) {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				//create a ruby project for ETSI statistics spreadsheet
				IRubyProject rubyProject = null;
				try {
					String aweProjectName = AWEProjectManager.getActiveProjectName();
					rubyProject = NewRubyElementCreationWizard.configureRubyProject(Messages.Statistics_Ruby_Project_name, aweProjectName);
				}
				catch (CoreException e) {
					return Status.CANCEL_STATUS;
				}
				
				IPath rubyProjectPath = rubyProject.getProject().getFullPath();
				
				ETSIStatistics statistics = new ETSIStatistics(rubyProjectPath, spreadsheetName);
				statistics.calculateStatistics(datasetName, StatisticsPeriod.getPeriodByName(period));
				final SpreadsheetNode spreadsheet = statistics.getSpreadsheet(); 
				
				ActionUtil.getInstance().runTask(new Runnable() {

					@Override
					public void run() {
						NeoSplashUtil.openSpreadsheet(PlatformUI.getWorkbench(), spreadsheet);
					}
					
				}, true);				
				
				return Status.OK_STATUS;
			}
		};
		
		correlateJob.schedule(50);
	}

	/**
	 * Creates controls in parent Composite
	 * 
	 * @param parent parent Composite
	 */
	
	private void createControl(Composite parent) {
		parent.setLayout(new GridLayout(1, true));		
		parent.setLayoutData(new GridData(SWT.FILL));
		
		Label firstComboLabel = new Label(parent, SWT.NONE);
		firstComboLabel.setText(Messages.Statistics_Dialog_ETSI_Dataset_combo);
		
		firstCombo = new Combo(parent, SWT.VERTICAL | SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		firstCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		addDatasetItems(firstCombo);
		
		Label secondComboLabel = new Label(parent, SWT.NONE);
		secondComboLabel.setText(Messages.Statistics_Dialog_Period_combo);
		
		secondCombo = new Combo(parent, SWT.VERTICAL | SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		secondCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		addPeriodItems(secondCombo);
		
		createFinishButtons(parent);
	}
	
	/**
	 * Creates 'Cancel' and 'Load' buttons
	 * 
	 * @param parent
	 */
	
	private void createFinishButtons(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new FormLayout());
		GridData data = new GridData(SWT.FILL, SWT.BOTTOM, true, false);	
		panel.setLayoutData(data);
		
		cancel = new Button(panel, SWT.CENTER);
		cancel.setText(Messages.Statistics_Dialog_Cancel_button);
		FormData cancelButtonFormData = new FormData();
		cancelButtonFormData.right = new FormAttachment(100, -10);
		cancelButtonFormData.bottom = new FormAttachment(100, -10);
		cancelButtonFormData.top = new FormAttachment(0, 10);
		cancelButtonFormData.width = 100;
		cancel.setLayoutData(cancelButtonFormData);
		
		calculateStatistics = new Button(panel, SWT.CENTER);
		calculateStatistics.setText(Messages.Statistics_Dialog_Calculate_button);
		calculateStatistics.setEnabled(false);
		FormData loadButtonFormData = new FormData();
		loadButtonFormData.right = new FormAttachment(cancel, -10);
		loadButtonFormData.bottom = new FormAttachment(100, -10);
		loadButtonFormData.top = new FormAttachment(0, 10);
		loadButtonFormData.width = 120;
		calculateStatistics.setLayoutData(loadButtonFormData);
	}
	
	/**
	 * Adds items to combo
	 *
	 * @param combo combo to update
	 */
	private void addDatasetItems(Combo combo) {
		for (String dataSetName : getAllDatasets()) {
			combo.add(dataSetName);
		}
	}
	
	private void addPeriodItems(Combo combo) {
		for (StatisticsPeriod period : StatisticsPeriod.values()) {
			combo.add(period.getPeriodName());
		}
	}
	
	/**
     * Forms list of Datasets
     * 
     * @return array of Datasets nodes
     */
    private ArrayList<String> getAllDatasets() {
    	ArrayList<String> result = new ArrayList<String>();
        Transaction tx = NeoUtils.beginTransaction();
        try {        	
        	Traverser allDatasetTraverser = NeoCorePlugin.getDefault().getProjectService().getAllDatasetTraverser(
                    NeoServiceProvider.getProvider().getService().getReferenceNode());        	
            for (Node node : allDatasetTraverser) {
            	result.add((String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME));
            }
            
            return result;
        } finally {
            tx.finish();
        }
    }
	
}
