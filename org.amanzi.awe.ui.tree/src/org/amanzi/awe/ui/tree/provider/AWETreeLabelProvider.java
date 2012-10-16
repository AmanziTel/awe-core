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

import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.ui.label.AWELabelProvider;
import org.amanzi.awe.ui.tree.AWETreePlugin;
import org.amanzi.awe.ui.tree.label.LabelTemplateUtils;
import org.amanzi.awe.ui.tree.label.LabelTemplateUtils.LabelTemplate;
import org.amanzi.neo.dto.IDataElement;
import org.apache.commons.lang3.ArrayUtils;
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

    private final Map<String, LabelTemplate> templateMap;

    private final String[] templateKeys;

    public AWETreeLabelProvider(final String... templateKeys) {
        final IPreferenceStore store = AWETreePlugin.getDefault().getPreferenceStore();

        templateMap = new HashMap<String, LabelTemplateUtils.LabelTemplate>(templateKeys.length);

        for (final String key : templateKeys) {
            final LabelTemplate template = LabelTemplateUtils.getTemplate(store.getString(key));

            templateMap.put(key, template);
        }

        this.templateKeys = templateKeys;

        store.addPropertyChangeListener(this);
    }

    /**
     * @param element
     * @return
     */
    @Override
    protected String getNameFromDataElement(final IDataElement element) {
        int range = 0;
        LabelTemplate actualTemplate = null;

        for (final LabelTemplate template : templateMap.values()) {
            final int newRange = template.handleRange(element);

            if (newRange > range) {
                range = newRange;
                actualTemplate = template;
            }
        }

        if (range > 0) {
            return actualTemplate.toString(element);
        } else {
            return super.getNameFromDataElement(element);
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        if (ArrayUtils.contains(templateKeys, event.getProperty())) {
            templateMap.put(event.getProperty(), LabelTemplateUtils.getTemplate(event.getNewValue().toString()));
        }
    }

    @Override
    public void dispose() {
        AWETreePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
    }
}
