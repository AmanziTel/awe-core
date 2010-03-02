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

package org.amanzi.awe.neighbours;

import static org.amanzi.neo.core.enums.gpeh.Parameters.EVENT_PARAM_C_ID_1;
import static org.amanzi.neo.core.enums.gpeh.Parameters.EVENT_PARAM_C_ID_2;
import static org.amanzi.neo.core.enums.gpeh.Parameters.EVENT_PARAM_C_ID_3;
import static org.amanzi.neo.core.enums.gpeh.Parameters.EVENT_PARAM_C_ID_4;
import static org.amanzi.neo.core.enums.gpeh.Parameters.EVENT_PARAM_RNC_ID_1;
import static org.amanzi.neo.core.enums.gpeh.Parameters.EVENT_PARAM_RNC_ID_2;
import static org.amanzi.neo.core.enums.gpeh.Parameters.EVENT_PARAM_RNC_ID_3;
import static org.amanzi.neo.core.enums.gpeh.Parameters.EVENT_PARAM_RNC_ID_4;
import static org.amanzi.neo.core.enums.gpeh.Parameters.EVENT_PARAM_RNC_MODULE_ID;
import static org.amanzi.neo.core.enums.gpeh.Parameters.EVENT_PARAM_UE_CONTEXT;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.gpeh.Events;
import org.amanzi.neo.core.enums.gpeh.Parameters;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.amanzi.splash.utilities.SpreadsheetCreator;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;

/**
 * <p>
 * Analyse Model
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class AnalyseModel {
    private static final String ANALYSE_RABY_NAME = "analysing";
    private Node gpehNode;
    private final  List<Parameters> spreadsheetList;
    private final  Set<Events> events;

    /**
     * constructor
     * @param gpehNode - gpeh node;
     */
    protected AnalyseModel( Node gpehNode) {
        this();
        this.gpehNode = gpehNode;
        
    }
    protected void addEvents(Events...events){
       for (Events event:events){
           this.events.add(event);
           for (Parameters parameter:event.getAllParameters()){
               if (!spreadsheetList.contains(parameter)){
                   spreadsheetList.add(parameter);
               }
           }
       }
    }
    /**
     * constructor
     */
    protected AnalyseModel() {
        spreadsheetList=new LinkedList<Parameters>();
        events=new LinkedHashSet<Events>();
        spreadsheetList.add(EVENT_PARAM_UE_CONTEXT);
        spreadsheetList.add(EVENT_PARAM_RNC_MODULE_ID);
        spreadsheetList.add(EVENT_PARAM_C_ID_1);
        spreadsheetList.add(EVENT_PARAM_RNC_ID_1);
        spreadsheetList.add(EVENT_PARAM_C_ID_2);
        spreadsheetList.add(EVENT_PARAM_RNC_ID_2);
        spreadsheetList.add(EVENT_PARAM_C_ID_3);
        spreadsheetList.add(EVENT_PARAM_RNC_ID_3);
        spreadsheetList.add(EVENT_PARAM_C_ID_4);
        spreadsheetList.add(EVENT_PARAM_RNC_ID_4);
    }

    /**
     *
     * @param gpehNode
     * @param neo
     * @return
     */
    public static AnalyseModel create(Node gpehNode, NeoService neo) {
        AnalyseModel result=new AnalyseModel(gpehNode);
        result.addEvents(Events.INTERNAL_SOHO_DS_MISSING_NEIGHBOUR,Events.INTERNAL_SOHO_DS_UNMONITORED_NEIGHBOUR,Events.INTERNAL_SOFT_HANDOVER_EVALUATION,Events.INTERNAL_SOFT_HANDOVER_EXECUTION);
        return result;
    }

    /**
     * @param string
     * @return
     */
    public SpreadsheetNode createSpreadSheet(String name, NeoService service) {
        Transaction tx = service.beginTx();
        try {
            SpreadsheetCreator creator = new SpreadsheetCreator(NeoSplashUtil.configureRubyPath(ANALYSE_RABY_NAME), name);
            int column = 0;
            Cell cellToadd = new Cell(0, column, "", "EVENT name", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "EVENT id", null);
            creator.saveCell(cellToadd);
            column++;
            for (Parameters parameter : spreadsheetList) {
                cellToadd = new Cell(0, column, "", parameter.name(), null);
                column++;
                creator.saveCell(cellToadd);
            }
            int row=1;
            for (Node eventNode : getEventsNodes()) {
                column=0;
                cellToadd = new Cell(row, column, "", eventNode.getProperty(INeoConstants.PROPERTY_NAME_NAME, ""), null);
                creator.saveCell(cellToadd);
                column++;
                cellToadd = new Cell(row, column, "", eventNode.getProperty(INeoConstants.PROPERTY_EVENT_ID, 0), null);
                creator.saveCell(cellToadd);
                column++;
                for (Parameters parameter : spreadsheetList) {
                    cellToadd = new Cell(row, column, "", eventNode.getProperty(parameter.name(), ""), null);
                    creator.saveCell(cellToadd);
                    column++;
                }
                row++;
            }
            tx.success();
            System.out.println(creator.getSpreadsheet().getUnderlyingNode().getId());
            return creator.getSpreadsheet();
        } finally {
            tx.finish();
        }

    }
    /**
     *
     * @return
     */
    private Traverser getEventsNodes() {
        return gpehNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
            
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                Node currentNode=currentPos.currentNode();
                if (!NodeTypes.GPEH_EVENT.checkNode(currentNode)){
                    return false;
                }
                Integer id=(Integer)currentNode.getProperty(INeoConstants.PROPERTY_EVENT_ID,null);
                if (id==null){
                    return false;
                }
                for (Events event:events){
                    if (event.getId()==id){
                        return true;
                    }
                }
                return false;
            }
        }   , GeoNeoRelationshipTypes.CHILD,Direction.OUTGOING,GeoNeoRelationshipTypes.NEXT,Direction.OUTGOING);
    }

}
