package org.amanzi.awe.catalog.neo.actions;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class NeoDirSelector 
{
	
	IProgressMonitor monitor;
	
	
	public void run(Display display)
	{
    Shell shell = new Shell(display);
    shell.setText("File Dialog");
    createContents(shell);
    shell.pack();
    shell.open();
    //TODO: Do we need this if we did not create the display?
    /*
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    */
	
	}

	private void createContents(final Shell shell) 
	{// TODO Auto-generated method stub
		 shell.setLayout(new GridLayout(5, true));
		 Button openDir = new Button(shell, SWT.PUSH);
		    openDir.setText("Select Neo database location...");
		    openDir.addSelectionListener(new SelectionAdapter()
		    {
		    	 public void widgetSelected(SelectionEvent event) 
		    	 {
		    		   DirectoryDialog dirDlg=new DirectoryDialog(shell);
		    		   String dirLocation=dirDlg.open();
		    		 if(dirLocation!=null)
		    		 {
		    		    File neoLocationFile=new File(dirLocation);
		    		 if(neoLocationFile.isDirectory())
		    		 {
		    			if(neoLocationFile.list().length>0)
		    			{
		    				for(int j=0;j<neoLocationFile.list().length;j++)
		    				{
		    					if(neoLocationFile.list()[j].equals("neostore.nodestore"))
		    					{
		    						//here should be loaded data from Neo database
		    						System.out.println("Starting NeoService !!!");
		    						startService(dirLocation);
		    						shell.close();
		    						//break;
		    					}
		    				}
		    			} 
		    		 }
		    		 }
		    	 }
		    });
	}
	
	
	@SuppressWarnings("deprecation")
	private void startService(String neoLocationFile)
	{
	    	monitor = new NullProgressMonitor(); 
		    Map<String,Serializable> params = new HashMap<String,Serializable>();
	        params.put( NeoServiceExtension.URL_KEY,neoLocationFile );
	        params.put( NeoServiceExtension.CLASS_KEY,URL.class );
	        
	        List<IService> match = CatalogPlugin.getDefault().getServiceFactory().acquire( params );
	        if( !match.isEmpty()){
	            IService service = match.get(0);
	            
	            ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
	    		IService found = catalog.getById( NeoService.class,service.getIdentifier(), monitor );
	    		if( found != null ){
	    		   return; // already loaded!
	    		}
	    		ICatalog local = CatalogPlugin.getDefault().getLocalCatalog();
	    		try
	    		{
	    		local.add( service );
	    		}
	    		catch(Exception ex)
	    		{
	    			System.err.println("Error service cannot start!");
	    		}
	        }         
		
		monitor.done();
		
	}
	

}
