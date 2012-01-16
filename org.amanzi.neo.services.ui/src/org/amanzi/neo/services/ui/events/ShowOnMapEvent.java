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
import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.ui.enums.EventsType;

/**
 * <p>
 * SHOW_ON_MAP event
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ShowOnMapEvent extends AbstractEvent {
    /**
     * list of loaded models;
     */
    private List<IRenderableModel> renderableList = new ArrayList<IRenderableModel>();
        
    /**
     * selected elements 
     */
    private List<IDataElement> selectedElements = new ArrayList<IDataElement>();
    
    /**
     * zoom degree
     */
    private double zoom;
     
    /**
     *  Initialize Event type
     */    
    {
    	type = EventsType.SHOW_ON_MAP;
    }
    
    /**
     * Initialize IRenderableModel and zoom
     * 
     * @param renderableModel
     * @param zoom
     */
    public ShowOnMapEvent(IRenderableModel renderableModel, double zoom) {    	
    	renderableList.add(renderableModel);
    	selectedElements.addAll(renderableModel.getSelectedElements());
    	this.zoom = zoom;
	}

    /**
     * Initialize IRenderableModel list and zoom
     * 
     * @param renderableList
     */
    public ShowOnMapEvent(List<IRenderableModel> renderableList, double zoom) {
        this.renderableList = renderableList;
        this.zoom = zoom;        
        for (IRenderableModel renderableModel : renderableList) {
        	selectedElements.addAll(renderableModel.getSelectedElements());
        }
        
    }
    
    /**
     * Initialize IRenderableModel, IDataElement and zoom
     * 
     * @param selectedElement
     * @param zoom
     */
    public ShowOnMapEvent(IRenderableModel renderableModel, IDataElement selectedElement, double zoom) {    	
    	this(renderableModel, zoom);    	
    	selectedElements.add(selectedElement);    	
	}
	
    /**
     * return IRenderableModel list
     * 
     * @return
     */
	public List<IRenderableModel> getRenderableModelList() {
        return renderableList;
    }

	/**
	 * return zoom
	 * 
	 * @return
	 */
    public double getZoom() {
        return zoom;
    }
    
    /**
     * return selected IDataElement's
     * 
     * @return
     */
	public List<IDataElement> getSelectedElements() {
		return selectedElements;
	}	
}
