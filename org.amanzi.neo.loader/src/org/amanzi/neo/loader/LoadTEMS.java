package org.amanzi.neo.loader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Hashtable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.refractions.udig.project.ui.tool.AbstractActionTool;

public class LoadTEMS extends AbstractActionTool {
	//private static final String[] FILTER_NAMES = {"TEMS Drive Test Export Table (*.FMT)"};
	//private static final String[] FILTER_EXTS = {"*.FMT","*.fmt"};

	public LoadTEMS() {
	}

	public void run() {
		final Display display = this.getContext().getViewportPane().getControl().getDisplay();
		this.getContext().updateUI(new Runnable(){
	
			public void run() {
				//(new ShowFileDialog("TEMS",FILTER_NAMES,FILTER_EXTS)).run();
				(new ShowTEMSFileDialog()).run(display);
			}
		});
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

}

 class ShowTEMSFileDialog {
		private String directory = null;
		public String getDirectory(){
			return directory;
		}

	 
	 Hashtable hashdata;
	 File[] files;
  // These filter names are displayed to the user in the file dialog. Note that
  // the inclusion of the actual extension in parentheses is optional, and
  // doesn't have any effect on which files are displayed.
  private static final String[] FILTER_NAMES = {
      " (*.FMT)"
     };

  // These filter extensions are used to filter which files are displayed.
  private static final String[] FILTER_EXTS = { "*.FMT"};

  /**
   * Runs the application
   */
  public void run(Display display) {
    Shell shell = new Shell(display);
    shell.setMinimumSize(450, 350);
    shell.setText("File Dialog");
    createContents(shell);
    shell.pack();
    shell.open();
  }

  /**
   * Creates the contents for the window
   * 
   * @param shell the parent shell
   */
  public void createContents(final Shell shell) {
    shell.setLayout(new GridLayout(1, false));
    
    new Label(shell, SWT.NONE).setText("File Name:");

    final Text fileName = new Text(shell, SWT.BORDER);
    GridData data = new GridData(GridData.FILL_HORIZONTAL);
    data.horizontalSpan = 4;
    fileName.setLayoutData(data);

    Button open = new Button(shell, SWT.PUSH);
    open.setText("Open file...");
    open.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        // User has selected to open a single file
        FileDialog dlg = new FileDialog(shell, SWT.OPEN);
		dlg.setText("Select a file containing TEMS drive test log data in FMT format");
        dlg.setFilterNames(FILTER_NAMES);
        dlg.setFilterExtensions(FILTER_EXTS);
		dlg.setFilterPath(directory);
        String fn = dlg.open();
      
        if (fn != null) {
        	directory = dlg.getFilterPath();
          fileName.setText(fn);
        }
        
        File dir = new File(fileName.getText());
        
        if(dir.isFile())
        {
            files=new File[1];
            files[0]=dir;
        }
      }
    });

    
    Button openDir = new Button(shell, SWT.PUSH);
    openDir.setText("Open from folder...");
    openDir.addSelectionListener(new SelectionAdapter()
    {
    	 public void widgetSelected(SelectionEvent event) 
    	 {
    		 DirectoryDialog dirDialog= new DirectoryDialog(shell);
    		 String dirDlg= dirDialog.open();
    		 File dir = new File(dirDlg);
    		 if(dir.isDirectory())
    		 {
    			  FilenameFilter filter = new FilenameFilter() {
    	                public boolean accept(File dir, String name) {
    	                    return name.endsWith(".FMT");
    	                }
    	            };
    	            files = dir.listFiles(filter);
    	            hashdata=new Hashtable(files.length);
    	            for(int i=0;i<files.length;i++)
    	            {
    	            	final Button selectBtn=new Button(shell, SWT.CHECK|SWT.BOTTOM);	
    	            	selectBtn.setText(files[i].getAbsolutePath());
    	            	selectBtn.setSelection(true);
    	            	hashdata.put(files[i].getAbsolutePath(), 1);
    	            	selectBtn.addListener(SWT.Selection, new Listener(){

							public void handleEvent(Event event) 
							{
								if(selectBtn.getSelection())
								{
									hashdata.put(selectBtn.getText(), 1);
								}
								else
								{
									hashdata.put(selectBtn.getText(),0);
								}
							}});
    	            	
    	            }
    		 }
    	 }
    });
    
    Button load = new Button(shell, SWT.PUSH);
    load.setText("Load TEMS...");
    load.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) 
      {
  		try
  		{
  			for(int j =0;j<files.length;j++)
  			{
  				if(files.length>1 && hashdata.get(files[j].getAbsolutePath())=="1")
  				{
  					TEMSLoader temsLoader = new TEMSLoader(files[j].getAbsolutePath());
  					temsLoader.run();
  	  				temsLoader.printStats();	// stats for this load
  				}
  				else
  				{
  					TEMSLoader temsLoader = new TEMSLoader(files[j].getAbsolutePath());
  					temsLoader.run();
  	  				temsLoader.printStats();	// stats for this load
  				}
  			}
  			//printTimesStats();	// stats for all loads
  		} catch (IOException e) {
  			System.err.println("Error loading TEMS data: "+e);
  			e.printStackTrace(System.err);
  		}
      
	  }});
     // }
    //});
  }
}