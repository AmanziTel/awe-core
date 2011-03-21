package org.amanzi.awe.ui.timeformat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * This class creates a Group container which contains tools for select format
 * @author Kruglik_A
 *
 */
public class Format{
	
	
	private Label exampleFormatLabel; //label with example format 
	
	private Date date = new Date(); 
	private Combo combo;
	private IPreferenceStore preferenceStore;
	private String key; // key to default formats in preferenceStore
	private String value; // value of default formats in preferenceStore
	private String exampleKey; // key to example format in preferenceStore
	private String exampleValue; // value of example format in preferenceStore
	

	public Format(Composite parent, String aKey, String aValue, IPreferenceStore aPreferenceStore) {
		this.preferenceStore = aPreferenceStore;
		this.key = aKey;
		this.value = aValue;
		this.exampleKey = "example_"+key;
		this.exampleValue = value.split("@")[0];
	
		
		
		
		Group group = new Group(parent, SWT.NONE);
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);

		group.setLayout(fillLayout);
		
		setDefaultPreference();
		
		Composite composite_1 =new Composite(group, SWT.NONE);		 
		GridLayout gridLayout = new GridLayout(3, true);
		gridLayout.marginTop = 20;
		composite_1.setLayout(gridLayout);
		
		Label label_1 = new Label(composite_1,SWT.LEFT);
		label_1.setText("Enter "+key+ " format : ");
		final Text text = new Text(composite_1, SWT.BORDER|SWT.LEFT);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button button = new Button(composite_1, SWT.PUSH);
		button.setText("Add");
		
		Label label_2 = new Label(composite_1, SWT.LEFT);
		label_2.setText("or choose from list : ");
			
		combo = new Combo(composite_1, SWT.NONE);
		String[] defaultComboFields = preferenceStore.getString(key).split("@");
		for (String def : defaultComboFields){
			combo.add(def);
		}
		
		combo.select(combo.indexOf(preferenceStore.getString(exampleKey)));
	    combo.setVisibleItemCount(4);
	    
	    Composite composite_2 = new Composite(group, SWT.SHADOW_ETCHED_IN);	 
	    GridLayout gridLayout_2 = new GridLayout(2, true);
	    composite_2.setLayout(gridLayout_2);

	    Label label_3 = new Label(composite_2,SWT.HORIZONTAL|SWT.CENTER);
	    label_3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    label_3.setText("Example: ");

	    exampleFormatLabel = new Label(composite_2, SWT.CENTER);
	    exampleFormatLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    try{
	    	exampleFormatLabel.setText(new SimpleDateFormat(preferenceStore.getString(exampleKey),
	    			new Locale("en","US","WINDOWS")).format(date));
	    }catch (Exception exp){}

	    combo.addSelectionListener(new SelectionAdapter() {

	    	public void widgetSelected(SelectionEvent e) {
	    		try{
	    			exampleFormatLabel.setText(new SimpleDateFormat(combo.getText(),
	    					new Locale("en","US","WINDOWS")).format(date)); 

	    		}
	    		catch(Exception e2){
	    			exampleFormatLabel.setText("uncorrect format");
	    		}
	    	}
	    });

	    button.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		String str = text.getText();
	    		try{
	    			exampleFormatLabel.setText(new SimpleDateFormat(str,
	    					new Locale("en","US","WINDOWS")).format(date));
	    			if (combo.indexOf(str)==-1){
		    			combo.add(str);
		    			String other =preferenceStore.getString(key)+"@"+str;
		    			
		    			preferenceStore.setValue(key, other);		    			
		    		}
	    			combo.select(combo.indexOf(str));
	    			
	    		}
	    		catch(Exception e2){
	    			exampleFormatLabel.setText("uncorrect format");
	    		}
	    	}
	    });
	   
	}
	
	public Combo getCombo(){
		return combo;
	}
	
	public void setDefFormatLabel(){
		
		try{
	    	exampleFormatLabel.setText(new SimpleDateFormat(preferenceStore.getDefaultString(exampleKey),
	    			new Locale("en","US","WINDOWS")).format(date));
	    }catch (Exception exp){}
	}
	
	public void setDefCombo(){
		combo.clearSelection();
		combo.removeAll();
		String[] defaultComboFields = preferenceStore.getDefaultString(key).split("@");
		for (String def : defaultComboFields){
			combo.add(def);
		}
		
		combo.select(combo.indexOf(preferenceStore.getString(exampleKey)));
	}
	
	private void setDefaultPreference(){
				
		preferenceStore.setDefault(exampleKey, exampleValue);

		preferenceStore.setDefault(key,value);
		
	}

}
