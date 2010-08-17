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

package org.amanzi.awe.afp.loaders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;


/**
 * Writes the data from the neo4j database to external file
 * 
 * @author Rahul
 *
 */

/**
 * TODO code not implemented yet for import of exceptions file.
 * 		So its export is also not implemented
 */

public class AfpExporter {
	
	private GraphDatabaseService service;
	private Node afpRoot;
//	private String siteName;
	
	//protected String tmpAfpFolder = System.getProperty("user.home") + File.separator + "AfpTemp" + File.separator;
	public final String tmpAfpFolder = File.separator + "AfpTemp" + File.separator;
	
	/** The Control File*/
	public final String controlFileName = tmpAfpFolder + "InputControlFile.awe";
	public final String inputCellFileName = tmpAfpFolder + "InputCellFile.awe";
	public final String inputNeighboursFileName = tmpAfpFolder + "InputNeighboursFile.awe";
	public final String inputInterferenceFileName = tmpAfpFolder + "InputInterferenceFile.awe";
	public final String inputCliquesFileName = tmpAfpFolder + "InputCliquesFile.awe";
	public final String inputForbiddenFileName = tmpAfpFolder + "InputForbiddenFile.awe";
	public final String inputExceptionFileName = tmpAfpFolder + "InputExceptionFile.awe";
	public final String paramFileName = tmpAfpFolder + "param.awe";
	public final String logFileName = tmpAfpFolder + "logfile.awe";
	public final String outputFileName = tmpAfpFolder + "outputFile.awe";
	
	public AfpExporter(Node afpRoot){
		this.afpRoot = afpRoot;
	}
	
	/**
	 * creates the carrier file
	 * 
	 */
	public void createCarrierFile(){
		
		File carrierFile = getFile(this.inputCellFileName);

		try{
			 carrierFile.createNewFile();
			 BufferedWriter writer  = new BufferedWriter(new FileWriter(carrierFile));
			 
			 for (Node sector : afpRoot.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.CHILD, Direction.OUTGOING)){
				 if (!sector.getProperty("type").equals("sector"))
				 	 continue;
				 String sectorValues[] = parseSectorName(sector);			
				 writer.write(sectorValues[0]);
				 writer.write(" " + sectorValues[1]);
				 writer.write(" " + sector.getProperty("nonrelevant"));
				 writer.write(" " + sector.getProperty("numberoffreqenciesrequired"));
				 writer.write(" " + sector.getProperty("numberoffrequenciesgiven"));
				 Object obj = sector.getProperty("frq");
				 
				 if (obj != null){
					 Integer temp[]  = new Integer[2];
					 if (obj.getClass() == temp.getClass()){
						 Integer givenFrequencies[] = (Integer[]) obj;
						 for (Integer frequency : givenFrequencies){
							 writer.write(" " + frequency);
						 }
					 }
					 else{
						 int givenFrequencies[] = (int[])obj;
						 for (int frequency : givenFrequencies){
							 writer.write(" " + frequency);
						 }
					 }
					 
				 }
				 writer.newLine();
			 }
			 writer.close();
		}catch (Exception e){
			AweConsolePlugin.exception(e);
		}
	}
	
	/**
	 * Creates the Control file to be given as input to the C++ engine
	 */
	public void createControlFile(){
		File controlFile = getFile(this.controlFileName);
		
		try {
			controlFile.createNewFile();
			BufferedWriter writer  = new BufferedWriter(new FileWriter(controlFile));
			
			
			writer.write("SiteSpacing " + afpRoot.getProperty("SiteSpacing"));
			writer.newLine();
			
			writer.write("CellSpacing " + afpRoot.getProperty("CellSpacing"));
			writer.newLine();
			
			writer.write("RegNbrSpacing " + afpRoot.getProperty("RegNbrSpacing"));
			writer.newLine();

			writer.write("MinNbrSpacing " + afpRoot.getProperty("MinNbrSpacing"));
			writer.newLine();

			writer.write("SecondNbrSpacing " + afpRoot.getProperty("SecondNbrSpacing"));
			writer.newLine();

			writer.write("RecalculateAll " + afpRoot.getProperty("RecalculateAll"));
			writer.newLine();

			writer.write("UseTraffic " + afpRoot.getProperty("UseTraffic"));
			writer.newLine();

			writer.write("UseSONbrs " + afpRoot.getProperty("UseSONbrs"));
			writer.newLine();

			writer.write("Quality " + afpRoot.getProperty("Quality"));
			writer.newLine();

			writer.write("DecomposeInCliques " + afpRoot.getProperty("DecomposeInCliques"));
			writer.newLine();

			writer.write("ExistCliques " + afpRoot.getProperty("ExistCliques"));
			writer.newLine();

			writer.write("GMaxRTperCell " + afpRoot.getProperty("GMaxRTperCell"));
			writer.newLine();

			writer.write("GMaxRTperSite " + afpRoot.getProperty("GMaxRTperSite"));
			writer.newLine();

			writer.write("HoppingType " + afpRoot.getProperty("HoppingType"));
			writer.newLine();
			
			writer.write("UseGrouping " + afpRoot.getProperty("UseGrouping"));
			writer.newLine();

			writer.write("NrOfGroups " + afpRoot.getProperty("NrOfGroups"));
			writer.newLine();
			
			writer.write("LogFile " + "\"" + this.logFileName + "\"");
			writer.newLine();

			writer.write("CellCardinality " + afpRoot.getProperty("CellCardinality"));
			writer.newLine();
			
			writer.write("CellFile " + "\"" + this.inputCellFileName + "\"");
			writer.newLine();
			
			writer.write("NeighboursFile " + "\"" + this.inputNeighboursFileName + "\"");
			writer.newLine();
			
			writer.write("InterferenceFile " + "\"" + this.inputInterferenceFileName + "\"");
			writer.newLine();

			writer.write("OutputFile " + "\"" + this.outputFileName + "\"");
			writer.newLine();
			
			writer.write("CliquesFile " + "\"" + this.inputCliquesFileName + "\"");
			writer.newLine();

			writer.write("ForbiddenFile " + "\"" + this.inputForbiddenFileName + "\"");
			writer.newLine();
			
			writer.write("ExceptionFile " + "\"" + this.inputExceptionFileName + "\"");
			writer.newLine();
			
			writer.write("Carriers " + afpRoot.getProperty("Carriers"));
			writer.newLine();

			writer.close();
		}catch (Exception e){
			AweConsolePlugin.exception(e);
		}
	}
	
	/**
	 * Creates the neighbors file for input to the C++ engine
	 */
	public void createNeighboursFile(){
		File neighboursFile = getFile(this.inputNeighboursFileName);
		Node startSector = null;
		
		try {
			neighboursFile.createNewFile();
			BufferedWriter writer  = new BufferedWriter(new FileWriter(neighboursFile));
			
			for (Node neighbour : afpRoot.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.NEIGHBOUR_DATA, Direction.OUTGOING)){
				//TODO: put condition here to get the desired neighbour list in case of multiple neighbour list
				startSector = neighbour.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.OUTGOING).getEndNode();
				break;				
			}
			
			for (Node proxySector : startSector.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL, NetworkRelationshipTypes.NEXT, Direction.OUTGOING)){
				if (!proxySector.getProperty("type").equals("sector_sector_relations"))
					 continue;
				Node sector = proxySector.getSingleRelationship(NetworkRelationshipTypes.NEIGHBOURS, Direction.INCOMING).getOtherNode(proxySector);
				String sectorValues[] = parseSectorName(sector);			
				writer.write("CELL " + sectorValues[0] + " " + sectorValues[1]);
				writer.newLine();
				for (Node neighbourProxySector: proxySector.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.NEIGHBOUR, Direction.OUTGOING)){
					Node neighbourSector = neighbourProxySector.getSingleRelationship(NetworkRelationshipTypes.NEIGHBOURS, Direction.INCOMING).getOtherNode(neighbourProxySector);
					sectorValues = parseSectorName(neighbourSector);
					writer.write("NBR " + sectorValues[0] + " " + sectorValues[1]);
					writer.newLine();
				}
			}
			
			writer.close();
		}catch (Exception e){
			AweConsolePlugin.exception(e);
		}
	}
	
	/**
	 * Gets the site name and sector no of the sector
	 * @param sector the sector node
	 * @return string array containg site name and sector no
	 */
	public String[] parseSectorName(Node sector){
		Node site = sector.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(sector);
		String siteName = site.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
		String sectorValues[] = new String[2];
		sectorValues[0] = siteName;
		sectorValues[1] = sector.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString().substring(siteName.length());
		return sectorValues;
	}
	
	/**
	 * Creates the interference file for input to the C++ engine
	 */
	public void createInterferenceFile(){
		File interferenceFile = getFile(this.inputInterferenceFileName);
		Node startSector = null;

		try {
			interferenceFile.createNewFile();
			BufferedWriter writer  = new BufferedWriter(new FileWriter(interferenceFile));
			
			for (Node interferer : afpRoot.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.INTERFERENCE_DATA, Direction.OUTGOING)){
				//TODO: put condition here to get the desired interference list in case of multiple interference list
				startSector = interferer.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.OUTGOING).getEndNode();
				break;				
			}
			
			for (Node proxySector : startSector.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL, NetworkRelationshipTypes.NEXT, Direction.OUTGOING)){
				if (!proxySector.getProperty("type").equals("sector_sector_relations"))
					 continue;
				Node sector = proxySector.getSingleRelationship(NetworkRelationshipTypes.INTERFERENCE, Direction.INCOMING).getOtherNode(proxySector);		
				writer.write("SUBCELL " + proxySector.getProperty("nonrelevant1") + " " +
							 proxySector.getProperty("nonrelevant2") + " " +
							 proxySector.getProperty("total-cell-area") + " " +
							 proxySector.getProperty("total-cell-traffic") + " " +
							 proxySector.getProperty("numberofinterferers") + " " +
							 sector.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString());
				writer.newLine();
				for (Relationship relation : proxySector.getRelationships(NetworkRelationshipTypes.INTERFERS, Direction.OUTGOING)){
					Node neighbourProxySector = relation.getEndNode();
					Node neighbourSector = neighbourProxySector.getSingleRelationship(NetworkRelationshipTypes.INTERFERENCE, Direction.INCOMING).getOtherNode(neighbourProxySector);
					DecimalFormat df = new DecimalFormat("0.0000000000");
					writer.write("INT " + proxySector.getProperty("nonrelevant1") + "\t" +
							relation.getProperty("nonrelevant2").toString() + "\t" +
							df.format(relation.getProperty("co-channel-interf-area")).toString() + " " +
							df.format(relation.getProperty("co-channel-interf-traffic")).toString() + " " +
							df.format(relation.getProperty("adj-channel-interf-area")).toString() + " " +
							df.format(relation.getProperty("adj-channel-interf-traffic")).toString() + " " +
							neighbourSector.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString());
					writer.newLine();
				}
			}
			
			writer.close();
		}catch (Exception e){
			AweConsolePlugin.exception(e);
		}
	}
	
	/**
	 * Creates the exception file for input to the C++ engine
	 */
	public void createExceptionFile(){
		File exceptionFile = getFile(this.inputExceptionFileName);
		Node startSector = null;
		try {
			exceptionFile.createNewFile();
			BufferedWriter writer  = new BufferedWriter(new FileWriter(exceptionFile));
			for (Node exception : afpRoot.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.EXCEPTION_DATA, Direction.OUTGOING)){
				//TODO: put condition here to get the desired exception list in case of multiple interference list
				startSector = exception.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.OUTGOING).getEndNode();
				break;				
			}
			
			for (Node proxySector : startSector.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL, NetworkRelationshipTypes.NEXT, Direction.OUTGOING)){
				if (!proxySector.getProperty("type").equals("sector_sector_relations"))
					 continue;
				Node sector = proxySector.getSingleRelationship(NetworkRelationshipTypes.EXCEPTIONS, Direction.INCOMING).getOtherNode(proxySector);		
				String sectorValues[] = parseSectorName(sector);
				for (Relationship relation : proxySector.getRelationships(NetworkRelationshipTypes.EXCEPTION, Direction.OUTGOING)){
					Node exceptionProxySector = relation.getEndNode();
					Node exceptionSector = exceptionProxySector.getSingleRelationship(NetworkRelationshipTypes.EXCEPTIONS, Direction.INCOMING).getOtherNode(exceptionProxySector);
					String exceptionSectorValues[] = parseSectorName(exceptionSector);
					writer.write(sectorValues[0] + " " + sectorValues[1] + " " + exceptionSectorValues[0] + " " + exceptionSectorValues[1] + " " +
								relation.getProperty("new_spacing"));
					writer.newLine();
				}
			}
			writer.close();
		}catch (Exception e){
			AweConsolePlugin.exception(e);
		}
	}
	
	/**
	 * Creates the forbidden file for input to the C++ engine
	 */
	public void createForbiddenFile(){
		File forbiddenFile = getFile(this.inputForbiddenFileName);
		try {
			forbiddenFile.createNewFile();
			BufferedWriter writer  = new BufferedWriter(new FileWriter(forbiddenFile));
			
			 for (Node sector : afpRoot.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.CHILD, Direction.OUTGOING)){
				 if (!sector.getProperty("type").equals("sector"))
				 	 continue;
				 String sectorValues[] = parseSectorName(sector);			
				 
				 Object obj = sector.getProperty("numberofforbidden", null);
				 if (!(obj == null)){
					 String numForbidden = obj.toString();
					 writer.write(sectorValues[0]);
					 writer.write(" " + sectorValues[1]);
					 writer.write(" " + numForbidden);
					 obj = sector.getProperty("forb_fr_list");
					 
					 if (obj != null){
						 Integer temp[]  = new Integer[2];
						 if (obj.getClass() == temp.getClass()){
							 Integer forbiddenFrequencies[] = (Integer[]) obj;
							 for (Integer frequency : forbiddenFrequencies){
								 writer.write(" " + frequency);
							 }
						 }
						 else{
							 int forbiddenFrequencies[] = (int[])obj;
							 for (int frequency : forbiddenFrequencies){
								 writer.write(" " + frequency);
							 }
						 }
						 
					 }
					 writer.newLine();
				 }
			 }

			
			writer.close();
		}catch (Exception e){
			AweConsolePlugin.exception(e);
		}
	}
	
	/**
	 * Creates the cliques file for input to the C++ engine
	 */
	public void createCliquesFile(){
		File cliquesFile = getFile(this.inputCliquesFileName);

		try {
			cliquesFile.createNewFile();
			BufferedWriter writer  = new BufferedWriter(new FileWriter(cliquesFile));
			
			/**
			 *Write code here to write the file
			 */
			
			writer.close();
		}catch (Exception e){
			AweConsolePlugin.exception(e);
		}
	}
	
	
	
	
	public void createParamFile(){
		File file = getFile(this.paramFileName);
		try {
			file.createNewFile();
			BufferedWriter writer  = new BufferedWriter(new FileWriter(file));
			writer.write(controlFileName + "\n\r");
			writer.close();
		}catch (Exception e){
			AweConsolePlugin.exception(e);
		}
	}
	/**
	 * Gets the file instance for the specified file name
	 * @param name the name for that file
	 * @return returns the file instance
	 */
	
	private File getFile(String name){
		
		File file = new File(this.tmpAfpFolder);
		if (!file.exists())
			file.mkdir();
		
		file = new File(name);
		return file; 
	}


}
