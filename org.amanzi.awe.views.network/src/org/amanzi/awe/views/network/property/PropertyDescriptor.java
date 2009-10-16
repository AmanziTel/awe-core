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
package org.amanzi.awe.views.network.property;

import org.amanzi.awe.views.network.NetworkTreePluginMessages;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class PropertyDescriptor implements IPropertyDescriptor {

    /*
     * key of property
     */
    private Object key;
    
    /*
     * name of property
     */
    private String name;
    
    /*
     * category of property
     */
    private String category;
    
    /*
     * Class of property
     */
    private Class<?> cls = null;
    
    /**
     * A constant, empty array, to be used instead of a null array.
     */
    private final static String[] EMPTY_ARRAY = new String[0];
    
    /*
     * Label provider for Properties
     */
    private final static NetworkPropertyLabelProvider labelProvider = new NetworkPropertyLabelProvider();
    
    /**
     * Create a Neo property cell.
     * @param key
     *            the key of the property
     * @param name
     *            the name of the property
     * @param category
     *            the category of the property
     * @param allowEdit
     *            choose if this cell should be possible to edit
     */
    public PropertyDescriptor( Object key, String name, String category,
        Class<?> cls )
    {
        this.key = key;
        this.name = name;
        this.category = category;
        this.cls = cls;        
    }

    /**
     * Create a Neo property cell without editing capabilities. Use this for id
     * and relationship types "fake properties".
     * @param key
     *            the key of the property
     * @param name
     *            the name of the property
     * @param category
     *            the category of the property
     * @param allowEdit
     *            choose if this cell should be possible to edit
     */
    public PropertyDescriptor( Object key, String name, String category )
    {
        this.key = key;
        this.name = name;
        this.category = category;
    }
    
    /**
     * Creates PropertyEditor
     */

    public CellEditor createPropertyEditor(Composite parent) {
        TextCellEditor editor = new TextCellEditor(parent, SWT.READ_ONLY) {
            protected void doSetValue(Object value) {
                super.doSetValue(value.toString());
            }
        };
        
        return editor;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        if ( cls != null ) {
            return NetworkTreePluginMessages.getFormattedString(NetworkTreePluginMessages.NetworkPropertySheet_Description, (String)key, cls.getSimpleName());            
        }
        return "";
    }

    public String getDisplayName() {
        return name;
    }

    public String[] getFilterFlags() {
        return EMPTY_ARRAY;
    }

    public Object getHelpContextIds() {
        return null;
    }

    public Object getId() {
        return key;
    }

    public ILabelProvider getLabelProvider() {
        return labelProvider;
    }

    public boolean isCompatibleWith(IPropertyDescriptor arg0) {
        return false;
    }

}
