package org.amanzi.awe.star.tool;

import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.amanzi.awe.selection.tool.AbstractSelectionTool;
import org.amanzi.awe.star.tool.analyzer.StarToolAnalyzer;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDataModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.ui.enums.EventsType;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.IEventsListener;
import org.amanzi.neo.services.ui.events.StarToolAnalyzerEvent;
import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Star tool implementation
 * 
 * @author Bondoronok_p
 */
public class StarTool extends AbstractSelectionTool {

    /**
     * Logger instance
     */
    private static final Logger LOGGER = Logger.getLogger(StarTool.class);

    /**
     * Creates an new instance of the SOtarTool which supports mouse actions and mouse motion. These
     * are used to support panning as well as the star analysis.
     */
    @SuppressWarnings("unchecked")
    public StarTool() {
        super();
        EventManager.getInstance().addListener(EventsType.ANALYSE, new StarToolAnalyzerListener());
    }

    @Override
    protected void handleSelection(IDataModel model, Envelope selectionBounds, Point point) {
        if (model instanceof INetworkModel) {

            try {
                INetworkModel networkModel = ((INetworkModel)model).getStarToolSelectedModel();
                StarToolAnalyzer toolAnalyzer = new StarToolAnalyzer(networkModel, iGeoResource, getContext(), dragged,
                        selectionBounds);
                toolAnalyzer.analyze(point);
            } catch (AWEException e) {
                LOGGER.info("Star Tool: No model for the analysis of.");
            }
        }
    }

    /**
     * Star Tool Analyzer Listener
     * 
     * @author Bondoronok_p
     */
    private class StarToolAnalyzerListener implements IEventsListener<StarToolAnalyzerEvent> {
        @Override
        public void handleEvent(StarToolAnalyzerEvent data) {
            List<IDataElement> dataElements = data.getAnalyzedElements();
            if (!dataElements.isEmpty()) {
                Set<IDataElement> analyzedElements = new HashSet<IDataElement>();
                analyzedElements.addAll(dataElements);
            }
        }

        @Override
        public Object getSource() {
            return null;
        }
    }
}
