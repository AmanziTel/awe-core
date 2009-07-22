package org.amanzi.splash.jfreechart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;


/**
 * @author Anthony Wachira
 *
 */
public class ChartReportArea extends ViewPart {

	public static Composite comp = null;
	//public static JSONObject json = null;
	
	/** 
	 * This method is called the time the view is loaded. The static Composite 
	 * property is allocated the value of the Composite, that will be used when 
	 * displaying charts
	 */
	@Override
	public void createPartControl(Composite cmp) {
		
		comp = cmp;
		
		//setting the Layout
		comp.setLayout(new FillLayout(SWT.VERTICAL));
	    
		try{
			//createJSON();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
	}
		/**
         * In this method the InputStream is converted to String using the 
         * BufferedReader.readLine()method. 
         * The returned string is then used in the constructor of the JSONObject
         */
	
    public String convertStreamToString(InputStream is) {
       
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        
        try {
        	
            while ((line = reader.readLine()) != null) {
            	
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        	
            e.printStackTrace();
            
        } finally {
        	
            try {
            	
                is.close();
                
            } catch (IOException e) {
            	
                e.printStackTrace();
            }
        }
 
        return sb.toString();
    }
    

	@Override
	protected void finalize() throws Throwable {
		
		super.finalize();
		
	}

	 @Override
	public void setFocus() {
		
		//Nothing to do currently
		
	}
	
}
