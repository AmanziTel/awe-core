package org.amanzi.awe.selection.tool;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDataModel;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.ShowOnMapEvent;
import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Envelope;

public class ElementsSelectionTool extends AbstractSelectionTool {

	/**
	 * Logger instance
	 */
	private static final Logger LOGGER = Logger
			.getLogger(ElementsSelectionTool.class);

	public ElementsSelectionTool() {
		super();
	}

	@Override
	protected void handleSelection(IDataModel model, Envelope selectionBounds,
			Point point) {
		if (model instanceof IDriveModel) {
			DriveModel driveModel = (DriveModel) model;
			try {
				for (IDataElement selectedElement : driveModel
						.getElements(selectionBounds)) {
					driveModel.setSelectedDataElementToList(selectedElement);
				}
				fireEvents(driveModel);
			} catch (AWEException e) {
				LOGGER.error("Elements Selection Tool: cannot get elements by bounds. "
						+ e);
			}
		}
	}

	private void fireEvents(IRenderableModel model) {
		EventManager eventManager = EventManager.getInstance();
		eventManager.fireEvent(new ShowOnMapEvent(model, !dragged));
		Set<IDataElement> elements = new HashSet<IDataElement>();
		elements.addAll(model.getSelectedElements());
		getPropertiesView().updateTableView(elements, (IDriveModel) model);
	}

}
