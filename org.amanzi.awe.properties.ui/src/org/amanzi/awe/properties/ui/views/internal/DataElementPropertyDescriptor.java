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

package org.amanzi.awe.properties.ui.views.internal;

import java.util.HashSet;
import java.util.Set;

import org.amanzi.awe.properties.ui.AWEPropertiesPlugin;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DataElementPropertyDescriptor extends PropertyDescriptor {

    private enum Category {
        PROPERTY("Properties"), HEADER("General info");

        private String title;

        private static final Set<String> UNEDITABLE_PROPERTIES = new HashSet<String>();

        static {
            UNEDITABLE_PROPERTIES.add(generalNodeProperties.getNodeTypeProperty());
            UNEDITABLE_PROPERTIES.add(generalNodeProperties.getLastChildID());
            UNEDITABLE_PROPERTIES.add(generalNodeProperties.getParentIDProperty());
            UNEDITABLE_PROPERTIES.add(generalNodeProperties.getSizeProperty());
            UNEDITABLE_PROPERTIES.add(ID_PROPERTY);
        }

        protected static Category computeCategory(final String propertyName) {
            if (UNEDITABLE_PROPERTIES.contains(propertyName)) {
                return Category.HEADER;
            } else {
                return Category.PROPERTY;
            }
        }

        private Category(final String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    protected final static String ID_PROPERTY = "id";

    private static final IGeneralNodeProperties generalNodeProperties;

    static {
        generalNodeProperties = AWEPropertiesPlugin.getDefault().getGeneralNodeProperties();
    }

    /**
     * @param id
     * @param displayName
     */
    public DataElementPropertyDescriptor(final String propertyName) {
        super(propertyName, propertyName);
        setCategory(calculateCategory(propertyName).getTitle());
    }

    private Category calculateCategory(final String propertyName) {
        return Category.computeCategory(propertyName);
    }

    @Override
    public CellEditor createPropertyEditor(final Composite parent) {
        if (getCategory().equals(Category.HEADER.getTitle())) {
            return null;
        } else {
            return new PropertyCellEditor(parent, SWT.BORDER);
        }
    }
}
