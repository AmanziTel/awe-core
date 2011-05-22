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

package org.amanzi.awe.neostyle;

import org.amanzi.neo.core.utils.AbstractDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkFilterDefiner extends AbstractDialog<IFilterWrapper> {
    private NetworkStyleDefiner defaultStyle=new NetworkStyleDefiner();
    private Text name;
    private Button bOk;
    private Button bCancel;

    public NetworkFilterDefiner(Shell parent, String title) {
        super(parent, title);
    }

    @Override
    protected void createContents(Shell shell) {
        shell.setLayout(new GridLayout(2, true));
        Composite cmp=new Composite(shell, SWT.FILL);
        cmp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,2,1));
        createFilterGroup(cmp);
        defaultStyle.createPartControl(cmp);
        bOk = new Button(shell, SWT.PUSH);
        bOk.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        bOk.setText("OK");
        bCancel = new Button(shell, SWT.PUSH);
        bCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        bCancel.setText("Cancel");
        bOk.setEnabled(false);
        addListeners();
        shell.setLocation(100,100);
    }
        /**
     *
     */
    private void addListeners() {
    }

    /**
     *
     * @param main
     */
    private void createFilterGroup(Composite parent) {
        Group filter=new Group(parent, SWT.FILL);
        filter.setLayout(new GridLayout(2, false));
        filter.setText("Filter");
        new Label(filter,SWT.NONE).setText("Name");
        name=new Text(filter, SWT.BORDER);
//        new Label(filter,SWT.NONE).setText("Name");
        
        
    }
    public static void main(String[] args) {
        Display display = new Display ();
        final Shell shell = new Shell (display);
        new NetworkFilterDefiner(shell,"Filter definer").open();
        
        display.dispose ();

    }

}
