package org.amanzi.awe.network.editor;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.render.RenderException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.amanzi.awe.catalog.json.Feature;
import org.amanzi.awe.catalog.json.JSONReader;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

public class JSONArrayTable 
{
	static String[] columnNamesSectors;
	static String[][] SectorsDataValues;
	
	static String[] columnNamesSites;
	static String[][] SitesDataValues;
	
	static String[] columnNamesBSCs;
	static String[][] BSCsDataValues;
	
	static String[] columnNamesCarriers;
	static String[][] CarriersDataValues;
	
    
	  @SuppressWarnings({ "unused", "unchecked" })
	public static String[][] parseJSONofSectors(  final IGeoResource jsonGeoResource,IProgressMonitor monitor ) throws RenderException 
	{
		  
	        try 
	        {
	            JSONReader jsonReader = jsonGeoResource.resolve(JSONReader.class,new SubProgressMonitor(monitor, 10));
	            for( Feature feature : jsonReader.getFeatures() ) 
	            {
	                Map<String, Object> properties = feature.getProperties();
	              
	                if (properties != null) 
	                {
	                    try 
	                    {
	                        if (properties.containsKey("sectors")) 
	                        {
	                            Object sectorsObj = properties.get("sectors");
	                            System.out.println("Found sectors: " + sectorsObj);
	                            if (sectorsObj instanceof JSONArray) 
	                            {
	                                JSONArray sectors = (JSONArray) sectorsObj;
	                                JSONObject sectorLength = sectors.getJSONObject(0);
	                                JSONObject sectorLengthProperties = sectorLength.getJSONObject("properties");
	                                Iterator<String> it=sectorLengthProperties.keys();
	                                int SectorColumns=0;
	                                Vector<String> columnNamesVector=new Vector<String>();
	                                while(it.hasNext())
	                                {
	                                	columnNamesVector.addElement(it.next());
	                                	SectorColumns++;
	                                }
	                                columnNamesSectors=(String[])columnNamesVector.toArray();
	                                
	                                SectorsDataValues=new String[SectorColumns][sectors.size()];
	                                
	                                for( int s = 0; s < sectors.size(); s++ ) 
	                                {
	                                    JSONObject sector = sectors.getJSONObject(s);
	                                    if (sector != null) 
	                                    {
	                                        System.out.println("Sector: " + sector);
	                                        JSONObject sectorProperties = sector.getJSONObject("properties");
	                                      
	                                        for(int sc=0;sc<SectorColumns;sc++)
	                                        {
	                                        	SectorsDataValues[sc][s]=sectorProperties.getString(columnNamesSectors[sc]);
	                                        	//I left here posibility to obtain these values like double or whatever in later implementation
	                                        }
	                                       // double azimuth = sectorProperties.getDouble("azimuth");
	                                        //double beamwidth = sectorProperties.getDouble("beamwidth");
	                                    }
	                                }
	                            } 
	                            else 
	                            {
	                                System.err.println("sectors object is not a JSONArray: "
	                                        + sectorsObj);
	                            }
	                        }
	                    }
	                    finally 
	                    {
	                    }
	                }
	            }
	            // updateNetworkTreeView(jsonReader);     
	        } 
	        catch (IOException e) 
	        {
	            throw new RenderException(e); // rethrow any exceptions encountered
	        } finally 
	        {
	            // if (jsonReader != null)
	            // jsonReader.close();
	           
	        }
	        return SectorsDataValues;
	    }
	  
	  public static String[] getColumnNamesSectors()
	  {
		  return columnNamesSectors;
	  }
	  
	  public static String[] getColumnNamesSites()
	  {
		  return columnNamesSites;
	  }
	  
	  public static String[] getColumnNamesBSCs()
	  {
		  return columnNamesBSCs;
	  }
	  
	  @SuppressWarnings({ "unused", "unchecked" })
		public static String[][] parseJSONofSites(  final IGeoResource jsonGeoResource,IProgressMonitor monitor ) throws RenderException 
		{
			  
		        try 
		        {
		            JSONReader jsonReader = jsonGeoResource.resolve(JSONReader.class,new SubProgressMonitor(monitor, 10));
		            for( Feature feature : jsonReader.getFeatures() ) 
		            {
		                Map<String, Object> properties = feature.getProperties();
		               
		                if (properties != null) 
		                {
		                    try 
		                    {
		                        if (properties.containsKey("sites")) 
		                        {
		                            Object sitesObj = properties.get("sites");
		                            System.out.println("Found sites: " + sitesObj);
		                            if (sitesObj instanceof JSONArray) 
		                            {
		                                JSONArray sites = (JSONArray) sitesObj;
		                                JSONObject sitesLength = sites.getJSONObject(0);
		                                JSONObject siteLengthProperties = sitesLength.getJSONObject("properties");
		                                Iterator<String> it=siteLengthProperties.keys();
		                                int SiteColumns=0;
		                                Vector<String> columnNamesVector=new Vector<String>();
		                                while(it.hasNext())
		                                {
		                                	columnNamesVector.addElement(it.next());
		                                	SiteColumns++;
		                                }
		                                columnNamesSites=(String[])columnNamesVector.toArray();
		                                
		                                SitesDataValues=new String[SiteColumns][sites.size()];
		                                
		                                for( int s = 0; s < sites.size(); s++ ) 
		                                {
		                                    JSONObject site = sites.getJSONObject(s);
		                                    if (site != null) 
		                                    {
		                                        System.out.println("Sector: " + site);
		                                        JSONObject sectorProperties = site.getJSONObject("properties");
		                                      
		                                        for(int sc=0;sc<SiteColumns;sc++)
		                                        {
		                                        	SitesDataValues[sc][s]=sectorProperties.getString(columnNamesSites[sc]);
		                                        	//I left here posibility to obtain these values like double or whatever in later implementation
		                                        }
		                                       // double azimuth = sectorProperties.getDouble("azimuth");
		                                        //double beamwidth = sectorProperties.getDouble("beamwidth");
		                                    }
		                                }
		                            } 
		                            else 
		                            {
		                                System.err.println("sites object is not a JSONArray: "
		                                        + sitesObj);
		                            }
		                        }
		                    }
		                    finally 
		                    {
		                    }
		                }
		            }
		            // updateNetworkTreeView(jsonReader);     
		        } 
		        catch (IOException e) 
		        {
		            throw new RenderException(e); // rethrow any exceptions encountered
		        } finally 
		        {
		            // if (jsonReader != null)
		            // jsonReader.close();
		           
		        }
		        return SitesDataValues;
		    }
	  
	  
	  @SuppressWarnings({ "unused", "unchecked" })
		public static String[][] parseJSONofBSCs(  final IGeoResource jsonGeoResource,IProgressMonitor monitor ) throws RenderException 
		{
			  
		        try 
		        {
		            JSONReader jsonReader = jsonGeoResource.resolve(JSONReader.class,new SubProgressMonitor(monitor, 10));
		            for( Feature feature : jsonReader.getFeatures() ) 
		            {
		                Map<String, Object> properties = feature.getProperties();
		               
		                if (properties != null) 
		                {
		                    try 
		                    {
		                        if (properties.containsKey("bsc")) 
		                        {
		                            Object bscsObj = properties.get("bsc");
		                            System.out.println("Found sites: " + bscsObj);
		                            if (bscsObj instanceof JSONArray) 
		                            {
		                                JSONArray bscs = (JSONArray) bscsObj;
		                                JSONObject bscsLength = bscs.getJSONObject(0);
		                                JSONObject bscLengthProperties = bscsLength.getJSONObject("properties");
		                                Iterator<String> it=bscLengthProperties.keys();
		                                int BSCcolumns=0;
		                                Vector<String> columnNamesVector=new Vector<String>();
		                                while(it.hasNext())
		                                {
		                                	columnNamesVector.addElement(it.next());
		                                	BSCcolumns++;
		                                }
		                                columnNamesBSCs=(String[])columnNamesVector.toArray();
		                                
		                                BSCsDataValues=new String[BSCcolumns][bscs.size()];
		                                
		                                for( int s = 0; s < bscs.size(); s++ ) 
		                                {
		                                    JSONObject site = bscs.getJSONObject(s);
		                                    if (site != null) 
		                                    {
		                                        System.out.println("BSCs: " + site);
		                                        JSONObject BSCproperties = site.getJSONObject("properties");
		                                      
		                                        for(int sc=0;sc<BSCcolumns;sc++)
		                                        {
		                                        	BSCsDataValues[sc][s]=BSCproperties.getString(columnNamesBSCs[sc]);
		                                        	//I left here posibility to obtain these values like double or whatever in later implementation
		                                        }
		                                       // double azimuth = sectorProperties.getDouble("azimuth");
		                                        //double beamwidth = sectorProperties.getDouble("beamwidth");
		                                    }
		                                }
		                            } 
		                            else 
		                            {
		                                System.err.println("BSCs object is not a JSONArray: "
		                                        + bscsObj);
		                            }
		                        }
		                    }
		                    finally 
		                    {
		                    }
		                }
		            }
		            // updateNetworkTreeView(jsonReader);     
		        } 
		        catch (IOException e) 
		        {
		            throw new RenderException(e); // rethrow any exceptions encountered
		        } finally 
		        {
		            // if (jsonReader != null)
		            // jsonReader.close();
		           
		        }
		        return BSCsDataValues;
		    }

	  @SuppressWarnings({ "unused", "unchecked" })
		public static String[][] parseJSONofCarriers(  final IGeoResource jsonGeoResource,IProgressMonitor monitor ) throws RenderException 
		{
			  
		        try 
		        {
		            JSONReader jsonReader = jsonGeoResource.resolve(JSONReader.class,new SubProgressMonitor(monitor, 10));
		            for( Feature feature : jsonReader.getFeatures() ) 
		            {
		                Map<String, Object> properties = feature.getProperties();
		               
		                if (properties != null) 
		                {
		                    try 
		                    {
		                        if (properties.containsKey("carriers")) 
		                        {
		                            Object carriersObj = properties.get("carriers");
		                            System.out.println("Found sites: " + carriersObj);
		                            if (carriersObj instanceof JSONArray) 
		                            {
		                                JSONArray carriers = (JSONArray) carriersObj;
		                                JSONObject carriersLength = carriers.getJSONObject(0);
		                                JSONObject carriersLengthProperties = carriersLength.getJSONObject("properties");
		                                Iterator<String> it=carriersLengthProperties.keys();
		                                int CarriersColumns=0;
		                                Vector<String> columnNamesVector=new Vector<String>();
		                                while(it.hasNext())
		                                {
		                                	columnNamesVector.addElement(it.next());
		                                	CarriersColumns++;
		                                }
		                                columnNamesCarriers=(String[])columnNamesVector.toArray();
		                                
		                                CarriersDataValues=new String[CarriersColumns][carriers.size()];
		                                
		                                for( int s = 0; s < carriers.size(); s++ ) 
		                                {
		                                    JSONObject site = carriers.getJSONObject(s);
		                                    if (site != null) 
		                                    {
		                                        System.out.println("BSCs: " + site);
		                                        JSONObject Carriersproperties = site.getJSONObject("properties");
		                                      
		                                        for(int sc=0;sc<CarriersColumns;sc++)
		                                        {
		                                        	CarriersDataValues[sc][s]=Carriersproperties.getString(columnNamesCarriers[sc]);
		                                        	//I left here posibility to obtain these values like double or whatever in later implementation
		                                        }
		                                       // double azimuth = sectorProperties.getDouble("azimuth");
		                                        //double beamwidth = sectorProperties.getDouble("beamwidth");
		                                    }
		                                }
		                            } 
		                            else 
		                            {
		                                System.err.println("Carriers object is not a JSONArray: "
		                                        + carriersObj);
		                            }
		                        }
		                    }
		                    finally 
		                    {
		                    }
		                }
		            }
		            // updateNetworkTreeView(jsonReader);     
		        } 
		        catch (IOException e) 
		        {
		            throw new RenderException(e); // rethrow any exceptions encountered
		        } finally 
		        {
		            // if (jsonReader != null)
		            // jsonReader.close();
		           
		        }
		        return BSCsDataValues;
		    }

	  
	  

}
