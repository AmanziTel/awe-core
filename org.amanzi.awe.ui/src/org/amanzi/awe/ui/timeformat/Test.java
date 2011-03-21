package org.amanzi.awe.ui.timeformat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import org.amanzi.awe.ui.AweUiPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class Test extends PreferencePage implements IWorkbenchPreferencePage {

	public Test() {
		super();
		preferenceStore = AweUiPlugin.getDefault().getPreferenceStore();
		
		setDescription("Select time and date format: ");
	}

	public static final String TIME_KEY = "time";
	public static final String DATE_KEY = "date";
	public static final String TIME_VALUE_DEF = "h:mm a@hh 'o''clock' a, zzzz@K:mm a, z@H:mm:ss:SSS";
	public static final String DATE_VALUE_DEF = "yyyy.MM.dd@EEE, MMM d, ''yy@dd-MMM-yyyy@yyyy.MMM.dd";
	private IPreferenceStore preferenceStore; 
	private Format timeFormat;
	private Format dateFormat;
	
	
	
	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}
	
	
	protected Control createContents(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.NONE);
		
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		composite.setLayout(fillLayout);
		
		timeFormat = new Format(composite, TIME_KEY, TIME_VALUE_DEF, preferenceStore);
		dateFormat = new Format(composite, DATE_KEY, DATE_VALUE_DEF, preferenceStore);
		
	    return composite;
	}

	public void performApply() { 
		
		preferenceStore.setValue("default_"+TIME_KEY, timeFormat.getCombo().getItem(timeFormat.getCombo().getSelectionIndex()));
		preferenceStore.setValue("default_"+DATE_KEY, dateFormat.getCombo().getItem(dateFormat.getCombo().getSelectionIndex()));
		
	}

	public void performDefaults(){
		preferenceStore.setToDefault("example_"+TIME_KEY);
		preferenceStore.setToDefault("example_"+DATE_KEY);
		
		preferenceStore.setToDefault(TIME_KEY);
		preferenceStore.setToDefault(DATE_KEY);
		
		timeFormat.setDefFormatLabel();
		dateFormat.setDefFormatLabel();
		
		timeFormat.setDefCombo();
		dateFormat.setDefCombo();
		
	}

	
}
