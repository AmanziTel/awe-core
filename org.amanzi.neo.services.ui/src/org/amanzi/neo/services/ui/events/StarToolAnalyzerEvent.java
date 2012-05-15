/**
 * 
 */
package org.amanzi.neo.services.ui.events;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.ui.enums.EventsType;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Star tool analyzer event
 * 
 * @author Bondoronok_p
 */
public class StarToolAnalyzerEvent extends AbstractEvent {
	/**
	 * Checked model for analyze
	 */
	private INetworkModel analyzedModel;
	
	/**
	 * Bounds
	 */
	private Envelope selectedBounds;

	/**
	 * Analyzed data elements
	 */
	private List<IDataElement> elements = new ArrayList<IDataElement>(0);
	
	/**
	 * Initializing event type
	 */
	{
		type = EventsType.ANALYSE;
	}
	
	public StarToolAnalyzerEvent(INetworkModel model, Envelope envelope) {		
		this.analyzedModel = model;
		this.selectedBounds = envelope;
	}
	
	public StarToolAnalyzerEvent(INetworkModel model, List<IDataElement> dataElements) {
		this.analyzedModel = model;
		this.elements = dataElements;
	}

	public INetworkModel getAnalysedModel() {
		return analyzedModel;
	}

	public Envelope getSelectedBounds() {
		return selectedBounds;
	}

	public List<IDataElement> getAnalyzedElements() {		
		return elements;
	}	
}
