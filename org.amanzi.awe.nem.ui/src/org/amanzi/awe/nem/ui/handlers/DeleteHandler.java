package org.amanzi.awe.nem.ui.handlers;

import java.util.Iterator;

import org.amanzi.awe.nem.managers.network.NetworkElementManager;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.awe.ui.util.ActionUtil;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.network.INetworkModel;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class DeleteHandler extends AbstractHandler {

    private static class NemDeleteJob extends Job {

        private static final NetworkElementManager MANAGER = NetworkElementManager.getInstance();
        private IDataElement element;
        private INetworkModel model;

        /**
         * @param isNeedToCreateBuild
         * @param name
         */
        public NemDeleteJob(INetworkModel model, IDataElement element) {
            super("Removing data: " + model + " element:" + element);
            this.model = model;
            this.element = element;
        }

        @Override
        protected IStatus run(final IProgressMonitor monitor) {
            try {
                if (element == null) {
                    MANAGER.removeModel(model);
                } else {
                    MANAGER.removeElement(model, element);
                }

                ActionUtil.getInstance().runTask(new Runnable() {

                    @Override
                    public void run() {
                        AWEEventManager.getManager().fireDataUpdatedEvent(null);
                    }
                }, true);

            } catch (Exception e) {
                return new Status(Status.ERROR, "org.amanzi.awe.nem.ui", "Error on deleting element", e);
            }
            return Status.OK_STATUS;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();

        if (selection instanceof IStructuredSelection) {
            Iterator<Object> selectionIterator = ((IStructuredSelection)selection).iterator();
            try {
                while (selectionIterator.hasNext()) {
                    Object selectedObject = selectionIterator.next();
                    if (selectedObject instanceof ITreeItem) {
                        ITreeItem treeItem = (ITreeItem)selectedObject;
                        if (treeItem.getParent() instanceof INetworkModel || treeItem.getChild() instanceof INetworkModel) {
                            NemDeleteJob job = null;
                            if (treeItem.getParent() == null) {
                                job = new NemDeleteJob((INetworkModel)treeItem.getChild(), null);
                            } else if (treeItem.getChild() instanceof IDataElement) {
                                job = new NemDeleteJob((INetworkModel)treeItem.getParent(), (IDataElement)treeItem.getChild());
                            }
                            if (job != null) {
                                job.schedule();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new ExecutionException("can't execute action ", e);
            }
        }
        return null;
    }
}
