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

package org.amanzi.awe.ui.tree.wrapper.impl;

import org.amanzi.awe.ui.tree.item.ITreeItem;
import org.amanzi.awe.ui.tree.item.impl.TreeItem;
import org.amanzi.awe.ui.tree.label.LabelTemplateUtils;
import org.amanzi.awe.ui.tree.label.LabelTemplateUtils.LabelTemplate;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapper;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractTreeWrapper extends TreeItem implements ITreeWrapper {

    private LabelTemplate template;

    protected AbstractTreeWrapper() {
        this(null, null, null);
    }

    protected AbstractTreeWrapper(final ITreeWrapper wrapper) {
        this(null, null, wrapper);
    }

    /**
     * @param parent
     * @param child
     * @param wrapper
     */
    protected AbstractTreeWrapper(final IModel parent, final Object child, final ITreeWrapper wrapper) {
        super(parent, child, wrapper);

        if (getPreferenceKey() != null && getPreferenceStore() != null) {
            template = LabelTemplateUtils.getTemplate(getPreferenceStore().getString(getPreferenceKey()));
        }
    }

    @Override
    public String getTitle(final ITreeItem item) {
        final IDataElement element = item.castChild(IDataElement.class);

        if (element != null && template != null) {
            return template.toString(element);
        }

        return null;
    }

    protected abstract String getPreferenceKey();

    protected abstract IPreferenceStore getPreferenceStore();
}
