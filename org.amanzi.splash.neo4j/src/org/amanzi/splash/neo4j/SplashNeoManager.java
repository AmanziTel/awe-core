package org.amanzi.splash.neo4j;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.CellRelationTypes;
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
			n = splashIndexService.getSingleNode( INeoConstants.PROPERTY_ID_NAME, id );			
			tx.success();
		}
		finally
		{
			tx.finish();
		}


		if (n != null){
			try{
				cell = NodeToCell(n);
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
		String id = (String) n.getProperty(INeoConstants.PROPERTY_ID_NAME);
		String value = (String) n.getProperty(INeoConstants.PROPERTY_VALUE_NAME);
		String definition = (String) n.getProperty(INeoConstants.PROPERTY_DEFINITION_NAME);
		CellFormat cf = new CellFormat();
		cf.setFontName((String) n.getProperty(INeoConstants.PROPERTY_FONT_NAME_NAME));
		cf.setFontStyle((Integer) n.getProperty(INeoConstants.PROPERTY_FONT_STYLE_NAME));
		cf.setFontSize((Integer) n.getProperty(INeoConstants.RPOPERTY_FONT_SIZE_NAME));
		cf.setVerticalAlignment((Integer) n.getProperty(INeoConstants.PROPERT_VERTICAL_ALIGNMENT_NAME));
		cf.setHorizontalAlignment((Integer) n.getProperty(INeoConstants.PROPERTY_HORIZONTAL_ALIGNMENT_NAME));
		
		int fontColorR =  (Integer) n.getProperty(INeoConstants.PROPERTY_FONT_COLOR_R_NAME);
		int fontColorG =  (Integer) n.getProperty(INeoConstants.PROPERTY_FONT_COLOR_G_NAME);
		int fontColorB =  (Integer) n.getProperty(INeoConstants.PROPERTY_FONT_COLOR_B_NAME);
		Color fontColor = new Color(fontColorR, fontColorG, fontColorB);
		
		cf.setFontColor(fontColor);
		
		int bgColorR =  (Integer) n.getProperty(INeoConstants.PROPERTY_BG_COLOR_R_NAME);
		int bgColorG =  (Integer) n.getProperty(INeoConstants.PROPERTY_BG_COLOR_G_NAME);
		int bgColorB =  (Integer) n.getProperty(INeoConstants.RPOPERTY_BG_COLOR_B_NAME);
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
			Node n = splashIndexService.getSingleNode( INeoConstants.PROPERTY_ID_NAME, id );
			Traverser cellTraverser = n.traverse(
					Traverser.Order.BREADTH_FIRST,
					StopEvaluator.END_OF_NETWORK,
					ReturnableEvaluator.ALL_BUT_START_NODE,
					CellRelationTypes.RFG,
					Direction.OUTGOING );
			// Traverse the node space and print out the result
			for ( Node friend : cellTraverser )
			{
				System.out.println( "RFG Cells:" + friend.getProperty( INeoConstants.PROPERTY_ID_NAME ) );
				retIDs.add((String) friend.getProperty( INeoConstants.PROPERTY_ID_NAME ));
			}

			cellTraverser = n.traverse(
					Traverser.Order.BREADTH_FIRST,
					StopEvaluator.END_OF_NETWORK,
					ReturnableEvaluator.ALL_BUT_START_NODE,
					CellRelationTypes.RFD,
					Direction.OUTGOING );
			// Traverse the node space and print out the result
			for ( Node friend : cellTraverser )
			{
				System.out.println( "RFD Cells:" + friend.getProperty( INeoConstants.PROPERTY_ID_NAME ) );
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
		n.setProperty(INeoConstants.PROPERTY_ID_NAME, c.getCellID());
		n.setProperty(INeoConstants.PROPERTY_VALUE_NAME, c.getValue());
		n.setProperty(INeoConstants.PROPERTY_DEFINITION_NAME, c.getDefinition());
		n.setProperty(INeoConstants.PROPERTY_FONT_NAME_NAME, c.getCellFormat().getFontName());
		n.setProperty(INeoConstants.PROPERTY_FONT_STYLE_NAME, c.getCellFormat().getFontStyle());
		n.setProperty(INeoConstants.RPOPERTY_FONT_SIZE_NAME, c.getCellFormat().getFontSize());
		n.setProperty(INeoConstants.PROPERT_VERTICAL_ALIGNMENT_NAME, c.getCellFormat().getVerticalAlignment());
		n.setProperty(INeoConstants.PROPERTY_HORIZONTAL_ALIGNMENT_NAME, c.getCellFormat().getHorizontalAlignment());
		int fontColorR = c.getCellFormat().getFontColor().getRed();
		int fontColorG = c.getCellFormat().getFontColor().getGreen();
		int fontColorB = c.getCellFormat().getFontColor().getBlue();
		n.setProperty(INeoConstants.PROPERTY_FONT_COLOR_R_NAME, fontColorR);
		n.setProperty(INeoConstants.PROPERTY_FONT_COLOR_G_NAME, fontColorG);
		n.setProperty(INeoConstants.PROPERTY_FONT_COLOR_B_NAME, fontColorB);
		int bgColorR = c.getCellFormat().getBackgroundColor().getRed();
		int bgColorG = c.getCellFormat().getBackgroundColor().getGreen();
		int bgColorB = c.getCellFormat().getBackgroundColor().getBlue();
		n.setProperty(INeoConstants.PROPERTY_BG_COLOR_R_NAME, bgColorR);
		n.setProperty(INeoConstants.PROPERTY_BG_COLOR_G_NAME, bgColorG);
		n.setProperty(INeoConstants.RPOPERTY_BG_COLOR_B_NAME, bgColorB);

		return n;
	}
	
	public void renameCell( String oldID, String newID){
		Transaction tx = neo.beginTx();
		try{
			Node n = splashIndexService.getSingleNode( INeoConstants.PROPERTY_ID_NAME, oldID );
			
			n.setProperty(INeoConstants.PROPERTY_ID_NAME, newID);

			splashIndexService.index( n, INeoConstants.PROPERTY_ID_NAME, newID );
		}
		finally
		{
			tx.finish();
		}
	}

	public void updateCell( String id, Cell updatedCell){
		Transaction tx = neo.beginTx();
		try{
			Node n = splashIndexService.getSingleNode( INeoConstants.PROPERTY_ID_NAME, id );

			splashIndexService.index( CellToNode(n, updatedCell), INeoConstants.PROPERTY_ID_NAME, id );

			List<String> rfdCellsIDs = Util.findComplexCellIDs((String) updatedCell.getDefinition());

			for (int i=0;i<rfdCellsIDs.size();i++){

				String ID = rfdCellsIDs.get(i).toUpperCase();
				Node nc = splashIndexService.getSingleNode( INeoConstants.PROPERTY_ID_NAME, ID);

				if (nc == null){
					Node newNode = neo.createNode();
					newNode.setProperty( INeoConstants.PROPERTY_ID_NAME, ID );
					splashIndexService.index( newNode, INeoConstants.PROPERTY_ID_NAME, ID );
				}
				n.createRelationshipTo(nc, CellRelationTypes.RFD);
				nc.createRelationshipTo(n, CellRelationTypes.RFG);
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
			Node n = splashIndexService.getSingleNode( INeoConstants.PROPERTY_ID_NAME, id );
			if (n == null){
				Node newNode = neo.createNode();
				splashIndexService.index( CellToNode(newNode, new_cell), INeoConstants.PROPERTY_ID_NAME, id );
			}else{
				splashIndexService.index( CellToNode(n, new_cell), INeoConstants.PROPERTY_ID_NAME, id );
			}
			tx.success();
		}
		finally
		{
			tx.finish();
		}
	}
}
