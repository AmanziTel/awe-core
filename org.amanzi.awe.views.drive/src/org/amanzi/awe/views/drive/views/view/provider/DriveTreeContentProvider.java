package org.amanzi.awe.views.drive.views.view.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.views.drive.views.DriveTreeView;
import org.amanzi.awe.views.drive.views.Messages;
import org.amanzi.neo.model.distribution.IDistributionBar;
import org.amanzi.neo.model.distribution.IDistributionModel;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.neo.services.ui.events.AnalyseEvent;
import org.amanzi.neo.services.ui.events.EventManager;
import org.apache.commons.collections.iterators.ListIteratorWrapper;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Bondoronok_P
 */
public class DriveTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {

    private boolean previousPressed;

    /**
     * The Constructor
     */
    public DriveTreeContentProvider() {
        super();
        this.previousPressed = false;
    }

    /**
     * @return true or false
     */
    public boolean isPreviousPressed() {
        return previousPressed;
    }

    /**
     * Set that previous elements TreeViewer item was pushed
     * 
     * @param previousPressed true or false
     */
    public void setPreviousPressed(boolean previousPressed) {
        this.previousPressed = previousPressed;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        Iterable<IDataElement> children = null;
        VirtualTree tree = VirtualTree.getInstance();
        if (parentElement instanceof IDriveModel) {
            children = ((IDriveModel)parentElement).getAllMeasurements();
            tree.initalizeIterator(children, (IDriveModel)parentElement);
            return isPreviousPressed() ? tree.getPreviousElements().toArray() : tree.getNextElements().toArray();
        } else if (parentElement instanceof IDataElement) {
            // String name = (String) ((IDataElement) parentElement)
            // .get(AbstractService.NAME);
            // if (name != null) {
            // if (Messages.PreviousElementsTitle.equals(name)) {
            // return tree.getPreviousElements().toArray();
            // }
            // }
            return new Object[] {parentElement};
        }
        return null;
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof IDistributionModel) {
            return ((IDistributionModel)element).getAnalyzedModel();
        } else if (element instanceof IDistributionBar) {
            return ((IDistributionBar)element).getDistribution();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object parentElement) {
        Iterable<IDataElement> children = null;
        if (parentElement instanceof IDriveModel) {
            children = ((IDriveModel)parentElement).getAllMeasurements();
            if (children.iterator().hasNext()) {
                return true;
            }
        } else if (parentElement instanceof IDataElement) {
            String name = (String)((IDataElement)parentElement).get(AbstractService.NAME);
            if (name != null && Messages.NextElementsTitle.equals(name) || Messages.PreviousElementsTitle.equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        Iterable<IDriveModel> driveModels;
        try {
            driveModels = ProjectModel.getCurrentProjectModel().findAllDriveModels();
        } catch (AWEException e) {
            MessageDialog.openError(null, Messages.ErrorTitle, Messages.CouldNotGetAllDriveModels);
            return null;
        }

        Object[] networkModelsInObjects = new Object[0];

        // TODO BP: expanded all drive models;
        for (IDriveModel model : driveModels) {
            networkModelsInObjects = ArrayUtils.add(networkModelsInObjects, model);
            EventManager.getInstance().fireEvent(new AnalyseEvent(model, DriveTreeView.DRIVE_TREE_VIEW_ID));
        }

        return networkModelsInObjects;
    }

    /**
     * Virtual Tree implementation
     * 
     * @author Bondoronok_P
     */
    private static class VirtualTree {

        private static final int LOADED_ELEMENTS_SIZE = 18;

        private static VirtualTree instance = null;
        private static ListIteratorWrapper iterator;
        private static IDriveModel model;

        /**
         * The Constructor
         */
        private VirtualTree() {
        }

        /**
         * Get VirtualTree instance NOTE: After you get virtual table instance you must initialize
         * iterator, by call initializeIterator() method
         * 
         * @return VirtualTree
         */
        public static VirtualTree getInstance() {
            if (instance == null) {
                instance = new VirtualTree();
            }
            return instance;
        }

        /**
         * Initialize VirtualTree iterator
         * 
         * @param loadedElements
         */
        public void initalizeIterator(Iterable<IDataElement> loadedElements, IDriveModel driveModel) {
            if (loadedElements != null && iterator == null && model == null) {
                iterator = new ListIteratorWrapper(loadedElements.iterator());
                model = driveModel;
            }
        }

        /**
         * Get next 20 elements. Before using this method virtual table must be initialized
         * 
         * @return next elements
         */
        public List<IDataElement> getNextElements() {

            if (iterator == null) {
                throw new IllegalArgumentException("Iterator doesn't initialized.");
            }

            List<IDataElement> nextElements = initializeElementsSet();
            IDataElement element = null;
            for (int i = 0; i < LOADED_ELEMENTS_SIZE; i++) {
                if (iterator.hasNext()) {
                    element = (IDataElement)iterator.next();
                    element.put(DriveTreeView.DRIVE_MODEL, model);
                    nextElements.add(element);
                } else {
                    break;
                }
            }
            return checkLastElements(nextElements);
        }

        /**
         * Get previous 20 elements. Before using this method virtual table must be initialized
         * 
         * @return previous elements
         */
        public List<IDataElement> getPreviousElements() {

            if (iterator == null) {
                throw new IllegalArgumentException("Iterator doesn't initialized.");
            }

            List<IDataElement> previousElements = initializeElementsSet();
            IDataElement element = null;
            for (int i = 0; i < LOADED_ELEMENTS_SIZE; i++) {
                if (iterator.hasPrevious()) {
                    element = (IDataElement)iterator.previous();
                    element.put(DriveTreeView.DRIVE_MODEL, model);
                    previousElements.add(element);
                } else {
                    break;
                }
            }
            return checkLastElements(previousElements);
        }

        /**
         * Put Previous elements IDataElement in case if iterator has previous items
         * 
         * @return Initialized List of IDataElements
         */
        private List<IDataElement> initializeElementsSet() {
            List<IDataElement> resultList = new ArrayList<IDataElement>();
            Map<String, Object> parentItemProperties;
            if (isNotRoot()) {
                parentItemProperties = new HashMap<String, Object>(1);
                parentItemProperties.put(AbstractService.NAME, Messages.PreviousElementsTitle);
                parentItemProperties.put(AbstractService.TYPE, DriveNodeTypes.M);
                resultList.add(new DataElement(parentItemProperties));
            }
            return resultList;
        }

        /**
         * Put Next elements IDataElement in case if iterator has next items
         * 
         * @param elements initialized list
         * @return list of IDataElements
         */
        private List<IDataElement> checkLastElements(List<IDataElement> elements) {
            Map<String, Object> parentItemProperties;
            if (isNotLastElement()) {
                parentItemProperties = new HashMap<String, Object>(1);
                parentItemProperties.put(AbstractService.NAME, Messages.NextElementsTitle);
                parentItemProperties.put(AbstractService.TYPE, DriveNodeTypes.M);
                elements.add(new DataElement(parentItemProperties));
            }
            return elements;
        }

        private boolean isNotRoot() {
            return iterator != null ? iterator.hasPrevious() : true;
        }

        private boolean isNotLastElement() {
            return iterator != null ? iterator.hasNext() : true;
        }
    }

}
