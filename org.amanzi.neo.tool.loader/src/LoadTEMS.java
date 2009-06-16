import org.amanzi.neo.loader.views.TEMSLoader;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;

import org.amanzi.neo.loader.views.NetworkLoader;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.neo4j.api.core.EmbeddedNeo;

import net.refractions.udig.project.ui.tool.AbstractActionTool;


public class LoadTEMS extends AbstractActionTool {

	
	public LoadTEMS() {
		// TODO Auto-generated constructor stub
	}

	public void run() {
		// TODO Auto-generated method stub
		ShowFileDialog SFD=new ShowFileDialog();
        SFD.run();

	}

	public void dispose()
	{
		// TODO Auto-generated method stub
         
	}
}
/**
 * This class demonstrates FileDialog
 */
 class ShowFileDialog {
	 
	 
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
  public void run() {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setMinimumSize(450, 350);
    shell.setText("File Dialog");
    createContents(shell);
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();
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
        dlg.setFilterNames(FILTER_NAMES);
        dlg.setFilterExtensions(FILTER_EXTS);
        String fn = dlg.open();
      
        if (fn != null) {
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
    	            for(int i=0;i<files.length;i++)
    	            {
    	            	Button selectBtn=new Button(shell, SWT.CHECK|SWT.BOTTOM);	
    	            	selectBtn.setText(files[i].getAbsolutePath());
    	            	selectBtn.setSelection(true);
    	            }
    		 }
    	 }
    });
    
    Button load = new Button(shell, SWT.PUSH);
    load.setText("Load TEMS...");
    load.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) 
      {
    	  EmbeddedNeo neo = new EmbeddedNeo("var/neo");
  		try
  		{
  			for(int j =0;j<files.length;j++)
  			{
  				TEMSLoader temsLoader = new TEMSLoader(neo,files[j].getAbsolutePath(),100);
  				temsLoader.printStats();	// stats for this load
  			}
  			//printTimesStats();	// stats for all loads
  		} catch (IOException e) {
  			System.err.println("Error loading TEMS data: "+e);
  			e.printStackTrace(System.err);
  		}finally{
  			neo.shutdown();
  		}
      
	  }});
     // }
    //});
  }
  /**
   * The application entry point
   * 
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    new ShowFileDialog().run();
  }
}