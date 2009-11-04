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

package org.neo4j.neoclipse.preference;

import org.eclipse.jface.preference.IntegerFieldEditor;

/**
 * <p>
 * The page for neo tuning preferences.
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class NeoTuningPreferencePage extends AbstractPreferencePage {

    private static final String LABEL_NODES = "Nodes";
    private static final String LABEL_RELATIONSHIPS = "Relationships";
    private static final String LABEL_PROPERTIES = "Properties";
    private static final String LABEL_PROPERTIES_INDEX = "Properties Index";
    private static final String LABEL_PROPERTIES_KEYS = "Properties Keys";
    private static final String LABEL_PROPERTIES_STRING = "String Properties";
    private static final String LABEL_PROPERTIES_ARRAY = "Array Properties";
    private static final String PROPTERTY_NOTE = "stating that changes will only be effective after a restart";

    @Override
    protected void createFieldEditors() {
        IntegerFieldEditor nodes = new IntegerFieldEditor(NeoPreferences.NEOSTORE_NODES, LABEL_NODES, getFieldEditorParent());
        nodes.setValidRange(1, Integer.MAX_VALUE);
        addField(nodes, PROPTERTY_NOTE);
        IntegerFieldEditor relationships = new IntegerFieldEditor(NeoPreferences.NEOSTORE_RELATIONSHIPS, LABEL_RELATIONSHIPS,
                getFieldEditorParent());
        relationships.setValidRange(1, Integer.MAX_VALUE);
        addField(relationships, PROPTERTY_NOTE);
        IntegerFieldEditor properties = new IntegerFieldEditor(NeoPreferences.NEOSTORE_PROPERTIES, LABEL_PROPERTIES,
                getFieldEditorParent());
        properties.setValidRange(1, Integer.MAX_VALUE);
        addField(properties, PROPTERTY_NOTE);
        IntegerFieldEditor properties_ind = new IntegerFieldEditor(NeoPreferences.NEOSTORE_PROPERTIES_INDEX,
                LABEL_PROPERTIES_INDEX, getFieldEditorParent());
        properties_ind.setValidRange(1, Integer.MAX_VALUE);
        addField(properties_ind, PROPTERTY_NOTE);
        IntegerFieldEditor properties_keys = new IntegerFieldEditor(NeoPreferences.NEOSTORE_PROPERTIES_KEYS, LABEL_PROPERTIES_KEYS,
                getFieldEditorParent());
        properties_keys.setValidRange(1, Integer.MAX_VALUE);
        addField(properties_keys, PROPTERTY_NOTE);
        IntegerFieldEditor properties_string = new IntegerFieldEditor(NeoPreferences.NEOSTORE_PROPERTIES_STRING,
                LABEL_PROPERTIES_STRING, getFieldEditorParent());
        properties_string.setValidRange(1, Integer.MAX_VALUE);
        addField(properties_string, PROPTERTY_NOTE);
        IntegerFieldEditor properties_arrays = new IntegerFieldEditor(NeoPreferences.NEOSTORE_PROPERTIES_ARRAYS,
                LABEL_PROPERTIES_ARRAY, getFieldEditorParent());
        properties_arrays.setValidRange(1, Integer.MAX_VALUE);
        addField(properties_arrays, PROPTERTY_NOTE);
    }
}
