package org.amanzi.awe.ui.timeformat;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.amanzi.awe.ui.AweUiPlugin;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class TimeFormat extends PreferencePage implements
		IWorkbenchPreferencePage {

	public TimeFormat() {
		super();
		setPreferenceStore(AweUiPlugin.getDefault().getPreferenceStore());
		setDescription("Change time format: ");
	}

	

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}


	private static final String FORM_1 = "dd.MM.yy";
	private static final String FORM_2 = "yyyy.MM.dd G 'at' hh:mm:ss z";
	private static final String FORM_3 = "EEE, MMM d, ''yy";
	private static final String FORM_4 = "h:mm a";
	private static final String FORM_5 = "H:mm";
	private static final String FORM_6 = "H:mm:ss:SSS";
	private static final String FORM_7 = "K:mm a,z";
	private static final String FORM_8 = "yyyy.MMMMM.dd GGG hh:mm aaa";
	
	/*public enum FormType{
		FORM_1,FORM_2 ,FORM_3 ,FORM_4 ,FORM_5,
		FORM_6 , FORM_7, FORM_8
	}*/

	  
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
	    
	    //IPreferenceStore preferenceStore = getPreferenceStore();
		composite.setLayout(new GridLayout(2, true));
	   

	    final Combo combo = new Combo(composite, SWT.READ_ONLY);
	    String items[] = {FORM_1 ,FORM_2 ,FORM_3 ,FORM_4 ,FORM_5,
	    		FORM_6 , FORM_7, FORM_8 };
	    combo.setItems(items);
	    combo.setVisibleItemCount(8);
	    final Date date = new Date();
	    final Label label = new Label(composite, SWT.LEFT);
	    label.setText(date.toString());
	    
	    combo.addSelectionListener(new SelectionAdapter() {
		
	        public void widgetSelected(SelectionEvent e) {
	           if (combo.getText().equals(FORM_1))
	        	   label.setText(new SimpleDateFormat(FORM_1,
	   	    			new Locale("en","US","WINDOWS")).format(date)); 
	           if (combo.getText().equals(FORM_2))
	        	   label.setText(new SimpleDateFormat(FORM_2,
	   	    			new Locale("en","US","WINDOWS")).format(date));
	           if (combo.getText().equals(FORM_3))
	        	   label.setText(new SimpleDateFormat(FORM_3,
	   	    			new Locale("en","US","WINDOWS")).format(date));
	           if (combo.getText().equals(FORM_4))
	        	   label.setText(new SimpleDateFormat(FORM_4,
	   	    			new Locale("en","US","WINDOWS")).format(date));
	           if (combo.getText().equals(FORM_5))
	        	   label.setText(new SimpleDateFormat(FORM_5,
	   	    			new Locale("en","US","WINDOWS")).format(date));
	           if (combo.getText().equals(FORM_6))
	        	   label.setText(new SimpleDateFormat(FORM_6,
	   	    			new Locale("en","US","WINDOWS")).format(date));
	           if (combo.getText().equals(FORM_7))
	        	   label.setText(new SimpleDateFormat(FORM_7,
	   	    			new Locale("en","US","WINDOWS")).format(date));
	           if (combo.getText().equals(FORM_8))
	        	   label.setText(new SimpleDateFormat(FORM_8,
	   	    			new Locale("en","US","WINDOWS")).format(date));
	        }
	    });
	    
	   
	    
	   
	    
	    return composite;

	}

}
