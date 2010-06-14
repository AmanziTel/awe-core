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

package org.amanzi.awe.wizards.geoptima;

import java.util.Set;

import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class ReplayDialog extends ProcessDialog{

    private DateTime sStartTime;
    private DateTime sStartDate;
    private DateTime sEndTime;
    private DateTime sEndDate;
    private Spinner sTimeWindow;
    private Button cRepeat;
    private Button bCorrelate;
    /**
     * @param parent
     * @param title
     * @param processBtnLabel
     */
    public ReplayDialog(Shell parent) {
        super(parent,"Replay Subscriber Locations", "Start");
    }
    @Override
    protected void formdataMap(Set<Node> storedData) {
        datamap.clear();
        Transaction tx = service.beginTx();
        try{
        for (Node node : storedData) {
            //have gis node
            if (node.hasRelationship(GeoNeoRelationshipTypes.NEXT,Direction.INCOMING)){
                datamap.put(NeoUtils.getNodeName(node), node);
            }
        }
        }finally{
            tx.finish();
        }
    }
@Override
protected void createContents(final Shell shell) {
    this.shell = shell;
    shell.setLayout(new GridLayout(3, false));
    Label label = new Label(shell, SWT.NONE);
    label.setText("Select data:");
    cData = new Combo(shell, SWT.FILL | SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
    GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    layoutData.horizontalSpan=2;
    layoutData.grabExcessHorizontalSpace = true;
    cData.setLayoutData(layoutData);

    label = new Label(shell, SWT.NONE);
    label.setText("Start time:");
    sStartTime = new DateTime(shell, SWT.BORDER|SWT.TIME|SWT.LONG);
    layoutData =  new GridData(SWT.FILL, SWT.CENTER, true, false);
    layoutData.widthHint = 100;
    sStartTime.setLayoutData(layoutData);
    sStartDate= new DateTime(shell,  SWT.BORDER|SWT.DATE|SWT.LONG);
    layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    layoutData.widthHint = 100;
    sStartDate.setLayoutData(layoutData);
    label = new Label(shell, SWT.NONE);
    label.setText("End time:");
    sEndTime = new DateTime(shell, SWT.BORDER|SWT.TIME|SWT.LONG);
    layoutData =  new GridData(SWT.FILL, SWT.CENTER, true, false);
    sEndTime.setLayoutData(layoutData);
    sEndDate= new DateTime(shell,  SWT.BORDER|SWT.DATE|SWT.LONG);
    layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    sEndDate.setLayoutData(layoutData);
    label = new Label(shell, SWT.NONE);
    label.setText("Time window(sec):");
    sTimeWindow=new Spinner(shell, SWT.BORDER);
    layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
//    layoutData.horizontalSpan=2;
    sTimeWindow.setLayoutData(layoutData);   
//    new Label(shell, SWT.NONE);
//    label = new Label(shell, SWT.NONE);
    cRepeat=new Button(shell,SWT.CHECK);
    cRepeat.setText("Repeat");
    cRepeat.setSelection(true);
//    new Label(shell, SWT.NONE);
    new Label(shell, SWT.NONE);
     bCorrelate = new Button(shell, SWT.PUSH);
     bCorrelate.setEnabled(false);
    GridData gdBtnOk = new GridData(SWT.LEFT,SWT.CENTER,false,false);
    gdBtnOk.widthHint = 70;
    bCorrelate.setLayoutData(gdBtnOk);
    bCorrelate.setText(getProcessButtonLabel());
    bCorrelate.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
            processBtn();
        }

    });

    Button btnOk = new Button(shell, SWT.PUSH);
    btnOk.setText("OK");
    gdBtnOk = new GridData(SWT.LEFT,SWT.CENTER,false,false);
    gdBtnOk.widthHint = 70;
    btnOk.setLayoutData(gdBtnOk);
    btnOk.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
            status = SWT.OK;
            shell.close();
        }

    });
}
/**
 *
 */
protected void changeData() {
    
}
public static void main(String[] args) {
    Display display = new Display ();
    final Shell shell = new Shell (display);
    shell.setLayout(new FillLayout());

    Button open = new Button (shell, SWT.PUSH);
    open.setText ("Open Dialog");
    open.addSelectionListener (new SelectionAdapter () {
      @Override
    public void widgetSelected (SelectionEvent e) {
        final Shell dialog = new Shell (shell, SWT.DIALOG_TRIM);
        dialog.setLayout (new GridLayout (3, false));

        final DateTime calendar = new DateTime (dialog, SWT.CALENDAR | SWT.BORDER);
        final DateTime date = new DateTime (dialog, SWT.DATE );
        new DateTime (dialog, SWT.DATE | SWT.SHORT);
         new DateTime (dialog, SWT.DATE | SWT.MEDIUM);
        new DateTime (dialog, SWT.DATE | SWT.LONG);
         final DateTime time = new DateTime (dialog, SWT.TIME );
         new DateTime (dialog, SWT.TIME | SWT.SHORT);
         new DateTime (dialog, SWT.TIME | SWT.MEDIUM );
         new DateTime (dialog, SWT.TIME | SWT.LONG);

        new Label (dialog, SWT.NONE);
        new Label (dialog, SWT.NONE);
        Button ok = new Button (dialog, SWT.PUSH);
        ok.setText ("OK");
        ok.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, false, false));
        ok.addSelectionListener (new SelectionAdapter () {
          @Override
        public void widgetSelected (SelectionEvent e) {
            System.out.println ("Calendar date selected (MM/DD/YYYY) = " + (calendar.getMonth () + 1) + "/" + calendar.getDay () + "/" + calendar.getYear ());
            System.out.println ("Date selected (MM/YYYY) = " + (date.getMonth () + 1) + "/" + date.getYear ());
            System.out.println ("Time selected (HH:MM) = " + time.getHours () + ":" + time.getMinutes ());
            dialog.close ();
          }
        });
        dialog.setDefaultButton (ok);
        dialog.pack ();
        dialog.open ();
      }
    });
    shell.pack ();
    shell.open ();
    
    while (!shell.isDisposed ()) {
      if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
  }
}
