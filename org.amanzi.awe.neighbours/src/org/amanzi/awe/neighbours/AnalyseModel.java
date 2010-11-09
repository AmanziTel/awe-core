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

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.amanzi.neo.core.enums.gpeh.Events;
import org.amanzi.neo.core.enums.gpeh.Parameters;
import org.amanzi.neo.core.utils.GpehReportUtil;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.nodes.SpreadsheetNode;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.amanzi.splash.utilities.SpreadsheetCreator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * <p>
 * Analyse Model
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class AnalyseModel {
    private static final String ANALYSE_RUBY_NAME = "analysing";
    private Node gpehNode;
    private final  List<Parameters> spreadsheetList;
    private final  List<String> additionalColumn;
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
        additionalColumn=new LinkedList<String>();
        events=new LinkedHashSet<Events>();
    }

    /**
     *
     * @param gpehNode
     * @param neo
     * @return
     */
    public static AnalyseModel create(Node gpehNode, GraphDatabaseService neo) {
        AnalyseModel result=new AnalyseModel(gpehNode);
//        result.addEvents(Events.INTERNAL_SOHO_DS_MISSING_NEIGHBOUR,Events.INTERNAL_SOHO_DS_UNMONITORED_NEIGHBOUR,Events.INTERNAL_SOFT_HANDOVER_EVALUATION,Events.INTERNAL_SOFT_HANDOVER_EXECUTION);
        result.addEvents(Events.RRC_MEASUREMENT_REPORT);
        return result;
    }

    /**
     * @param monitor 
     * @param string
     * @return
     */
    public SpreadsheetNode createSpreadSheet(String name, GraphDatabaseService service, IProgressMonitor monitor) {
       
        Transaction tx = service.beginTx();
        try {
            try {
                CSVWriter out=new CSVWriter(new FileWriter("c://event8.csv"));
                List<String> outList=new LinkedList<String>();
                int row=0;
                for (Node eventNode : getEventsNodes()) {
                    outList.clear();
                    outList.add((String)eventNode.getProperty(INeoConstants.PROPERTY_NAME_NAME, ""));
                    outList.add(eventNode.getProperty(INeoConstants.PROPERTY_EVENT_ID, "").toString());
                    outList.add(eventNode.getProperty(GpehReportUtil.MR_TYPE, "").toString());
                    for (Parameters parameter : spreadsheetList) {
                        outList.add(eventNode.getProperty(parameter.name(), "").toString());
                    }
                    for (String key:eventNode.getPropertyKeys()){
                        if (additionalColumn.contains(key)){
                            continue;
                        }
                        if (GpehReportUtil.isReportProperties(key)){
                            additionalColumn.add(key);
                        }
                    }
                    for (String key:additionalColumn){
                        outList.add(GpehReportUtil.getPropertyRangeName(key, eventNode.getProperty(key, null)));
                    }
                    row++;
                    out.writeNext(outList.toArray(new String[0]));
                    monitor.setTaskName(String.format("Rows created: %s", row));
            }
                outList.clear();
                outList.add("name");
                outList.add("id");
                outList.add("report type");
                for (Parameters parameter : spreadsheetList) {
                    outList.add(parameter.name());
                }
                for (String key:additionalColumn){
                    outList.add(key);
                }
                out.writeNext(outList.toArray(new String[0]));
                out.close();
            } catch (IOException e) {
                // TODO Handle IOException
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
            SpreadsheetCreator creator = new SpreadsheetCreator(NeoSplashUtil.configureRubyPath(ANALYSE_RUBY_NAME), name);
            tx.success();
            if (true) return creator.getSpreadsheet();
//            SpreadsheetCreator creator = new SpreadsheetCreator(NeoSplashUtil.configureRubyPath(ANALYSE_RUBY_NAME), name);
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
            int lastColumn =column;
            int row=1;
            int maxWork=0;
            int saveCount=0;
            for (Node eventNode : getEventsNodes()) {
                saveCount++;
                if (saveCount>9999){
                    saveCount=0;
                    System.out.println(maxWork);
                }
                maxWork++;
            }
            monitor= SubMonitor.convert(monitor, maxWork);
            long time = System.currentTimeMillis();
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
                for (String key:eventNode.getPropertyKeys()){
                    if (additionalColumn.contains(key)){
                        continue;
                    }
                    if (GpehReportUtil.isReportProperties(key)){
                        additionalColumn.add(key);
                    }
                }
                for (String key:additionalColumn){
                    cellToadd = new Cell(row, column, "", GpehReportUtil.getPropertyRangeName(key, eventNode.getProperty(key, null)), null);
                    creator.saveCell(cellToadd);
                    column++;                    
                }
                monitor.setTaskName(String.format("Rows created: %s", row));
                saveCount++;
                if (saveCount>1000){
                    time=System.currentTimeMillis()-time;
                    tx.success();
                    tx.finish();
                    saveCount=0;
                    System.out.println("time of storing 1000 rows: "+time);
                    time = System.currentTimeMillis();
                    tx=service.beginTx();
                }
                monitor.worked(1);
                row++;
            }
            monitor.setTaskName("modify header");
            for (String key:additionalColumn) {
                cellToadd = new Cell(0, ++lastColumn, "", key, null);
                creator.saveCell(cellToadd);
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
