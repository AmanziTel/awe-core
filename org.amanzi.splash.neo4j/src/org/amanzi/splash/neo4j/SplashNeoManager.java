package org.amanzi.splash.neo4j;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.splash.neo4j.swing.Cell;
import org.amanzi.splash.neo4j.utilities.Util;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.preference.IPreferenceStore;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.Traverser;
import org.neo4j.remote.RemoteNeo;
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
			
			String location = ResourcesPlugin.getWorkspace().getRoot() + splashID;//home/amabdelsalam/Desktop/neo";
			Util.logn("location: " + location);
			
//			if ( (resourceUri != null) && (resourceUri.trim().length() != 0) )
//			{
//				// let's try the resource URI
//				try
//				{
//					System.out.println( "Splash: trying remote neo" );
//					neo = new RemoteNeo( resourceUri );
//					System.out.println( "Splash: connected to remote neo" );
//				}
//				catch ( Exception e )
//				{
//					e.printStackTrace();
//				}
//			}
//			else
//			{
//				// determine the neo directory from the preferences
////				String location = preferenceStore
////				.getString( NeoPreferences.DATABASE_LOCATION );
//				String location = "/home/amadelsalam/Desktop/neo";
//				if ( (location == null) || (location.trim().length() == 0) )
//				{
//					return;
//				}
//				// seems to be a valid directory, try starting neo
//				neo = new EmbeddedNeo( location );
//				System.out.println( "Splash: connected to embedded neo" );
//			}
			
			neo = new EmbeddedNeo( location );
			this.splashIndexService = new NeoIndexService( neo );
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
//			try
//			{
//				//tx.failure();
//				tx.finish();
//			}
//			catch ( Exception e )
//			{
//				e.printStackTrace();
//			}
			try
			{
				tx.finish();
				neo.shutdown();
				Util.logn("Neo Service Stopped");
				// notify listeners
				//fireServiceChangedEvent( NeoServiceStatus.STOPPED );
				}
			finally
			{
//				neo = null;
			}
			
			
		}
	}

	public Cell getCell(String id){
		
		Transaction tx = neo.beginTx();
		Node n = null;
		Cell cell = null;
		try
		{
			n = this.splashIndexService.getSingleNode( "id", id );
			tx.success();
		}
		finally
		{
			tx.finish();
		}


		if (n != null){
			try{
			String value = (String) n.getProperty("value");
			String definition = (String) n.getProperty("definition");
			CellFormat cf = new CellFormat();
			cf.setFontName((String) n.getProperty("fontName"));
			cf.setFontStyle((Integer) n.getProperty("fontStyle"));
			cf.setFontSize((Integer) n.getProperty("fontSize"));
			cf.setVerticalAlignment((Integer) n.getProperty("verticalAlignment"));
			cf.setHorizontalAlignment((Integer) n.getProperty("horizontalAlignment"));

			cell = new Cell(Util.getRowIndexFromCellID(id),
					Util.getColumnIndexFromCellID(id),
					definition, 
					value, cf);
			
			
			Util.logn("cell.definition: " + cell.getDefinition());
			Util.logn("cell.value: " + cell.getValue());
			
			}catch (Exception ex){
				cell = new Cell(Util.getRowIndexFromCellID(id),
						Util.getColumnIndexFromCellID(id),
						"", 
						"", new CellFormat());
				//addCell(id, cell);
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

	public ArrayList<String> findReferringCells(String id){
		ArrayList<String> retIDs = new ArrayList<String>();
		Transaction tx = neo.beginTx();//neo.beginTx();
		try{
			Node n = this.splashIndexService.getSingleNode( "id", id );
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

	public void updateCell( String id, Cell updatedCell){
		Transaction tx = neo.beginTx();// neo.beginTx();
		try{
			Node n = this.splashIndexService.getSingleNode( "id", id );
			
			//Util.logn("Updating cell id=" + id + " - value=" + updatedCell.getValue());

			n.setProperty("value", updatedCell.getValue());
			n.setProperty("definition", updatedCell.getDefinition());
			n.setProperty("fontName", updatedCell.getCellFormat().getFontName());
			n.setProperty("fontStyle", updatedCell.getCellFormat().getFontStyle());
			n.setProperty("fontSize", updatedCell.getCellFormat().getFontSize());
			n.setProperty("verticalAlignment", updatedCell.getCellFormat().getVerticalAlignment());
			n.setProperty("horizontalAlignment", updatedCell.getCellFormat().getHorizontalAlignment());
			//n.setProperty("fontColor", updatedCell.getCellFormat().getFontColor());
			//n.setProperty("backgroundColor", updatedCell.getCellFormat().getBackgroundColor());
			//n.setProperty("cellBorder", updatedCell.getCellFormat().getCellBorder());

			//Util.logn("Indexing cell ...");
			this.splashIndexService.index( n, "id", id );
			//Util.logn("Finished Indexing cell ...");
			
			//Util.logn("Finding cell IDs...");
			List<String> rfdCellsIDs = Util.findComplexCellIDs((String) updatedCell.getDefinition());

			for (int i=0;i<rfdCellsIDs.size();i++){

				String ID = rfdCellsIDs.get(i).toUpperCase();
				//Util.logn("Cell ID: " + ID);
				Node nc = this.splashIndexService.getSingleNode( "id", ID);
				//Util.logn("nc = " + nc);

				if (nc == null){
					Node newNode = neo.createNode();
					newNode.setProperty( "id", ID );
					//newNode.setProperty( "value", new_cell.getValue() );
					//newNode.setProperty("definition", new_cell.getDefinition());
					this.splashIndexService.index( newNode, "id", ID );
				}
				//Util.logn("Creating relationship...");
				n.createRelationshipTo(nc, CellRelationTypes.RFD);
				nc.createRelationshipTo(n, CellRelationTypes.RFG);
				//Util.logn("Finished");
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
		//Util.logn("new_cell = " + new_cell);
		Transaction tx = neo.beginTx();// neo.beginTx();
		try{
			Node n = this.splashIndexService.getSingleNode( "id", id );
			if (n == null){
				//Util.logn("Adding new cell id=" + id + " - value=" + new_cell.getValue());
				Node newNode = neo.createNode();
				newNode.setProperty( "id", id );
				newNode.setProperty( "value", new_cell.getValue() );
				newNode.setProperty("definition", new_cell.getDefinition());
				newNode.setProperty("fontName", new_cell.getCellFormat().getFontName());
				newNode.setProperty("fontStyle", new_cell.getCellFormat().getFontStyle());
				newNode.setProperty("fontSize", new_cell.getCellFormat().getFontSize());
				newNode.setProperty("verticalAlignment", new_cell.getCellFormat().getVerticalAlignment());
				newNode.setProperty("horizontalAlignment", new_cell.getCellFormat().getHorizontalAlignment());
				//newNode.setProperty("fontColor", new_cell.getCellFormat().getFontColor());
				//newNode.setProperty("backgroundColor", new_cell.getCellFormat().getBackgroundColor());
				//newNode.setProperty("cellBorder", new_cell.getCellFormat().getCellBorder());

				this.splashIndexService.index( newNode, "id", id );
			}else{
				//Util.logn("Updating cell id=" + id + " - value=" + new_cell.getValue());
				n.setProperty("value", new_cell.getValue());
				n.setProperty("definition", new_cell.getDefinition());
				n.setProperty("fontName", new_cell.getCellFormat().getFontName());
				n.setProperty("fontStyle", new_cell.getCellFormat().getFontStyle());
				n.setProperty("fontSize", new_cell.getCellFormat().getFontSize());
				n.setProperty("verticalAlignment", new_cell.getCellFormat().getVerticalAlignment());
				n.setProperty("horizontalAlignment", new_cell.getCellFormat().getHorizontalAlignment());
				this.splashIndexService.index( n, "id", id );
			}

			tx.success();
		}
		finally
		{
			tx.finish();
		}
	}
}
