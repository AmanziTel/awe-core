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

package org.amanzi.awe.nem.ui.widgets;

import java.util.List;

import org.amanzi.awe.nem.export.SynonymsWrapper;
import org.amanzi.awe.nem.ui.properties.table.SynonymsTable;
import org.amanzi.awe.nem.ui.widgets.SynonymsEditorTable.ISynonymsTableListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class SynonymsEditorTable extends AbstractAWEWidget<Composite, ISynonymsTableListener> {

    public interface ISynonymsTableListener extends AbstractAWEWidget.IAWEWidgetListener {

    }

    private final List<SynonymsWrapper> synonyms;
    private SynonymsTable table;

    /**
     * @param parent
     * @param style
     * @param listener
     */
    public SynonymsEditorTable(final Composite parent, final ISynonymsTableListener listener, final List<SynonymsWrapper> synonyms) {
        super(parent, SWT.FILL, listener);
        this.synonyms = synonyms;
    }

    @Override
    protected Composite createWidget(final Composite parent, final int style) {
        this.table = new SynonymsTable(parent);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.heightHint = parent.getSize().y;
        table.getTable().setLayoutData(gridData);
        table.init(synonyms);
        return parent;
    }
}
