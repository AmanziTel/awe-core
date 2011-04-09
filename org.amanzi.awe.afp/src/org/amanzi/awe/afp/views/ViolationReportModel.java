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

package org.amanzi.awe.afp.views;

import org.amanzi.awe.ui.custom_table.ChangeModelType;
import org.amanzi.awe.ui.custom_table.IModelChangeEvent;
import org.amanzi.awe.ui.custom_table.TableModel;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.utils.CountedIterable;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * <p>
 * ViolationReportModel
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class ViolationReportModel extends TableModel {
    private CountedIterable<ViolationWrapper> iterable = null;
    private int colCount = 0;
    private ILazyContentProvider provider;
    private FrequencyPlanModel model;

    public ViolationReportModel() {
        provider = new ContentProvider();
    }
    @Override
    public ILazyContentProvider getContentProvider() {
        return provider;
    }

    @Override
    public int getRowsCount() {
        return iterable == null ? null : iterable.getElementCount(true);
    }

    public void setFrequemcyPlanModel(FrequencyPlanModel model) {
        this.model = model;
        fireEvent(new IModelChangeEvent() {

            @Override
            public ChangeModelType getType() {
                return ChangeModelType.CONTENT;
            }

            @Override
            public Object getData() {
                return iterable;
            }
        });

    }
    @Override
    public boolean canSort() {
        return false;
    }

    @Override
    public void sortData(int columnId, int direction) {
    }

    @Override
    public void updateColumn(Table table, TableColumn column, int columnId) {
    }

    @Override
    public int getColumnsCount() {
        return colCount;
    }

    public static class ContentProvider implements ILazyContentProvider {
        CountedIterable<ViolationWrapper> iterable;
        private TableViewer viewer;

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            this.viewer = (TableViewer)viewer;
            iterable = (CountedIterable<ViolationWrapper>)newInput;
        }

        @Override
        public void updateElement(int index) {
            if (iterable == null) {
                return;
            }
            ViolationWrapper wrapper = iterable.getElement(index / 2);
            wrapper.setActualId(index % 2);
            viewer.replace(wrapper, index);
        }

    }
    public static class ViolationWrapper{

        private int id;

        /**
         * @param i
         */
        public void setActualId(int id) {
            Assert.isTrue(id < 2 && id >= 0);
            this.id = id;
        }
        
    }
}
