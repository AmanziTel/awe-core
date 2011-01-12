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
package org.amanzi.awe;

import java.io.File;

import org.eclipse.core.internal.jobs.Worker;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Class to choose database location
 * <p>
 *
 * </p>
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class GraphDbServiceChangeLocation {
    
    // directory to database location - composite
    private static DirectoryFieldEditor outputDir;
    // directory to database location
    private static String outputDirectory;
    // button to choose way to database
    private static Button button = null;
    // label to view the result of removing
    private static Text isDeleted = null;
    
    public static void main(String[] args) {
        createCompositeDatabaseLocation("c:/a");
    }
    
    /**
     * creating a window with a choice of database location
     *
     * @param defaultPath default path to database
     * @return
     */
    public static String createCompositeDatabaseLocation(final String defaultPath) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setSize(550, 100);
        shell.setLayout(new RowLayout());

        shell.setText("Choose database location");

        final Composite composite = new Composite(shell, SWT.NONE);
        GridLayout gridLayout = new GridLayout(3, false); 
        composite.setLayout(gridLayout);
        
        GridData gridData = new GridData(310, 15);
        
        outputDir = new DirectoryFieldEditor("editor", "Output directory: ", composite);
        outputDir.setEmptyStringAllowed(true);
        outputDir.getTextControl(composite).setLayoutData(gridData);

        outputDir.getTextControl(composite).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                outputDirectory = outputDir.getStringValue();
                try {
                    File fileOutput = new File(outputDirectory);
                    if (fileOutput.isDirectory() && 
                            fileOutput.exists()) {
                        button.setEnabled(true);
                    }
                    else {
                        button.setEnabled(false);
                    }
                }
                catch (Exception ex) {
                    
                }
            }
        });
        outputDir.setStringValue(defaultPath);
        
        button = new Button(composite, SWT.BUTTON1);
        button.setText("Choose way");
        button.setEnabled(false);
        button.addMouseListener(new MouseListener() {
            
            @Override
            public void mouseUp(MouseEvent e) {
            }
            
            @Override
            public void mouseDown(MouseEvent e) {
            }
            
            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });
        if (outputDir.getTextControl(composite).getText().equals(defaultPath)) {
            button.setEnabled(true);
            outputDirectory = defaultPath;
        }
        
        Button button2 = new Button(composite, SWT.BUTTON1);
        button2.setText("Delete default location");
        button2.addMouseListener(new MouseListener() {
            
            @Override
            public void mouseUp(MouseEvent e) {
            }
            
            @Override
            public void mouseDown(MouseEvent e) {
                boolean isDel = deleteDefaultLocation(defaultPath);
                if (isDel) {
                    isDeleted.setText("Successfully");
                }
                else {
                    isDeleted.setText("Exception");
                }
            }
            
            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });
        
        isDeleted = new Text(composite, SWT.NULL);
        
        shell.open();
        while (!shell.isDisposed()) {
          if (!display.readAndDispatch())
            display.sleep();
        }
        
        display.dispose();
        
        return outputDirectory;
    }
    
    /**
     * delete default database location
     *
     * @param path path to database
     */
    private static boolean deleteDefaultLocation(String path) {
        File file = new File(path);
        return delete(file);
    }
    
    /**
     * method to delete directory
     *
     * @param file directory to delete
     */
    private static boolean delete(File file)
    {
        if(!file.exists())
            return false;
        if(file.isDirectory())
        {
            for(File f : file.listFiles())
                delete(f);
            file.delete();
        }
        else
        {
            file.delete();
        }
        
        return true;
    }
}
