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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.neo.core.utils.AbstractDialog;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.filters.Filter;
import org.amanzi.neo.services.filters.FilterType;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * <p>
 * Define filter for network style
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkFilterDefiner extends AbstractDialog<IFilterWrapper> {
    private NetworkStyleDefiner defaultStyle = new NetworkStyleDefiner();
    private Text filterName;
    private Set<String> restrictedNames = new HashSet<String>();
    private String wrapperName;
    private Button bOk;
    private Button bCancel;
    private Shell shell;
    private final FilterWrapperImpl<NetworkNeoStyle> wrapper;
    private Combo types;
    private Combo filterType;
    private Label message;

    public NetworkFilterDefiner(Shell parent, String title, GeoNeo resource, String wrapperName, FilterWrapperImpl<NetworkNeoStyle> wrapper) {
        super(parent, title, SWT.RESIZE | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        this.wrapper = wrapper;
        defaultStyle.setCurStyle(wrapper.getStyle());
        defaultStyle.setGeoNeo(resource);
    }

    public void setRestrictedNames(Collection<String> names) {
        restrictedNames.clear();
        restrictedNames.addAll(names);
    }

    public String getWrapperName() {
        return wrapperName;
    }

    @Override
    protected void createContents(Shell shell) {
        this.shell = shell;
        shell.setLayout(new GridLayout(2, true));
        ScrolledComposite top1 = new ScrolledComposite(shell, SWT.V_SCROLL | SWT.H_SCROLL | SWT.V_SCROLL);
        top1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,2,1));
        Composite top = new Composite(top1, SWT.NONE);
        top1.setExpandHorizontal(true);
        top1.setExpandVertical(true);
        GridLayout gdLayout = new GridLayout(1, false);
        top.setLayout(gdLayout);
        top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        top1.setContent(top);
        message = new Label(top, SWT.NONE);
        message.setText("www");
        Composite cmp2 = new Composite(top, SWT.NONE);
        cmp2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createFilterGroup(cmp2);
        Composite cmp3 = new Composite(top, SWT.NONE);
        cmp3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        defaultStyle.createPartControl(cmp3);

        bOk = new Button(shell, SWT.PUSH);
        bOk.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        bOk.setText("OK");
        bCancel = new Button(shell, SWT.PUSH);
        bCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        bCancel.setText("Cancel");
        bOk.setEnabled(false);
        addListeners();
        top1.setMinSize(top.computeSize(SWT.DEFAULT, SWT.DEFAULT));

    }

    @Override
    protected void beforeOpen() {
        super.beforeOpen();
        defaultStyle.refresh();
    }

    private void addListeners() {
        bOk.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!validate()) {
                    return;
                }
                defaultStyle.preApply();
                wrapper.setStyle(defaultStyle.getCurStyle());
                wrapper.setFilter(getFilter());
                status = wrapper;
                shell.close();
            }
        });
        bCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                status = null;
                shell.close();
            }
        });

    }

    protected boolean validate() {
        return false;
    }

    protected Filter getFilter() {
        FilterType type = FilterType.valueOf(filterType.getText());
        Filter result = new Filter(type);
        NodeTypes nodeType = NodeTypes.getEnumById(types.getText());
        // result.setExpression(nodeType, propertyName, value)
        return result;
    }

    private void createFilterGroup(Composite parent) {
        parent.setLayout(new GridLayout());
        Group filter = new Group(parent, SWT.FILL);
        filter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        filter.setLayout(new GridLayout(2, false));
        filter.setText("Filter");
        new Label(filter, SWT.NONE).setText("Name");
        filterName = new Text(filter, SWT.BORDER);
        if (StringUtils.isNotEmpty(wrapperName)) {
            filterName.setText(wrapperName);
        }
        new Label(filter, SWT.NONE).setText("Node type");
        types = new Combo(filter, SWT.BORDER | SWT.READ_ONLY);
        types.setItems(new String[] {"site", "sector"});
        // new Label(filter,SWT.NONE).setText("Name");

    }
}
