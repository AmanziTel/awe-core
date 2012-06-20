package org.amanzi.awe.views.network.view;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * <p>
 * Page for ExportToFileSettingsWizard
 * </p>
 * 
 * @author ladornaya_a
 * @since 1.0.0
 */
public class ExportToFileSettingsPage extends WizardPage {

	// default extensions
	private final static String[] DEFAULT_EXTENSION = { ".csv", ".txt" };

	// default separators
	private final static String[] DEFAULT_SEPARATOR = { "\"\\" + "t\"",
			"\",\"", "\";\"" };

	private final static String[] SEPARATORS = { "\t", ",", ";" };

	// page name

	// container for groups
	private Composite container;

	// directory
	private DirectoryFieldEditor directoryEditor;

	// text for other separator
	private Text text;

	// values
	private String extensionValue;
	private String separatorValue;
	private String directoryValue;

	/*
	 * getters for values
	 */
	public String getExtensionValue() {
		return extensionValue;
	}

	public String getSeparatorValue() {
		return separatorValue;
	}

	public String getDirectoryValue() {
		return directoryValue;
	}

	// setter for separatorValue
	public void setSeparatorValue(String extension) {
		int j = 0;
		for (String sep : DEFAULT_SEPARATOR) {
			if (sep.equals(extension)) {
				separatorValue = SEPARATORS[j];
			}
			j++;
		}
	}

	protected ExportToFileSettingsPage() {
		super(NetworkMessages.PAGE_NAME);
		setDescription(NetworkMessages.MAIN_DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {

		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;

		// directory
		Group groupD = new Group(container, SWT.NONE);
		GridLayout layoutGroupD = new GridLayout(1, true);
		GridData dataD = new GridData(SWT.FILL, SWT.FILL, true, true);
		dataD.widthHint = 200;
		groupD.setLayoutData(dataD);
		groupD.setLayout(layoutGroupD);

		directoryEditor = new DirectoryFieldEditor(
				NetworkMessages.DIRECTORY_NAME,
				NetworkMessages.DIRECTORY_LABEL, groupD);
		directoryEditor.getTextControl(groupD).addModifyListener(
				new ModifyListener() {

					@Override
					public void modifyText(ModifyEvent e) {
						directoryValue = directoryEditor.getStringValue();
						validate();
					}
				});

		// main group for radio buttons
		Group group = new Group(container, SWT.NONE);
		GridLayout layoutGroup = new GridLayout(2, true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.widthHint = 200;
		group.setLayoutData(data);
		group.setLayout(layoutGroup);

		// first group - group for extension radio buttons
		Group group1 = new Group(group, SWT.FILL);

		GridLayout layoutGroup1 = new GridLayout(1, true);
		group1.setLayout(layoutGroup1);

		createRadioExtensionGroup(group1);

		// second group - group for separator radio buttons
		Group group2 = new Group(group, SWT.FILL);

		GridLayout layoutGroup2 = new GridLayout(1, true);
		group2.setLayout(layoutGroup2);

		createRadioSeparatorGroup(group2);

		// Required to avoid an error in the system
		setControl(container);

		setPageComplete(false);

	}

	/**
	 * Create radio extension group
	 * 
	 * @param group
	 */
	private void createRadioExtensionGroup(Group group) {

		boolean first = true;

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 100;
		data.widthHint = 150;
		group.setLayoutData(data);

		Label label = new Label(group, SWT.NONE);
		label.setText(NetworkMessages.LABEL_EXTENSION);

		for (String ext : DEFAULT_EXTENSION) {
			final Button radio = new Button(group, SWT.RADIO);
			radio.setText(ext);
			if (first) {
				radio.setSelection(true);
				extensionValue = radio.getText();
				first = false;
			}
			radio.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					extensionValue = radio.getText();
					validate();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
		}
	}

	/**
	 * Create radio separator group
	 * 
	 * @param group
	 */
	private void createRadioSeparatorGroup(Group group) {

		boolean first = true;

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 130;
		data.widthHint = 150;
		group.setLayoutData(data);

		Label label = new Label(group, SWT.NONE);
		label.setText(NetworkMessages.LABEL_SEPARATOR);

		for (String ext : DEFAULT_SEPARATOR) {
			final Button radio = new Button(group, SWT.RADIO);
			radio.setText(ext);
			if (first) {
				radio.setSelection(true);
				setSeparatorValue(radio.getText());
				first = false;
			}
			radio.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					text.setText(StringUtils.EMPTY);
					text.setEnabled(false);
					setSeparatorValue(radio.getText());
					validate();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
		}

		final Button radio = new Button(group, SWT.RADIO);
		radio.setText(NetworkMessages.OTHER_SEPARATOR);

		radio.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(false);
				text.setEnabled(true);
				separatorValue = text.getText();
				validate();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		text = new Text(group, SWT.BORDER);
		text.setEnabled(false);
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				separatorValue = text.getText();
				validate();
			}
		});
	}

	/**
	 * validate main values - directoryValue and separatorValue
	 */
	private void validate() {

		if (!validateValue(directoryValue)) {
			setPageComplete(false);
			setDescription(NetworkMessages.DESCRIPTION_DIRECTORY);
		} else if (!validateValue(separatorValue)) {
			setPageComplete(false);
			setDescription(NetworkMessages.DESCRIPTION_SEPARATOR);
		} else {
			setPageComplete(true);
			setDescription(StringUtils.EMPTY);
		}

	}

	/**
	 * Check string value on null and empty
	 * 
	 * @param value
	 *            string value
	 * @return true - is validate, false - is not validate
	 */
	private boolean validateValue(String value) {
		if (value.isEmpty() || value == null) {
			return false;
		}
		return true;
	}

}
