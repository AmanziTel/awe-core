package org.amanzi.awe.catalog.neo.actions;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;



import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;

public class NeoServiceExtension implements ServiceExtension
{
	
	public static final String URL_KEY = "org.amanzi.awe.catalog.neo.url";
	public static final String CLASS_KEY = "org.amanzi.awe.catalog.neo.class";
	
	@Override
	public Map<String, Serializable> createParams(URL url) {
		// TODO Auto-generated method stub
		try
		{
		  Map<String, Serializable> params = new HashMap<String, Serializable>();
		  params.put(URL_KEY, url);
          params.put(CLASS_KEY, URL.class);
          return params;
		}
		catch (Exception ex)
		{
			  System.err.println("Failed to initialize parameters for url[" + url + "]: "
	                    + ex.toString());
	            ex.printStackTrace(System.err);
			return null;
		}
		
	}

	@Override
	public IService createService(URL id, Map<String, Serializable> params) {
		// TODO Auto-generated method stub
		  if (params == null) {
	            return null;
	        }

	        // check for the property service key
	        if (params.containsKey(URL_KEY)) {
	            // found it, create the service handle
	            return new NeoService(params);
	        }
	        // key not found
	        return null;
	}

}
