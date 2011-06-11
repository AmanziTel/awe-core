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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.neo.core.utils.AbstractDialog;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.filters.Filter;
import org.amanzi.neo.services.filters.FilterType;
import org.amanzi.neo.services.utils.Utils;
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

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Define filter for network style
 * </p>.
 *
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkFilterDefiner extends AbstractDialog<IFilterWrapper> {
    
    /** The default style. */
    private NetworkStyleDefiner defaultStyle = new NetworkStyleDefiner();
    
    /** The filter name. */
    private Text filterName;
    
    /** The restricted names. */
    private Set<String> restrictedNames = new HashSet<String>();
    
    /** The wrapper name. */
    private String wrapperName;
    
    /** The b ok. */
    private Button bOk;
    
    /** The b cancel. */
    private Button bCancel;
    
    /** The shell. */
    private Shell shell;
    
    /** The wrapper. */
    private final FilterWrapperImpl<NetworkNeoStyle> wrapper;
    
    /** The types. */
    private Combo types;
    
    /** The filter type. */
    private Combo filterType;
    
    /** The message. */
    private Label message;
    
    /** The property. */
    private Text property;
    
    /** The value. */
    private Text value;
    
    /** The valuetypes. */
    private Combo valuetypes;
    
    /** The value types map. */
    private final Map<String,Class<? extends Serializable>> valueTypesMap=new HashMap<String, Class<? extends Serializable>>();

    /**
     * Instantiates a new network filter definer.
     *
     * @param parent the parent
     * @param title the title
     * @param resource the resource
     * @param wrapperName the wrapper name
     * @param wrapper the wrapper
     */
    public NetworkFilterDefiner(Shell parent, String title, GeoNeo resource, String wrapperName, FilterWrapperImpl<NetworkNeoStyle> wrapper) {
        super(parent, title, SWT.RESIZE | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        this.wrapperName = wrapperName;
        this.wrapper = wrapper;
        defaultStyle.setCurStyle(wrapper.getStyle());
        defaultStyle.setGeoNeo(resource);
        valueTypesMap.put("String", String.class);
        valueTypesMap.put("Integer", Integer.class);
        valueTypesMap.put("Double", Double.class);
        valueTypesMap.put("Float", Float.class);
    }

    /**
     * Sets the restricted names.
     *
     * @param names the new restricted names
     */
    public void setRestrictedNames(Collection<String> names) {
        restrictedNames.clear();
        restrictedNames.addAll(names);
    }

    /**
     * Gets the wrapper name.
     *
     * @return the wrapper name
     */
    public String getWrapperName() {
        return wrapperName;
    }

    /**
     * Creates the contents.
     *
     * @param shell the shell
     */
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
        message.setText("");
        message.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
        
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
//        bOk.setEnabled(false);
        addListeners();
        top1.setMinSize(top.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }
@Override
protected void changeShellSize(Shell shell, Shell parentShell) {
    int x = Math.min(parentShell.getSize().x-100, shell.getSize().x);
    int y = Math.min(parentShell.getSize().y-100, shell.getSize().y);
    shell.setSize(x,y);
    
}
    /**
     * Before open.
     */
    @Override
    protected void beforeOpen() {
        super.beforeOpen();
        refresh();
    }

    /**
     * Adds the listeners.
     */
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

    /**
     * Validate.
     *
     * @return true, if successful
     */
    protected boolean validate() {
        if (filterType.getSelectionIndex()<0){
            message.setText("Incorrect filter type");
            return false;
        }
        wrapperName=filterName.getText().trim();
        if (wrapperName.isEmpty()||restrictedNames.contains(wrapperName)){
            message.setText("Incorrect filter name");
            return false;           
        }
        int id = types.getSelectionIndex();
        if (id<0){
            message.setText("Node type should be defined.");
            return false;           
        }
        id = filterType.getSelectionIndex();
        if (id<0){
            message.setText("Filter type should be defined.");
            return false;           
        }
//        id = valuetypes.getSelectionIndex();
//        if (id<0){
//            message.setText("Value type should be defined.");
//            return false;           
//        }        
        message.setText("");
        return true;
    }

    /**
     * Gets the filter.
     *
     * @return the filter
     */
    protected Filter getFilter() {
        FilterType type = FilterType.valueOf(filterType.getText());
        Filter result = new Filter(type);
        NodeTypes nodeType = NodeTypes.getEnumById(types.getText());
        Serializable value=getValue();
        String propertyName=property.getText().trim();
        result.setExpression(nodeType, propertyName, value);
        // result.setExpression(nodeType, propertyName, value)
        return result;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    private Serializable getValue() {
        Class klass=getValueClass();
        if (klass!=null){
            String valueStr = value.getText();
            if (String.class==klass){
                return valueStr;
            }
            try {
                return Utils.getNumberValue(klass, valueStr);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            
        }
        return null;
    }

    /**
     * Gets the value class.
     *
     * @return the value class
     */
    private Class getValueClass() {
        Class result=null;
        String name=valuetypes.getText();
        for (Entry<String,Class<? extends Serializable>>  entry:valueTypesMap.entrySet()){
            if (entry.getKey().equals(name)){
                result=entry.getValue();
                break;
            }
        }
        return result;
    }

    /**
     * Creates the filter group.
     *
     * @param parent the parent
     */
    private void createFilterGroup(Composite parent) {
        parent.setLayout(new GridLayout());
        Group filter = new Group(parent, SWT.FILL);
        filter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        filter.setLayout(new GridLayout(2, false));
        filter.setText("Filter");
        new Label(filter, SWT.NONE).setText("Name");
        filterName = new Text(filter, SWT.BORDER);
        filterName.setText(wrapperName);
        filterName.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));

        new Label(filter, SWT.NONE).setText("Type");
        filterType = new Combo(filter, SWT.BORDER| SWT.READ_ONLY);
        filterType.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
        
        new Label(filter, SWT.NONE).setText("Node type");
        types = new Combo(filter, SWT.BORDER | SWT.READ_ONLY);
        types.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
       

        new Label(filter, SWT.NONE).setText("Property name");
        property = new Text(filter, SWT.BORDER);
        property.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
        
        new Label(filter, SWT.NONE).setText("Value");
        value = new Text(filter, SWT.BORDER);
        value.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
        
        new Label(filter, SWT.NONE).setText("Value type");
        valuetypes = new Combo(filter, SWT.BORDER | SWT.READ_ONLY);
        valuetypes.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
    }

    /**
     * Refresh.
     */
    private void refresh() {
        defaultStyle.refresh();
        valuetypes.setItems(valueTypesMap.keySet().toArray(new String[0]));
        types.setItems(new String[] {"site", "sector"});
        String[] items=new String[FilterType.values().length];
        for (int i = 0; i < items.length; i++) {
            items[i]=FilterType.values()[i].name();
        }
        filterType.setItems(items);
        Filter filtr= (Filter)wrapper.getFilter();
        Serializable val = filtr.getValue();
        value.setText("");
        if (val!=null){
            for (Entry<String,Class<? extends Serializable>>entry:valueTypesMap.entrySet()){
                if (entry.getValue()==val.getClass()){
                    valuetypes.setText(entry.getKey());
                    break;
                }
            }
            value.setText(val.toString());
            
        }
        
        FilterType type = filtr.getFilterType();
        if (type!=null){
            filterType.setText(type.name());
        }
        
        String property=filtr.getPropertyName();
        if (StringUtils.isNotEmpty(property)) {
            this.property.setText(property);
        }
        
        if (StringUtils.isNotEmpty(wrapperName)) {
            filterName.setText(wrapperName);
        }   
        INodeType nodeType = wrapper.getFilter().getNodeType();
        if (nodeType!=null){
            types.setText(nodeType.getId());
        }
    }
}
