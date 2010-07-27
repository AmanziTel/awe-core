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
import java.util.ResourceBundle;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;


/**
 * Writes the data from the neo4j database to external file
 * 
 * @author Rahul
 *
 */

/**
 * TODO file import for neighbors file giving errors, 
 * 		and code not implemented yet for import of interference and exceptions file.
 * 		So their export is also not implemented
 */

public class AfpExporter {
	
	private GraphDatabaseService service;
	private Node afpRoot;
	private String siteName;
	
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
		boolean start = false;
		String sectorName = null;
		String sectorNo = null;
		File carrierFile = getFile(this.inputCellFileName);

		service = NeoServiceProvider.getProvider().getService();
		NeoServiceProvider.initProvider(service);
		try{
			 carrierFile.createNewFile();
			 start = true;
			 BufferedWriter writer  = new BufferedWriter(new FileWriter(carrierFile));
			  
			 for (Node site : afpRoot.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.CHILD, Direction.OUTGOING)){
				 if (!site.getProperty("type").equals("site"))
					 continue;
				 siteName = site.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
				 if (!start)
					 writer.newLine();
				 start = false;
				 writer.write(siteName);
				 Node sector = site.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.OUTGOING).getOtherNode(site);
				 
				 sectorName = sector.getProperty("name").toString();
				 sectorNo = sectorName.substring(siteName.length());
				 writer.write(" " + sectorNo);
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
//			for (String key :afpRoot.getPropertyKeys()){
//				String keyTemp = null;
//				if (key.equals("type") || key.equals("name") || key.equals("primary_type")){
//						continue;
//				}
//				
//				/**
//				 * Write the destinations for the corresponding files as <key property>
//				 */
//				if (key.endsWith("File")){
//					
//					/** For, the interefernce and neighbours file, 
//					 * Use the same file, which is given as input to write the database
//					 * till the code for writing these files is not implemented
//					 */
//					if (key.equals("NeighboursFile") || key.endsWith("InterferenceFile")) {
//						writer.write(key + " " + afpRoot.getProperty(key));
//						writer.newLine();
//					}
//					continue;
//				}
//					
//				writer.write(key + " " + afpRoot.getProperty(key));
//				writer.newLine();
//			 }
			
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

		try {
			neighboursFile.createNewFile();
			BufferedWriter writer  = new BufferedWriter(new FileWriter(neighboursFile));
			
			/**
			 *TODO Write code here to write the file
			 */
			
			writer.close();
		}catch (Exception e){
			AweConsolePlugin.exception(e);
		}
	}
	
	/**
	 * Creates the interference file for input to the C++ engine
	 */
	public void createInterferenceFile(){
		File interferenceFile = getFile(this.inputInterferenceFileName);

		try {
			interferenceFile.createNewFile();
			BufferedWriter writer  = new BufferedWriter(new FileWriter(interferenceFile));
			
			/**
			 *TODO Write code here to write the file
			 */
			
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
			 *TODO Write code here to write the file
			 */
			
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
			
			/**
			 *TODO Write code here to write the file
			 */
			
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
		try {
			exceptionFile.createNewFile();
			BufferedWriter writer  = new BufferedWriter(new FileWriter(exceptionFile));
			
			/**
			 *TODO Write code here to write the file
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
