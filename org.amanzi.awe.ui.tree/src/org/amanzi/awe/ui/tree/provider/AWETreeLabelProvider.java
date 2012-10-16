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

package org.amanzi.awe.ui.tree.provider;

import org.amanzi.awe.ui.label.AWELabelProvider;
import org.amanzi.awe.ui.tree.label.LabelTemplateUtils;
import org.amanzi.awe.ui.tree.label.LabelTemplateUtils.LabelTemplate;
import org.amanzi.neo.dto.IDataElement;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AWETreeLabelProvider extends AWELabelProvider implements IPropertyChangeListener {

    private LabelTemplate template;

    private final String labelTemplateKey;

    private final IPreferenceStore store;

    public AWETreeLabelProvider(final IPreferenceStore store, final String labelTemplateKey) {
        template = LabelTemplateUtils.getTemplate(store.getString(labelTemplateKey));

        this.store = store;
        this.labelTemplateKey = labelTemplateKey;

        store.addPropertyChangeListener(this);
    }

    /**
     * @param element
     * @return
     */
    @Override
    protected String getNameFromDataElement(final IDataElement element) {
        return template.toString(element);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        if (event.getProperty().equals(labelTemplateKey)) {
            template = LabelTemplateUtils.getTemplate(event.getNewValue().toString());
        }
    }

    @Override
    public void dispose() {
        store.removePropertyChangeListener(this);
    }
}
