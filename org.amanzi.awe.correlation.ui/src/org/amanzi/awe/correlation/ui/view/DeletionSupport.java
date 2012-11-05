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

package org.amanzi.awe.correlation.ui.view;

import java.text.MessageFormat;

import org.amanzi.awe.correlation.engine.CorrelationEngine;
import org.amanzi.awe.correlation.model.ICorrelationModel;
import org.amanzi.awe.correlation.ui.internal.CorrelationMessages;
import org.amanzi.awe.ui.util.ActionUtil;
import org.amanzi.neo.models.exceptions.ModelException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class DeletionSupport extends EditingSupport {
    /**
     * @param viewer
     */
    public DeletionSupport(final TableViewer viewer) {
        super(viewer);
    }

    @Override
    protected boolean canEdit(final Object element) {
        return true;
    }

    @Override
    protected CellEditor getCellEditor(final Object element) {
        final ICorrelationModel model = (ICorrelationModel)element;
        boolean result = MessageDialog.openQuestion(getViewer().getControl().getShell(),
                CorrelationMessages.REMOVE_CORRELATION_DIALOG_TITLE,
                MessageFormat.format(CorrelationMessages.REMOVE_CORRELATION_DIALOG_MESSAGE, model.getName()));
        if (result == true) {

            Job job = new Job("remove model" + model.getName()) {

                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    try {
                        CorrelationEngine.removeModel(model);
                        ActionUtil.getInstance().runTask(new Runnable() {

                            @Override
                            public void run() {
                                ((TableViewer)getViewer()).remove(element);
                                getViewer().cancelEditing();
                            }
                        }, false);
                    } catch (ModelException e) {
                        return new Status(Status.ERROR, "org.amanzi.awe.correlation.view",
                                "erroc occured when trying to delete model");
                    }
                    return Status.OK_STATUS;
                }
            };
            job.schedule();
        }
        return null;
    }

    @Override
    protected Object getValue(final Object element) {
        return null;
    }

    @Override
    protected void setValue(final Object element, final Object value) {
    }

}
