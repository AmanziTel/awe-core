/*******************************************************************************
 * Copyright (c) 2006 RadRails.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.aptana.rdt.internal.rake.view;

import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.ui.RubyExplorerTracker;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyProjectSelectionAction;
import org.rubypeople.rdt.internal.ui.RubyExplorerTracker.IRubyProjectListener;
import org.rubypeople.rdt.internal.ui.text.RubyColorManager;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallChangedListener;
import org.rubypeople.rdt.launching.RubyRuntime;

import com.aptana.rdt.rake.IRakeHelper;
import com.aptana.rdt.rake.PreferenceConstants;
import com.aptana.rdt.rake.RakePlugin;

/**
 * RakeTasksView
 * 
 * @author Kevin Sawicki (added labels)
 */
public class RakeTasksView extends ViewPart implements IVMInstallChangedListener, IPropertyChangeListener,
		IRubyProjectListener
{

	private static final String PROJECT = "Current Ruby Project: ";

	private StackLayout fViewLayout;
	private Composite fRakeTasksView;
	private Label projectNameLabel;
	private RubyProjectSelectionAction projectSelectionAction;
	private Label fSpecifyRakePath;
	private Label fSelectRailsProjectView;
	private Composite fParent;

	private Composite tasksComp;
	private Label tasksLabel;
	private Combo fTasksCombo;

	private Label paramLabel;
	private Text fParamText;

	private Label descriptionText;
	private Text fDescripText;

	private RubyColorManager fColorManager;

	private Map<String, String> fTasks;

	private IProject project;

	private Job updateRakeTasksJob;

	/**
	 * RakeTasksView
	 */
	public RakeTasksView()
	{
		super();
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		fColorManager = new RubyColorManager(true);
		fParent = parent;
		fViewLayout = new StackLayout();
		parent.setLayout(fViewLayout);

		fRakeTasksView = new Composite(parent, SWT.NULL);
		fRakeTasksView.setLayout(new GridLayout(2, false));
		fRakeTasksView.setLayoutData(new GridData(GridData.FILL_BOTH));

		createRakeControls(fRakeTasksView);

		fSpecifyRakePath = new Label(parent, SWT.NULL);
		fSpecifyRakePath.setText(RakeViewMessages.SpecifyRakePath_message);
		fSelectRailsProjectView = new Label(parent, SWT.NULL);
		fSelectRailsProjectView.setText(RakeViewMessages.SelectRubyProject_message);

		if (emptyRakePath())
		{
			fViewLayout.topControl = fSpecifyRakePath;
		}
		else
		{
			if (getSelectedRubyProject() != null)
			{
				fViewLayout.topControl = fRakeTasksView;
			}
			else
			{
				fViewLayout.topControl = fSelectRailsProjectView;
			}
		}
		parent.layout();

		getProjectTracker().addProjectListener(this);
		RubyRuntime.addVMInstallChangedListener(this);
		RakePlugin.getDefault().getPluginPreferences().addPropertyChangeListener(this);

		projectSelectionAction = new RubyProjectSelectionAction();
		projectSelectionAction.setListener(this);
		IActionBars bars = getViewSite().getActionBars();
		bars.getToolBarManager().add(projectSelectionAction);

		IProject project = getProjectTracker().getSelectedRubyProject();
		IProject[] projects = RubyCore.getRubyProjects();
		if (project != null)
		{
			this.projectSelected(project);

		}
		else if (projects != null && projects.length > 0)
		{
			this.projectSelected(projects[0]);
		}
	}

	/**
	 * Create the rake controls
	 * 
	 * @param parent
	 */
	protected void createRakeControls(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NULL);

		// Create the layout for the view
		GridLayout compLayout = new GridLayout();
		compLayout.numColumns = 4;
		compLayout.marginHeight = 0;
		compLayout.marginWidth = 0;
		comp.setLayout(compLayout);
		GridData compLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		comp.setLayoutData(compLayoutData);

		projectNameLabel = new Label(comp, SWT.LEFT);
		GridData pnlData = new GridData(SWT.FILL, SWT.FILL, true, false);
		pnlData.horizontalSpan = 4;
		projectNameLabel.setText(PROJECT);
		projectNameLabel.setForeground(fColorManager.getColor(new RGB(128, 128, 128)));
		projectNameLabel.setLayoutData(pnlData);

		// Create the combo box of tasks
		tasksComp = new Composite(comp, SWT.LEFT);
		GridLayout tcLayout = new GridLayout(2, false);
		tcLayout.marginHeight = 0;
		tcLayout.marginWidth = 0;
		tasksComp.setLayout(tcLayout);
		tasksComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		tasksLabel = new Label(tasksComp, SWT.LEFT);
		tasksLabel.setText("Tasks:");
		fTasksCombo = new Combo(tasksComp, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData tasksComboData = new GridData(SWT.FILL, SWT.FILL, true, false);
		fTasksCombo.setLayoutData(tasksComboData);
		fTasksCombo.setVisibleItemCount(20);

		fTasksCombo.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
				// Do nothing
			}

			public void widgetSelected(SelectionEvent e)
			{
				Combo c = (Combo) e.widget;
				String descrip = (String) fTasks.get(c.getText());
				fDescripText.setText(descrip);
			}

		});

		// Create the parameters text field
		paramLabel = new Label(comp, SWT.LEFT);
		paramLabel.setText("Parameters:");
		fParamText = new Text(comp, SWT.BORDER);
		GridData paramTextData = new GridData(GridData.FILL_HORIZONTAL);
		fParamText.setLayoutData(paramTextData);

		fParamText.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				// Do nothing
			}

			public void keyReleased(KeyEvent e)
			{
				// Take action if Enter was pressed
				if (e.character == SWT.CR)
				{
					runRakeTask();
				}
			}
		});

		// Create the Go button
		Button genButton = new Button(comp, SWT.PUSH);
		genButton.setText("Go");

		// Run the task when the button is clicked
		genButton.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{
				runRakeTask();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				// Do nothing
			}

		});

		// Blank
		Label hint = new Label(comp, SWT.WRAP);
		hint
				.setText("If dropdown is empty, you may not have a Rakefile in the selected Ruby project. If you do, you can hit yellow arrow 'refresh' icon to force a refresh of the listing.");
		hint.setForeground(fColorManager.getColor(new RGB(128, 128, 128)));
		GridData hintData = new GridData();
		hintData.widthHint = 300;
		hintData.verticalAlignment = SWT.TOP;
		hint.setLayoutData(hintData);

		// Create the text area for the task descriptions
		descriptionText = new Label(comp, SWT.LEFT);
		descriptionText.setText("Description:");
		descriptionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		fDescripText = new Text(comp, SWT.WRAP | SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		fDescripText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose()
	{
		super.dispose();
		fColorManager.dispose();
		getProjectTracker().removeProjectListener(this);
		RubyRuntime.removeVMInstallChangedListener(this);
		RakePlugin.getDefault().getPluginPreferences().removePropertyChangeListener(this);
	}

	private RubyExplorerTracker getProjectTracker()
	{
		return RubyPlugin.getDefault().getProjectTracker();
	}

	private void runRakeTask()
	{
		IProject project = getSelectedRubyProject();
		if (project != null)
		{
			getRakeTasksHelper().runRakeTask(project, fTasksCombo.getText(), fParamText.getText());
		}
	}

	private IProject getSelectedRubyProject()
	{
		return this.project;
	}

	private IRakeHelper getRakeTasksHelper()
	{
		return RakePlugin.getDefault().getRakeHelper();
	}

	/**
	 * Updates the rake tasks
	 */
	protected void updateRakeTasks(final boolean force)
	{
		fTasksCombo.removeAll();
		if (project == null)
			return;
		// Part of ROR-1098 - We shouldn't allow multiple instances of this job to run simultaneously!
		if (updateRakeTasksJob != null)
		{
			updateRakeTasksJob.cancel();
		}
		updateRakeTasksJob = new Job("Update rake tasks")
		{
			protected IStatus run(IProgressMonitor monitor)
			{
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;				
				monitor.beginTask("Loading rake tasks", 2);
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
				{
					public void run()
					{
						if (fDescripText != null && !fDescripText.isDisposed())
						{
							fDescripText.setText("Please wait, loading rake tasks...");
						}
					}
				});
				monitor.worked(1);
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;
				fTasks = getRakeTasksHelper().getTasks(getSelectedRubyProject(), force);
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
				{
					public void run()
					{
						Collection<String> sortedItems = new TreeSet<String>(fTasks.keySet());
						if (!fTasksCombo.isDisposed())
						{
							fTasksCombo.setItems(sortedItems.toArray(new String[sortedItems.size()]));
							if (sortedItems.size() > 0)
								fTasksCombo.setText(sortedItems.iterator().next());
						}
						if (!fDescripText.isDisposed())
							fDescripText.setText("");
					}
				});
				monitor.worked(1);
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		updateRakeTasksJob.schedule();
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
		fTasksCombo.setFocus();
	}

	/**
	 * @see org.rubypeople.rdt.launching.IVMInstallChangedListener#defaultVMInstallChanged(org.rubypeople.rdt.launching.IVMInstall,
	 *      org.rubypeople.rdt.launching.IVMInstall)
	 */
	public void defaultVMInstallChanged(IVMInstall previous, IVMInstall current)
	{
		handlePossibleRakeChange(getRakePath());
	}

	private String getRakePath()
	{
		return RakePlugin.getDefault().getRakePath();
	}

	/**
	 * @see org.rubypeople.rdt.launching.IVMInstallChangedListener#vmAdded(org.rubypeople.rdt.launching.IVMInstall)
	 */
	public void vmAdded(IVMInstall newVm)
	{
		// ignore

	}

	/**
	 * @see org.rubypeople.rdt.launching.IVMInstallChangedListener#vmChanged(org.rubypeople.rdt.launching.PropertyChangeEvent)
	 */
	public void vmChanged(org.rubypeople.rdt.launching.PropertyChangeEvent event)
	{
		// ignore

	}

	/**
	 * @see org.rubypeople.rdt.launching.IVMInstallChangedListener#vmRemoved(org.rubypeople.rdt.launching.IVMInstall)
	 */
	public void vmRemoved(IVMInstall removedVm)
	{
		// ignore
	}

	/**
	 * propertyChange
	 * 
	 * @param event
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getProperty().equals(PreferenceConstants.PREF_RAKE_PATH))
		{
			handlePossibleRakeChange(event.getNewValue());
		}
	}

	private void handlePossibleRakeChange(final Object value)
	{
		if (!fParent.isDisposed())
		{
			Display.getDefault().asyncExec(new Runnable()
			{

				public void run()
				{
					if (value == null || value.equals(""))
					{
						fViewLayout.topControl = fSpecifyRakePath;
					}
					else
					{
						fViewLayout.topControl = fRakeTasksView;
					}
					fParent.layout();
				}

			});
		}
	}

	/**
	 * Sets the widget enablement
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled)
	{
		fDescripText.setEnabled(enabled);
		fTasksCombo.setEnabled(enabled);
		fParamText.setEnabled(enabled);
	}

	/**
	 * @see org.rubypeople.rdt.internal.ui.RubyExplorerTracker.IRubyProjectListener#projectSelected(org.eclipse.core.resources.IProject)
	 */
	public void projectSelected(IProject project)
	{
		if (fParent.isDisposed())
		{
			return;
		}
		if (project != null && RubyCore.isRubyProject(project) && project.exists() && project.isOpen())
		{
			projectNameLabel.setText(PROJECT + project.getName());
			this.project = project;
			setEnabled(true);
		}
		else if (project == null || !project.exists())
		{
			fViewLayout.topControl = fSelectRailsProjectView;
			projectNameLabel.setText(PROJECT + "<Select a Rails project>");
			setEnabled(false);
			this.project = null;
		}
		if (emptyRakePath())
		{
			fViewLayout.topControl = fSpecifyRakePath;
		}
		else
		{
			fViewLayout.topControl = fRakeTasksView;
		}
		updateRakeTasks(false);
		fParent.layout();
	}

	private boolean emptyRakePath()
	{
		return getRakePath() == null || getRakePath().equals("");
	}
}
