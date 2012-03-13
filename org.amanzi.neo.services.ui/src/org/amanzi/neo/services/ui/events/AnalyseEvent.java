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

package org.amanzi.neo.services.ui.events;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.ui.enums.EventsType;

/**
 * <p>
 * ANALYSE event
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class AnalyseEvent extends AbstractEvent {
	/**
	 * selected model to analyse
	 */
	private IModel selectedModel;

	/**
	 * selected elements to analyse
	 */
	private List<IDataElement> selectedElements = new ArrayList<IDataElement>();
	/**
	 * id of required analyser
	 */
	private String analyserId;

	/**
	 * initialize event class with necessary parameters
	 * 
	 * @param selectedModel
	 *            model which should be send to view
	 * @param analyser_id
	 *            view id
	 */
	public AnalyseEvent(IModel selectedModel, String analyser_id) {
		type = EventsType.ANALYSE;
		this.selectedModel = selectedModel;
		this.analyserId = analyser_id;
		targetPluginId = analyserId;
	}

	/**
	 * Initialize event
	 * 	 
	 * @param selectedModel model 
	 * @param selectedElements elements
	 * @param analyser_id analyser id 
	 */
	public AnalyseEvent(IModel selectedModel, List<IDataElement> selectedElements, String analyser_id) {
       this(selectedModel, analyser_id);
       this.selectedElements.clear();
       this.selectedElements.addAll(selectedElements);       
    }

	public IModel getSelectedModel() {
		return selectedModel;
	}

	public String getAnalyserId() {
		return analyserId;
	}

	public List<IDataElement> getSelectedElements() {
		return selectedElements;
	}	

}
