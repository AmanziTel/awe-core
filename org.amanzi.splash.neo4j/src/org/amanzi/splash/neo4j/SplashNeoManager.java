package org.amanzi.splash.neo4j;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.splash.neo4j.swing.Cell;
import org.amanzi.splash.neo4j.utilities.Util;
import org.eclipse.core.runtime.Platform;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.Traverser;
import org.neo4j.util.index.NeoIndexService;

import com.eteks.openjeks.format.CellFormat;

public class SplashNeoManager{

	/**
	 * The service instance.
	 */
	protected NeoService neo;
	private NeoIndexService splashIndexService;
	private Transaction tx;

	private String splashID = "";


	public SplashNeoManager(String splash_id)
	{
		if (neo != null) return;
		splashID = splash_id;

		startNeoService();

		//neo = org.amanzi.neo4j.Activator.getDefault().getNeo();
		//indexService = org.amanzi.neo4j.Activator.getDefault().getIndexService();
	}


	/**
	 * Starts the neo service.
	 */
	public void startNeoService() throws RuntimeException
	{
		System.out.println( "Splash: checking service ..." );
		if ( neo == null )
		{
			System.out.println( "Splash: starting neo" );

//			final IPreferenceStore preferenceStore = Activator.getDefault()
//			.getPreferenceStore();
			// try the resource URI first
//			String resourceUri = preferenceStore
//			.getString( NeoPreferences.DATABASE_RESOURCE_URI );

			String location = Platform.getLocation() + "/neo4j/" + splashID;
			//Util.logn("location: " + location);

			neo = new EmbeddedNeo( location );
			splashIndexService = new NeoIndexService( neo );
			System.out.println( "Splash: connected to embedded neo" );
		}
	}

	/**
	 * Stops the neo service.
	 */
	public void stopNeoService()
	{
		if ( neo != null )
		{
			try
			{
				tx.finish();
				neo.shutdown();
				//Util.logn("Neo Service Stopped");
			}
			finally
			{

			}


		}
	}

	public Cell getCell(String id){

		Transaction tx = neo.beginTx();
		Node n = null;
		Cell cell = null;
		try
		{
			n = splashIndexService.getSingleNode( "id", id );
			if (n != null){
				//Util.logn("----------------------------------------------");
				for (String s : n.getPropertyKeys()){
					//Util.logn ("Key: " + s + " - Value: " + n.getProperty(s));
				}
				//Util.logn("----------------------------------------------");
			}
			tx.success();
		}
		finally
		{
			tx.finish();
		}


		if (n != null){
			try{
				cell = NodeToCell(n);
				//Util.logn("cell.definition: " + cell.getDefinition());
				//Util.logn("cell.value: " + cell.getValue());

			}catch (Exception ex){
				cell = new Cell(Util.getRowIndexFromCellID(id),
						Util.getColumnIndexFromCellID(id),
						"", 
						"", new CellFormat());
				addCell(id, cell);
			}
		}else{
			cell = new Cell(Util.getRowIndexFromCellID(id),
					Util.getColumnIndexFromCellID(id),
					"", 
					"", new CellFormat());
			addCell(id, cell);
		}

		return cell;
	}
	
	

	public Cell NodeToCell(Node n){
		String id = (String) n.getProperty("id");
		String value = (String) n.getProperty("value");
		String definition = (String) n.getProperty("definition");
		CellFormat cf = new CellFormat();
		cf.setFontName((String) n.getProperty("fontName"));
		cf.setFontStyle((Integer) n.getProperty("fontStyle"));
		cf.setFontSize((Integer) n.getProperty("fontSize"));
		cf.setVerticalAlignment((Integer) n.getProperty("verticalAlignment"));
		cf.setHorizontalAlignment((Integer) n.getProperty("horizontalAlignment"));
		
		int fontColorR =  (Integer) n.getProperty("fontColorR");
		int fontColorG =  (Integer) n.getProperty("fontColorG");
		int fontColorB =  (Integer) n.getProperty("fontColorB");
		Color fontColor = new Color(fontColorR, fontColorG, fontColorB);
		
		cf.setFontColor(fontColor);
		
		int bgColorR =  (Integer) n.getProperty("bgColorR");
		int bgColorG =  (Integer) n.getProperty("bgColorG");
		int bgColorB =  (Integer) n.getProperty("bgColorB");
		Color bgColor = new Color(bgColorR,bgColorG, bgColorB);
		
		cf.setBackgroundColor(bgColor);
		
		return new Cell(Util.getRowIndexFromCellID(id),
				Util.getColumnIndexFromCellID(id),
				definition, 
				value, cf);
	}

	@SuppressWarnings("deprecation")
	public ArrayList<String> findReferringCells(String id){
		ArrayList<String> retIDs = new ArrayList<String>();
		Transaction tx = neo.beginTx();//neo.beginTx();
		try{
			Node n = splashIndexService.getSingleNode( "id", id );
			Traverser cellTraverser = n.traverse(
					Traverser.Order.BREADTH_FIRST,
					StopEvaluator.END_OF_NETWORK,
					ReturnableEvaluator.ALL_BUT_START_NODE,
					CellRelationTypes.RFG,
					Direction.OUTGOING );
			// Traverse the node space and print out the result
			//System.out.println( "Mr Anderson's friends:" );
			for ( Node friend : cellTraverser )
			{
				System.out.println( "RFG Cells:" + friend.getProperty( "id" ) );
				retIDs.add((String) friend.getProperty( "id" ));
			}

			cellTraverser = n.traverse(
					Traverser.Order.BREADTH_FIRST,
					StopEvaluator.END_OF_NETWORK,
					ReturnableEvaluator.ALL_BUT_START_NODE,
					CellRelationTypes.RFD,
					Direction.OUTGOING );
			// Traverse the node space and print out the result
			//System.out.println( "Mr Anderson's friends:" );
			for ( Node friend : cellTraverser )
			{
				System.out.println( "RFD Cells:" + friend.getProperty( "id" ) );
			}
			tx.success();
		}
		finally
		{
			tx.finish();
		}

		return retIDs;
	}

	public Node CellToNode (Node node, Cell c){
		Node n = node;
		n.setProperty("id", c.getCellID());
		n.setProperty("value", c.getValue());
		n.setProperty("definition", c.getDefinition());
		n.setProperty("fontName", c.getCellFormat().getFontName());
		n.setProperty("fontStyle", c.getCellFormat().getFontStyle());
		n.setProperty("fontSize", c.getCellFormat().getFontSize());
		n.setProperty("verticalAlignment", c.getCellFormat().getVerticalAlignment());
		n.setProperty("horizontalAlignment", c.getCellFormat().getHorizontalAlignment());
		int fontColorR = c.getCellFormat().getFontColor().getRed();
		int fontColorG = c.getCellFormat().getFontColor().getGreen();
		int fontColorB = c.getCellFormat().getFontColor().getBlue();
		n.setProperty("fontColorR", fontColorR);
		n.setProperty("fontColorG", fontColorG);
		n.setProperty("fontColorB", fontColorB);
		int bgColorR = c.getCellFormat().getBackgroundColor().getRed();
		int bgColorG = c.getCellFormat().getBackgroundColor().getGreen();
		int bgColorB = c.getCellFormat().getBackgroundColor().getBlue();
		n.setProperty("bgColorR", bgColorR);
		n.setProperty("bgColorG", bgColorG);
		n.setProperty("bgColorB", bgColorB);

		return n;
	}
	
	public void renameCell( String oldID, String newID){
		Transaction tx = neo.beginTx();
		try{
			Node n = splashIndexService.getSingleNode( "id", oldID );
			
			n.setProperty("id", newID);

			splashIndexService.index( n, "id", newID );
		}
		finally
		{
			tx.finish();
		}
	}

	public void updateCell( String id, Cell updatedCell){
		Transaction tx = neo.beginTx();
		try{
			Node n = splashIndexService.getSingleNode( "id", id );

			splashIndexService.index( CellToNode(n, updatedCell), "id", id );

			////Util.logn("Finding cell IDs...");
			List<String> rfdCellsIDs = Util.findComplexCellIDs((String) updatedCell.getDefinition());

			for (int i=0;i<rfdCellsIDs.size();i++){

				String ID = rfdCellsIDs.get(i).toUpperCase();
				////Util.logn("Cell ID: " + ID);
				Node nc = splashIndexService.getSingleNode( "id", ID);
				////Util.logn("nc = " + nc);

				if (nc == null){
					Node newNode = neo.createNode();
					newNode.setProperty( "id", ID );
					//newNode.setProperty( "value", new_cell.getValue() );
					//newNode.setProperty("definition", new_cell.getDefinition());
					splashIndexService.index( newNode, "id", ID );
				}
				////Util.logn("Creating relationship...");
				n.createRelationshipTo(nc, CellRelationTypes.RFD);
				nc.createRelationshipTo(n, CellRelationTypes.RFG);
				////Util.logn("Finished");
			}

			tx.success();
		}
		finally
		{
			tx.finish();
		}
	}

	public void addCell( String id, Cell new_cell)
	{
		Transaction tx = neo.beginTx();
		try{
			Node n = splashIndexService.getSingleNode( "id", id );
			if (n == null){
				Node newNode = neo.createNode();
				splashIndexService.index( CellToNode(newNode, new_cell), "id", id );
			}else{
				splashIndexService.index( CellToNode(n, new_cell), "id", id );
			}
			tx.success();
		}
		finally
		{
			tx.finish();
		}
	}
}
