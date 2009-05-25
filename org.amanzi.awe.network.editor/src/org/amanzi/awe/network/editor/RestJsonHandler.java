package org.amanzi.awe.network.editor;

import java.io.StringReader;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.UriInfo;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.http.HTTPBinding;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;


public class RestJsonHandler {

	  static UriInfo uriInfo;
	  static String localURL="http://127.0.0.1:3005/";
	
	   @GET
	   @Produces("application/json")
	   public static JSONObject updateJSONProperties(String[] properties,String[] values,String uri) throws JSONException {
	       JSONObject updateResponseREST=new JSONObject();
		   for(int i=0;i<properties.length;i++)
	       {
			   updateResponseREST.put(properties[i], values[i]);
			   updateResponseREST.put(uri, uriInfo.getRequestUriBuilder().path(uri).build());
	       
	       }
		   
		   
	       return new JSONObject();
	      
	    } 

	   @WebServiceProvider
	   @ServiceMode(value=Service.Mode.PAYLOAD)
	   public class CustomProvider implements Provider<Source> {
	       public Source invoke(Source source) {
	            String replyElement = new String("<p>hello world</p>");
	            StreamSource reply = new StreamSource(
	                                     new StringReader(replyElement));
	            return reply;
	         }

	   public  void publish(){
	          Endpoint e = Endpoint.create( HTTPBinding.HTTP_BINDING,
	                                        new CustomProvider());
	         e.publish(localURL);
	          // Run forever  e.stop();
	    }
	   }

	
}
